package com.example

import com.example.data.model.MealDistributionEntity
import com.example.data.model.ParticipantEntity
import com.example.data.model.RoomEntity
import com.example.ui.screens.dashboard.calculateRoomOccupancy
import com.example.ui.screens.dashboard.deriveMealHeadcountMetrics
import org.junit.Assert.*
import org.junit.Test

class MealHeadcountAndRoomMatrixTest {

    private val sampleParticipants = listOf(
        ParticipantEntity(
            id = 1L,
            campCode = "LSC-001",
            fileNo = "F-001",
            nameBn = "আব্দুল্লাহ আল মামুন",
            nameEn = "Abdullah Al Mamun",
            districtBn = "ঢাকা",
            districtEn = "Dhaka",
            gender = "Male",
            age = 14,
            groupId = "bukhari",
            groupNameBn = "বুখারী",
            groupNameEn = "Bukhari",
            subgroup = "Bukhari-1",
            roomNo = "১০১",
            bedNo = "Bed-01",
            mealPref = "নিয়মিত",
            guardianName = "মো: রফিক",
            guardianPhone = "01711-111111"
        ),
        ParticipantEntity(
            id = 2L,
            campCode = "LSC-002",
            fileNo = "F-002",
            nameBn = "আহমেদ হাসিব",
            nameEn = "Ahmed Hasib",
            districtBn = "চট্টগ্রাম",
            districtEn = "Chittagong",
            gender = "Male",
            age = 13,
            groupId = "bukhari",
            groupNameBn = "বুখারী",
            groupNameEn = "Bukhari",
            subgroup = "Bukhari-1",
            roomNo = "১০১",
            bedNo = "Bed-02",
            mealPref = "চিংড়ি অ্যালার্জি (No Prawns)",
            guardianName = "মো: হাসিব",
            guardianPhone = "01711-222222"
        ),
        ParticipantEntity(
            id = 3L,
            campCode = "LSC-003",
            fileNo = "F-003",
            nameBn = "জুনায়েদ হোসাইন",
            nameEn = "Junaid Hossain",
            districtBn = "সিলেট",
            districtEn = "Sylhet",
            gender = "Male",
            age = 14,
            groupId = "bukhari",
            groupNameBn = "বুখারী",
            groupNameEn = "Bukhari",
            subgroup = "Bukhari-2",
            roomNo = "১০২",
            bedNo = "Bed-01",
            mealPref = "মৃদু ঝাল (Mild Spicy)",
            guardianName = "মো: হোসাইন",
            guardianPhone = "01711-333333"
        ),
        ParticipantEntity(
            id = 4L,
            campCode = "LSC-004",
            fileNo = "F-004",
            nameBn = "মরিয়ম আক্তার",
            nameEn = "Mariam Akhter",
            districtBn = "রাজশাহী",
            districtEn = "Rajshahi",
            gender = "Female",
            age = 13,
            groupId = "sumaiya",
            groupNameBn = "সুমাইয়া",
            groupNameEn = "Sumaiya",
            subgroup = "Sumaiya-1",
            roomNo = "১০৩",
            bedNo = "Bed-01",
            mealPref = "নিয়মিত",
            guardianName = "মোছা: আক্তার",
            guardianPhone = "01819-444444"
        )
    )

    private val sampleRooms = listOf(
        RoomEntity(
            id = 1,
            buildingName = "পদ্মা কটেজ",
            roomNo = "১০১",
            floor = "১ম তলা",
            capacity = 2,
            assignedCount = 2,
            assignedGroupId = "bukhari",
            inChargeName = "মুহাম্মদ জুবায়ের",
            inChargePhone = "01711-101001"
        ),
        RoomEntity(
            id = 2,
            buildingName = "পদ্মা কটেজ",
            roomNo = "১০২",
            floor = "১ম তলা",
            capacity = 3,
            assignedCount = 1,
            assignedGroupId = "bukhari",
            inChargeName = "সাঈদ আহমেদ",
            inChargePhone = "01711-101002"
        ),
        RoomEntity(
            id = 3,
            buildingName = "মেঘনা কটেজ",
            roomNo = "১০৩",
            floor = "১ম তলা",
            capacity = 2,
            assignedCount = 1,
            assignedGroupId = "sumaiya",
            inChargeName = "ফারহানা ইয়াসমিন",
            inChargePhone = "01819-101003"
        )
    )

