package com.example.data.sync

import android.util.Log
import com.example.data.firestore.FirestoreManager
import com.example.data.local.CampDao
import com.example.data.model.*
import com.example.data.repository.CampRepository
import com.example.data.util.MoshiJsonUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

/**
 * DataSyncManager coordinates real-time Firestore listeners with the local Room database,
 * ensuring camp schedules, rooms, meal menus, bulletins, incidents, and QR station check-in
 * data remain synchronized locally even during intermittent offline periods.
 */
class DataSyncManager private constructor(
    private val repository: CampRepository,
    private val dao: CampDao
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncTime = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTime: StateFlow<Long> = _lastSyncTime.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    private var syncJob: Job? = null

    /**
     * Starts real-time Firestore snapshot listeners to mirror cloud changes into local Room.
     */
    fun startRealtimeSync() {
        if (syncJob?.isActive == true) return
        syncJob = scope.launch {
            _isSyncing.value = true
            try {
                // Schedule events sync
                launch {
                    FirestoreManager.observeSchedule().catch { e ->
                        Log.w(TAG, "Schedule sync offline listener: ${e.message}")
                    }.collect { scheduleList ->
                        if (scheduleList.isNotEmpty()) {
                            dao.insertScheduleEvents(scheduleList)
                            _lastSyncTime.value = System.currentTimeMillis()
                        }
                    }
                }

                // Rooms sync
                launch {
                    FirestoreManager.observeRooms().catch { e ->
                        Log.w(TAG, "Rooms sync offline listener: ${e.message}")
                    }.collect { roomsList ->
                        if (roomsList.isNotEmpty()) {
                            dao.insertRooms(roomsList)
                            _lastSyncTime.value = System.currentTimeMillis()
                        }
                    }
                }

                // Meals sync
                launch {
                    FirestoreManager.observeMeals().catch { e ->
                        Log.w(TAG, "Meals sync offline listener: ${e.message}")
                    }.collect { mealsList ->
                        if (mealsList.isNotEmpty()) {
                            dao.insertMealClaims(mealsList)
                            _lastSyncTime.value = System.currentTimeMillis()
                        }
                    }
                }

                // Stations sync
                launch {
                    FirestoreManager.observeStations().catch { e ->
                        Log.w(TAG, "Stations sync offline listener: ${e.message}")
                    }.collect { stationList ->
                        if (stationList.isNotEmpty()) {
                            dao.insertStations(stationList)
                            _lastSyncTime.value = System.currentTimeMillis()
                        }
                    }
                }

                // Bulletins sync
                launch {
                    FirestoreManager.observeBulletins().catch { e ->
                        Log.w(TAG, "Bulletins sync offline listener: ${e.message}")
                    }.collect { bulletinList ->
                        if (bulletinList.isNotEmpty()) {
                            dao.insertBulletins(bulletinList)
                            _lastSyncTime.value = System.currentTimeMillis()
                        }
                    }
                }

                // Incidents sync
                launch {
                    FirestoreManager.observeIncidents().catch { e ->
                        Log.w(TAG, "Incidents sync offline listener: ${e.message}")
                    }.collect { incidentList ->
                        if (incidentList.isNotEmpty()) {
                            for (inc in incidentList) {
                                dao.insertIncident(inc.copy(isSynced = true))
                            }
                            _lastSyncTime.value = System.currentTimeMillis()
                        }
                    }
                }

                // Health reports sync
                launch {
                    FirestoreManager.observeHealthRecords().catch { e ->
                        Log.w(TAG, "Health reports sync offline listener: ${e.message}")
                    }.collect { healthList ->
                        if (healthList.isNotEmpty()) {
                            for (record in healthList) {
                                dao.insertHealthRecord(record.copy(isSynced = true))
                            }
                            _lastSyncTime.value = System.currentTimeMillis()
                        }
                    }
                }

                // Flush pending offline queue once connected
                launch {
                    processPendingOfflineQueue()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Realtime sync exception: ${e.message}")
                _syncMessage.value = "Sync error: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    /**
     * Stops active real-time listeners.
     */
    fun stopRealtimeSync() {
        syncJob?.cancel()
        syncJob = null
        _isSyncing.value = false
    }

    /**
     * Records a QR code station check-in locally in Room AND pushes to Firestore.
     */
    suspend fun syncStationCheckIn(
        participantCampCode: String,
        stationId: Int,
        score: Double = 0.0,
        scannerId: String = "STATION_LEADER"
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val participant = dao.getParticipantByCampCode(participantCampCode)
                val record = AttendanceRecordEntity(
                    id = System.currentTimeMillis(),
                    eventId = stationId.toLong(),
                    participantId = participant?.id ?: 0L,
                    campCode = participantCampCode,
                    participantName = participant?.nameBn ?: "অংশগ্রহণকারী",
                    groupName = participant?.groupNameBn ?: "ক্যাম্প গ্রুপ",
                    subgroup = participant?.subgroup ?: "ক্যাম্প দল",
                    status = "PRESENT",
                    scannedAt = System.currentTimeMillis(),
                    method = "QR_SCAN",
                    isSynced = false
                )
                dao.insertAttendanceRecord(record)

                // Push to Firestore attendance collection and verify server write acknowledgement
                try {
                    val firestoreDb = FirestoreManager.db ?: throw IllegalStateException("Firestore unavailable")
                    val data = hashMapOf(
                        "id" to record.id,
                        "stationId" to stationId,
                        "campCode" to participantCampCode,
                        "participantName" to record.participantName,
                        "groupName" to record.groupName,
                        "subgroup" to record.subgroup,
                        "timestamp" to record.scannedAt,
                        "status" to record.status,
                        "scannedBy" to scannerId
                    )
                    firestoreDb.collection("attendance").document(record.id.toString())
                        .set(data, com.google.firebase.firestore.SetOptions.merge())
                        .await()

                    // ONLY mark as synced AFTER server write acknowledgement succeeds
                    dao.markAttendanceRecordSynced(record.id)
                    _lastSyncTime.value = System.currentTimeMillis()
                } catch (e: Exception) {
                    Log.w(TAG, "Station check-in stored locally offline (will flush when connected): ${e.message}")
                }

                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync station check-in: ${e.message}")
                false
            }
        }
    }

    /**
     * Syncs a new incident report to local Room & Firestore with offline queuing support
     */
    suspend fun syncIncidentReport(incident: IncidentEntity): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val localIncident = if (incident.id == 0L) {
                    val generatedId = dao.insertIncident(incident.copy(isSynced = false))
                    incident.copy(id = generatedId, isSynced = false)
                } else {
                    dao.insertIncident(incident.copy(isSynced = false))
                    incident.copy(isSynced = false)
                }

                try {
                    FirestoreManager.saveIncident(localIncident.copy(isSynced = true))
                    // Server-side write acknowledged
                    dao.markIncidentSynced(localIncident.id)
                    _lastSyncTime.value = System.currentTimeMillis()
                } catch (e: Exception) {
                    Log.w(TAG, "Offline mode: Incident report queued locally for sync: ${e.message}")
                    val payloadMap = mapOf(
                        "id" to localIncident.id,
                        "title" to localIncident.title,
                        "category" to localIncident.category,
                        "participantCampCode" to (localIncident.participantCampCode ?: ""),
                        "participantName" to (localIncident.participantName ?: ""),
                        "severity" to localIncident.severity,
                        "location" to localIncident.location,
                        "description" to localIncident.description,
                        "actionTaken" to localIncident.actionTaken,
                        "reporter" to localIncident.reporter,
                        "status" to localIncident.status,
                        "timestamp" to localIncident.timestamp
                    )
                    dao.insertCachedSyncItem(
                        CachedSyncItemEntity(
                            itemType = "INCIDENT_REPORT",
                            participantId = 0L,
                            campCode = localIncident.participantCampCode ?: "",
                            participantName = localIncident.participantName ?: "ইনসিডেন্স রিপোর্ট",
                            summary = "ঘটনা রিপোর্ট: ${localIncident.title} (${localIncident.severity})",
                            payloadJson = MoshiJsonUtils.toJson(payloadMap),
                            status = CachedSyncItemEntity.STATUS_PENDING
                        )
                    )
                }
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error saving incident report locally: ${e.message}")
                false
            }
        }
    }

    /**
     * Syncs a new bulletin notice to local Room & Firestore
     */
    suspend fun syncBulletin(bulletin: BulletinEntity): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                dao.insertBulletin(bulletin)
                FirestoreManager.saveBulletin(bulletin)
                _lastSyncTime.value = System.currentTimeMillis()
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing bulletin: ${e.message}")
                false
            }
        }
    }

    /**
     * Syncs a health report to local Room & Firestore with offline queuing support
     */
    suspend fun syncHealthReport(record: HealthRecordEntity): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val localRecord = if (record.id == 0L) {
                    val generatedId = dao.insertHealthRecord(record.copy(isSynced = false))
                    record.copy(id = generatedId, isSynced = false)
                } else {
                    dao.insertHealthRecord(record.copy(isSynced = false))
                    record.copy(isSynced = false)
                }

                try {
                    FirestoreManager.saveHealthRecord(localRecord.copy(isSynced = true))
                    // Server-side write acknowledged
                    dao.markHealthRecordSynced(localRecord.id)
                    _lastSyncTime.value = System.currentTimeMillis()
                } catch (e: Exception) {
                    Log.w(TAG, "Offline mode: Health report queued locally for sync: ${e.message}")
                    val payloadMap = mapOf(
                        "id" to localRecord.id,
                        "participantId" to localRecord.participantId,
                        "campCode" to localRecord.campCode,
                        "participantName" to localRecord.participantName,
                        "groupName" to localRecord.groupName,
                        "checkupType" to localRecord.checkupType,
                        "doctorName" to localRecord.doctorName,
                        "vitals" to localRecord.vitals,
                        "diagnosis" to localRecord.diagnosis,
                        "adviceAndMeds" to localRecord.adviceAndMeds,
                        "isEmergencyFlag" to localRecord.isEmergencyFlag,
                        "priority" to localRecord.priority,
                        "status" to localRecord.status,
                        "createdAt" to localRecord.createdAt
                    )
                    dao.insertCachedSyncItem(
                        CachedSyncItemEntity(
                            itemType = CachedSyncItemEntity.TYPE_HEALTH_CHECKUP,
                            participantId = localRecord.participantId,
                            campCode = localRecord.campCode,
                            participantName = localRecord.participantName,
                            summary = "স্বাস্থ্য স্ক্রিনিং: ${localRecord.checkupType} (${localRecord.doctorName})",
                            payloadJson = MoshiJsonUtils.toJson(payloadMap),
                            status = CachedSyncItemEntity.STATUS_PENDING
                        )
                    )
                }
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error saving health report locally: ${e.message}")
                false
            }
        }
    }

    /**
     * Flushes offline queued incident reports, health status updates, and cached sync items
     * to Firestore once connectivity is active.
     */
    suspend fun processPendingOfflineQueue(): Pair<Int, Int> {
        return withContext(Dispatchers.IO) {
            var syncedIncidents = 0
            var syncedHealth = 0
            try {
                // 1. Unsynced Incidents
                val unsyncedIncidents = dao.getUnsyncedIncidents()
                for (inc in unsyncedIncidents) {
                    try {
                        FirestoreManager.saveIncident(inc.copy(isSynced = true))
                        dao.markIncidentSynced(inc.id)
                        syncedIncidents++
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to flush offline incident report ${inc.id}: ${e.message}")
                    }
                }

                // 2. Unsynced Health Records
                val unsyncedHealth = dao.getUnsyncedHealthRecords()
                for (record in unsyncedHealth) {
                    try {
                        FirestoreManager.saveHealthRecord(record.copy(isSynced = true))
                        dao.markHealthRecordSynced(record.id)
                        syncedHealth++
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to flush offline health record ${record.id}: ${e.message}")
                    }
                }

                // 3. Unsynced Attendance records
                val unsyncedAttendance = dao.getUnsyncedAttendanceRecords()
                val firestoreDb = FirestoreManager.db
                if (firestoreDb != null) {
                    for (att in unsyncedAttendance) {
                        try {
                            val data = hashMapOf(
                                "id" to att.id,
                                "stationId" to att.eventId,
                                "campCode" to att.campCode,
                                "participantName" to att.participantName,
                                "groupName" to att.groupName,
                                "subgroup" to att.subgroup,
                                "timestamp" to att.scannedAt,
                                "status" to att.status
                            )
                            firestoreDb.collection("attendance").document(att.id.toString())
                                .set(data, com.google.firebase.firestore.SetOptions.merge())
                                .await()

                            dao.markAttendanceRecordSynced(att.id)
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to flush offline attendance ${att.id}: ${e.message}")
                        }
                    }
                }

                // 4. Pending cached sync items with Moshi deserialization & Firestore write acknowledgement
                val pendingCached = dao.getPendingCachedSyncItems()
                if (firestoreDb != null) {
                    for (cached in pendingCached) {
                        try {
                            val payloadMap = MoshiJsonUtils.jsonToMap(cached.payloadJson)
                            if (payloadMap.isNotEmpty()) {
                                val targetCollection = when (cached.itemType) {
                                    CachedSyncItemEntity.TYPE_HEALTH_CHECKUP,
                                    CachedSyncItemEntity.TYPE_HEALTH_LOG -> "health_reports"
                                    "INCIDENT_REPORT" -> "incidents"
                                    CachedSyncItemEntity.TYPE_ATTENDANCE -> "attendance"
                                    else -> "cached_sync"
                                }
                                val rawId = payloadMap["id"]
                                val docId = when (rawId) {
                                    is Number -> rawId.toLong().toString()
                                    is String -> rawId
                                    else -> cached.id.toString()
                                }

                                firestoreDb.collection(targetCollection).document(docId)
                                    .set(payloadMap, com.google.firebase.firestore.SetOptions.merge())
                                    .await()

                                // ONLY mark as synced AFTER server write acknowledgement (.await()) succeeds
                                dao.markCachedItemSynced(cached.id)
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to flush pending cached item ${cached.id}: ${e.message}")
                        }
                    }
                }

                if (syncedIncidents > 0 || syncedHealth > 0 || unsyncedAttendance.isNotEmpty() || pendingCached.isNotEmpty()) {
                    _lastSyncTime.value = System.currentTimeMillis()
                    _syncMessage.value = "অফলাইন ডেটা সিঙ্ক সম্পন্ন ($syncedIncidents ইনসিডেন্স, $syncedHealth স্বাস্থ্য রেকর্ড)"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in processPendingOfflineQueue: ${e.message}")
            }
            Pair(syncedIncidents, syncedHealth)
        }
    }

    /**
     * Schedules WorkManager periodic background sync job for reliable offline content availability.
     */
    fun schedulePeriodicBackgroundSync(context: android.content.Context) {
        try {
            val constraints = androidx.work.Constraints.Builder()
                .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                .build()

            val syncWorkRequest = androidx.work.PeriodicWorkRequestBuilder<DataSyncWorker>(
                15, java.util.concurrent.TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "CampDataPeriodicSync",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest
            )
            Log.d(TAG, "Periodic WorkManager sync scheduled successfully.")
        } catch (e: Exception) {
            Log.w(TAG, "Unable to schedule WorkManager periodic sync: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "DataSyncManager"

        @Volatile
        private var INSTANCE: DataSyncManager? = null

        fun getInstance(repository: CampRepository, dao: CampDao): DataSyncManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataSyncManager(repository, dao).also { INSTANCE = it }
            }
        }
    }
}
