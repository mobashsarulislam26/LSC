package com.example

import com.example.data.service.ParticipantImportService
import com.example.data.service.ScheduleBulkUploadService
import org.junit.Assert.*
import org.junit.Test

class ParticipantImportAndScheduleBulkUploadTest {

    @Test
    fun testParseJsonArrayParticipants() {
        val json = """
            [
              {
                "campCode": "B-01",
                "fileNo": "F-1001",
                "nameBn": "মুহাম্মদ আব্দুল্লাহ",
                "nameEn": "Muhammad Abdullah",
                "groupId": "bukhari",
                "subgroup": "বুখারী ১",
                "roomNo": "১০১",
                "guardianPhone": "01711-100001"
              },
              {
                "campCode": "M-01",
                "fileNo": "F-1013",
                "nameBn": "হাসান মাহমুদ",
                "nameEn": "Hasan Mahmud",
                "groupId": "muslim",
                "subgroup": "মুসলিম ১",
                "roomNo": "১০২",
                "guardianPhone": "01711-100013"
              }
            ]
        """.trimIndent()

        val participants = ParticipantImportService.parseCsvFormattedJson(json)
        assertEquals(2, participants.size)
        assertEquals("B-01", participants[0].campCode)
        assertEquals("মুহাম্মদ আব্দুল্লাহ", participants[0].nameBn)
        assertEquals("bukhari", participants[0].groupId)
        assertEquals("M-01", participants[1].campCode)
        assertEquals("হাসান মাহমুদ", participants[1].nameBn)
        assertEquals("muslim", participants[1].groupId)
    }

    @Test
    fun testParseCsvWrappedInJson() {
        val json = """
            {
              "csv": "campCode,fileNo,nameBn,nameEn,groupId,subgroup,roomNo,guardianName,guardianPhone\nB-01,F-1001,মুহাম্মদ আব্দুল্লাহ,Muhammad Abdullah,বুখারী,বুখারী ১,১০১,আব্দুর রহমান,01711-100001\nA-01,F-1025,তারিক জামিল,Tariq Jameel,আবু দাউদ,আবু দাউদ ১,১০৩,জামিল সাহেব,01711-100025"
            }
        """.trimIndent()

        val participants = ParticipantImportService.parseCsvFormattedJson(json)
        assertEquals(2, participants.size)
        assertEquals("B-01", participants[0].campCode)
        assertEquals("bukhari", participants[0].groupId)
        assertEquals("A-01", participants[1].campCode)
        assertEquals("abu_dawood", participants[1].groupId)
    }

    @Test
    fun testGetOfficialCampSchedule() {
        val schedule = ScheduleBulkUploadService.getOfficialCampSchedule()
        assertTrue("Official schedule should have events", schedule.isNotEmpty())
        val days = schedule.map { it.dayNo }.toSet()
        assertTrue("Day 1 should be present", days.contains(1))
        assertTrue("Day 2 should be present", days.contains(2))
        assertTrue("Day 3 should be present", days.contains(3))
        assertTrue("Day 4 should be present", days.contains(4))
        assertTrue("Day 5 should be present", days.contains(5))

        // Check essential fields
        schedule.forEach { event ->
            assertTrue("DayNo must be between 1 and 5", event.dayNo in 1..5)
            assertTrue("Time slot must not be blank", event.timeSlot.isNotBlank())
            assertTrue("Bengali title must not be blank", event.titleBn.isNotBlank())
            assertTrue("Location must not be blank", event.location.isNotBlank())
        }
    }

    @Test
    fun testParseCustomScheduleJson() {
        val json = """
            [
              {
                "dayNo": 1,
                "timeSlot": "05:00 - 06:00",
                "titleBn": "ফজর নামাজ ও কুরআন চর্চা",
                "titleEn": "Fajr Prayer and Quran Recitation",
                "location": "ক্যাম্প জামাত চত্বর / মসজিদ",
                "category": "Jamaat"
              },
              {
                "dayNo": 1,
                "timeSlot": "06:00 - 07:00",
                "titleBn": "প্রভাতী শরীরচর্চা ও পিটি ড্রিল",
                "titleEn": "Morning PT Drill",
                "location": "প্রধান প্যারেড মাঠ",
                "category": "Station"
              }
            ]
        """.trimIndent()

        val events = ScheduleBulkUploadService.parseScheduleData(json)
        assertEquals(2, events.size)
        assertEquals(1, events[0].dayNo)
        assertEquals("05:00 - 06:00", events[0].timeSlot)
        assertEquals("ফজর নামাজ ও কুরআন চর্চা", events[0].titleBn)
        assertEquals("ক্যাম্প জামাত চত্বর / মসজিদ", events[0].location)
        assertEquals("Jamaat", events[0].category)

        assertEquals("প্রভাতী শরীরচর্চা ও পিটি ড্রিল", events[1].titleBn)
        assertEquals("প্রধান প্যারেড মাঠ", events[1].location)
    }

    @Test
    fun testParseCustomScheduleCsv() {
        val csv = """
            dayNo,timeSlot,titleBn,titleEn,location,category
            2,05:00 - 06:00,ফজর নামাজ ও তাহাজ্জুদ,Fajr Prayer,মসজিদ চত্বর,Jamaat
            2,08:30 - 11:30,দক্ষতা স্টেশন রোটেশন,Station Rotation,স্টেশন চত্বর,Station
        """.trimIndent()

        val events = ScheduleBulkUploadService.parseScheduleData(csv)
        assertEquals(2, events.size)
        assertEquals(2, events[0].dayNo)
        assertEquals("05:00 - 06:00", events[0].timeSlot)
        assertEquals("ফজর নামাজ ও তাহাজ্জুদ", events[0].titleBn)
        assertEquals("মসজিদ চত্বর", events[0].location)
    }
}
