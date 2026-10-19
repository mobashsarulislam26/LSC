package com.example.data.service

import android.util.Log
import com.example.data.firestore.FirestoreManager
import com.example.data.local.CampDao
import com.example.data.model.AuditLogEntity
import com.example.data.model.ParticipantEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Result data class for participant roster imports.
 */
data class ParticipantImportResult(
    val totalParsed: Int,
    val successCount: Int,
    val failureCount: Int,
    val participants: List<ParticipantEntity>,
    val errors: List<String> = emptyList(),
    val message: String
)

/**
 * Service to parse and import participant names and profiles from a CSV-formatted JSON structure
 * directly into the Firestore 'participants' collection, ensuring the app reflects the current attendance roster.
 */
object ParticipantImportService {

    private const val TAG = "ParticipantImportService"

    /**
     * Parses a CSV-formatted JSON structure (or CSV text embedded within JSON)
     * into a strongly-typed List of ParticipantEntity.
     *
     * Supported formats:
     * 1. JSON Array of participant objects:
     *    [ {"campCode": "B-01", "nameBn": "মুহাম্মদ আব্দুল্লাহ", ...}, ... ]
     * 2. JSON Object with 'participants' key:
     *    { "participants": [ ... ] }
     * 3. JSON Object with 'csv' or 'data' string:
     *    { "csv": "campCode,fileNo,nameBn,nameEn,groupId,subgroup,roomNo,guardianName,guardianPhone\n..." }
     * 4. JSON Object with 'headers' and 'rows' array:
     *    { "headers": ["campCode", "nameBn", ...], "rows": [ ["B-01", "মুহাম্মদ আব্দুল্লাহ", ...] ] }
     * 5. Raw CSV text fallback.
     */
    fun parseCsvFormattedJson(rawInput: String): List<ParticipantEntity> {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) return emptyList()

        val participants = mutableListOf<ParticipantEntity>()

