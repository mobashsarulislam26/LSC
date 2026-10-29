package com.example.ui.screens.stations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.EditGroupDialog
import com.example.ui.components.EditScheduleDialog
import com.example.ui.theme.*

@Composable
fun StationRotationScreen(
    language: CampLanguage,
    stations: List<StationEntity>,
    scheduleEvents: List<ScheduleEventEntity>,
    groups: List<CampGroupEntity>,
    selectedDay: Int,
    isAdminUnlocked: Boolean = false,
    onRequestAdminUnlock: () -> Unit = {},
    onDaySelect: (Int) -> Unit,
    onUpdateScheduleEvent: (ScheduleEventEntity) -> Unit = {},
    onUpdateGroup: (CampGroupEntity) -> Unit = {},
    onOpenScanner: () -> Unit = {},
    currentRole: UserRole = UserRole.CAMP_CHIEF,
    participants: List<ParticipantEntity> = emptyList(),
    onOpenScannerForStation: (Int) -> Unit = { onOpenScanner() },
    onBatchSubgroupAttendance: (
        eventId: Long,
        eventTitleBn: String,
        dayNo: Int,
        subgroupKey: String,
        absentIds: Set<Long>,
        stationSealCode: String?
    ) -> Unit = { _, _, _, _, _, _ -> }
) {
    val defaultMode = if (currentRole == UserRole.STATION_MANAGER || currentRole == UserRole.STATION_MODERATOR) {
        "CONSOLE"
    } else {
        "TIMELINE"
    }
    var selectedGroupFilter by remember { mutableStateOf<String?>(null) }
    var selectedViewMode by remember(currentRole) { mutableStateOf(defaultMode) }
    var eventToEdit by remember { mutableStateOf<ScheduleEventEntity?>(null) }
    var groupToEdit by remember { mutableStateOf<CampGroupEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Station Leader QR Attendance Scanner Banner
        Surface(
            color = Color(0xFFEFF6FF),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDBEAFE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color(0xFF1D4ED8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "স্টেশন র‍্যাপিড হাজিরা স্ক্যানার ও ★ সিল" else "Station Rapid Attendance & ★ Seal",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "নন-স্টপ ক্যামেরা কিউআর গান অথবা ১-ট্যাপ সাব-গ্রুপ হাজিরা নিন"
                            else
                                "Scan QR badges continuously or log 1-tap subgroup batch roll-call",
                            fontSize = 10.sp,
                            color = Color(0xFF3B82F6)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onOpenScanner,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("open_station_camera_scanner_btn")
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (language == CampLanguage.BANGLA) "হাজিরা স্ক্যান" else "Scan QR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Day Selector Tabs (Day 1 to Day 5: 22–26 October 2026)
        val dayDatesBn = mapOf(1 to "২২ অক্টো", 2 to "২৩ অক্টো", 3 to "২৪ অক্টো", 4 to "২৫ অক্টো", 5 to "২৬ অক্টো")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            (1..5).forEach { day ->
                val isSelected = selectedDay == day
                Surface(
                    color = if (isSelected) LscEmeraldPrimary else Color.White,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) LscEmeraldPrimary else Color(0xFFCBD5E1)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onDaySelect(day) }
                        .testTag("day_${day}_selector")
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "দিন $day" else "Day $day",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else LscSlateDark
                        )
                        Text(
                            text = dayDatesBn[day] ?: "",
                            fontSize = 9.sp,
                            color = if (isSelected) Color.White.copy(alpha = 0.85f) else Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // View Mode Switcher (Live Console | Schedule | 6 Stations | Leaders)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            FilterChip(
                selected = selectedViewMode == "CONSOLE",
                onClick = { selectedViewMode = "CONSOLE" },
                label = { Text(if (language == CampLanguage.BANGLA) "⚡ কনসোল" else "⚡ Console", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF0F172A),
                    selectedLabelColor = LscGoldLight
                ),
                modifier = Modifier.weight(1.05f).testTag("station_mode_console_chip")
            )
            FilterChip(
                selected = selectedViewMode == "TIMELINE",
                onClick = { selectedViewMode = "TIMELINE" },
                label = { Text(if (language == CampLanguage.BANGLA) "শিডিউল" else "Schedule", fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LscEmeraldPrimary,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(0.95f)
            )
            FilterChip(
                selected = selectedViewMode == "STATIONS",
                onClick = { selectedViewMode = "STATIONS" },
                label = { Text(if (language == CampLanguage.BANGLA) "৬ স্টেশন" else "Stations", fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LscEmeraldPrimary,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(0.95f)
            )
            FilterChip(
                selected = selectedViewMode == "GROUPS",
                onClick = { selectedViewMode = "GROUPS" },
                label = { Text(if (language == CampLanguage.BANGLA) "লিডার ও ইনচার্জ" else "Leaders", fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1D4ED8),
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(1.2f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedViewMode) {
            "CONSOLE" -> {
                StationManagerConsoleScreen(
                    language = language,
                    stations = stations,
                    groups = groups,
                    participants = participants,
                    scheduleEvents = scheduleEvents,
                    selectedDay = selectedDay,
                    onDaySelect = onDaySelect,
                    onOpenScannerForStation = onOpenScannerForStation,
                    onBatchSubgroupAttendance = onBatchSubgroupAttendance
                )
            }

            "TIMELINE" -> {
                val daySchedule = scheduleEvents.filter { it.dayNo == selectedDay }.ifEmpty {
                    scheduleEvents.filter { it.dayNo == 1 }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(daySchedule, key = { it.id }) { event ->
                        ScheduleItemCard(
                            event = event,
                            language = language,
                            highlightGroup = selectedGroupFilter,
                            isAdminUnlocked = isAdminUnlocked,
                            onEditClick = {
                                if (isAdminUnlocked) {
                                    eventToEdit = event
                                } else {
                                    onRequestAdminUnlock()
                                }
                            }
                        )
                    }
                }
            }

            "STATIONS" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(stations, key = { it.id }) { station ->
                        StationDetailCard(
                            station = station,
                            language = language
                        )
                    }
                }
            }

            "GROUPS" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "৬টি মূল গ্রুপের লিডার ও ২৪টি সাব-গ্রুপ ইনচার্জদের তালিকা (ডানপাশের এডিট বোতামে আপডেটযোগ্য)।",
                                    fontSize = 11.sp,
                                    color = Color(0xFF1E3A8A),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    items(groups, key = { it.id }) { grp ->
                        GroupLeaderInfoCard(
                            group = grp,
                            language = language,
                            isAdminUnlocked = isAdminUnlocked,
                            onEditClick = {
                                if (isAdminUnlocked) {
                                    groupToEdit = grp
                                } else {
                                    onRequestAdminUnlock()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal Edit Schedule Dialog
    eventToEdit?.let { targetEvent ->
        EditScheduleDialog(
            event = targetEvent,
            language = language,
            onDismiss = { eventToEdit = null },
            onSave = { updated ->
                onUpdateScheduleEvent(updated)
                eventToEdit = null
            }
        )
    }

    // Modal Edit Group Dialog
    groupToEdit?.let { targetGroup ->
        EditGroupDialog(
            group = targetGroup,
            language = language,
            onDismiss = { groupToEdit = null },
            onSave = { updated ->
                onUpdateGroup(updated)
                groupToEdit = null
            }
        )
    }
}

private fun shortGroupLabelBn(groupId: String): String = when (groupId.trim().lowercase()) {
    "bukhari" -> "বুখারী"
    "shafeyi" -> "শাফে’য়ী"
    "salahuddin" -> "আয়ুবী"
    "sumaiya" -> "সুমাইয়া"
    "umm_ayman" -> "উম্মে আইমান"
    "umm_saad" -> "উম্মে সা’দ"
    else -> groupId.replaceFirstChar { it.uppercase() }
}

@Composable
fun ScheduleItemCard(
    event: ScheduleEventEntity,
    language: CampLanguage,
    highlightGroup: String?,
    isAdminUnlocked: Boolean,
    onEditClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (event.isCurrent) Color(0xFFFEF3C7) else Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (event.isCurrent) LscGoldSecondary else Color(0xFFE2E8F0)
        ),
        modifier = Modifier.fillMaxWidth().testTag("schedule_event_${event.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = when (event.category) {
                            "Station" -> LscEmeraldPrimary
                            "Meal" -> Color(0xFFD97706)
                            "Competition" -> Color(0xFF7C3AED)
                            "Physical" -> Color(0xFF0284C7)
                            else -> Color(0xFF475569)
                        },
                        shape = RoundedCornerShape(6.dp)
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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = event.timeSlot, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LscSlateDark)
                    }
                }

                // Edit Button
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(30.dp).testTag("edit_event_${event.id}")
                ) {
                    Icon(
                        imageVector = if (isAdminUnlocked) Icons.Default.Edit else Icons.Default.Lock,
                        contentDescription = "Edit Schedule Session",
                        tint = if (isAdminUnlocked) LscEmeraldPrimary else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (language == CampLanguage.BANGLA) event.titleBn else event.titleEn,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = LscSlateDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = event.location, fontSize = 11.sp, color = Color.Gray)
            }

            // If rotation json is present, render Station rotation matrix!
            if (event.rotationJson.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "গ্রুপ স্টেশন বণ্টন ম্যাট্রিক্স (৬টি স্টেশন রোটেশন):" else "Assigned Rotation Matrix:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LscEmeraldPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        val pairs = event.rotationJson.split(",").map { it.split(":") }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            pairs.take(3).forEach { p ->
                                if (p.size == 2) {
                                    val label = if (language == CampLanguage.BANGLA) shortGroupLabelBn(p[0]) else p[0].replaceFirstChar { it.uppercase() }
                                    Text(
                                        text = "$label ➔ S${p[1].trim()}",
                                        fontSize = 10.sp,
                                        fontWeight = if (highlightGroup != null && p[0].contains(highlightGroup)) FontWeight.Bold else FontWeight.Medium,
                                        color = if (highlightGroup != null && p[0].contains(highlightGroup)) LscGoldDark else Color.DarkGray
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            pairs.drop(3).forEach { p ->
                                if (p.size == 2) {
                                    val label = if (language == CampLanguage.BANGLA) shortGroupLabelBn(p[0]) else p[0].replaceFirstChar { it.uppercase() }
                                    Text(
                                        text = "$label ➔ S${p[1].trim()}",
                                        fontSize = 10.sp,
                                        fontWeight = if (highlightGroup != null && p[0].contains(highlightGroup)) FontWeight.Bold else FontWeight.Medium,
                                        color = if (highlightGroup != null && p[0].contains(highlightGroup)) LscGoldDark else Color.DarkGray
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

@Composable
fun GroupLeaderInfoCard(
    group: CampGroupEntity,
    language: CampLanguage,
    isAdminUnlocked: Boolean,
    onEditClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth().testTag("group_leader_card_${group.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(group.colorHex))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${group.nameBn} (${group.memberCount} জন)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = LscSlateDark
                    )
                }

                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(32.dp).testTag("edit_group_btn_${group.id}")
                ) {
                    Icon(
                        imageVector = if (isAdminUnlocked) Icons.Default.Edit else Icons.Default.Lock,
                        contentDescription = "Edit Group Mentors",
                        tint = if (isAdminUnlocked) Color(0xFF1D4ED8) else Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Leader details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "গ্রুপ লিডার:", fontSize = 10.sp, color = Color.Gray)
                    Text(text = group.leaderName.ifBlank { "নির্ধারিত নয়" }, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LscSlateDark)
                    if (group.leaderPhone.isNotBlank()) {
                        Text(text = "মোবাইল: ${group.leaderPhone}", fontSize = 11.sp, color = Color(0xFF0F766E))
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "প্রধান মেন্টর:", fontSize = 10.sp, color = Color.Gray)
                    Text(text = group.mentorName.ifBlank { "সমন্বয়ক টিম" }, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LscSlateDark)
                    if (group.mentorPhone.isNotBlank()) {
                        Text(text = group.mentorPhone, fontSize = 11.sp, color = Color(0xFF0F766E))
                    }
                }
            }

            if (group.subgroupMentors.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "৪টি সাব-গ্রুপ ইনচার্জ:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = group.subgroupMentors, fontSize = 11.sp, color = Color(0xFF334155))
                    }
                }
            }
        }
    }
}

@Composable
fun StationDetailCard(
    station: StationEntity,
    language: CampLanguage
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(LscEmeraldLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "S${station.id}",
                        fontWeight = FontWeight.Bold,
                        color = LscEmeraldPrimary,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (language == CampLanguage.BANGLA) station.nameBn else station.nameEn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = LscSlateDark
                    )
                    Text(
                        text = "মডারেটর: ${station.moderatorName}",
                        fontSize = 11.sp,
                        color = LscEmeraldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (station.subModerators.isNotBlank()) {
                        Text(
                            text = "সহযোগী ও সহ-মডারেটর: ${station.subModerators}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📍 ভেন্যু: ${station.location}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = LscGoldDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "লক্ষ্য ও উদ্দেশ্য: ${station.objectiveBn}",
                fontSize = 11.sp,
                color = Color.DarkGray,
                lineHeight = 16.sp
            )

            // 3-Day Official Curriculum Breakdown (Days 2, 4, 5)
            if (station.day2TopicBn.isNotBlank() || station.day4TopicBn.isNotBlank() || station.day5TopicBn.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "📚 ৩ দিনের পাঠ্যক্রম ও প্রশিক্ষক সূচি:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                        if (station.day2TopicBn.isNotBlank()) {
                            Text(text = "• ২য় দিন (২৩ অক্টো): ${station.day2TopicBn}", fontSize = 10.sp, color = LscSlateDark)
                        }
                        if (station.day4TopicBn.isNotBlank()) {
                            Text(text = "• ৪র্থ দিন (২৫ অক্টো): ${station.day4TopicBn}", fontSize = 10.sp, color = LscSlateDark)
                        }
                        if (station.day5TopicBn.isNotBlank()) {
                            Text(text = "• ৫ম দিন (২৬ অক্টো): ${station.day5TopicBn}", fontSize = 10.sp, color = LscSlateDark)
                        }
                    }
                }
            }
        }
    }
}
