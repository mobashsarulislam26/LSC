package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.model.ParticipantEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExportHelper {

    private const val UTF8_BOM = "\uFEFF"

    /**
     * Escapes standard CSV fields according to RFC 4180.
     */
    fun escapeCsv(value: String): String {
        val needsQuotes = value.contains(",") ||
                value.contains("\"") ||
                value.contains("\n") ||
                value.contains("\r")
        return if (needsQuotes) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }

    /**
     * Generates a fully formatted CSV string from participant entities with UTF-8 BOM.
     */
    fun generateParticipantsCsv(participants: List<ParticipantEntity>): String {
        val sb = StringBuilder()
        // Prepend UTF-8 BOM for Excel compatibility with Bengali Unicode
        sb.append(UTF8_BOM)

        // CSV Header for Camp Administration Report
        val headers = listOf(
            "SL",
            "Camp ID",
            "File No",
            "Name (Bangla)",
            "Name (English)",
            "Gender",
            "Age",
            "District (Bangla)",
            "District (English)",
            "Group (Bangla)",
            "Group (English)",
            "Sub-group",
            "Room No",
            "Bed No",
            "Meal Preference",
            "Guardian Name",
            "Guardian Phone",
            "Check-in Status",
            "Medical Flag",
            "Attendance (%)",
            "Total Score"
        )
        sb.append(headers.joinToString(",") { escapeCsv(it) }).append("\r\n")

        // Rows
        participants.forEachIndexed { index, p ->
            val row = listOf(
                (index + 1).toString(),
                p.campCode,
                p.fileNo,
                p.nameBn,
                p.nameEn,
                p.gender,
                p.age.toString(),
                p.districtBn,
                p.districtEn,
                p.groupNameBn,
                p.groupNameEn,
                p.subgroup,
                p.roomNo,
                p.bedNo,
                p.mealPref,
                p.guardianName,
                p.guardianPhone,
                if (p.isCheckedIn) "Checked In" else "Pending",
                p.medicalFlag ?: "None",
                String.format(Locale.US, "%.1f", p.attendancePercentage),
                String.format(Locale.US, "%.1f", p.totalScore)
            )
            sb.append(row.joinToString(",") { escapeCsv(it) }).append("\r\n")
        }

        return sb.toString()
    }

    /**
     * Writes CSV data to a cache file and returns the File handle.
     */
    fun createCsvCacheFile(
        context: Context,
        participants: List<ParticipantEntity>,
        fileNamePrefix: String = "LSC_Camp_Participants_Report"
    ): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "${fileNamePrefix}_${timestamp}.csv"
        val file = File(context.cacheDir, fileName)
        
        val csvContent = generateParticipantsCsv(participants)
        FileOutputStream(file).use { fos ->
            fos.write(csvContent.toByteArray(Charsets.UTF_8))
        }
        return file
    }

    /**
     * Builds an Intent to share the CSV file via FileProvider.
     */
    fun getShareIntent(context: Context, file: File, chooserTitle: String = "Export Participant List CSV"): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "LSC Leadership Camp - Participant List Report")
            putExtra(
                Intent.EXTRA_TEXT,
                "Attached is the official LSC Leadership Camp participant list report exported as CSV."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return Intent.createChooser(sendIntent, chooserTitle)
    }

    /**
     * Copies CSV text to the system clipboard.
     */
    fun copyToClipboard(context: Context, text: String, label: String = "Participant CSV Data"): Boolean {
        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
            true
        } catch (_: Exception) {
            false
        }
    }
}