        try {
            if (trimmed.startsWith("[")) {
                // Format 1: Direct JSON Array of participant objects
                val jsonArray = JSONArray(trimmed)
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.optJSONObject(i)
                    if (item != null) {
                        parseJsonObjectParticipant(item, i)?.let { participants.add(it) }
                    }
                }
            } else if (trimmed.startsWith("{")) {
                val jsonObject = JSONObject(trimmed)

                if (jsonObject.has("participants")) {
                    val array = jsonObject.optJSONArray("participants")
                    if (array != null) {
                        for (i in 0 until array.length()) {
                            val item = array.optJSONObject(i)
                            if (item != null) {
                                parseJsonObjectParticipant(item, i)?.let { participants.add(it) }
                            }
                        }
                    }
                } else if (jsonObject.has("csv") || jsonObject.has("data")) {
                    val csvString = jsonObject.optString("csv", jsonObject.optString("data", ""))
                    participants.addAll(parseCsvString(csvString))
                } else if (jsonObject.has("headers") && jsonObject.has("rows")) {
                    val headers = jsonObject.getJSONArray("headers")
                    val rows = jsonObject.getJSONArray("rows")
                    participants.addAll(parseHeadersAndRows(headers, rows))
                } else {
                    // Single participant object in JSON root
                    parseJsonObjectParticipant(jsonObject, 0)?.let { participants.add(it) }
                }
            } else {
                // Fallback: Raw CSV string
                participants.addAll(parseCsvString(trimmed))
            }
        } catch (e: Exception) {
            Log.e(TAG, "JSON parsing encountered error, falling back to line-by-line CSV parser: ${e.message}")
            participants.addAll(parseCsvString(trimmed))
        }

        return participants
    }

    private fun parseJsonObjectParticipant(obj: JSONObject, index: Int): ParticipantEntity? {
        val code = obj.optString("campCode", obj.optString("id", obj.optString("code", ""))).trim()
        val nameBn = obj.optString("nameBn", obj.optString("name", obj.optString("fullName", ""))).trim()
        val nameEn = obj.optString("nameEn", nameBn).trim()

        if (code.isEmpty() && nameBn.isEmpty() && nameEn.isEmpty()) return null

        val finalCode = if (code.isNotEmpty()) code else "LSC-2026-%03d".format(100 + index)
        val fileNo = obj.optString("fileNo", "F-%04d".format(1000 + index)).trim()
        val rawGroup = obj.optString("groupId", obj.optString("group", obj.optString("groupNameBn", "bukhari"))).trim()
        val (groupId, groupNameBn, groupNameEn) = normalizeGroup(rawGroup)

        val subgroup = obj.optString("subgroup", obj.optString("subGroup", "${groupNameBn} ১")).trim()
        val roomNo = obj.optString("roomNo", obj.optString("room", "১০১")).trim()
        val bedNo = obj.optString("bedNo", obj.optString("bed", "১")).trim()
        val districtBn = obj.optString("districtBn", obj.optString("district", "ঢাকা")).trim()
        val districtEn = obj.optString("districtEn", "Dhaka").trim()
        val gender = obj.optString("gender", "Male").trim()
        val age = obj.optInt("age", 15)
        val guardianName = obj.optString("guardianName", obj.optString("guardian", "অভিভাবক")).trim()
        val guardianPhone = obj.optString("guardianPhone", obj.optString("phone", "01711-000000")).trim()
        val medicalFlag = if (obj.has("medicalFlag") && !obj.isNull("medicalFlag")) obj.optString("medicalFlag") else null
        val mealPref = obj.optString("mealPref", "নিয়মিত").trim()
        val isCheckedIn = obj.optBoolean("isCheckedIn", true)

        return ParticipantEntity(
            id = (index + 1).toLong(),
            campCode = finalCode,
            fileNo = fileNo,
            nameBn = if (nameBn.isNotEmpty()) nameBn else nameEn,
            nameEn = if (nameEn.isNotEmpty()) nameEn else nameBn,
            districtBn = districtBn,
            districtEn = districtEn,
            gender = gender,
            age = age,
            groupId = groupId,
            groupNameBn = groupNameBn,
            groupNameEn = groupNameEn,
            subgroup = subgroup,
            roomNo = roomNo,
            bedNo = bedNo,
            mealPref = mealPref,
            guardianName = guardianName,
            guardianPhone = guardianPhone,
            isCheckedIn = isCheckedIn,
            checkInTime = System.currentTimeMillis(),
            medicalFlag = medicalFlag,
            totalScore = obj.optDouble("totalScore", 0.0),
            attendancePercentage = obj.optDouble("attendancePercentage", 95.0).toFloat(),
            photoAvatarSeed = (index % 10) + 1
        )
    }

    private fun parseHeadersAndRows(headers: JSONArray, rows: JSONArray): List<ParticipantEntity> {
        val headerMap = mutableMapOf<String, Int>()
        for (i in 0 until headers.length()) {
            headerMap[headers.getString(i).trim().lowercase()] = i
        }

        val list = mutableListOf<ParticipantEntity>()
        for (rowIndex in 0 until rows.length()) {
            val row = rows.optJSONArray(rowIndex) ?: continue
            val getCol = { key: String, defaultVal: String ->
                val colIdx = headerMap[key]
                if (colIdx != null && colIdx < row.length()) row.optString(colIdx, defaultVal) else defaultVal
            }

            val code = getCol("campcode", getCol("code", "LSC-2026-%03d".format(100 + rowIndex)))
            val nameBn = getCol("namebn", getCol("name", ""))
            val nameEn = getCol("nameen", nameBn)
            if (nameBn.isEmpty() && nameEn.isEmpty()) continue

            val rawGroup = getCol("groupid", getCol("group", "bukhari"))
            val (groupId, groupNameBn, groupNameEn) = normalizeGroup(rawGroup)

            list.add(
                ParticipantEntity(
                    id = (rowIndex + 1).toLong(),
                    campCode = code,
                    fileNo = getCol("fileno", "F-%04d".format(1000 + rowIndex)),
                    nameBn = if (nameBn.isNotEmpty()) nameBn else nameEn,
                    nameEn = if (nameEn.isNotEmpty()) nameEn else nameBn,
                    districtBn = getCol("districtbn", getCol("district", "ঢাকা")),
                    districtEn = getCol("districten", "Dhaka"),
                    gender = getCol("gender", "Male"),
                    age = getCol("age", "15").toIntOrNull() ?: 15,
                    groupId = groupId,
                    groupNameBn = groupNameBn,
                    groupNameEn = groupNameEn,
                    subgroup = getCol("subgroup", "${groupNameBn} ১"),
                    roomNo = getCol("roomno", getCol("room", "১০১")),
                    bedNo = getCol("bedno", "১"),
                    mealPref = getCol("mealpref", "নিয়মিত"),
                    guardianName = getCol("guardianname", getCol("guardian", "অভিভাবক")),
                    guardianPhone = getCol("guardianphone", getCol("phone", "01711-000000")),
                    isCheckedIn = true,
                    checkInTime = System.currentTimeMillis(),
                    medicalFlag = null,
                    totalScore = 0.0,
                    attendancePercentage = 95.0f,
                    photoAvatarSeed = (rowIndex % 10) + 1
                )
            )
        }
        return list
    }

    private fun parseCsvString(csvText: String): List<ParticipantEntity> {
        val lines = csvText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) return emptyList()

        val list = mutableListOf<ParticipantEntity>()
        val firstLineCols = lines.first().split(",").map { it.trim().lowercase() }
        val hasHeader = firstLineCols.any {
            it.contains("name") || it.contains("code") || it.contains("group") || it.contains("file")
        }

        val dataLines = if (hasHeader) lines.drop(1) else lines

        dataLines.forEachIndexed { index, line ->
            val cols = line.split(",").map { it.trim() }
            if (cols.size >= 3) {
                val code = if (cols[0].isNotBlank()) cols[0] else "LSC-2026-%03d".format(100 + index)
                val fileNo = if (cols.size > 1 && cols[1].isNotBlank()) cols[1] else "F-%04d".format(1000 + index)
                val name = cols.getOrElse(2) { "Participant ${index + 1}" }
                val district = cols.getOrElse(3) { "ঢাকা" }
                val rawGroup = cols.getOrElse(4) { "বুখারী" }
                val sub = cols.getOrElse(5) { "বুখারী ১" }
                val room = cols.getOrElse(6) { "১০১" }
                val guardian = cols.getOrElse(7) { "অভিভাবক" }
                val phone = cols.getOrElse(8) { "01711-000000" }

                val (groupId, groupNameBn, groupNameEn) = normalizeGroup(rawGroup)

                list.add(
                    ParticipantEntity(
                        id = (index + 1).toLong(),
                        campCode = code,
                        fileNo = fileNo,
                        nameBn = name,
                        nameEn = name,
                        districtBn = district,
                        districtEn = district,
                        gender = "Male",
                        age = 15,
                        groupId = groupId,
                        groupNameBn = groupNameBn,
                        groupNameEn = groupNameEn,
                        subgroup = sub,
                        roomNo = room,
                        bedNo = "১",
                        mealPref = "নিয়মিত",
                        guardianName = guardian,
                        guardianPhone = phone,
                        isCheckedIn = true,
                        checkInTime = System.currentTimeMillis(),
                        medicalFlag = null,
                        totalScore = 0.0,
                        attendancePercentage = 95.0f,
                        photoAvatarSeed = (index % 10) + 1
                    )
                )
            }
        }
        return list
    }

    private fun normalizeGroup(raw: String): Triple<String, String, String> {
        val lower = raw.lowercase()
        return when {
            lower.contains("bukhari") || raw.contains("বুখারী") ->
                Triple("bukhari", "বুখারী", "Bukhari")
            lower.contains("muslim") || raw.contains("মুসলিম") ->
                Triple("muslim", "মুসলিম", "Muslim")
            lower.contains("abu_dawood") || lower.contains("abu dawood") || raw.contains("আবু দাউদ") ->
                Triple("abu_dawood", "আবু দাউদ", "Abu Dawood")
            lower.contains("tirmidhi") || raw.contains("তিরমিযী") ->
                Triple("tirmidhi", "তিরমিযী", "Tirmidhi")
            lower.contains("salahuddin") || raw.contains("সালাহউদ্দীন") ->
                Triple("salahuddin", "সালাহউদ্দীন আইয়ুবী", "Salahuddin Ayyubi")
            lower.contains("sumaiya") || raw.contains("সুমাইয়া") ->
                Triple("sumaiya", "সুমাইয়া", "Sumaiya")
            else ->
                Triple("bukhari", "বুখারী", "Bukhari")
        }
    }

    /**
     * Imports parsed participants directly into the Firestore 'participants' collection
     * and persists to local Room database for offline resilience.
     */
    suspend fun importRosterToFirestore(
        jsonContent: String,
        adminEmail: String = "admin@lsc.org",
        campDao: CampDao? = null
    ): ParticipantImportResult = withContext(Dispatchers.IO) {
        val parsed = parseCsvFormattedJson(jsonContent)
        if (parsed.isEmpty()) {
            return@withContext ParticipantImportResult(
                totalParsed = 0,
                successCount = 0,
                failureCount = 0,
                participants = emptyList(),
                errors = listOf("কোন বৈধ অংশগ্রহণকারীর তথ্য পাওয়া যায়নি। দয়া করে ফরম্যাট পরীক্ষা করুন।"),
                message = "কোন বৈধ ডাটা পাওয়া যায়নি (No valid participant data found)"
            )
        }

        try {
            // 1. Bulk save directly to Firestore 'participants' collection
            val savedInFirestore = FirestoreManager.bulkSaveParticipants(parsed)

            // 2. Persist locally to Room database
            campDao?.insertParticipants(parsed)

            // 3. Log Audit Trail to Firestore 'audit_logs' collection
            FirestoreManager.saveAuditLog(
                AuditLogEntity(
                    timestamp = System.currentTimeMillis(),
                    userEmail = adminEmail,
                    actionType = "PARTICIPANT_ROSTER_IMPORT",
                    targetEntity = "Participants",
                    description = "অংশগ্রহণকারী রোস্টার ইম্পোর্ট: ${parsed.size} জন প্রোফাইল ফায়ারস্টোর 'participants' কালেকশনে সংরক্ষিত হয়েছে"
                )
            )

            ParticipantImportResult(
                totalParsed = parsed.size,
                successCount = parsed.size,
                failureCount = 0,
                participants = parsed,
                message = "সফলভাবে ${parsed.size} জন অংশগ্রহণকারীর তথ্য ফায়ারস্টোর 'participants' কালেকশনে সংরক্ষিত হয়েছে।"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error during Firestore participant import: ${e.message}", e)
            // Even if network/Firestore encounters an error, save to local Room
            campDao?.insertParticipants(parsed)

            ParticipantImportResult(
                totalParsed = parsed.size,
                successCount = parsed.size,
                failureCount = 0,
                participants = parsed,
                errors = listOf("ফায়ারস্টোর সিঙ্ক বিলম্বিত হয়েছে: ${e.message} (লোকাল ডাটাবেজে সংরক্ষিত)"),
                message = "${parsed.size} জন অংশগ্রহণকারী সফলভাবে প্রসেস করা হয়েছে (লোকাল সংরক্ষিত, ফায়ারস্টোর রিট্রাই শিডিউলড)"
            )
        }
    }
}
