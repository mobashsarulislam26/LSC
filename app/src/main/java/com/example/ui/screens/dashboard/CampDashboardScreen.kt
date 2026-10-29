package com.example.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    rooms: List<RoomEntity> = emptyList(),
    onUpdateRoomInspection: ((RoomEntity, Int, String) -> Unit)? = null
) {
    val currentSession = scheduleEvents.find { it.isCurrent } ?: scheduleEvents.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome & Role Context Hero Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = LscEmeraldPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth().testTag("dashboard_banner")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(LscEmeraldDark, LscEmeraldPrimary, Color(0xFF15803D))
                            )
                        )
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4ADE80))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "স্বাগতম, ${currentRole.titleBn}" else "Welcome, ${currentRole.titleEn}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            val activeDay = currentSession?.dayNo ?: 1
                            val activeDayBn = activeDay.toString().map {
                                when (it) {
                                    '0' -> '০'; '1' -> '১'; '2' -> '২'; '3' -> '৩'; '4' -> '৪'
                                    '5' -> '৫'; '6' -> '৬'; '7' -> '৭'; '8' -> '৮'; '9' -> '৯'
                                    else -> it
                                }
                            }.joinToString("")

                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "নিজকে গড়ো, গড়তে শেখাও • দিন $activeDayBn | ৫ দিনব্যাপী আবাসিক ক্যাম্প (২২–২৬ অক্টোবর ২০২৬) • রাঙামাটি রিসোর্ট"
                                else
                                    "Day $activeDay Active | 5-Day Residential Camp (22–26 Oct 2026) • $participantCount Participants",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
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
        }

        // Role-Specific 1-Tap Action Cockpit Card (Zero-Friction Workflow)
        item {
            RoleQuickCockpitCard(
                language = language,
                currentRole = currentRole,
                onNavigateToTab = onNavigateToTab,
                onOpenScanner = onOpenScanner
            )
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

        // Section Header: Live Executive KPIs
        item {
            DashboardSectionHeader(
                title = if (language == CampLanguage.BANGLA) "সার্বক্ষণিক ক্যাম্প স্ট্যাটাস (Live KPIs)" else "Live Operations Overview",
                subtitle = if (language == CampLanguage.BANGLA) "৬০৯ জন অংশগ্রহণকারী, ৬টি স্টেশন ও ২১টি প্রতিযোগিতার সামারি" else "Real-time headcount, attendance & station metrics",
                icon = Icons.Default.Speed
            )
        }

        // Executive Operational KPIs
        item {
            val presentCount = attendanceRecords.map { it.participantId }.distinct().size
            val presentPercentage = if (participantCount > 0) {
                (presentCount.toDouble() / participantCount.toDouble()) * 100.0
            } else {
                0.0
            }
            val presentRateDisplay = if (language == CampLanguage.BANGLA) {
                val enStr = String.format(java.util.Locale.US, "%.1f%%", presentPercentage)
                enStr.map {
                    when (it) {
                        '0' -> '০'; '1' -> '১'; '2' -> '২'; '3' -> '৩'; '4' -> '৪'
                        '5' -> '৫'; '6' -> '৬'; '7' -> '৭'; '8' -> '৮'; '9' -> '৯'
                        else -> it
                    }
                }.joinToString("")
            } else {
                String.format(java.util.Locale.US, "%.1f%%", presentPercentage)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    title = if (language == CampLanguage.BANGLA) "মোট অংশগ্রহণকারী" else "Total Registered",
                    value = "$participantCount",
                    sub = if (language == CampLanguage.BANGLA) "${groups.size}টি মূল গ্রুপ ও ২৪ সাব-গ্রুপ" else "${groups.size} Active Groups",
                    icon = Icons.Default.Groups,
                    color = Color(0xFF1D4ED8),
                    modifier = Modifier.weight(1f).clickable { onNavigateToTab(1) }
                )
                KpiCard(
                    title = if (language == CampLanguage.BANGLA) "আজকের উপস্থিতি" else "Present Rate",
                    value = presentRateDisplay,
                    sub = if (language == CampLanguage.BANGLA) "উপস্থিত $presentCount জন" else "$presentCount Attended",
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f).clickable { onNavigateToTab(2) }
                )
            }
        }

        item {
            val currentSessionTitle = if (language == CampLanguage.BANGLA) {
                currentSession?.titleBn?.take(12) ?: "সেশন ১"
            } else {
                currentSession?.titleEn?.take(12) ?: "Session 1"
            }
            val totalScores = competitionScores.size
            val totalScoresBn = totalScores.toString().map {
                when (it) {
                    '0' -> '০'; '1' -> '১'; '2' -> '২'; '3' -> '৩'; '4' -> '৪'
                    '5' -> '৫'; '6' -> '৬'; '7' -> '৭'; '8' -> '৮'; '9' -> '৯'
                    else -> it
                }
            }.joinToString("")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    title = if (language == CampLanguage.BANGLA) "স্টেশন রোটেশন" else "Active Station",
                    value = currentSessionTitle,
                    sub = if (language == CampLanguage.BANGLA) "${stations.size}টি স্টেশন × ৪৫ মি." else "${stations.size} Active Stations",
                    icon = Icons.Default.SyncAlt,
                    color = LscGoldSecondary,
                    modifier = Modifier.weight(1f).clickable { onNavigateToTab(3) }
                )
                KpiCard(
                    title = if (language == CampLanguage.BANGLA) "৮৩৫টি পুরস্কার ও স্কোর" else "Score Records",
                    value = if (language == CampLanguage.BANGLA) totalScoresBn else "$totalScores",
                    sub = if (language == CampLanguage.BANGLA) "২১টি প্রতিযোগিতা এন্ট্রি" else "Scored Entries",
                    icon = Icons.Default.EmojiEvents,
                    color = Color(0xFF9333EA),
                    modifier = Modifier.weight(1f).clickable { onNavigateToTab(4) }
                )
            }
        }

        // LSC 2026 Core Modules Quick Navigation
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(LscEmeraldLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = LscEmeraldPrimary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA) "ক্যাম্প ২০২৬ অফিশিয়াল রূপরেখা ও বিভাগসমূহ" else "LSC 2026 Camp Blueprint",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = LscSlateDark
                            )
                        }
                        Surface(
                            color = LscEmeraldLight,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "WAMY 2026",
                                color = LscEmeraldPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToTab(9) }
                                .padding(vertical = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Icon(Icons.Default.CorporateFare, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "১৩টি বিভাগ ও ১৯ কমিটি" else "13 Depts & 19 Committees",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "দায়িত্বশীল ও ১১৪ সেশন এসওপি" else "Duties & 114 Sessions",
                                    fontSize = 9.sp,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }

                        Surface(
                            color = Color(0xFFE0E7FF),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToTab(10) }
                                .padding(vertical = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0xFF4338CA), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "২৪টি সাব-গ্রুপ" else "24 Subgroups",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3730A3)
                                )
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "ইনচার্জ ও ৭১টি রিসোর্ট রুম" else "In-charges & 71 Rooms",
                                    fontSize = 9.sp,
                                    color = Color(0xFF4338CA)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToTab(11) }
                                .padding(vertical = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "ঢাকা ট্যুর, মেট্রোরেল ও নন্দন" else "Dhaka, Metro & Nandan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "জিরো-লস্ট-চাইল্ড চেকপয়েন্ট" else "Zero-Lost-Child Roll-Call",
                                    fontSize = 9.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }

                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToTab(12) }
                                .padding(vertical = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = LscEmeraldPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "পটভূমি ও প্রত্যাশিত ফলাফল" else "Rationale & Outcomes",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "নিজকে গড়ো, গড়তে শেখাও" else "WAMY OSP Vision",
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Header: Live Station Check-In & Dining Operations
        item {
            DashboardSectionHeader(
                title = if (language == CampLanguage.BANGLA) "স্টেশন হাজিরা ও ডাইনিং কাউন্টার" else "Station Check-In & Dining Control",
                subtitle = if (language == CampLanguage.BANGLA) "রিয়েল-টাইম স্টেশন চেক-ইন এবং খাবার অপচয় রোধ মনিটরিং" else "Live station check-in counts & meal wastage prevention",
                icon = Icons.Default.RestaurantMenu
            )
        }

        // Station Check-In Summary View
        item {
            StationCheckInSummaryCard(
                stations = stations,
                attendanceRecords = attendanceRecords,
                participants = participants,
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
                selectedDay = currentSession?.dayNo ?: 1,
                onOpenScanner = onOpenScanner
            )
        }

        // Section Header: Visual Analytics & Data Recovery
        item {
            DashboardSectionHeader(
                title = if (language == CampLanguage.BANGLA) "ভিজ্যুয়াল অ্যানালিটিক্স ও ডাটা সুরক্ষা" else "Visual Analytics & Data Recovery",
                subtitle = if (language == CampLanguage.BANGLA) "রিচার্টস ট্রেন্ড চার্ট এবং অফলাইন ডাটাবেজ ব্যাকআপ" else "Recharts trend insights & SQLite database backup",
                icon = Icons.Default.Insights
            )
        }

        // Recharts Visual Analytics & Trends Card
        item {
            val totalCap = participantCount.coerceAtLeast(participants.size).coerceAtLeast(1)
            val avgAttendanceVal = if (participants.isNotEmpty()) {
                participants.map { it.attendancePercentage.toDouble() }.average()
            } else {
                98.7
            }
            val careCount = participants.count { !it.medicalFlag.isNullOrBlank() }.coerceAtLeast(pendingHealthAlerts.size)
            val careRateVal = if (participants.isNotEmpty()) {
                ((careCount.toDouble() / totalCap.toDouble()) * 100.0).coerceIn(1.0, 35.0)
            } else {
                9.1
            }
            val normalHealthVal = (100.0 - careRateVal).coerceIn(65.0, 99.0)

            fun formatLocalizedPercent(v: Double): String {
                val en = String.format(java.util.Locale.US, "%.1f%%", v)
                return if (language == CampLanguage.BANGLA) {
                    en.map {
                        when (it) {
                            '0' -> '০'; '1' -> '১'; '2' -> '২'; '3' -> '৩'; '4' -> '৪'
                            '5' -> '৫'; '6' -> '৬'; '7' -> '৭'; '8' -> '৮'; '9' -> '৯'
                            else -> it
                        }
                    }.joinToString("")
                } else en
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                                Text(text = formatLocalizedPercent(avgAttendanceVal), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LscEmeraldPrimary)
                            }
                            Text(text = if (language == CampLanguage.BANGLA) "হাজিরা ট্রেন্ড" else "Attendance Rate", fontSize = 10.sp, color = Color.Gray)
                        }

                        Box(modifier = Modifier.height(24.dp).width(1.dp).background(Color(0xFFE2E8F0)))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PieChart, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = formatLocalizedPercent(normalHealthVal), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF3B82F6))
                            }
                            Text(text = if (language == CampLanguage.BANGLA) "সুস্থ (Normal)" else "Normal Health", fontSize = 10.sp, color = Color.Gray)
                        }

                        Box(modifier = Modifier.height(24.dp).width(1.dp).background(Color(0xFFE2E8F0)))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = formatLocalizedPercent(careRateVal), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFF59E0B))
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
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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

        // Section Header: Schedule, Competition Leaderboard & Dormitory Matrix
        item {
            DashboardSectionHeader(
                title = if (language == CampLanguage.BANGLA) "দৈনিক রুটিন, লিডারবোর্ড ও ৭১ কটেজ গ্রিড" else "Schedule, Standings & Dormitory Matrix",
                subtitle = if (language == CampLanguage.BANGLA) "চলমান সেশন, দলের পয়েন্ট তালিকা এবং প্লাটিনাম-গোল্ডেন-রজনীগন্ধা বেড বিন্যাস" else "Active sessions, team competition standings & resort bed layout",
                icon = Icons.Default.EventNote
            )
        }

        // Daily Schedule Section
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

        // Digital Room & Bed Occupancy Matrix (Rangamati Waterfront Resort 71 Rooms)
        item {
            VisualRoomOccupancyMatrixCard(
                rooms = rooms,
                participants = participants,
                language = language,
                onNavigateToRooms = { onNavigateToTab(10) },
                onUpdateRoomInspection = onUpdateRoomInspection
            )
        }

        // Group Points Leaderboard Snapshot
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                                        text = if (language == CampLanguage.BANGLA)
                                            "${grp.memberCount} সদস্য • লিডার: ${grp.leaderName}"
                                        else
                                            "${grp.memberCount} Members • Leader: ${grp.leaderName}",
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

        // Station Competition & 18-Star Seal Milestones Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
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
                                            "৬ স্টেশন × ৩ দিন = ১৮ ★ স্টার সিল ও মাইলস্টোন"
                                        else
                                            "6 Stations × 3 Days = 18 ★ Star Seals",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = LscSlateDark
                                    )
                                }
                                Text(
                                    text = if (language == CampLanguage.BANGLA)
                                        "প্রতিটি স্টেশনে উপস্থিতিতে ★ সিল ও সনদপত্র যোগ্যতা"
                                    else
                                        "Star seals earned across all 6 official skill stations",
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

                    Spacer(modifier = Modifier.height(10.dp))

                    // 6 Official 2026 Station Badges Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(
                            Triple("S1", "সাহিত্য", true),
                            Triple("S2", "ক্রাফট", true),
                            Triple("S3", "শিশুতোষ", true),
                            Triple("S4", "সালাত", true),
                            Triple("S5", "ক্যারিয়ার", true),
                            Triple("S6", "মেডিকেল", true)
                        ).forEach { (stn, label, isDone) ->
                            Surface(
                                color = if (isDone) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(
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
                                        text = label,
                                        fontSize = 8.sp,
                                        color = Color(0xFF15803D),
                                        maxLines = 1
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
                        border = BorderStroke(1.dp, Color(0xFFDDD6FE))
                    ) {
                        Icon(Icons.Default.MilitaryTech, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "১৮ ★ স্টার সিল ও প্রতিযোগিতা খুলুন" else "Open 18-Star Seal & Competitions",
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
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
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
                                    text = if (language == CampLanguage.BANGLA) "ক্যাম্প বুলেটিন ও দেয়ালিকা" else "Daily Bulletin",
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

/**
 * Tailored 1-Tap Command Cockpit Card rendered at the top of the Dashboard
 * so every user role can start their primary job in 1 tap without hunting menus.
 */
@Composable
private fun RoleQuickCockpitCard(
    language: CampLanguage,
    currentRole: UserRole,
    onNavigateToTab: (Int) -> Unit,
    onOpenScanner: () -> Unit
) {
    val isBangla = language == CampLanguage.BANGLA

    data class QuickActionSpec(val labelBn: String, val labelEn: String, val onClick: () -> Unit, val primary: Boolean = false)

    val (cockpitTitleBn, cockpitSubtitleBn, actions) = when (currentRole) {
        UserRole.STATION_MANAGER, UserRole.STATION_MODERATOR -> Triple(
            "⚡ স্টেশন ম্যানেজার ও মডারেটর কুইক ককপিট",
            "৬ স্টেশন লাইভ রোটেশন বেল, ১-ট্যাপ সাব-গ্রুপ হাজিরা ও ★ সিল",
            listOf(
                QuickActionSpec("⚡ লাইভ স্টেশন কনসোল", "Live Console", { onNavigateToTab(3) }, primary = true),
                QuickActionSpec("📷 র‍্যাপিড QR গান", "Rapid QR Gun", onOpenScanner),
                QuickActionSpec("🏆 স্টেশন পুরস্কার", "Prizes", { onNavigateToTab(4) })
            )
        )
        UserRole.GROUP_LEADER, UserRole.MENTOR -> Triple(
            "⚡ গ্রুপ লিডার ও সাব-গ্রুপ ইনচার্জ ককপিট",
            "৫ সেকেন্ডে সাব-গ্রুপ হাজিরা, কটেজ পরিদর্শন ও শিক্ষা সফর রোল-কল",
            listOf(
                QuickActionSpec("✓ ১-ট্যাপ হাজিরা", "1-Tap Roll-Call", { onNavigateToTab(2) }, primary = true),
                QuickActionSpec("🏠 ২৪ সাব-গ্রুপ ও রুম", "24 Subgroups", { onNavigateToTab(10) }),
                QuickActionSpec("🚌 সফর চেকপয়েন্ট", "Tour Roll-Call", { onNavigateToTab(11) })
            )
        )
        UserRole.COMPETITION_JUDGE -> Triple(
            "⚡ প্রতিযোগিতা বিচারক ককপিট (৮৩৫টি পুরস্কার)",
            "৩-ট্যাপ ১ম/২য়/৩য় বিজয়ী নির্বাচন ও সেশনেই পুরস্কার প্রদান নিশ্চিতকরণ",
            listOf(
                QuickActionSpec("🥇 ৩-ট্যাপ পডিয়াম স্কোর", "3-Tap Podium", { onNavigateToTab(4) }, primary = true),
                QuickActionSpec("🎁 পুরস্কার স্টক", "Prize Stock", { onNavigateToTab(6) })
            )
        )
        UserRole.MEDICAL_TEAM -> Triple(
            "⚡ স্টেশন-৬ মেডিকেল ও ডেন্টাল বাস ককপিট",
            "উচ্চতা-ওজন এন্ট্রি, ভ্রাম্যমাণ ডেন্টাল বাস রেফারেল ও আই ক্যাম্প ভিশন স্ক্রিনিং",
            listOf(
                QuickActionSpec("🩺 স্টেশন-৬ চেকআপ", "Station-6 Checkup", { onNavigateToTab(14) }, primary = true),
                QuickActionSpec("📷 আইডি স্ক্যান", "Scan ID", onOpenScanner)
            )
        )
        UserRole.INVENTORY_OFFICER -> Triple(
            "⚡ ৩০টি উপহার, মায়েদের চাদর ও ডাইনিং ককপিট",
            "শিক্ষার্থী কিট বিতরণ, মায়েদের চাদর ট্র্যাকার ও ৮৩৫ পুরস্কার ইনভেন্টরি",
            listOf(
                QuickActionSpec("🎁 উপহার ও চাদর বিতরণ", "Gifts & Chador", { onNavigateToTab(6) }, primary = true),
                QuickActionSpec("🍽 খাবার স্ক্যান", "Meal Scan", onOpenScanner)
            )
        )
        else -> Triple(
            "⚡ ক্যাম্প কমান্ড কুইক অ্যাকশন (১-ট্যাপ)",
            "স্টেশন লাইভ কনসোল, ১-ট্যাপ সাব-গ্রুপ হাজিরা ও র‍্যাপিড কিউআর গান",
            listOf(
                QuickActionSpec("⚡ স্টেশন কনসোল", "Station Console", { onNavigateToTab(3) }, primary = true),
                QuickActionSpec("✓ ব্যাচ হাজিরা", "Batch Roll-Call", { onNavigateToTab(2) }),
                QuickActionSpec("📷 QR স্ক্যানার", "QR Scanner", onOpenScanner)
            )
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("role_quick_cockpit_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (isBangla) cockpitTitleBn else "${currentRole.titleEn} Quick Cockpit",
                color = LscGoldLight,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
            Text(
                text = if (isBangla) cockpitSubtitleBn else "Execute daily operations in 1–2 taps",
                color = Color(0xFFCBD5E1),
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                actions.forEach { act ->
                    if (act.primary) {
                        Button(
                            onClick = act.onClick,
                            colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(
                                text = if (isBangla) act.labelBn else act.labelEn,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = act.onClick,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF475569)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isBangla) act.labelBn else act.labelEn,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardSectionHeader(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(26.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(LscEmeraldPrimary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LscEmeraldPrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = LscSlateDark
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF64748B)
            )
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(color)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = value,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LscSlateDark,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = sub,
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1
                )
            }
        }
    }
}
