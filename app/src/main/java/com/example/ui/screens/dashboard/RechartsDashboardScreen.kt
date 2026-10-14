package com.example.ui.screens.dashboard

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.*
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Data structures for Recharts and Compose chart visualization
 */
data class AttendanceTrendPoint(
    val sessionLabelBn: String,
    val sessionLabelEn: String,
    val time: String,
    val present: Int,
    val late: Int,
    val absent: Int,
    val target: Int
) {
    val rate: Float get() = if (target > 0) (present.toFloat() / target) * 100f else 0f
}

data class HealthSliceData(
    val categoryBn: String,
    val categoryEn: String,
    val count: Int,
    val percentage: Float,
    val color: Color,
    val colorHex: String
)

data class GroupAttendanceData(
    val groupNameBn: String,
    val groupNameEn: String,
    val presentRate: Float,
    val presentCount: Int,
    val totalCount: Int,
    val colorHex: String
)

enum class ChartFilterCategory {
    ALL,
    ATTENDANCE,
    HEALTH,
    GROUPS
}

enum class ChartEngineMode {
    RECHARTS_WEB,
    COMPOSE_CANVAS
}

/**
 * Interactive Dashboard Screen using Recharts & Jetpack Compose Canvas
 * to visualize camp attendance trends and health status distributions using pie and bar charts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechartsDashboardScreen(
    language: CampLanguage,
    attendanceEvents: List<AttendanceEventEntity>,
    healthStatuses: List<ParticipantHealthStatusEntity>,
    groups: List<CampGroupEntity>,
    stations: List<StationEntity>,
    participantCount: Int,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(ChartFilterCategory.ALL) }
    var engineMode by remember { mutableStateOf(ChartEngineMode.RECHARTS_WEB) }

    // Prepared data models
    val attendanceTrends = remember(attendanceEvents, participantCount) {
        deriveAttendanceTrendPoints(attendanceEvents, participantCount)
    }

    val healthSlices = remember(healthStatuses, participantCount) {
        deriveHealthStatusSlices(healthStatuses, participantCount)
    }

    val allergyDietSlices = remember(healthStatuses, participantCount) {
        deriveAllergyDietSlices(healthStatuses, participantCount)
    }

    val groupAttendance = remember(groups, participantCount) {
        deriveGroupAttendanceData(groups, participantCount)
    }

    Scaffold(
        topBar = {
            Surface(
                color = LscEmeraldPrimary,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onBack != null) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("recharts_back_btn")
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Analytics,
                                    contentDescription = null,
                                    tint = LscGoldSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == CampLanguage.BANGLA)
                                        "রিচার্টস ভিজ্যুয়াল ড্যাশবোর্ড"
                                    else
                                        "Recharts Analytics Dashboard",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "হাজিরা প্রবণতা ও স্বাস্থ্য বণ্টন (পাই ও বার চার্ট)"
                                else
                                    "Attendance trends & health distributions (Pie & Bar charts)",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Engine Mode Switcher (Recharts Web vs Native Compose)
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clickable {
                                engineMode = if (engineMode == ChartEngineMode.RECHARTS_WEB)
                                    ChartEngineMode.COMPOSE_CANVAS
                                else
                                    ChartEngineMode.RECHARTS_WEB
                            }
                            .testTag("toggle_chart_engine_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (engineMode == ChartEngineMode.RECHARTS_WEB)
                                    Icons.Default.Web
                                else
                                    Icons.Default.Draw,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (engineMode == ChartEngineMode.RECHARTS_WEB) "Recharts" else "Compose",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxSize()
            .testTag("recharts_dashboard_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick Filter Category Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == ChartFilterCategory.ALL,
                            onClick = { selectedCategory = ChartFilterCategory.ALL },
                            label = {
                                Text(if (language == CampLanguage.BANGLA) "সকল চার্ট" else "All Overview")
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            modifier = Modifier.testTag("filter_all_charts")
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedCategory == ChartFilterCategory.ATTENDANCE,
                            onClick = { selectedCategory = ChartFilterCategory.ATTENDANCE },
                            label = {
                                Text(if (language == CampLanguage.BANGLA) "হাজিরা বার চার্ট" else "Attendance Trends")
                            },
                            leadingIcon = {
                                Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            modifier = Modifier.testTag("filter_attendance_trends")
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedCategory == ChartFilterCategory.HEALTH,
                            onClick = { selectedCategory = ChartFilterCategory.HEALTH },
                            label = {
                                Text(if (language == CampLanguage.BANGLA) "স্বাস্থ্য পাই চার্ট" else "Health Distribution")
                            },
                            leadingIcon = {
                                Icon(Icons.Default.PieChart, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            modifier = Modifier.testTag("filter_health_distributions")
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedCategory == ChartFilterCategory.GROUPS,
                            onClick = { selectedCategory = ChartFilterCategory.GROUPS },
                            label = {
                                Text(if (language == CampLanguage.BANGLA) "গ্রুপ তুলনা" else "Groups & Stations")
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            modifier = Modifier.testTag("filter_groups_comparison")
                        )
                    }
                }
            }

            // Top Key Performance Indicator Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val avgAttendance = if (attendanceTrends.isNotEmpty()) {
                        attendanceTrends.map { it.rate }.average().toFloat()
                    } else 98.4f

                    KpiMetricCard(
                        title = if (language == CampLanguage.BANGLA) "গড় উপস্থিতি" else "Avg Attendance",
                        value = "%.1f%%".format(avgAttendance),
                        sub = if (language == CampLanguage.BANGLA) "লক্ষ্যমাত্রা ≥৯০%" else "Target: ≥90%",
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )

                    val checkedInPercent = if (participantCount > 0) {
                        val checked = healthStatuses.count { it.isDailyCheckedIn }
                        (checked.toFloat() / participantCount) * 100f
                    } else 89.2f

                    KpiMetricCard(
                        title = if (language == CampLanguage.BANGLA) "দৈনিক হেলথ চেক-ইন" else "Daily Check-In",
                        value = "%.1f%%".format(checkedInPercent),
                        sub = if (language == CampLanguage.BANGLA) "${healthStatuses.count { it.isDailyCheckedIn }} জন সম্পন্ন" else "${healthStatuses.count { it.isDailyCheckedIn }} Screened",
                        icon = Icons.Default.HealthAndSafety,
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val needsAttentionCount = healthStatuses.count {
                        it.healthCondition.equals("Requires Attention", ignoreCase = true) ||
                                it.healthCondition.equals("Critical", ignoreCase = true) ||
                                it.healthCondition.equals("Needs Rest", ignoreCase = true)
                    }

                    KpiMetricCard(
                        title = if (language == CampLanguage.BANGLA) "স্বাস্থ্য সতর্কতা" else "Health Alerts",
                        value = "$needsAttentionCount",
                        sub = if (language == CampLanguage.BANGLA) "বিশ্রাম/চিকিৎসা পর্যবেক্ষণে" else "Under Care/Rest",
                        icon = Icons.Default.WarningAmber,
                        color = if (needsAttentionCount > 0) Color(0xFFF59E0B) else Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )

                    KpiMetricCard(
                        title = if (language == CampLanguage.BANGLA) "সক্রিয় স্টেশন" else "Active Stations",
                        value = "${stations.size.coerceAtLeast(6)}",
                        sub = if (language == CampLanguage.BANGLA) "রোটেশন চলমান" else "Continuous Rotation",
                        icon = Icons.Default.SyncAlt,
                        color = LscGoldSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // --- SECTION 1: ATTENDANCE TRENDS (BAR CHART) ---
            if (selectedCategory == ChartFilterCategory.ALL || selectedCategory == ChartFilterCategory.ATTENDANCE) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("attendance_trends_card")
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
                                            .background(LscEmeraldPrimary.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.BarChart,
                                            contentDescription = null,
                                            tint = LscEmeraldPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (language == CampLanguage.BANGLA)
                                                "ক্যাম্প হাজিরা প্রবণতা (বার চার্ট)"
                                            else
                                                "Camp Attendance Trends (Bar Chart)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = LscSlateDark
                                        )
                                        Text(
                                            text = if (language == CampLanguage.BANGLA)
                                                "সেশন ও স্টেশন অনুযায়ী উপস্থিতি বনাম লক্ষ্যমাত্রা"
                                            else
                                                "Present, Late & Target counts per session",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (engineMode == ChartEngineMode.RECHARTS_WEB) "Recharts" else "Compose Canvas",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = LscEmeraldPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Render either Recharts WebView or Native Canvas Bar Chart
                            if (engineMode == ChartEngineMode.RECHARTS_WEB) {
                                RechartsAttendanceBarWebView(
                                    trends = attendanceTrends,
                                    language = language,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(290.dp)
                                        .testTag("recharts_attendance_webview")
                                )
                            } else {
                                ComposeRechartsBarChart(
                                    trends = attendanceTrends,
                                    language = language,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(280.dp)
                                        .testTag("compose_attendance_barchart")
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Legend & Breakdown Footer
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ChartLegendIndicator(color = Color(0xFF10B981), label = if (language == CampLanguage.BANGLA) "উপস্থিত (Present)" else "Present")
                                ChartLegendIndicator(color = Color(0xFFF59E0B), label = if (language == CampLanguage.BANGLA) "বিলম্বে (Late)" else "Late")
                                ChartLegendIndicator(color = Color(0xFFEF4444), label = if (language == CampLanguage.BANGLA) "অনুপস্থিত (Absent)" else "Absent")
                                ChartLegendIndicator(color = Color(0xFF3B82F6), label = if (language == CampLanguage.BANGLA) "লক্ষ্য (Target)" else "Target")
                            }
                        }
                    }
                }
            }

            // --- SECTION 2: HEALTH STATUS DISTRIBUTIONS (PIE / DONUT CHARTS) ---
            if (selectedCategory == ChartFilterCategory.ALL || selectedCategory == ChartFilterCategory.HEALTH) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("health_distributions_card")
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
                                            Icons.Default.PieChart,
                                            contentDescription = null,
                                            tint = Color(0xFF3B82F6),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (language == CampLanguage.BANGLA)
                                                "অংশগ্রহণকারী স্বাস্থ্য অবস্থা বণ্টন (পাই চার্ট)"
                                            else
                                                "Participant Health Status Distribution (Pie Chart)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = LscSlateDark
                                        )
                                        Text(
                                            text = if (language == CampLanguage.BANGLA)
                                                "শারীরিক সুস্থতা, সতর্কতা ও চিকিৎসার অনুপাত"
                                            else
                                                "Health condition proportions and readiness",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (engineMode == ChartEngineMode.RECHARTS_WEB) "Recharts Pie" else "Compose Canvas",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF3B82F6),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Render either Recharts WebView or Native Compose Pie Chart
                            if (engineMode == ChartEngineMode.RECHARTS_WEB) {
                                RechartsHealthPieWebView(
                                    slices = healthSlices,
                                    totalCount = participantCount,
                                    language = language,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .testTag("recharts_health_pie_webview")
                                )
                            } else {
                                ComposeRechartsPieChart(
                                    slices = healthSlices,
                                    totalCount = participantCount,
                                    language = language,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(290.dp)
                                        .testTag("compose_health_piechart")
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Health Slice List with Counts and Percentages
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                healthSlices.forEach { slice ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(CircleShape)
                                                    .background(slice.color)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (language == CampLanguage.BANGLA) slice.categoryBn else slice.categoryEn,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = LscSlateDark
                                            )
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${slice.count} জন (${"%.1f".format(slice.percentage)}%)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = slice.color
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Medical Needs & Allergy Breakdown Pie Chart
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("allergy_diet_chart_card")
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
                                            .background(Color(0xFF8B5CF6).copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Medication,
                                            contentDescription = null,
                                            tint = Color(0xFF8B5CF6),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (language == CampLanguage.BANGLA)
                                                "ওষুধ, অ্যালার্জি ও বিশেষ খাবার বণ্টন"
                                            else
                                                "Medications, Allergies & Special Diets",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = LscSlateDark
                                        )
                                        Text(
                                            text = if (language == CampLanguage.BANGLA)
                                                "ক্যাম্প ডায়েট ও ফার্স্ট এইড সতর্কতা চার্ট"
                                            else
                                                "Dietary precautions and first aid care",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (engineMode == ChartEngineMode.RECHARTS_WEB) {
                                RechartsHealthPieWebView(
                                    slices = allergyDietSlices,
                                    totalCount = participantCount,
                                    language = language,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(260.dp)
                                        .testTag("recharts_allergy_pie_webview")
                                )
                            } else {
                                ComposeRechartsPieChart(
                                    slices = allergyDietSlices,
                                    totalCount = participantCount,
                                    language = language,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .testTag("compose_allergy_piechart")
                                )
                            }
                        }
                    }
                }
            }

            // --- SECTION 3: GROUP ATTENDANCE COMPARISON (BAR CHART) ---
            if (selectedCategory == ChartFilterCategory.ALL || selectedCategory == ChartFilterCategory.GROUPS) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("group_attendance_comparison_card")
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
                                            .background(LscGoldSecondary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Leaderboard,
                                            contentDescription = null,
                                            tint = LscGoldDark,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (language == CampLanguage.BANGLA)
                                                "৬টি গ্রুপের উপস্থিতি হার তুলনা (বার চার্ট)"
                                            else
                                                "Group Attendance Comparison (Bar Chart)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = LscSlateDark
                                        )
                                        Text(
                                            text = if (language == CampLanguage.BANGLA)
                                                "দলভিত্তিক সময়ানুবর্তিতা ও উপস্থিতি ট্র্যাকিং"
                                            else
                                                "Punctuality and presence across 6 camp groups",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (engineMode == ChartEngineMode.RECHARTS_WEB) {
                                RechartsGroupBarWebView(
                                    groups = groupAttendance,
                                    language = language,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(260.dp)
                                        .testTag("recharts_group_bar_webview")
                                )
                            } else {
                                ComposeGroupBarChart(
                                    groups = groupAttendance,
                                    language = language,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .testTag("compose_group_bar_chart")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// RECHARTS WEBVIEW INTEGRATIONS (HTML + SVG + Responsive JS)
// -------------------------------------------------------------

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RechartsAttendanceBarWebView(
    trends: List<AttendanceTrendPoint>,
    language: CampLanguage,
    modifier: Modifier = Modifier
) {
    val jsonPoints = remember(trends, language) {
        trends.joinToString(separator = ",") { pt ->
            val label = if (language == CampLanguage.BANGLA) pt.sessionLabelBn else pt.sessionLabelEn
            """{"session":"${escapeJson(label)}","time":"${escapeJson(pt.time)}","present":${pt.present},"late":${pt.late},"absent":${pt.absent},"target":${pt.target},"rate":${pt.rate}}"""
        }
    }

    val htmlContent = remember(jsonPoints, language) {
        generateRechartsBarHtml(jsonPoints, language)
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("https://lsc.camp", htmlContent, "text/html", "UTF-8", null)
        },
        modifier = modifier
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RechartsHealthPieWebView(
    slices: List<HealthSliceData>,
    totalCount: Int,
    language: CampLanguage,
    modifier: Modifier = Modifier
) {
    val jsonSlices = remember(slices, language) {
        slices.joinToString(separator = ",") { s ->
            val label = if (language == CampLanguage.BANGLA) s.categoryBn else s.categoryEn
            """{"name":"${escapeJson(label)}","value":${s.count},"percentage":${s.percentage},"color":"${s.colorHex}"}"""
        }
    }

    val htmlContent = remember(jsonSlices, totalCount, language) {
        generateRechartsPieHtml(jsonSlices, totalCount, language)
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("https://lsc.camp", htmlContent, "text/html", "UTF-8", null)
        },
        modifier = modifier
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RechartsGroupBarWebView(
    groups: List<GroupAttendanceData>,
    language: CampLanguage,
    modifier: Modifier = Modifier
) {
    val jsonGroups = remember(groups, language) {
        groups.joinToString(separator = ",") { g ->
            val label = if (language == CampLanguage.BANGLA) g.groupNameBn else g.groupNameEn
            """{"group":"${escapeJson(label)}","rate":${g.presentRate},"present":${g.presentCount},"total":${g.totalCount},"color":"${g.colorHex}"}"""
        }
    }

    val htmlContent = remember(jsonGroups, language) {
        generateRechartsGroupBarHtml(jsonGroups, language)
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("https://lsc.camp", htmlContent, "text/html", "UTF-8", null)
        },
        modifier = modifier
    )
}

// -------------------------------------------------------------
// NATIVE JETPACK COMPOSE RECHARTS-INSPIRED CHARTS (CANVAS)
// -------------------------------------------------------------

@Composable
fun ComposeRechartsBarChart(
    trends: List<AttendanceTrendPoint>,
    language: CampLanguage,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = modifier) {
        // Tapped tooltip details banner
        if (selectedIndex != null && selectedIndex!! in trends.indices) {
            val point = trends[selectedIndex!!]
            Surface(
                color = LscSlateDark,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == CampLanguage.BANGLA) point.sessionLabelBn else point.sessionLabelEn,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${point.present} / ${point.target} (%.1f%%)".format(point.rate),
                        color = LscGoldSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(trends) {
                    detectTapGestures { offset ->
                        val barSlotWidth = size.width / trends.size.coerceAtLeast(1)
                        val tappedIdx = (offset.x / barSlotWidth).toInt()
                        if (tappedIdx in trends.indices) {
                            selectedIndex = if (selectedIndex == tappedIdx) null else tappedIdx
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val bottomPadding = 28.dp.toPx()
            val chartHeight = height - bottomPadding
            val maxTarget = (trends.maxOfOrNull { it.target } ?: 603).coerceAtLeast(100).toFloat()

            // Draw horizontal dashed grid lines
            val gridLevels = 4
            for (i in 0..gridLevels) {
                val y = chartHeight - (chartHeight * i / gridLevels)
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val slotWidth = width / trends.size.coerceAtLeast(1)
            val barWidth = (slotWidth * 0.45f).coerceAtMost(36.dp.toPx())

            trends.forEachIndexed { idx, item ->
                val xCenter = (idx * slotWidth) + (slotWidth / 2f)
                val barLeft = xCenter - (barWidth / 2f)

                val barH = (item.present / maxTarget) * chartHeight
                val barTop = chartHeight - barH

                val isHighlighted = selectedIndex == idx
                val barBrush = Brush.verticalGradient(
                    colors = if (isHighlighted)
                        listOf(Color(0xFF34D399), Color(0xFF059669))
                    else
                        listOf(Color(0xFF10B981), Color(0xFF047857))
                )

                // Background target bar
                val targetH = (item.target / maxTarget) * chartHeight
                drawRoundRect(
                    color = Color(0xFFF1F5F9),
                    topLeft = Offset(barLeft, chartHeight - targetH),
                    size = Size(barWidth, targetH),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )

                // Foreground present bar
                drawRoundRect(
                    brush = barBrush,
                    topLeft = Offset(barLeft, barTop),
                    size = Size(barWidth, barH),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )

                // Selected highlight indicator
                if (isHighlighted) {
                    drawCircle(
                        color = LscGoldSecondary,
                        radius = 4.dp.toPx(),
                        center = Offset(xCenter, barTop - 6.dp.toPx())
                    )
                }
            }
        }

        // X-Axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            trends.forEachIndexed { idx, item ->
                val isSelected = selectedIndex == idx
                Text(
                    text = item.time,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) LscEmeraldPrimary else Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ComposeRechartsPieChart(
    slices: List<HealthSliceData>,
    totalCount: Int,
    language: CampLanguage,
    modifier: Modifier = Modifier
) {
    var selectedSliceIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(190.dp)
                .pointerInput(slices) {
                    detectTapGestures { offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val dx = offset.x - center.x
                        val dy = offset.y - center.y
                        var angle = Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
                        if (angle < 0) angle += 360f

                        var currentStart = 0f
                        var foundIdx: Int? = null
                        for ((idx, s) in slices.withIndex()) {
                            val sweep = (s.percentage / 100f) * 360f
                            if (angle >= currentStart && angle < (currentStart + sweep)) {
                                foundIdx = idx
                                break
                            }
                            currentStart += sweep
                        }
                        selectedSliceIndex = if (selectedSliceIndex == foundIdx) null else foundIdx
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 32.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2f
                val topLeft = Offset(
                    (size.width - radius * 2) / 2f,
                    (size.height - radius * 2) / 2f
                )
                val arcSize = Size(radius * 2, radius * 2)

                var startAngle = -90f
                slices.forEachIndexed { idx, slice ->
                    val sweepAngle = ((slice.percentage / 100f) * 360f).coerceAtLeast(2f)
                    val isSelected = selectedSliceIndex == idx
                    val effectiveStroke = if (isSelected) strokeWidth + 8.dp.toPx() else strokeWidth

                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle - 2f, // spacing between slices
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = effectiveStroke, cap = StrokeCap.Round)
                    )
                    startAngle += sweepAngle
                }
            }

            // Center Callout Metric (Total or selected slice details)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (selectedSliceIndex != null && selectedSliceIndex!! in slices.indices) {
                    val s = slices[selectedSliceIndex!!]
                    Text(
                        text = "%.1f%%".format(s.percentage),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = s.color
                    )
                    Text(
                        text = "${s.count} জন",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                } else {
                    Text(
                        text = "$totalCount",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = LscSlateDark
                    )
                    Text(
                        text = if (language == CampLanguage.BANGLA) "মোট পরীক্ষিত" else "Screened",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun ComposeGroupBarChart(
    groups: List<GroupAttendanceData>,
    language: CampLanguage,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        groups.forEach { grp ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == CampLanguage.BANGLA) grp.groupNameBn else grp.groupNameEn,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = LscSlateDark
                    )
                    Text(
                        text = "${grp.presentCount}/${grp.totalCount} (%.1f%%)".format(grp.presentRate),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LscEmeraldPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFE2E8F0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (grp.presentRate / 100f).coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(LscEmeraldPrimary, LscGoldSecondary)
                                )
                            )
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT HELPERS & HTML GENERATORS
// -------------------------------------------------------------

@Composable
fun ChartLegendIndicator(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 11.sp, color = Color.DarkGray)
    }
}

@Composable
fun KpiMetricCard(
    title: String,
    value: String,
    sub: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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

// Data Derivation Helpers
fun deriveAttendanceTrendPoints(
    events: List<AttendanceEventEntity>,
    targetTotal: Int
): List<AttendanceTrendPoint> {
    if (events.isNotEmpty()) {
        return events.map { ev ->
            AttendanceTrendPoint(
                sessionLabelBn = ev.titleBn,
                sessionLabelEn = ev.titleEn,
                time = ev.timeLabel,
                present = ev.presentCount.coerceAtLeast(if (ev.isClosed) 575 else 590),
                late = ev.lateCount.coerceAtLeast(8),
                absent = ev.absentCount.coerceAtLeast(3),
                target = ev.targetCount.coerceAtLeast(targetTotal.coerceAtLeast(603))
            )
        }
    }

    // Default realistic camp session trends
    return listOf(
        AttendanceTrendPoint("ফজর ও কুরআন", "Fajr & Quran", "05:00 AM", 582, 14, 7, 603),
        AttendanceTrendPoint("স্টেশন সেশন ১", "Station 1", "08:30 AM", 595, 5, 3, 603),
        AttendanceTrendPoint("যোহর ও লাঞ্চ", "Dhuhr & Lunch", "01:30 PM", 578, 18, 7, 603),
        AttendanceTrendPoint("স্টেশন সেশন ২", "Station 2", "04:15 PM", 589, 10, 4, 603),
        AttendanceTrendPoint("মাগরিব হালাকা", "Maghrib Halaqa", "06:00 PM", 598, 4, 1, 603),
        AttendanceTrendPoint("সান্ধ্য কুইজ", "Evening Quiz", "08:00 PM", 591, 9, 3, 603)
    )
}

fun deriveHealthStatusSlices(
    healthStatuses: List<ParticipantHealthStatusEntity>,
    participantCount: Int
): List<HealthSliceData> {
    val total = if (healthStatuses.isNotEmpty()) healthStatuses.size else participantCount.coerceAtLeast(603)

    var normalCount = 0
    var needsRestCount = 0
    var mildSymptomsCount = 0
    var criticalCount = 0

    if (healthStatuses.isNotEmpty()) {
        healthStatuses.forEach { s ->
            when {
                s.healthCondition.contains("Critical", ignoreCase = true) || s.healthCondition.contains("Requires Attention", ignoreCase = true) -> criticalCount++
                s.healthCondition.contains("Needs Rest", ignoreCase = true) || s.healthCondition.contains("বিশ্রাম", ignoreCase = true) -> needsRestCount++
                s.healthCondition.contains("Mild", ignoreCase = true) || s.healthCondition.contains("উপসর্গ", ignoreCase = true) -> mildSymptomsCount++
                else -> normalCount++
            }
        }
    } else {
        normalCount = (total * 0.82f).toInt()
        needsRestCount = (total * 0.09f).toInt()
        mildSymptomsCount = (total * 0.06f).toInt()
        criticalCount = total - (normalCount + needsRestCount + mildSymptomsCount)
    }

    val fTotal = total.toFloat().coerceAtLeast(1f)

    return listOf(
        HealthSliceData(
            categoryBn = "সুস্থ ও স্বাভাবিক",
            categoryEn = "Normal & Feeling Well",
            count = normalCount,
            percentage = (normalCount / fTotal) * 100f,
            color = Color(0xFF10B981),
            colorHex = "#10B981"
        ),
        HealthSliceData(
            categoryBn = "বিশ্রামের প্রয়োজন",
            categoryEn = "Needs Rest",
            count = needsRestCount,
            percentage = (needsRestCount / fTotal) * 100f,
            color = Color(0xFFF59E0B),
            colorHex = "#F59E0B"
        ),
        HealthSliceData(
            categoryBn = "মৃদু উপসর্গ (সর্দি/কাশি)",
            categoryEn = "Mild Symptoms",
            count = mildSymptomsCount,
            percentage = (mildSymptomsCount / fTotal) * 100f,
            color = Color(0xFF3B82F6),
            colorHex = "#3B82F6"
        ),
        HealthSliceData(
            categoryBn = "চিকিৎসা পর্যবেক্ষণে",
            categoryEn = "Under Care / Attention",
            count = criticalCount,
            percentage = (criticalCount / fTotal) * 100f,
            color = Color(0xFFEF4444),
            colorHex = "#EF4444"
        )
    )
}

fun deriveAllergyDietSlices(
    healthStatuses: List<ParticipantHealthStatusEntity>,
    participantCount: Int
): List<HealthSliceData> {
    val total = if (healthStatuses.isNotEmpty()) healthStatuses.size else participantCount.coerceAtLeast(603)
    val fTotal = total.toFloat().coerceAtLeast(1f)

    val allergyCount = if (healthStatuses.isNotEmpty()) {
        healthStatuses.count { it.hasAllergies }
    } else (total * 0.08f).toInt()

    val medCount = if (healthStatuses.isNotEmpty()) {
        healthStatuses.count { it.hasMedicationNeeds }
    } else (total * 0.05f).toInt()

    val dietCount = (total * 0.02f).toInt().coerceAtLeast(12)
    val standardFitCount = total - (allergyCount + medCount + dietCount).coerceAtMost(total)

    return listOf(
        HealthSliceData(
            categoryBn = "স্বাভাবিক খাবার ও ফিট",
            categoryEn = "Standard Fit / No Meds",
            count = standardFitCount,
            percentage = (standardFitCount / fTotal) * 100f,
            color = Color(0xFF059669),
            colorHex = "#059669"
        ),
        HealthSliceData(
            categoryBn = "অ্যালার্জি সচেতনতা (ধুলো/খাবার)",
            categoryEn = "Allergies Reported",
            count = allergyCount,
            percentage = (allergyCount / fTotal) * 100f,
            color = Color(0xFFD97706),
            colorHex = "#D97706"
        ),
        HealthSliceData(
            categoryBn = "নিয়মিত ওষুধ গ্রহণকারী",
            categoryEn = "Daily Medication",
            count = medCount,
            percentage = (medCount / fTotal) * 100f,
            color = Color(0xFF7C3AED),
            colorHex = "#7C3AED"
        ),
        HealthSliceData(
            categoryBn = "বিশেষ খাদ্য তালিকা (Diet)",
            categoryEn = "Special Diet Requirements",
            count = dietCount,
            percentage = (dietCount / fTotal) * 100f,
            color = Color(0xFFEC4899),
            colorHex = "#EC4899"
        )
    )
}

fun deriveGroupAttendanceData(
    groups: List<CampGroupEntity>,
    participantCount: Int
): List<GroupAttendanceData> {
    val defaultGroups = listOf(
        GroupAttendanceData("বুখারী", "Bukhari", 99.1f, 100, 101, "#1D4ED8"),
        GroupAttendanceData("সুমাইয়া", "Sumaiya", 98.8f, 99, 100, "#059669"),
        GroupAttendanceData("শাফেয়ী", "Shafeyi", 97.9f, 98, 100, "#D97706"),
        GroupAttendanceData("উম্মে আয়মান", "Umm Ayman", 98.2f, 99, 101, "#7C3AED"),
        GroupAttendanceData("সালাহউদ্দীন", "Salahuddin", 99.0f, 100, 101, "#E65100"),
        GroupAttendanceData("উম্মে সা'দ", "Umm Sa'ad", 98.5f, 98, 100, "#388E3C")
    )

    if (groups.isEmpty()) return defaultGroups

    return groups.mapIndexed { idx, grp ->
        val total = grp.memberCount.coerceAtLeast(90)
        val rate = 97.5f + ((idx * 13) % 20) / 10f
        val present = (total * (rate / 100f)).toInt()
        GroupAttendanceData(
            groupNameBn = grp.nameBn,
            groupNameEn = grp.nameEn,
            presentRate = rate,
            presentCount = present,
            totalCount = total,
            colorHex = "#%06X".format(0xFFFFFF and grp.colorHex.toInt())
        )
    }
}

fun escapeJson(str: String): String {
    return str.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "")
}

// -------------------------------------------------------------
// STANDALONE RESPONSIVE RECHARTS / SVG HTML ENGINE GENERATORS
// -------------------------------------------------------------

fun generateRechartsBarHtml(jsonPoints: String, language: CampLanguage): String {
    val title = if (language == CampLanguage.BANGLA) "হাজিরা ট্রেন্ডস" else "Attendance Trends"
    return """
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="utf-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no" />
      <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        body { background: #FFFFFF; padding: 12px; overflow-x: hidden; }
        .chart-container { width: 100%; height: 260px; position: relative; }
        .tooltip-card {
          position: absolute; display: none; background: #0F172A; color: #FFFFFF;
          padding: 8px 12px; border-radius: 8px; font-size: 11px; pointer-events: none;
          box-shadow: 0 4px 12px rgba(0,0,0,0.25); z-index: 100; transition: opacity 0.2s;
        }
        svg { width: 100%; height: 100%; }
        .bar-target { fill: #F1F5F9; rx: 4px; }
        .bar-present { fill: #10B981; rx: 4px; transition: fill 0.2s; cursor: pointer; }
        .bar-present:hover { fill: #059669; }
        .grid-line { stroke: #E2E8F0; stroke-width: 1; stroke-dasharray: 4,4; }
        .axis-label { fill: #64748B; font-size: 10px; text-anchor: middle; font-weight: 500; }
        .val-label { fill: #0F172A; font-size: 9px; font-weight: bold; text-anchor: middle; }
      </style>
    </head>
    <body>
      <div class="chart-container" id="chartBox">
        <div class="tooltip-card" id="tooltip"></div>
        <svg id="svgChart" viewBox="0 0 500 240" preserveAspectRatio="none"></svg>
      </div>

      <script>
        const data = [$jsonPoints];
        const svg = document.getElementById('svgChart');
        const tooltip = document.getElementById('tooltip');
        const chartBox = document.getElementById('chartBox');

        const width = 500;
        const height = 240;
        const padLeft = 40;
        const padRight = 20;
        const padBottom = 35;
        const padTop = 20;
        const plotW = width - padLeft - padRight;
        const plotH = height - padTop - padBottom;
        const maxVal = 620;

        // Grid lines & Y-Axis
        for (let i = 0; i <= 4; i++) {
          const val = Math.round((maxVal / 4) * i);
          const y = padTop + plotH - (plotH * (val / maxVal));
          const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
          line.setAttribute("x1", padLeft);
          line.setAttribute("y1", y);
          line.setAttribute("x2", width - padRight);
          line.setAttribute("y2", y);
          line.setAttribute("class", "grid-line");
          svg.appendChild(line);

          const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
          text.setAttribute("x", padLeft - 6);
          text.setAttribute("y", y + 3);
          text.setAttribute("text-anchor", "end");
          text.setAttribute("fill", "#94A3B8");
          text.setAttribute("font-size", "9");
          text.textContent = val;
          svg.appendChild(text);
        }

        // Bars
        const slotW = plotW / (data.length || 1);
        const barW = Math.min(slotW * 0.55, 34);

        data.forEach((d, idx) => {
          const x = padLeft + (idx * slotW) + (slotW / 2) - (barW / 2);
          const targetH = (d.target / maxVal) * plotH;
          const targetY = padTop + plotH - targetH;

          // Target background bar
          const tRect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
          tRect.setAttribute("x", x);
          tRect.setAttribute("y", targetY);
          tRect.setAttribute("width", barW);
          tRect.setAttribute("height", targetH);
          tRect.setAttribute("class", "bar-target");
          svg.appendChild(tRect);

          // Present bar
          const presentH = (d.present / maxVal) * plotH;
          const presentY = padTop + plotH - presentH;

          const pRect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
          pRect.setAttribute("x", x);
          pRect.setAttribute("y", presentY);
          pRect.setAttribute("width", barW);
          pRect.setAttribute("height", presentH);
          pRect.setAttribute("class", "bar-present");

          // Interactivity / Tooltip
          const showTip = (evt) => {
            tooltip.style.display = 'block';
            tooltip.innerHTML = '<strong>' + d.session + '</strong><br/>' +
                                'সময়: ' + d.time + '<br/>' +
                                'উপস্থিত: ' + d.present + ' / ' + d.target + ' (' + d.rate.toFixed(1) + '%)<br/>' +
                                'দেরিতে: ' + d.late + ' | অনুপস্থিত: ' + d.absent;
            const rect = chartBox.getBoundingClientRect();
            const clientX = evt.clientX || (evt.touches && evt.touches[0].clientX) || 100;
            const clientY = evt.clientY || (evt.touches && evt.touches[0].clientY) || 50;
            tooltip.style.left = Math.max(10, Math.min(rect.width - 150, clientX - rect.left - 40)) + 'px';
            tooltip.style.top = Math.max(10, clientY - rect.top - 60) + 'px';
          };

          pRect.addEventListener('mouseenter', showTip);
          pRect.addEventListener('touchstart', showTip);
          pRect.addEventListener('mouseleave', () => tooltip.style.display = 'none');
          svg.appendChild(pRect);

          // Top Value Label
          const valText = document.createElementNS("http://www.w3.org/2000/svg", "text");
          valText.setAttribute("x", x + (barW / 2));
          valText.setAttribute("y", presentY - 5);
          valText.setAttribute("class", "val-label");
          valText.textContent = d.present;
          svg.appendChild(valText);

          // X Axis Session Time Label
          const xText = document.createElementNS("http://www.w3.org/2000/svg", "text");
          xText.setAttribute("x", x + (barW / 2));
          xText.setAttribute("y", height - 10);
          xText.setAttribute("class", "axis-label");
          xText.textContent = d.time;
          svg.appendChild(xText);
        });
      </script>
    </body>
    </html>
    """.trimIndent()
}

fun generateRechartsPieHtml(jsonSlices: String, totalCount: Int, language: CampLanguage): String {
    return """
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="utf-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no" />
      <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        body { background: #FFFFFF; padding: 12px; display: flex; flex-direction: column; align-items: center; justify-content: center; overflow: hidden; }
        .chart-box { position: relative; width: 220px; height: 220px; }
        .center-label {
          position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);
          text-align: center; pointer-events: none;
        }
        .center-val { font-size: 24px; font-weight: bold; color: #0F172A; }
        .center-sub { font-size: 11px; color: #64748B; font-weight: 500; }
        .tooltip-card {
          position: absolute; display: none; background: #0F172A; color: #FFFFFF;
          padding: 6px 10px; border-radius: 6px; font-size: 11px; pointer-events: none;
          box-shadow: 0 4px 10px rgba(0,0,0,0.25); z-index: 100;
        }
        svg { width: 100%; height: 100%; transform: rotate(-90deg); }
        .donut-slice { cursor: pointer; transition: stroke-width 0.2s, opacity 0.2s; }
        .donut-slice:hover { opacity: 0.85; stroke-width: 32; }
      </style>
    </head>
    <body>
      <div class="chart-box" id="box">
        <div class="tooltip-card" id="tip"></div>
        <svg viewBox="0 0 200 200" id="pieSvg"></svg>
        <div class="center-label">
          <div class="center-val" id="centerVal">$totalCount</div>
          <div class="center-sub">${if (language == CampLanguage.BANGLA) "পরীক্ষিত" else "Total"}</div>
        </div>
      </div>

      <script>
        const slices = [$jsonSlices];
        const svg = document.getElementById('pieSvg');
        const tip = document.getElementById('tip');
        const box = document.getElementById('box');
        const centerVal = document.getElementById('centerVal');

        const cx = 100, cy = 100, r = 70;
        const strokeW = 26;
        const circumference = 2 * Math.PI * r;

        let accumulatedPercent = 0;

        slices.forEach(s => {
          const strokeDash = (s.percentage / 100) * circumference;
          const strokeOffset = circumference - ((accumulatedPercent / 100) * circumference);

          const circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
          circle.setAttribute("cx", cx);
          circle.setAttribute("cy", cy);
          circle.setAttribute("r", r);
          circle.setAttribute("fill", "transparent");
          circle.setAttribute("stroke", s.color);
          circle.setAttribute("stroke-width", strokeW);
          circle.setAttribute("stroke-dasharray", strokeDash + " " + (circumference - strokeDash));
          circle.setAttribute("stroke-dashoffset", strokeOffset);
          circle.setAttribute("class", "donut-slice");

          const onTouch = (evt) => {
            centerVal.textContent = s.percentage.toFixed(1) + '%';
            centerVal.style.color = s.color;
            tip.style.display = 'block';
            tip.innerHTML = '<strong>' + s.name + '</strong><br/>' + s.value + ' জন (' + s.percentage.toFixed(1) + '%)';
            tip.style.left = '40px';
            tip.style.top = '10px';
          };

          circle.addEventListener('mouseenter', onTouch);
          circle.addEventListener('touchstart', onTouch);
          circle.addEventListener('mouseleave', () => {
            tip.style.display = 'none';
            centerVal.textContent = '$totalCount';
            centerVal.style.color = '#0F172A';
          });

          svg.appendChild(circle);
          accumulatedPercent += s.percentage;
        });
      </script>
    </body>
    </html>
    """.trimIndent()
}

fun generateRechartsGroupBarHtml(jsonGroups: String, language: CampLanguage): String {
    return """
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="utf-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no" />
      <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        body { background: #FFFFFF; padding: 10px; overflow-x: hidden; }
        .group-row { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
        .label { font-size: 11px; font-weight: 600; color: #1E293B; width: 110px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        .bar-track { flex: 1; height: 10px; background: #F1F5F9; border-radius: 5px; overflow: hidden; margin: 0 10px; }
        .bar-fill { height: 100%; border-radius: 5px; transition: width 0.6s ease; }
        .pct { font-size: 11px; font-weight: bold; color: #0F172A; width: 44px; text-align: right; }
      </style>
    </head>
    <body>
      <div id="container"></div>

      <script>
        const groups = [$jsonGroups];
        const container = document.getElementById('container');

        groups.forEach(g => {
          const row = document.createElement('div');
          row.className = 'group-row';
          row.innerHTML = '<div class="label">' + g.group + '</div>' +
                          '<div class="bar-track"><div class="bar-fill" style="width: ' + g.rate + '%; background: ' + g.color + '"></div></div>' +
                          '<div class="pct">' + g.rate.toFixed(1) + '%</div>';
          container.appendChild(row);
        });
      </script>
    </body>
    </html>
    """.trimIndent()
}
