package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ParticipantEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing CRUD operations on Camp Participants.
 * All write/delete operations are suspend functions running off the main thread,
 * and data queries return reactive Kotlin Coroutines Flow streams.
 */
@Dao
interface ParticipantDao {

    // --- CREATE ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: ParticipantEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<ParticipantEntity>): List<Long>

    // --- READ ---
    @Query("SELECT * FROM participants ORDER BY id ASC")
    fun getAllParticipants(): Flow<List<ParticipantEntity>>

    @Query("SELECT * FROM participants ORDER BY id ASC")
    suspend fun getAllParticipantsList(): List<ParticipantEntity>

    @Query("SELECT * FROM participants WHERE id = :id LIMIT 1")
    suspend fun getParticipantById(id: Long): ParticipantEntity?

    @Query("SELECT * FROM participants WHERE campCode = :campCode LIMIT 1")
    suspend fun getParticipantByCampCode(campCode: String): ParticipantEntity?

    @Query("SELECT * FROM participants WHERE groupId = :groupId ORDER BY id ASC")
    fun getParticipantsByGroup(groupId: String): Flow<List<ParticipantEntity>>

    @Query("""
        SELECT * FROM participants 
        WHERE nameBn LIKE '%' || :query || '%' 
           OR nameEn LIKE '%' || :query || '%' 
           OR campCode LIKE '%' || :query || '%' 
           OR fileNo LIKE '%' || :query || '%'
           OR districtBn LIKE '%' || :query || '%'
           OR subgroup LIKE '%' || :query || '%'
        ORDER BY id ASC
    """)
    fun searchParticipants(query: String): Flow<List<ParticipantEntity>>

    @Query("SELECT COUNT(*) FROM participants")
    suspend fun getParticipantCount(): Int

    @Query("SELECT COUNT(*) FROM participants")
    fun observeParticipantCount(): Flow<Int>

    // --- UPDATE ---
    @Update
    suspend fun updateParticipant(participant: ParticipantEntity)

    @Update
    suspend fun updateParticipants(participants: List<ParticipantEntity>)

    // --- DELETE ---
    @Delete
    suspend fun deleteParticipant(participant: ParticipantEntity)

    @Query("DELETE FROM participants WHERE id = :id")
    suspend fun deleteParticipantById(id: Long)

    @Query("DELETE FROM participants WHERE campCode = :campCode")
    suspend fun deleteParticipantByCampCode(campCode: String)

    @Query("DELETE FROM participants")
    suspend fun deleteAllParticipants()
}
