package com.example

import com.example.data.model.*
import com.example.ui.screens.dashboard.aggregateTeamStationScores
import com.example.ui.screens.dashboard.deriveStationCheckInMetrics
import org.junit.Assert.*
import org.junit.Test

class RechartsLeaderboardAndStationSummaryTest {

    private val sampleGroups = listOf(
        CampGroupEntity(
            id = "bukhari",
            nameBn = "বুখারী",
            nameEn = "Bukhari",
            category = "Senior",
            leaderName = "মাওলানা আব্দুল করিম",
            colorHex = "#1D4ED8",
            totalPoints = 85.0
        ),
        CampGroupEntity(
            id = "sumaiya",
            nameBn = "সুমাইয়া",
            nameEn = "Sumaiya",
            category = "Senior",
            leaderName = "উম্মে সালমা",
            colorHex = "#BE185D",
            totalPoints = 92.5
        ),
        CampGroupEntity(
            id = "shafeyi",
            nameBn = "শাফেয়ী",
            nameEn = "Shafeyi",
            category = "Junior",
            leaderName = "মুফতি আব্দুর রহমান",
            colorHex = "#059669",
            totalPoints = 78.0
        )
    )

    private val sampleStations = listOf(
        StationEntity(id = 1, nameBn = "আক্বীদা ও ফিক্বহ", nameEn = "Aqeedah & Fiqh", moderatorName = "মুফতি সাইফুল্লাহ", location = "রুম ১০১", objectiveBn = "মৌলিক আক্বীদা", iconKey = "menu_book"),
        StationEntity(id = 2, nameBn = "কুরআন তিলাওয়াত ও হিফজ", nameEn = "Quran Recitation & Hifz", moderatorName = "ক্বারী আব্দুল্লাহ", location = "মসজিদ হল", objectiveBn = "সহিহ তিলাওয়াত", iconKey = "record_voice_over"),
        StationEntity(id = 3, nameBn = "হাদীস অধ্যয়ন", nameEn = "Hadith Study", moderatorName = "শায়খ আহমদুল্লাহ", location = "রুম ১০২", objectiveBn = "হাদীস পাঠ", iconKey = "auto_stories"),
        StationEntity(id = 4, nameBn = "দাওয়াহ ও নেতৃত্ব", nameEn = "Dawah & Leadership", moderatorName = "মাওলানা তারিক", location = "কনফারেন্স রুম", objectiveBn = "নেতৃত্ব গঠন", iconKey = "campaign"),
        StationEntity(id = 5, nameBn = "বিজ্ঞান ও প্রযুক্তি", nameEn = "Science & Tech", moderatorName = "ড. জহিরুল ইসলাম", location = "আইটি ল্যাব", objectiveBn = "প্রযুক্তি চেতনা", iconKey = "biotech"),
        StationEntity(id = 6, nameBn = "শারীরিক সুস্থতা ও ক্রীড়া", nameEn = "Physical Fitness & Sports", moderatorName = "ওস্তাদ তানভীর", location = "খেলার মাঠ", objectiveBn = "শারীরিক গঠন", iconKey = "fitness_center")
    )

