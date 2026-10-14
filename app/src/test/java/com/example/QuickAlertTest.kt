package com.example

import com.example.data.model.IncidentEntity
import com.example.data.model.ParticipantEntity
import com.example.ui.components.QUICK_ALERT_PRESETS
import org.junit.Assert.*
import org.junit.Test

class QuickAlertTest {

    private val sampleParticipant = ParticipantEntity(
        id = 29L,
        campCode = "LSC-2026-029",
        fileNo = "F-1029",
        nameBn = "জুনায়েদ হোসাইন",
        nameEn = "Junaid Hossain",
        districtBn = "সিলেট",
        districtEn = "Sylhet",
        gender = "Male",
        age = 13,
        groupId = "bukhari",
        groupNameBn = "বুখারী",
        groupNameEn = "Bukhari",
        subgroup = "Bukhari-2",
        roomNo = "Block A Room 102",
        bedNo = "Bed-03",
        mealPref = "নিয়মিত",
        guardianName = "মো: অভিভাবক",
        guardianPhone = "01711-292929"
    )

    @Test
    fun testQuickAlertPresetsContentAndSeverities() {
        assertNotNull(QUICK_ALERT_PRESETS)
        assertTrue("Presets should have at least 5 standard urgent templates", QUICK_ALERT_PRESETS.size >= 5)

        val validCategories = setOf("HEALTH", "SAFETY", "MISSING_PERSON", "INJURY")
        val validSeverities = setOf("CRITICAL", "HIGH", "MEDIUM", "LOW")

        for (preset in QUICK_ALERT_PRESETS) {
            assertTrue("Preset Bangla title must not be blank", preset.titleBn.isNotBlank())
            assertTrue("Preset English title must not be blank", preset.titleEn.isNotBlank())
            assertTrue("Preset category must be valid: ${preset.category}", validCategories.contains(preset.category))
            assertTrue("Preset default severity must be valid: ${preset.defaultSeverity}", validSeverities.contains(preset.defaultSeverity))
            assertTrue("Preset description must not be blank", preset.defaultDescBn.isNotBlank())
        }
    }

    @Test
    fun testSpecificPresetsCategories() {
        val asthma = QUICK_ALERT_PRESETS.find { it.category == "HEALTH" && it.titleEn.contains("Asthma", ignoreCase = true) }
        assertNotNull("Asthma preset should exist", asthma)
        assertEquals("CRITICAL", asthma?.defaultSeverity)

        val missing = QUICK_ALERT_PRESETS.find { it.category == "MISSING_PERSON" }
        assertNotNull("Missing person preset should exist", missing)
        assertEquals("CRITICAL", missing?.defaultSeverity)

        val injury = QUICK_ALERT_PRESETS.find { it.category == "INJURY" }
        assertNotNull("Injury preset should exist", injury)
        assertEquals("HIGH", injury?.defaultSeverity)

        val security = QUICK_ALERT_PRESETS.find { it.category == "SAFETY" }
        assertNotNull("Security/Safeguarding preset should exist", security)
        assertEquals("HIGH", security?.defaultSeverity)
    }

    @Test
    fun testIncidentEntityCreationWithCamperLink() {
        val incident = IncidentEntity(
            id = 1001L,
            title = "তীব্র শ্বাসকষ্ট / ইনহেলার প্রয়োজন",
            participantCampCode = sampleParticipant.campCode,
            participantName = sampleParticipant.nameBn,
            severity = "CRITICAL",
            location = "স্টেশন ২: ল্যাবরেটরি উইং",
            description = "অংশগ্রহণকারীর হঠাৎ তীব্র শ্বাসকষ্ট শুরু হয়েছে।",
            actionTaken = "ক্যাম্প অ্যাডমিনিস্ট্রেশনে তাৎক্ষণিক পুশ অ্যালার্ট প্রেরিত",
            reporter = "কাউন্সেলর (দলনেতা)",
            status = "OPEN"
        )

        assertEquals("LSC-2026-029", incident.participantCampCode)
        assertEquals("জুনায়েদ হোসাইন", incident.participantName)
        assertEquals("CRITICAL", incident.severity)
        assertEquals("OPEN", incident.status)
        assertEquals("স্টেশন ২: ল্যাবরেটরি উইং", incident.location)
        assertEquals("কাউন্সেলর (দলনেতা)", incident.reporter)
        assertTrue(incident.timestamp > 0)
    }

