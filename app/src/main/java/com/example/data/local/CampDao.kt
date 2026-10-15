package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CampDao {

    // Participants
    @Query("SELECT * FROM participants ORDER BY id ASC")
    fun getAllParticipants(): Flow<List<ParticipantEntity>>

    @Query("SELECT * FROM participants ORDER BY id ASC")
    suspend fun getAllParticipantsList(): List<ParticipantEntity>

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

    @Query("SELECT * FROM participants WHERE campCode = :campCode LIMIT 1")
    suspend fun getParticipantByCampCode(campCode: String): ParticipantEntity?

    @Query("SELECT * FROM participants WHERE id = :id LIMIT 1")
    suspend fun getParticipantById(id: Long): ParticipantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<ParticipantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: ParticipantEntity): Long

    @Update
    suspend fun updateParticipant(participant: ParticipantEntity)

    @Delete
    suspend fun deleteParticipant(participant: ParticipantEntity)

    @Query("DELETE FROM participants WHERE id = :id")
    suspend fun deleteParticipantById(id: Long)

    @Query("DELETE FROM participants WHERE campCode = :campCode")
    suspend fun deleteParticipantByCampCode(campCode: String)

    @Query("DELETE FROM participants")
    suspend fun deleteAllParticipants()

    @Query("SELECT COUNT(*) FROM participants")
    suspend fun getParticipantCount(): Int

    @Query("SELECT COUNT(*) FROM participants")
    fun observeParticipantCount(): Flow<Int>

    // Groups
    @Query("SELECT * FROM camp_groups ORDER BY id ASC")
    fun getAllGroups(): Flow<List<CampGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<CampGroupEntity>)

    @Update
    suspend fun updateGroup(group: CampGroupEntity)

    @Query("UPDATE camp_groups SET totalPoints = totalPoints + :points WHERE id = :groupId")
    suspend fun addGroupPoints(groupId: String, points: Int)

    // Stations
    @Query("SELECT * FROM stations ORDER BY id ASC")
    fun getAllStations(): Flow<List<StationEntity>>

    @Query("SELECT * FROM stations ORDER BY id ASC")
    suspend fun getAllStationsList(): List<StationEntity>

    @Query("SELECT * FROM stations WHERE id = :id LIMIT 1")
    suspend fun getStationById(id: Int): StationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<StationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStation(station: StationEntity): Long

    @Update
    suspend fun updateStation(station: StationEntity)

    @Delete
    suspend fun deleteStation(station: StationEntity)

    @Query("DELETE FROM stations WHERE id = :id")
    suspend fun deleteStationById(id: Int)

    // Schedule
    @Query("SELECT * FROM schedule_events WHERE dayNo = :dayNo ORDER BY id ASC")
    fun getScheduleForDay(dayNo: Int): Flow<List<ScheduleEventEntity>>

    @Query("SELECT * FROM schedule_events ORDER BY dayNo ASC, id ASC")
    fun getAllSchedule(): Flow<List<ScheduleEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleEvents(events: List<ScheduleEventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleEvent(event: ScheduleEventEntity): Long

    @Update
    suspend fun updateScheduleEvent(event: ScheduleEventEntity)

    @Delete
    suspend fun deleteScheduleEvent(event: ScheduleEventEntity)

    @Query("DELETE FROM schedule_events WHERE id = :id")
    suspend fun deleteScheduleEventById(id: Long)

    // Attendance Events
    @Query("SELECT * FROM attendance_events ORDER BY dayNo ASC, id ASC")
    fun getAllAttendanceEvents(): Flow<List<AttendanceEventEntity>>

    @Query("SELECT * FROM attendance_events WHERE id = :id LIMIT 1")
    suspend fun getAttendanceEventById(id: Long): AttendanceEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceEvent(event: AttendanceEventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceEvents(events: List<AttendanceEventEntity>)

    @Update
    suspend fun updateAttendanceEvent(event: AttendanceEventEntity)

    @Delete
    suspend fun deleteAttendanceEvent(event: AttendanceEventEntity)

    // Attendance Records
    @Query("SELECT * FROM attendance_records WHERE eventId = :eventId ORDER BY scannedAt DESC")
    fun getRecordsForEvent(eventId: Long): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance_records ORDER BY scannedAt DESC")
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance_records WHERE eventId = :eventId AND participantId = :participantId LIMIT 1")
    suspend fun getRecordForParticipant(eventId: Long, participantId: Long): AttendanceRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecord(record: AttendanceRecordEntity): Long

    @Query("SELECT COUNT(*) FROM attendance_records WHERE eventId = :eventId AND status = 'PRESENT'")
    suspend fun countPresentForEvent(eventId: Long): Int

    @Query("SELECT COUNT(*) FROM attendance_records WHERE eventId = :eventId AND status = 'LATE'")
    suspend fun countLateForEvent(eventId: Long): Int

    @Query("SELECT COUNT(*) FROM attendance_records WHERE eventId = :eventId AND status = 'ABSENT'")
    suspend fun countAbsentForEvent(eventId: Long): Int

    // Competitions
    @Query("SELECT * FROM competitions ORDER BY dayNo ASC, id ASC")
    fun getAllCompetitions(): Flow<List<CompetitionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompetitions(competitions: List<CompetitionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompetition(competition: CompetitionEntity): Long

    @Query("SELECT * FROM competition_scores WHERE competitionId = :competitionId ORDER BY totalScore DESC")
    fun getScoresForCompetition(competitionId: Long): Flow<List<CompetitionScoreEntity>>

    @Query("SELECT * FROM competition_scores ORDER BY totalScore DESC")
    fun getAllCompetitionScores(): Flow<List<CompetitionScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompetitionScore(score: CompetitionScoreEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompetitionScores(scores: List<CompetitionScoreEntity>)

    // Judges
    @Query("SELECT * FROM judges ORDER BY id DESC")
    fun getAllJudges(): Flow<List<JudgeEntity>>

    @Query("SELECT * FROM judges WHERE isActive = 1 ORDER BY id DESC")
    fun getActiveJudges(): Flow<List<JudgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJudge(judge: JudgeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJudges(judges: List<JudgeEntity>)

    @Update
    suspend fun updateJudge(judge: JudgeEntity)

    @Delete
    suspend fun deleteJudge(judge: JudgeEntity)

    // Volunteers
    @Query("SELECT * FROM volunteers ORDER BY id DESC")
    fun getAllVolunteers(): Flow<List<VolunteerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolunteer(volunteer: VolunteerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolunteers(volunteers: List<VolunteerEntity>)

    @Update
    suspend fun updateVolunteer(volunteer: VolunteerEntity)

    @Delete
    suspend fun deleteVolunteer(volunteer: VolunteerEntity)

    // Duty Officers
    @Query("SELECT * FROM duty_officers ORDER BY id ASC")
    fun getAllDutyOfficers(): Flow<List<DutyOfficerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDutyOfficer(officer: DutyOfficerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDutyOfficers(officers: List<DutyOfficerEntity>)

    @Update
    suspend fun updateDutyOfficer(officer: DutyOfficerEntity)

    @Delete
    suspend fun deleteDutyOfficer(officer: DutyOfficerEntity)

    // Health
    @Query("SELECT * FROM health_records ORDER BY createdAt DESC")
    fun getAllHealthRecords(): Flow<List<HealthRecordEntity>>

    @Query("SELECT * FROM health_records WHERE participantId = :participantId ORDER BY createdAt DESC")
    fun getHealthRecordsForParticipant(participantId: Long): Flow<List<HealthRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(record: HealthRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecords(records: List<HealthRecordEntity>)

    @Update
    suspend fun updateHealthRecord(record: HealthRecordEntity)

    @Query("SELECT * FROM health_records WHERE isEmergencyFlag = 1 ORDER BY createdAt DESC")
    fun getHighPriorityHealthRecords(): Flow<List<HealthRecordEntity>>

    // Inventory
    @Query("SELECT * FROM inventory_items ORDER BY id ASC")
    fun getAllInventoryItems(): Flow<List<InventoryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItems(items: List<InventoryItemEntity>)

    @Query("SELECT * FROM inventory_items WHERE id = :id LIMIT 1")
    suspend fun getInventoryItemById(id: Long): InventoryItemEntity?

    @Update
    suspend fun updateInventoryItem(item: InventoryItemEntity)

    @Query("SELECT * FROM inventory_transactions ORDER BY timestamp DESC")
    fun getAllInventoryTransactions(): Flow<List<InventoryTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryTransaction(tx: InventoryTransactionEntity): Long

    // Bulletins
    @Query("SELECT * FROM bulletins ORDER BY dayNo DESC, id DESC")
    fun getAllBulletins(): Flow<List<BulletinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBulletins(bulletins: List<BulletinEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBulletin(bulletin: BulletinEntity): Long

    @Update
    suspend fun updateBulletin(bulletin: BulletinEntity)

    @Delete
    suspend fun deleteBulletin(bulletin: BulletinEntity)

    // Incidents
    @Query("SELECT * FROM incidents ORDER BY timestamp DESC")
    fun getAllIncidents(): Flow<List<IncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: IncidentEntity): Long

    @Update
    suspend fun updateIncident(incident: IncidentEntity)

    @Delete
    suspend fun deleteIncident(incident: IncidentEntity)

    // Meals
    @Query("SELECT * FROM meal_distributions ORDER BY claimedAt DESC")
    fun getAllMealDistributions(): Flow<List<MealDistributionEntity>>

    @Query("SELECT * FROM meal_distributions WHERE dayNo = :dayNo AND mealType = :mealType ORDER BY claimedAt DESC")
    fun getMealClaims(dayNo: Int, mealType: String): Flow<List<MealDistributionEntity>>

    @Query("SELECT COUNT(*) FROM meal_distributions WHERE dayNo = :dayNo AND mealType = :mealType AND participantId = :participantId")
    suspend fun hasClaimedMeal(dayNo: Int, mealType: String, participantId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealClaim(claim: MealDistributionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealClaims(claims: List<MealDistributionEntity>)

    // Accommodation & Rooms
    @Query("SELECT * FROM camp_rooms ORDER BY buildingName ASC, roomNo ASC")
    fun getAllRooms(): Flow<List<RoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: RoomEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<RoomEntity>)

    @Update
    suspend fun updateRoom(room: RoomEntity)

    @Delete
    suspend fun deleteRoom(room: RoomEntity)

    // Travel & Vehicles
    @Query("SELECT * FROM travel_vehicles ORDER BY id ASC")
    fun getAllVehicles(): Flow<List<TravelVehicleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<TravelVehicleEntity>)

    @Update
    suspend fun updateVehicle(vehicle: TravelVehicleEntity)

    @Delete
    suspend fun deleteVehicle(vehicle: TravelVehicleEntity)

    // Duty Rosters
    @Query("SELECT * FROM duty_rosters ORDER BY id ASC")
    fun getAllDutyRosters(): Flow<List<DutyRosterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDutyRosters(rosters: List<DutyRosterEntity>)

    @Update
    suspend fun updateDutyRoster(roster: DutyRosterEntity)

    @Delete
    suspend fun deleteDutyRoster(roster: DutyRosterEntity)

    // Kit Distributions
    @Query("SELECT * FROM kit_distributions ORDER BY campCode ASC")
    fun getAllKitDistributions(): Flow<List<KitDistributionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKitDistributions(distributions: List<KitDistributionEntity>)

    @Update
    suspend fun updateKitDistribution(distribution: KitDistributionEntity)

    // Goal Metrics
    @Query("SELECT * FROM camp_goal_metrics ORDER BY id ASC")
    fun getAllGoalMetrics(): Flow<List<CampGoalMetricEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoalMetrics(metrics: List<CampGoalMetricEntity>)

    @Update
    suspend fun updateGoalMetric(metric: CampGoalMetricEntity)

    // User Accounts
    @Query("SELECT * FROM user_accounts ORDER BY fullName ASC")
    fun getAllUserAccounts(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    suspend fun getUserAccount(email: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(user: UserAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccounts(users: List<UserAccountEntity>)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity): Long

    @Transaction
    suspend fun seedDatabase(
        groups: List<CampGroupEntity>,
        stations: List<StationEntity>,
        participants: List<ParticipantEntity>,
        schedules: List<ScheduleEventEntity>,
        attendanceEvents: List<AttendanceEventEntity>,
        competitions: List<CompetitionEntity>,
        inventoryItems: List<InventoryItemEntity>,
        bulletins: List<BulletinEntity>,
        healthRecords: List<HealthRecordEntity>,
        competitionScores: List<CompetitionScoreEntity>,
        judges: List<JudgeEntity> = emptyList(),
        rooms: List<RoomEntity> = emptyList(),
        vehicles: List<TravelVehicleEntity> = emptyList(),
        dutyRosters: List<DutyRosterEntity> = emptyList(),
        kitDistributions: List<KitDistributionEntity> = emptyList(),
        goalMetrics: List<CampGoalMetricEntity> = emptyList(),
        users: List<UserAccountEntity> = emptyList(),
        meals: List<MealDistributionEntity> = emptyList()
    ) {
        insertGroups(groups)
        insertStations(stations)
        insertParticipants(participants)
        insertScheduleEvents(schedules)
        insertAttendanceEvents(attendanceEvents)
        insertCompetitions(competitions)
        insertInventoryItems(inventoryItems)
        insertBulletins(bulletins)
        insertHealthRecords(healthRecords)
        insertCompetitionScores(competitionScores)
        if (judges.isNotEmpty()) {
            insertJudges(judges)
        }
        if (rooms.isNotEmpty()) {
            insertRooms(rooms)
        }
        if (vehicles.isNotEmpty()) {
            insertVehicles(vehicles)
        }
        if (dutyRosters.isNotEmpty()) {
            insertDutyRosters(dutyRosters)
        }
        if (kitDistributions.isNotEmpty()) {
            insertKitDistributions(kitDistributions)
        }
        if (goalMetrics.isNotEmpty()) {
            insertGoalMetrics(goalMetrics)
        }
        if (users.isNotEmpty()) {
            insertUserAccounts(users)
        }
        if (meals.isNotEmpty()) {
            insertMealClaims(meals)
        }
    }

    // --- Cached Sync Queue (Low Connectivity Mode) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedSyncItem(item: CachedSyncItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedSyncItems(items: List<CachedSyncItemEntity>): List<Long>

    @Query("SELECT * FROM cached_sync_items ORDER BY cachedAt DESC")
    fun getAllCachedSyncItems(): Flow<List<CachedSyncItemEntity>>

    @Query("SELECT * FROM cached_sync_items WHERE status = 'PENDING' ORDER BY cachedAt ASC")
    fun getPendingCachedSyncItemsFlow(): Flow<List<CachedSyncItemEntity>>

    @Query("SELECT * FROM cached_sync_items WHERE status = 'PENDING' ORDER BY cachedAt ASC")
    suspend fun getPendingCachedSyncItems(): List<CachedSyncItemEntity>

    @Query("SELECT COUNT(*) FROM cached_sync_items WHERE status = 'PENDING'")
    fun observePendingCachedSyncCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cached_sync_items WHERE status = 'PENDING'")
    suspend fun getPendingCachedSyncCount(): Int

    @Query("UPDATE cached_sync_items SET status = 'SYNCED', syncedAt = :syncedAt WHERE id = :id")
    suspend fun markCachedItemSynced(id: Long, syncedAt: Long = System.currentTimeMillis())

    @Query("UPDATE cached_sync_items SET status = 'SYNCED', syncedAt = :syncedAt WHERE status = 'PENDING'")
    suspend fun markAllPendingCachedItemsSynced(syncedAt: Long = System.currentTimeMillis()): Int

    @Query("DELETE FROM cached_sync_items WHERE status = 'SYNCED'")
    suspend fun clearSyncedCacheHistory(): Int

    @Query("DELETE FROM cached_sync_items WHERE id = :id")
    suspend fun deleteCachedSyncItem(id: Long)

    @Query("SELECT * FROM attendance_records WHERE isSynced = 0")
    suspend fun getUnsyncedAttendanceRecords(): List<AttendanceRecordEntity>

    @Query("UPDATE attendance_records SET isSynced = 1 WHERE isSynced = 0")
    suspend fun markAllAttendanceRecordsSynced(): Int

    @Query("UPDATE attendance_records SET isSynced = 1 WHERE id = :id")
    suspend fun markAttendanceRecordSynced(id: Long)

    @Query("SELECT * FROM incidents WHERE isSynced = 0 ORDER BY timestamp ASC")
    suspend fun getUnsyncedIncidents(): List<IncidentEntity>

    @Query("UPDATE incidents SET isSynced = 1 WHERE id = :id")
    suspend fun markIncidentSynced(id: Long)

    @Query("SELECT * FROM health_records WHERE isSynced = 0 ORDER BY createdAt ASC")
    suspend fun getUnsyncedHealthRecords(): List<HealthRecordEntity>

    @Query("UPDATE health_records SET isSynced = 1 WHERE id = :id")
    suspend fun markHealthRecordSynced(id: Long)
}
