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
        try {
            val database = db ?: return
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
        } catch (e: Exception) {
            Log.e(TAG, "Error saving incident ${incident.title}: ${e.message}", e)
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
        try {
            val database = db ?: return
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
            database.collection("health_reports").document(record.id.toString()).set(data).await()
            Log.d(TAG, "Health report saved successfully to Firestore: ${record.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving health report ${record.id}: ${e.message}", e)
        }
    }
}