    @Test
    fun testIncidentLifecycleStatusTransitions() {
        // 1. Initially OPEN
        val initialAlert = IncidentEntity(
            id = 2001L,
            title = "অংশগ্রহণকারী নিখোঁজ",
            participantCampCode = "LSC-2026-050",
            participantName = "রাকিব হাসান",
            severity = "CRITICAL",
            location = "স্টেশন ৫: সুইমিং ও স্পোর্টস জোন",
            description = "সেশন শেষে রোস্টারে পাওয়া যাচ্ছে না",
            actionTaken = "জরুরি পুশ অ্যালার্ট প্রেরিত",
            reporter = "স্টেশন মডারেটর",
            status = "OPEN"
        )
        assertEquals("OPEN", initialAlert.status)

        // 2. Acknowledged by Camp Admin -> IN_PROGRESS
        val acknowledgedAlert = initialAlert.copy(
            status = "IN_PROGRESS",
            actionTaken = "কর্তৃপক্ষ কর্তৃক গৃহীত হয়েছে এবং অনুসন্ধান টিম পাঠানো হয়েছে।"
        )
        assertEquals("IN_PROGRESS", acknowledgedAlert.status)
        assertTrue(acknowledgedAlert.actionTaken.contains("কর্তৃপক্ষ কর্তৃক গৃহীত"))

        // 3. Resolved by Camp Admin -> RESOLVED
        val resolutionNotes = "অংশগ্রহণকারীকে অডিটোরিয়াম লাইব্রেরিতে নিরাপদে পাওয়া গিয়েছে।"
        val resolvedAlert = acknowledgedAlert.copy(
            status = "RESOLVED",
            actionTaken = "${acknowledgedAlert.actionTaken} | সমাধান: $resolutionNotes"
        )
        assertEquals("RESOLVED", resolvedAlert.status)
        assertTrue(resolvedAlert.actionTaken.contains("লাইব্রেরিতে নিরাপদে পাওয়া গিয়েছে"))
    }

    @Test
    fun testFilterActiveQuickAlerts() {
        val incidentList = listOf(
            IncidentEntity(id = 1L, title = "Alert 1", severity = "CRITICAL", location = "S1", description = "D1", actionTaken = "A1", reporter = "R1", status = "OPEN"),
            IncidentEntity(id = 2L, title = "Alert 2", severity = "HIGH", location = "S2", description = "D2", actionTaken = "A2", reporter = "R2", status = "IN_PROGRESS"),
            IncidentEntity(id = 3L, title = "Alert 3", severity = "LOW", location = "S3", description = "D3", actionTaken = "A3", reporter = "R3", status = "RESOLVED"),
            IncidentEntity(id = 4L, title = "Alert 4", severity = "CRITICAL", location = "S4", description = "D4", actionTaken = "A4", reporter = "R4", status = "RESOLVED"),
            IncidentEntity(id = 5L, title = "Alert 5", severity = "MEDIUM", location = "S5", description = "D5", actionTaken = "A5", reporter = "R5", status = "OPEN")
        )

        val activeAlerts = incidentList.filter { it.status != "RESOLVED" }
        assertEquals(3, activeAlerts.size)
        assertTrue(activeAlerts.any { it.id == 1L })
        assertTrue(activeAlerts.any { it.id == 2L })
        assertTrue(activeAlerts.any { it.id == 5L })
        assertFalse(activeAlerts.any { it.status == "RESOLVED" })

        val criticalActive = activeAlerts.filter { it.severity == "CRITICAL" }
        assertEquals(1, criticalActive.size)
        assertEquals(1L, criticalActive.first().id)
    }

    @Test
    fun testAlertPrioritySorting() {
        val alerts = listOf(
            IncidentEntity(id = 1L, title = "Low Alert", severity = "LOW", location = "S1", description = "", actionTaken = "", reporter = "", status = "OPEN"),
            IncidentEntity(id = 2L, title = "Critical Alert", severity = "CRITICAL", location = "S2", description = "", actionTaken = "", reporter = "", status = "OPEN"),
            IncidentEntity(id = 3L, title = "High Alert", severity = "HIGH", location = "S3", description = "", actionTaken = "", reporter = "", status = "OPEN")
        )

        val severityWeight = { s: String ->
            when (s) {
                "CRITICAL" -> 4
                "HIGH" -> 3
                "MEDIUM" -> 2
                else -> 1
            }
        }

        val sortedByPriority = alerts.sortedByDescending { severityWeight(it.severity) }
        assertEquals("CRITICAL", sortedByPriority[0].severity)
        assertEquals("HIGH", sortedByPriority[1].severity)
        assertEquals("LOW", sortedByPriority[2].severity)
    }
}
