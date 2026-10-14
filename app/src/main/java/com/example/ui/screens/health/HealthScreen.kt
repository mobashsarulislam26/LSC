package com.example.ui.screens.health

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun HealthScreen(
    language: CampLanguage,
    currentRole: UserRole,
    healthRecords: List<HealthRecordEntity>,
    participants: List<ParticipantEntity>,
    onSubmitRecord: (participant: ParticipantEntity, type: String, doc: String, vitals: String, diag: String, advice: String, isEmergency: Boolean) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("ALL") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Health Status Metrics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HealthKpi(
                title = if (language == CampLanguage.BANGLA) "মোট স্ক্রিনিং" else "Screened",
                count = "${healthRecords.size}",
                color = LscEmeraldPrimary,
                modifier = Modifier.weight(1f)
            )
            HealthKpi(
                title = if (language == CampLanguage.BANGLA) "ডেন্টাল চেকআপ" else "Dental",
                count = "${healthRecords.count { it.checkupType == "DENTAL" }}",
                color = Color(0xFF0284C7),
                modifier = Modifier.weight(1f)
            )
            HealthKpi(
                title = if (language == CampLanguage.BANGLA) "জরুরি সতর্কতা" else "Urgent Flag",
                count = "${healthRecords.count { it.isEmergencyFlag }}",
                color = Color(0xFFDC2626),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips and Add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("সকল", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LscEmeraldPrimary,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedFilter == "EMERGENCY",
                    onClick = { selectedFilter = "EMERGENCY" },
                    label = { Text("জরুরি ফ্ল্যাগ", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFDC2626),
                        selectedLabelColor = Color.White
                    )
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_health_record_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (language == CampLanguage.BANGLA) "নতুন স্ক্রিনিং" else "New Checkup", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val displayList = if (selectedFilter == "EMERGENCY") {
            healthRecords.filter { it.isEmergencyFlag }
        } else {
            healthRecords
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(displayList) { record ->
                HealthRecordCard(record = record, language = language)
            }
        }
    }

    if (showAddDialog) {
        AddHealthDialog(
            participants = participants.take(20),
            language = language,
            onDismiss = { showAddDialog = false },
            onSubmit = { p, type, doc, vit, diag, adv, isEmer ->
                onSubmitRecord(p, type, doc, vit, diag, adv, isEmer)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun HealthKpi(title: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = count, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = color)
            Text(text = title, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun HealthRecordCard(record: HealthRecordEntity, language: CampLanguage) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (record.isEmergencyFlag) Color(0xFFFEF2F2) else Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (record.isEmergencyFlag) Color(0xFFFCA5A5) else Color(0xFFE2E8F0)
        ),
        modifier = Modifier.fillMaxWidth().testTag("health_card_${record.campCode}")
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (record.isEmergencyFlag) Color(0xFFDC2626) else LscEmeraldLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (record.checkupType) {
                                "DENTAL" -> Icons.Default.Mood
                                "EYE" -> Icons.Default.Visibility
                                else -> Icons.Default.MedicalServices
                            },
                            contentDescription = null,
                            tint = if (record.isEmergencyFlag) Color.White else LscEmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = record.participantName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = LscSlateDark
                        )
                        Text(
                            text = "${record.campCode} • ${record.groupName}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color.Gray
                        )
                    }
                }

                Surface(
                    color = when (record.checkupType) {
                        "DENTAL" -> Color(0xFFE0F2FE)
                        "EYE" -> Color(0xFFF3E8FF)
                        "EMERGENCY" -> Color(0xFFFEE2E2)
                        else -> Color(0xFFDCFCE7)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = record.checkupType,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (record.isEmergencyFlag) Color(0xFFDC2626) else LscEmeraldPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "লক্ষণ / রোগ নির্ণয়: ${record.diagnosis}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = LscSlateDark)
            Text(text = "পরামর্শ ও ঔষধ: ${record.adviceAndMeds}", fontSize = 11.sp, color = Color.DarkGray)
            Text(text = "ভাইটালস: ${record.vitals}", fontSize = 10.sp, color = Color.Gray)
            Text(text = "দায়িত্বরত চিকিৎসক: ${record.doctorName}", fontSize = 10.sp, color = Color.Gray)

            if (record.followUpDate != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "পরবর্তী ফলো-আপ: ${record.followUpDate}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }
        }
    }
}

@Composable
fun AddHealthDialog(
    participants: List<ParticipantEntity>,
    language: CampLanguage,
    onDismiss: () -> Unit,
    onSubmit: (ParticipantEntity, String, String, String, String, String, Boolean) -> Unit
) {
    var selectedParticipant by remember { mutableStateOf(participants.firstOrNull()) }
    var checkupType by remember { mutableStateOf("GENERAL") }
    var doctor by remember { mutableStateOf("ডা. তারিকুল ইসলাম") }
    var vitals by remember { mutableStateOf("BP: 110/70, Temp: 98.4°F") }
    var diagnosis by remember { mutableStateOf("") }
    var advice by remember { mutableStateOf("") }
    var isEmergency by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = if (language == CampLanguage.BANGLA) "স্বাস্থ্য ও মেডিকেল স্ক্রিনিং এন্ট্রি" else "New Health Screening",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = LscSlateDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "অংশগ্রহণকারী:", fontSize = 11.sp, color = Color.Gray)
                LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(participants) { p ->
                        val isSel = p.id == selectedParticipant?.id
                        Surface(
                            color = if (isSel) LscEmeraldPrimary else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { selectedParticipant = p }
                        ) {
                            Text(
                                text = "${p.nameBn.take(8)} (${p.campCode})",
                                fontSize = 10.sp,
                                color = if (isSel) Color.White else LscSlateDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("GENERAL", "DENTAL", "EYE").forEach { type ->
                        FilterChip(
                            selected = checkupType == type,
                            onClick = { checkupType = type },
                            label = { Text(type, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = diagnosis,
                    onValueChange = { diagnosis = it },
                    placeholder = { Text("রোগ নির্ণয় / লক্ষণ (যেমন: কাশি, দাঁতের ক্ষয়)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = advice,
                    onValueChange = { advice = it },
                    placeholder = { Text("চিকিৎসকের পরামর্শ ও প্রেসক্রিপশন", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isEmergency, onCheckedChange = { isEmergency = it })
                    Text(text = "জরুরি সতর্কতা ফ্ল্যাগ (Urgent Alert)", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("বাতিল")
                    }
                    Button(
                        onClick = {
                            selectedParticipant?.let { p ->
                                onSubmit(p, checkupType, doctor, vitals, diagnosis.ifEmpty { "স্বাভাবিক" }, advice.ifEmpty { "বিশ্রাম ও পর্যাপ্ত পানি" }, isEmergency)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("সংরক্ষণ করুন")
                    }
                }
            }
        }
    }
}
