package com.example

import com.example.data.model.CompetitionEntity
import com.example.data.model.CompetitionScoreEntity
import com.example.data.model.ParticipantEntity
import com.example.data.model.StationEntity
import com.example.ui.screens.competitions.deriveParticipantStationMilestones
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class StationMilestoneTrackerTest {

    private lateinit var testStations: List<StationEntity>
    private lateinit var testCompetitions: List<CompetitionEntity>

    private val testParticipant = ParticipantEntity(
        id = 201L,
        campCode = "LSC-2026-201",
        fileNo = "F-1201",
        nameBn = "তাহমিদ জামান",
        nameEn = "Tahmid Zaman",
        districtBn = "চট্টগ্রাম",
        districtEn = "Chattogram",
        gender = "Male",
        age = 15,
        groupId = "bukhari",
        groupNameBn = "বুখারী",
        groupNameEn = "Bukhari",
        subgroup = "Bukhari-1",
        roomNo = "Room 101",
        bedNo = "Bed-1",
        mealPref = "নিয়মিত",
        guardianName = "রফিক জামান",
        guardianPhone = "01819-112233"
    )

    @Before
    fun setUp() {
        testStations = listOf(
            StationEntity(id = 1, nameBn = "স্টেশন ১: প্রতিযোগিতা ও শিষ্টাচার", nameEn = "Station 1", moderatorName = "মডারেটর ১", location = "ভেন্যু ১", objectiveBn = "উদ্দেশ্য ১", iconKey = "quran"),
            StationEntity(id = 2, nameBn = "স্টেশন ২: লাইফ স্কিল ও ব্যবহারিক দক্ষতা", nameEn = "Station 2", moderatorName = "মডারেটর ২", location = "ভেন্যু ২", objectiveBn = "উদ্দেশ্য ২", iconKey = "science"),
            StationEntity(id = 3, nameBn = "স্টেশন ৩: শিশুতোষ আলোচনা ও মানসিক বিকাশ", nameEn = "Station 3", moderatorName = "মডারেটর ৩", location = "ভেন্যু ৩", objectiveBn = "উদ্দেশ্য ৩", iconKey = "speech"),
            StationEntity(id = 4, nameBn = "স্টেশন ৪: ফিকাহ ও সালাত প্রশিক্ষণ", nameEn = "Station 4", moderatorName = "মডারেটর ৪", location = "ভেন্যু ৪", objectiveBn = "উদ্দেশ্য ৪", iconKey = "fitness"),
            StationEntity(id = 5, nameBn = "স্টেশন ৫: রিফ্লেক্টিভ সেশন ও আত্মশুদ্ধি", nameEn = "Station 5", moderatorName = "মডারেটর ৫", location = "ভেন্যু ৫", objectiveBn = "উদ্দেশ্য ৫", iconKey = "arts"),
            StationEntity(id = 6, nameBn = "স্টেশন ৬: মেডিকেল, ডেন্টাল ও আই ক্যাম্প", nameEn = "Station 6", moderatorName = "মডারেটর ৬", location = "ভেন্যু ৬", objectiveBn = "উদ্দেশ্য ৬", iconKey = "medical")
        )

        testCompetitions = listOf(
            CompetitionEntity(id = 1L, nameBn = "হিফজুল হাদিস ও অনুবাদ", nameEn = "Hadith Memorization", category = "Hadith", dayNo = 1, maxScore = 100, judgeName = "বিচারক ১"),
            CompetitionEntity(id = 2L, nameBn = "ক্বিরাত ও তাজবীদ", nameEn = "Quran Recitation", category = "Quran", dayNo = 1, maxScore = 100, judgeName = "বিচারক ২"),
            CompetitionEntity(id = 3L, nameBn = "ইসলামিক সাধারণ জ্ঞান কুইজ", nameEn = "Islamic Quiz", category = "Quiz", dayNo = 2, maxScore = 100, judgeName = "বিচারক ৩"),
            CompetitionEntity(id = 4L, nameBn = "আরবি ও বাংলা ক্যালিগ্রাফি", nameEn = "Calligraphy", category = "Arts", dayNo = 2, maxScore = 100, judgeName = "বিচারক ৪"),
            CompetitionEntity(id = 5L, nameBn = "তাৎক্ষণিক বক্তৃতা ও দলনেতৃত্ব", nameEn = "Extempore Speech", category = "Speech", dayNo = 3, maxScore = 100, judgeName = "বিচারক ৫"),
            CompetitionEntity(id = 6L, nameBn = "শারীরিক সক্ষমতা ও অবস্টাকল", nameEn = "Obstacle Fitness", category = "Sports", dayNo = 4, maxScore = 100, judgeName = "বিচারক ৬")
        )
    }

    @Test
    fun testDeriveParticipantStationMilestones_noScoresPending() {
        val progress = deriveParticipantStationMilestones(
            participant = testParticipant,
            competitions = testCompetitions,
            competitionScores = emptyList(),
            stations = testStations
        )

        assertEquals(6, progress.totalMilestones)
        assertEquals(0, progress.completedCount)
        assertEquals(6, progress.pendingCount)
        assertEquals(0f, progress.completionPercentage, 0.01f)
        assertEquals(0.0, progress.totalScoreEarned, 0.01)

        assertTrue(progress.milestones.all { !it.isCompleted })
        assertTrue(progress.milestones.all { it.scoreRecord == null })
    }

    @Test
    fun testDeriveParticipantStationMilestones_partialCompletion() {
        val sampleScores = listOf(
            CompetitionScoreEntity(
                id = 1L,
                competitionId = 1L,
                participantId = testParticipant.id,
                campCode = testParticipant.campCode,
                participantName = testParticipant.nameBn,
                groupName = testParticipant.groupNameBn,
                subgroup = testParticipant.subgroup,
                accuracyScore = 38.0,
                presentationScore = 28.0,
                disciplineScore = 26.0,
                totalScore = 92.0,
                rank = 1,
                isAwardEligible = true
            ),
            CompetitionScoreEntity(
                id = 2L,
                competitionId = 2L,
                participantId = testParticipant.id,
                campCode = testParticipant.campCode,
                participantName = testParticipant.nameBn,
                groupName = testParticipant.groupNameBn,
                subgroup = testParticipant.subgroup,
                accuracyScore = 35.0,
                presentationScore = 26.0,
                disciplineScore = 27.0,
                totalScore = 88.0,
                rank = 2,
                isAwardEligible = true
            ),
            CompetitionScoreEntity(
                id = 3L,
                competitionId = 4L,
                participantId = testParticipant.id,
                campCode = testParticipant.campCode,
                participantName = testParticipant.nameBn,
                groupName = testParticipant.groupNameBn,
                subgroup = testParticipant.subgroup,
                accuracyScore = 32.0,
                presentationScore = 25.0,
                disciplineScore = 25.0,
                totalScore = 82.0,
                rank = 3,
                isAwardEligible = true
            ),
            CompetitionScoreEntity(
                id = 4L,
                competitionId = 6L,
                participantId = testParticipant.id,
                campCode = testParticipant.campCode,
                participantName = testParticipant.nameBn,
                groupName = testParticipant.groupNameBn,
                subgroup = testParticipant.subgroup,
                accuracyScore = 39.0,
                presentationScore = 29.0,
                disciplineScore = 27.0,
                totalScore = 95.0,
                rank = 1,
                isAwardEligible = true
            )
        )

        val progress = deriveParticipantStationMilestones(
            participant = testParticipant,
            competitions = testCompetitions,
            competitionScores = sampleScores,
            stations = testStations
        )

        assertEquals(6, progress.totalMilestones)
        assertEquals(4, progress.completedCount)
        assertEquals(2, progress.pendingCount)
        assertEquals(66.666f, progress.completionPercentage, 0.1f)
        assertEquals(357.0, progress.totalScoreEarned, 0.01)

        // Station 1 milestone should be completed with score 92
        val stn1Milestone = progress.milestones.find { it.stationId == 1 }
        assertNotNull(stn1Milestone)
        assertTrue(stn1Milestone!!.isCompleted)
        assertEquals(92.0, stn1Milestone.scoreRecord?.totalScore ?: 0.0, 0.01)
        assertEquals(1, stn1Milestone.scoreRecord?.rank)

        // Station 3 milestone (competition 3) should be pending
        val stn3Milestone = progress.milestones.find { it.stationId == 3 }
        assertNotNull(stn3Milestone)
        assertFalse(stn3Milestone!!.isCompleted)
        assertNull(stn3Milestone.scoreRecord)
    }

    @Test
    fun testDeriveParticipantStationMilestones_allCompleted() {
        val allScores = (1..6).map { idx ->
            CompetitionScoreEntity(
                id = idx.toLong(),
                competitionId = idx.toLong(),
                participantId = testParticipant.id,
                campCode = testParticipant.campCode,
                participantName = testParticipant.nameBn,
                groupName = testParticipant.groupNameBn,
                subgroup = testParticipant.subgroup,
                accuracyScore = 35.0,
                presentationScore = 27.0,
                disciplineScore = 28.0,
                totalScore = 90.0,
                rank = 1,
                isAwardEligible = true
            )
        }

        val progress = deriveParticipantStationMilestones(
            participant = testParticipant,
            competitions = testCompetitions,
            competitionScores = allScores,
            stations = testStations
        )

        assertEquals(6, progress.totalMilestones)
        assertEquals(6, progress.completedCount)
        assertEquals(0, progress.pendingCount)
        assertEquals(100f, progress.completionPercentage, 0.01f)
        assertEquals(540.0, progress.totalScoreEarned, 0.01)
        assertTrue(progress.milestones.all { it.isCompleted })
    }

    @Test
    fun testStationMilestoneCardMapping() {
        val progress = deriveParticipantStationMilestones(
            participant = testParticipant,
            competitions = testCompetitions,
            competitionScores = emptyList(),
            stations = testStations
        )

        val stn1 = progress.milestones[0]
        assertEquals(1, stn1.stationId)
        assertEquals("স্টেশন ১: প্রতিযোগিতা ও শিষ্টাচার", stn1.stationNameBn)
        assertEquals("হিফজুল হাদিস ও অনুবাদ", stn1.milestoneTitleBn)
        assertEquals("Hadith", stn1.category)

        val stn6 = progress.milestones[5]
        assertEquals(6, stn6.stationId)
        assertEquals("স্টেশন ৬: মেডিকেল, ডেন্টাল ও আই ক্যাম্প", stn6.stationNameBn)
        assertEquals("Sports", stn6.category)
    }
}
