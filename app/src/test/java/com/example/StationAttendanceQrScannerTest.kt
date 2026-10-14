package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CampDatabase
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.ParticipantEntity
import com.example.data.repository.CampRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.flow.first
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
class StationAttendanceQrScannerTest {

    private lateinit var db: CampDatabase
    private lateinit var repository: CampRepository

    private val testParticipant = ParticipantEntity(
        id = 501L,
        campCode = "LSC-2026-501",
        fileNo = "F-1501",
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
        roomNo = "Room 302",
        bedNo = "Bed-A",
        mealPref = "রেগুলার",
        guardianName = "রফিক জামান",
        guardianPhone = "01819-998877"
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CampDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = CampRepository(db.campDao(), db.participantHealthDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testGenerateParticipantQrCodeBitMatrix() {
        // Verify ZXing encodes participant camp code to valid QR bit matrix
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(testParticipant.campCode, BarcodeFormat.QR_CODE, 200, 200)

        assertNotNull(bitMatrix)
        assertTrue(bitMatrix.width > 0)
        assertTrue(bitMatrix.height > 0)
    }

    @Test
    fun testStationLeaderAttendanceLoggingViaQrScan() = runBlocking {
        // Insert participant
        repository.insertParticipant(testParticipant)

        // Station leader logs attendance at Station 1 (First Aid & Survival)
        val stationName = "স্টেশন ১: ফার্স্ট এইড ও সারভাইভাল"
        val success = repository.recordAttendance(
            eventId = 4L, // Station Rotation event ID
            participant = testParticipant,
            status = "PRESENT",
            method = "QR_SCAN",
            reason = "Station: $stationName"
        )

        assertTrue(success)

        // Verify record in Room database
        val record = db.campDao().getRecordForParticipant(4L, 501L)
        assertNotNull(record)
        assertEquals("LSC-2026-501", record?.campCode)
        assertEquals("PRESENT", record?.status)
        assertEquals("QR_SCAN", record?.method)
        assertEquals("Station: $stationName", record?.overrideReason)
    }

    @Test
    fun testStationAttendanceLateStatusUpdate() = runBlocking {
        repository.insertParticipant(testParticipant)

        // First mark late
        repository.recordAttendance(
            eventId = 4L,
            participant = testParticipant,
            status = "LATE",
            method = "QR_SCAN",
            reason = "Station: স্টেশন ২: ফায়ার সেফটি"
        )

        val record1 = db.campDao().getRecordForParticipant(4L, 501L)
        assertEquals("LATE", record1?.status)

        // Then update to PRESENT
        repository.recordAttendance(
            eventId = 4L,
            participant = testParticipant,
            status = "PRESENT",
            method = "QR_SCAN",
            reason = "Station: স্টেশন ২: ফায়ার সেফটি"
        )

        val record2 = db.campDao().getRecordForParticipant(4L, 501L)
        assertEquals("PRESENT", record2?.status)
    }
}
