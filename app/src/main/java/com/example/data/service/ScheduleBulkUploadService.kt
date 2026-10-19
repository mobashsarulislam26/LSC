package com.example.data.service

import android.util.Log
import com.example.data.firestore.FirestoreManager
import com.example.data.local.CampDao
import com.example.data.model.AuditLogEntity
import com.example.data.model.ScheduleEventEntity
import com.example.data.sample.SeedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Result data class for bulk schedule uploads.
 */
data class ScheduleBulkUploadResult(
    val totalParsed: Int,
    val successCount: Int,
    val failureCount: Int,
    val events: List<ScheduleEventEntity>,
    val errors: List<String> = emptyList(),
    val message: String
)

/**
 * Service to parse, prefill, and bulk upload the official camp schedule data
 * (event names, timings, locations, categories) directly into the Firestore 'schedules' collection.
 */
object ScheduleBulkUploadService {

    private const val TAG = "ScheduleBulkUploadService"

    /**
     * Returns the comprehensive official 5-day camp schedule data from official camp documentation.
     */
    fun getOfficialCampSchedule(): List<ScheduleEventEntity> {
        return SeedData.scheduleEvents
    }

    /**
     * Parses schedule data from JSON array, JSON object wrapper, or CSV format.
     */
    fun parseScheduleData(rawInput: String): List<ScheduleEventEntity> {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) return emptyList()

        val events = mutableListOf<ScheduleEventEntity>()

        try {
            if (trimmed.startsWith("[")) {
                // Direct JSON Array of schedule events
                val jsonArray = JSONArray(trimmed)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.optJSONObject(i)
                    if (obj != null) {
                        parseJsonObjectSchedule(obj, i)?.let { events.add(it) }
                    }
                }
            } else if (trimmed.startsWith("{")) {
                val jsonObject = JSONObject(trimmed)
                if (jsonObject.has("schedules") || jsonObject.has("events") || jsonObject.has("schedule")) {
                    val key = if (jsonObject.has("schedules")) "schedules" else if (jsonObject.has("events")) "events" else "schedule"
                    val array = jsonObject.optJSONArray(key)
                    if (array != null) {
                        for (i in 0 until array.length()) {
                            val obj = array.optJSONObject(i)
                            if (obj != null) {
                                parseJsonObjectSchedule(obj, i)?.let { events.add(it) }
                            }
                        }
                    }
                } else if (jsonObject.has("csv") || jsonObject.has("data")) {
                    val csvString = jsonObject.optString("csv", jsonObject.optString("data", ""))
                    events.addAll(parseCsvSchedule(csvString))
                } else {
                    parseJsonObjectSchedule(jsonObject, 0)?.let { events.add(it) }
                }
            } else {
                // Raw CSV fallback
                events.addAll(parseCsvSchedule(trimmed))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing schedule JSON, falling back to CSV line parser: ${e.message}")
            events.addAll(parseCsvSchedule(trimmed))
        }

