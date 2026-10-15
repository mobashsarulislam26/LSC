package com.example.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.firestore.FirestoreManager
import com.example.data.local.CampDatabase
import com.example.data.repository.CampRepository
import kotlinx.coroutines.flow.firstOrNull

/**
 * Background WorkManager worker that periodically fetches updated camp content
 * from Firestore and persists it into local Room database for full offline readiness.
 */
class DataSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting WorkManager background camp data sync...")

            val db = CampDatabase.getDatabase(applicationContext)
            val dao = db.campDao()

            // Sync Stations
            val stations = FirestoreManager.observeStations().firstOrNull()
            if (!stations.isNullOrEmpty()) {
                dao.insertStations(stations)
            }

            // Sync Schedules
            val schedules = FirestoreManager.observeSchedule().firstOrNull()
            if (!schedules.isNullOrEmpty()) {
                dao.insertScheduleEvents(schedules)
            }

            // Sync Rooms
            val rooms = FirestoreManager.observeRooms().firstOrNull()
            if (!rooms.isNullOrEmpty()) {
                dao.insertRooms(rooms)
            }

            // Sync Meals
            val meals = FirestoreManager.observeMeals().firstOrNull()
            if (!meals.isNullOrEmpty()) {
                dao.insertMealClaims(meals)
            }

            // Sync Bulletins
            val bulletins = FirestoreManager.observeBulletins().firstOrNull()
            if (!bulletins.isNullOrEmpty()) {
                dao.insertBulletins(bulletins)
            }

            // Sync Incidents
            val incidents = FirestoreManager.observeIncidents().firstOrNull()
            if (!incidents.isNullOrEmpty()) {
                for (inc in incidents) {
                    dao.insertIncident(inc.copy(isSynced = true))
                }
            }

            // Flush offline queue (incidents, health records, attendance)
            val repository = CampRepository.getInstance(applicationContext)
            val syncManager = DataSyncManager.getInstance(repository, dao)
            syncManager.processPendingOfflineQueue()

            Log.d(TAG, "WorkManager background camp sync completed successfully.")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "WorkManager background sync failed: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "DataSyncWorker"
    }
}
