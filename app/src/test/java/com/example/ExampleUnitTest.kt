package com.example

import com.example.data.sample.SeedData
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCampParticipantsSeedCount() {
        val participants = SeedData.generateParticipants()
        assertEquals(609, participants.size)
        // Verify unique Camp IDs across all 609 real orphan students
        val uniqueCodes = participants.map { it.campCode }.toSet()
        assertEquals(609, uniqueCodes.size)
    }

    @Test
    fun testCampGroupsCountAndNames() {
        assertEquals(6, SeedData.groups.size)
        val groupIds = SeedData.groups.map { it.id }.toSet()
        assertTrue(groupIds.contains("bukhari"))
        assertTrue(groupIds.contains("shafeyi"))
        assertTrue(groupIds.contains("salahuddin"))
        assertTrue(groupIds.contains("sumaiya"))
        assertTrue(groupIds.contains("umm_ayman"))
        assertTrue(groupIds.contains("umm_saad"))
        assertEquals(609, SeedData.groups.sumOf { it.memberCount })
    }

    @Test
    fun testStationCountAndCurriculum() {
        assertEquals(6, SeedData.stations.size)
        assertTrue(SeedData.stations.all { it.day2TopicBn.isNotBlank() && it.day4TopicBn.isNotBlank() && it.day5TopicBn.isNotBlank() })
    }

    @Test
    fun testInventoryItemsAndAwardStock() {
        val items = SeedData.inventoryItems
        val giftAndKitItems = items.filter { it.category != "AWARD" }
        val awardItems = items.filter { it.category == "AWARD" }
        // 30 official gift items + 835 competition awards
        assertEquals(30, giftAndKitItems.size)
        assertEquals(835, awardItems.sumOf { it.openingQty })
        assertEquals(21, SeedData.competitions.size)
        assertEquals(835, SeedData.competitions.sumOf { it.prizeCount })
    }

    @Test
    fun testJudgesAndStationManagerSeed() {
        val judges = SeedData.judges
        assertEquals(6, judges.size)
        assertTrue(judges.any { it.nameBn.contains("শাহাদাত হোসাইন") })
        assertTrue(judges.any { it.nameBn.contains("ওমর ফারুক") })
        assertTrue(SeedData.staffAccounts.any { it.role == "STATION_MANAGER" && it.email == "stationmanager@lsc.org" })
        assertEquals(71, SeedData.rooms.size)
    }
}
