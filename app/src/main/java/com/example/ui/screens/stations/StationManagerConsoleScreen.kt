package com.example.ui.screens.stations

import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Official 45-Minute Rotation Slot Metadata (Days 2, 4, and 5: 09:00 AM – 01:30 PM).
 */
data class OfficialRotationSlot(
    val slotNumber: Int,
    val labelBn: String,
    val labelEn: String,
    val timeRange: String,
    val defaultRotationJson: String
)

val OFFICIAL_ROTATION_SLOTS = listOf(
    OfficialRotationSlot(1, "স্লট ১", "Slot 1", "০৯:০০ - ০৯:৪৫", "bukhari:1,sumaiya:2,shafeyi:3,umm_ayman:4,salahuddin:5,umm_saad:6"),
    OfficialRotationSlot(2, "স্লট ২", "Slot 2", "০৯:৪৫ - ১০:৩০", "umm_saad:1,bukhari:2,sumaiya:3,shafeyi:4,umm_ayman:5,salahuddin:6"),
    OfficialRotationSlot(3, "স্লট ৩", "Slot 3", "১০:৩০ - ১১:১৫", "salahuddin:1,umm_saad:2,bukhari:3,sumaiya:4,shafeyi:5,umm_ayman:6"),
    OfficialRotationSlot(4, "স্লট ৪", "Slot 4", "১১:১৫ - ১২:০০", "umm_ayman:1,salahuddin:2,umm_saad:3,bukhari:4,sumaiya:5,shafeyi:6"),
    OfficialRotationSlot(5, "স্লট ৫", "Slot 5", "১২:০০ - ১২:৪৫", "shafeyi:1,umm_ayman:2,salahuddin:3,umm_saad:4,bukhari:5,sumaiya:6"),
    OfficialRotationSlot(6, "স্লট ৬", "Slot 6", "১২:৪৫ - ০১:৩০", "sumaiya:1,shafeyi:2,umm_ayman:3,salahuddin:4,umm_saad:5,bukhari:6")
)

fun groupDisplayNameBn(groupId: String): String = when (groupId.lowercase()) {
    "bukhari" -> "বুখারী গ্রুপ"
    "shafeyi" -> "শাফে’য়ী গ্রুপ"
    "salahuddin" -> "সালাহউদ্দিন আয়ুবী গ্রুপ"
    "sumaiya" -> "সুমাইয়া গ্রুপ"
    "umm_ayman" -> "উম্মে আইমান গ্রুপ"
    "umm_saad" -> "উম্মে সা’দ (হাম্বলী) গ্রুপ"
    else -> groupId
}

/**
 * Dedicated 1-Screen Live Command Cockpit for the Station Manager (আবদুল মোহাইমিন নাঈম),
 * Station Moderators, Subgroup In-charges, and Camp Chief.
 * Controls all 6 Stations × 6 (45-minute) Rotation Slots across Days 2, 4, and 5 in 1–2 taps.
 */
