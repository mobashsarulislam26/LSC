package com.example.ui.screens.participants

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CampLanguage
import com.example.data.model.HealthRecordEntity
import com.example.data.model.ParticipantEntity
import com.example.data.model.ParticipantHealthStatusEntity
import com.example.ui.components.QrCodeCanvas
import com.example.ui.components.ParticipantQrDialog
import com.example.ui.components.ParticipantMedicalRecordsLogCard
import com.example.ui.theme.*

/**
 * Full Participant Detail Screen featuring:
 * - Comprehensive profile details, accommodation, scores, and timeline
 * - Dedicated Daily Health Status & Check-In card
 * - Floating Action Button for recording daily health check-in logs
 * - Quick-log Dialog with presets ('Feeling well', 'Needs rest', etc.)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantDetailScreen(
    participant: ParticipantEntity,
    healthStatus: ParticipantHealthStatusEntity?,
    language: CampLanguage,
    onBack: () -> Unit,
    onViewIdCard: () -> Unit,
    onGenerateCertificate: () -> Unit,
    onEditParticipant: (() -> Unit)? = null,
    medicalRecords: List<HealthRecordEntity> = emptyList(),
    onRecordHealthCheckIn: (status: String, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showQuickLogDialog by remember { mutableStateOf(false) }
    var showFullscreenQr by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("participant_detail_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "অংশগ্রহণকারী প্রোফাইল" else "Participant Profile",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                        Text(
                            text = "${participant.campCode} • ${participant.nameBn}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detail_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LscSlateDark
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detail_close_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showQuickLogDialog = true },
                icon = {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = "Health Check-in",
                        modifier = Modifier.size(24.dp)
                    )
                },
                text = {
                    Text(
                        text = if (language == CampLanguage.BANGLA) "দৈনিক স্বাস্থ্য চেক-ইন" else "Daily Health Log",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                },
                containerColor = LscEmeraldPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .testTag("quick_health_checkin_fab")
                    .testTag("fab_daily_health_checkin")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Participant Header Card
            ParticipantHeaderCard(
                participant = participant,
                healthStatus = healthStatus,
                language = language,
                onViewIdCard = onViewIdCard,
                onGenerateCertificate = onGenerateCertificate,
                onEditParticipant = onEditParticipant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Dedicated Participant QR Pass Card
            ParticipantQrPassCard(
                participant = participant,
                language = language,
                onOpenFullscreenQr = { showFullscreenQr = true }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Dedicated Daily Health Status & Check-In Card
            ParticipantHealthStatusCard(
                participant = participant,
                healthStatus = healthStatus,
                language = language,
                onOpenQuickLog = { showQuickLogDialog = true }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Recent Medical Entries Log Card
            ParticipantMedicalRecordsLogCard(
                medicalRecords = medicalRecords,
                language = language
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Accommodation & Registration Details Card
            AccommodationIdentityCard(
                participant = participant,
                language = language
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Guardian Details Card
            GuardianContactCard(
                participant = participant,
                language = language
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Camp Journey Timeline
            ParticipantTimelineCard(
                participant = participant,
                language = language
            )

            // Bottom spacing to avoid FAB overlaying content
            Spacer(modifier = Modifier.height(88.dp))
        }
    }

    // Quick Health Log Dialog
    if (showQuickLogDialog) {
        QuickHealthLogDialog(
            participant = participant,
            currentHealthStatus = healthStatus,
            language = language,
            onDismiss = { showQuickLogDialog = false },
            onConfirm = { status, notes ->
                onRecordHealthCheckIn(status, notes)
                showQuickLogDialog = false
            }
        )
    }

    // Fullscreen Participant QR Pass Dialog
    if (showFullscreenQr) {
        ParticipantQrDialog(
            participant = participant,
            language = language,
            onDismiss = { showFullscreenQr = false }
        )
    }
}

/**
 * Quick-Log Dialog to record daily health check-in status (e.g. 'Feeling well', 'Needs rest').
 */
