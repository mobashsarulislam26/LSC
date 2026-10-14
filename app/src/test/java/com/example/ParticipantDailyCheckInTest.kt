package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CampDatabase
import com.example.data.local.ParticipantHealthDao
import com.example.data.model.ParticipantEntity
import com.example.data.model.ParticipantHealthStatusEntity
import com.example.data.repository.CampRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ParticipantDailyCheckInTest {

    private lateinit var db: CampDatabase
    private lateinit var healthDao: ParticipantHealthDao
    private lateinit var repository: CampRepository

    private val sampleParticipant = ParticipantEntity(
        id = 205L,
        campCode = "LSC-2026-205",
        fileNo = "F-1205",
        nameBn = "আব্দুর রহমান",
        nameEn = "Abdur Rahman",
        districtBn = "সিলেট",
        districtEn = "Sylhet",
        gender = "Male",
        age = 14,
        groupId = "bukhari",
        groupNameBn = "বুখারী",
        groupNameEn = "Bukhari",
        subgroup = "Bukhari-2",
        roomNo = "Room 204",
        bedNo = "Bed-B",
        mealPref = "রেগুলার",
        guardianName = "মোস্তাফিজুর রহমান",
        guardianPhone = "01712-345678",
        medicalFlag = "ডাস্ট এলার্জি (Dust Allergy)"
    )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CampDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        healthDao = db.participantHealthDao()
        repository = CampRepository(db.campDao(), healthDao)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testRecordFeelingWellCheckIn() = runBlocking {
        // Record "Feeling well"
        val saved = repository.recordDailyHealthCheckIn(
            participant = sampleParticipant,
            status = "Feeling well",
            notes = "স্বাভাবিকভাবে সকালের কার্যক্রমে অংশ নিয়েছে",
            checkInDate = "2026-10-13"
        )

        assertNotNull(saved)
        assertEquals(205L, saved.participantId)
        assertEquals("LSC-2026-205", saved.campCode)
        assertTrue(saved.isDailyCheckedIn)
        assertEquals("2026-10-13", saved.lastCheckInDate)
        assertEquals("Normal", saved.healthCondition)
        assertTrue(saved.dailyCheckInLogs.contains("Feeling well"))
        assertTrue(saved.dailyCheckInLogs.contains("স্বাভাবিকভাবে সকালের কার্যক্রমে অংশ নিয়েছে"))

        // Verify retrieval from database
        val retrieved = repository.getParticipantHealthStatus(205L)
        assertNotNull(retrieved)
        assertEquals(true, retrieved?.isDailyCheckedIn)
        assertTrue(retrieved?.dailyCheckInLogs?.contains("Feeling well") == true)
    }

    @Test
    fun testRecordNeedsRestCheckIn() = runBlocking {
        // Record "Needs rest"
        val saved = repository.recordDailyHealthCheckIn(
            participant = sampleParticipant,
            status = "Needs rest",
            notes = "রাতে পর্যাপ্ত ঘুম হয়নি, সামান্য ক্লান্তি",
            checkInDate = "2026-10-13"
        )

        assertNotNull(saved)
        assertTrue(saved.isDailyCheckedIn)
        assertEquals("Needs Rest", saved.healthCondition)
        assertTrue(saved.dailyCheckInLogs.contains("Needs rest"))
        assertTrue(saved.dailyCheckInLogs.contains("রাতে পর্যাপ্ত ঘুম হয়নি"))

        // Sequential check-in update (e.g. later in the day feeling well)
        val secondCheckIn = repository.recordDailyHealthCheckIn(
            participant = sampleParticipant,
            status = "Feeling well",
            notes = "বিশ্রামের পর সুস্থ অনুভব করছে",
            checkInDate = "2026-10-13"
        )

        assertTrue(secondCheckIn.dailyCheckInLogs.contains("Needs rest"))
        assertTrue(secondCheckIn.dailyCheckInLogs.contains("Feeling well"))
        assertEquals("Normal", secondCheckIn.healthCondition)
    }
}
