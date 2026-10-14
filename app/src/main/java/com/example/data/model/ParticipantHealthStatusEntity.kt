package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity for tracking participant health status in the camp,
 * including medication needs, allergies, and daily check-in logs.
 */
@Entity(
    tableName = "participant_health_status",
    indices = [
        Index(value = ["participantId"]),
        Index(value = ["campCode"]),
        Index(value = ["hasMedicationNeeds"]),
        Index(value = ["hasAllergies"]),
        Index(value = ["isDailyCheckedIn"])
    ]
)
data class ParticipantHealthStatusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val participantId: Long,
    val campCode: String,
    val participantName: String,
    val medicationNeeds: String = "",       // Details of medications, schedules, and dosages
    val allergies: String = "",             // Food, medicine, or environmental allergies
    val dailyCheckInLogs: String = "",       // Chronological logs and notes from daily check-ins
    val hasMedicationNeeds: Boolean = false, // Fast indexing & filter flag
    val hasAllergies: Boolean = false,       // Fast indexing & filter flag
    val isDailyCheckedIn: Boolean = false,   // Today's daily check-in indicator
    val lastCheckInDate: String = "",        // e.g. "2026-10-13"
    val lastCheckInTime: Long = System.currentTimeMillis(),
    val vitalsSummary: String = "",          // e.g. "BP: 110/70, Temp: 98.6°F, SpO2: 99%"
    val healthCondition: String = "Normal",  // "Normal", "Under Observation", "Requires Attention", "Critical"
    val bloodGroup: String = "",             // e.g. "A+", "B+", "O+", "AB+"
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val doctorOrNurseNotes: String = "",
    val specialDietNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

typealias ParticipantHealthStatus = ParticipantHealthStatusEntity
