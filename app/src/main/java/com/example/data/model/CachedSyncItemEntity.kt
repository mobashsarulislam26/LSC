package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for caching offline/low-connectivity actions locally before
 * synchronizing them to the remote/main database.
 */
@Entity(
    tableName = "cached_sync_items",
    indices = [
        Index(value = ["status"]),
        Index(value = ["itemType"]),
        Index(value = ["participantId"]),
        Index(value = ["cachedAt"])
    ]
)
data class CachedSyncItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemType: String,               // "ATTENDANCE_SCAN", "HEALTH_LOG", "HEALTH_CHECKUP"
    val participantId: Long,
    val campCode: String,
    val participantName: String,
    val summary: String,                // User-friendly text for UI logs & lists
    val payloadJson: String = "",       // Detailed JSON or serialized arguments
    val cachedAt: Long = System.currentTimeMillis(),
    val status: String = STATUS_PENDING, // "PENDING", "SYNCED"
    val syncedAt: Long? = null
) {
    companion object {
        const val TYPE_ATTENDANCE = "ATTENDANCE_SCAN"
        const val TYPE_HEALTH_LOG = "HEALTH_LOG"
        const val TYPE_HEALTH_CHECKUP = "HEALTH_CHECKUP"

        const val STATUS_PENDING = "PENDING"
        const val STATUS_SYNCED = "SYNCED"
    }
}
