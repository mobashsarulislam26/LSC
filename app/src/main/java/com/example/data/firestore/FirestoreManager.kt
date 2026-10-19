package com.example.data.firestore

import android.util.Log
import com.example.data.model.*
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Singleton manager class for initializing and managing Firestore SDK access
 * and dynamic camp data entities (stations, rooms, event schedules, user roles, etc.).
 */
object FirestoreManager {

    private const val TAG = "FirestoreManager"

    // Firestore Collection Names
    const val COLLECTION_USERS = "users"
    const val COLLECTION_STATIONS = "stations"
    const val COLLECTION_ROOMS = "rooms"
    const val COLLECTION_SCHEDULE = "schedules"
    const val COLLECTION_MEALS = "meals"
    const val COLLECTION_BULLETINS = "bulletins"
    const val COLLECTION_INCIDENTS = "incidents"
    const val COLLECTION_COMPETITIONS = "competitions"
    const val COLLECTION_AUDIT_TRAIL = "audit_trail"
    const val COLLECTION_AUDIT_LOGS = "audit_trail"
    const val COLLECTION_PARTICIPANTS = "participants"

    private var dbInstance: FirebaseFirestore? = null

    /**
     * Lazy singleton initializer for FirebaseFirestore SDK.
     */
    val db: FirebaseFirestore?
        get() {
            if (dbInstance == null) {
                synchronized(this) {
                    if (dbInstance == null) {
                        try {
                            dbInstance = Firebase.firestore
                            Log.d(TAG, "Firestore SDK initialized successfully.")
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to initialize Firestore SDK: ${e.message}")
                            return null
                        }
                    }
                }
            }
            return dbInstance
        }

    // ==================== User & Role Management ====================

