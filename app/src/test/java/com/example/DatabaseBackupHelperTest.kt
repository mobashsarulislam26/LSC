package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CampDatabase
import com.example.data.model.ParticipantEntity
import com.example.util.BackupResult
import com.example.util.DatabaseBackupHelper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DatabaseBackupHelperTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testFormatFileSize() {
        assertEquals("500 B", DatabaseBackupHelper.formatFileSize(500))
        assertEquals("1.0 KB", DatabaseBackupHelper.formatFileSize(1024))
        assertEquals("250.0 KB", DatabaseBackupHelper.formatFileSize(250 * 1024))
        assertEquals("2.00 MB", DatabaseBackupHelper.formatFileSize(2 * 1024 * 1024))
    }

    @Test
    fun testVerifySqliteHeader() {
        val tempDir = context.cacheDir

        // 1. Valid SQLite header file
        val validFile = File(tempDir, "valid_test.db")
        FileOutputStream(validFile).use { out ->
            out.write("SQLite format 3\u0000".toByteArray(Charsets.US_ASCII))
            out.write(ByteArray(512)) // padding
        }
        assertTrue(DatabaseBackupHelper.verifySqliteHeader(validFile))

        // 2. Invalid header file
        val invalidFile = File(tempDir, "invalid_test.db")
        FileOutputStream(invalidFile).use { out ->
            out.write("INVALID HEADER DATA".toByteArray(Charsets.US_ASCII))
        }
        assertFalse(DatabaseBackupHelper.verifySqliteHeader(invalidFile))

        // 3. Non-existent file
        val missingFile = File(tempDir, "missing_test.db")
        assertFalse(DatabaseBackupHelper.verifySqliteHeader(missingFile))
    }

    @Test
    fun testGetBackupDirectory() {
        val backupDir = DatabaseBackupHelper.getBackupDirectory(context)
        assertNotNull(backupDir)
        assertTrue(backupDir.exists())
        assertTrue(backupDir.isDirectory)
        assertTrue(backupDir.name == "LSC_Backups")
    }

    @Test
    fun testBackupDatabaseToDocuments_createsValidSqliteBackup() = runBlocking {
        // Initialize the actual Room database
        val db = CampDatabase.getDatabase(context)
        val dao = db.campDao()

        // Insert a test participant to ensure db has data
        val testParticipant = ParticipantEntity(
            campCode = "LSC-TEST-999",
            fileNo = "F-9999",
            nameBn = "ব্যাকআপ টেস্ট",
            nameEn = "Backup Test Participant",
            groupId = "bukhari",
            groupNameBn = "বুখারী",
            groupNameEn = "Bukhari",
            subgroup = "Bukhari-1",
            roomNo = "Block A Room 101",
            bedNo = "Bed-1",
            age = 15,
            districtBn = "ঢাকা",
            districtEn = "Dhaka",
            gender = "Male",
            mealPref = "Regular",
            guardianName = "টেস্ট অভিভাবক",
            guardianPhone = "01711-223344"
        )
        dao.insertParticipant(testParticipant)

        // Ensure database file exists on disk for Robolectric test environment
        val sourceFile = context.getDatabasePath("lsc_camp_database")
        if (!sourceFile.exists()) {
            sourceFile.parentFile?.mkdirs()
            FileOutputStream(sourceFile).use { out ->
                out.write("SQLite format 3\u0000".toByteArray(Charsets.US_ASCII))
                out.write(ByteArray(1024))
            }
        }

        // Execute Backup to Document Storage
        val result = DatabaseBackupHelper.backupDatabaseToDocuments(context)
        assertTrue("Backup result should be Success", result is BackupResult.Success)

        val success = result as BackupResult.Success
        assertNotNull(success.file)
        assertTrue(success.file!!.exists())
        assertTrue(success.sizeBytes > 0)
        assertTrue(success.fileName.endsWith(".db"))
        assertTrue(DatabaseBackupHelper.verifySqliteHeader(success.file!!))

        // Verify that listExistingBackups includes this newly created backup
        val backups = DatabaseBackupHelper.listExistingBackups(context)
        assertTrue("Backups list should not be empty", backups.isNotEmpty())
        val found = backups.find { it.fileName == success.fileName }
        assertNotNull("Backup should be found in existing backups list", found)
        assertTrue(found!!.isValidSqlite)
        assertEquals(success.sizeBytes, found.sizeBytes)
    }
}