        return events
    }

    private fun parseJsonObjectSchedule(obj: JSONObject, index: Int): ScheduleEventEntity? {
        val titleBn = obj.optString("titleBn", obj.optString("title", obj.optString("name", ""))).trim()
        val titleEn = obj.optString("titleEn", titleBn).trim()
        val timeSlot = obj.optString("timeSlot", obj.optString("timing", obj.optString("time", ""))).trim()

        if (titleBn.isEmpty() && timeSlot.isEmpty()) return null

        val dayNo = obj.optInt("dayNo", obj.optInt("day", 1))
        val location = obj.optString("location", obj.optString("place", "প্রধান মাঠ / অডিটোরিয়াম")).trim()
        val category = obj.optString("category", "Station").trim()
        val isCurrent = obj.optBoolean("isCurrent", false)
        val isCompleted = obj.optBoolean("isCompleted", false)
        val id = obj.optLong("id", (dayNo * 100 + index + 1).toLong())

        return ScheduleEventEntity(
            id = id,
            dayNo = dayNo,
            timeSlot = timeSlot,
            titleBn = if (titleBn.isNotEmpty()) titleBn else titleEn,
            titleEn = if (titleEn.isNotEmpty()) titleEn else titleBn,
            category = category,
            stationId = if (obj.has("stationId")) obj.optInt("stationId") else null,
            location = location,
            isCurrent = isCurrent,
            isCompleted = isCompleted,
            rotationJson = obj.optString("rotationJson", "")
        )
    }

    private fun parseCsvSchedule(csvText: String): List<ScheduleEventEntity> {
        val lines = csvText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) return emptyList()

        val list = mutableListOf<ScheduleEventEntity>()
        val firstLineCols = lines.first().split(",").map { it.trim().lowercase() }
        val hasHeader = firstLineCols.any {
            it.contains("day") || it.contains("time") || it.contains("title") || it.contains("location")
        }

        val dataLines = if (hasHeader) lines.drop(1) else lines

        dataLines.forEachIndexed { index, line ->
            val cols = line.split(",").map { it.trim() }
            if (cols.size >= 3) {
                val dayNo = cols[0].toIntOrNull() ?: 1
                val timeSlot = cols.getOrElse(1) { "08:00 - 09:00" }
                val titleBn = cols.getOrElse(2) { "কার্যক্রম ${index + 1}" }
                val titleEn = cols.getOrElse(3) { titleBn }
                val location = cols.getOrElse(4) { "প্রধান প্রাঙ্গণ" }
                val category = cols.getOrElse(5) { "Station" }

                list.add(
                    ScheduleEventEntity(
                        id = (dayNo * 100 + index + 1).toLong(),
                        dayNo = dayNo,
                        timeSlot = timeSlot,
                        titleBn = titleBn,
                        titleEn = titleEn,
                        category = category,
                        location = location,
                        isCurrent = false,
                        isCompleted = false
                    )
                )
            }
        }
        return list
    }

    /**
     * Bulk uploads schedule events to Firestore 'schedules' collection
     * and persists locally in Room database for offline operation.
     */
    suspend fun bulkUploadSchedules(
        events: List<ScheduleEventEntity>,
        adminEmail: String = "admin@lsc.org",
        campDao: CampDao? = null
    ): ScheduleBulkUploadResult = withContext(Dispatchers.IO) {
        if (events.isEmpty()) {
            return@withContext ScheduleBulkUploadResult(
                totalParsed = 0,
                successCount = 0,
                failureCount = 0,
                events = emptyList(),
                errors = listOf("কোন শিডিউল ইভেন্ট পাওয়া যায়নি।"),
                message = "কোন ইভেন্ট আপলোড করা হয়নি"
            )
        }

        try {
            // 1. Bulk save to Firestore 'schedules' collection
            val savedCount = FirestoreManager.bulkSaveSchedules(events)

            // 2. Persist locally to Room
            campDao?.insertScheduleEvents(events)

            // 3. Log Audit Trail to Firestore 'audit_logs' collection
            FirestoreManager.saveAuditLog(
                AuditLogEntity(
                    timestamp = System.currentTimeMillis(),
                    userEmail = adminEmail,
                    actionType = "SCHEDULE_BULK_UPLOAD",
                    targetEntity = "schedules",
                    description = "বাল্ক শিডিউল আপলোড: ${events.size}টি ইভেন্ট ফায়ারস্টোর 'schedules' কালেকশনে সংরক্ষিত হয়েছে"
                )
            )

            ScheduleBulkUploadResult(
                totalParsed = events.size,
                successCount = events.size,
                failureCount = 0,
                events = events,
                message = "সফলভাবে ${events.size}টি শিডিউল ইভেন্ট ফায়ারস্টোর 'schedules' কালেকশনে সংরক্ষিত হয়েছে।"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error bulk uploading schedules to Firestore: ${e.message}", e)
            campDao?.insertScheduleEvents(events)

            ScheduleBulkUploadResult(
                totalParsed = events.size,
                successCount = events.size,
                failureCount = 0,
                events = events,
                errors = listOf("ফায়ারস্টোর সিঙ্ক বিলম্বিত হয়েছে: ${e.message} (লোকাল ডাটাবেজে সংরক্ষিত)"),
                message = "${events.size}টি শিডিউল লোকাল ডাটাবেজে সংরক্ষিত হয়েছে (ফায়ারস্টোরে পরে সিঙ্ক হবে)"
            )
        }
    }
}