    suspend fun saveUserRole(uid: String, email: String, role: UserRole, campName: String = "Rover Camp") {
        try {
            val database = db ?: return
            val userMap = hashMapOf(
                "uid" to uid,
                "email" to email,
                "role" to role.name,
                "campName" to campName,
                "updatedAt" to System.currentTimeMillis()
            )
            database.collection(COLLECTION_USERS).document(uid)
                .set(userMap, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user role for $uid: ${e.message}", e)
        }
    }

    suspend fun getUserRole(uid: String): UserRole? {
        return try {
            val database = db ?: return null
            val snapshot = database.collection(COLLECTION_USERS).document(uid).get().await()
            val roleStr = snapshot.getString("role")
            roleStr?.let { UserRole.valueOf(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user role for $uid: ${e.message}", e)
            null
        }
    }

    /**
     * Exclusively verifies the user role via Firebase Authentication custom claims
     * or a verified role document fetched from Firestore ('users' collection).
     */
    suspend fun getVerifiedUserRole(user: com.google.firebase.auth.FirebaseUser): UserRole? {
        // 1. Check secure Firebase Auth custom claims
        try {
            val tokenResult = user.getIdToken(false).await()
            val customRole = tokenResult.claims["role"] as? String
            if (!customRole.isNullOrBlank()) {
                val matched = UserRole.values().find { it.name.equals(customRole, ignoreCase = true) }
                if (matched != null) {
                    Log.d(TAG, "User role verified via Firebase Auth custom claims: ${matched.name}")
                    return matched
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Custom claim token check note: ${e.message}")
        }

        // 2. Fetch verified role document from Firestore 'users/{uid}'
        return getUserRole(user.uid)
    }

    // ==================== Dynamic Stations ====================

    fun observeStations(): Flow<List<StationEntity>> = callbackFlow {
        val database = db ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = database.collection(COLLECTION_STATIONS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val stations = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        StationEntity(
                            id = doc.getLong("id")?.toInt() ?: doc.id.hashCode(),
                            nameBn = doc.getString("nameBn") ?: "",
                            nameEn = doc.getString("nameEn") ?: "",
                            moderatorName = doc.getString("moderatorName") ?: "",
                            location = doc.getString("location") ?: "",
                            objectiveBn = doc.getString("objectiveBn") ?: "",
                            iconKey = doc.getString("iconKey") ?: "star",
                            materialsNeeded = doc.getString("materialsNeeded") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(stations)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveStation(station: StationEntity) {
        try {
            val database = db ?: return
            val data = hashMapOf(
                "id" to station.id,
                "nameBn" to station.nameBn,
                "nameEn" to station.nameEn,
                "moderatorName" to station.moderatorName,
                "location" to station.location,
                "objectiveBn" to station.objectiveBn,
                "iconKey" to station.iconKey,
                "materialsNeeded" to station.materialsNeeded
            )
            database.collection(COLLECTION_STATIONS).document(station.id.toString())
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving station ${station.nameEn}: ${e.message}", e)
        }
    }

    // ==================== Dynamic Rooms ====================

    fun observeRooms(): Flow<List<RoomEntity>> = callbackFlow {
        val database = db ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = database.collection(COLLECTION_ROOMS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val rooms = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        RoomEntity(
                            id = doc.getLong("id") ?: doc.id.hashCode().toLong(),
                            buildingName = doc.getString("buildingName") ?: "",
                            roomNo = doc.getString("roomNo") ?: "",
                            floor = doc.getString("floor") ?: "১ম তলা",
                            capacity = doc.getLong("capacity")?.toInt() ?: 10,
                            assignedCount = doc.getLong("assignedCount")?.toInt() ?: 0,
                            assignedGroupId = doc.getString("assignedGroupId") ?: "bukhari",
                            inChargeName = doc.getString("inChargeName") ?: "",
                            inChargePhone = doc.getString("inChargePhone") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(rooms)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveRoom(room: RoomEntity) {
        try {
            val database = db ?: return
            val data = hashMapOf(
                "id" to room.id,
                "buildingName" to room.buildingName,
                "roomNo" to room.roomNo,
                "floor" to room.floor,
                "capacity" to room.capacity,
                "assignedCount" to room.assignedCount,
                "assignedGroupId" to room.assignedGroupId,
                "inChargeName" to room.inChargeName,
                "inChargePhone" to room.inChargePhone
            )
            database.collection(COLLECTION_ROOMS).document(room.id.toString())
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving room ${room.roomNo}: ${e.message}", e)
        }
    }

    // ==================== Dynamic Schedules ====================

    fun observeSchedule(): Flow<List<ScheduleEventEntity>> = callbackFlow {
        val database = db ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = database.collection(COLLECTION_SCHEDULE)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val events = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ScheduleEventEntity(
                            id = doc.getLong("id") ?: doc.id.hashCode().toLong(),
                            dayNo = doc.getLong("dayNo")?.toInt() ?: 1,
                            timeSlot = doc.getString("timeSlot") ?: "",
                            titleBn = doc.getString("titleBn") ?: "",
                            titleEn = doc.getString("titleEn") ?: "",
                            category = doc.getString("category") ?: "Station",
                            location = doc.getString("location") ?: "Main Ground",
                            isCurrent = doc.getBoolean("isCurrent") ?: false,
                            isCompleted = doc.getBoolean("isCompleted") ?: false
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(events)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveScheduleEvent(event: ScheduleEventEntity) {
        try {
            val database = db ?: return
            val data = hashMapOf(
                "id" to event.id,
                "dayNo" to event.dayNo,
                "timeSlot" to event.timeSlot,
                "titleBn" to event.titleBn,
                "titleEn" to event.titleEn,
                "category" to event.category,
                "location" to event.location,
                "isCurrent" to event.isCurrent,
                "isCompleted" to event.isCompleted
            )
            database.collection(COLLECTION_SCHEDULE).document(event.id.toString())
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving schedule event ${event.titleEn}: ${e.message}", e)
        }
    }

    // ==================== Real-time Live Bulletins ====================

    fun observeBulletins(): Flow<List<BulletinEntity>> = callbackFlow {
        val database = db ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = database.collection(COLLECTION_BULLETINS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        BulletinEntity(
                            id = doc.getLong("id") ?: doc.id.hashCode().toLong(),
                            dayNo = doc.getLong("dayNo")?.toInt() ?: 1,
                            dateStr = doc.getString("dateStr") ?: "আজ",
                            titleBn = doc.getString("titleBn") ?: "",
                            titleEn = doc.getString("titleEn") ?: "",
                            summaryBn = doc.getString("summaryBn") ?: "",
                            summaryEn = doc.getString("summaryEn") ?: "",
                            author = doc.getString("author") ?: "ক্যাম্প প্রশাসন",
                            groupSpotlight = doc.getString("groupSpotlight") ?: "ক্যাম্প ঘোষণা",
                            likesCount = doc.getLong("likesCount")?.toInt() ?: 0,
                            isPublished = doc.getBoolean("isPublished") ?: true,
                            status = doc.getString("status") ?: "PUBLISHED",
                            hasImageConsent = doc.getBoolean("hasImageConsent") ?: true,
                            wallMagazineContributions = doc.getLong("wallMagazineContributions")?.toInt() ?: 0
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveBulletin(bulletin: BulletinEntity) {
        try {
            val database = db ?: return
            val data = hashMapOf(
                "id" to bulletin.id,
                "dayNo" to bulletin.dayNo,
                "dateStr" to bulletin.dateStr,
                "titleBn" to bulletin.titleBn,
                "titleEn" to bulletin.titleEn,
                "summaryBn" to bulletin.summaryBn,
                "summaryEn" to bulletin.summaryEn,
                "author" to bulletin.author,
                "groupSpotlight" to bulletin.groupSpotlight,
                "likesCount" to bulletin.likesCount,
                "isPublished" to bulletin.isPublished,
                "status" to bulletin.status,
                "timestamp" to System.currentTimeMillis()
            )
            database.collection(COLLECTION_BULLETINS).document(bulletin.id.toString())
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving bulletin ${bulletin.titleBn}: ${e.message}", e)
        }
    }

    // ==================== Real-time Incident Reports ====================

    fun observeIncidents(): Flow<List<IncidentEntity>> = callbackFlow {
        val database = db ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = database.collection(COLLECTION_INCIDENTS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        IncidentEntity(
                            id = doc.getLong("id") ?: doc.id.hashCode().toLong(),
                            title = doc.getString("title") ?: "",
                            category = doc.getString("category") ?: "MEDICAL",
                            participantCampCode = doc.getString("participantCampCode"),
                            participantName = doc.getString("participantName"),
                            severity = doc.getString("severity") ?: "MEDIUM",
                            location = doc.getString("location") ?: "",
                            description = doc.getString("description") ?: "",
                            actionTaken = doc.getString("actionTaken") ?: "",
                            reporter = doc.getString("reporter") ?: "স্টাফ/অংশগ্রহণকারী",
                            status = doc.getString("status") ?: "OPEN",
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveIncident(incident: IncidentEntity) {
        val database = db ?: throw IllegalStateException("Firestore database is unavailable")
        try {
            val data = hashMapOf(
                "id" to incident.id,
                "title" to incident.title,
                "category" to incident.category,
                "participantCampCode" to incident.participantCampCode,
                "participantName" to incident.participantName,
                "severity" to incident.severity,
                "location" to incident.location,
                "description" to incident.description,
                "actionTaken" to incident.actionTaken,
                "reporter" to incident.reporter,
                "status" to incident.status,
                "timestamp" to incident.timestamp
            )
            database.collection(COLLECTION_INCIDENTS).document(incident.id.toString())
                .set(data, SetOptions.merge())
                .await()
            Log.d(TAG, "Incident saved successfully to Firestore: ${incident.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving incident ${incident.title}: ${e.message}", e)
            throw e
        }
    }

    // ==================== Dynamic Meals ====================

    fun observeMeals(): Flow<List<MealDistributionEntity>> = callbackFlow {
        val database = db ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = database.collection(COLLECTION_MEALS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        MealDistributionEntity(
                            id = doc.getLong("id") ?: doc.id.hashCode().toLong(),
                            dayNo = doc.getLong("dayNo")?.toInt() ?: 1,
                            mealType = doc.getString("mealType") ?: "LUNCH",
                            participantId = doc.getLong("participantId") ?: 0L,
                            campCode = doc.getString("campCode") ?: "",
                            participantName = doc.getString("participantName") ?: "",
                            claimedAt = doc.getLong("claimedAt") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveMeal(meal: MealDistributionEntity) {
        try {
            val database = db ?: return
            val data = hashMapOf(
                "id" to meal.id,
                "dayNo" to meal.dayNo,
                "mealType" to meal.mealType,
                "participantId" to meal.participantId,
                "campCode" to meal.campCode,
                "participantName" to meal.participantName,
                "claimedAt" to meal.claimedAt
            )
            database.collection(COLLECTION_MEALS).document(meal.id.toString())
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving meal ${meal.mealType}: ${e.message}", e)
        }
    }

    // ==================== Firestore Deletions ====================

    suspend fun deleteStation(stationId: Int) {
        try {
            db?.collection(COLLECTION_STATIONS)?.document(stationId.toString())?.delete()?.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting station $stationId: ${e.message}")
        }
    }

    suspend fun deleteScheduleEvent(eventId: Long) {
        try {
            db?.collection(COLLECTION_SCHEDULE)?.document(eventId.toString())?.delete()?.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting schedule $eventId: ${e.message}")
        }
    }

    suspend fun deleteRoom(roomId: Long) {
        try {
            db?.collection(COLLECTION_ROOMS)?.document(roomId.toString())?.delete()?.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting room $roomId: ${e.message}")
        }
    }

    suspend fun deleteMeal(mealId: Long) {
        try {
            db?.collection(COLLECTION_MEALS)?.document(mealId.toString())?.delete()?.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting meal $mealId: ${e.message}")
        }
    }

    // ==================== Dynamic Health Reports ====================

    fun observeHealthRecords(): Flow<List<HealthRecordEntity>> = callbackFlow {
        val database = db
        if (database == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = database.collection("health_reports")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Health reports snapshot error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        HealthRecordEntity(
                            id = doc.getLong("id") ?: doc.id.hashCode().toLong(),
                            participantId = doc.getLong("participantId") ?: 0L,
                            campCode = doc.getString("campCode") ?: "",
                            participantName = doc.getString("participantName") ?: "অংশগ্রহণকারী",
                            groupName = doc.getString("groupName") ?: "ক্যাম্প দল",
                            checkupType = doc.getString("checkupType") ?: "GENERAL",
                            doctorName = doc.getString("doctorName") ?: "",
                            vitals = doc.getString("vitals") ?: "",
                            diagnosis = doc.getString("diagnosis") ?: "",
                            adviceAndMeds = doc.getString("adviceAndMeds") ?: "",
                            isEmergencyFlag = doc.getBoolean("isEmergencyFlag") ?: false,
                            priority = doc.getString("priority") ?: "NORMAL",
                            status = doc.getString("status") ?: "PENDING",
                            createdAt = doc.getLong("createdAt") ?: doc.getLong("checkupTime") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveHealthRecord(record: HealthRecordEntity) {
        val database = db ?: throw IllegalStateException("Firestore database is unavailable")
        try {
            val data = hashMapOf(
                "id" to record.id,
                "participantId" to record.participantId,
                "campCode" to record.campCode,
                "participantName" to record.participantName,
                "groupName" to record.groupName,
                "checkupType" to record.checkupType,
                "doctorName" to record.doctorName,
                "vitals" to record.vitals,
                "diagnosis" to record.diagnosis,
                "adviceAndMeds" to record.adviceAndMeds,
                "isEmergencyFlag" to record.isEmergencyFlag,
                "priority" to record.priority,
                "status" to record.status,
                "createdAt" to record.createdAt
            )
            database.collection("health_reports").document(record.id.toString()).set(data, SetOptions.merge()).await()
            Log.d(TAG, "Health report saved successfully to Firestore: ${record.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving health report ${record.id}: ${e.message}", e)
            throw e
        }
    }

    // ==================== Real-time Audit Logs ====================

    fun observeAuditLogs(): Flow<List<AuditLogEntity>> = callbackFlow {
        val database = db
        if (database == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = database.collection(COLLECTION_AUDIT_TRAIL)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Audit logs snapshot error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        AuditLogEntity(
                            id = doc.getLong("id") ?: doc.id.hashCode().toLong(),
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                            adminId = doc.getString("adminId") ?: doc.getString("uid") ?: "",
                            userEmail = doc.getString("userEmail") ?: doc.getString("adminEmail") ?: "admin@lsc.org",
                            actionType = doc.getString("actionType") ?: "UPDATE",
                            targetEntity = doc.getString("targetEntity") ?: "System",
                            description = doc.getString("description") ?: "",
                            modifiedRecordDetails = doc.getString("modifiedRecordDetails") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                }?.sortedByDescending { it.timestamp } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveAuditLog(log: AuditLogEntity) {
        val database = db ?: return
        try {
            val docId = if (log.id != 0L) log.id.toString() else "${log.timestamp}_${log.userEmail.hashCode()}"
            val data = hashMapOf(
                "id" to log.id,
                "timestamp" to log.timestamp,
                "adminId" to log.adminId,
                "userEmail" to log.userEmail,
                "actionType" to log.actionType,
                "targetEntity" to log.targetEntity,
                "description" to log.description,
                "modifiedRecordDetails" to log.modifiedRecordDetails
            )
            database.collection(COLLECTION_AUDIT_TRAIL).document(docId)
                .set(data, SetOptions.merge())
                .await()
            Log.d(TAG, "Audit log saved successfully to Firestore dedicated 'audit_trail' collection: $docId")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving audit log to Firestore 'audit_trail': ${e.message}", e)
        }
    }

    // ==================== Dynamic Participants & Roster ====================

    fun observeParticipants(): Flow<List<ParticipantEntity>> = callbackFlow {
        val database = db ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = database.collection(COLLECTION_PARTICIPANTS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Participants snapshot error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ParticipantEntity(
                            id = doc.getLong("id") ?: doc.id.hashCode().toLong(),
                            campCode = doc.getString("campCode") ?: doc.id,
                            fileNo = doc.getString("fileNo") ?: "",
                            nameBn = doc.getString("nameBn") ?: "",
                            nameEn = doc.getString("nameEn") ?: "",
                            districtBn = doc.getString("districtBn") ?: "ঢাকা",
                            districtEn = doc.getString("districtEn") ?: "Dhaka",
                            gender = doc.getString("gender") ?: "Male",
                            age = doc.getLong("age")?.toInt() ?: 15,
                            groupId = doc.getString("groupId") ?: "bukhari",
                            groupNameBn = doc.getString("groupNameBn") ?: "বুখারী",
                            groupNameEn = doc.getString("groupNameEn") ?: "Bukhari",
                            subgroup = doc.getString("subgroup") ?: "বুখারী ১",
                            roomNo = doc.getString("roomNo") ?: "১০১",
                            bedNo = doc.getString("bedNo") ?: "১",
                            mealPref = doc.getString("mealPref") ?: "নিয়মিত",
                            guardianName = doc.getString("guardianName") ?: "",
                            guardianPhone = doc.getString("guardianPhone") ?: "",
                            isCheckedIn = doc.getBoolean("isCheckedIn") ?: true,
                            checkInTime = doc.getLong("checkInTime") ?: System.currentTimeMillis(),
                            medicalFlag = doc.getString("medicalFlag"),
                            totalScore = doc.getDouble("totalScore") ?: 0.0,
                            attendancePercentage = doc.getDouble("attendancePercentage")?.toFloat() ?: 95.0f,
                            photoAvatarSeed = doc.getLong("photoAvatarSeed")?.toInt() ?: 1
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveParticipant(participant: ParticipantEntity) {
        val database = db ?: return
        try {
            val docId = participant.campCode.ifBlank { participant.id.toString() }
            val data = hashMapOf(
                "id" to participant.id,
                "campCode" to participant.campCode,
                "fileNo" to participant.fileNo,
                "nameBn" to participant.nameBn,
                "nameEn" to participant.nameEn,
                "districtBn" to participant.districtBn,
                "districtEn" to participant.districtEn,
                "gender" to participant.gender,
                "age" to participant.age,
                "groupId" to participant.groupId,
                "groupNameBn" to participant.groupNameBn,
                "groupNameEn" to participant.groupNameEn,
                "subgroup" to participant.subgroup,
                "roomNo" to participant.roomNo,
                "bedNo" to participant.bedNo,
                "mealPref" to participant.mealPref,
                "guardianName" to participant.guardianName,
                "guardianPhone" to participant.guardianPhone,
                "isCheckedIn" to participant.isCheckedIn,
                "checkInTime" to participant.checkInTime,
                "medicalFlag" to participant.medicalFlag,
                "totalScore" to participant.totalScore,
                "attendancePercentage" to participant.attendancePercentage,
                "photoAvatarSeed" to participant.photoAvatarSeed,
                "updatedAt" to System.currentTimeMillis()
            )
            database.collection(COLLECTION_PARTICIPANTS).document(docId)
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving participant ${participant.campCode}: ${e.message}", e)
            throw e
        }
    }

    suspend fun bulkSaveParticipants(participants: List<ParticipantEntity>): Int {
        val database = db ?: return 0
        var savedCount = 0
        try {
            participants.chunked(400).forEach { chunk ->
                val batch = database.batch()
                chunk.forEach { p ->
                    val docId = p.campCode.ifBlank { p.id.toString() }
                    val docRef = database.collection(COLLECTION_PARTICIPANTS).document(docId)
                    val data = hashMapOf(
                        "id" to p.id,
                        "campCode" to p.campCode,
                        "fileNo" to p.fileNo,
                        "nameBn" to p.nameBn,
                        "nameEn" to p.nameEn,
                        "districtBn" to p.districtBn,
                        "districtEn" to p.districtEn,
                        "gender" to p.gender,
                        "age" to p.age,
                        "groupId" to p.groupId,
                        "groupNameBn" to p.groupNameBn,
                        "groupNameEn" to p.groupNameEn,
                        "subgroup" to p.subgroup,
                        "roomNo" to p.roomNo,
                        "bedNo" to p.bedNo,
                        "mealPref" to p.mealPref,
                        "guardianName" to p.guardianName,
                        "guardianPhone" to p.guardianPhone,
                        "isCheckedIn" to p.isCheckedIn,
                        "checkInTime" to p.checkInTime,
                        "medicalFlag" to p.medicalFlag,
                        "totalScore" to p.totalScore,
                        "attendancePercentage" to p.attendancePercentage,
                        "photoAvatarSeed" to p.photoAvatarSeed,
                        "updatedAt" to System.currentTimeMillis()
                    )
                    batch.set(docRef, data, SetOptions.merge())
                }
                batch.commit().await()
                savedCount += chunk.size
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in bulkSaveParticipants: ${e.message}", e)
            throw e
        }
        return savedCount
    }

    suspend fun bulkSaveSchedules(schedules: List<ScheduleEventEntity>): Int {
        val database = db ?: return 0
        var savedCount = 0
        try {
            schedules.chunked(400).forEach { chunk ->
                val batch = database.batch()
                chunk.forEach { ev ->
                    val docRef = database.collection(COLLECTION_SCHEDULE).document(ev.id.toString())
                    val data = hashMapOf(
                        "id" to ev.id,
                        "dayNo" to ev.dayNo,
                        "timeSlot" to ev.timeSlot,
                        "titleBn" to ev.titleBn,
                        "titleEn" to ev.titleEn,
                        "category" to ev.category,
                        "stationId" to ev.stationId,
                        "location" to ev.location,
                        "isCurrent" to ev.isCurrent,
                        "isCompleted" to ev.isCompleted,
                        "rotationJson" to ev.rotationJson,
                        "updatedAt" to System.currentTimeMillis()
                    )
                    batch.set(docRef, data, SetOptions.merge())
                }
                batch.commit().await()
                savedCount += chunk.size
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in bulkSaveSchedules: ${e.message}", e)
            throw e
        }
        return savedCount
    }
}
