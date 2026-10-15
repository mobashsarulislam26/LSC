package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.ui.screens.participants.ParticipantHealthStatusCard
import com.example.ui.screens.participants.QuickHealthLogDialog
import com.example.ui.screens.participants.ParticipantQrPassCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ParticipantDetailDialog(
    participant: ParticipantEntity,
    language: CampLanguage,
    onDismiss: () -> Unit,
    onViewIdCard: () -> Unit,
    onGenerateCertificate: () -> Unit,
    onEditParticipant: (() -> Unit)? = null,
    healthStatus: ParticipantHealthStatusEntity? = null,
    medicalRecords: List<HealthRecordEntity> = emptyList(),
    onRecordHealthCheckIn: ((status: String, notes: String) -> Unit)? = null
) {
    var showQuickLogDialog by remember { mutableStateOf(false) }
    var showFullscreenQr by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("participant_detail_dialog")
        ) {
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.92f)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                // Header
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
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(LscEmeraldLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = participant.nameBn.firstOrNull()?.toString() ?: "P",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = LscEmeraldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = participant.nameBn,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = LscSlateDark
                            )
                            Text(
                                text = participant.nameEn,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
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
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
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
                        border = androidx.compose.foundation.BorderStroke(1.dp, healthColor.copy(alpha = 0.35f)),
                        modifier = Modifier.testTag("health_indicator_detail_${participant.campCode}")
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
                                (if (language == CampLanguage.BANGLA) "✓ চেক-ইন" else "✓ Checked In")
                            else
                                (if (language == CampLanguage.BANGLA) "পেন্ডিং" else "Pending"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (participant.isCheckedIn) Color(0xFF059669) else Color(0xFFD97706),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewIdCard,
                        modifier = Modifier.weight(1f).testTag("view_id_from_detail_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == CampLanguage.BANGLA) "আইডি" else "ID", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onGenerateCertificate,
                        colors = ButtonDefaults.buttonColors(containerColor = LscGoldSecondary),
                        modifier = Modifier.weight(1.2f).testTag("gen_cert_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == CampLanguage.BANGLA) "সার্টিফিকেট" else "Certificate", fontSize = 12.sp)
                    }

                    if (onEditParticipant != null) {
                        FilledTonalButton(
                            onClick = onEditParticipant,
                            modifier = Modifier.weight(1f).testTag("edit_participant_from_detail_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (language == CampLanguage.BANGLA) "এডিট" else "Edit", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Key Stats Row (Age, Gender, Attendance, Score)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (language == CampLanguage.BANGLA) "বয়স / লিঙ্গ" else "Age / Gender",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "${participant.age}y • ${participant.gender}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LscSlateDark
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (language == CampLanguage.BANGLA) "উপস্থিতি হার" else "Attendance",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "${"%.0f".format(participant.attendancePercentage)}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (participant.attendancePercentage >= 90f) Color(0xFF059669) else Color(0xFFDC2626)
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (language == CampLanguage.BANGLA) "স্কোর" else "Score",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "${"%.1f".format(participant.totalScore)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LscEmeraldPrimary
                            )
                        }
                    }
                }

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

                // Log of Recent Medical Entries
                ParticipantMedicalRecordsLogCard(
                    medicalRecords = medicalRecords,
                    language = language
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Accommodation & Identification Card
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "আবাসন ও নিবন্ধন বিবরণ" else "Accommodation & Identity",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = LscEmeraldPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (language == CampLanguage.BANGLA) "ফাইল নং:" else "File No:", fontSize = 12.sp, color = Color.Gray)
                            Text(text = participant.fileNo, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (language == CampLanguage.BANGLA) "জেলা:" else "District:", fontSize = 12.sp, color = Color.Gray)
                            Text(text = "${participant.districtBn} (${participant.districtEn})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (language == CampLanguage.BANGLA) "রুম ও বেড:" else "Room & Bed:", fontSize = 12.sp, color = Color.Gray)
                            Text(text = "${participant.roomNo} | ${participant.bedNo}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Guardian & Health Card
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "অভিভাবক ও স্বাস্থ্য কল্যাণ" else "Guardian & Health Details",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (language == CampLanguage.BANGLA) "অভিভাবক:" else "Guardian:", fontSize = 12.sp, color = Color.Gray)
                            Text(text = participant.guardianName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (language == CampLanguage.BANGLA) "যোগাযোগ ফোন:" else "Phone:", fontSize = 12.sp, color = Color.Gray)
                            Text(text = participant.guardianPhone, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1D4ED8))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (language == CampLanguage.BANGLA) "খাবার পছন্দ:" else "Meal Preference:", fontSize = 12.sp, color = Color.Gray)
                            Text(text = participant.mealPref, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        if (participant.medicalFlag != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = Color(0xFFFEF2F2),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${if (language == CampLanguage.BANGLA) "চিকিৎসা সতর্কতা: " else "Medical Note: "}${participant.medicalFlag}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFDC2626)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Camp Timeline (as required by PRD 5.1)
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

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
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

                Spacer(modifier = Modifier.height(72.dp))
            }

            // Floating Action Button for recording daily health check-in
            ExtendedFloatingActionButton(
                    onClick = { showQuickLogDialog = true },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = "Daily Health Check-in",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    text = {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "স্বাস্থ্য চেক-ইন" else "Daily Health Log",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    },
                    containerColor = LscEmeraldPrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .testTag("quick_health_checkin_fab")
                        .testTag("fab_daily_health_checkin")
                )
            }
        }
    }

    if (showQuickLogDialog) {
        QuickHealthLogDialog(
            participant = participant,
            currentHealthStatus = healthStatus,
            language = language,
            onDismiss = { showQuickLogDialog = false },
            onConfirm = { status, notes ->
                onRecordHealthCheckIn?.invoke(status, notes)
                showQuickLogDialog = false
            }
        )
    }

    if (showFullscreenQr) {
        ParticipantQrDialog(
            participant = participant,
            language = language,
            onDismiss = { showFullscreenQr = false }
        )
    }
}

