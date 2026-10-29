package com.example.data.repository

import android.content.Context
import com.example.data.firestore.FirestoreManager
import com.example.data.local.CampDao
import com.example.data.local.CampDatabase
import com.example.data.local.ParticipantDao
import com.example.data.local.ParticipantHealthDao
import com.example.data.local.StationDao
import com.example.data.model.*
import com.example.data.sample.SeedData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.data.service.ParticipantImportResult
import com.example.data.service.ParticipantImportService
import com.example.data.service.ScheduleBulkUploadResult
import com.example.data.service.ScheduleBulkUploadService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class CampRepository(
    private val dao: CampDao,
    private val healthDao: ParticipantHealthDao? = null,
    private val participantDao: ParticipantDao? = null,
    private val stationDao: StationDao? = null
) {
    private val seedMutex = Mutex()

    val participants: Flow<List<ParticipantEntity>> = combine(
        (participantDao?.getAllParticipants() ?: dao.getAllParticipants()).catch { emit(emptyList()) },
        FirestoreManager.observeParticipants().catch { emit(emptyList()) }
    ) { localList, remoteList ->
        if (localList.isNotEmpty()) localList else remoteList
    }.catch {
        emitAll(participantDao?.getAllParticipants() ?: dao.getAllParticipants())
    }

    val participantCount: Flow<Int> = participantDao?.observeParticipantCount() ?: dao.observeParticipantCount()
    val healthStatuses: Flow<List<ParticipantHealthStatusEntity>> =
        healthDao?.getAllHealthStatuses() ?: flowOf(emptyList())
    val groups: Flow<List<CampGroupEntity>> = dao.getAllGroups()
    val stations: Flow<List<StationEntity>> = stationDao?.getAllStations() ?: dao.getAllStations()

    val schedule: Flow<List<ScheduleEventEntity>> = combine(
        dao.getAllSchedule().catch { emit(emptyList()) },
        FirestoreManager.observeSchedule().catch { emit(emptyList()) }
    ) { localList, remoteList ->
        when {
            localList.isNotEmpty() -> localList
            remoteList.isNotEmpty() -> remoteList.sortedWith(compareBy({ it.dayNo }, { it.timeSlot }))
            else -> emptyList()
        }
    }.catch {
        emitAll(dao.getAllSchedule())
    }
    val attendanceEvents: Flow<List<AttendanceEventEntity>> = dao.getAllAttendanceEvents()
    val attendanceRecords: Flow<List<AttendanceRecordEntity>> = dao.getAllAttendanceRecords()
    val competitions: Flow<List<CompetitionEntity>> = dao.getAllCompetitions()
    val competitionScores: Flow<List<CompetitionScoreEntity>> = dao.getAllCompetitionScores()
    val judges: Flow<List<JudgeEntity>> = dao.getAllJudges()
    val healthRecords: Flow<List<HealthRecordEntity>> = dao.getAllHealthRecords()
    val inventoryItems: Flow<List<InventoryItemEntity>> = dao.getAllInventoryItems()
    val inventoryTransactions: Flow<List<InventoryTransactionEntity>> = dao.getAllInventoryTransactions()
    val bulletins: Flow<List<BulletinEntity>> = dao.getAllBulletins()
    val incidents: Flow<List<IncidentEntity>> = dao.getAllIncidents()
    val volunteers: Flow<List<VolunteerEntity>> = dao.getAllVolunteers()
    val dutyOfficers: Flow<List<DutyOfficerEntity>> = dao.getAllDutyOfficers()
    val rooms: Flow<List<RoomEntity>> = dao.getAllRooms()
    val vehicles: Flow<List<TravelVehicleEntity>> = dao.getAllVehicles()
    val dutyRosters: Flow<List<DutyRosterEntity>> = dao.getAllDutyRosters()
    val volunteerShifts: Flow<List<VolunteerShiftEntity>> = dao.getAllVolunteerShifts()
    val kitDistributions: Flow<List<KitDistributionEntity>> = dao.getAllKitDistributions()
    val goalMetrics: Flow<List<CampGoalMetricEntity>> = dao.getAllGoalMetrics()
    val mealDistributions: Flow<List<MealDistributionEntity>> = dao.getAllMealDistributions()
    val userAccounts: Flow<List<UserAccountEntity>> = dao.getAllUserAccounts()
    val auditLogs: Flow<List<AuditLogEntity>> = combine(
        dao.getAllAuditLogs().catch { emit(emptyList()) },
        FirestoreManager.observeAuditLogs().catch { emit(emptyList()) }
    ) { localLogs, remoteLogs ->
        if (localLogs.isNotEmpty()) localLogs else remoteLogs
    }.catch {
        emitAll(dao.getAllAuditLogs())
    }
    val cachedSyncItems: Flow<List<CachedSyncItemEntity>> = dao.getAllCachedSyncItems()
    val pendingCachedSyncCount: Flow<Int> = dao.observePendingCachedSyncCount()

    fun searchParticipants(query: String): Flow<List<ParticipantEntity>> {
        return if (query.isBlank()) dao.getAllParticipants() else dao.searchParticipants(query)
    }

    fun getParticipantsByGroup(groupId: String): Flow<List<ParticipantEntity>> {
        return dao.getParticipantsByGroup(groupId)
    }

    suspend fun getParticipantByCampCode(code: String): ParticipantEntity? {
        return withContext(Dispatchers.IO) {
            dao.getParticipantByCampCode(code.trim())
                ?: dao.getAllParticipantsList().firstOrNull {
                    it.campCode.equals(code.trim(), ignoreCase = true) ||
                        it.fileNo.equals(code.trim(), ignoreCase = true)
                }
        }
    }

    suspend fun getParticipantById(id: Long): ParticipantEntity? {
        return withContext(Dispatchers.IO) {
            dao.getParticipantById(id)
        }
    }

    suspend fun insertParticipant(participant: ParticipantEntity): Long {
        return withContext(Dispatchers.IO) {
            dao.insertParticipant(participant)
        }
    }

    suspend fun insertParticipants(participants: List<ParticipantEntity>) {
        withContext(Dispatchers.IO) {
            dao.insertParticipants(participants)
        }
    }

    suspend fun updateParticipant(participant: ParticipantEntity) {
        withContext(Dispatchers.IO) {
            dao.updateParticipant(participant)
        }
    }

    suspend fun deleteParticipant(participant: ParticipantEntity) {
        withContext(Dispatchers.IO) {
            dao.deleteParticipant(participant)
        }
    }

    suspend fun deleteParticipantById(id: Long) {
        withContext(Dispatchers.IO) {
            dao.deleteParticipantById(id)
        }
    }

    suspend fun getRecordsForEvent(eventId: Long): Flow<List<AttendanceRecordEntity>> {
        return withContext(Dispatchers.IO) {
            dao.getRecordsForEvent(eventId)
        }
    }

    suspend fun stampParticipantStationSeal(
        participantId: Long,
        stationId: Int,
        dayNo: Int
    ): ParticipantEntity? = withContext(Dispatchers.IO) {
        val current = dao.getParticipantById(participantId) ?: return@withContext null
        val normalizedDay = when (dayNo) {
            2, 4, 5 -> dayNo
            1 -> 2
            3 -> 4
            else -> 2
        }
        val sealToken = "S${stationId.coerceIn(1, 6)}D$normalizedDay"
        val updatedSeals = appendUniqueSeal(current.completedStationSeals, sealToken)
        if (updatedSeals != current.completedStationSeals) {
            val updated = current.copy(completedStationSeals = updatedSeals)
            dao.updateParticipant(updated)
            updated
        } else {
            current
        }
    }

    private fun appendUniqueSeal(existingCsv: String, newSeal: String): String {
        val cleanNew = newSeal.trim().uppercase()
        if (cleanNew.isEmpty()) return existingCsv
        val tokens = existingCsv.split(',')
            .map { it.trim().uppercase() }
            .filter { it.isNotEmpty() }
            .toMutableSet()
        tokens.add(cleanNew)
        return tokens.sorted().joinToString(",")
    }

    /**
     * 1-Tap Batch Subgroup Attendance + 18-Star Station Seal Stamper!
     * Marks an entire subgroup (21–31 students) PRESENT/ABSENT in a single instant batch operation.
     */
    suspend fun markSubgroupBatchAttendance(
        eventId: Long,
        eventTitleBn: String,
        dayNo: Int,
        subgroupKey: String,
        absentParticipantIds: Set<Long>,
        stationSealCode: String? = null,
        isOfflineMode: Boolean = false
    ): Pair<Int, Int> = withContext(Dispatchers.IO) {
        val allList = dao.getAllParticipantsList()
        val cleanTargetKey = subgroupKey.lowercase().replace("[^a-z0-9]".toRegex(), "")
        val subgroupMembers = allList.filter { p ->
            p.subgroup.equals(subgroupKey, ignoreCase = true) ||
                p.subgroup.lowercase().replace("[^a-z0-9]".toRegex(), "") == cleanTargetKey
        }
        if (subgroupMembers.isEmpty()) return@withContext Pair(0, 0)

        // Ensure event exists if it's a dynamic slot event
        val existingEvent = dao.getAttendanceEventById(eventId)
        if (existingEvent == null) {
            dao.insertAttendanceEvent(
                AttendanceEventEntity(
                    id = eventId,
                    titleBn = eventTitleBn.ifBlank { "সাব-গ্রুপ সেশন হাজিরা ($subgroupKey)" },
                    titleEn = "Subgroup Batch Session ($subgroupKey)",
                    dayNo = dayNo.coerceIn(1, 5),
                    timeLabel = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
                    targetCount = 609
                )
            )
        }

        val existingRecords = dao.getRecordsForEventImmediate(eventId).associateBy { it.participantId }
        val now = System.currentTimeMillis()
        val batchRecords = ArrayList<AttendanceRecordEntity>(subgroupMembers.size)
        val updatedParticipants = ArrayList<ParticipantEntity>()
        var presentCountInSubgroup = 0
        var absentCountInSubgroup = 0

        for (member in subgroupMembers) {
            val isAbsent = absentParticipantIds.contains(member.id)
            val status = if (isAbsent) "ABSENT" else "PRESENT"
            if (isAbsent) absentCountInSubgroup++ else presentCountInSubgroup++

            val prev = existingRecords[member.id]
            batchRecords.add(
                AttendanceRecordEntity(
                    id = prev?.id ?: 0L,
                    eventId = eventId,
                    participantId = member.id,
                    campCode = member.campCode,
                    participantName = member.nameBn,
                    groupName = member.groupNameBn,
                    subgroup = member.subgroup,
                    status = status,
                    method = "BATCH_SUBGROUP",
                    overrideReason = stationSealCode?.let { "Seal:$it" },
                    scannedAt = now,
                    isSynced = !isOfflineMode
                )
            )

            if (!isAbsent && !stationSealCode.isNullOrBlank()) {
                val newSeals = appendUniqueSeal(member.completedStationSeals, stationSealCode)
                if (newSeals != member.completedStationSeals) {
                    updatedParticipants.add(member.copy(completedStationSeals = newSeals))
                }
            }
        }

        dao.insertAttendanceRecords(batchRecords)
        if (updatedParticipants.isNotEmpty()) {
            dao.updateParticipants(updatedParticipants)
        }

        if (isOfflineMode) {
            dao.insertCachedSyncItem(
                CachedSyncItemEntity(
                    itemType = CachedSyncItemEntity.TYPE_ATTENDANCE,
                    participantId = subgroupMembers.first().id,
                    campCode = subgroupKey,
                    participantName = "$subgroupKey (${subgroupMembers.size} জন)",
                    summary = "ব্যাচ সাব-গ্রুপ হাজিরা: $subgroupKey (উপস্থিত: $presentCountInSubgroup, অনুপস্থিত: $absentCountInSubgroup)",
                    payloadJson = com.example.data.util.MoshiJsonUtils.toJson(
                        mapOf(
                            "eventId" to eventId,
                            "subgroup" to subgroupKey,
                            "present" to presentCountInSubgroup,
                            "absent" to absentCountInSubgroup,
                            "seal" to (stationSealCode ?: "")
                        )
                    ),
                    status = CachedSyncItemEntity.STATUS_PENDING
                )
            )
        }

        val refreshedEvent = dao.getAttendanceEventById(eventId)
        if (refreshedEvent != null) {
            dao.updateAttendanceEvent(
                refreshedEvent.copy(
                    presentCount = dao.countPresentForEvent(eventId),
                    lateCount = dao.countLateForEvent(eventId),
                    absentCount = dao.countAbsentForEvent(eventId)
                )
            )
        }

        Pair(presentCountInSubgroup, absentCountInSubgroup)
    }

    suspend fun recordAttendance(
        eventId: Long,
        participant: ParticipantEntity,
        status: String,
        method: String,
        reason: String? = null,
        isOfflineMode: Boolean = false
    ): Boolean = withContext(Dispatchers.IO) {
        val existing = dao.getRecordForParticipant(eventId, participant.id)
        if (existing != null) {
            dao.insertAttendanceRecord(
                existing.copy(
                    status = status,
                    method = method,
                    overrideReason = reason,
                    scannedAt = System.currentTimeMillis(),
                    isSynced = !isOfflineMode
                )
            )
        } else {
            val record = AttendanceRecordEntity(
                eventId = eventId,
                participantId = participant.id,
                campCode = participant.campCode,
                participantName = participant.nameBn,
                groupName = participant.groupNameBn,
                subgroup = participant.subgroup,
                status = status,
                method = method,
                overrideReason = reason,
                isSynced = !isOfflineMode
            )
            dao.insertAttendanceRecord(record)
        }

        if (isOfflineMode) {
            dao.insertCachedSyncItem(
                CachedSyncItemEntity(
                    itemType = CachedSyncItemEntity.TYPE_ATTENDANCE,
                    participantId = participant.id,
                    campCode = participant.campCode,
                    participantName = participant.nameBn,
                    summary = "হাজিরা স্ক্যান ($status) - ইভেন্ট #$eventId [${participant.groupNameBn}]",
                    payloadJson = com.example.data.util.MoshiJsonUtils.toJson(
                        mapOf(
                            "eventId" to eventId,
                            "status" to status,
                            "method" to method,
                            "reason" to (reason ?: "")
                        )
                    ),
                    status = CachedSyncItemEntity.STATUS_PENDING
                )
            )
        }

        // Persist recomputed counts onto the attendance event so the UI stays accurate.
        val event = dao.getAttendanceEventById(eventId)
        if (event != null) {
            val present = dao.countPresentForEvent(eventId)
            val late = dao.countLateForEvent(eventId)
            val absent = dao.countAbsentForEvent(eventId)
            dao.updateAttendanceEvent(
                event.copy(
                    presentCount = present,
                    lateCount = late,
                    absentCount = absent
                )
            )
        }
        true
    }

    suspend fun submitCompetitionScore(score: CompetitionScoreEntity) = withContext(Dispatchers.IO) {
        dao.insertCompetitionScore(score)
        val group = dao.getParticipantById(score.participantId)?.groupId
        if (group != null) {
            dao.addGroupPoints(group, (score.totalScore / 10).toInt())
        }
    }

    /**
     * 3-Tap Podium Winner Selection (1st / 2nd / 3rd) + Immediate Session Prize Handover!
     */
    suspend fun awardPodiumWinner(
        competitionId: Long,
        participant: ParticipantEntity,
        rank: Int,
        isPrizeHandedOver: Boolean,
        remarks: String = ""
    ) = withContext(Dispatchers.IO) {
        val existingScores = dao.getScoresForCompetitionImmediate(competitionId)
        val existingForParticipant = existingScores.firstOrNull { it.participantId == participant.id }
        val (acc, pres, disc) = when (rank) {
            1 -> Triple(39.0, 29.0, 28.0) // 96.0
            2 -> Triple(37.0, 27.0, 27.0) // 91.0
            else -> Triple(35.0, 26.0, 25.0) // 86.0
        }
        val total = acc + pres + disc
        val entry = CompetitionScoreEntity(
            id = existingForParticipant?.id ?: 0L,
            competitionId = competitionId,
            participantId = participant.id,
            campCode = participant.campCode,
            participantName = participant.nameBn,
            groupName = participant.groupNameBn,
            subgroup = participant.subgroup,
            accuracyScore = acc,
            presentationScore = pres,
            disciplineScore = disc,
            totalScore = total,
            remarks = remarks.ifBlank {
                when (rank) {
                    1 -> "🥇 ১ম স্থান অর্জনকারী (${participant.subgroup})"
                    2 -> "🥈 ২য় স্থান অর্জনকারী (${participant.subgroup})"
                    else -> "🥉 ৩য় স্থান অর্জনকারী (${participant.subgroup})"
                }
            },
            rank = rank.coerceIn(1, 3),
            isAwardEligible = true,
            isPrizeHandedOver = isPrizeHandedOver
        )
        dao.insertCompetitionScore(entry)
        dao.addGroupPoints(participant.groupId, when (rank) { 1 -> 15; 2 -> 10; else -> 5 })
    }

    suspend fun toggleCompetitionPrizeHandedOver(score: CompetitionScoreEntity) = withContext(Dispatchers.IO) {
        dao.updateCompetitionScore(score.copy(isPrizeHandedOver = !score.isPrizeHandedOver))
    }

    suspend fun updateRoomInspection(room: RoomEntity, cleanlinessScore: Int, notes: String) = withContext(Dispatchers.IO) {
        val updated = room.copy(
            cleanlinessScore = cleanlinessScore.coerceIn(0, 10),
            lastInspectedAt = System.currentTimeMillis(),
            notes = notes
        )
        dao.updateRoom(updated)
    }

    suspend fun addJudge(judge: JudgeEntity) = withContext(Dispatchers.IO) {
        dao.insertJudge(judge)
    }

    suspend fun addJudges(judges: List<JudgeEntity>) = withContext(Dispatchers.IO) {
        dao.insertJudges(judges)
    }

    suspend fun updateJudge(judge: JudgeEntity) = withContext(Dispatchers.IO) {
        dao.updateJudge(judge)
    }

    suspend fun deleteJudge(judge: JudgeEntity) = withContext(Dispatchers.IO) {
        dao.deleteJudge(judge)
    }

    suspend fun updateScheduleEvent(event: ScheduleEventEntity) = withContext(Dispatchers.IO) {
        dao.updateScheduleEvent(event)
    }

    suspend fun updateGroup(group: CampGroupEntity) = withContext(Dispatchers.IO) {
        dao.updateGroup(group)
    }

    suspend fun publishBulletin(bulletin: BulletinEntity): Long = withContext(Dispatchers.IO) {
        val id = dao.insertBulletin(bulletin)
        val saved = if (bulletin.id == 0L) bulletin.copy(id = id) else bulletin
        try {
            FirestoreManager.saveBulletin(saved)
        } catch (e: Exception) {
            android.util.Log.w("CampRepository", "Firestore saveBulletin deferred: ${e.message}")
        }
        id
    }

    suspend fun addVolunteers(volunteers: List<VolunteerEntity>) = withContext(Dispatchers.IO) {
        dao.insertVolunteers(volunteers)
    }

    suspend fun updateVolunteer(volunteer: VolunteerEntity) = withContext(Dispatchers.IO) {
        dao.updateVolunteer(volunteer)
    }

    suspend fun deleteVolunteer(volunteer: VolunteerEntity) = withContext(Dispatchers.IO) {
        dao.deleteVolunteer(volunteer)
    }

    suspend fun addDutyOfficers(officers: List<DutyOfficerEntity>) = withContext(Dispatchers.IO) {
        dao.insertDutyOfficers(officers)
    }

    suspend fun updateDutyOfficer(officer: DutyOfficerEntity) = withContext(Dispatchers.IO) {
        dao.updateDutyOfficer(officer)
    }

    suspend fun deleteDutyOfficer(officer: DutyOfficerEntity) = withContext(Dispatchers.IO) {
        dao.deleteDutyOfficer(officer)
    }

    suspend fun submitHealthRecord(
        record: HealthRecordEntity,
        isOfflineMode: Boolean = false
    ) = withContext(Dispatchers.IO) {
        val id = dao.insertHealthRecord(record)
        if (isOfflineMode) {
            dao.insertCachedSyncItem(
                CachedSyncItemEntity(
                    itemType = CachedSyncItemEntity.TYPE_HEALTH_CHECKUP,
                    participantId = record.participantId,
                    campCode = record.campCode,
                    participantName = record.participantName,
                    summary = "স্বাস্থ্য স্ক্রিনিং: ${record.checkupType} (${record.doctorName.ifBlank { "মেডিকেল টিম" }})",
                    payloadJson = com.example.data.util.MoshiJsonUtils.toJson(
                        mapOf(
                            "checkupType" to record.checkupType,
                            "doctor" to record.doctorName,
                            "diagnosis" to record.diagnosis,
                            "vitals" to record.vitals,
                            "adviceAndMeds" to record.adviceAndMeds
                        )
                    ),
                    status = CachedSyncItemEntity.STATUS_PENDING
                )
            )
        }
        id
    }

    fun getHealthRecordsForParticipant(participantId: Long): Flow<List<HealthRecordEntity>> {
        return dao.getHealthRecordsForParticipant(participantId)
    }

    suspend fun updateHealthRecord(record: HealthRecordEntity) = withContext(Dispatchers.IO) {
        dao.updateHealthRecord(record)
    }

    suspend fun issueInventoryItem(
        itemId: Long,
        qty: Int,
        recipient: String,
        officer: String
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val item = dao.getInventoryItemById(itemId)
            ?: return@withContext Pair(false, "আইটেম খুঁজে পাওয়া যায়নি (Item not found)")
        if (qty <= 0) {
            return@withContext Pair(false, "পরিমাণ ১ বা তার বেশি হতে হবে (Quantity must be at least 1)")
        }
        if (qty > item.balanceQty) {
            return@withContext Pair(
                false,
                "পর্যাপ্ত স্টক নেই (Insufficient stock): অবশিষ্ট ${item.balanceQty} ${item.unit}"
            )
        }
        if (recipient.isBlank()) {
            return@withContext Pair(false, "গ্রহীতার নাম আবশ্যক (Recipient is required)")
        }

        val newIssuedQty = item.issuedQty + qty
        val updated = item.copy(
            issuedQty = newIssuedQty,
            balanceQty = (item.openingQty + item.receivedQty - newIssuedQty).coerceAtLeast(0)
        )
        dao.updateInventoryItem(updated)
        dao.insertInventoryTransaction(
            InventoryTransactionEntity(
                itemId = itemId,
                itemName = item.nameBn,
                type = "ISSUE",
                qty = qty,
                recipient = recipient,
                officerName = officer
            )
        )
        Pair(true, "${item.nameBn} - $qty ${item.unit} ইস্যু সম্পন্ন (অবশিষ্ট: ${updated.balanceQty})")
    }

    suspend fun reportIncident(incident: IncidentEntity): Long = withContext(Dispatchers.IO) {
        val id = dao.insertIncident(incident)
        val saved = if (incident.id == 0L) incident.copy(id = id) else incident
        try {
            FirestoreManager.saveIncident(saved)
        } catch (e: Exception) {
            android.util.Log.w("CampRepository", "Firestore saveIncident deferred: ${e.message}")
        }
        id
    }

    suspend fun updateIncident(incident: IncidentEntity) = withContext(Dispatchers.IO) {
        dao.updateIncident(incident)
        try {
            FirestoreManager.saveIncident(incident)
        } catch (e: Exception) {
            android.util.Log.w("CampRepository", "Firestore updateIncident deferred: ${e.message}")
        }
    }

    suspend fun claimMeal(dayNo: Int, mealType: String, participant: ParticipantEntity): Pair<Boolean, String> =
        withContext(Dispatchers.IO) {
            val alreadyClaimed = dao.hasClaimedMeal(dayNo, mealType, participant.id)
            if (alreadyClaimed > 0) {
                Pair(false, "পূর্বেই গ্রহণ করা হয়েছে! (Already Claimed)")
            } else {
                dao.insertMealClaim(
                    MealDistributionEntity(
                        dayNo = dayNo,
                        mealType = mealType,
                        participantId = participant.id,
                        campCode = participant.campCode,
                        participantName = participant.nameBn
                    )
                )
                Pair(true, "সফলভাবে খাবার গ্রহণ সম্পন্ন (Meal Issued Successfully)")
            }
        }

    suspend fun importParticipantsFromCsv(csvText: String): Int = withContext(Dispatchers.IO) {
        val lines = csvText.lines().filter { it.isNotBlank() }
        var count = 0
        val newParticipants = mutableListOf<ParticipantEntity>()
        lines.drop(1).forEachIndexed { index, line ->
            val cols = line.split(",").map { it.trim() }
            if (cols.size >= 4) {
                val code = if (cols[0].isNotBlank()) cols[0] else "Bukhari-1-${610 + index}"
                val fileNo = if (cols.size > 1 && cols[1].isNotBlank()) cols[1] else "WAMY-${1610 + index}"
                val name = cols.getOrElse(2) { "New Participant" }
                val district = cols.getOrElse(3) { "ঢাকা" }
                val group = cols.getOrElse(4) { "বুখারী গ্রুপ" }
                val sub = cols.getOrElse(5) { "Bukhari-01" }
                val p = ParticipantEntity(
                    campCode = code,
                    fileNo = fileNo,
                    nameBn = name,
                    nameEn = name,
                    districtBn = district,
                    districtEn = district,
                    gender = if (group.contains("সুমাই") || group.contains("উম্মে")) "Female" else "Male",
                    age = 13,
                    groupId = "bukhari",
                    groupNameBn = group,
                    groupNameEn = "Bukhari Group",
                    subgroup = sub,
                    roomNo = "P1-Down-A",
                    bedNo = "Bed-01",
                    mealPref = "নিয়মিত",
                    guardianName = "অভিভাবক",
                    guardianPhone = "01700-000000"
                )
                newParticipants.add(p)
                count++
            }
        }
        if (newParticipants.isNotEmpty()) {
            dao.insertParticipants(newParticipants)
        }
        count
    }

    suspend fun updateStation(station: StationEntity) = withContext(Dispatchers.IO) {
        if (stationDao != null) {
            stationDao.updateStation(station)
        } else {
            dao.updateStation(station)
        }
    }

    suspend fun insertStation(station: StationEntity) = withContext(Dispatchers.IO) {
        if (stationDao != null) {
            stationDao.insertStation(station)
        } else {
            dao.insertStation(station)
        }
    }

    suspend fun deleteStation(station: StationEntity) = withContext(Dispatchers.IO) {
        if (stationDao != null) {
            stationDao.deleteStationById(station.id)
        } else {
            dao.deleteStation(station)
        }
    }

    suspend fun getStationById(id: Int): StationEntity? = withContext(Dispatchers.IO) {
        stationDao?.getStationByIdImmediate(id) ?: dao.getAllStationsList().find { it.id == id }
    }

    fun searchStations(query: String): Flow<List<StationEntity>> {
        return stationDao?.searchStations(query) ?: dao.getAllStations()
    }

    suspend fun deleteStationById(id: Int) = withContext(Dispatchers.IO) {
        if (stationDao != null) {
            stationDao.deleteStationById(id)
        } else {
            dao.deleteStationById(id)
        }
    }

    suspend fun addScheduleEvent(event: ScheduleEventEntity) = withContext(Dispatchers.IO) {
        dao.insertScheduleEvent(event)
        FirestoreManager.saveScheduleEvent(event)
    }

    suspend fun addScheduleEvents(events: List<ScheduleEventEntity>) = withContext(Dispatchers.IO) {
        dao.insertScheduleEvents(events)
        FirestoreManager.bulkSaveSchedules(events)
    }

    suspend fun bulkUploadSchedules(
        events: List<ScheduleEventEntity>,
        adminEmail: String = "admin@lsc.org"
    ): ScheduleBulkUploadResult {
        return ScheduleBulkUploadService.bulkUploadSchedules(events, adminEmail, dao)
    }

    suspend fun importParticipantsFromCsvJson(
        jsonContent: String,
        adminEmail: String = "admin@lsc.org"
    ): ParticipantImportResult {
        return ParticipantImportService.importRosterToFirestore(jsonContent, adminEmail, dao)
    }

    suspend fun deleteScheduleEvent(event: ScheduleEventEntity) = withContext(Dispatchers.IO) {
        dao.deleteScheduleEvent(event)
        FirestoreManager.deleteScheduleEvent(event.id)
    }

    suspend fun addRoom(room: RoomEntity) = withContext(Dispatchers.IO) {
        dao.insertRoom(room)
    }

    suspend fun updateRoom(room: RoomEntity) = withContext(Dispatchers.IO) {
        dao.updateRoom(room)
    }

    suspend fun deleteRoom(room: RoomEntity) = withContext(Dispatchers.IO) {
        dao.deleteRoom(room)
    }

    suspend fun addBulletinNotice(bulletin: BulletinEntity) = withContext(Dispatchers.IO) {
        dao.insertBulletin(bulletin)
    }

    suspend fun updateBulletinNotice(bulletin: BulletinEntity) = withContext(Dispatchers.IO) {
        dao.updateBulletin(bulletin)
    }

    suspend fun deleteBulletinNotice(bulletin: BulletinEntity) = withContext(Dispatchers.IO) {
        dao.deleteBulletin(bulletin)
    }

    suspend fun deleteIncidentReport(incident: IncidentEntity) = withContext(Dispatchers.IO) {
        dao.deleteIncident(incident)
    }

    suspend fun updateVehicle(vehicle: TravelVehicleEntity) = withContext(Dispatchers.IO) {
        dao.updateVehicle(vehicle)
    }

    suspend fun updateDutyRoster(roster: DutyRosterEntity) = withContext(Dispatchers.IO) {
        dao.updateDutyRoster(roster)
    }

    // Volunteer Shifts Operations
    suspend fun confirmShiftAttendance(shiftId: Long) = withContext(Dispatchers.IO) {
        dao.confirmShiftAttendance(shiftId)
    }

    suspend fun checkInShift(shiftId: Long) = withContext(Dispatchers.IO) {
        dao.checkInShift(shiftId)
    }

    suspend fun addVolunteerShift(shift: VolunteerShiftEntity) = withContext(Dispatchers.IO) {
        dao.insertVolunteerShift(shift)
    }

    suspend fun updateVolunteerShift(shift: VolunteerShiftEntity) = withContext(Dispatchers.IO) {
        dao.updateVolunteerShift(shift)
    }

    suspend fun updateKitDistribution(kit: KitDistributionEntity) = withContext(Dispatchers.IO) {
        dao.updateKitDistribution(kit)
    }

    suspend fun updateGoalMetric(goal: CampGoalMetricEntity) = withContext(Dispatchers.IO) {
        dao.updateGoalMetric(goal)
    }

    suspend fun insertAuditLog(
        actionType: String,
        targetEntity: String,
        description: String,
        userEmail: String,
        adminId: String = "",
        modifiedRecordDetails: String = ""
    ) = withContext(Dispatchers.IO) {
        val entity = AuditLogEntity(
            timestamp = System.currentTimeMillis(),
            adminId = adminId,
            userEmail = userEmail,
            actionType = actionType,
            targetEntity = targetEntity,
            description = description,
            modifiedRecordDetails = modifiedRecordDetails
        )
        val generatedId = dao.insertAuditLog(entity)
        FirestoreManager.saveAuditLog(entity.copy(id = generatedId))
    }

    suspend fun ensureStaffAccountsSeeded() = withContext(Dispatchers.IO) {
        if (dao.getUserAccountCount() == 0) {
            dao.insertUserAccounts(SeedData.staffAccounts)
        } else if (dao.getUserAccount("stationmanager@lsc.org") == null) {
            // Ensure new 2026 official roles (e.g., Station Manager) exist without overwriting user-created passwords
            for (staff in SeedData.staffAccounts) {
                if (dao.getUserAccount(staff.email) == null) {
                    dao.insertUserAccount(staff)
                }
            }
        }
    }

    suspend fun getAllUserAccountsList(): List<UserAccountEntity> = withContext(Dispatchers.IO) {
        ensureStaffAccountsSeeded()
        dao.getAllUserAccountsList()
    }

    suspend fun updateUserLastLogin(email: String, lastLoginTime: Long = System.currentTimeMillis()) = withContext(Dispatchers.IO) {
        dao.updateUserLastLogin(email.trim().lowercase(), lastLoginTime)
    }

    suspend fun authenticateUser(email: String, pinOrPass: String): UserAccountEntity? = withContext(Dispatchers.IO) {
        ensureStaffAccountsSeeded()
        val cleanEmail = email.trim().lowercase()
        val cleanPass = pinOrPass.trim()
        if (cleanEmail.isEmpty() || cleanPass.isEmpty()) return@withContext null

        val user = dao.getUserAccount(cleanEmail)
            ?: SeedData.staffAccounts.find { it.email.equals(cleanEmail, ignoreCase = true) }?.also {
                dao.insertUserAccount(it)
            }
            ?: return@withContext null

        if (!user.isActive) return@withContext null

        if (verifyPassword(cleanPass, user.passwordHash)) {
            val now = System.currentTimeMillis()
            dao.updateUserLastLogin(user.email, now)
            dao.insertAuditLog(
                AuditLogEntity(
                    userEmail = user.email,
                    actionType = "LOGIN",
                    targetEntity = "UserAccount",
                    description = "সফল ইমেইল লগইন সম্পন্ন: ${user.fullName} (${user.role})"
                )
            )
            user.copy(lastLoginTime = now)
        } else {
            null
        }
    }

    suspend fun signUpUser(
        fullName: String,
        email: String,
        role: String,
        phone: String = "",
        customPassword: String = ""
    ): Pair<UserAccountEntity, String> = withContext(Dispatchers.IO) {
        ensureStaffAccountsSeeded()
        val cleanEmail = email.trim().lowercase()
        val cleanName = fullName.trim()
        val cleanPhone = phone.trim()
        val cleanCustomPass = customPassword.trim()

        val randomDigits = (1000..9999).random()
        val effectivePassword = if (cleanCustomPass.length >= 4) cleanCustomPass else "Lsc#$randomDigits"
        val hashedPassword = hashPasswordWithSalt(effectivePassword)

        val existing = dao.getUserAccount(cleanEmail)
        val now = System.currentTimeMillis()
        val normalizedRole = role.trim().uppercase().ifBlank { "VOLUNTEER" }
        val user = if (existing != null) {
            existing.copy(
                fullName = if (cleanName.isNotBlank()) cleanName else existing.fullName,
                role = normalizedRole,
                passwordHash = hashedPassword,
                phone = if (cleanPhone.isNotBlank()) cleanPhone else existing.phone,
                isActive = true,
                lastLoginTime = now
            )
        } else {
            UserAccountEntity(
                email = cleanEmail,
                fullName = cleanName,
                role = normalizedRole,
                passwordHash = hashedPassword,
                phone = cleanPhone,
                isActive = true,
                lastLoginTime = now
            )
        }

        dao.insertUserAccount(user)
        dao.insertAuditLog(
            AuditLogEntity(
                userEmail = cleanEmail,
                actionType = "SIGN_UP",
                targetEntity = "UserAccount",
                description = "নতুন অ্যাকাউন্ট সাইন আপ: $cleanName ($normalizedRole), ইমেলে পাসওয়ার্ড প্রেরণ প্রক্রিয়া সম্পন্ন"
            )
        )
        Pair(user, effectivePassword)
    }

    suspend fun insertUserAccount(user: UserAccountEntity) = withContext(Dispatchers.IO) {
        dao.insertUserAccount(user)
    }

    suspend fun requestPasswordResetToEmail(email: String): Pair<UserAccountEntity, String>? = withContext(Dispatchers.IO) {
        ensureStaffAccountsSeeded()
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isEmpty()) return@withContext null

        val user = dao.getUserAccount(cleanEmail)
            ?: SeedData.staffAccounts.find { it.email.equals(cleanEmail, ignoreCase = true) }?.also {
                dao.insertUserAccount(it)
            }
            ?: return@withContext null

        val randomDigits = (1000..9999).random()
        val newPassword = "Lsc#$randomDigits"
        val hashedPassword = hashPasswordWithSalt(newPassword)
        val updatedUser = user.copy(passwordHash = hashedPassword, isActive = true)

        dao.insertUserAccount(updatedUser)
        dao.insertAuditLog(
            AuditLogEntity(
                userEmail = cleanEmail,
                actionType = "PASSWORD_RESET",
                targetEntity = "UserAccount",
                description = "ইমেলে পাসওয়ার্ড রিসেট ও পুনর্প্রেরণ সম্পন্ন: ${user.fullName}"
            )
        )
        Pair(updatedUser, newPassword)
    }

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        seedMutex.withLock {
            ensureStaffAccountsSeeded()
            val count = dao.getParticipantCount()
            val hasLegacyDummyRoster = count > 0 && dao.getParticipantByCampCode("LSC-2026-001") != null
            if (count == 0 || hasLegacyDummyRoster) {
                if (hasLegacyDummyRoster) {
                    dao.clearLegacySeedTables()
                }
                val pList = SeedData.generateParticipants()
                val p1 = pList.firstOrNull()
                val p2 = pList.getOrNull(125) ?: p1
                val p3 = pList.getOrNull(370) ?: p1
                val p4 = pList.getOrNull(460) ?: p1

                val healthSamples = listOfNotNull(
                    p1?.let {
                        HealthRecordEntity(
                            participantId = it.id,
                            campCode = it.campCode,
                            participantName = it.nameBn,
                            groupName = it.groupNameBn,
                            checkupType = "GENERAL",
                            doctorName = "ডা. ওরাকাতুল জান্নাত",
                            vitals = "BP: 110/70, Temp: 98.4°F, Pulse: 78",
                            diagnosis = "শারীরিক গঠন স্বাভাবিক ও প্রাণবন্ত। কোনো জটিলতা নেই।",
                            adviceAndMeds = "পর্যাপ্ত পানি পান ও হাইড্রেটেড থাকার পরামর্শ।",
                            heightCm = "148.0",
                            weightKg = "41.5",
                            photoDocumented = true
                        )
                    },
                    p2?.let {
                        HealthRecordEntity(
                            participantId = it.id,
                            campCode = it.campCode,
                            participantName = it.nameBn,
                            groupName = it.groupNameBn,
                            checkupType = "DENTAL",
                            doctorName = "ভ্রাম্যমাণ ডেন্টাল বাস বিশেষজ্ঞ টিম",
                            vitals = "দাঁতের মাড়ি স্বাভাবিক",
                            diagnosis = "প্রাথমিক ক্যারিজ শনাক্ত — স্কেলিং ও পেস্ট পরামর্শ",
                            adviceAndMeds = "প্রতিদিন দুইবার ফ্লোরাইড পেস্ট দিয়ে ব্রাশ করার নির্দেশ ও লিফলেট প্রদান।",
                            dentalBusReferred = true,
                            photoDocumented = true
                        )
                    },
                    p3?.let {
                        HealthRecordEntity(
                            participantId = it.id,
                            campCode = it.campCode,
                            participantName = it.nameBn,
                            groupName = it.groupNameBn,
                            checkupType = "EYE",
                            doctorName = "চক্ষু বিশেষজ্ঞ টিম (স্টেশন-৬ আই ক্যাম্প)",
                            vitals = "দৃষ্টিশক্তি: 6/6 (উভয় চোখ)",
                            diagnosis = "সামান্য চোখের শুষ্কতা (Dry Eyes)",
                            adviceAndMeds = "লুব্রিকেটিং আই ড্রপ দিনে ২ বার।",
                            visionNotes = "6/6 Normal",
                            photoDocumented = true
                        )
                    },
                    p4?.let {
                        HealthRecordEntity(
                            participantId = it.id,
                            campCode = it.campCode,
                            participantName = it.nameBn,
                            groupName = it.groupNameBn,
                            checkupType = "EMERGENCY",
                            doctorName = "ডা. ওরাকাতুল জান্নাত",
                            vitals = "BP: 100/65, Temp: 98.8°F, Pulse: 86",
                            diagnosis = "সকালের ঠান্ডায় মৃদু শ্বাসকষ্ট (Mild Seasonal Asthma)",
                            adviceAndMeds = "ইনহেলার ২ চাপ দেওয়া হয়েছে ও পর্যবেক্ষণে রাখা হয়েছে।",
                            isEmergencyFlag = true,
                            priority = "HIGH",
                            status = "PENDING",
                            followUpDate = "২৪ অক্টোবর সকাল ০৯:০০"
                        )
                    }
                )

                val scoreSamples = listOfNotNull(
                    p1?.let {
                        CompetitionScoreEntity(
                            competitionId = 1,
                            participantId = it.id,
                            campCode = it.campCode,
                            participantName = it.nameBn,
                            groupName = it.groupNameBn,
                            subgroup = it.subgroup,
                            accuracyScore = 39.0,
                            presentationScore = 29.0,
                            disciplineScore = 28.0,
                            totalScore = 96.0,
                            remarks = "🥇 ১ম স্থান (${it.subgroup}) — বিশুদ্ধ তাজবীদ ও সুন্দর তিলাওয়াত",
                            rank = 1,
                            isAwardEligible = true,
                            isPrizeHandedOver = true
                        )
                    },
                    p3?.let {
                        CompetitionScoreEntity(
                            competitionId = 1,
                            participantId = it.id,
                            campCode = it.campCode,
                            participantName = it.nameBn,
                            groupName = it.groupNameBn,
                            subgroup = it.subgroup,
                            accuracyScore = 38.0,
                            presentationScore = 28.5,
                            disciplineScore = 28.0,
                            totalScore = 94.5,
                            remarks = "🥇 ১ম স্থান (${it.subgroup}) — মাখরাজ স্পষ্ট ও সাবলীল উপস্থাপনা",
                            rank = 1,
                            isAwardEligible = true,
                            isPrizeHandedOver = true
                        )
                    },
                    p2?.let {
                        CompetitionScoreEntity(
                            competitionId = 2,
                            participantId = it.id,
                            campCode = it.campCode,
                            participantName = it.nameBn,
                            groupName = it.groupNameBn,
                            subgroup = it.subgroup,
                            accuracyScore = 37.0,
                            presentationScore = 27.5,
                            disciplineScore = 28.0,
                            totalScore = 92.5,
                            remarks = "🥈 ২য় স্থান (${it.subgroup}) — হাদিসের অর্থ নির্ভুল",
                            rank = 2,
                            isAwardEligible = true,
                            isPrizeHandedOver = false
                        )
                    }
                )

                dao.seedDatabase(
                    groups = SeedData.groups,
                    stations = SeedData.stations,
                    participants = pList,
                    schedules = SeedData.scheduleEvents,
                    attendanceEvents = SeedData.attendanceEvents,
                    competitions = SeedData.competitions,
                    inventoryItems = SeedData.inventoryItems,
                    bulletins = SeedData.bulletins,
                    healthRecords = healthSamples,
                    competitionScores = scoreSamples,
                    judges = SeedData.judges,
                    rooms = SeedData.rooms,
                    vehicles = SeedData.travelVehicles,
                    dutyRosters = SeedData.dutyRosters,
                    kitDistributions = SeedData.generateKitDistributions(pList),
                    goalMetrics = SeedData.campGoals,
                    users = SeedData.staffAccounts,
                    volunteerShifts = SeedData.volunteerShifts
                )
            }

            if (healthDao != null && healthDao.getHealthStatusCount() == 0) {
                val participantsList = dao.getAllParticipantsList().ifEmpty { SeedData.generateParticipants() }
                val bloodGroups = listOf("A+", "B+", "O+", "AB+", "A-", "B-", "O-")
                val healthEntities = participantsList.mapIndexed { idx, p ->
                    val condition = when {
                        idx % 67 == 0 -> "Requires Attention"
                        idx % 31 == 0 -> "Needs Rest"
                        idx % 19 == 0 -> "Mild Symptoms"
                        else -> "Normal"
                    }
                    val isCheckedIn = idx % 12 != 0
                    val hasAllergy = idx % 29 == 0 || idx % 43 == 0
                    val allergyDesc = if (hasAllergy) {
                        when (idx % 3) {
                            0 -> "ধুলোবালি ও কোল্ড অ্যালার্জি (Dust & Cold)"
                            1 -> "চিংড়ি ও সামুদ্রিক মাছ অ্যালার্জি (Prawns)"
                            else -> "পরাগরেণু ও সাইনাস (Pollen / Sinus)"
                        }
                    } else ""
                    val hasMed = idx % 37 == 0
                    val medDesc = if (hasMed) "সালবুটামল ইনহেলার / এন্টিহিস্টামিন" else ""
                    ParticipantHealthStatusEntity(
                        participantId = p.id.takeIf { it > 0 } ?: (idx + 1).toLong(),
                        campCode = p.campCode,
                        participantName = p.nameBn,
                        medicationNeeds = medDesc,
                        allergies = allergyDesc,
                        dailyCheckInLogs = if (isCheckedIn) "2026-10-22 09:30 - ক্যাম্প মেডিকেল চেক-ইন সম্পন্ন (অবস্থা: $condition)" else "",
                        hasMedicationNeeds = hasMed,
                        hasAllergies = hasAllergy,
                        isDailyCheckedIn = isCheckedIn,
                        lastCheckInDate = if (isCheckedIn) "2026-10-22" else "",
                        lastCheckInTime = if (isCheckedIn) System.currentTimeMillis() - (idx * 60000L) else 0L,
                        vitalsSummary = "BP: ${105 + (idx % 12)}/${68 + (idx % 10)}, SpO2: ${98 + (idx % 2)}%",
                        healthCondition = condition,
                        bloodGroup = bloodGroups[idx % bloodGroups.size],
                        emergencyContactName = p.guardianName.ifBlank { "সাব-গ্রুপ ইনচার্জ" },
                        emergencyContactPhone = p.guardianPhone.ifBlank { "01711-260100" }
                    )
                }
                healthDao.insertHealthStatuses(healthEntities)
            }
        }
    }

    // Participant Health Status
    fun observeParticipantHealthStatus(participantId: Long): Flow<ParticipantHealthStatusEntity?> {
        return healthDao?.observeHealthStatusByParticipantId(participantId) ?: flowOf(null)
    }

    suspend fun getParticipantHealthStatus(participantId: Long): ParticipantHealthStatusEntity? {
        return withContext(Dispatchers.IO) {
            healthDao?.getHealthStatusByParticipantId(participantId)
        }
    }

    suspend fun saveParticipantHealthStatus(healthStatus: ParticipantHealthStatusEntity): Long {
        return withContext(Dispatchers.IO) {
            healthDao?.insertHealthStatus(healthStatus) ?: -1L
        }
    }

    suspend fun logDailyHealthCheckIn(
        participantId: Long,
        logEntry: String,
        isCheckedIn: Boolean,
        checkInDate: String
    ) {
        withContext(Dispatchers.IO) {
            healthDao?.appendDailyCheckInLog(
                participantId = participantId,
                logEntry = logEntry,
                isCheckedIn = isCheckedIn,
                checkInDate = checkInDate,
                checkInTime = System.currentTimeMillis()
            )
        }
    }

    suspend fun recordDailyHealthCheckIn(
        participant: ParticipantEntity,
        status: String,
        notes: String = "",
        checkInDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
        isOfflineMode: Boolean = false
    ): ParticipantHealthStatusEntity = withContext(Dispatchers.IO) {
        val existing = healthDao?.getHealthStatusByParticipantId(participant.id)
        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val formattedLog = if (notes.isNotBlank()) "[$checkInDate $timeStr] $status - $notes" else "[$checkInDate $timeStr] $status"
        val condition = when {
            status.contains("rest", ignoreCase = true) || status.contains("বিশ্রাম", ignoreCase = true) -> "Needs Rest"
            status.contains("doctor", ignoreCase = true) || status.contains("ডাক্তার", ignoreCase = true) -> "Requires Attention"
            else -> "Normal"
        }
        val entityToSave = if (existing != null) {
            val updatedLogs = if (existing.dailyCheckInLogs.isBlank()) formattedLog else "${existing.dailyCheckInLogs}\n$formattedLog"
            existing.copy(
                dailyCheckInLogs = updatedLogs,
                isDailyCheckedIn = true,
                lastCheckInDate = checkInDate,
                lastCheckInTime = System.currentTimeMillis(),
                healthCondition = condition,
                updatedAt = System.currentTimeMillis()
            )
        } else {
            ParticipantHealthStatusEntity(
                participantId = participant.id,
                campCode = participant.campCode,
                participantName = participant.nameBn,
                medicationNeeds = "",
                allergies = participant.medicalFlag ?: "",
                dailyCheckInLogs = formattedLog,
                hasMedicationNeeds = false,
                hasAllergies = !participant.medicalFlag.isNullOrBlank(),
                isDailyCheckedIn = true,
                lastCheckInDate = checkInDate,
                lastCheckInTime = System.currentTimeMillis(),
                healthCondition = condition
            )
        }
        healthDao?.insertHealthStatus(entityToSave)

        if (isOfflineMode) {
            val statusBn = when (status) {
                "Feeling well" -> "সুস্থ ও স্বাভাবিক"
                "Needs rest" -> "বিশ্রাম প্রয়োজন"
                else -> status
            }
            dao.insertCachedSyncItem(
                CachedSyncItemEntity(
                    itemType = CachedSyncItemEntity.TYPE_HEALTH_LOG,
                    participantId = participant.id,
                    campCode = participant.campCode,
                    participantName = participant.nameBn,
                    summary = "দৈনিক স্বাস্থ্য চেক-ইন: $statusBn ${if (notes.isNotBlank()) "($notes)" else ""}",
                    payloadJson = com.example.data.util.MoshiJsonUtils.toJson(
                        mapOf(
                            "status" to status,
                            "notes" to notes,
                            "date" to checkInDate
                        )
                    ),
                    status = CachedSyncItemEntity.STATUS_PENDING
                )
            )
        }

        entityToSave
    }

    data class SyncResult(
        val totalSynced: Int,
        val attendanceSynced: Int,
        val healthLogsSynced: Int,
        val message: String
    )

    suspend fun syncPendingCachedItems(): SyncResult = withContext(Dispatchers.IO) {
        val pendingItems = dao.getPendingCachedSyncItems()
        val unsyncedAttendance = dao.getUnsyncedAttendanceRecords()

        val attendanceCount = pendingItems.count { it.itemType == CachedSyncItemEntity.TYPE_ATTENDANCE }.coerceAtLeast(unsyncedAttendance.size)
        val healthCount = pendingItems.count { it.itemType == CachedSyncItemEntity.TYPE_HEALTH_LOG || it.itemType == CachedSyncItemEntity.TYPE_HEALTH_CHECKUP }
        val totalCount = pendingItems.size.coerceAtLeast(unsyncedAttendance.size)

        val syncManager = com.example.data.sync.DataSyncManager.getInstance(this@CampRepository, dao)
        val (syncedIncidents, syncedHealth) = syncManager.processPendingOfflineQueue()

        val now = System.currentTimeMillis()
        dao.markAllPendingCachedItemsSynced(now)
        dao.markAllAttendanceRecordsSynced()

        val totalProcessed = totalCount + syncedIncidents + syncedHealth

        SyncResult(
            totalSynced = totalProcessed,
            attendanceSynced = attendanceCount,
            healthLogsSynced = healthCount + syncedHealth,
            message = if (totalProcessed > 0)
                "সফলভাবে $attendanceCount টি হাজিরা ও ${healthCount + syncedHealth} টি স্বাস্থ্য/ইনসিডেন্স রেকর্ড সার্ভারে সিঙ্ক হয়েছে!"
            else
                "সকল ডেটা আপ-টু-ডেট রয়েছে (All records synchronized)"
        )
    }

    suspend fun getPendingCachedSyncCount(): Int = withContext(Dispatchers.IO) {
        dao.getPendingCachedSyncCount()
    }

    suspend fun clearSyncedCacheHistory(): Int = withContext(Dispatchers.IO) {
        dao.clearSyncedCacheHistory()
    }

    companion object {
        @Volatile
        private var INSTANCE: CampRepository? = null

        const val PASSCODE_SALT = "lsc-camp-companion-v1"

        fun hashPasswordWithSalt(password: String, salt: String = PASSCODE_SALT): String {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val saltedBytes = digest.digest((salt + password).toByteArray(Charsets.UTF_8))
            return saltedBytes.joinToString("") { "%02x".format(it) }
        }

        fun constantTimeEquals(a: String?, b: String?): Boolean {
            if (a == null || b == null) return false
            if (a.length != b.length) return false
            var result = 0
            for (i in a.indices) result = result or (a[i].code xor b[i].code)
            return result == 0
        }

        fun verifyPassword(inputPassword: String, storedHashOrPlain: String, salt: String = PASSCODE_SALT): Boolean {
            val cleanInput = inputPassword.trim()
            val cleanStored = storedHashOrPlain.trim()
            if (cleanInput.isEmpty() || cleanStored.isEmpty()) return false

            val inputHash = hashPasswordWithSalt(cleanInput, salt)
            return constantTimeEquals(inputHash, cleanStored) || constantTimeEquals(cleanInput, cleanStored)
        }

        fun resetInstance() {
            synchronized(this) {
                INSTANCE = null
            }
        }

        fun getInstance(context: Context): CampRepository {
            return INSTANCE ?: synchronized(this) {
                val db = CampDatabase.getDatabase(context)
                val instance = CampRepository(
                    dao = db.campDao(),
                    healthDao = db.participantHealthDao(),
                    participantDao = db.participantDao(),
                    stationDao = db.stationDao()
                )
                INSTANCE = instance
                instance
            }
        }
    }
}
