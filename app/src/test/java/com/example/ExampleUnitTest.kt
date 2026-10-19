package com.example

import com.example.data.sample.SeedData
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCampParticipantsSeedCount() {
        val participants = SeedData.generateParticipants()
        assertEquals(603, participants.size)
        // Verify unique Camp IDs
        val uniqueCodes = participants.map { it.campCode }.toSet()
        assertEquals(603, uniqueCodes.size)
    }

    @Test
    fun testCampGroupsCountAndNames() {
        assertEquals(6, SeedData.groups.size)
        val groupIds = SeedData.groups.map { it.id }.toSet()
        assertTrue(groupIds.contains("bukhari"))
        assertTrue(groupIds.contains("muslim"))
        assertTrue(groupIds.contains("abu_dawood"))
        assertTrue(groupIds.contains("tirmidhi"))
        assertTrue(groupIds.contains("salahuddin"))
        assertTrue(groupIds.contains("sumaiya"))
    }

    @Test
    fun testStationCount() {
        assertEquals(6, SeedData.stations.size)
    }

    @Test
    fun testInventoryItemsAndAwardStock() {
        val items = SeedData.inventoryItems
        // 28 item types
        assertEquals(28, items.size)
        // Sum of opening stock must be exactly 799 units as specified in PRD
        val totalOpening = items.sumOf { it.openingQty }
        assertEquals(799, totalOpening)
    }

    @Test
    fun testJudgesSeedCount() {
        val judges = SeedData.judges
        assertEquals(6, judges.size)
        assertTrue(judges.any { it.nameBn.contains("আব্দুল গাফফার") })
        assertTrue(judges.any { it.nameBn.contains("মাহমুদুল হাসান") })
    }
}
