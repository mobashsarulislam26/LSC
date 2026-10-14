package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CampDatabase
import com.example.data.local.ParticipantDao
import com.example.data.model.ParticipantEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ParticipantRoomDatabaseTest {

    private lateinit var db: CampDatabase
    private lateinit var dao: ParticipantDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CampDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.participantDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndGetParticipant() = runBlocking {
        val participant = ParticipantEntity(
            id = 1L,
            campCode = "LSC-2026-999",
            fileNo = "F-9999",
            nameBn = "আব্দুর রহমান",
            nameEn = "Abdur Rahman",
            districtBn = "সিলেট",
            districtEn = "Sylhet",
            gender = "Male",
            age = 18,
            groupId = "bukhari",
            groupNameBn = "ইমাম বুখারী কাফেলা",
            groupNameEn = "Imam Bukhari Group",
            subgroup = "দল-১",
            roomNo = "২০১",
            bedNo = "০১",
            mealPref = "নিয়মিত",
            guardianName = "মোঃ কফিল",
            guardianPhone = "01700000000"
        )

        dao.insertParticipant(participant)

        val retrieved = dao.getParticipantByCampCode("LSC-2026-999")
        assertNotNull(retrieved)
        assertEquals("আব্দুর রহমান", retrieved?.nameBn)

        val list = dao.getAllParticipants().first()
        assertEquals(1, list.size)
    }

    @Test
    fun testUpdateAndDeleteParticipant() = runBlocking {
        val participant = ParticipantEntity(
            id = 2L,
            campCode = "LSC-2026-123",
            fileNo = "F-1234",
            nameBn = "সাকিব",
            nameEn = "Sakib",
            districtBn = "ঢাকা",
            districtEn = "Dhaka",
            gender = "Male",
            age = 17,
            groupId = "sumaiya",
            groupNameBn = "সুমাইয়া কাফেলা",
            groupNameEn = "Sumaiya Group",
            subgroup = "দল-২",
            roomNo = "১০২",
            bedNo = "০২",
            mealPref = "নিয়মিত",
            guardianName = "করিম",
            guardianPhone = "01800000000"
        )

        dao.insertParticipant(participant)
        val updated = participant.copy(nameBn = "সাকিব আল হাসান")
        dao.updateParticipant(updated)

        val retrieved = dao.getParticipantById(2L)
        assertEquals("সাকিব আল হাসান", retrieved?.nameBn)

        dao.deleteParticipant(updated)
        val afterDelete = dao.getParticipantById(2L)
        assertNull(afterDelete)
    }
}
