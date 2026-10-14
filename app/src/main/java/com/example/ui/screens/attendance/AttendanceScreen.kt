package com.example.ui.screens.attendance

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun AttendanceScreen(
    language: CampLanguage,
    attendanceEvents: List<AttendanceEventEntity>,
    participants: List<ParticipantEntity>,
    groups: List<CampGroupEntity>,
    onMarkAttendance: (eventId: Long, participant: ParticipantEntity, status: String) -> Unit,
    onOpenScanner: () -> Unit
) {
    var selectedEventId by remember { mutableStateOf(attendanceEvents.firstOrNull()?.id ?: 2L) }
    var selectedGroupFilter by remember { mutableStateOf<String?>(null) }
    var showOnlyAbsent by remember { mutableStateOf(false) }

    val currentEvent = attendanceEvents.find { it.id == selectedEventId } ?: attendanceEvents.firstOrNull()

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
                            text = "সময়: ${ev.timeLabel} • উপস্থিত: ${ev.presentCount} জন",
                            fontSize = 10.sp,
                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Event Metric Card with 90% Target Badge
        if (currentEvent != null) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (language == CampLanguage.BANGLA) currentEvent.titleBn else currentEvent.titleEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = LscSlateDark
                            )
                             Text(
                                 text = "টার্গেট হেডকাউন্ট: ${currentEvent.targetCount} জন | বর্তমান হার: ${String.format("%.1f%%", ((currentEvent.presentCount + currentEvent.lateCount).toDouble() / currentEvent.targetCount * 100))}%",
                                 fontSize = 11.sp,
                                 color = Color.Gray
                             )
                        }

                        // Fast QR scan launcher for this event
                        Button(
                            onClick = onOpenScanner,
                            colors = ButtonDefaults.buttonColors(containerColor = LscGoldSecondary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("event_qr_scan_btn")
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (language == CampLanguage.BANGLA) "QR স্ক্যান" else "Scan", fontSize = 11.sp)
                        }
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

        Spacer(modifier = Modifier.height(10.dp))

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
                        onClick = { selectedGroupFilter = null },
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
                        onClick = { selectedGroupFilter = if (selectedGroupFilter == grp.id) null else grp.id },
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
                label = { Text(if (language == CampLanguage.BANGLA) "অনুপস্থিত তালিকা" else "Absent Only", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFDC2626),
                    selectedLabelColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Participants Roster for Attendance Tap
        val filteredList = participants.filter { p ->
            val matchesGroup = selectedGroupFilter == null || p.groupId == selectedGroupFilter
            val matchesAbsent = !showOnlyAbsent || (p.attendancePercentage < 90f)
            matchesGroup && matchesAbsent
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
