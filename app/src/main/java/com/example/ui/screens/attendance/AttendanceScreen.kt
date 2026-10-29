package com.example.ui.screens.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.ExportAttendanceDialog
import com.example.ui.screens.stations.SubgroupBatchAttendanceDialog
import com.example.ui.theme.*

@Composable
fun AttendanceScreen(
    language: CampLanguage,
    attendanceEvents: List<AttendanceEventEntity>,
    participants: List<ParticipantEntity>,
    groups: List<CampGroupEntity>,
    onMarkAttendance: (eventId: Long, participant: ParticipantEntity, status: String) -> Unit,
    onOpenScanner: () -> Unit,
    attendanceRecords: List<AttendanceRecordEntity> = emptyList(),
    stations: List<StationEntity> = emptyList(),
    onBatchSubgroupAttendance: (
        eventId: Long,
        eventTitleBn: String,
        dayNo: Int,
        subgroupKey: String,
        absentIds: Set<Long>,
        stationSealCode: String?
    ) -> Unit = { _, _, _, _, _, _ -> }
) {
    var selectedEventId by remember(attendanceEvents) { mutableStateOf(attendanceEvents.firstOrNull()?.id ?: 2L) }
    var selectedGroupFilter by remember { mutableStateOf<String?>(null) }
    var selectedSubgroupFilter by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showOnlyAbsent by remember { mutableStateOf(false) }
    var showExportAttendanceDialog by remember { mutableStateOf(false) }
    var showBatchSubgroupModal by remember { mutableStateOf(false) }

    val currentEvent = attendanceEvents.find { it.id == selectedEventId } ?: attendanceEvents.firstOrNull()

    val availableSubgroups = remember(participants, selectedGroupFilter) {
        participants
            .filter { selectedGroupFilter == null || it.groupId == selectedGroupFilter }
            .map { it.subgroup }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Event Selector Tabs
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(attendanceEvents) { ev ->
                val isSelected = ev.id == selectedEventId
                Surface(
                    color = if (isSelected) LscEmeraldPrimary else Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) LscEmeraldPrimary else Color(0xFFCBD5E1)),
                    modifier = Modifier.clickable { selectedEventId = ev.id }
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(
                            text = if (language == CampLanguage.BANGLA) ev.titleBn else ev.titleEn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else LscSlateDark
                        )
                        Text(
                            text = "সময়: ${ev.timeLabel} • উপস্থিত: ${ev.presentCount}/${ev.targetCount}",
                            fontSize = 10.sp,
                            color = if (isSelected) Color.White.copy(alpha = 0.85f) else Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Event Metric Card + 1-Tap Subgroup Batch Roll-Call Launcher
        if (currentEvent != null) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == CampLanguage.BANGLA) currentEvent.titleBn else currentEvent.titleEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = LscSlateDark
                            )
                            Text(
                                text = "টার্গেট হেডকাউন্ট: ${currentEvent.targetCount} জন | হার: ${String.format("%.1f%%", ((currentEvent.presentCount + currentEvent.lateCount).toDouble() / currentEvent.targetCount.coerceAtLeast(1) * 100))}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { showExportAttendanceDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("export_attendance_csv_btn")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Export CSV", modifier = Modifier.size(15.dp), tint = LscEmeraldPrimary)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LscEmeraldPrimary)
                            }

                            Button(
                                onClick = onOpenScanner,
                                colors = ButtonDefaults.buttonColors(containerColor = LscGoldSecondary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("event_qr_scan_btn")
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == CampLanguage.BANGLA) "QR স্ক্যান" else "Scan", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 1-Tap Subgroup Batch Attendance Hero Button
                    Button(
                        onClick = { showBatchSubgroupModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("open_subgroup_batch_attendance_btn")
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "⚡ ১-ট্যাপ সাব-গ্রুপ ব্যাচ হাজিরা (৫ সেকেন্ডে পুরো সাব-গ্রুপ)"
                            else
                                "⚡ 1-Tap Subgroup Batch Roll-Call (5 Seconds)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status Breakdown Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusSummaryPill(
                            label = if (language == CampLanguage.BANGLA) "উপস্থিত" else "Present",
                            count = currentEvent.presentCount,
                            color = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )
                        StatusSummaryPill(
                            label = if (language == CampLanguage.BANGLA) "দেরিতে" else "Late",
                            count = currentEvent.lateCount,
                            color = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f)
                        )
                        StatusSummaryPill(
                            label = if (language == CampLanguage.BANGLA) "অনুপস্থিত" else "Absent",
                            count = currentEvent.absentCount,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Group Filters & Absent Only Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedGroupFilter == null,
                        onClick = {
                            selectedGroupFilter = null
                            selectedSubgroupFilter = null
                        },
                        label = { Text(if (language == CampLanguage.BANGLA) "সকল গ্রুপ" else "All Groups", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LscEmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                items(groups) { grp ->
                    FilterChip(
                        selected = selectedGroupFilter == grp.id,
                        onClick = {
                            selectedGroupFilter = if (selectedGroupFilter == grp.id) null else grp.id
                            selectedSubgroupFilter = null
                        },
                        label = { Text(if (language == CampLanguage.BANGLA) grp.nameBn else grp.nameEn, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(grp.colorHex),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            FilterChip(
                selected = showOnlyAbsent,
                onClick = { showOnlyAbsent = !showOnlyAbsent },
                label = { Text(if (language == CampLanguage.BANGLA) "অনুপস্থিত" else "Absent", fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFDC2626),
                    selectedLabelColor = Color.White
                )
            )
        }

        // Subgroup Quick Filter + Search Bar
        if (availableSubgroups.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(availableSubgroups) { sg ->
                    FilterChip(
                        selected = selectedSubgroupFilter == sg,
                        onClick = { selectedSubgroupFilter = if (selectedSubgroupFilter == sg) null else sg },
                        label = { Text(sg, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1D4ED8),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (language == CampLanguage.BANGLA) "নাম, আইডি (Bukhari-1-1) বা সাব-গ্রুপ খুঁজুন..." else "Search name or code...", fontSize = 11.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        // Participants Roster for Attendance Tap
        val filteredList = remember(participants, selectedGroupFilter, selectedSubgroupFilter, showOnlyAbsent, searchQuery) {
            val q = searchQuery.trim().lowercase()
            participants.filter { p ->
                val matchesGroup = selectedGroupFilter == null || p.groupId == selectedGroupFilter
                val matchesSubgroup = selectedSubgroupFilter == null || p.subgroup == selectedSubgroupFilter
                val matchesAbsent = !showOnlyAbsent || (p.attendancePercentage < 90f)
                val matchesSearch = q.isEmpty() ||
                    p.nameBn.lowercase().contains(q) ||
                    p.nameEn.lowercase().contains(q) ||
                    p.campCode.lowercase().contains(q) ||
                    p.fileNo.lowercase().contains(q)
                matchesGroup && matchesSubgroup && matchesAbsent && matchesSearch
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredList, key = { it.id }) { participant ->
                AttendanceParticipantRow(
                    participant = participant,
                    language = language,
                    onMark = { status ->
                        onMarkAttendance(selectedEventId, participant, status)
                    }
                )
            }
        }
    }

    if (showExportAttendanceDialog) {
        ExportAttendanceDialog(
            language = language,
            attendanceRecords = attendanceRecords,
            attendanceEvents = attendanceEvents,
            stations = stations,
            initialEventId = selectedEventId,
            onDismiss = { showExportAttendanceDialog = false }
        )
    }

    if (showBatchSubgroupModal && currentEvent != null) {
        SubgroupBatchAttendanceDialog(
            language = language,
            dialogTitleBn = "⚡ ১-ট্যাপ সাব-গ্রুপ ব্যাচ হাজিরা",
            dialogSubtitleBn = "${currentEvent.titleBn} (${currentEvent.timeLabel})",
            participants = participants,
            initialGroupId = selectedGroupFilter,
            stationSealCode = null,
            onDismiss = { showBatchSubgroupModal = false },
            onConfirmBatch = { subgroupKey, absentIds ->
                onBatchSubgroupAttendance(
                    currentEvent.id,
                    currentEvent.titleBn,
                    currentEvent.dayNo,
                    subgroupKey,
                    absentIds,
                    null
                )
            }
        )
    }
}

@Composable
fun StatusSummaryPill(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$count", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
            Text(text = label, fontSize = 10.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun AttendanceParticipantRow(
    participant: ParticipantEntity,
    language: CampLanguage,
    onMark: (status: String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth().testTag("attendance_row_${participant.campCode}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = participant.nameBn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = LscSlateDark
                )
                Text(
                    text = "${participant.campCode} • ${participant.groupNameBn} (${participant.subgroup})",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Gray
                )
            }

            // Quick Tap Buttons: Present (P), Late (L), Absent (A)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { onMark("PRESENT") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp).testTag("mark_present_${participant.campCode}")
                ) {
                    Text("উপস্থিত", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { onMark("LATE") },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("লেট", fontSize = 10.sp, color = Color(0xFFD97706))
                }

                OutlinedButton(
                    onClick = { onMark("ABSENT") },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("অনুপস্থিত", fontSize = 10.sp, color = Color(0xFFDC2626))
                }
            }
        }
    }
}