    @Test
    fun testAggregateTeamStationScoresAggregationAndRanking() {
        // Competition scores across different stations (station 1, 2, 3)
        val scores = listOf(
            // Sumaiya gets 95.0 at Station 1 and 90.0 at Station 2 -> Total = 185.0
            CompetitionScoreEntity(id = 1, competitionId = 1, participantId = 101, campCode = "LSC-001", participantName = "ফাতেমা", groupName = "সুমাইয়া", subgroup = "Sumaiya-1", accuracyScore = 50.0, presentationScore = 25.0, disciplineScore = 20.0, totalScore = 95.0),
            CompetitionScoreEntity(id = 2, competitionId = 2, participantId = 102, campCode = "LSC-002", participantName = "আয়েশা", groupName = "সুমাইয়া", subgroup = "Sumaiya-2", accuracyScore = 45.0, presentationScore = 25.0, disciplineScore = 20.0, totalScore = 90.0),

            // Bukhari gets 80.0 at Station 1 and 85.0 at Station 3 -> Total = 165.0
            CompetitionScoreEntity(id = 3, competitionId = 1, participantId = 103, campCode = "LSC-003", participantName = "আব্দুল্লাহ", groupName = "বুখারী", subgroup = "Bukhari-1", accuracyScore = 40.0, presentationScore = 20.0, disciplineScore = 20.0, totalScore = 80.0),
            CompetitionScoreEntity(id = 4, competitionId = 3, participantId = 104, campCode = "LSC-004", participantName = "ওমর", groupName = "বুখারী", subgroup = "Bukhari-2", accuracyScore = 45.0, presentationScore = 20.0, disciplineScore = 20.0, totalScore = 85.0),

            // Shafeyi gets 70.0 at Station 2 -> Total = 70.0
            CompetitionScoreEntity(id = 5, competitionId = 2, participantId = 105, campCode = "LSC-005", participantName = "হাসান", groupName = "শাফেয়ী", subgroup = "Shafeyi-1", accuracyScore = 35.0, presentationScore = 20.0, disciplineScore = 15.0, totalScore = 70.0)
        )

        val aggregated = aggregateTeamStationScores(
            groups = sampleGroups,
            competitionScores = scores,
            selectedStationFilter = "ALL"
        )

        assertEquals("All 3 sample groups should be represented", 3, aggregated.size)

        // Rank 1: Sumaiya (185.0 points)
        val rank1 = aggregated[0]
        assertEquals("Rank 1 should be Sumaiya", "সুমাইয়া", rank1.teamNameBn)
        assertEquals("Rank 1 total score", 185.0, rank1.totalScore, 0.01)
        assertEquals("Rank 1 rank value", 1, rank1.rank)
        assertEquals("Rank 1 medal", "🥇", rank1.podiumMedal)
        assertEquals("Sumaiya station 1 score", 95.0, rank1.stationScores[1] ?: 0.0, 0.01)
        assertEquals("Sumaiya station 2 score", 90.0, rank1.stationScores[2] ?: 0.0, 0.01)

        // Rank 2: Bukhari (165.0 points)
        val rank2 = aggregated[1]
        assertEquals("Rank 2 should be Bukhari", "বুখারী", rank2.teamNameBn)
        assertEquals("Rank 2 total score", 165.0, rank2.totalScore, 0.01)
        assertEquals("Rank 2 rank value", 2, rank2.rank)
        assertEquals("Rank 2 medal", "🥈", rank2.podiumMedal)
        assertEquals("Bukhari station 1 score", 80.0, rank2.stationScores[1] ?: 0.0, 0.01)
        assertEquals("Bukhari station 3 score", 85.0, rank2.stationScores[3] ?: 0.0, 0.01)

        // Rank 3: Shafeyi (70.0 points)
        val rank3 = aggregated[2]
        assertEquals("Rank 3 should be Shafeyi", "শাফেয়ী", rank3.teamNameBn)
        assertEquals("Rank 3 total score", 70.0, rank3.totalScore, 0.01)
        assertEquals("Rank 3 rank value", 3, rank3.rank)
        assertEquals("Rank 3 medal", "🥉", rank3.podiumMedal)
    }

    @Test
    fun testAggregateTeamStationScoresWithStationFilter() {
        val scores = listOf(
            CompetitionScoreEntity(id = 1, competitionId = 1, participantId = 101, campCode = "LSC-001", participantName = "ফাতেমা", groupName = "সুমাইয়া", subgroup = "Sumaiya-1", accuracyScore = 50.0, presentationScore = 25.0, disciplineScore = 20.0, totalScore = 95.0),
            CompetitionScoreEntity(id = 2, competitionId = 2, participantId = 102, campCode = "LSC-002", participantName = "আয়েশা", groupName = "সুমাইয়া", subgroup = "Sumaiya-2", accuracyScore = 45.0, presentationScore = 25.0, disciplineScore = 20.0, totalScore = 90.0),
            CompetitionScoreEntity(id = 3, competitionId = 1, participantId = 103, campCode = "LSC-003", participantName = "আব্দুল্লাহ", groupName = "বুখারী", subgroup = "Bukhari-1", accuracyScore = 40.0, presentationScore = 20.0, disciplineScore = 20.0, totalScore = 80.0)
        )

        // Filter by S1 (Station 1)
        val s1Aggregated = aggregateTeamStationScores(
            groups = sampleGroups,
            competitionScores = scores,
            selectedStationFilter = "S1"
        )

        assertEquals(3, s1Aggregated.size)
        // Sumaiya scored 95 at S1, Bukhari scored 80 at S1, Shafeyi scored 0
        assertEquals("Sumaiya", s1Aggregated[0].teamNameEn)
        assertEquals(95.0, s1Aggregated[0].totalScore, 0.01)

        assertEquals("Bukhari", s1Aggregated[1].teamNameEn)
        assertEquals(80.0, s1Aggregated[1].totalScore, 0.01)

        assertEquals("Shafeyi", s1Aggregated[2].teamNameEn)
        assertEquals(0.0, s1Aggregated[2].totalScore, 0.01)
    }

