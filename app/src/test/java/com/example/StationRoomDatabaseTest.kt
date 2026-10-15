package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CampDatabase
import com.example.data.local.StationDao
import com.example.data.model.StationEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class StationRoomDatabaseTest {

    private lateinit var db: CampDatabase
    private lateinit var stationDao: StationDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CampDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        stationDao = db.stationDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndGetStation() = runBlocking {
        val station = StationEntity(
            id = 1,
            nameBn = "হিফজুল হাদিস",
            nameEn = "Hadith Memorization",
            moderatorName = "মাওলানা আব্দুল হাই",
            location = "রুম ১০১ (দ্বিতীয় তলা)",
            objectiveBn = "৪০টি নির্বাচিত হাদিস মুখস্থকরণ ও ব্যাখ্যা",
            iconKey = "auto_stories",
            materialsNeeded = "হাদিস বুকলেট, নোটপ্যাড"
        )

        stationDao.insertStation(station)

        val retrieved = stationDao.getStationByIdImmediate(1)
        assertNotNull(retrieved)
        assertEquals("হিফজুল হাদিস", retrieved?.nameBn)
        assertEquals("Hadith Memorization", retrieved?.nameEn)
        assertEquals("মাওলানা আব্দুল হাই", retrieved?.moderatorName)

        val list = stationDao.getAllStations().first()
        assertEquals(1, list.size)
        assertEquals(1, stationDao.getStationCount())
    }

    @Test
    fun testBatchInsertAndSearchStations() = runBlocking {
        val stations = listOf(
            StationEntity(
                id = 1,
                nameBn = "হিফজুল হাদিস",
                nameEn = "Hadith",
                moderatorName = "আব্দুল হাই",
                location = "রুম ১০১",
                objectiveBn = "হাদিস মুখস্থ",
                iconKey = "menu_book"
            ),
            StationEntity(
                id = 2,
                nameBn = "ক্বিরাত ও তাজবীদ",
                nameEn = "Quran & Tajweed",
                moderatorName = "ক্বারী মাহফুজ",
                location = "মসজিদ হল",
                objectiveBn = "সহিহ তিলাওয়াত",
                iconKey = "record_voice_over"
            ),
            StationEntity(
                id = 3,
                nameBn = "রোবোটিক্স ও স্টেম",
                nameEn = "Robotics & STEM",
                moderatorName = "ইঞ্জি. তারিক",
                location = "আইটি ল্যাব",
                objectiveBn = "প্রযুক্তি চর্চা",
                iconKey = "biotech"
            )
        )

        stationDao.insertStations(stations)

        assertEquals(3, stationDao.getStationCount())

        val searchResults = stationDao.searchStations("তাজবীদ").first()
        assertEquals(1, searchResults.size)
        assertEquals(2, searchResults[0].id)
        assertEquals("ক্বিরাত ও তাজবীদ", searchResults[0].nameBn)
    }

    @Test
    fun testUpdateAndDeleteStation() = runBlocking {
        val station = StationEntity(
            id = 4,
            nameBn = "সারভাইভাল কিট ও স্কাউটিং",
            nameEn = "Survival Skills",
            moderatorName = "তানভীর আহমেদ",
            location = "ক্যাম্প গ্রাউন্ড",
            objectiveBn = "ফার্স্ট এইড ও দুর্যোগ ব্যবস্থাপনা",
            iconKey = "medical_services"
        )

        stationDao.insertStation(station)
        assertEquals(1, stationDao.getStationCount())

        val updated = station.copy(moderatorName = "ওস্তাদ তানভীর আহমেদ")
        stationDao.updateStation(updated)

        val retrieved = stationDao.getStationByIdImmediate(4)
        assertEquals("ওস্তাদ তানভীর আহমেদ", retrieved?.moderatorName)

        stationDao.deleteStationById(4)
        assertNull(stationDao.getStationByIdImmediate(4))
        assertEquals(0, stationDao.getStationCount())
    }
}
