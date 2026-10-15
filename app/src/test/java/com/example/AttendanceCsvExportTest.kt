package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AttendanceEventEntity
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.StationEntity
import com.example.util.CsvExportHelper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AttendanceCsvExportTest {

    @Test
    fun testGenerateAttendanceCsvFormatsCorrectly() {
        val records = listOf(
            AttendanceRecordEntity(
                id = 101L,
                eventId = 2L,
                participantId = 1L,
                campCode = "LSC-001",
                participantName = "আব্দুল্লাহ আল মামুন",
                groupName = "বুখারী",
                subgroup = "A-1",
                status = "PRESENT",
                scannedAt = 1729000000000L,
                method = "QR_SCAN",
                overrideReason = null,
                isSynced = true
            ),
            AttendanceRecordEntity(
                id = 102L,
                eventId = 2L,
                participantId = 2L,
                campCode = "LSC-002",
                participantName = "তানভীর হাসান",
                groupName = "মুসলিম",
                subgroup = "B-2",
                status = "LATE",
                scannedAt = 1729000300000L,
                method = "ROSTER_TAP",
                overrideReason = "দেরিতে পৌঁছানো",
                isSynced = false
            ),
            AttendanceRecordEntity(
                id = 103L,
                eventId = 3L,
                participantId = 3L,
                campCode = "LSC-003",
                participantName = "সাকিব আহমেদ",
                groupName = "তিরমিযী",
                subgroup = "C-1",
                status = "ABSENT",
                scannedAt = 1729000600000L,
                method = "MANUAL_OVERRIDE",
                overrideReason = "অসুস্থতা",
                isSynced = true
            )
        )

        val events = listOf(
            AttendanceEventEntity(
                id = 2L,
                titleBn = "ফজর নামাজ ও সকালের সমাবেশ",
                titleEn = "Fajr Prayer & Assembly",
                dayNo = 1,
                timeLabel = "05:00 AM - 06:15 AM",
                targetCount = 603,
                presentCount = 590,
                lateCount = 8,
                absentCount = 5
            )
        )

        val stations = listOf(
            StationEntity(
                id = 3,
                nameBn = "কুরআন ও তাজবীদ",
                nameEn = "Quran & Tajweed",
                moderatorName = "কারী হাফিজুর রহমান",
                location = "মেইন অডিটোরিয়াম ৩য় তলা",
                objectiveBn = "সহীহ তিলাওয়াত ও তাজবীদ প্রশিক্ষণ",
                iconKey = "quran"
            )
        )

        val csv = CsvExportHelper.generateAttendanceCsv(records, events, stations)

        // Assert UTF-8 BOM
        assertTrue("CSV should start with UTF-8 BOM for Excel Unicode compatibility", csv.startsWith("\uFEFF"))

        // Assert CSV Header row
        assertTrue("CSV header should contain SL", csv.contains("SL"))
        assertTrue("CSV header should contain Record ID", csv.contains("Record ID"))
        assertTrue("CSV header should contain Camp ID", csv.contains("Camp ID"))
        assertTrue("CSV header should contain Participant Name", csv.contains("Participant Name"))
        assertTrue("CSV header should contain Attendance Status", csv.contains("Attendance Status"))
        assertTrue("CSV header should contain Check-in Method", csv.contains("Check-in Method"))

        // Assert Records data
        assertTrue("Contains participant LSC-001", csv.contains("LSC-001"))
        assertTrue("Contains participant name আব্দুল্লাহ আল মামুন", csv.contains("আব্দুল্লাহ আল মামুন"))
        assertTrue("Contains group বুখারী", csv.contains("বুখারী"))
        assertTrue("Contains event title ফজর নামাজ ও সকালের সমাবেশ", csv.contains("ফজর নামাজ ও সকালের সমাবেশ"))
        assertTrue("Contains status Present", csv.contains("উপস্থিত (Present)"))

        // Assert Late record
        assertTrue("Contains participant LSC-002", csv.contains("LSC-002"))
        assertTrue("Contains late reason দেরিতে পৌঁছানো", csv.contains("দেরিতে পৌঁছানো"))
        assertTrue("Contains status Late", csv.contains("দেরিতে (Late)"))

        // Assert Station mapped event
        assertTrue("Contains station name কুরআন ও তাজবীদ", csv.contains("কুরআন ও তাজবীদ"))

        val lines = csv.lines().filter { it.isNotBlank() }
        assertEquals("Should have 1 header line + 3 record lines", 4, lines.size)
    }

    @Test
    fun testCreateAttendanceCsvCacheFileAndShareIntent() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val records = listOf(
            AttendanceRecordEntity(
                id = 1L,
                eventId = 1L,
                participantId = 1L,
                campCode = "LSC-001",
                participantName = "আব্দুল্লাহ",
                groupName = "বুখারী",
                subgroup = "A-1",
                status = "PRESENT",
                scannedAt = System.currentTimeMillis()
            )
        )

        val cacheFile = CsvExportHelper.createAttendanceCsvCacheFile(app, records)
        assertTrue("Cache file should exist", cacheFile.exists())
        assertTrue("Cache file should have .csv extension", cacheFile.name.endsWith(".csv"))
        assertTrue("Cache file should contain content", cacheFile.length() > 0)

        val shareIntent = CsvExportHelper.getAttendanceShareIntent(app, cacheFile)
        assertNotNull("Share intent should not be null", shareIntent)
        assertEquals("Intent action should be ACTION_CHOOSER", android.content.Intent.ACTION_CHOOSER, shareIntent.action)
    }
}
