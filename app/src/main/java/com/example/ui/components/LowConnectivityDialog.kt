package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CachedSyncItemEntity
import com.example.data.model.CampLanguage
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LowConnectivityDialog(
    language: CampLanguage,
    isLowConnectivityMode: Boolean,
    isNetworkAvailable: Boolean,
    isAutoSyncEnabled: Boolean,
    isSyncing: Boolean,
    pendingSyncCount: Int,
    lastSyncTime: Long?,
    cachedItems: List<CachedSyncItemEntity>,
    onToggleLowConnectivity: (Boolean) -> Unit,
    onToggleAutoSync: (Boolean) -> Unit,
    onTriggerSync: () -> Unit,
    onSimulateNetwork: (Boolean) -> Unit,
    onResetSimulation: () -> Unit,
    onClearSyncedHistory: () -> Unit,
    onDismiss: () -> Unit
) {
    val isBangla = language == CampLanguage.BANGLA
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val fullDateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .testTag("low_connectivity_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isLowConnectivityMode) Color(0xFFFEF3C7) else Color(0xFFD1FAE5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isLowConnectivityMode) Icons.Default.CloudOff else Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = if (isLowConnectivityMode) Color(0xFFD97706) else Color(0xFF059669),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBangla) "কম কানেক্টিভিটি ও লোকাল ক্যাশ" else "Low Connectivity & Cache",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isBangla) "অফলাইন ডেটা ক্যাশ ও স্বয়ংক্রিয় সিঙ্ক" else "Local Caching & Auto-Sync",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_low_conn_dialog_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mode & Network Status Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Switch 1: Low Connectivity Mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBangla) "কম কানেক্টিভিটি মোড (Low Connectivity)" else "Low Connectivity Mode",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isBangla)
                                        "চালু থাকলে সকল হাজিরা ও স্বাস্থ্য লগ ডিভাইসে সুরক্ষিতভাবে ক্যাশ হবে"
                                    else
                                        "Caches all attendance and health logs on device before syncing",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isLowConnectivityMode,
                                onCheckedChange = onToggleLowConnectivity,
                                modifier = Modifier.testTag("low_conn_mode_switch")
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Network State & Auto-Sync
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isNetworkAvailable) Color(0xFF10B981) else Color(0xFFEF4444))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isNetworkAvailable) {
                                        if (isBangla) "ইন্টারনেট সংযোগ সক্রিয়" else "Internet Connected"
                                    } else {
                                        if (isBangla) "ইন্টারনেট সংযোগ নেই" else "Offline / Disconnected"
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isNetworkAvailable) Color(0xFF047857) else Color(0xFFB91C1C)
                                )
                            }

                            // Auto-sync toggle
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isBangla) "স্বয়ংক্রিয় সিঙ্ক" else "Auto-Sync",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = isAutoSyncEnabled,
                                    onCheckedChange = onToggleAutoSync,
                                    modifier = Modifier.testTag("auto_sync_switch")
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sync Action Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isBangla) "অপেক্ষমান রেকর্ড:" else "Pending records:",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (pendingSyncCount > 0) Color(0xFFEF4444) else Color(0xFF10B981),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "$pendingSyncCount",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            if (lastSyncTime != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isBangla) "সর্বশেষ সিঙ্ক: ${timeFormatter.format(Date(lastSyncTime))}" else "Last synced: ${timeFormatter.format(Date(lastSyncTime))}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = onTriggerSync,
                            enabled = !isSyncing,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                            modifier = Modifier.testTag("sync_now_btn")
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBangla) "সিঙ্ক হচ্ছে..." else "Syncing...", fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBangla) "এখনই সিঙ্ক" else "Sync Now", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Network Simulation Controls (for test/offline camp demonstration)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { onSimulateNetwork(true) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("simulate_online_btn")
                    ) {
                        Text(if (isBangla) "অনলাইন সিমুলেট" else "Simulate Online", fontSize = 11.sp, maxLines = 1)
                    }

                    OutlinedButton(
                        onClick = { onSimulateNetwork(false) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("simulate_offline_btn")
                    ) {
                        Text(if (isBangla) "অফলাইন সিমুলেট" else "Simulate Offline", fontSize = 11.sp, maxLines = 1)
                    }

                    TextButton(
                        onClick = onResetSimulation,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(if (isBangla) "রিসেট" else "Reset", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Cached Queue List Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isBangla) "লোকাল ক্যাশ সারি (${cachedItems.size})" else "Local Cache Queue (${cachedItems.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    val hasSyncedItems = cachedItems.any { it.status == CachedSyncItemEntity.STATUS_SYNCED }
                    if (hasSyncedItems) {
                        TextButton(
                            onClick = onClearSyncedHistory,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(if (isBangla) "সিঙ্ককৃত মুছুন" else "Clear Synced", fontSize = 11.sp, color = LscSlateDark)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Cached Items List
                if (cachedItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isBangla)
                                    "কোনো অপেক্ষমান অফলাইন ক্যাশ নেই"
                                else
                                    "No cached items pending sync",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (isBangla)
                                    "সকল হাজিরা স্ক্যান ও স্বাস্থ্য রেকর্ড মূল ডেটাবেজের সাথে সিঙ্ক আছে।"
                                else
                                    "All attendance and health entries are up to date.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("cached_sync_items_list"),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cachedItems, key = { it.id }) { item ->
                            CachedItemRow(
                                item = item,
                                dateFormatter = fullDateFormatter,
                                isBangla = isBangla
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CachedItemRow(
    item: CachedSyncItemEntity,
    dateFormatter: SimpleDateFormat,
    isBangla: Boolean
) {
    val isPending = item.status == CachedSyncItemEntity.STATUS_PENDING

    val typeColor = when (item.itemType) {
        CachedSyncItemEntity.TYPE_ATTENDANCE -> Color(0xFF0284C7)
        CachedSyncItemEntity.TYPE_HEALTH_LOG -> Color(0xFF059669)
        CachedSyncItemEntity.TYPE_HEALTH_CHECKUP -> Color(0xFFD97706)
        else -> Color.Gray
    }

    val typeLabel = when (item.itemType) {
        CachedSyncItemEntity.TYPE_ATTENDANCE -> if (isBangla) "হাজিরা স্ক্যান" else "Attendance"
        CachedSyncItemEntity.TYPE_HEALTH_LOG -> if (isBangla) "স্বাস্থ্য চেক-ইন" else "Health Log"
        CachedSyncItemEntity.TYPE_HEALTH_CHECKUP -> if (isBangla) "স্বাস্থ্য পরীক্ষা" else "Screening"
        else -> item.itemType
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPending) Color(0xFFFDE68A) else Color(0xFFE2E8F0)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Type pill
                Surface(
                    color = typeColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = typeLabel,
                        color = typeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Status pill
                Surface(
                    color = if (isPending) Color(0xFFFEF3C7) else Color(0xFFD1FAE5),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (isPending) Icons.Default.Schedule else Icons.Default.Check,
                            contentDescription = null,
                            tint = if (isPending) Color(0xFFD97706) else Color(0xFF059669),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isPending) {
                                if (isBangla) "অপেক্ষমান" else "Pending"
                            } else {
                                if (isBangla) "সিঙ্ক সম্পন্ন" else "Synced"
                            },
                            color = if (isPending) Color(0xFFD97706) else Color(0xFF059669),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Participant info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.participantName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "[${item.campCode}]",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.summary,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = dateFormatter.format(Date(item.cachedAt)),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}
