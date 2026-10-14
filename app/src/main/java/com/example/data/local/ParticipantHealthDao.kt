package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ParticipantHealthStatusEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing participant health status records,
 * including medication needs, allergies, and daily check-in logs.
 */
@Dao
interface ParticipantHealthDao {

    // --- CREATE ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthStatus(healthStatus: ParticipantHealthStatusEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthStatuses(healthStatuses: List<ParticipantHealthStatusEntity>): List<Long>

    // --- READ / QUERY ---
    @Query("SELECT * FROM participant_health_status ORDER BY id ASC")
    fun getAllHealthStatuses(): Flow<List<ParticipantHealthStatusEntity>>

    @Query("SELECT * FROM participant_health_status WHERE id = :id LIMIT 1")
    suspend fun getHealthStatusById(id: Long): ParticipantHealthStatusEntity?

    @Query("SELECT * FROM participant_health_status WHERE participantId = :participantId LIMIT 1")
    suspend fun getHealthStatusByParticipantId(participantId: Long): ParticipantHealthStatusEntity?

    @Query("SELECT * FROM participant_health_status WHERE participantId = :participantId LIMIT 1")
    fun observeHealthStatusByParticipantId(participantId: Long): Flow<ParticipantHealthStatusEntity?>

    @Query("SELECT * FROM participant_health_status WHERE campCode = :campCode LIMIT 1")
    suspend fun getHealthStatusByCampCode(campCode: String): ParticipantHealthStatusEntity?

    @Query("SELECT * FROM participant_health_status WHERE campCode = :campCode LIMIT 1")
    fun observeHealthStatusByCampCode(campCode: String): Flow<ParticipantHealthStatusEntity?>

    @Query("""
        SELECT * FROM participant_health_status 
        WHERE hasMedicationNeeds = 1 
           OR (LENGTH(TRIM(medicationNeeds)) > 0 AND LOWER(TRIM(medicationNeeds)) NOT IN ('none', 'n/a', 'no', 'নেই')) 
        ORDER BY participantName ASC
    """)
    fun getParticipantsWithMedicationNeeds(): Flow<List<ParticipantHealthStatusEntity>>

    @Query("""
        SELECT * FROM participant_health_status 
        WHERE hasAllergies = 1 
           OR (LENGTH(TRIM(allergies)) > 0 AND LOWER(TRIM(allergies)) NOT IN ('none', 'n/a', 'no', 'নেই')) 
        ORDER BY participantName ASC
    """)
    fun getParticipantsWithAllergies(): Flow<List<ParticipantHealthStatusEntity>>

    @Query("SELECT * FROM participant_health_status WHERE healthCondition != 'Normal' ORDER BY updatedAt DESC")
    fun getParticipantsRequiringAttention(): Flow<List<ParticipantHealthStatusEntity>>

    @Query("SELECT * FROM participant_health_status WHERE isDailyCheckedIn = 1 AND lastCheckInDate = :date ORDER BY lastCheckInTime DESC")
    fun getDailyCheckedInParticipants(date: String): Flow<List<ParticipantHealthStatusEntity>>

    @Query("SELECT * FROM participant_health_status WHERE isDailyCheckedIn = 0 OR lastCheckInDate != :date ORDER BY participantName ASC")
    fun getPendingDailyCheckInParticipants(date: String): Flow<List<ParticipantHealthStatusEntity>>

    @Query("""
        SELECT * FROM participant_health_status
        WHERE participantName LIKE '%' || :query || '%'
           OR campCode LIKE '%' || :query || '%'
           OR allergies LIKE '%' || :query || '%'
           OR medicationNeeds LIKE '%' || :query || '%'
           OR dailyCheckInLogs LIKE '%' || :query || '%'
           OR bloodGroup LIKE '%' || :query || '%'
        ORDER BY participantName ASC
    """)
    fun searchHealthStatus(query: String): Flow<List<ParticipantHealthStatusEntity>>

    @Query("SELECT COUNT(*) FROM participant_health_status")
    suspend fun getHealthStatusCount(): Int

    @Query("SELECT COUNT(*) FROM participant_health_status")
    fun observeHealthStatusCount(): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM participant_health_status 
        WHERE hasMedicationNeeds = 1 
           OR (LENGTH(TRIM(medicationNeeds)) > 0 AND LOWER(TRIM(medicationNeeds)) NOT IN ('none', 'n/a', 'no', 'নেই'))
    """)
    fun observeMedicationNeedsCount(): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM participant_health_status 
        WHERE hasAllergies = 1 
           OR (LENGTH(TRIM(allergies)) > 0 AND LOWER(TRIM(allergies)) NOT IN ('none', 'n/a', 'no', 'নেই'))
    """)
    fun observeAllergiesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM participant_health_status WHERE isDailyCheckedIn = 1 AND lastCheckInDate = :date")
    fun observeCheckedInCountForDate(date: String): Flow<Int>

    // --- UPDATE ---
    @Update
    suspend fun updateHealthStatus(healthStatus: ParticipantHealthStatusEntity)

    @Update
    suspend fun updateHealthStatuses(healthStatuses: List<ParticipantHealthStatusEntity>)

    @Query("""
        UPDATE participant_health_status
        SET isDailyCheckedIn = :isCheckedIn,
            lastCheckInDate = :checkInDate,
            lastCheckInTime = :checkInTime,
            dailyCheckInLogs = CASE 
                WHEN LENGTH(TRIM(dailyCheckInLogs)) > 0 
                THEN dailyCheckInLogs || char(10) || :logEntry 
                ELSE :logEntry 
            END,
            updatedAt = :updatedAt
        WHERE participantId = :participantId
    """)
    suspend fun appendDailyCheckInLog(
        participantId: Long,
        logEntry: String,
        isCheckedIn: Boolean,
        checkInDate: String,
        checkInTime: Long,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("""
        UPDATE participant_health_status
        SET medicationNeeds = :medicationNeeds,
            hasMedicationNeeds = :hasMedicationNeeds,
            updatedAt = :updatedAt
        WHERE participantId = :participantId
    """)
    suspend fun updateMedicationNeeds(
        participantId: Long,
        medicationNeeds: String,
        hasMedicationNeeds: Boolean,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("""
        UPDATE participant_health_status
        SET allergies = :allergies,
            hasAllergies = :hasAllergies,
            updatedAt = :updatedAt
        WHERE participantId = :participantId
    """)
    suspend fun updateAllergies(
        participantId: Long,
        allergies: String,
        hasAllergies: Boolean,
        updatedAt: Long = System.currentTimeMillis()
    )

    // --- DELETE ---
    @Delete
    suspend fun deleteHealthStatus(healthStatus: ParticipantHealthStatusEntity)

    @Query("DELETE FROM participant_health_status WHERE id = :id")
    suspend fun deleteHealthStatusById(id: Long)

    @Query("DELETE FROM participant_health_status WHERE participantId = :participantId")
    suspend fun deleteHealthStatusByParticipantId(participantId: Long)

    @Query("DELETE FROM participant_health_status WHERE campCode = :campCode")
    suspend fun deleteHealthStatusByCampCode(campCode: String)

    @Query("DELETE FROM participant_health_status")
    suspend fun deleteAllHealthStatuses()
}