@Composable
fun StationManagerConsoleScreen(
    language: CampLanguage,
    stations: List<StationEntity>,
    groups: List<CampGroupEntity>,
    participants: List<ParticipantEntity>,
    scheduleEvents: List<ScheduleEventEntity>,
    selectedDay: Int,
    onDaySelect: (Int) -> Unit,
    onOpenScannerForStation: (Int) -> Unit,
    onBatchSubgroupAttendance: (
        eventId: Long,
        eventTitleBn: String,
        dayNo: Int,
        subgroupKey: String,
        absentIds: Set<Long>,
        stationSealCode: String?
    ) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isBangla = language == CampLanguage.BANGLA

    // Official station rotation runs on Days 2 (23 Oct), 4 (25 Oct), and 5 (26 Oct)
    val activeRotationDay = if (selectedDay in listOf(2, 4, 5)) selectedDay else 2
    var selectedSlotIndex by remember { mutableIntStateOf(0) }
    val activeSlot = OFFICIAL_ROTATION_SLOTS[selectedSlotIndex]

    // 45-Minute Live Countdown Timer State
    var remainingSeconds by remember { mutableIntStateOf(45 * 60) }
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds -= 1
            if (remainingSeconds == 0) {
                isTimerRunning = false
                try {
                    ToneGenerator(AudioManager.STREAM_NOTIFICATION, 95)
                        .startTone(ToneGenerator.TONE_PROP_ACK, 600)
                } catch (_: Exception) {}
                Toast.makeText(
                    context,
                    if (isBangla) "🔔 ৪৫ মিনিট সম্পন্ন! পরবর্তী স্টেশনে রোটেশন বেল বাজান।" else "🔔 45 mins up! Rotate groups to next station.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Batch Subgroup Roll-Call Modal Target State
    var batchModalTarget by remember { mutableStateOf<Pair<StationEntity, String>?>(null) }

    // Resolve rotation mapping for active day + slot
    val matchingScheduleEvent = remember(scheduleEvents, activeRotationDay, selectedSlotIndex) {
        val dayStationEvents = scheduleEvents
            .filter { it.dayNo == activeRotationDay && it.rotationJson.isNotBlank() }
        dayStationEvents.getOrNull(selectedSlotIndex)
    }
    val rotationJson = matchingScheduleEvent?.rotationJson ?: activeSlot.defaultRotationJson
    val groupByStationId = remember(rotationJson) {
        rotationJson.split(",").mapNotNull { token ->
            val parts = token.split(":")
            if (parts.size == 2) {
                val gId = parts[0].trim()
                val sId = parts[1].trim().toIntOrNull()
                if (sId != null) sId to gId else null
            } else null
        }.toMap()
    }

    val totalSealsForDay = remember(participants, activeRotationDay) {
        val daySuffix = "D$activeRotationDay"
        participants.sumOf { p ->
            p.completedStationSeals.split(",")
                .count { it.trim().endsWith(daySuffix, ignoreCase = true) }
        }
    }
    val targetSealsForDay = (participants.size.coerceAtLeast(609)) * 6

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("station_manager_console"),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Live Command Header + 45-Min Rotation Bell Timer
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (isBangla) "⚡ স্টেশন ম্যানেজার লাইভ কনসোল" else "⚡ STATION MANAGER LIVE CONSOLE",
                                        color = Color(0xFF34D399),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBangla)
                                    "আবদুল মোহাইমিন নাঈম • ৬টি স্টেশন × ৪৫ মি. রোটেশন"
                                else
                                    "Abdul Mohaimin Nayeem • 6 Stations × 45-Min Rotation",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (isBangla)
                                    "দিন $activeRotationDay ★ স্টার সিল অগ্রগতি: $totalSealsForDay / $targetSealsForDay সিল"
                                else
                                    "Day $activeRotationDay ★ Star Seals: $totalSealsForDay / $targetSealsForDay",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }

                        // Bell & Timer Box
                        val mins = remainingSeconds / 60
                        val secs = remainingSeconds % 60
                        Surface(
                            color = if (isTimerRunning) Color(0xFF065F46) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = String.format("%02d:%02d", mins, secs),
                                    color = LscGoldLight,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = if (isTimerRunning) (if (isBangla) "⏸ থামান" else "Pause") else (if (isBangla) "▶ শুরু" else "Start"),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(LscEmeraldPrimary)
                                            .clickable { isTimerRunning = !isTimerRunning }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                            .testTag("station_timer_toggle_btn")
                                    )
                                    Text(
                                        text = "↺",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF475569))
                                            .clickable {
                                                isTimerRunning = false
                                                remainingSeconds = 45 * 60
                                            }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Rotation Day Quick Chips (Day 2, Day 4, Day 5) + Rotation Bell Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val rotationDays = listOf(
                            2 to (if (isBangla) "২য় দিন (২৩ অক্টো)" else "Day 2 (23 Oct)"),
                            4 to (if (isBangla) "৪র্থ দিন (২৫ অক্টো)" else "Day 4 (25 Oct)"),
                            5 to (if (isBangla) "৫ম দিন (২৬ অক্টো)" else "Day 5 (26 Oct)")
                        )
                        rotationDays.forEach { (dayNum, label) ->
                            val selected = activeRotationDay == dayNum
                            Surface(
                                color = if (selected) LscEmeraldPrimary else Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (selected) Color(0xFF34D399) else Color(0xFF334155)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onDaySelect(dayNum) }
                            ) {
                                Text(
                                    text = label,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Surface(
                            color = LscGoldSecondary,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    try {
                                        ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
                                            .startTone(ToneGenerator.TONE_PROP_BEEP2, 450)
                                    } catch (_: Exception) {}
                                    selectedSlotIndex = (selectedSlotIndex + 1) % OFFICIAL_ROTATION_SLOTS.size
                                    remainingSeconds = 45 * 60
                                    Toast.makeText(
                                        context,
                                        if (isBangla)
                                            "🔔 রোটেশন বেল! পরবর্তী স্লট: ${OFFICIAL_ROTATION_SLOTS[selectedSlotIndex].labelBn} (${OFFICIAL_ROTATION_SLOTS[selectedSlotIndex].timeRange})"
                                        else
                                            "🔔 Bell! Next: ${OFFICIAL_ROTATION_SLOTS[selectedSlotIndex].labelEn}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .testTag("station_rotation_bell_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isBangla) "বেল" else "Bell",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 6 × 45-Minute Slot Selector Row
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(OFFICIAL_ROTATION_SLOTS.size) { idx ->
                            val slot = OFFICIAL_ROTATION_SLOTS[idx]
                            val isSelected = idx == selectedSlotIndex
                            Surface(
                                color = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF93C5FD) else Color(0xFF334155)),
                                modifier = Modifier
                                    .clickable {
                                        selectedSlotIndex = idx
                                        remainingSeconds = 45 * 60
                                    }
                                    .testTag("rotation_slot_chip_${slot.slotNumber}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (isBangla) slot.labelBn else slot.labelEn,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = slot.timeRange,
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Live 6 Station Command Cards
        items(stations.sortedBy { it.id }, key = { it.id }) { station ->
            val assignedGroupId = groupByStationId[station.id] ?: "bukhari"
            val groupObj = groups.find { it.id.equals(assignedGroupId, ignoreCase = true) }
            val groupStudents = remember(participants, assignedGroupId) {
                participants.filter { it.groupId.equals(assignedGroupId, ignoreCase = true) }
            }
            val sealCode = "S${station.id}D$activeRotationDay"
            val sealedCount = remember(groupStudents, sealCode) {
                groupStudents.count { p ->
                    p.completedStationSeals.split(",").any { it.trim().equals(sealCode, ignoreCase = true) }
                }
            }
            val totalGroupCount = groupStudents.size.coerceAtLeast(groupObj?.memberCount ?: 1)
            val topicForDay = when (activeRotationDay) {
                2 -> station.day2TopicBn.ifBlank { station.objectiveBn }
                4 -> station.day4TopicBn.ifBlank { station.objectiveBn }
                5 -> station.day5TopicBn.ifBlank { station.objectiveBn }
                else -> station.objectiveBn
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("live_station_card_${station.id}")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Station Header + Assigned Group Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(LscEmeraldLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "S${station.id}",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = LscEmeraldPrimary,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isBangla) station.nameBn else station.nameEn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = LscSlateDark
                                )
                                Text(
                                    text = "📍 ${station.location} • মডারেটর: ${station.moderatorName}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }

                        // 1-Tap Call Group Leader / Moderator
                        val leaderPhone = groupObj?.leaderPhone.orEmpty()
                        if (leaderPhone.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    try {
                                        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$leaderPhone")))
                                    } catch (_: Exception) {}
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF))
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF1D4ED8), modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Today's Topic Box
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "📖 আজকের পাঠ্যক্রম (দিন $activeRotationDay): $topicForDay",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                lineHeight = 15.sp
                            )
                            if (station.subModerators.isNotBlank()) {
                                Text(
                                    text = "সহ-মডারেটর: ${station.subModerators}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Active Group & Star Seal Progress
                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "👥 বর্তমান দল: ${groupObj?.nameBn ?: groupDisplayNameBn(assignedGroupId)} (${groupStudents.size} জন)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF065F46)
                                )
                                Text(
                                    text = "★ সিল ($sealCode): $sealedCount / $totalGroupCount",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    color = LscEmeraldPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (sealedCount.toFloat() / totalGroupCount.toFloat()).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = LscEmeraldPrimary,
                                trackColor = Color(0xFFD1FAE5)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 1-Tap Action Buttons: Batch Subgroup Roll-Call + Rapid QR Gun
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { batchModalTarget = station to assignedGroupId },
                            colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1.35f)
                                .height(36.dp)
                                .testTag("batch_subgroup_btn_station_${station.id}")
                        ) {
                            Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBangla) "⚡ ১-ট্যাপ সাব-গ্রুপ হাজিরা ও ★" else "⚡ 1-Tap Subgroup Roll-Call",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }

                        OutlinedButton(
                            onClick = { onOpenScannerForStation(station.id) },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF1D4ED8)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(0.85f)
                                .height(36.dp)
                                .testTag("rapid_qr_btn_station_${station.id}")
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBangla) "📷 QR গান" else "📷 QR Gun",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }

    // 1-Tap Subgroup Batch Attendance & Star Seal Dialog
    batchModalTarget?.let { (targetStation, targetGroupId) ->
        val eventId = matchingScheduleEvent?.id ?: (activeRotationDay * 100L + targetStation.id)
        val sealCode = "S${targetStation.id}D$activeRotationDay"
        val titleBn = "স্টেশন-${targetStation.id} (${activeSlot.labelBn}) — ${groupDisplayNameBn(targetGroupId)}"

        SubgroupBatchAttendanceDialog(
            language = language,
            dialogTitleBn = "⚡ ১-ট্যাপ সাব-গ্রুপ হাজিরা ও ★ সিল ($sealCode)",
            dialogSubtitleBn = "${targetStation.nameBn} • ${activeSlot.labelBn} (${activeSlot.timeRange})",
            participants = participants,
            initialGroupId = targetGroupId,
            stationSealCode = sealCode,
            onDismiss = { batchModalTarget = null },
            onConfirmBatch = { subgroupKey, absentIds ->
                onBatchSubgroupAttendance(
                    eventId,
                    titleBn,
                    activeRotationDay,
                    subgroupKey,
                    absentIds,
                    sealCode
                )
            }
        )
    }
}

