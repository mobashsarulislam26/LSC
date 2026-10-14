package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CampDao
import com.example.data.local.CampDatabase
import com.example.data.local.ParticipantHealthDao
import com.example.data.model.CachedSyncItemEntity
import com.example.data.model.HealthRecordEntity
import com.example.data.model.ParticipantEntity
import com.example.data.repository.CampRepository
import com.example.ui.viewmodel.CampViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LowConnectivitySyncTest {

    private lateinit var db: CampDatabase
    private lateinit var campDao: CampDao
    private lateinit var healthDao: ParticipantHealthDao
    private lateinit var repository: CampRepository

    private val sampleParticipant = ParticipantEntity(
        id = 101L,
        campCode = "LSC-2026-101",
        fileNo = "F-1101",
        nameBn = "মো: সাকিবুল হাসান",
        nameEn = "Md. Shakibul Hasan",
        districtBn = "কুমিল্লা",
        districtEn = "Cumilla",
        gender = "Male",
        age = 15,
        groupId = "bukhari",
        groupNameBn = "বুখারী",
        groupNameEn = "Bukhari",
        subgroup = "Bukhari-1",
        roomNo = "Room 101",
        bedNo = "Bed-A",
        mealPref = "নরমাল",
        guardianName = "মো: আনোয়ার হোসেন",
        guardianPhone = "01812-345678"
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CampDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        campDao = db.campDao()
        healthDao = db.participantHealthDao()
        repository = CampRepository(campDao, healthDao)

        runBlocking {
            campDao.insertParticipant(sampleParticipant)
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testAttendanceScanCachedLocallyInLowConnectivityMode() = runBlocking {
        // Record attendance with isOfflineMode = true (Low Connectivity)
        val eventId = 2L
        val success = repository.recordAttendance(
            eventId = eventId,
            participant = sampleParticipant,
            status = "PRESENT",
            method = "QR_SCAN",
            reason = "Station 1 Scan",
            isOfflineMode = true
        )

        assertTrue("Attendance should be recorded successfully", success)

        // Verify AttendanceRecord has isSynced = false
        val attendance = campDao.getRecordForParticipant(eventId, sampleParticipant.id)
        assertNotNull("Attendance record should exist in local Room database", attendance)
        assertEquals("PRESENT", attendance?.status)
        assertFalse("Attendance record should be marked as unsynced in low connectivity mode", attendance!!.isSynced)

        // Verify item is added to the cached sync queue
        val pendingCount = campDao.getPendingCachedSyncCount()
        assertEquals("There should be 1 pending cached item", 1, pendingCount)

        val cachedItems = campDao.getPendingCachedSyncItems()
        assertEquals(1, cachedItems.size)
        val item = cachedItems.first()
        assertEquals(CachedSyncItemEntity.TYPE_ATTENDANCE, item.itemType)
        assertEquals(sampleParticipant.id, item.participantId)
        assertEquals(sampleParticipant.campCode, item.campCode)
        assertEquals(CachedSyncItemEntity.STATUS_PENDING, item.status)
        assertTrue(item.summary.contains("হাজিরা স্ক্যান"))
    }

    @Test
    fun testDailyHealthCheckInCachedLocallyInLowConnectivityMode() = runBlocking {
        // Record daily health check-in with isOfflineMode = true
        val checkInStatus = "Needs rest"
        val notes = "মাথাব্যথা ও সামান্য ক্লান্তি"
        val result = repository.recordDailyHealthCheckIn(
            participant = sampleParticipant,
            status = checkInStatus,
            notes = notes,
            checkInDate = "2026-10-13",
            isOfflineMode = true
        )

        assertNotNull(result)
        assertTrue(result.isDailyCheckedIn)
        assertEquals("Needs Rest", result.healthCondition)

        // Verify item is added to the cached sync queue
        val cachedItems = campDao.getPendingCachedSyncItems()
        val healthLogItem = cachedItems.find { it.itemType == CachedSyncItemEntity.TYPE_HEALTH_LOG }
        assertNotNull("Health check-in should be cached in sync queue", healthLogItem)
        assertEquals(sampleParticipant.id, healthLogItem!!.participantId)
        assertEquals(CachedSyncItemEntity.STATUS_PENDING, healthLogItem.status)
        assertTrue(healthLogItem.summary.contains("দৈনিক স্বাস্থ্য চেক-ইন"))
    }

    @Test
    fun testHealthCheckupCachedLocallyInLowConnectivityMode() = runBlocking {
        // Submit clinical checkup in low connectivity mode
        val record = HealthRecordEntity(
            participantId = sampleParticipant.id,
            campCode = sampleParticipant.campCode,
            participantName = sampleParticipant.nameBn,
            groupName = sampleParticipant.groupNameBn,
            checkupType = "GENERAL",
            doctorName = "ডা. মাহফুজুর রহমান",
            vitals = "BP: 110/70, Temp: 98.6°F",
            diagnosis = "হালকা পানিশূন্যতা",
            adviceAndMeds = "ওরস্যালাইন ও বিশ্রাম",
            isEmergencyFlag = false
        )

        repository.submitHealthRecord(record, isOfflineMode = true)

        val cachedItems = campDao.getPendingCachedSyncItems()
        val checkupItem = cachedItems.find { it.itemType == CachedSyncItemEntity.TYPE_HEALTH_CHECKUP }
        assertNotNull("Health checkup should be cached in sync queue", checkupItem)
        assertEquals(sampleParticipant.id, checkupItem!!.participantId)
        assertEquals(CachedSyncItemEntity.STATUS_PENDING, checkupItem.status)
        assertTrue(checkupItem.summary.contains("স্বাস্থ্য স্ক্রিনিং"))
    }

    @Test
    fun testAutomaticSyncWhenNetworkIsDetected() = runBlocking {
        // Cache 1 attendance and 1 health log in low connectivity mode
        repository.recordAttendance(
            eventId = 3L,
            participant = sampleParticipant,
            status = "PRESENT",
            method = "QR_SCAN",
            isOfflineMode = true
        )
        repository.recordDailyHealthCheckIn(
            participant = sampleParticipant,
            status = "Feeling well",
            notes = "সম্পূর্ণ সুস্থ",
            isOfflineMode = true
        )

        // Verify we have 2 pending cached items
        val initialPending = campDao.getPendingCachedSyncCount()
        assertEquals(2, initialPending)

        // Trigger Sync (simulating network connection re-established)
        val syncResult = repository.syncPendingCachedItems()

        assertEquals("Total synced count should match items queued", 2, syncResult.totalSynced)
        assertEquals("Attendance synced count", 1, syncResult.attendanceSynced)
        assertEquals("Health logs synced count", 1, syncResult.healthLogsSynced)

        // Verify pending count is now 0
        val remainingPending = campDao.getPendingCachedSyncCount()
        assertEquals("Pending sync count should be 0 after successful sync", 0, remainingPending)

        // Verify cached items status is now SYNCED
        val allCached = campDao.getAllCachedSyncItems().first()
        assertEquals(2, allCached.size)
        assertTrue("All cached items should be marked SYNCED", allCached.all { it.status == CachedSyncItemEntity.STATUS_SYNCED })
        assertTrue("All cached items should have syncedAt timestamp set", allCached.all { it.syncedAt != null })

        // Verify AttendanceRecord is now marked isSynced = true
        val unsyncedAttendance = campDao.getUnsyncedAttendanceRecords()
        assertTrue("No attendance records should remain unsynced", unsyncedAttendance.isEmpty())
    }

    @Test
    fun testClearSyncedHistory() = runBlocking {
        repository.recordAttendance(
            eventId = 4L,
            participant = sampleParticipant,
            status = "PRESENT",
            method = "QR_SCAN",
            isOfflineMode = true
        )

        repository.syncPendingCachedItems()

        val countBeforeClear = campDao.getAllCachedSyncItems().first().size
        assertEquals(1, countBeforeClear)

        val deletedCount = repository.clearSyncedCacheHistory()
        assertEquals(1, deletedCount)

        val countAfterClear = campDao.getAllCachedSyncItems().first().size
        assertEquals("Synced history should be cleared", 0, countAfterClear)
    }

    @Test
    fun testViewModelLowConnectivityAndSimulation() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = CampViewModel(app)

        // Enable Low Connectivity mode
        viewModel.setLowConnectivityMode(true)
        assertTrue(viewModel.isLowConnectivityMode.value)
        assertTrue(viewModel.isOffline.value)

        // Simulate network disconnect
        viewModel.simulateNetworkAvailability(false)
        assertFalse(viewModel.isNetworkAvailable.value)
        assertTrue(viewModel.isOffline.value)

        // Simulate network restore
        viewModel.simulateNetworkAvailability(true)
        assertTrue(viewModel.isNetworkAvailable.value)
        // Still offline because low connectivity mode was forced on
        assertTrue(viewModel.isOffline.value)

        // Turn off low connectivity mode
        viewModel.setLowConnectivityMode(false)
        assertFalse(viewModel.isLowConnectivityMode.value)
        // Now effective offline is false because network is available
        assertFalse(viewModel.isOffline.value)

        // Reset simulation
        viewModel.resetNetworkSimulation()
    }
}
