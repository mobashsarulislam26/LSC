package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.model.AttendanceEventEntity
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.ParticipantEntity
import com.example.data.model.StationEntity
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
        val uri = try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (_: Exception) {
            android.net.Uri.fromFile(file)
        }

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

    /**
     * Generates a fully formatted CSV string from AttendanceRecordEntity list with UTF-8 BOM for Excel compatibility.
     */
    fun generateAttendanceCsv(
        records: List<AttendanceRecordEntity>,
        events: List<AttendanceEventEntity> = emptyList(),
        stations: List<StationEntity> = emptyList()
    ): String {
        val sb = StringBuilder()
        sb.append(UTF8_BOM)

        val eventMap = events.associateBy { it.id }
        val stationMap = stations.associateBy { it.id.toLong() }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US)

        val headers = listOf(
            "SL",
            "Record ID",
            "Camp ID",
            "Participant Name",
            "Group",
            "Subgroup",
            "Event / Station",
            "Attendance Status",
            "Check-in Method",
            "Check-in Date & Time",
            "Override / Notes",
            "Sync Status"
        )
        sb.append(headers.joinToString(",") { escapeCsv(it) }).append("\r\n")

        records.forEachIndexed { index, record ->
            val eventTitle = eventMap[record.eventId]?.let { "${it.titleBn} (${it.titleEn})" }
                ?: stationMap[record.eventId]?.let { "স্টেশন ${it.id}: ${it.nameBn}" }
                ?: "ইভেন্ট #${record.eventId}"

            val statusLabel = when (record.status.uppercase()) {
                "PRESENT" -> "উপস্থিত (Present)"
                "LATE" -> "দেরিতে (Late)"
                "ABSENT" -> "অনুপস্থিত (Absent)"
                "EXCUSED" -> "অনুমোদিত ছুটি (Excused)"
                "MEDICAL" -> "মেডিকেল ছুটি (Medical)"
                else -> record.status
            }

            val methodLabel = when (record.method.uppercase()) {
                "QR_SCAN" -> "QR স্ক্যান (QR Code)"
                "ROSTER_TAP" -> "রোস্টার ট্যাপ (Roster Tap)"
                "MANUAL_OVERRIDE" -> "ম্যানুয়াল এন্ট্রি (Manual Override)"
                else -> record.method
            }

            val formattedTime = try {
                dateFormat.format(Date(record.scannedAt))
            } catch (_: Exception) {
                record.scannedAt.toString()
            }

            val syncLabel = if (record.isSynced) "সিঙ্ক সম্পন্ন (Synced)" else "অপেক্ষমাণ (Pending)"

            val row = listOf(
                (index + 1).toString(),
                record.id.toString(),
                record.campCode,
                record.participantName,
                record.groupName,
                record.subgroup,
                eventTitle,
                statusLabel,
                methodLabel,
                formattedTime,
                record.overrideReason ?: "—",
                syncLabel
            )
            sb.append(row.joinToString(",") { escapeCsv(it) }).append("\r\n")
        }

        return sb.toString()
    }

    /**
     * Writes attendance CSV data to cache and returns the File handle.
     */
    fun createAttendanceCsvCacheFile(
        context: Context,
        records: List<AttendanceRecordEntity>,
        events: List<AttendanceEventEntity> = emptyList(),
        stations: List<StationEntity> = emptyList(),
        fileNamePrefix: String = "LSC_Attendance_Records"
    ): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "${fileNamePrefix}_${timestamp}.csv"
        val file = File(context.cacheDir, fileName)

        val csvContent = generateAttendanceCsv(records, events, stations)
        FileOutputStream(file).use { fos ->
            fos.write(csvContent.toByteArray(Charsets.UTF_8))
        }
        return file
    }

    /**
     * Builds an Intent to share attendance CSV file via FileProvider.
     */
    fun getAttendanceShareIntent(
        context: Context,
        file: File,
        chooserTitle: String = "Export Attendance Records CSV"
    ): Intent {
        val uri = try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (_: Exception) {
            android.net.Uri.fromFile(file)
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "LSC Leadership Camp - Attendance Records CSV Report")
            putExtra(
                Intent.EXTRA_TEXT,
                "Attached is the official attendance records CSV report from LSC Camp Management System."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return Intent.createChooser(sendIntent, chooserTitle)
    }
}