@Composable
fun QuickHealthLogDialog(
    participant: ParticipantEntity,
    currentHealthStatus: ParticipantHealthStatusEntity?,
    language: CampLanguage,
    onDismiss: () -> Unit,
    onConfirm: (status: String, notes: String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf("Feeling well") }
    var notesText by remember { mutableStateOf("") }

    val statusOptions = listOf(
        HealthStatusPreset(
            id = "Feeling well",
            titleBn = "সুস্থ ও স্বাভাবিক (Feeling well)",
            titleEn = "Feeling well",
            descriptionBn = "কোনো শারীরিক সমস্যা নেই, স্বাভাবিক উদ্যম বিদ্যমান",
            icon = Icons.Default.CheckCircle,
            primaryColor = Color(0xFF059669),
            bgColor = Color(0xFFECFDF5),
            borderColor = Color(0xFFA7F3D0)
        ),
        HealthStatusPreset(
            id = "Needs rest",
            titleBn = "বিশ্রাম প্রয়োজন (Needs rest)",
            titleEn = "Needs rest",
            descriptionBn = "ক্লান্তি, ঘুম কম বা কিছুটা বিশ্রাম নিলে সেরে যাবে",
            icon = Icons.Default.Hotel,
            primaryColor = Color(0xFFD97706),
            bgColor = Color(0xFFFFFBEB),
            borderColor = Color(0xFFFDE68A)
        ),
        HealthStatusPreset(
            id = "Mild symptoms",
            titleBn = "হালকা উপসর্গ (Mild symptoms)",
            titleEn = "Mild symptoms",
            descriptionBn = "মাথাব্যথা, ঠান্ডা বা সামান্য শরীর ব্যথা",
            icon = Icons.Default.Healing,
            primaryColor = Color(0xFFEA580C),
            bgColor = Color(0xFFFFF7ED),
            borderColor = Color(0xFFFED7AA)
        ),
        HealthStatusPreset(
            id = "Requires Doctor Check",
            titleBn = "ডাক্তার দেখানো প্রয়োজন (Doctor Check)",
            titleEn = "Requires Doctor Check",
            descriptionBn = "জ্বর, তীব্র অস্বস্তি বা ক্যাম্প মেডিকেল টিম প্রয়োজন",
            icon = Icons.Default.MedicalServices,
            primaryColor = Color(0xFFDC2626),
            bgColor = Color(0xFFFEF2F2),
            borderColor = Color(0xFFFECACA)
        )
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("quick_health_log_dialog")
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFECFDF5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = LscEmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == CampLanguage.BANGLA) "দৈনিক স্বাস্থ্য চেক-ইন" else "Daily Health Check-in",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = LscSlateDark
                            )
                            Text(
                                text = "${participant.nameBn} (${participant.campCode})",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("quick_log_cancel_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (language == CampLanguage.BANGLA) "আজকের শারীরিক অবস্থা নির্বাচন করুন:" else "Select Current Daily Health Status:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LscSlateDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Presets list
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    statusOptions.forEach { preset ->
                        val isSelected = selectedStatus == preset.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) preset.bgColor else Color(0xFFF8FAFC),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) preset.primaryColor else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStatus = preset.id }
                                .testTag(
                                    when (preset.id) {
                                        "Feeling well" -> "status_option_feeling_well"
                                        "Needs rest" -> "status_option_needs_rest"
                                        "Mild symptoms" -> "status_option_mild_symptoms"
                                        else -> "status_option_requires_doctor"
                                    }
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = preset.icon,
                                    contentDescription = null,
                                    tint = preset.primaryColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (language == CampLanguage.BANGLA) preset.titleBn else preset.titleEn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) preset.primaryColor else LscSlateDark
                                    )
                                    Text(
                                        text = preset.descriptionBn,
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.RadioButtonChecked,
                                        contentDescription = "Selected",
                                        tint = preset.primaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Unselected",
                                        tint = Color.LightGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Optional Notes Text Field
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = {
                        Text(
                            if (language == CampLanguage.BANGLA) "অতিরিক্ত পর্যবেক্ষণ / মন্তব্য (ঐচ্ছিক)" else "Notes / Observations (Optional)",
                            fontSize = 12.sp
                        )
                    },
                    placeholder = {
                        Text(
                            if (language == CampLanguage.BANGLA) "যেমন: রাতে ঘুম কম হয়েছে, মাথা ধরেছে বা ওষুধ গ্রহণ করেছে..." else "e.g., Slept late, mild headache, or took prescribed inhaler...",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quick_health_log_notes_input")
                        .testTag("quick_log_notes_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LscEmeraldPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Actions row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_log_dismiss_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (language == CampLanguage.BANGLA) "বাতিল" else "Cancel")
                    }

                    Button(
                        onClick = { onConfirm(selectedStatus, notesText) },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("save_health_log_btn")
                            .testTag("quick_health_log_confirm_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "চেক-ইন সংরক্ষণ" else "Record Check-In",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private data class HealthStatusPreset(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val bgColor: Color,
    val borderColor: Color
)

@Composable
private fun ParticipantHeaderCard(
    participant: ParticipantEntity,
    healthStatus: ParticipantHealthStatusEntity?,
    language: CampLanguage,
    onViewIdCard: () -> Unit,
    onGenerateCertificate: () -> Unit,
    onEditParticipant: (() -> Unit)?
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(LscEmeraldLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = participant.nameBn.firstOrNull()?.toString() ?: "P",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = LscEmeraldPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = participant.nameBn,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = LscSlateDark
                    )
                    if (participant.nameEn.isNotBlank()) {
                        Text(
                            text = participant.nameEn,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Badges row with Color-Coded Health Status Indicator
            val (healthColor, healthLabel) = when (healthStatus?.healthCondition?.lowercase()) {
                "critical" -> Color(0xFFDC2626) to (if (language == CampLanguage.BANGLA) "জরুরি" else "Critical")
                "requires attention" -> Color(0xFFEA580C) to (if (language == CampLanguage.BANGLA) "নজরদারি" else "Attention")
                "needs rest", "mild symptoms" -> Color(0xFFD97706) to (if (language == CampLanguage.BANGLA) "বিশ্রাম" else "Rest")
                else -> if (participant.medicalFlag != null) Color(0xFFEA580C) to (if (language == CampLanguage.BANGLA) "চিকিৎসা নোট" else "Medical Note") else Color(0xFF059669) to (if (language == CampLanguage.BANGLA) "সুস্থ" else "Healthy")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Text(
                        text = participant.campCode,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF1D4ED8),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    color = LscEmeraldLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${participant.groupNameBn} (${participant.subgroup})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LscEmeraldPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                // Color-coded Health Status Indicator
                Surface(
                    color = healthColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, healthColor.copy(alpha = 0.35f)),
                    modifier = Modifier.testTag("health_indicator_header_${participant.campCode}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(healthColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = healthLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = healthColor
                        )
                    }
                }
                Surface(
                    color = if (participant.isCheckedIn) Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (participant.isCheckedIn)
                            (if (language == CampLanguage.BANGLA) "✓ ক্যাম্প চেক-ইন" else "✓ Checked In")
                        else
                            (if (language == CampLanguage.BANGLA) "পেন্ডিং" else "Pending"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (participant.isCheckedIn) Color(0xFF059669) else Color(0xFFD97706),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewIdCard,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("view_id_from_detail_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (language == CampLanguage.BANGLA) "আইডি" else "ID", fontSize = 12.sp)
                }

                Button(
                    onClick = onGenerateCertificate,
                    colors = ButtonDefaults.buttonColors(containerColor = LscGoldSecondary),
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("gen_cert_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (language == CampLanguage.BANGLA) "সার্টিফিকেট" else "Certificate", fontSize = 12.sp)
                }

                if (onEditParticipant != null) {
                    FilledTonalButton(
                        onClick = onEditParticipant,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("edit_participant_from_detail_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == CampLanguage.BANGLA) "এডিট" else "Edit", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Key Stats Row (Age, Attendance, 18-Star Seals, Score)
            val completedSealsCount = remember(participant.completedStationSeals) {
                participant.completedStationSeals.split(',').count { it.isNotBlank() }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatPill(
                    label = if (language == CampLanguage.BANGLA) "বয়স / লিঙ্গ" else "Age / Gender",
                    value = "${participant.age}y • ${participant.gender.firstOrNull() ?: ""}",
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    label = if (language == CampLanguage.BANGLA) "উপস্থিতি" else "Attendance",
                    value = "${"%.0f".format(participant.attendancePercentage)}%",
                    valueColor = if (participant.attendancePercentage >= 90f) Color(0xFF059669) else Color(0xFFDC2626),
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    label = if (language == CampLanguage.BANGLA) "স্টার সিল" else "Seals",
                    value = "★ $completedSealsCount/18",
                    valueColor = Color(0xFFB45309),
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    label = if (language == CampLanguage.BANGLA) "স্কোর" else "Score",
                    value = "${"%.1f".format(participant.totalScore)}",
                    valueColor = LscEmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    valueColor: Color = LscSlateDark,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 10.sp, color = Color.Gray)
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

/**
 * Dedicated Daily Health Status Card showing:
 * - Today's check-in status (Feeling well, Needs rest, etc.)
 * - Inline Quick Log Button
 * - Medication needs
 * - Allergies
 * - Chronological Daily Check-in Logs History
 */
@Composable
fun ParticipantHealthStatusCard(
    participant: ParticipantEntity,
    healthStatus: ParticipantHealthStatusEntity?,
    language: CampLanguage,
    onOpenQuickLog: () -> Unit
) {
    val isCheckedInToday = healthStatus?.isDailyCheckedIn == true
    val condition = healthStatus?.healthCondition ?: "Normal"

    val conditionColor = when {
        condition.contains("rest", ignoreCase = true) || condition.contains("বিশ্রাম", ignoreCase = true) -> Color(0xFFD97706)
        condition.contains("attention", ignoreCase = true) || condition.contains("doctor", ignoreCase = true) -> Color(0xFFDC2626)
        condition.contains("observation", ignoreCase = true) -> Color(0xFFEA580C)
        else -> Color(0xFF059669)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("participant_health_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with title and quick log action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        tint = LscEmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == CampLanguage.BANGLA) "দৈনিক স্বাস্থ্য ও চেক-ইন অবস্থা" else "Daily Health & Wellness",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = LscEmeraldPrimary
                    )
                }

                Button(
                    onClick = onOpenQuickLog,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECFDF5)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("quick_log_inline_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        contentDescription = null,
                        tint = LscEmeraldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (language == CampLanguage.BANGLA) "লগ করুন" else "Quick Log",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LscEmeraldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Today's Check-in Status Banner
            Surface(
                color = if (isCheckedInToday) Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (isCheckedInToday) Color(0xFFA7F3D0) else Color(0xFFFDE68A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isCheckedInToday) Icons.Default.CheckCircle else Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (isCheckedInToday) Color(0xFF059669) else Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isCheckedInToday) {
                                    if (language == CampLanguage.BANGLA) "✓ আজকের স্বাস্থ্য চেক-ইন সম্পন্ন" else "✓ Today's Health Checked In"
                                } else {
                                    if (language == CampLanguage.BANGLA) "আজকের স্বাস্থ্য চেক-ইন বাকি (Pending)" else "Today's Health Check-in Pending"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCheckedInToday) Color(0xFF059669) else Color(0xFFD97706)
                            )
                            if (healthStatus?.lastCheckInDate?.isNotBlank() == true) {
                                Text(
                                    text = "${if (language == CampLanguage.BANGLA) "সর্বশেষ আপডেট: " else "Last Check-in: "}${healthStatus.lastCheckInDate}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    // Health Condition Tag
                    Surface(
                        color = conditionColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = condition,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = conditionColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Medication & Allergies Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Medication Needs Box
                val meds = healthStatus?.medicationNeeds ?: ""
                val hasMeds = healthStatus?.hasMedicationNeeds == true || meds.isNotBlank()
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Medication, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA) "ঔষধের বিবরণ" else "Medications",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0284C7)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (hasMeds) meds else (if (language == CampLanguage.BANGLA) "কোনো নিয়মিত ঔষধ নেই" else "No regular medication"),
                            fontSize = 11.sp,
                            color = if (hasMeds) LscSlateDark else Color.Gray,
                            fontWeight = if (hasMeds) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }

                // Allergies Box
                val allergies = healthStatus?.allergies ?: (participant.medicalFlag ?: "")
                val hasAllergies = healthStatus?.hasAllergies == true || allergies.isNotBlank()
                Surface(
                    color = if (hasAllergies) Color(0xFFFEF2F2) else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (hasAllergies) Color(0xFFFECACA) else Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (hasAllergies) Icons.Default.Warning else Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (hasAllergies) Color(0xFFDC2626) else Color(0xFF059669),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA) "অ্যালার্জি সতর্কতা" else "Allergies",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (hasAllergies) Color(0xFFDC2626) else Color(0xFF059669)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (hasAllergies) allergies else (if (language == CampLanguage.BANGLA) "কোনো জ্ঞাত অ্যালার্জি নেই" else "No known allergies"),
                            fontSize = 11.sp,
                            color = if (hasAllergies) Color(0xFFDC2626) else Color.Gray,
                            fontWeight = if (hasAllergies) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chronological Daily Check-in Logs History
            Text(
                text = if (language == CampLanguage.BANGLA) "দৈনিক চেক-ইন লগ ইতিহাস (Daily Check-in History)" else "Daily Check-in Logs History",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LscSlateDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            val logs = healthStatus?.dailyCheckInLogs?.lines()?.filter { it.isNotBlank() } ?: emptyList()

            if (logs.isEmpty()) {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "এখনো কোনো দৈনিক চেক-ইন লগ নেই। নিচের বাটনে চাপ দিয়ে দ্রুত লগ করুন।"
                            else
                                "No daily check-in logs recorded yet. Tap the button below to quick-log.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.testTag("health_checkin_history_list")
                ) {
                    logs.asReversed().take(6).forEach { logLine ->
                        val isNeedsRest = logLine.contains("rest", ignoreCase = true) || logLine.contains("বিশ্রাম", ignoreCase = true)
                        val isDoctor = logLine.contains("doctor", ignoreCase = true) || logLine.contains("ডাক্তার", ignoreCase = true)
                        val bulletColor = when {
                            isDoctor -> Color(0xFFDC2626)
                            isNeedsRest -> Color(0xFFD97706)
                            else -> Color(0xFF059669)
                        }

                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .padding(top = 4.dp)
                                        .clip(CircleShape)
                                        .background(bulletColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = logLine,
                                    fontSize = 11.sp,
                                    color = LscSlateDark,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccommodationIdentityCard(
    participant: ParticipantEntity,
    language: CampLanguage
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (language == CampLanguage.BANGLA) "আবাসন ও নিবন্ধন বিবরণ" else "Accommodation & Identity",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = LscEmeraldPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = if (language == CampLanguage.BANGLA) "ফাইল নং:" else "File No:", fontSize = 12.sp, color = Color.Gray)
                Text(text = participant.fileNo, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            if (participant.classLevel.isNotBlank() || participant.organization.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = if (language == CampLanguage.BANGLA) "শ্রেণি ও সংস্থা:" else "Class & Org:", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = listOf(participant.classLevel, participant.organization).filter { it.isNotBlank() }.joinToString(" • "),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = if (language == CampLanguage.BANGLA) "জেলা:" else "District:", fontSize = 12.sp, color = Color.Gray)
                Text(text = "${participant.districtBn} (${participant.districtEn})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = if (language == CampLanguage.BANGLA) "রুম ও বেড:" else "Room & Bed:", fontSize = 12.sp, color = Color.Gray)
                Text(text = "${participant.roomNo} | ${participant.bedNo}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun GuardianContactCard(
    participant: ParticipantEntity,
    language: CampLanguage
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (language == CampLanguage.BANGLA) "অভিভাবক ও যোগাযোগ" else "Guardian & Contact Details",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = LscSlateDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = if (language == CampLanguage.BANGLA) "অভিভাবক:" else "Guardian:", fontSize = 12.sp, color = Color.Gray)
                Text(text = participant.guardianName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = if (language == CampLanguage.BANGLA) "যোগাযোগ ফোন:" else "Phone:", fontSize = 12.sp, color = Color.Gray)
                Text(text = participant.guardianPhone, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1D4ED8))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = if (language == CampLanguage.BANGLA) "খাবার পছন্দ:" else "Meal Preference:", fontSize = 12.sp, color = Color.Gray)
                Text(text = participant.mealPref, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ParticipantTimelineCard(
    participant: ParticipantEntity,
    language: CampLanguage
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (language == CampLanguage.BANGLA) "অংশগ্রহণকারী টাইমলাইন (Camp Timeline)" else "Participant Camp Journey",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = LscEmeraldPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            val timelineSteps = listOf(
                "১. প্রাথমিক নিবন্ধন সম্পন্ন (Excel Master File: ${participant.fileNo})" to true,
                "২. ক্যাম্প ভেন্যুতে চেক-ইন সম্পন্ন (রুম: ${participant.roomNo})" to participant.isCheckedIn,
                "৩. দৈনিক উপস্থিতি হার: ${"%.1f".format(participant.attendancePercentage)}% (লক্ষ্যমাত্রা: ≥৯০%)" to (participant.attendancePercentage >= 90f),
                "৪. প্রাথমিক স্বাস্থ্য ও ডেন্টাল স্ক্রিনিং সম্পন্ন" to true,
                "৫. স্টেশন রোটেশন ১-৬ কার্যক্রম চলমান" to true,
                "৬. সামগ্রিক প্রতিযোগিতা স্কোর: ${"%.1f".format(participant.totalScore)} পয়েন্ট" to true,
                "৭. ক্যাম্প সমাপনী সনদ প্রাপ্তির যোগ্য" to (participant.attendancePercentage >= 85f)
            )

            timelineSteps.forEach { (text, isDone) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isDone) Color(0xFF10B981) else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = text,
                        fontSize = 11.sp,
                        color = if (isDone) LscSlateDark else Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * Dedicated QR Code Pass Card displaying participant's scannable QR Code.
 */
@Composable
fun ParticipantQrPassCard(
    participant: ParticipantEntity,
    language: CampLanguage,
    onOpenFullscreenQr: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("participant_qr_code_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = LscEmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == CampLanguage.BANGLA) "অংশগ্রহণকারী কিউআর কোড (QR Pass)" else "Participant QR Code",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = LscEmeraldPrimary
                    )
                }

                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Text(
                        text = participant.campCode,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF1D4ED8),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // High-contrast scannable QR Code Frame
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .clickable { onOpenFullscreenQr() }
                    .testTag("participant_qr_image")
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    QrCodeCanvas(
                        token = participant.campCode,
                        modifier = Modifier.size(120.dp),
                        matrixSize = 25
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (language == CampLanguage.BANGLA)
                    "স্টেশন হাজিরা ও চেক-ইনের জন্য এই কিউআর কোড স্ক্যান করুন"
                else
                    "Scan this QR code for station attendance and check-in verification",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onOpenFullscreenQr,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_fullscreen_qr_btn")
            ) {
                Icon(Icons.Default.Fullscreen, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (language == CampLanguage.BANGLA) "ফুলস্ক্রিন কিউআর কোড দেখুন" else "View Fullscreen QR Pass",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

