package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.screens.competitions.MilestoneParticipantPickerDialog
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Quick Alert presets for counselors to fire immediate urgent notifications in 1 tap.
 */
data class QuickAlertPreset(
    val titleBn: String,
    val titleEn: String,
    val category: String, // "HEALTH", "SAFETY", "MISSING_PERSON", "INJURY"
    val defaultSeverity: String, // "CRITICAL", "HIGH", "MEDIUM"
    val defaultDescBn: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

val QUICK_ALERT_PRESETS = listOf(
    QuickAlertPreset(
        titleBn = "তীব্র শ্বাসকষ্ট / ইনহেলার ও অক্সিজেন প্রয়োজন",
        titleEn = "Severe Asthma / Inhaler Needed Urgently",
        category = "HEALTH",
        defaultSeverity = "CRITICAL",
        defaultDescBn = "অংশগ্রহণকারীর হঠাৎ তীব্র শ্বাসকষ্ট শুরু হয়েছে, অবিলম্বে মেডিকেল টিম ও ইনহেলার নিয়ে আসুন।",
        icon = Icons.Default.MedicalServices
    ),
    QuickAlertPreset(
        titleBn = "অংশগ্রহণকারী নিখোঁজ / সেশন থেকে অনুপস্থিত",
        titleEn = "Camper Missing / Unaccounted from Station",
        category = "MISSING_PERSON",
        defaultSeverity = "CRITICAL",
        defaultDescBn = "স্টেশন রোটেশনের সময় অংশগ্রহণকারীকে পাওয়া যাচ্ছে না। গেট ও সীমানা নিরাপত্তা তল্লাশি প্রয়োজন।",
        icon = Icons.Default.PersonSearch
    ),
    QuickAlertPreset(
        titleBn = "মাঠে শারীরিক আঘাত / স্পোর্টস ফ্র্যাকচার বা মচকে যাওয়া",
        titleEn = "Physical Injury / Sports Fracture",
        category = "INJURY",
        defaultSeverity = "HIGH",
        defaultDescBn = "মাঠে দৌড়ানোর সময় পড়ে গিয়ে পায়ে তীব্র আঘাত পেয়েছে, স্ট্রেচার ও বরফ কিট প্রয়োজন।",
        icon = Icons.Default.Healing
    ),
    QuickAlertPreset(
        titleBn = "মারাত্মক খাবার অ্যালার্জি / অ্যানাফিল্যাক্সিস শঙ্কা",
        titleEn = "Severe Food Allergy Reaction",
        category = "HEALTH",
        defaultSeverity = "CRITICAL",
        defaultDescBn = "খাবার গ্রহণের পর ত্বকে ফুসকুড়ি ও গলা চুলকানি শুরু হয়েছে। এন্টিহিস্টামিন ইনজেকশন প্রয়োজন।",
        icon = Icons.Default.Warning
    ),
    QuickAlertPreset(
        titleBn = "নিরাপত্তা ও শৃঙ্খলা শঙ্কা / অননুমোদিত প্রবেশ",
        titleEn = "Security / Safeguarding Hazard",
        category = "SAFETY",
        defaultSeverity = "HIGH",
        defaultDescBn = "ক্যাম্প সীমানায় অননুমোদিত ব্যক্তি বা নিরাপত্তা শঙ্কা পরিলক্ষিত হয়েছে। নিরাপত্তা টিম পাঠান।",
        icon = Icons.Default.Security
    )
)

/**
 * Counselor Quick Alert Composer Modal.
 * Enables immediate dispatch of urgent push notifications to Camp Administration.
 */
@Composable
fun QuickAlertDialog(
    language: CampLanguage,
    currentRole: UserRole,
    participants: List<ParticipantEntity>,
    stations: List<StationEntity>,
    onDismiss: () -> Unit,
    onSendAlert: (
        title: String,
        category: String,
        severity: String,
        location: String,
        desc: String,
        participant: ParticipantEntity?
    ) -> Unit
) {
    var selectedPreset by remember { mutableStateOf<QuickAlertPreset?>(QUICK_ALERT_PRESETS.first()) }
    var title by remember { mutableStateOf(selectedPreset?.titleBn ?: "") }
    var category by remember { mutableStateOf(selectedPreset?.category ?: "HEALTH") }
    var severity by remember { mutableStateOf(selectedPreset?.defaultSeverity ?: "CRITICAL") }
    var location by remember { mutableStateOf("স্টেশন ১: অডিটোরিয়াম উইং এ") }
    var description by remember { mutableStateOf(selectedPreset?.defaultDescBn ?: "") }
    var selectedCamper by remember { mutableStateOf<ParticipantEntity?>(null) }
    var showCamperPicker by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("quick_alert_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDC2626)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationImportant,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "জরুরি পুশ অ্যালার্ট" else "Urgent Push Alert",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF991B1B)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "LIVE SOS",
                                        color = Color(0xFFDC2626),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "কাউন্সেলর থেকে ক্যাম্প প্রশাসনে তাৎক্ষণিক সতর্কবার্তা"
                                else
                                    "Counselor immediate broadcast to Admin Dashboard",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_quick_alert_btn")) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1-Tap Urgent Presets Row
                    item {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "১-ক্লিক দ্রুত প্রিসেট (Quick Presets):" else "1-Tap Urgent Presets:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LscSlateDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            QUICK_ALERT_PRESETS.forEach { preset ->
                                val isSelected = selectedPreset == preset
                                Surface(
                                    color = if (isSelected) Color(0xFFFEF2F2) else Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFFDC2626) else Color(0xFFE2E8F0)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedPreset = preset
                                            title = preset.titleBn
                                            category = preset.category
                                            severity = preset.defaultSeverity
                                            description = preset.defaultDescBn
                                        }
                                        .testTag("preset_${preset.category}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = preset.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color(0xFFDC2626) else Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (language == CampLanguage.BANGLA) preset.titleBn else preset.titleEn,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color(0xFF991B1B) else LscSlateDark
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFFDC2626),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Severity Selector Chips
                    item {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "জরুরি মাত্রা (Severity):" else "Urgency Level:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LscSlateDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Triple("CRITICAL", "অতি জরুরি (Critical)", Color(0xFFDC2626)),
                                Triple("HIGH", "উচ্চ ঝুঁকি (High)", Color(0xFFEA580C)),
                                Triple("MEDIUM", "সাধারণ জরুরি (Medium)", Color(0xFFD97706))
                            ).forEach { (sevKey, sevLabel, sevColor) ->
                                val isChosen = severity == sevKey
                                Surface(
                                    color = if (isChosen) sevColor else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (isChosen) sevColor else Color(0xFFE2E8F0)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { severity = sevKey }
                                        .testTag("severity_${sevKey.lowercase()}")
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = sevLabel,
                                            color = if (isChosen) Color.White else Color.DarkGray,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Alert Title & Location Inputs
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text(if (language == CampLanguage.BANGLA) "সতর্কতার শিরোনাম *" else "Alert Title *", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("alert_title_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text(if (language == CampLanguage.BANGLA) "ঘটনাস্থল / ভেন্যু *" else "Incident Location *", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.fillMaxWidth().testTag("alert_location_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Link Camper (Optional)
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCamperPicker = true }
                                .testTag("link_camper_selector")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    val camper = selectedCamper
                                    Column {
                                        Text(
                                            text = if (camper != null)
                                                "${camper.nameBn} [${camper.campCode}]"
                                            else
                                                if (language == CampLanguage.BANGLA) "অংশগ্রহণকারী যুক্ত করুন (ঐচ্ছিক)" else "Link Camper (Optional)",
                                            fontSize = 12.sp,
                                            fontWeight = if (camper != null) FontWeight.Bold else FontWeight.Normal,
                                            color = if (camper != null) LscSlateDark else Color.Gray
                                        )
                                        if (camper != null) {
                                            Text(
                                                text = "${camper.groupNameBn} • ${camper.subgroup} • রুম: ${camper.roomNo}",
                                                fontSize = 10.sp,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                }

                                if (selectedCamper != null) {
                                    IconButton(
                                        onClick = { selectedCamper = null },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    }
                                } else {
                                    Text(
                                        text = if (language == CampLanguage.BANGLA) "বাছাই করুন" else "Select",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1D4ED8)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text(if (language == CampLanguage.BANGLA) "বিস্তারিত বিবরণ ও পরিস্থিতি *" else "Details & Situation *", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("alert_desc_input"),
                            minLines = 3,
                            maxLines = 5
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Send Alert Button
                Button(
                    onClick = {
                        if (title.isNotBlank() && location.isNotBlank()) {
                            onSendAlert(title, category, severity, location, description, selectedCamper)
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("send_push_alert_btn")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == CampLanguage.BANGLA)
                            "প্রশাসনে তাৎক্ষণিক পুশ অ্যালার্ট পাঠান"
                        else
                            "Send Immediate Push Alert to Admin",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showCamperPicker) {
        MilestoneParticipantPickerDialog(
            language = language,
            participants = participants,
            currentSelected = selectedCamper ?: participants.firstOrNull() ?: ParticipantEntity(campCode = "", fileNo = "", nameBn = "", nameEn = "", districtBn = "", districtEn = "", gender = "", age = 0, groupId = "", groupNameBn = "", groupNameEn = "", subgroup = "", roomNo = "", bedNo = "", mealPref = "", guardianName = "", guardianPhone = ""),
            onDismiss = { showCamperPicker = false },
            onSelect = { p ->
                selectedCamper = p
                showCamperPicker = false
            }
        )
    }
}

/**
 * High-priority Floating In-App Push Notification Banner.
 * Appears immediately when a counselor dispatches an urgent alert.
 */
@Composable
fun InAppPushNotificationBanner(
    alert: IncidentEntity,
    language: CampLanguage,
    onViewOnDashboard: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D)),
        border = BorderStroke(1.5.dp, Color(0xFFF87171)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("in_app_push_banner")
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
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🚨 জরুরি পুশ নোটিফিকেশন (EMERGENCY PUSH)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp).testTag("dismiss_push_banner_btn")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = alert.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "স্থান: ${alert.location} • প্রেরক: ${alert.reporter}",
                fontSize = 11.sp,
                color = Color(0xFFFECACA)
            )

            if (!alert.participantName.isNullOrBlank()) {
                Text(
                    text = "সংশ্লিষ্ট অংশগ্রহণকারী: ${alert.participantName} [${alert.participantCampCode ?: ""}]",
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(if (language == CampLanguage.BANGLA) "পরে দেখুন" else "Dismiss", color = Color(0xFFFCA5A5), fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(
                    onClick = {
                        onViewOnDashboard()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("push_banner_view_dashboard_btn")
                ) {
                    Text(
                        text = if (language == CampLanguage.BANGLA) "ড্যাশবোর্ডে দেখুন" else "View Alert",
                        color = Color(0xFF991B1B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

/**
 * Administration Dashboard Live Alert Hub.
 * Shows all active, unacknowledged urgent alerts right at the top of the Admin Dashboard.
 */
@Composable
fun DashboardAlertHub(
    language: CampLanguage,
    activeAlerts: List<IncidentEntity>,
    onOpenSendAlertDialog: () -> Unit,
    onAcknowledgeAlert: (Long) -> Unit,
    onResolveAlert: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
    pendingHealthAlerts: List<HealthRecordEntity> = emptyList(),
    onAttendHealthAlert: (Long) -> Unit = {},
    onResolveHealthAlert: (Long, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    if (activeAlerts.isEmpty() && pendingHealthAlerts.isEmpty()) {
        // Quiet state: counselor SOS button bar
        Surface(
            color = Color(0xFFFEF2F2),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFFECACA)),
            modifier = modifier.fillMaxWidth().testTag("dashboard_quiet_alert_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDC2626)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Emergency, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "কাউন্সেলর কুইক অ্যালার্ট" else "Counselor Quick Alert",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF991B1B)
                        )
                        Text(
                            text = if (language == CampLanguage.BANGLA) "জরুরি স্বাস্থ্য বা সুরক্ষায় ১-ক্লিকে নোটিফিকেশন" else "Instant push notification for health/safety",
                            fontSize = 10.sp,
                            color = Color(0xFFB91C1C)
                        )
                    }
                }

                FilledTonalButton(
                    onClick = onOpenSendAlertDialog,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFDC2626), contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("dashboard_trigger_quick_alert_btn")
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (language == CampLanguage.BANGLA) "অ্যালার্ট পাঠান" else "SOS Alert",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    } else {
        // Active Urgent Alert Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
            border = BorderStroke(1.5.dp, Color(0xFFDC2626)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = modifier
                .fillMaxWidth()
                .testTag("dashboard_quick_alerts_banner")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFDC2626),
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA)
                                        "🚨 সক্রিয় জরুরি ও স্বাস্থ্য সতর্কতা (${activeAlerts.size + pendingHealthAlerts.size} টি)"
                                    else
                                        "🚨 Active Urgent & Health Alerts (${activeAlerts.size + pendingHealthAlerts.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF991B1B)
                                )
                            }
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "কাউন্সেলরদের পাঠানো তাৎক্ষণিক স্বাস্থ্য ও সুরক্ষা সতর্কতা"
                                else
                                    "Urgent alerts dispatched by counselors requiring action",
                                fontSize = 11.sp,
                                color = Color(0xFFB91C1C)
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = onOpenSendAlertDialog,
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF991B1B), contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("add_another_alert_btn")
                    ) {
                        Icon(Icons.Default.AddAlert, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ অ্যালার্ট", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val totalAlertCount = activeAlerts.size + pendingHealthAlerts.size
                // Alert Cards List
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Pending High-Priority Health Log Alerts
                    pendingHealthAlerts.forEach { healthAlert ->
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.5.dp, Color(0xFFDC2626)),
                            modifier = Modifier.fillMaxWidth().testTag("pending_health_log_item_${healthAlert.id}")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = Color(0xFFDC2626),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "HIGH PRIORITY HEALTH",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = if (healthAlert.status == "ATTENDED") Color(0xFFFEF3C7) else Color(0xFFFEE2E2),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (healthAlert.status == "ATTENDED") "মেডিকেল টিম উপস্থিত (ATTENDED)" else "পেন্ডিং স্বাস্থ্য এলার্ট (PENDING)",
                                                color = if (healthAlert.status == "ATTENDED") Color(0xFFB45309) else Color(0xFFDC2626),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = timeFormat.format(Date(healthAlert.createdAt)),
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "স্বাস্থ্য পর্যবেক্ষণ: ${healthAlert.diagnosis}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = LscSlateDark
                                )

                                Text(
                                    text = "ক্যাম্পার: ${healthAlert.participantName} [${healthAlert.campCode}] • ${healthAlert.groupName}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF7F1D1D),
                                    fontWeight = FontWeight.SemiBold
                                )

                                if (healthAlert.vitals.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "ভাইটালস: ${healthAlert.vitals} • ধরন: ${healthAlert.checkupType}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF1E3A8A)
                                    )
                                }

                                if (healthAlert.adviceAndMeds.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "পরামর্শ/ঔষধ: ${healthAlert.adviceAndMeds}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF0F766E),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (healthAlert.status == "PENDING") {
                                        Button(
                                            onClick = { onAttendHealthAlert(healthAlert.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.weight(1f).testTag("attend_health_alert_${healthAlert.id}")
                                        ) {
                                            Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (language == CampLanguage.BANGLA) "অ্যাটেন্ড করুন" else "Attend", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Button(
                                        onClick = { onResolveHealthAlert(healthAlert.id, "ক্যাম্প ক্লিনিক কর্তৃক সেবা সম্পন্ন ও শিক্ষার্থী সুস্থ।") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.weight(1f).testTag("resolve_health_alert_${healthAlert.id}")
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (language == CampLanguage.BANGLA) "সমাধান সম্পন্ন" else "Resolve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    activeAlerts.forEach { alert ->
                        val isAcknowledged = alert.status == "IN_PROGRESS"
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (isAcknowledged) Color(0xFFFDE68A) else Color(0xFFFCA5A5)),
                            modifier = Modifier.fillMaxWidth().testTag("active_alert_card_${alert.id}")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = when (alert.severity) {
                                                "CRITICAL" -> Color(0xFFDC2626)
                                                "HIGH" -> Color(0xFFEA580C)
                                                else -> Color(0xFFD97706)
                                            },
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = alert.severity,
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        Surface(
                                            color = if (isAcknowledged) Color(0xFFFEF3C7) else Color(0xFFFEE2E2),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (isAcknowledged) "গৃহীত (In Progress)" else "নতুন সতর্কতা (OPEN)",
                                                color = if (isAcknowledged) Color(0xFFB45309) else Color(0xFFDC2626),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = timeFormat.format(Date(alert.timestamp)),
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = alert.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = LscSlateDark
                                )

                                Text(
                                    text = "ঘটনাস্থল: ${alert.location} • প্রেরক: ${alert.reporter}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF1E3A8A),
                                    fontWeight = FontWeight.Medium
                                )

                                if (!alert.participantName.isNullOrBlank()) {
                                    Text(
                                        text = "ভুক্তভোগী camper: ${alert.participantName} [${alert.participantCampCode ?: ""}]",
                                        fontSize = 11.sp,
                                        color = Color(0xFF7F1D1D),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                if (alert.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = alert.description,
                                        fontSize = 11.sp,
                                        color = Color(0xFF334155),
                                        lineHeight = 15.sp
                                    )
                                }

                                if (alert.actionTaken.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "পদক্ষেপ: ${alert.actionTaken}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF0F766E),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Quick Action Buttons: Acknowledge vs Resolve vs Direct Call
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (!isAcknowledged) {
                                        Button(
                                            onClick = { onAcknowledgeAlert(alert.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.weight(1f).testTag("ack_alert_btn_${alert.id}")
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("রিসিভ করেছি", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Button(
                                        onClick = { onResolveAlert(alert.id, "ক্যাম্প প্রশাসন কর্তৃক সমাধান সম্পন্ন হয়েছে") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.weight(1f).testTag("resolve_alert_btn_${alert.id}")
                                    ) {
                                        Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("সমাধান সম্পন্ন", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:01819-222333"))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                // ignore in testing
                                            }
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.testTag("call_medical_btn")
                                    ) {
                                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("মেডিকেল কল", fontSize = 10.sp, color = Color(0xFFDC2626))
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