/**
 * Reusable Ultra-Fast 1-Tap Subgroup Batch Roll-Call Dialog.
 * All 20–31 students in the selected subgroup are pre-marked PRESENT by default;
 * the staff member only taps absent students (if any) and hits Save!
 */
@Composable
fun SubgroupBatchAttendanceDialog(
    language: CampLanguage,
    dialogTitleBn: String,
    dialogSubtitleBn: String,
    participants: List<ParticipantEntity>,
    initialGroupId: String? = null,
    initialSubgroup: String? = null,
    stationSealCode: String? = null,
    onDismiss: () -> Unit,
    onConfirmBatch: (subgroupKey: String, absentIds: Set<Long>) -> Unit
) {
    val isBangla = language == CampLanguage.BANGLA

    val availableSubgroups = remember(participants, initialGroupId) {
        val scoped = if (!initialGroupId.isNullOrBlank()) {
            participants.filter { it.groupId.equals(initialGroupId, ignoreCase = true) }
        } else {
            participants
        }
        scoped.map { it.subgroup }.filter { it.isNotBlank() }.distinct().sorted()
    }

    var selectedSubgroup by remember(availableSubgroups, initialSubgroup) {
        val matched = if (!initialSubgroup.isNullOrBlank()) {
            availableSubgroups.find { it.equals(initialSubgroup, ignoreCase = true) }
        } else null
        mutableStateOf(matched ?: availableSubgroups.firstOrNull().orEmpty())
    }
    val subgroupStudents = remember(participants, selectedSubgroup) {
        participants.filter { it.subgroup.equals(selectedSubgroup, ignoreCase = true) }
    }
    val absentIds = remember(selectedSubgroup) { mutableStateListOf<Long>() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.86f)
                .testTag("subgroup_batch_attendance_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBangla) dialogTitleBn else "⚡ 1-Tap Subgroup Batch Roll-Call",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = LscSlateDark
                        )
                        Text(
                            text = dialogSubtitleBn,
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Subgroup Selector Chips
                Text(
                    text = if (isBangla) "সাব-গ্রুপ নির্বাচন করুন (সকলে ডিফল্টভাবে উপস্থিত ✓):" else "Select Subgroup (All pre-marked Present ✓):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LscEmeraldPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(availableSubgroups) { sg ->
                        val isSel = sg == selectedSubgroup
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedSubgroup = sg },
                            label = { Text(sg, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LscEmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Live Summary Strip
                val presentCount = (subgroupStudents.size - absentIds.size).coerceAtLeast(0)
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "মোট: ${subgroupStudents.size} জন • উপস্থিত: $presentCount • অনুপস্থিত: ${absentIds.size}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF166534)
                        )
                        if (absentIds.isNotEmpty()) {
                            Text(
                                text = "সবাই উপস্থিত করুন",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8),
                                modifier = Modifier.clickable { absentIds.clear() }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isBangla)
                        "💡 শুধুমাত্র কেউ অনুপস্থিত থাকলে তার নামের পাশে ট্যাপ করুন — বাকি সবার হাজিরা ১ ট্যাপেই সম্পন্ন হবে!"
                    else
                        "💡 Tap ONLY absent students; everyone else stays Present!",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Student Checklist
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(subgroupStudents, key = { it.id }) { student ->
                        val isAbsent = absentIds.contains(student.id)
                        val alreadyHasSeal = stationSealCode != null &&
                            student.completedStationSeals.split(",").any { it.trim().equals(stationSealCode, ignoreCase = true) }

                        Surface(
                            color = if (isAbsent) Color(0xFFFEF2F2) else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isAbsent) Color(0xFFFCA5A5) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isAbsent) absentIds.remove(student.id) else absentIds.add(student.id)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = student.nameBn,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isAbsent) Color(0xFFB91C1C) else LscSlateDark
                                        )
                                        if (alreadyHasSeal) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "★ সিলপ্রাপ্ত",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = LscGoldDark
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${student.campCode} • রুম: ${student.roomNo} (${student.bedNo})",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.Gray
                                    )
                                }

                                Surface(
                                    color = if (isAbsent) Color(0xFFDC2626) else Color(0xFF10B981),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (isAbsent) "✗ অনুপস্থিত" else "✓ উপস্থিত",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 1-Tap Save & Stamp Star Seal Button
                Button(
                    onClick = {
                        if (selectedSubgroup.isNotBlank()) {
                            onConfirmBatch(selectedSubgroup, absentIds.toSet())
                            onDismiss()
                        }
                    },
                    enabled = selectedSubgroup.isNotBlank() && subgroupStudents.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_subgroup_batch_btn")
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (stationSealCode != null)
                            "✓ $selectedSubgroup ($presentCount জন) হাজিরা ও ★ সিল সংরক্ষণ করুন"
                        else
                            "✓ $selectedSubgroup ($presentCount জন) ব্যাচ হাজিরা সংরক্ষণ করুন",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
