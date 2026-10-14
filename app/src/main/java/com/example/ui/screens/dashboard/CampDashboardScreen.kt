package com.example.ui.screens.dashboard

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.CampLogoImage
import com.example.ui.components.DashboardAlertHub
import com.example.ui.theme.*

@Composable
fun CampDashboardScreen(
    language: CampLanguage,
    currentRole: UserRole,
    groups: List<CampGroupEntity>,
    stations: List<StationEntity>,
    scheduleEvents: List<ScheduleEventEntity>,
    participantCount: Int,
    attendanceEvents: List<AttendanceEventEntity>,
    bulletins: List<BulletinEntity>,
    incidents: List<IncidentEntity>,
    onNavigateToTab: (Int) -> Unit,
    onOpenScanner: () -> Unit,
    onOpenCharts: () -> Unit = { onNavigateToTab(8) },
    onOpenBackupDatabase: () -> Unit = {},
    participants: List<ParticipantEntity> = emptyList(),
    selectedParticipant: ParticipantEntity? = null,
    onSelectParticipant: ((ParticipantEntity?) -> Unit)? = null,
    activeAlerts: List<IncidentEntity> = emptyList(),
    pendingHealthAlerts: List<HealthRecordEntity> = emptyList(),
    onOpenSendAlertDialog: () -> Unit = {},
    onAcknowledgeAlert: (Long) -> Unit = {},
    onResolveAlert: (Long, String) -> Unit = { _, _ -> },
    onAttendHealthAlert: (Long) -> Unit = {},
    onResolveHealthAlert: (Long, String) -> Unit = { _, _ -> },
    competitionScores: List<CompetitionScoreEntity> = emptyList(),
    attendanceRecords: List<AttendanceRecordEntity> = emptyList(),
    mealDistributions: List<MealDistributionEntity> = emptyList(),
    rooms: List<RoomEntity> = emptyList()
) {
    val currentSession = scheduleEvents.find { it.isCurrent } ?: scheduleEvents.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome & Role Context Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LscEmeraldPrimary),
                modifier = Modifier.fillMaxWidth().testTag("dashboard_banner")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CampLogoImage(
                        size = 54.dp,
                        elevation = 4.dp,
                        modifier = Modifier.testTag("dashboard_camp_logo")
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "স্বাগতম, ${currentRole.titleBn}" else "Welcome, ${currentRole.titleEn}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "আজকের কার্যক্রম: দিন ১ | ৪-৫ দিনব্যাপী আবাসিক ক্যাম্প সুন্দর ও সুশৃঙ্খলভাবে পরিচালিত হচ্ছে।"
                            else
                                "Day 1 Active | 4-Day residential camp operations running smoothly with 603+ participants.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                    IconButton(
                        onClick = onOpenScanner,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(LscGoldSecondary)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", tint = Color.White)
                    }
                }
            }
        }

        // Counselor Quick Alert Hub & High-Priority Health Alerts
        item {
            DashboardAlertHub(
                language = language,
                activeAlerts = activeAlerts,
                pendingHealthAlerts = pendingHealthAlerts,
                onOpenSendAlertDialog = onOpenSendAlertDialog,
                onAcknowledgeAlert = onAcknowledgeAlert,
                onResolveAlert = onResolveAlert,
                onAttendHealthAlert = onAttendHealthAlert,
                onResolveHealthAlert = onResolveHealthAlert
            )
        }

        // Executive Operational KPIs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    title = if (language == CampLanguage.BANGLA) "মোট অংশগ্রহণকারী" else "Total Registered",
                    value = "$participantCount",
                    sub = if (language == CampLanguage.BANGLA) "৬টি গ্রুপে বিভক্ত" else "6 Active Groups",
                    icon = Icons.Default.Groups,
                    color = Color(0xFF1D4ED8),
                    modifier = Modifier.weight(1f).clickable { onNavigateToTab(1) }
                )
                KpiCard(
                    title = if (language == CampLanguage.BANGLA) "আজকের উপস্থিতি" else "Present Rate",
                    value = "৯৮.৭%",
                    sub = if (language == CampLanguage.BANGLA) "লক্ষ্যমাত্রা ≥৯০%" else "Target ≥90%",
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f).clickable { onNavigateToTab(2) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    title = if (language == CampLanguage.BANGLA) "স্টেশন রোটেশন" else "Active Station",
                    value = "সেশন ১",
                    sub = if (language == CampLanguage.BANGLA) "৬টি স্টেশনে চলমান" else "6 Stations Busy",
                    icon = Icons.Default.SyncAlt,
                    color = LscGoldSecondary,
                    modifier = Modifier.weight(1f).clickable { onNavigateToTab(3) }
                )
                KpiCard(
                    title = if (language == CampLanguage.BANGLA) "পুরস্কার ইনভেন্টরি" else "Awards Stock",
                    value = "৭৯৯",
                    sub = if (language == CampLanguage.BANGLA) "২৮ ধরনের আইটেম" else "28 Item Masters",
                    icon = Icons.Default.EmojiEvents,
                    color = Color(0xFF9333EA),
                    modifier = Modifier.weight(1f).clickable { onNavigateToTab(6) }
                )
            }
        }

        // Station Check-In Summary View (pulling live check-in counts from Room database)
        item {
            StationCheckInSummaryCard(
                stations = stations,
                attendanceRecords = attendanceRecords,
                scheduleEvents = scheduleEvents,
                language = language,
                onOpenScanner = onOpenScanner,
                onNavigateToStations = { onNavigateToTab(3) }
            )
        }

        // Smart Meal & Dining Live Headcount & Wastage Prep Counter
        item {
            LiveMealHeadcountCard(
                participants = participants,
                mealDistributions = mealDistributions,
                language = language,
                onOpenScanner = onOpenScanner
            )
        }

        // Recharts Visual Analytics & Trends Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenCharts() }
                    .testTag("dashboard_recharts_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF3B82F6).copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Analytics,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (language == CampLanguage.BANGLA)
                                            "রিচার্টস ভিজ্যুয়াল ড্যাশবোর্ড"
                                        else
                                            "Recharts Visual Analytics",
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
                                            text = "Recharts",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2563EB),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (language == CampLanguage.BANGLA)
                                        "হাজিরা প্রবণতা বার চার্ট ও স্বাস্থ্য বণ্টন পাই চার্ট"
                                    else
                                        "Attendance bar trends & health distribution pie charts",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "Open",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick visual highlights preview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.BarChart, contentDescription = null, tint = LscEmeraldPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "৯৮.৭%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LscEmeraldPrimary)
                            }
                            Text(text = if (language == CampLanguage.BANGLA) "হাজিরা ট্রেন্ড" else "Attendance Rate", fontSize = 10.sp, color = Color.Gray)
                        }

                        Box(modifier = Modifier.height(24.dp).width(1.dp).background(Color(0xFFE2E8F0)))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PieChart, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "৮১.৯%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF3B82F6))
                            }
                            Text(text = if (language == CampLanguage.BANGLA) "সুস্থ (Normal)" else "Normal Health", fontSize = 10.sp, color = Color.Gray)
                        }

                        Box(modifier = Modifier.height(24.dp).width(1.dp).background(Color(0xFFE2E8F0)))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "৯.১%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFF59E0B))
                            }
                            Text(text = if (language == CampLanguage.BANGLA) "বিশ্রাম/কেয়ার" else "Rest/Care", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { onOpenCharts() },
                        modifier = Modifier.fillMaxWidth().testTag("open_recharts_dashboard_btn"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Insights, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "পূর্ণ রিচার্টস ড্যাশবোর্ড দেখুন" else "Explore Full Recharts Dashboard",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Database Backup & Manual Recovery Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenBackupDatabase() }
                    .testTag("dashboard_backup_db_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F766E).copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Storage,
                                contentDescription = null,
                                tint = Color(0xFF0F766E),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA)
                                        "রুম ডাটাবেজ ব্যাকআপ"
                                    else
                                        "Backup Room Database",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = LscSlateDark
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFFCCFBF1),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "SQLite",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F766E),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "ম্যানুয়াল রিকভারির জন্য ডিভাইসের ডকুমেন্ট স্টোরেজে কপি সেভ করুন"
                                else
                                    "Save copy to device document storage for recovery",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = { onOpenBackupDatabase() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFF0FDFA),
                            contentColor = Color(0xFF0F766E)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("dashboard_backup_db_btn")
                    ) {
                        Icon(Icons.Default.SaveAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "ব্যাকআপ" else "Backup",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Daily Schedule Section (Highlights current camp activities & upcoming session timings based on participant's assigned station)
        item {
            DailyScheduleSection(
                language = language,
                scheduleEvents = scheduleEvents,
                stations = stations,
                groups = groups,
                participants = participants,
                selectedParticipant = selectedParticipant,
                onSelectParticipant = { onSelectParticipant?.invoke(it) },
                onNavigateToTab = onNavigateToTab,
                onOpenScanner = onOpenScanner
            )
        }

        // Recharts Real-Time Team Competition Leaderboard
        item {
            RechartsTeamLeaderboardCard(
                groups = groups,
                competitionScores = competitionScores,
                stations = stations,
                language = language,
                onNavigateToCompetitions = { onNavigateToTab(4) }
            )
        }

        // Digital Room & Bed Occupancy Matrix (Rangamati Resort Layout)
        item {
            VisualRoomOccupancyMatrixCard(
                rooms = rooms,
                participants = participants,
                language = language,
                onNavigateToRooms = { onNavigateToTab(4) }
            )
        }

        // Group Points Leaderboard Snapshot
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Leaderboard, contentDescription = null, tint = LscGoldSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA) "গ্রুপ স্কোর ও পয়েন্ট টেবিল" else "Group Leaderboard Points",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        TextButton(onClick = { onNavigateToTab(4) }) {
                            Text(if (language == CampLanguage.BANGLA) "পূর্ণ তালিকা" else "Full List", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val sortedGroups = remember(groups) { groups.sortedByDescending { it.totalPoints } }
                    sortedGroups.take(3).forEachIndexed { idx, grp ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = if (idx == 0) LscGoldLight else Color(0xFFF1F5F9),
                                    shape = CircleShape,
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${idx + 1}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (idx == 0) LscGoldDark else Color.DarkGray
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${grp.nameBn} (${grp.nameEn})",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${grp.memberCount} সদস্য • লিডার: ${grp.leaderName.take(20)}...",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Text(
                                text = "${grp.totalPoints} pts",
                                fontWeight = FontWeight.Bold,
                                color = LscEmeraldPrimary,
                                fontSize = 14.sp
                            )
                        }
                        if (idx < 2) HorizontalDivider(color = Color(0xFFF1F5F9))
                    }
                }
            }
        }

        // Station Competition Milestones Progress Tracker Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9D5FF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToTab(4) }
                    .testTag("dashboard_milestones_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF7C3AED).copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MilitaryTech,
                                    contentDescription = null,
                                    tint = Color(0xFF7C3AED),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (language == CampLanguage.BANGLA)
                                            "স্টেশন প্রতিযোগিতা মাইলস্টোন"
                                        else
                                            "Station Competition Milestones",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = LscSlateDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFFF3E8FF),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "৬টি স্টেশন",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF7C3AED),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (language == CampLanguage.BANGLA)
                                        "বিভিন্ন স্টেশনে অর্জিত চ্যালেঞ্জ ও স্কোরের অগ্রগতি"
                                    else
                                        "Track milestones completed at assigned stations",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "সামগ্রিক অগ্রগতি: ৪/৬ সম্পন্ন" else "Overall Progress: 4/6 Completed",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6B21A8)
                        )
                        Text(
                            text = "৬৭%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7C3AED)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { 4f / 6f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF7C3AED),
                        trackColor = Color(0xFFE9D5FF)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 6 Station Milestone Badges Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(
                            Triple("S1", "হাদিস", true),
                            Triple("S2", "লাইফ স্কিল", true),
                            Triple("S3", "কুইজ", false),
                            Triple("S4", "সালাত", true),
                            Triple("S5", "ক্যালিগ্রাফি", false),
                            Triple("S6", "ফিটনেস", true)
                        ).forEach { (stn, title, isDone) ->
                            Surface(
                                color = if (isDone) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isDone) Color(0xFF86EFAC) else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = stn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (isDone) Color(0xFF15803D) else Color(0xFF64748B)
                                    )
                                    Text(
                                        text = if (isDone) "✓" else "⏳",
                                        fontSize = 9.sp,
                                        color = if (isDone) Color(0xFF15803D) else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { onNavigateToTab(4) },
                        modifier = Modifier.fillMaxWidth().testTag("open_milestones_tracker_btn"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF7C3AED)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD6FE))
                    ) {
                        Icon(Icons.Default.MilitaryTech, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "মাইলস্টোন ট্র্যাকার খুলুন" else "Open Milestone Progress Tracker",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Daily Bulletin Highlight Card
        item {
            val latestBulletin = bulletins.firstOrNull()
            if (latestBulletin != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToTab(7) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFF16A34A),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "আজকের বুলেটিন" else "Daily Bulletin",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(text = latestBulletin.dateStr, fontSize = 11.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) latestBulletin.titleBn else latestBulletin.titleEn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = LscSlateDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) latestBulletin.summaryBn else latestBulletin.summaryEn,
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LscSlateDark)
            Text(text = sub, fontSize = 10.sp, color = Color.Gray)
        }
    }
}
