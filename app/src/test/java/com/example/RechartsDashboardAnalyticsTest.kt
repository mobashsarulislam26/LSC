package com.example

import com.example.data.model.AttendanceEventEntity
import com.example.data.model.CampGroupEntity
import com.example.data.model.CampLanguage
import com.example.data.model.ParticipantHealthStatusEntity
import com.example.ui.screens.dashboard.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for Recharts Dashboard Analytics, Attendance Trends & Health Distribution
 */
class RechartsDashboardAnalyticsTest {

    @Test
    fun testDeriveAttendanceTrendPoints_withEvents() {
        val events = listOf(
            AttendanceEventEntity(
                id = 1,
                titleBn = "ফজর জামাত",
                titleEn = "Fajr Prayer",
                dayNo = 1,
                timeLabel = "05:00 AM",
                targetCount = 603,
                presentCount = 590,
                lateCount = 10,
                absentCount = 3,
                isClosed = true
            ),
            AttendanceEventEntity(
                id = 2,
                titleBn = "স্টেশন ১",
                titleEn = "Station 1",
                dayNo = 1,
                timeLabel = "08:30 AM",
                targetCount = 603,
                presentCount = 598,
                lateCount = 4,
                absentCount = 1,
                isClosed = false
            )
        )

        val trends = deriveAttendanceTrendPoints(events, 603)
        assertEquals(2, trends.size)
        assertEquals("ফজর জামাত", trends[0].sessionLabelBn)
        assertEquals("Fajr Prayer", trends[0].sessionLabelEn)
        assertEquals(590, trends[0].present)
        assertEquals(10, trends[0].late)
        assertEquals(3, trends[0].absent)
        assertTrue(trends[0].rate >= 90f)

        assertEquals("Station 1", trends[1].sessionLabelEn)
        assertEquals(598, trends[1].present)
    }

    @Test
    fun testDeriveAttendanceTrendPoints_emptyFallback() {
        val trends = deriveAttendanceTrendPoints(emptyList(), 603)
        assertTrue(trends.isNotEmpty())
        assertTrue(trends.size >= 5)
        trends.forEach { pt ->
            assertTrue(pt.present > 500)
            assertTrue(pt.target == 603)
            assertTrue(pt.rate in 90f..100f)
        }
    }

    @Test
    fun testDeriveHealthStatusSlices_distributionAndPercentages() {
        val statuses = listOf(
            ParticipantHealthStatusEntity(
                participantId = 1,
                campCode = "LSC-001",
                participantName = "Participant 1",
                healthCondition = "Normal"
            ),
            ParticipantHealthStatusEntity(
                participantId = 2,
                campCode = "LSC-002",
                participantName = "Participant 2",
                healthCondition = "Normal"
            ),
            ParticipantHealthStatusEntity(
                participantId = 3,
                campCode = "LSC-003",
                participantName = "Participant 3",
                healthCondition = "Needs Rest"
            ),
            ParticipantHealthStatusEntity(
                participantId = 4,
                campCode = "LSC-004",
                participantName = "Participant 4",
                healthCondition = "Mild Symptoms"
            ),
            ParticipantHealthStatusEntity(
                participantId = 5,
                campCode = "LSC-005",
                participantName = "Participant 5",
                healthCondition = "Requires Attention"
            )
        )

        val slices = deriveHealthStatusSlices(statuses, 5)
        assertEquals(4, slices.size)

        val normalSlice = slices.find { it.categoryEn.contains("Normal") }
        assertNotNull(normalSlice)
        assertEquals(2, normalSlice!!.count)
        assertEquals(40.0f, normalSlice.percentage, 0.1f)

        val restSlice = slices.find { it.categoryEn.contains("Rest") }
        assertNotNull(restSlice)
        assertEquals(1, restSlice!!.count)
        assertEquals(20.0f, restSlice.percentage, 0.1f)

        val mildSlice = slices.find { it.categoryEn.contains("Mild") }
        assertNotNull(mildSlice)
        assertEquals(1, mildSlice!!.count)

        val careSlice = slices.find { it.categoryEn.contains("Attention") }
        assertNotNull(careSlice)
        assertEquals(1, careSlice!!.count)

        val totalPct = slices.sumOf { it.percentage.toDouble() }
        assertEquals(100.0, totalPct, 0.5)
    }

    @Test
    fun testDeriveAllergyDietSlices() {
        val statuses = listOf(
            ParticipantHealthStatusEntity(
                participantId = 1,
                campCode = "LSC-001",
                participantName = "P1",
                hasAllergies = true,
                allergies = "Dust allergy"
            ),
            ParticipantHealthStatusEntity(
                participantId = 2,
                campCode = "LSC-002",
                participantName = "P2",
                hasMedicationNeeds = true,
                medicationNeeds = "Asthma Inhaler"
            ),
            ParticipantHealthStatusEntity(
                participantId = 3,
                campCode = "LSC-003",
                participantName = "P3"
            )
        )

        val slices = deriveAllergyDietSlices(statuses, 3)
        assertTrue(slices.isNotEmpty())
        assertTrue(slices.any { it.categoryEn.contains("Allergies") })
        assertTrue(slices.any { it.categoryEn.contains("Medication") })
    }

    @Test
    fun testDeriveGroupAttendanceData() {
        val sampleGroups = listOf(
            CampGroupEntity(
                id = "bukhari",
                nameBn = "বুখারী",
                nameEn = "Bukhari",
                leaderName = "Leader 1",
                leaderPhone = "01700-111111",
                colorHex = 0xFF1D4ED8,
                memberCount = 100,
                totalPoints = 850
            ),
            CampGroupEntity(
                id = "sumaiya",
                nameBn = "সুমাইয়া",
                nameEn = "Sumaiya",
                leaderName = "Leader 2",
                leaderPhone = "01700-222222",
                colorHex = 0xFF059669,
                memberCount = 100,
                totalPoints = 820
            )
        )

        val groupData = deriveGroupAttendanceData(sampleGroups, 200)
        assertEquals(2, groupData.size)
        assertEquals("বুখারী", groupData[0].groupNameBn)
        assertEquals("Bukhari", groupData[0].groupNameEn)
        assertTrue(groupData[0].presentRate >= 90f)
    }

    @Test
    fun testRechartsHtmlGenerators_validMarkup() {
        val barHtml = generateRechartsBarHtml(
            """{"session":"Session 1","time":"08:30","present":590,"late":10,"absent":3,"target":603,"rate":97.8}""",
            CampLanguage.BANGLA
        )
        assertTrue(barHtml.contains("<!DOCTYPE html>"))
        assertTrue(barHtml.contains("svgChart"))
        assertTrue(barHtml.contains("bar-present"))
        assertTrue(barHtml.contains("tooltip"))

        val pieHtml = generateRechartsPieHtml(
            """{"name":"Normal","value":490,"percentage":81.2,"color":"#10B981"}""",
            603,
            CampLanguage.BANGLA
        )
        assertTrue(pieHtml.contains("pieSvg"))
        assertTrue(pieHtml.contains("donut-slice"))
        assertTrue(pieHtml.contains("center-val"))

        val groupHtml = generateRechartsGroupBarHtml(
            """{"group":"Bukhari","rate":99.1,"present":100,"total":101,"color":"#1D4ED8"}""",
            CampLanguage.ENGLISH
        )
        assertTrue(groupHtml.contains("group-row"))
        assertTrue(groupHtml.contains("bar-track"))
    }
}
