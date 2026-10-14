package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CampDatabase
import com.example.data.local.ParticipantHealthDao
import com.example.data.model.ParticipantHealthStatusEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ParticipantHealthDatabaseTest {

    private lateinit var db: CampDatabase
    private lateinit var healthDao: ParticipantHealthDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CampDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        healthDao = db.participantHealthDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndRetrieveHealthStatus() = runBlocking {
        val healthStatus = ParticipantHealthStatusEntity(
            id = 1L,
            participantId = 101L,
            campCode = "LSC-2026-101",
            participantName = "তানভীর আহমেদ (Tanvir Ahmed)",
            medicationNeeds = "ইনহেলার (Salbutamol 100mcg) দিনে ২ বার",
            allergies = "ডাস্ট এলার্জি ও চিংড়ি মাছ (Dust & Shrimp)",
            dailyCheckInLogs = "দিন ১: সুস্থ আছে, পালস ৭৪।",
            hasMedicationNeeds = true,
            hasAllergies = true,
            isDailyCheckedIn = true,
            lastCheckInDate = "2026-10-13",
            healthCondition = "Under Observation",
            bloodGroup = "B+"
        )

        val insertedId = healthDao.insertHealthStatus(healthStatus)
        assertTrue(insertedId > 0)

        val retrieved = healthDao.getHealthStatusByParticipantId(101L)
        assertNotNull(retrieved)
        assertEquals("LSC-2026-101", retrieved?.campCode)
        assertEquals("ইনহেলার (Salbutamol 100mcg) দিনে ২ বার", retrieved?.medicationNeeds)
        assertEquals("ডাস্ট এলার্জি ও চিংড়ি মাছ (Dust & Shrimp)", retrieved?.allergies)
        assertEquals("দিন ১: সুস্থ আছে, পালস ৭৪।", retrieved?.dailyCheckInLogs)
        assertTrue(retrieved?.hasMedicationNeeds == true)
        assertTrue(retrieved?.hasAllergies == true)
        assertTrue(retrieved?.isDailyCheckedIn == true)
        assertEquals("B+", retrieved?.bloodGroup)
    }

    @Test
    fun testFilterByMedicationNeedsAndAllergies() = runBlocking {
        val p1 = ParticipantHealthStatusEntity(
            id = 1L,
            participantId = 101L,
            campCode = "LSC-2026-101",
            participantName = "তানভীর",
            medicationNeeds = "Paracetamol 500mg PRN",
            allergies = "None",
            dailyCheckInLogs = "Day 1: OK",
            hasMedicationNeeds = true,
            hasAllergies = false
        )
        val p2 = ParticipantHealthStatusEntity(
            id = 2L,
            participantId = 102L,
            campCode = "LSC-2026-102",
            participantName = "খাদিজা",
            medicationNeeds = "",
            allergies = "Peanuts Allergy",
            dailyCheckInLogs = "Day 1: OK",
            hasMedicationNeeds = false,
            hasAllergies = true
        )
        val p3 = ParticipantHealthStatusEntity(
            id = 3L,
            participantId = 103L,
            campCode = "LSC-2026-103",
            participantName = "আব্দুল্লাহ",
            medicationNeeds = "",
            allergies = "",
            dailyCheckInLogs = "Day 1: OK",
            hasMedicationNeeds = false,
            hasAllergies = false
        )

        healthDao.insertHealthStatuses(listOf(p1, p2, p3))

        val withMeds = healthDao.getParticipantsWithMedicationNeeds().first()
        assertEquals(1, withMeds.size)
        assertEquals("তানভীর", withMeds[0].participantName)

        val withAllergies = healthDao.getParticipantsWithAllergies().first()
        assertEquals(1, withAllergies.size)
        assertEquals("খাদিজা", withAllergies[0].participantName)
    }

    @Test
    fun testDailyCheckInLogAppendsCorrectly() = runBlocking {
        val p = ParticipantHealthStatusEntity(
            id = 1L,
            participantId = 201L,
            campCode = "LSC-2026-201",
            participantName = "রাফি",
            dailyCheckInLogs = "দিন ১: স্বাভাবিক চেক-ইন সম্পন্ন।"
        )
        healthDao.insertHealthStatus(p)

        healthDao.appendDailyCheckInLog(
            participantId = 201L,
            logEntry = "দিন ২: হালকা ঠান্ডা লেগেছে, মেডিকেল টিম বিশ্রাম দিয়েছে।",
            isCheckedIn = true,
            checkInDate = "2026-10-14",
            checkInTime = System.currentTimeMillis()
        )

        val updated = healthDao.getHealthStatusByParticipantId(201L)
        assertNotNull(updated)
        assertTrue(updated?.dailyCheckInLogs?.contains("দিন ১: স্বাভাবিক চেক-ইন সম্পন্ন।") == true)
        assertTrue(updated?.dailyCheckInLogs?.contains("দিন ২: হালকা ঠান্ডা লেগেছে") == true)
        assertTrue(updated?.isDailyCheckedIn == true)
        assertEquals("2026-10-14", updated?.lastCheckInDate)
    }

    @Test
    fun testUpdateAndDeleteHealthStatus() = runBlocking {
        val p = ParticipantHealthStatusEntity(
            id = 1L,
            participantId = 301L,
            campCode = "LSC-2026-301",
            participantName = "সাইফুল",
            medicationNeeds = "ভিটামিন সি",
            allergies = "নেই"
        )
        healthDao.insertHealthStatus(p)

        healthDao.updateMedicationNeeds(
            participantId = 301L,
            medicationNeeds = "অ্যান্টিহিস্টামিন রাত্রে",
            hasMedicationNeeds = true
        )

        val afterUpdate = healthDao.getHealthStatusByParticipantId(301L)
        assertEquals("অ্যান্টিহিস্টামিন রাত্রে", afterUpdate?.medicationNeeds)

        healthDao.deleteHealthStatusByParticipantId(301L)
        val afterDelete = healthDao.getHealthStatusByParticipantId(301L)
        assertNull(afterDelete)
    }
}
