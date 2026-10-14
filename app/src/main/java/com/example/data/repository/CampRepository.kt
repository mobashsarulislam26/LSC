package com.example.data.repository

import android.content.Context
import com.example.data.local.CampDao
import com.example.data.local.CampDatabase
import com.example.data.local.ParticipantHealthDao
import com.example.data.model.*
import com.example.data.sample.SeedData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

class CampRepository(
    private val dao: CampDao,
    private val healthDao: ParticipantHealthDao? = null
) {
    val participants: Flow<List<ParticipantEntity>> = dao.getAllParticipants()
    val participantCount: Flow<Int> = dao.observeParticipantCount()
    val healthStatuses: Flow<List<ParticipantHealthStatusEntity>> =
        healthDao?.getAllHealthStatuses() ?: flowOf(emptyList())
    val groups: Flow<List<CampGroupEntity>> = dao.getAllGroups()
    val stations: Flow<List<StationEntity>> = dao.getAllStations()
    val schedule: Flow<List<ScheduleEventEntity>> = dao.getAllSchedule()
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
    val kitDistributions: Flow<List<KitDistributionEntity>> = dao.getAllKitDistributions()
    val goalMetrics: Flow<List<CampGoalMetricEntity>> = dao.getAllGoalMetrics()
    val mealDistributions: Flow<List<MealDistributionEntity>> = dao.getAllMealDistributions()
    val userAccounts: Flow<List<UserAccountEntity>> = dao.getAllUserAccounts()
    val auditLogs: Flow<List<AuditLogEntity>> = dao.getAllAuditLogs()
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
            dao.getParticipantByCampCode(code)
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
                    payloadJson = """{"eventId":$eventId,"status":"$status","method":"$method","reason":"${reason ?: ""}"}""",
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
        // Award points to group
        val group = dao.getParticipantById(score.participantId)?.groupId
        if (group != null) {
            dao.addGroupPoints(group, (score.totalScore / 10).toInt())
        }
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
        dao.insertBulletin(bulletin)
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
                    payloadJson = """{"checkupType":"${record.checkupType}","doctor":"${record.doctorName}","diagnosis":"${record.diagnosis}"}""",
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

        val updated = item.copy(
            issuedQty = item.issuedQty + qty,
            balanceQty = item.openingQty + item.receivedQty - item.issuedQty
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
        dao.insertIncident(incident)
    }

    suspend fun updateIncident(incident: IncidentEntity) = withContext(Dispatchers.IO) {
        dao.updateIncident(incident)
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
                val code = if (cols[0].startsWith("LSC")) cols[0] else "LSC-2026-%03d".format(604 + index)
                val fileNo = if (cols.size > 1 && cols[1].startsWith("F-")) cols[1] else "F-%04d".format(1604 + index)
                val name = cols.getOrElse(2) { "New Participant" }
                val district = cols.getOrElse(3) { "ঢাকা" }
                val group = cols.getOrElse(4) { "বুখারী" }
                val sub = cols.getOrElse(5) { "Bukhari-1" }
                val p = ParticipantEntity(
                    campCode = code,
                    fileNo = fileNo,
                    nameBn = name,
                    nameEn = name,
                    districtBn = district,
                    districtEn = district,
                    gender = "Male",
                    age = 13,
                    groupId = "bukhari",
                    groupNameBn = group,
                    groupNameEn = "Bukhari",
                    subgroup = sub,
                    roomNo = "Block A Room 101",
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
        dao.updateStation(station)
    }

    suspend fun updateRoom(room: RoomEntity) = withContext(Dispatchers.IO) {
        dao.updateRoom(room)
    }

    suspend fun updateVehicle(vehicle: TravelVehicleEntity) = withContext(Dispatchers.IO) {
        dao.updateVehicle(vehicle)
    }

    suspend fun updateDutyRoster(roster: DutyRosterEntity) = withContext(Dispatchers.IO) {
        dao.updateDutyRoster(roster)
    }

    suspend fun updateKitDistribution(kit: KitDistributionEntity) = withContext(Dispatchers.IO) {
        dao.updateKitDistribution(kit)
    }

    suspend fun updateGoalMetric(goal: CampGoalMetricEntity) = withContext(Dispatchers.IO) {
        dao.updateGoalMetric(goal)
    }

    suspend fun insertAuditLog(actionType: String, targetEntity: String, description: String, userEmail: String) = withContext(Dispatchers.IO) {
        dao.insertAuditLog(
            AuditLogEntity(
                userEmail = userEmail,
                actionType = actionType,
                targetEntity = targetEntity,
                description = description
            )
        )
    }

    suspend fun authenticateUser(email: String, pinOrPass: String): UserAccountEntity? = withContext(Dispatchers.IO) {
        val user = dao.getUserAccount(email.trim().lowercase()) ?: return@withContext null
        if (constantTimeEquals(user.passwordHash, pinOrPass.trim())) {
            dao.insertAuditLog(
                AuditLogEntity(
                    userEmail = user.email,
                    actionType = "LOGIN",
                    targetEntity = "UserAccount",
                    description = "সফল ইমেইল লগইন সম্পন্ন: ${user.fullName} (${user.role})"
                )
            )
            user
        } else {
            null
        }
    }

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        val count = dao.getParticipantCount()
        if (count == 0) {
            val pList = SeedData.generateParticipants()
            val healthSamples = listOf(
                HealthRecordEntity(
                    participantId = 1,
                    campCode = "LSC-2026-001",
                    participantName = "আব্দুল্লাহ আল মামুন",
                    groupName = "বুখারী",
                    checkupType = "GENERAL",
                    doctorName = "ডা. তারিকুল ইসলাম",
                    vitals = "BP: 110/70, Temp: 98.4°F, Pulse: 78",
                    diagnosis = "শারীরিক গঠন স্বাভাবিক ও প্রাণবন্ত। কোনো জটিলতা নেই।",
                    adviceAndMeds = "পর্যাপ্ত পানি পান ও হাইড্রেটেড থাকার পরামর্শ।"
                ),
                HealthRecordEntity(
                    participantId = 29,
                    campCode = "LSC-2026-029",
                    participantName = "জুনায়েদ হোসাইন",
                    groupName = "বুখারী",
                    checkupType = "EMERGENCY",
                    doctorName = "ডা. ফারহানা চৌধুরী",
                    vitals = "BP: 100/65, Temp: 98.8°F, Pulse: 84",
                    diagnosis = "মৃদু শ্বাসকষ্ট (Mild Asthma Symptoms due to cold morning air)",
                    adviceAndMeds = "সালবুটামল ইনহেলার ২ চাপ দেওয়া হয়েছে। ধুলোবালি পরিহারের পরামর্শ।",
                    isEmergencyFlag = true,
                    priority = "HIGH",
                    status = "PENDING",
                    followUpDate = "২৮ সেপ্টেম্বর সকাল ১০:০০"
                ),
                HealthRecordEntity(
                    participantId = 15,
                    campCode = "LSC-2026-015",
                    participantName = "মরিয়ম আক্তার",
                    groupName = "সুমাইয়া",
                    checkupType = "DENTAL",
                    doctorName = "ডা. সাইদুর রহমান (বিডিএস)",
                    vitals = "দাঁতের মাড়ি সুস্থ",
                    diagnosis = "প্রাথমিক ক্যারিজ (Dental Caries) শনাক্ত",
                    adviceAndMeds = "প্রতিদিন দুইবার ব্রাশ করার নির্দেশ ও মেডিকেটেড পেস্ট।"
                ),
                HealthRecordEntity(
                    participantId = 53,
                    campCode = "LSC-2026-053",
                    participantName = "সাকিব চৌধুরী",
                    groupName = "শাফেয়ী",
                    checkupType = "EYE",
                    doctorName = "ডা. মোস্তাফিজুর রহমান (চক্ষু বিশেষজ্ঞ)",
                    vitals = "দৃষ্টিশক্তি স্বাভাবিক (6/6)",
                    diagnosis = "সামান্য চোখের শুষ্কতা (Dry Eyes)",
                    adviceAndMeds = "লুব্রিকেটিং আই ড্রপ দিনে ২ বার।"
                )
            )

            val scoreSamples = listOf(
                CompetitionScoreEntity(
                    competitionId = 1,
                    participantId = 1,
                    campCode = "LSC-2026-001",
                    participantName = "আব্দুল্লাহ আল মামুন",
                    groupName = "বুখারী",
                    subgroup = "Bukhari-1",
                    accuracyScore = 38.5,
                    presentationScore = 28.0,
                    disciplineScore = 29.0,
                    totalScore = 95.5,
                    remarks = "অসাধারণ বিশুদ্ধ উচ্চারণ ও সনদসহ মুখস্থ।",
                    rank = 1,
                    isAwardEligible = true
                ),
                CompetitionScoreEntity(
                    competitionId = 1,
                    participantId = 15,
                    campCode = "LSC-2026-015",
                    participantName = "মরিয়ম আক্তার",
                    groupName = "সুমাইয়া",
                    subgroup = "Sumaiya-2",
                    accuracyScore = 37.0,
                    presentationScore = 28.5,
                    disciplineScore = 28.0,
                    totalScore = 93.5,
                    remarks = "মাখরাজ স্পষ্ট ও সাবলীল উপস্থাপনা।",
                    rank = 2,
                    isAwardEligible = true
                ),
                CompetitionScoreEntity(
                    competitionId = 1,
                    participantId = 65,
                    campCode = "LSC-2026-065",
                    participantName = "তাহমিদ ইসলাম",
                    groupName = "সালাহউদ্দীন আইয়ুবী",
                    subgroup = "Salahuddin-3",
                    accuracyScore = 36.0,
                    presentationScore = 27.5,
                    disciplineScore = 28.0,
                    totalScore = 91.5,
                    remarks = "হাদিসের নম্বর ও রাবীর নাম নির্ভুল।",
                    rank = 3,
                    isAwardEligible = true
                )
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
                users = SeedData.staffAccounts
            )
        }

        if (healthDao != null && healthDao.getHealthStatusCount() == 0) {
            val participantsList = dao.getAllParticipantsList().ifEmpty { SeedData.generateParticipants() }
            val bloodGroups = listOf("A+", "B+", "O+", "AB+", "A-", "B-", "O-")
            val healthEntities = participantsList.mapIndexed { idx, p ->
                val condition = when {
                    idx % 37 == 0 -> "Critical"
                    idx % 23 == 0 -> "Requires Attention"
                    idx % 11 == 0 -> "Needs Rest"
                    idx % 7 == 0 -> "Mild Symptoms"
                    else -> "Normal"
                }
                val isCheckedIn = idx % 9 != 0
                val hasAllergy = idx % 19 == 0 || idx % 41 == 0
                val allergyDesc = if (hasAllergy) {
                    when (idx % 4) {
                        0 -> "ধুলোবালি ও কোল্ড অ্যালার্জি (Dust & Cold)"
                        1 -> "চিংড়ি ও সামুদ্রিক মাছ অ্যালার্জি (Prawns)"
                        2 -> "পেনিসিলিন অ্যালার্জি (Penicillin)"
                        else -> "পরাগরেণু ও সাইনাস (Pollen / Sinus)"
                    }
                } else ""
                val hasMed = idx % 17 == 0 || idx % 29 == 0
                val medDesc = if (hasMed) {
                    when (idx % 3) {
                        0 -> "সালবুটামল ইনহেলার (Salbutamol Inhaler twice daily)"
                        1 -> "লুব্রিকেটিং চোখের ড্রপ (Eye drops)"
                        else -> "এন্টিহিস্টামিন ও প্যারাসিটামল (Antihistamine)"
                    }
                } else ""
                ParticipantHealthStatusEntity(
                    participantId = p.id.takeIf { it > 0 } ?: (idx + 1).toLong(),
                    campCode = p.campCode,
                    participantName = p.nameBn,
                    medicationNeeds = medDesc,
                    allergies = allergyDesc,
                    dailyCheckInLogs = if (isCheckedIn) "2026-10-13 07:30 - সকালের চেক-ইন সম্পন্ন (অবস্থা: $condition)" else "",
                    hasMedicationNeeds = hasMed,
                    hasAllergies = hasAllergy,
                    isDailyCheckedIn = isCheckedIn,
                    lastCheckInDate = if (isCheckedIn) "2026-10-13" else "",
                    lastCheckInTime = if (isCheckedIn) System.currentTimeMillis() - (idx * 120000L) else 0L,
                    vitalsSummary = "BP: ${110 + (idx % 15)}/${70 + (idx % 10)}, SpO2: ${98 + (idx % 3)}%",
                    healthCondition = condition,
                    bloodGroup = bloodGroups[idx % bloodGroups.size],
                    emergencyContactName = p.guardianName.ifBlank { "অভিভাবক" },
                    emergencyContactPhone = p.guardianPhone.ifBlank { "01711-000000" }
                )
            }
            healthDao.insertHealthStatuses(healthEntities)
        }

        // Seed sample meal claims for day 1 lunch if empty (538 out of 603 claimed, 65 remaining)
        val lunchClaimCheck = dao.hasClaimedMeal(1, "LUNCH", 1)
        if (lunchClaimCheck == 0) {
            val participantsList = dao.getAllParticipantsList()
            if (participantsList.isNotEmpty()) {
                val lunchClaims = participantsList.take(538).mapIndexed { idx, p ->
                    MealDistributionEntity(
                        dayNo = 1,
                        mealType = "LUNCH",
                        participantId = p.id,
                        campCode = p.campCode,
                        participantName = p.nameBn,
                        claimedAt = System.currentTimeMillis() - ((538 - idx) * 15000L)
                    )
                }
                dao.insertMealClaims(lunchClaims)
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
                    payloadJson = """{"status":"$status","notes":"$notes","date":"$checkInDate"}""",
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

        val now = System.currentTimeMillis()
        dao.markAllPendingCachedItemsSynced(now)
        dao.markAllAttendanceRecordsSynced()

        SyncResult(
            totalSynced = totalCount,
            attendanceSynced = attendanceCount,
            healthLogsSynced = healthCount,
            message = if (totalCount > 0)
                "সফলভাবে $attendanceCount টি হাজিরা ও $healthCount টি স্বাস্থ্য লগ ক্লাউডে সিঙ্ক হয়েছে!"
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

        private fun constantTimeEquals(a: String?, b: String?): Boolean {
            if (a == null || b == null) return false
            if (a.length != b.length) return false
            var result = 0
            for (i in a.indices) result = result or (a[i].code xor b[i].code)
            return result == 0
        }

        fun getInstance(context: Context): CampRepository {
            return INSTANCE ?: synchronized(this) {
                val db = CampDatabase.getDatabase(context)
                val instance = CampRepository(db.campDao(), db.participantHealthDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
