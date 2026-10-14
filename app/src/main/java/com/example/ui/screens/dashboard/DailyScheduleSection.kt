package com.example.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

/**
 * Resolves the assigned station for a participant during a given schedule event.
 * Uses rotationJson mapping (e.g. "bukhari:1,sumaiya:2,...") if present,
 * or falls back to event.stationId or station matching participant groupId.
 */
fun resolveParticipantAssignedStation(
    event: ScheduleEventEntity,
    participant: ParticipantEntity?,
    stations: List<StationEntity>
): StationEntity? {
    if (stations.isEmpty()) return null
    val targetGroupId = participant?.groupId?.trim()?.lowercase()

    if (!targetGroupId.isNullOrBlank() && event.rotationJson.isNotBlank()) {
        val pairs = event.rotationJson.split(",").map { it.trim().split(":") }
        for (p in pairs) {
            if (p.size == 2 && p[0].trim().lowercase() == targetGroupId) {
                val stnId = p[1].trim().toIntOrNull()
                if (stnId != null) {
                    val found = stations.find { it.id == stnId }
                    if (found != null) return found
                }
            }
        }
    }

    if (event.stationId != null) {
        val found = stations.find { it.id == event.stationId }
        if (found != null) return found
    }

    return null
}

/**
 * Resolves the assigned station for a specific group id during a given schedule event.
 */
fun resolveGroupAssignedStation(
    event: ScheduleEventEntity,
    groupId: String?,
    stations: List<StationEntity>
): StationEntity? {
    if (stations.isEmpty() || groupId.isNullOrBlank()) return null
    val targetGroupId = groupId.trim().lowercase()

    if (event.rotationJson.isNotBlank()) {
        val pairs = event.rotationJson.split(",").map { it.trim().split(":") }
        for (p in pairs) {
            if (p.size == 2 && p[0].trim().lowercase() == targetGroupId) {
                val stnId = p[1].trim().toIntOrNull()
                if (stnId != null) {
                    val found = stations.find { it.id == stnId }
                    if (found != null) return found
                }
            }
        }
    }

    if (event.stationId != null) {
        return stations.find { it.id == event.stationId }
    }

    return null
}

/**
 * Finds which group is assigned to a specific station for an event.
 */
fun findAssignedGroupForStation(
    event: ScheduleEventEntity,
    stationId: Int,
    groups: List<CampGroupEntity>
): CampGroupEntity? {
    if (event.rotationJson.isBlank()) return null
    val pairs = event.rotationJson.split(",").map { it.trim().split(":") }
    for (p in pairs) {
        if (p.size == 2 && p[1].trim().toIntOrNull() == stationId) {
            val grpId = p[0].trim().lowercase()
            return groups.find { it.id.trim().lowercase() == grpId }
        }
    }
    return null
}

