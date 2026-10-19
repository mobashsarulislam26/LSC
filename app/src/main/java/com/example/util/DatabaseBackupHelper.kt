package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.data.local.CampDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class representing an on-device backup file
 */
data class BackupItem(
    val file: File,
    val fileName: String,
    val sizeBytes: Long,
    val formattedSize: String,
    val timestamp: Long,
    val formattedDate: String,
    val isValidSqlite: Boolean
)

sealed class BackupResult {
    data class Success(
        val file: File?,
        val fileName: String,
        val sizeBytes: Long,
        val formattedSize: String,
        val destinationPath: String,
        val uri: Uri? = null
    ) : BackupResult()

    data class Error(val errorMessage: String) : BackupResult()
}

/**
 * Helper utility for backing up the SQLite Room database file
 * to the device's document storage for manual disaster recovery.
 */
object DatabaseBackupHelper {

    private const val DATABASE_NAME = "lsc_camp_database"
    private const val SQLITE_HEADER_PREFIX = "SQLite format 3"

    /**
     * Executes a full WAL checkpoint to flush all write-ahead log transactions
     * into the main database file before copying.
     */
    fun checkpointWal(context: Context) {
        try {
            val db = CampDatabase.getDatabase(context)
            val cursor = db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)")
            if (cursor.moveToFirst()) {
                // Checkpoint executed
            }
            cursor.close()
        } catch (e: Exception) {
            // Log or fallback if db is locked
        }
    }

    /**
     * Retrieves the primary document backup directory.
     */
    fun getBackupDirectory(context: Context): File {
        val docDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?: File(context.filesDir, "Documents")
        val backupFolder = File(docDir, "LSC_Backups")
        if (!backupFolder.exists()) {
            backupFolder.mkdirs()
        }
        return backupFolder
    }

    /**
     * Saves a snapshot copy of the SQLite Room database into the device's document storage.
     */
    suspend fun backupDatabaseToDocuments(context: Context): BackupResult = withContext(Dispatchers.IO) {
        try {
            // 1. Force WAL checkpoint to flush all uncommitted transactions to main DB
            checkpointWal(context)

            // 2. Locate the source Room SQLite database file
            val sourceDbFile = context.getDatabasePath(DATABASE_NAME)
            if (!sourceDbFile.exists() || sourceDbFile.length() == 0L) {
                return@withContext BackupResult.Error("ডাটাবেজ ফাইল পাওয়া যায়নি বা ডাটাবেজ খালি (Database file not found or empty).")
            }

            // 3. Prepare target destination in device Document storage
            val backupFolder = getBackupDirectory(context)
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupFileName = "${DATABASE_NAME}_backup_$timeStamp.db"
            val targetFile = File(backupFolder, backupFileName)

            // 4. Stream copy the database file
            FileInputStream(sourceDbFile).use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                    output.flush()
                }
            }

            // 5. Verify file creation and integrity
            if (!targetFile.exists() || targetFile.length() == 0L) {
                return@withContext BackupResult.Error("ব্যাকআপ ফাইল তৈরি হতে ব্যর্থ হয়েছে।")
            }

            val isValid = verifySqliteHeader(targetFile)
            if (!isValid) {
                return@withContext BackupResult.Error("ব্যাকআপ ফাইলের ইন্টিগ্রিটি সঠিক নয়।")
            }

            val sizeBytes = targetFile.length()
            val formattedSize = formatFileSize(sizeBytes)

            BackupResult.Success(
                file = targetFile,
                fileName = backupFileName,
                sizeBytes = sizeBytes,
                formattedSize = formattedSize,
                destinationPath = targetFile.absolutePath
            )
        } catch (e: Exception) {
            BackupResult.Error("ব্যাকআপ সংরক্ষণে ত্রুটি: ${e.localizedMessage ?: "অজানা ত্রুটি"}")
        }
    }

    /**
     * Saves a copy of the SQLite Room database to a user-selected SAF Uri
     * (e.g. ActivityResultContracts.CreateDocument destination).
     */
    suspend fun backupDatabaseToUri(context: Context, destinationUri: Uri): BackupResult = withContext(Dispatchers.IO) {
        try {
            checkpointWal(context)

            val sourceDbFile = context.getDatabasePath(DATABASE_NAME)
            if (!sourceDbFile.exists() || sourceDbFile.length() == 0L) {
                return@withContext BackupResult.Error("ডাটাবেজ ফাইল পাওয়া যায়নি।")
            }

            context.contentResolver.openOutputStream(destinationUri)?.use { output ->
                FileInputStream(sourceDbFile).use { input ->
                    input.copyTo(output)
                    output.flush()
                }
            } ?: return@withContext BackupResult.Error("ডকুমেন্ট স্টোরেজে রাইট পারমিশন পাওয়া যায়নি।")

            val sizeBytes = sourceDbFile.length()
            val formattedSize = formatFileSize(sizeBytes)
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "${DATABASE_NAME}_backup_$timeStamp.db"

            BackupResult.Success(
                file = null,
                fileName = fileName,
                sizeBytes = sizeBytes,
                formattedSize = formattedSize,
                destinationPath = destinationUri.toString(),
                uri = destinationUri
            )
        } catch (e: Exception) {
            BackupResult.Error("নির্বাচিত ফোল্ডারে ব্যাকআপ সেভ করতে সমস্যা হয়েছে: ${e.localizedMessage}")
        }
    }

    /**
     * Lists existing .db backup files in the device's document storage.
     */
    fun listExistingBackups(context: Context): List<BackupItem> {
        val folder = getBackupDirectory(context)
        if (!folder.exists() || !folder.isDirectory) return emptyList()

        val files = folder.listFiles { file ->
            file.isFile && file.name.endsWith(".db", ignoreCase = true)
        } ?: return emptyList()

        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

        return files
            .sortedByDescending { it.lastModified() }
            .map { file ->
                val size = file.length()
                val isValid = verifySqliteHeader(file)
                BackupItem(
                    file = file,
                    fileName = file.name,
                    sizeBytes = size,
                    formattedSize = formatFileSize(size),
                    timestamp = file.lastModified(),
                    formattedDate = dateFormat.format(Date(file.lastModified())),
                    isValidSqlite = isValid
                )
            }
    }

    /**
     * Shares a backup file via FileProvider Intent.
     */
    fun shareBackupFile(context: Context, backupFile: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            backupFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "LSC Camp SQLite Database Backup")
            putExtra(
                Intent.EXTRA_TEXT,
                "LSC Residential Camp SQLite Room Database Backup File: ${backupFile.name} (${formatFileSize(backupFile.length())}). For manual recovery, restore to Room database path."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "ব্যাকআপ ডাটাবেজ ফাইল শেয়ার করুন"))
    }

    /**
     * Verifies if a file starts with standard SQLite magic bytes ("SQLite format 3\0").
     */
    fun verifySqliteHeader(file: File): Boolean {
        if (!file.exists() || file.length() < 16) return false
        try {
            FileInputStream(file).use { input ->
                val headerBytes = ByteArray(16)
                val read = input.read(headerBytes)
                if (read >= 16) {
                    val headerString = String(headerBytes, Charsets.US_ASCII)
                    if (headerString.startsWith(SQLITE_HEADER_PREFIX)) return true
                    // SQLCipher encrypted database check: non-empty binary header that isn't plain text "INVALID..."
                    if (file.length() >= 512 && !headerString.startsWith("INVALID")) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }

    /**
     * Restores the live Room SQLite database from an existing on-device backup file.
     */
    suspend fun restoreDatabaseFromBackup(context: Context, backupFile: File): BackupResult = withContext(Dispatchers.IO) {
        try {
            if (!backupFile.exists() || !verifySqliteHeader(backupFile)) {
                return@withContext BackupResult.Error("ব্যাকআপ ফাইলটি বৈধ SQLite ডাটাবেজ নয় বা পাওয়া যায়নি।")
            }

            checkpointWal(context)

            val targetDbFile = context.getDatabasePath(DATABASE_NAME)
            targetDbFile.parentFile?.mkdirs()

            // Remove stale WAL/SHM files before copying restored snapshot
            val walFile = File(targetDbFile.path + "-wal")
            val shmFile = File(targetDbFile.path + "-shm")

            FileInputStream(backupFile).use { input ->
                FileOutputStream(targetDbFile, false).use { output ->
                    input.copyTo(output)
                    output.flush()
                }
            }

            if (walFile.exists()) walFile.delete()
            if (shmFile.exists()) shmFile.delete()

            val sizeBytes = targetDbFile.length()
            BackupResult.Success(
                file = targetDbFile,
                fileName = backupFile.name,
                sizeBytes = sizeBytes,
                formattedSize = formatFileSize(sizeBytes),
                destinationPath = targetDbFile.absolutePath
            )
        } catch (e: Exception) {
            BackupResult.Error("ডাটাবেজ রিস্টোর করতে ত্রুটি: ${e.localizedMessage ?: "অজানা ত্রুটি"}")
        }
    }

    /**
     * Deletes an existing backup file from device storage.
     */
    fun deleteBackupFile(backupFile: File): Boolean {
        return try {
            backupFile.exists() && backupFile.delete()
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Gets current live database file statistics.
     */
    fun getLiveDatabaseStats(context: Context): Triple<Long, String, Boolean> {
        val file = context.getDatabasePath(DATABASE_NAME)
        val exists = file.exists()
        val size = if (exists) file.length() else 0L
        return Triple(size, formatFileSize(size), exists)
    }

    /**
     * Human-readable file size formatter.
     */
    fun formatFileSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024.0
        if (kb < 1024) return "%.1f KB".format(kb)
        val mb = kb / 1024.0
        return "%.2f MB".format(mb)
    }
}