@Composable
fun CertificateDialog(
    participant: ParticipantEntity,
    language: CampLanguage,
    onDismiss: () -> Unit
) {
    CertificateGeneratorDialog(
        participant = participant,
        isBangla = language == CampLanguage.BANGLA,
        onDismiss = onDismiss
    )
}

@Composable
fun ExcelImportDialog(
    language: CampLanguage,
    onDismiss: () -> Unit,
    onConfirmImport: (csvData: String) -> Unit
) {
    var rawCsv by remember {
        mutableStateOf(
            """Camp ID,File No,Name,District,Group,Sub-group
LSC-2026-604,F-1604,মুহাম্মদ রাফি চৌধুরী,চট্টগ্রাম,বুখারী,Bukhari-2
LSC-2026-605,F-1605,সাব্বির আহমেদ,ঢাকা,সালাহউদ্দীন আইয়ুবী,Salahuddin-1
LSC-2026-606,F-1606,ফাতিহা বিনতে জামান,সিলেট,সুমাইয়া,Sumaiya-3"""
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("excel_import_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, tint = LscEmeraldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "এক্সেল / সিএসভি ইমপোর্ট উইজার্ড" else "Excel/CSV Import Wizard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (language == CampLanguage.BANGLA)
                        "ম্যাপড কলাম: Camp ID, File No, Name, District, Group, Sub-group। নিচের প্রিভিউ ডেটা সরাসরি ইমপোর্ট করতে পারবেন:"
                    else
                        "Mapped Columns: Camp ID, File No, Name, District, Group, Sub-group. Verify and confirm import:",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = rawCsv,
                    onValueChange = { rawCsv = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .testTag("csv_data_input"),
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        onConfirmImport(rawCsv)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("confirm_import_btn")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (language == CampLanguage.BANGLA) "ডেটাবেজে ইমপোর্ট নিশ্চিত করুন" else "Confirm Import to DB")
                }
            }
        }
    }
}

/**
 * Log of Recent Medical Entries Card
 * Displays chronological health and clinical records with diagnosis, vitals,
 * medications, priority badges, and attending doctor.
 */
@Composable
fun ParticipantMedicalRecordsLogCard(
    medicalRecords: List<HealthRecordEntity>,
    language: CampLanguage,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("participant_medical_entries_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == CampLanguage.BANGLA)
                            "সাম্প্রতিক মেডিকেল ও স্বাস্থ্য লগ"
                        else
                            "Recent Medical Entries Log",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = LscSlateDark
                    )
                }

                Surface(
                    color = if (medicalRecords.any { it.priority == "HIGH" || it.isEmergencyFlag }) Color(0xFFFEF2F2) else Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${medicalRecords.size} ${if (language == CampLanguage.BANGLA) "এন্ট্রি" else "entries"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (medicalRecords.any { it.priority == "HIGH" || it.isEmergencyFlag }) Color(0xFFDC2626) else LscSlateDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (medicalRecords.isEmpty()) {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "কোনো পূর্ববর্তী জরুরি মেডিকেল লগ পাওয়া যায়নি। ক্যাম্প মেডিকেল টিমের সাথে নিয়মিত পর্যবেক্ষণ সক্রিয় রয়েছে।"
                            else
                                "No prior medical issues or emergency logs recorded. Routine health monitoring is active.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.testTag("medical_entries_log")
                ) {
                    medicalRecords.sortedByDescending { it.createdAt }.forEach { record ->
                        val isHighPriority = record.priority == "HIGH" || record.isEmergencyFlag
                        Surface(
                            color = if (isHighPriority) Color(0xFFFEF2F2) else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isHighPriority) Color(0xFFFCA5A5) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("medical_record_item_${record.id}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            color = when (record.checkupType.uppercase()) {
                                                "EMERGENCY" -> Color(0xFFDC2626)
                                                "DENTAL" -> Color(0xFF0284C7)
                                                "EYE" -> Color(0xFF7C3AED)
                                                else -> Color(0xFF059669)
                                            },
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = record.checkupType.uppercase(),
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }

                                        if (isHighPriority) {
                                            Surface(
                                                color = Color(0xFFDC2626),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "HIGH PRIORITY",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Surface(
                                            color = when (record.status.uppercase()) {
                                                "RESOLVED" -> Color(0xFFD1FAE5)
                                                "ATTENDED" -> Color(0xFFFEF3C7)
                                                else -> Color(0xFFFEE2E2)
                                            },
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = record.status.uppercase(),
                                                color = when (record.status.uppercase()) {
                                                    "RESOLVED" -> Color(0xFF065F46)
                                                    "ATTENDED" -> Color(0xFF92400E)
                                                    else -> Color(0xFF991B1B)
                                                },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = dateFormat.format(Date(record.createdAt)),
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = record.diagnosis,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LscSlateDark
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${if (language == CampLanguage.BANGLA) "চিকিৎসক: " else "Doctor: "}${record.doctorName}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                    if (record.vitals.isNotBlank()) {
                                        Surface(
                                            color = Color.White,
                                            shape = RoundedCornerShape(4.dp),
                                            border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFFCBD5E1))
                                        ) {
                                            Text(
                                                text = record.vitals,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = Color(0xFF334155),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                if (record.adviceAndMeds.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Medication,
                                                contentDescription = null,
                                                tint = Color(0xFF0284C7),
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = record.adviceAndMeds,
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
        }
    }
}