@Composable
fun DailyScheduleSection(
    language: CampLanguage,
    scheduleEvents: List<ScheduleEventEntity>,
    stations: List<StationEntity>,
    groups: List<CampGroupEntity>,
    participants: List<ParticipantEntity>,
    selectedParticipant: ParticipantEntity?,
    onSelectParticipant: (ParticipantEntity?) -> Unit,
    onNavigateToTab: (Int) -> Unit,
    onOpenScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDay by remember { mutableIntStateOf(1) }
    var selectedStationFilter by remember { mutableStateOf<Int?>(null) } // null = All, 1..6 = Specific station
    var selectedCategoryFilter by remember { mutableStateOf("ALL") } // "ALL", "STATION", "GENERAL"
    var showParticipantPicker by remember { mutableStateOf(false) }
    var showCompletedSessions by remember { mutableStateOf(false) }

    // Active participant: either explicitly selected or first participant from roster
    val activeParticipant = remember(selectedParticipant, participants) {
        selectedParticipant ?: participants.firstOrNull()
    }

    // Filter events for selected day (default to Day 1)
    val dayEvents = remember(scheduleEvents, selectedDay) {
        val filtered = scheduleEvents.filter { it.dayNo == selectedDay }
        if (filtered.isNotEmpty()) filtered else scheduleEvents.filter { it.dayNo == 1 }
    }

    // Current live event
    val currentEvent = remember(dayEvents) {
        dayEvents.find { it.isCurrent } ?: dayEvents.firstOrNull { !it.isCompleted } ?: dayEvents.firstOrNull()
    }

    // Completed events for the day
    val completedEvents = remember(dayEvents, currentEvent) {
        dayEvents.filter { it.isCompleted && it.id != currentEvent?.id }
    }

    // Upcoming events for the day (after current session)
    val upcomingEvents = remember(dayEvents, currentEvent, selectedCategoryFilter, selectedStationFilter, activeParticipant) {
        val afterCurrent = if (currentEvent != null) {
            val currIdx = dayEvents.indexOf(currentEvent)
            if (currIdx >= 0) dayEvents.drop(currIdx + 1) else dayEvents.filter { !it.isCompleted && it.id != currentEvent.id }
        } else {
            dayEvents.filter { !it.isCompleted }
        }

        afterCurrent.filter { event ->
            val matchesCategory = when (selectedCategoryFilter) {
                "STATION" -> event.category == "Station" || event.stationId != null || event.rotationJson.isNotBlank()
                "GENERAL" -> event.category != "Station" && event.stationId == null && event.rotationJson.isBlank()
                else -> true
            }

            val matchesStation = if (selectedStationFilter != null) {
                val assigned = resolveParticipantAssignedStation(event, activeParticipant, stations)
                assigned?.id == selectedStationFilter || event.stationId == selectedStationFilter ||
                        (event.rotationJson.isNotBlank() && event.rotationJson.contains(":${selectedStationFilter}"))
            } else {
                true
            }

            matchesCategory && matchesStation
        }
    }

    // Resolve assigned station for current activity
    val currentAssignedStation = remember(currentEvent, activeParticipant, stations, selectedStationFilter) {
        if (selectedStationFilter != null) {
            stations.find { it.id == selectedStationFilter }
        } else if (currentEvent != null) {
            resolveParticipantAssignedStation(currentEvent, activeParticipant, stations)
        } else null
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_daily_schedule_section")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Title & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(LscEmeraldPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = LscEmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (language == CampLanguage.BANGLA) "দৈনিক সময়সূচি" else "Daily Schedule",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = LscSlateDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = LscEmeraldLight,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "দিন $selectedDay" else "Day $selectedDay",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LscEmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "অংশগ্রহণকারীর নির্ধারিত স্টেশন ও কার্যক্রম"
                            else
                                "Assigned station activities & session timings",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                TextButton(
                    onClick = { onNavigateToTab(3) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("view_full_schedule_matrix_btn")
                ) {
                    Text(
                        text = if (language == CampLanguage.BANGLA) "সম্পূর্ণ শিডিউল" else "Full Matrix",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LscEmeraldPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = LscEmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Day Selector Tabs (Day 1 to Day 5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (1..5).forEach { day ->
                    val isSelected = selectedDay == day
                    Surface(
                        color = if (isSelected) LscEmeraldPrimary else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (isSelected) LscEmeraldPrimary else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedDay = day }
                            .testTag("schedule_day_${day}_tab")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (language == CampLanguage.BANGLA) "দিন $day" else "Day $day",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else LscSlateDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Participant & Station Assignment Context Banner
            Surface(
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("participant_station_context_banner")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
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
                                    .background(LscEmeraldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (activeParticipant?.nameBn?.take(1) ?: "ক"),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = activeParticipant?.nameBn ?: "তাহমিদ জামান",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = LscSlateDark,
                                        modifier = Modifier.testTag("active_participant_name")
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(1.dp, Color(0xFF86EFAC))
                                    ) {
                                        Text(
                                            text = activeParticipant?.campCode ?: "LSC-2026-001",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${activeParticipant?.groupNameBn ?: "বুখারী"} • ${activeParticipant?.subgroup ?: "Bukhari-1"}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF166534),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Switch participant button
                        OutlinedButton(
                            onClick = { showParticipantPicker = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF15803D)),
                            border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("switch_participant_schedule_btn")
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA) "পরিবর্তন" else "Switch",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Assigned Station pill if available
                    if (currentAssignedStation != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                            modifier = Modifier.fillMaxWidth().testTag("assigned_station_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color(0xFF16A34A),
                                    shape = CircleShape,
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "S${currentAssignedStation.id}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (language == CampLanguage.BANGLA)
                                            "আপনার নির্ধারিত স্টেশন: ${currentAssignedStation.nameBn}"
                                        else
                                            "Assigned: ${currentAssignedStation.nameEn}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF14532D)
                                    )
                                    Text(
                                        text = "ভেন্যু: ${currentAssignedStation.location} • মডারেটর: ${currentAssignedStation.moderatorName}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF166534)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Station Filters (All, S1..S6)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = selectedStationFilter == null,
                    onClick = { selectedStationFilter = null },
                    label = { Text(if (language == CampLanguage.BANGLA) "সকল স্টেশন" else "All", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LscEmeraldPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_station_all")
                )

                stations.take(6).forEach { stn ->
                    val isStnSelected = selectedStationFilter == stn.id
                    FilterChip(
                        selected = isStnSelected,
                        onClick = {
                            selectedStationFilter = if (isStnSelected) null else stn.id
                        },
                        label = { Text("S${stn.id}", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1D4ED8),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_station_${stn.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Current Live Camp Activity Card (Highlighted)
            if (currentEvent != null) {
                CurrentCampActivityHighlightCard(
                    event = currentEvent,
                    language = language,
                    assignedStation = currentAssignedStation,
                    participant = activeParticipant,
                    groups = groups,
                    onOpenScanner = onOpenScanner,
                    onViewStationDetails = { onNavigateToTab(3) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2. Upcoming Session Timings Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Update,
                        contentDescription = null,
                        tint = Color(0xFF1D4ED8),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == CampLanguage.BANGLA) "আসন্ন সেশন ও সময়সূচি" else "Upcoming Session Timings",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = LscSlateDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(0xFFDBEAFE),
                        shape = CircleShape,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${upcomingEvents.size}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                        }
                    }
                }

                // Category filter chips (All vs Station Sessions)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(
                        selected = selectedCategoryFilter == "ALL",
                        onClick = { selectedCategoryFilter = "ALL" },
                        label = { Text(if (language == CampLanguage.BANGLA) "সব" else "All", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1D4ED8),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("category_filter_all")
                    )
                    FilterChip(
                        selected = selectedCategoryFilter == "STATION",
                        onClick = { selectedCategoryFilter = "STATION" },
                        label = { Text(if (language == CampLanguage.BANGLA) "স্টেশন" else "Station", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LscEmeraldPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("category_filter_station")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (upcomingEvents.isEmpty()) {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth().testTag("no_upcoming_sessions_banner")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "আজকের দিনের সকল সেশন সম্পন্ন হয়েছে!"
                            else
                                "All sessions for today are completed!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LscSlateDark
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("upcoming_sessions_list")
                ) {
                    upcomingEvents.forEachIndexed { index, event ->
                        val nextAssignedStation = resolveParticipantAssignedStation(event, activeParticipant, stations)
                        UpcomingSessionCard(
                            event = event,
                            language = language,
                            isNextImmediate = index == 0,
                            assignedStation = nextAssignedStation,
                            participant = activeParticipant,
                            onViewStation = { onNavigateToTab(3) }
                        )
                    }
                }
            }

            // Completed Sessions Collapsible Toggle
            if (completedEvents.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCompletedSessions = !showCompletedSessions }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "পূর্ববর্তী সম্পন্ন কার্যক্রম (${completedEvents.size} টি)"
                            else
                                "Completed Earlier Sessions (${completedEvents.size})",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = if (showCompletedSessions) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                AnimatedVisibility(visible = showCompletedSessions) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        completedEvents.forEach { compEvent ->
                            CompletedSessionRow(
                                event = compEvent,
                                language = language
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Participant Picker Dialog
    if (showParticipantPicker) {
        ParticipantPickerDialog(
            language = language,
            participants = participants,
            currentSelected = activeParticipant,
            onDismiss = { showParticipantPicker = false },
            onSelect = { participant ->
                onSelectParticipant(participant)
                showParticipantPicker = false
            }
        )
    }
}

@Composable
fun CurrentCampActivityHighlightCard(
    event: ScheduleEventEntity,
    language: CampLanguage,
    assignedStation: StationEntity?,
    participant: ParticipantEntity?,
    groups: List<CampGroupEntity>,
    onOpenScanner: () -> Unit,
    onViewStationDetails: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
        border = BorderStroke(1.5.dp, LscGoldSecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("current_camp_activity_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Live Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFDC2626),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "চলমান কার্যক্রম" else "CURRENT ACTIVITY",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = LscGoldDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.timeSlot,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LscGoldDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Activity Title
            Text(
                text = if (language == CampLanguage.BANGLA) event.titleBn else event.titleEn,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = LscSlateDark,
                modifier = Modifier.testTag("current_activity_title")
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Venue & Category Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (event.category) {
                        "Station" -> LscEmeraldPrimary
                        "Meal" -> Color(0xFFD97706)
                        "Competition" -> Color(0xFF7C3AED)
                        "Physical" -> Color(0xFF0284C7)
                        else -> Color(0xFF475569)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = event.category,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = event.location,
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }

            // Participant's Assigned Station Highlight Box
            if (assignedStation != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth().testTag("current_assigned_station_box")
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(LscEmeraldPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "S${assignedStation.id}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (language == CampLanguage.BANGLA)
                                            assignedStation.nameBn
                                        else
                                            assignedStation.nameEn,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LscEmeraldPrimary
                                    )
                                    Text(
                                        text = "মডারেটর: ${assignedStation.moderatorName}",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "নির্ধারিত স্টেশন" else "Assigned Station",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D4ED8),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "ভেন্যু: ${assignedStation.location}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LscGoldDark
                        )
                        Text(
                            text = "উদ্দেশ্য: ${assignedStation.objectiveBn}",
                            fontSize = 10.sp,
                            color = Color(0xFF334155),
                            lineHeight = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Fast Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onOpenScanner,
                                colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("log_station_attendance_btn")
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "স্টেশন হাজিরা দিন" else "Log Attendance",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = onViewStationDetails,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("view_station_details_btn")
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "স্টেশন বিবরণ" else "Station Details",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            } else if (event.rotationJson.isNotBlank()) {
                // If no specific participant selected, show rotation matrix snippet
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "সকল গ্রুপের বর্তমান স্টেশন বণ্টন:" else "Group Station Assignments:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LscEmeraldPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val pairs = event.rotationJson.split(",").map { it.split(":") }
                        pairs.chunked(3).forEach { rowPairs ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowPairs.forEach { p ->
                                    if (p.size == 2) {
                                        Text(
                                            text = "${p[0].replaceFirstChar { it.uppercase() }} ➔ Stn ${p[1]}",
                                            fontSize = 10.sp,
                                            fontWeight = if (p[0] == participant?.groupId) FontWeight.Bold else FontWeight.Normal,
                                            color = if (p[0] == participant?.groupId) LscGoldDark else Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpcomingSessionCard(
    event: ScheduleEventEntity,
    language: CampLanguage,
    isNextImmediate: Boolean,
    assignedStation: StationEntity?,
    participant: ParticipantEntity?,
    onViewStation: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isNextImmediate) Color(0xFFF0FDF4) else Color.White
        ),
        border = BorderStroke(
            1.dp,
            if (isNextImmediate) Color(0xFF86EFAC) else Color(0xFFE2E8F0)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("upcoming_session_${event.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isNextImmediate) {
                        Surface(
                            color = LscEmeraldPrimary,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (language == CampLanguage.BANGLA) "পরবর্তী সেশন" else "NEXT UP",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Surface(
                        color = when (event.category) {
                            "Station" -> Color(0xFFEFF6FF)
                            "Meal" -> Color(0xFFFFFBEB)
                            "Competition" -> Color(0xFFF5F3FF)
                            "Physical" -> Color(0xFFF0F9FF)
                            else -> Color(0xFFF8FAFC)
                        },
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Text(
                            text = event.category,
                            color = when (event.category) {
                                "Station" -> Color(0xFF1D4ED8)
                                "Meal" -> Color(0xFFB45309)
                                "Competition" -> Color(0xFF6D28D9)
                                "Physical" -> Color(0xFF0369A1)
                                else -> Color(0xFF475569)
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = event.timeSlot,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LscSlateDark
                        )
                    }
                }

                if (assignedStation != null) {
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Text(
                            text = "➔ Station ${assignedStation.id}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (language == CampLanguage.BANGLA) event.titleBn else event.titleEn,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = LscSlateDark
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = event.location,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            // If this is a station session with assigned station for participant
            if (assignedStation != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "নির্ধারিত: ${assignedStation.nameBn}"
                                else
                                    "Assigned: ${assignedStation.nameEn}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F766E)
                            )
                            Text(
                                text = "ভেন্যু: ${assignedStation.location} • মডারেটর: ${assignedStation.moderatorName}",
                                fontSize = 9.sp,
                                color = Color.Gray
                            )
                        }

                        IconButton(
                            onClick = onViewStation,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View Station",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompletedSessionRow(
    event: ScheduleEventEntity,
    language: CampLanguage
) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (language == CampLanguage.BANGLA) event.titleBn else event.titleEn,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Text(
                        text = "${event.timeSlot} • ${event.location}",
                        fontSize = 9.sp,
                        color = Color.LightGray
                    )
                }
            }

            Surface(
                color = Color(0xFFE2E8F0),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = if (language == CampLanguage.BANGLA) "সম্পন্ন" else "Done",
                    fontSize = 9.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ParticipantPickerDialog(
    language: CampLanguage,
    participants: List<ParticipantEntity>,
    currentSelected: ParticipantEntity?,
    onDismiss: () -> Unit,
    onSelect: (ParticipantEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredParticipants = remember(participants, searchQuery) {
        val clean = searchQuery.trim()
        if (clean.isBlank()) {
            participants.take(30)
        } else {
            participants.filter {
                it.nameBn.contains(clean, ignoreCase = true) ||
                        it.nameEn.contains(clean, ignoreCase = true) ||
                        it.campCode.contains(clean, ignoreCase = true) ||
                        it.groupNameBn.contains(clean, ignoreCase = true) ||
                        it.groupNameEn.contains(clean, ignoreCase = true) ||
                        it.subgroup.contains(clean, ignoreCase = true)
            }.take(30)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = if (language == CampLanguage.BANGLA) "অংশগ্রহণকারী নির্বাচন" else "Select Participant",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = if (language == CampLanguage.BANGLA)
                        "স্টেশনভিত্তিক শিডিউল দেখার জন্য অংশগ্রহণকারী বাছুন"
                    else
                        "Pick a participant to personalize their station schedule",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(if (language == CampLanguage.BANGLA) "নাম বা ক্যাম্প কোড খুঁজুন..." else "Search name or camp code...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("participant_picker_search"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(modifier = Modifier.heightIn(max = 280.dp)) {
                    androidx.compose.foundation.lazy.LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredParticipants.size) { idx ->
                            val p = filteredParticipants[idx]
                            val isChosen = p.id == currentSelected?.id
                            Surface(
                                color = if (isChosen) LscEmeraldLight else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isChosen) LscEmeraldPrimary else Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(p) }
                                    .testTag("picker_participant_item_${p.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = p.nameBn,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = LscSlateDark
                                        )
                                        Text(
                                            text = "${p.campCode} • ${p.groupNameBn} (${p.subgroup})",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    if (isChosen) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = LscEmeraldPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == CampLanguage.BANGLA) "বাতিল" else "Cancel")
            }
        }
    )
}