    @Test
    fun testDeriveMealHeadcountMetricsCalculations() {
        // 2 participants claimed lunch out of 4 total
        val mealClaims = listOf(
            MealDistributionEntity(id = 1, dayNo = 1, mealType = "LUNCH", participantId = 1L, campCode = "LSC-001", participantName = "আব্দুল্লাহ আল মামুন"),
            MealDistributionEntity(id = 2, dayNo = 1, mealType = "LUNCH", participantId = 2L, campCode = "LSC-002", participantName = "আহমেদ হাসিব")
        )

        val metrics = deriveMealHeadcountMetrics(
            mealType = "LUNCH",
            dayNo = 1,
            participants = sampleParticipants,
            mealDistributions = mealClaims
        )

        assertEquals("Total target should be 4", 4, metrics.targetCount)
        assertEquals("Served count should be 2", 2, metrics.servedCount)
        assertEquals("Remaining count should be 2", 2, metrics.remainingCount)
        assertEquals("Serving percentage should be 50%", 50.0f, metrics.servingPercentage, 0.01f)

        // Special diet: Participant 2 (prawn allergy) and Participant 3 (mild spicy) = 2 special diets
        assertEquals("Special diet target count", 2, metrics.specialDietTargetCount)
        // Participant 2 claimed meal, participant 3 has not
        assertEquals("Special diet served count", 1, metrics.specialDietServedCount)

        // Remaining count is 2 (<= 80), so prep caution alert is active
        assertEquals("PREP_CAUTION", metrics.wastageRiskLevel)
        assertTrue("Bangla advisory should mention remaining persons", metrics.cateringAdvisoryBn.contains("২ জন বাকি"))
    }

    @Test
    fun testDeriveMealHeadcountOptimalWhenNearFull() {
        // All 4 claimed
        val allClaimed = listOf(
            MealDistributionEntity(id = 1, dayNo = 1, mealType = "DINNER", participantId = 1L, campCode = "LSC-001", participantName = "P1"),
            MealDistributionEntity(id = 2, dayNo = 1, mealType = "DINNER", participantId = 2L, campCode = "LSC-002", participantName = "P2"),
            MealDistributionEntity(id = 3, dayNo = 1, mealType = "DINNER", participantId = 3L, campCode = "LSC-003", participantName = "P3"),
            MealDistributionEntity(id = 4, dayNo = 1, mealType = "DINNER", participantId = 4L, campCode = "LSC-004", participantName = "P4")
        )

        val metrics = deriveMealHeadcountMetrics(
            mealType = "DINNER",
            dayNo = 1,
            participants = sampleParticipants,
            mealDistributions = allClaimed
        )

        assertEquals(4, metrics.servedCount)
        assertEquals(0, metrics.remainingCount)
        assertEquals(100.0f, metrics.servingPercentage, 0.01f)
        assertEquals("OPTIMAL", metrics.wastageRiskLevel)
        assertTrue(metrics.cateringAdvisoryBn.contains("অপচয় পুরোপুরি রোধ"))
    }

    @Test
    fun testCalculateRoomOccupancySummary() {
        val summary = calculateRoomOccupancy(sampleRooms, sampleParticipants)

        // Total rooms: 3
        assertEquals(3, summary.totalRooms)

        // Total capacity: 2 + 3 + 2 = 7 beds
        assertEquals(7, summary.totalBedsCapacity)

        // Room 101 has 2 participants (Mamun, Hasib) -> 2 occupied (Capacity 2 -> FULL)
        // Room 102 has 1 participant (Junaid) -> 1 occupied (Capacity 3 -> 2 AVAILABLE)
        // Room 103 has 1 participant (Mariam) -> 1 occupied (Capacity 2 -> 1 AVAILABLE)
        // Total occupied = 2 + 1 + 1 = 4 beds
        assertEquals(4, summary.totalOccupiedBeds)

        // Total available = 7 - 4 = 3 beds
        assertEquals(3, summary.totalAvailableBeds)

        // Rate = 4 / 7 * 100 = 57.14%
        assertEquals(57.14f, summary.overallOccupancyRate, 0.5f)

        // 1 fully occupied room (Room 101), 2 partially available rooms (102, 103)
        assertEquals(1, summary.fullyOccupiedRoomsCount)
        assertEquals(2, summary.partiallyAvailableRoomsCount)
    }
}
