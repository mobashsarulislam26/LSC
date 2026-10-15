package com.example.data.local

import androidx.room.*
import com.example.data.model.StationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing Camp Station information in Room database.
 * Provides reactive Kotlin Coroutines Flow streams for data observation
 * and suspend functions for write/delete operations.
 */
@Dao
interface StationDao {

    // --- CREATE / INSERT ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStation(station: StationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<StationEntity>)

    // --- READ / QUERY ---
    @Query("SELECT * FROM stations ORDER BY id ASC")
    fun getAllStations(): Flow<List<StationEntity>>

    @Query("SELECT * FROM stations ORDER BY id ASC")
    suspend fun getAllStationsList(): List<StationEntity>

    @Query("SELECT * FROM stations WHERE id = :id LIMIT 1")
    fun getStationById(id: Int): Flow<StationEntity?>

    @Query("SELECT * FROM stations WHERE id = :id LIMIT 1")
    suspend fun getStationByIdImmediate(id: Int): StationEntity?

    @Query("SELECT COUNT(*) FROM stations")
    fun observeStationCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM stations")
    suspend fun getStationCount(): Int

    @Query("""
        SELECT * FROM stations 
        WHERE nameBn LIKE '%' || :query || '%' 
           OR nameEn LIKE '%' || :query || '%' 
           OR moderatorName LIKE '%' || :query || '%' 
           OR location LIKE '%' || :query || '%' 
           OR objectiveBn LIKE '%' || :query || '%'
        ORDER BY id ASC
    """)
    fun searchStations(query: String): Flow<List<StationEntity>>

    // --- UPDATE ---
    @Update
    suspend fun updateStation(station: StationEntity)

    @Update
    suspend fun updateStations(stations: List<StationEntity>)

    // --- DELETE ---
    @Delete
    suspend fun deleteStation(station: StationEntity)

    @Query("DELETE FROM stations WHERE id = :id")
    suspend fun deleteStationById(id: Int)

    @Query("DELETE FROM stations")
    suspend fun deleteAllStations()
}
