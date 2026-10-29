package com.example.ui.screens.dashboard

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CampLanguage
import com.example.data.model.ParticipantEntity
import com.example.data.model.RoomEntity
import com.example.ui.theme.LscEmeraldPrimary
import com.example.ui.theme.LscSlateDark

data class RoomOccupancySummary(
    val totalRooms: Int,
    val totalBedsCapacity: Int,
    val totalOccupiedBeds: Int,
    val totalAvailableBeds: Int,
    val overallOccupancyRate: Float,
    val fullyOccupiedRoomsCount: Int,
    val partiallyAvailableRoomsCount: Int
)

private val banglaDigitMap = mapOf(
    '০' to '0', '১' to '1', '২' to '2', '৩' to '3', '৪' to '4',
    '৫' to '5', '৬' to '6', '৭' to '7', '৮' to '8', '৯' to '9'
)

fun normalizeRoomDigits(raw: String): String =
    raw.map { banglaDigitMap[it] ?: it }.joinToString("").filter { it.isDigit() }

private fun normalizeRoomAsciiLetters(raw: String): String =
    raw.filter { it.isLetter() && it.code < 128 }.lowercase()

fun matchParticipantsToRoom(
    room: RoomEntity,
    participants: List<ParticipantEntity>
): List<ParticipantEntity> {
    val roomTrimmed = room.roomNo.trim()
    val roomDigits = normalizeRoomDigits(roomTrimmed)
    val roomLetters = normalizeRoomAsciiLetters(roomTrimmed)
    return participants.filter { p ->
        val pTrimmed = p.roomNo.trim()
        if (pTrimmed.isEmpty()) return@filter false
        if (pTrimmed.equals(roomTrimmed, ignoreCase = true)) return@filter true
        val pRoomDigits = normalizeRoomDigits(pTrimmed)
        val pLetters = normalizeRoomAsciiLetters(pTrimmed)
        // Match pure Bangla/English room numbers (e.g., "১০১" == "101") without colliding "P1-Down-A" vs "P1-Down-B"
        (roomDigits.isNotBlank() && pRoomDigits == roomDigits && pLetters == roomLetters) ||
            (roomLetters.isEmpty() && pLetters.isEmpty() && pTrimmed.contains(roomTrimmed, ignoreCase = true))
    }
}

fun calculateRoomOccupancy(
    rooms: List<RoomEntity>,
    participants: List<ParticipantEntity>
): RoomOccupancySummary {
    val totalRooms = rooms.size
    val totalBeds = rooms.sumOf { it.capacity }

    var occupiedBeds = 0
    var fullRooms = 0
    var availableRooms = 0

    rooms.forEach { room ->
        val directMatches = matchParticipantsToRoom(room, participants)
        val assigned = if (directMatches.isNotEmpty()) {
            directMatches.size
        } else if (room.assignedGroupId.equals("staff", ignoreCase = true) || room.assignedGroupId.equals("guest", ignoreCase = true)) {
            room.assignedCount
        } else if (participants.isNotEmpty()) {
            directMatches.size
        } else {
            room.assignedCount
        }
        val effectiveAssigned = assigned.coerceAtMost(room.capacity)
        occupiedBeds += effectiveAssigned
        if (effectiveAssigned >= room.capacity) {
            fullRooms++
        } else {
            availableRooms++
        }
    }

    val availableBeds = (totalBeds - occupiedBeds).coerceAtLeast(0)
    val rate = if (totalBeds > 0) (occupiedBeds.toFloat() / totalBeds.toFloat()) * 100f else 0f

    return RoomOccupancySummary(
        totalRooms = totalRooms,
        totalBedsCapacity = totalBeds,
        totalOccupiedBeds = occupiedBeds,
        totalAvailableBeds = availableBeds,
        overallOccupancyRate = rate,
        fullyOccupiedRoomsCount = fullRooms,
        partiallyAvailableRoomsCount = availableRooms
    )
}

/**
 * Visual Room & Bed Occupancy Matrix Card.
 * Gives camp administrators & Evaluation Dept a live layout of Rangamati Waterfront Resort cottages
 * (Platinum 1-6, Golden 1-7, Rajonigandha/Silver), room capacity, available beds, bed roster,
 * and 1-tap Room Inspection Cleanliness Scoring (Table 25 & Day 2 Group Inspection).
 */