    @Test
    fun testDeriveStationCheckInMetricsPullsFromAttendanceRecords() {
        val scheduleEvents = listOf(
            ScheduleEventEntity(
                id = 1,
                dayNo = 1,
                timeSlot = "09:00 - 10:15",
                titleBn = "স্টেশন সেশন ১",
                titleEn = "Station Session 1",
                category = "Station",
                stationId = 1,
                isCurrent = true,
                rotationJson = "bukhari:1,sumaiya:2,shafeyi:3"
            )
        )

        val attendanceRecords = listOf(
            // 3 records for station 1 (bukhari)
            AttendanceRecordEntity(id = 1, eventId = 1, participantId = 1, campCode = "LSC-001", participantName = "রহিম", groupName = "বুখারী", subgroup = "Bukhari-1", status = "PRESENT"),
            AttendanceRecordEntity(id = 2, eventId = 1, participantId = 2, campCode = "LSC-002", participantName = "করিম", groupName = "বুখারী", subgroup = "Bukhari-1", status = "PRESENT"),
            AttendanceRecordEntity(id = 3, eventId = 1, participantId = 3, campCode = "LSC-003", participantName = "সাঈদ", groupName = "বুখারী", subgroup = "Bukhari-2", status = "LATE"),
            // 1 record for station 2 (sumaiya)
            AttendanceRecordEntity(id = 4, eventId = 1, participantId = 4, campCode = "LSC-004", participantName = "আয়েশা", groupName = "সুমাইয়া", subgroup = "Sumaiya-1", status = "PRESENT")
        )

        val metrics = deriveStationCheckInMetrics(
            stations = sampleStations,
            attendanceRecords = attendanceRecords,
            scheduleEvents = scheduleEvents
        )

        assertEquals("Should compute metrics for all 6 stations", 6, metrics.size)

        // Station 1: checked in count should be 3
        val s1 = metrics.find { it.station.id == 1 }
        assertNotNull(s1)
        assertEquals("Station 1 checked in count", 3, s1!!.checkedInCount)
        assertTrue("Station 1 target count should be positive", s1.targetCount > 0)
        assertTrue("Station 1 percentage should be calculated correctly", s1.checkInPercentage > 0.0f)
        assertEquals("Station 1 active team", "বুখারী", s1.activeTeamNameBn)

        // Station 2: checked in count should be 1
        val s2 = metrics.find { it.station.id == 2 }
        assertNotNull(s2)
        assertEquals("Station 2 checked in count", 1, s2!!.checkedInCount)
        assertEquals("Station 2 active team", "সুমাইয়া", s2.activeTeamNameBn)

        // Station 3: checked in count should be 0
        val s3 = metrics.find { it.station.id == 3 }
        assertNotNull(s3)
        assertEquals("Station 3 checked in count", 0, s3!!.checkedInCount)
        assertEquals(0.0f, s3.checkInPercentage, 0.01f)
    }

    @Test
    fun testHealthRecordEntityDefaultsAndPriorityFlagging() {
        val normalRecord = HealthRecordEntity(
            id = 1L,
            participantId = 10L,
            campCode = "LSC-010",
            participantName = "মাহমুদ",
            groupName = "বুখারী",
            checkupType = "GENERAL",
            doctorName = "ডাঃ রফিকুল ইসলাম",
            vitals = "BP: 110/75, Temp: 98.4°F",
            diagnosis = "স্বাভাবিক স্বাস্থ্য পরীক্ষা",
            adviceAndMeds = "পর্যাপ্ত পানি পান ও বিশ্রাম",
            isEmergencyFlag = false
        )

        assertEquals("Default priority should be NORMAL", "NORMAL", normalRecord.priority)
        assertEquals("Default status should be PENDING", "PENDING", normalRecord.status)
        assertFalse(normalRecord.isEmergencyFlag)

        val emergencyRecord = HealthRecordEntity(
            id = 2L,
            participantId = 29L,
            campCode = "LSC-029",
            participantName = "জুনায়েদ হোসাইন",
            groupName = "বুখারী",
            checkupType = "EMERGENCY",
            doctorName = "ডাঃ রফিকুল ইসলাম (মেডিকেল অফিসার)",
            vitals = "SpO2: 91%, Pulse: 115, Resp: 28/min",
            diagnosis = "তীব্র ব্রঙ্কিয়াল অ্যাজমা অ্যাটাক (Acute Bronchial Asthma Exacerbation)",
            adviceAndMeds = "Salbutamol 5mg Nebulization stat + Hydrocortisone 100mg IV",
            isEmergencyFlag = true,
            priority = "HIGH",
            status = "PENDING"
        )

        assertEquals("HIGH", emergencyRecord.priority)
        assertEquals("PENDING", emergencyRecord.status)
        assertTrue(emergencyRecord.isEmergencyFlag)

        // Test filtering logic for high priority pending health alerts
        val records = listOf(normalRecord, emergencyRecord)
        val highPriorityPending = records.filter { record ->
            (record.isEmergencyFlag || record.priority == "HIGH" || record.checkupType == "EMERGENCY") &&
            record.status != "RESOLVED"
        }

        assertEquals(1, highPriorityPending.size)
        assertEquals(29L, highPriorityPending[0].participantId)
        assertEquals("LSC-029", highPriorityPending[0].campCode)
    }
}
