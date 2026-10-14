package com.example

import com.example.data.model.CampGroupEntity
import com.example.data.model.ParticipantEntity
import com.example.data.model.ScheduleEventEntity
import com.example.data.model.StationEntity
import com.example.ui.screens.dashboard.findAssignedGroupForStation
import com.example.ui.screens.dashboard.resolveGroupAssignedStation
import com.example.ui.screens.dashboard.resolveParticipantAssignedStation
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DailyScheduleSectionTest {

    private lateinit var stations: List<StationEntity>
    private lateinit var groups: List<CampGroupEntity>

    private val bukhariParticipant = ParticipantEntity(
        id = 101L,
        campCode = "LSC-2026-101",
        fileNo = "F-1101",
        nameBn = "তাহমিদ জামান",
        nameEn = "Tahmid Zaman",
        districtBn = "ঢাকা",
        districtEn = "Dhaka",
        gender = "Male",
        age = 14,
        groupId = "bukhari",
        groupNameBn = "বুখারী",
        groupNameEn = "Bukhari",
        subgroup = "Bukhari-1",
        roomNo = "A-101",
        bedNo = "Bed-1",
        mealPref = "নিয়মিত",
        guardianName = "রফিক জামান",
        guardianPhone = "01711-123456"
    )

    private val sumaiyaParticipant = ParticipantEntity(
        id = 102L,
        campCode = "LSC-2026-102",
        fileNo = "F-1102",
        nameBn = "ফাতিমা আক্তার",
        nameEn = "Fatima Akter",
        districtBn = "চট্টগ্রাম",
        districtEn = "Chattogram",
        gender = "Female",
        age = 13,
        groupId = "sumaiya",
        groupNameBn = "সুমাইয়া",
        groupNameEn = "Sumaiya",
        subgroup = "Sumaiya-2",
        roomNo = "B-201",
        bedNo = "Bed-2",
        mealPref = "ঝাল কম",
        guardianName = "আব্দুল করিম",
        guardianPhone = "01819-654321"
    )

    private val salahuddinParticipant = ParticipantEntity(
        id = 103L,
        campCode = "LSC-2026-103",
        fileNo = "F-1103",
        nameBn = "রায়হান কবির",
        nameEn = "Rayhan Kabir",
        districtBn = "সিলেট",
        districtEn = "Sylhet",
        gender = "Male",
        age = 15,
        groupId = "salahuddin",
        groupNameBn = "সালাহউদ্দীন আইয়ুবী",
        groupNameEn = "Salahuddin Ayyubi",
        subgroup = "Salahuddin-3",
        roomNo = "A-105",
        bedNo = "Bed-3",
        mealPref = "নিয়মিত",
        guardianName = "কবীর হোসেন",
        guardianPhone = "01911-789012"
    )

    @Before
    fun setUp() {
        stations = listOf(
            StationEntity(
                id = 1,
                nameBn = "স্টেশন ১: প্রতিযোগিতা ও শিষ্টাচার",
                nameEn = "Station 1: Competitions & Etiquette",
                moderatorName = "উস্তাদ মাহমুদুল হাসান",
                location = "অডিটোরিয়াম উইং এ",
                objectiveBn = "সুন্নাহসম্মত আদব ও চারিত্রিক উৎকর্ষ",
                iconKey = "quran"
            ),
            StationEntity(
                id = 2,
                nameBn = "স্টেশন ২: লাইফ স্কিল ও ব্যবহারিক দক্ষতা",
                nameEn = "Station 2: Life Skills & Practical Wisdom",
                moderatorName = "ব্রাদার তানভীর হোসাইন",
                location = "স্কিল ওয়ার্কশপ শেড",
                objectiveBn = "বাস্তব জীবনের সিদ্ধান্ত ও দুর্যোগ প্রস্তুতি",
                iconKey = "science"
            ),
            StationEntity(
                id = 3,
                nameBn = "স্টেশন ৩: শিশুতোষ আলোচনা ও মানসিক বিকাশ",
                nameEn = "Station 3: Child Development & Psychology",
                moderatorName = "মুহতারামা খাদিজা আক্তার",
                location = "কাউন্সেলিং রুম ও লাইব্রেরি",
                objectiveBn = "বয়সোপযোগী মানসিক বিকাশ ও সহমর্মিতা",
                iconKey = "speech"
            ),
            StationEntity(
                id = 4,
                nameBn = "স্টেশন ৪: ফিকাহ ও সালাত প্রশিক্ষণ",
                nameEn = "Station 4: Fiqh & Prayer Mastery",
                moderatorName = "মাওলানা আব্দুল গাফফার",
                location = "মসজিদ কমপ্লেক্স হল",
                objectiveBn = "সহীহ পদ্ধতিতে অজু ও সালাত",
                iconKey = "fitness"
            ),
            StationEntity(
                id = 5,
                nameBn = "স্টেশন ৫: রিফ্লেক্টিভ সেশন ও আত্মশুদ্ধি",
                nameEn = "Station 5: Reflective Session & Tazkiyah",
                moderatorName = "শায়খ আবু তালহা",
                location = "গ্রাউন্ড প্যাভিলিয়ন",
                objectiveBn = "আত্মপর্যালোচনা ও আন্তরিক তওবা",
                iconKey = "arts"
            ),
            StationEntity(
                id = 6,
                nameBn = "স্টেশন ৬: মেডিকেল, ডেন্টাল ও আই ক্যাম্প",
                nameEn = "Station 6: Medical, Dental & Eye Clinic",
                moderatorName = "ডা. তারিকুল ইসলাম",
                location = "মেডিকেল সেন্টার",
                objectiveBn = "দাঁত, চোখ ও সাধারণ স্বাস্থ্য পরীক্ষা",
                iconKey = "medical"
            )
        )

        groups = listOf(
            CampGroupEntity(id = "bukhari", nameBn = "বুখারী", nameEn = "Bukhari", leaderName = "তানভীর", leaderPhone = "01711", colorHex = 0xFF1976D2, memberCount = 101),
            CampGroupEntity(id = "sumaiya", nameBn = "সুমাইয়া", nameEn = "Sumaiya", leaderName = "খাদিজা", leaderPhone = "01819", colorHex = 0xFFC2185B, memberCount = 100),
            CampGroupEntity(id = "shafeyi", nameBn = "শাফেয়ী", nameEn = "Shafeyi", leaderName = "আব্দুল গাফফার", leaderPhone = "01912", colorHex = 0xFF00796B, memberCount = 101),
            CampGroupEntity(id = "umm_ayman", nameBn = "উম্মে আয়মান", nameEn = "Umm Ayman", leaderName = "সাবিহা", leaderPhone = "01722", colorHex = 0xFF7B1FA2, memberCount = 100),
            CampGroupEntity(id = "salahuddin", nameBn = "সালাহউদ্দীন আইয়ুবী", nameEn = "Salahuddin Ayyubi", leaderName = "রায়হান", leaderPhone = "01611", colorHex = 0xFFE65100, memberCount = 101),
            CampGroupEntity(id = "umm_saad", nameBn = "উম্মে সা'দ", nameEn = "Umm Sa'ad", leaderName = "নাসরিন", leaderPhone = "01511", colorHex = 0xFF388E3C, memberCount = 100)
        )
    }

    @Test
    fun testResolveParticipantAssignedStation_currentLiveSession() {
        val currentStationEvent = ScheduleEventEntity(
            id = 4L,
            dayNo = 1,
            timeSlot = "08:30 - 10:00",
            titleBn = "স্টেশন রোটেশন সেশন - ১",
            titleEn = "Station Rotation Session 1",
            category = "Station",
            stationId = 1,
            isCurrent = true,
            rotationJson = "bukhari:1,sumaiya:2,shafeyi:3,umm_ayman:4,salahuddin:5,umm_saad:6"
        )

        // Bukhari participant should be assigned to Station 1
        val bukhariStn = resolveParticipantAssignedStation(currentStationEvent, bukhariParticipant, stations)
        assertNotNull(bukhariStn)
        assertEquals(1, bukhariStn?.id)
        assertEquals("স্টেশন ১: প্রতিযোগিতা ও শিষ্টাচার", bukhariStn?.nameBn)

        // Sumaiya participant should be assigned to Station 2
        val sumaiyaStn = resolveParticipantAssignedStation(currentStationEvent, sumaiyaParticipant, stations)
        assertNotNull(sumaiyaStn)
        assertEquals(2, sumaiyaStn?.id)
        assertEquals("Station 2: Life Skills & Practical Wisdom", sumaiyaStn?.nameEn)

        // Salahuddin participant should be assigned to Station 5
        val salahuddinStn = resolveParticipantAssignedStation(currentStationEvent, salahuddinParticipant, stations)
        assertNotNull(salahuddinStn)
        assertEquals(5, salahuddinStn?.id)
        assertEquals("গ্রাউন্ড প্যাভিলিয়ন", salahuddinStn?.location)
    }

    @Test
    fun testResolveParticipantAssignedStation_upcomingSessionRotation() {
        // Session 2 rotation
        val upcomingSession2 = ScheduleEventEntity(
            id = 5L,
            dayNo = 1,
            timeSlot = "10:15 - 11:45",
            titleBn = "স্টেশন রোটেশন সেশন - ২",
            titleEn = "Station Rotation Session 2",
            category = "Station",
            stationId = 2,
            isCurrent = false,
            rotationJson = "bukhari:2,sumaiya:3,shafeyi:4,umm_ayman:5,salahuddin:6,umm_saad:1"
        )

        // In Session 2, Bukhari moves to Station 2
        val bukhariNextStn = resolveParticipantAssignedStation(upcomingSession2, bukhariParticipant, stations)
        assertNotNull(bukhariNextStn)
        assertEquals(2, bukhariNextStn?.id)
        assertEquals("স্কিল ওয়ার্কশপ শেড", bukhariNextStn?.location)

        // In Session 2, Sumaiya moves to Station 3
        val sumaiyaNextStn = resolveParticipantAssignedStation(upcomingSession2, sumaiyaParticipant, stations)
        assertNotNull(sumaiyaNextStn)
        assertEquals(3, sumaiyaNextStn?.id)

        // In Session 2, Salahuddin moves to Station 6 (Medical & Clinic)
        val salahuddinNextStn = resolveParticipantAssignedStation(upcomingSession2, salahuddinParticipant, stations)
        assertNotNull(salahuddinNextStn)
        assertEquals(6, salahuddinNextStn?.id)
        assertEquals("ডা. তারিকুল ইসলাম", salahuddinNextStn?.moderatorName)
    }

    @Test
    fun testResolveParticipantAssignedStation_fallbackToStationId() {
        val singleStationEvent = ScheduleEventEntity(
            id = 12L,
            dayNo = 2,
            timeSlot = "02:00 - 03:00",
            titleBn = "জরুরি ফার্স্ট এইড ড্রিল",
            titleEn = "Emergency First Aid Drill",
            category = "Station",
            stationId = 4,
            rotationJson = "" // No matrix, specific station
        )

        val resolved = resolveParticipantAssignedStation(singleStationEvent, bukhariParticipant, stations)
        assertNotNull(resolved)
        assertEquals(4, resolved?.id)
        assertEquals("মাওলানা আব্দুল গাফফার", resolved?.moderatorName)
    }

    @Test
    fun testResolveParticipantAssignedStation_nonStationEventReturnsNull() {
        val lunchEvent = ScheduleEventEntity(
            id = 7L,
            dayNo = 1,
            timeSlot = "01:30 - 02:45",
            titleBn = "যোহর নামাজ ও মধ্যাহ্নভোজ",
            titleEn = "Dhuhr Prayer & Lunch",
            category = "Meal",
            stationId = null,
            rotationJson = ""
        )

        val resolved = resolveParticipantAssignedStation(lunchEvent, bukhariParticipant, stations)
        assertNull(resolved)
    }

    @Test
    fun testResolveGroupAssignedStation_direct() {
        val event = ScheduleEventEntity(
            id = 8L,
            dayNo = 1,
            timeSlot = "03:15 - 04:30",
            titleBn = "স্টেশন রোটেশন সেশন - ৩",
            titleEn = "Station Rotation Session 3",
            category = "Station",
            rotationJson = "bukhari:3,sumaiya:4,shafeyi:5,umm_ayman:6,salahuddin:1,umm_saad:2"
        )

        val bukhariStn = resolveGroupAssignedStation(event, "bukhari", stations)
        assertEquals(3, bukhariStn?.id)

        val salahuddinStn = resolveGroupAssignedStation(event, "salahuddin", stations)
        assertEquals(1, salahuddinStn?.id)
    }

    @Test
    fun testFindAssignedGroupForStation_lookup() {
        val event = ScheduleEventEntity(
            id = 4L,
            dayNo = 1,
            timeSlot = "08:30 - 10:00",
            titleBn = "স্টেশন রোটেশন সেশন - ১",
            titleEn = "Station Rotation Session 1",
            category = "Station",
            rotationJson = "bukhari:1,sumaiya:2,shafeyi:3,umm_ayman:4,salahuddin:5,umm_saad:6"
        )

        val assignedToStn1 = findAssignedGroupForStation(event, 1, groups)
        assertNotNull(assignedToStn1)
        assertEquals("bukhari", assignedToStn1?.id)
        assertEquals("বুখারী", assignedToStn1?.nameBn)

        val assignedToStn5 = findAssignedGroupForStation(event, 5, groups)
        assertNotNull(assignedToStn5)
        assertEquals("salahuddin", assignedToStn5?.id)
    }
}