@Composable
fun VisualRoomOccupancyMatrixCard(
    rooms: List<RoomEntity>,
    participants: List<ParticipantEntity>,
    language: CampLanguage,
    onNavigateToRooms: () -> Unit = {},
    onUpdateRoomInspection: ((RoomEntity, Int, String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedBuildingFilter by remember { mutableStateOf("ALL") }
    var selectedRoomForDetail by remember { mutableStateOf<RoomEntity?>(null) }
    var showAllRoomsInSummary by remember { mutableStateOf(false) }

    val buildings = remember(rooms) {
        listOf("ALL") + rooms.map { it.buildingName }.distinct()
    }

    val summary = remember(rooms, participants) {
        calculateRoomOccupancy(rooms, participants)
    }

    val filteredRooms = remember(rooms, selectedBuildingFilter) {
        if (selectedBuildingFilter == "ALL") rooms
        else rooms.filter { it.buildingName.equals(selectedBuildingFilter, ignoreCase = true) }
    }

    val displayedRooms = remember(filteredRooms, selectedBuildingFilter, showAllRoomsInSummary) {
        if (selectedBuildingFilter == "ALL" && !showAllRoomsInSummary && filteredRooms.size > 12) {
            filteredRooms.take(12)
        } else {
            filteredRooms
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_room_matrix_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2563EB).copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MeetingRoom,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "রিসোর্ট আবাসন ও রুম পরিদর্শন গ্রিড"
                                else
                                    "Resort Dormitory & Inspection Matrix",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = LscSlateDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Platinum • Golden • Silver",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D4ED8),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "রাঙামাটি ওয়াটারফ্রন্ট রিসোর্ট (শফিপুর, গাজীপুর) — ৭১টি রুমের লাইভ বেড ও পরিদর্শন নম্বর"
                            else
                                "Rangamati Waterfront Resort — 71 rooms live occupancy & inspection score",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                TextButton(
                    onClick = onNavigateToRooms,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (language == CampLanguage.BANGLA) "সব দেখুন" else "View All",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Resort Capacity KPI Summary Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${summary.totalRooms}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = LscSlateDark
                    )
                    Text(
                        text = if (language == CampLanguage.BANGLA) "মোট রুম" else "Total Rooms",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Box(modifier = Modifier.height(24.dp).width(1.dp).background(Color(0xFFE2E8F0)))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${summary.totalOccupiedBeds} / ${summary.totalBedsCapacity}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669)
                    )
                    Text(
                        text = if (language == CampLanguage.BANGLA) "বরাদ্দ বেড" else "Occupied Beds",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Box(modifier = Modifier.height(24.dp).width(1.dp).background(Color(0xFFE2E8F0)))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${summary.totalAvailableBeds}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (summary.totalAvailableBeds > 0) Color(0xFF2563EB) else Color.Gray
                    )
                    Text(
                        text = if (language == CampLanguage.BANGLA) "অতিরিক্ত বেড" else "Spare Beds",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Box(modifier = Modifier.height(24.dp).width(1.dp).background(Color(0xFFE2E8F0)))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${"%.0f".format(summary.overallOccupancyRate)}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7C3AED)
                    )
                    Text(
                        text = if (language == CampLanguage.BANGLA) "অকুপেন্সি রেট" else "Occupancy Rate",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Building / Cottage Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth().testTag("building_filter_chips_row")
            ) {
                items(buildings) { building ->
                    val isSelected = selectedBuildingFilter == building
                    val label = when (building) {
                        "ALL" -> if (language == CampLanguage.BANGLA) "সকল ভবন (${rooms.size})" else "All Cottages (${rooms.size})"
                        else -> building
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedBuildingFilter = building },
                        label = { Text(text = label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2563EB),
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("filter_chip_$building")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2-Column Visual Room Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.testTag("rooms_matrix_grid")
            ) {
                displayedRooms.chunked(2).forEach { roomPair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        roomPair.forEach { room ->
                            val directMatches = remember(room, participants) {
                                matchParticipantsToRoom(room, participants)
                            }
                            val assignedCount = if (directMatches.isNotEmpty()) {
                                directMatches.size.coerceAtMost(room.capacity)
                            } else if (room.assignedGroupId.equals("staff", ignoreCase = true) || room.assignedGroupId.equals("guest", ignoreCase = true)) {
                                room.assignedCount.coerceAtMost(room.capacity)
                            } else if (participants.isNotEmpty()) {
                                directMatches.size.coerceAtMost(room.capacity)
                            } else {
                                room.assignedCount.coerceAtMost(room.capacity)
                            }
                            val isFull = assignedCount >= room.capacity
                            val emptyBeds = (room.capacity - assignedCount).coerceAtLeast(0)

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = BorderStroke(
                                    1.dp,
                                    if (isFull) Color(0xFFFECACA) else Color(0xFFBFDBFE)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedRoomForDetail = room }
                                    .testTag("room_card_${room.roomNo}")
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = room.roomNo,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = LscSlateDark,
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f)
                                        )

                                        // Full or Available Bed Badge
                                        Surface(
                                            color = if (isFull) Color(0xFFFEE2E2) else Color(0xFFECFDF5),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (isFull)
                                                    (if (language == CampLanguage.BANGLA) "পূর্ণ" else "FULL")
                                                else
                                                    "$emptyBeds ${if (language == CampLanguage.BANGLA) "খালি" else "free"}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isFull) Color(0xFFDC2626) else Color(0xFF059669),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${room.buildingName} • ${room.floor}",
                                            fontSize = 10.sp,
                                            color = Color.Gray,
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (room.cleanlinessScore > 0) {
                                            Surface(
                                                color = Color(0xFFFEF3C7),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "★ ${room.cleanlinessScore}/10",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFB45309),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Bed Capacity Progress Bar
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (language == CampLanguage.BANGLA) "বেড অকুপেন্সি" else "Beds",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = "$assignedCount / ${room.capacity}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isFull) Color(0xFFDC2626) else Color(0xFF2563EB)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    LinearProgressIndicator(
                                        progress = { (assignedCount.toFloat() / room.capacity.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = if (isFull) Color(0xFFDC2626) else Color(0xFF2563EB),
                                        trackColor = Color(0xFFE2E8F0)
                                    )

                                    if (room.inChargeName.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "👤 ${room.inChargeName}",
                                            fontSize = 10.sp,
                                            color = LscSlateDark,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                        if (roomPair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            if (selectedBuildingFilter == "ALL" && filteredRooms.size > 12) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showAllRoomsInSummary = !showAllRoomsInSummary },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (showAllRoomsInSummary) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showAllRoomsInSummary) {
                            if (language == CampLanguage.BANGLA) "সংক্ষিপ্ত ভিউ দেখুন" else "Collapse Rooms"
                        } else {
                            if (language == CampLanguage.BANGLA) "বাকি ${filteredRooms.size - 12}টি রুম দেখুন (মোট ${filteredRooms.size}টি)" else "Show All ${filteredRooms.size} Rooms"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Interactive Room Bed Roster & Evaluation Inspection Dialog
    selectedRoomForDetail?.let { room ->
        val latestRoom = rooms.find { it.id == room.id } ?: room
        val roomOccupants = remember(latestRoom, participants, rooms) {
            val directMatches = matchParticipantsToRoom(latestRoom, participants)

            if (directMatches.isNotEmpty()) {
                directMatches.take(latestRoom.capacity)
            } else if (latestRoom.assignedGroupId.isNotBlank() &&
                !latestRoom.assignedGroupId.equals("staff", ignoreCase = true) &&
                !latestRoom.assignedGroupId.equals("guest", ignoreCase = true)
            ) {
                val groupParticipants = participants.filter { it.groupId == latestRoom.assignedGroupId }
                val roomsInSameGroup = rooms.filter { it.assignedGroupId == latestRoom.assignedGroupId }
                val roomIndex = roomsInSameGroup.indexOf(latestRoom).coerceAtLeast(0)
                val startIndex = roomIndex * latestRoom.capacity
                groupParticipants.drop(startIndex).take(latestRoom.capacity)
            } else {
                emptyList()
            }
        }

        RoomBedRosterDialog(
            room = latestRoom,
            occupants = roomOccupants,
            language = language,
            onSaveInspection = onUpdateRoomInspection,
            onDismiss = { selectedRoomForDetail = null }
        )
    }
}

/**
 * Modal Dialog showing detailed bed-by-bed allocation matrix & 1-tap Room Inspection Score
 * (Evaluation & Monitoring Committee — Table 25, Duty #7).
 */
@Composable
fun RoomBedRosterDialog(
    room: RoomEntity,
    occupants: List<ParticipantEntity>,
    language: CampLanguage,
    onSaveInspection: ((RoomEntity, Int, String) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var inspectionScore by remember(room.id, room.cleanlinessScore) { mutableIntStateOf(room.cleanlinessScore) }
    var inspectionNote by remember(room.id, room.notes) { mutableStateOf(room.notes) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("room_bed_roster_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Hotel, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${room.roomNo} • ${room.buildingName}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = LscSlateDark
                            )
                        }
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "${room.floor} • ধারণক্ষমতা: ${room.capacity} বেড • বর্তমান: ${occupants.size} জন"
                            else
                                "${room.floor} • Capacity: ${room.capacity} Beds • Occupied: ${occupants.size}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // In-Charge Contact Card
                if (room.inChargeName.isNotBlank()) {
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "সাব-গ্রুপ / রুম ইনচার্জ" else "Room In-Charge",
                                    fontSize = 10.sp,
                                    color = Color(0xFF1D4ED8),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = room.inChargeName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LscSlateDark
                                )
                                Text(
                                    text = room.inChargePhone,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Gray
                                )
                            }
                            if (room.inChargePhone.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${room.inChargePhone}"))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            android.util.Log.w("RoomBedRosterDialog", "Unable to launch dialer: ${e.message}")
                                        }
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2563EB))
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = "Call In-charge", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                // 1-Tap Room Cleanliness & Inspection Scoring (Table 25 — Evaluation Dept)
                if (onSaveInspection != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "গ্রুপ আবাসন পরিদর্শন নম্বর (০–১০)" else "Room Inspection Score (0–10)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = "★ $inspectionScore / 10",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFB45309)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(6, 7, 8, 9, 10).forEach { scoreOpt ->
                                    val selected = inspectionScore == scoreOpt
                                    Surface(
                                        color = if (selected) Color(0xFFD97706) else Color.White,
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, if (selected) Color(0xFFD97706) else Color(0xFFFCD34D)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                inspectionScore = scoreOpt
                                                onSaveInspection(room, scoreOpt, inspectionNote)
                                            }
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "$scoreOpt",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (selected) Color.White else Color(0xFF92400E)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (language == CampLanguage.BANGLA) "বেডভিত্তিক বরাদ্দ তালিকা (Bed Roster)" else "Bed Allocation Roster",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LscSlateDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bed-by-Bed Occupant Matrix List
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.testTag("bed_roster_list")
                ) {
                    for (bedIndex in 1..room.capacity) {
                        val occupant = occupants.getOrNull(bedIndex - 1)
                        val bedLabel = "Bed-%02d".format(bedIndex)

                        Surface(
                            color = if (occupant != null) Color.White else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (occupant != null) Color(0xFFCBD5E1) else Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth().testTag("bed_item_$bedIndex")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = if (occupant != null) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = bedLabel,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (occupant != null) Color(0xFF1D4ED8) else Color.Gray,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    if (occupant != null) {
                                        Column {
                                            Text(
                                                text = if (language == CampLanguage.BANGLA) occupant.nameBn else occupant.nameEn.ifBlank { occupant.nameBn },
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = LscSlateDark
                                            )
                                            Text(
                                                text = if (language == CampLanguage.BANGLA)
                                                    "${occupant.campCode} • ${occupant.subgroup} (${occupant.districtBn})"
                                                else
                                                    "${occupant.campCode} • ${occupant.subgroup} (${occupant.districtEn})",
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    } else {
                                        val specialNote = when {
                                            room.assignedGroupId.equals("staff", ignoreCase = true) ->
                                                if (language == CampLanguage.BANGLA) "স্টাফ আবাসন (Golden-1)" else "Staff Accommodation"
                                            room.assignedGroupId.equals("guest", ignoreCase = true) ->
                                                if (language == CampLanguage.BANGLA) "অতিথি আবাসন (Golden-7)" else "Guest Accommodation"
                                            else ->
                                                if (language == CampLanguage.BANGLA) "খালি বেড (Available)" else "Vacant Bed"
                                        }
                                        Text(
                                            text = specialNote,
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    }
                                }

                                if (occupant != null) {
                                    Surface(
                                        color = if (occupant.isCheckedIn) Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (occupant.isCheckedIn)
                                                (if (language == CampLanguage.BANGLA) "✓ উপস্থিত" else "✓ Checked-In")
                                            else
                                                (if (language == CampLanguage.BANGLA) "পেন্ডিং" else "Pending"),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (occupant.isCheckedIn) Color(0xFF059669) else Color(0xFFD97706),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (language == CampLanguage.BANGLA) "ঠিক আছে" else "Close")
                }
            }
        }
    }
}
