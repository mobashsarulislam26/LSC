package com.example.ui.screens.health

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
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
    onSubmitRecord: (participant: ParticipantEntity, type: String, doc: String, vitals: String, diag: String, advice: String, isEmergency: Boolean) -> Unit = { _, _, _, _, _, _, _ -> },
    onSubmitStation6Record: (
        participant: ParticipantEntity,
        type: String,
        doc: String,
        vitals: String,
        diag: String,
        advice: String,
        isEmergency: Boolean,
        heightCm: Double,
        weightKg: Double,
        dentalBusReferred: Boolean,
        visionNotes: String,
        photoDocumented: Boolean
    ) -> Unit = { p, t, d, v, dg, adv, em, _, _, _, _, _ ->
        onSubmitRecord(p, t, d, v, dg, adv, em)
    }
) {
    val isBangla = language == CampLanguage.BANGLA
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "GENERAL", "DENTAL", "EYE", "EMERGENCY"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Station-6 Official Banner
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
            border = BorderStroke(1.dp, Color(0xFF86EFAC)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBangla) "🏥 স্টেশন-৬: মেডিকেল, ভ্রাম্যমাণ ডেন্টাল বাস ও আই ক্যাম্প" else "🏥 Station 6: Medical, Dental Bus & Eye Camp",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF065F46)
                    )
                    Text(
                        text = if (isBangla)
                            "২য় দিন: উচ্চতা-ওজন • ৪র্থ দিন: ডেন্টাল বাস • ৫ম দিন: চক্ষু পরীক্ষা (ইন-চার্জ: ডা. আনোয়ার পারভেজ)"
                        else
                            "Day 2: General/BMI • Day 4: Mobile Dental Bus • Day 5: Vision Camp",
                        fontSize = 10.sp,
                        color = Color(0xFF047857)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4 KPI Counters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            HealthKpi(
                title = if (isBangla) "মোট স্ক্রিনিং" else "Screened",
                count = "${healthRecords.size}",
                color = LscEmeraldPrimary,
                modifier = Modifier.weight(1f)
            )
            HealthKpi(
                title = if (isBangla) "🦷 ডেন্টাল বাস" else "Dental",
                count = "${healthRecords.count { it.checkupType == "DENTAL" || it.dentalBusReferred }}",
                color = Color(0xFF0284C7),
                modifier = Modifier.weight(1f)
            )
            HealthKpi(
                title = if (isBangla) "👁 চক্ষু পরীক্ষা" else "Vision",
                count = "${healthRecords.count { it.checkupType == "EYE" || it.visionNotes.isNotBlank() }}",
                color = Color(0xFF7C3AED),
                modifier = Modifier.weight(1f)
            )
            HealthKpi(
                title = if (isBangla) "🚨 জরুরি" else "Urgent",
                count = "${healthRecords.count { it.isEmergencyFlag }}",
                color = Color(0xFFDC2626),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter chips and Fast Add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                val tabs = listOf(
                    "ALL" to "সকল",
                    "GENERAL" to "২য় দিন: সাধারণ",
                    "DENTAL" to "৪র্থ দিন: ডেন্টাল",
                    "EYE" to "৫ম দিন: চক্ষু",
                    "EMERGENCY" to "জরুরি ফ্ল্যাগ"
                )
                items(tabs) { (key, label) ->
                    FilterChip(
                        selected = selectedFilter == key,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (key == "EMERGENCY") Color(0xFFDC2626) else LscEmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("add_health_record_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isBangla) "দ্রুত স্ক্রিনিং" else "New Checkup", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val displayList = remember(healthRecords, selectedFilter) {
            when (selectedFilter) {
                "EMERGENCY" -> healthRecords.filter { it.isEmergencyFlag }
                "GENERAL" -> healthRecords.filter { it.checkupType == "GENERAL" }
                "DENTAL" -> healthRecords.filter { it.checkupType == "DENTAL" || it.dentalBusReferred }
                "EYE" -> healthRecords.filter { it.checkupType == "EYE" || it.visionNotes.isNotBlank() }
                else -> healthRecords
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(displayList, key = { it.id }) { record ->
                HealthRecordCard(record = record, language = language)
            }
        }
    }

    if (showAddDialog) {
        AddHealthDialog(
            participants = participants,
            language = language,
            onDismiss = { showAddDialog = false },
            onSubmit = { p, type, doc, vit, diag, adv, isEmer, hCm, wKg, dentalRef, vis, photo ->
                onSubmitStation6Record(p, type, doc, vit, diag, adv, isEmer, hCm, wKg, dentalRef, vis, photo)
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
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = count, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = color)
            Text(text = title, fontSize = 9.5.sp, color = Color.Gray)
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
        border = BorderStroke(
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

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (record.photoDocumented) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "📸 ডকুমেন্টেড",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
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
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(6.dp))

            if (record.heightCm.isNotBlank() || record.weightKg.isNotBlank() || record.dentalBusReferred || record.visionNotes.isNotBlank()) {
                Row(
                    modifier = Modifier.padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (record.heightCm.isNotBlank() || record.weightKg.isNotBlank()) {
                        Text(
                            text = "📏 উচ্চতা: ${record.heightCm} cm | ⚖ ওজন: ${record.weightKg} kg",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F766E)
                        )
                    }
                    if (record.dentalBusReferred) {
                        Text(
                            text = "🦷 ডেন্টাল বাস রেফার্ড",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                    }
                    if (record.visionNotes.isNotBlank()) {
                        Text(
                            text = "👁 ভিশন: ${record.visionNotes}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7C3AED)
                        )
                    }
                }
            }

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
    onSubmit: (
        participant: ParticipantEntity,
        type: String,
        doctor: String,
        vitals: String,
        diagnosis: String,
        advice: String,
        isEmergency: Boolean,
        heightCm: Double,
        weightKg: Double,
        dentalBusReferred: Boolean,
        visionNotes: String,
        photoDocumented: Boolean
    ) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubgroup by remember { mutableStateOf("ALL") }

    val subgroups = remember(participants) {
        participants.map { it.subgroup }.filter { it.isNotBlank() }.distinct()
    }

    val filteredParticipants = remember(participants, searchQuery, selectedSubgroup) {
        val q = searchQuery.trim()
        participants.filter { p ->
            val matchSub = selectedSubgroup == "ALL" || p.subgroup.equals(selectedSubgroup, ignoreCase = true)
            val matchQ = q.isBlank() ||
                    p.nameBn.contains(q, ignoreCase = true) ||
                    p.nameEn.contains(q, ignoreCase = true) ||
                    p.campCode.contains(q, ignoreCase = true) ||
                    p.fileNo.contains(q, ignoreCase = true)
            matchSub && matchQ
        }.take(80)
    }

    var selectedParticipant by remember(filteredParticipants) { mutableStateOf(filteredParticipants.firstOrNull()) }
    var checkupType by remember { mutableStateOf("GENERAL") } // GENERAL (Day 2), DENTAL (Day 4), EYE (Day 5)
    var doctor by remember { mutableStateOf("ডা. আনোয়ার পারভেজ (স্টেশন-৬)") }
    var vitals by remember { mutableStateOf("BP: 110/70, Temp: 98.4°F") }
    var heightText by remember { mutableStateOf("152") }
    var weightText by remember { mutableStateOf("44") }
    var dentalBusReferred by remember { mutableStateOf(false) }
    var visionNotes by remember { mutableStateOf("6/6 স্বাভাবিক") }
    var photoDocumented by remember { mutableStateOf(true) }
    var diagnosis by remember { mutableStateOf("সুস্থ ও স্বাভাবিক") }
    var advice by remember { mutableStateOf("নিয়মিত সুষম খাবার ও পর্যাপ্ত পানি পান") }
    var isEmergency by remember { mutableStateOf(false) }

    val quickDiagnosisChips = listOf(
        "সুস্থ ও স্বাভাবিক" to "নিয়মিত পুষ্টিকর খাবার ও পানি পান",
        "হালকা জ্বর/সর্দি" to "প্যারাসিটামল ও ওআরএস প্রদান, বিশ্রাম",
        "দাঁতের যত্ন/স্কেলিং প্রয়োজন" to "ভ্রাম্যমাণ ডেন্টাল বাসে রেফার ও দিনে ২ বার ব্রাশ",
        "চক্ষু ভিশন 6/6 স্বাভাবিক" to "স্ক্রিন টাইম কমানো ও সবুজ শাকসবজি গ্রহণ",
        "চশমা / চক্ষু ফলো-আপ প্রয়োজন" to "চক্ষু বিশেষজ্ঞের প্রেসক্রিপশন অনুযায়ী চশমা প্রদান"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = if (language == CampLanguage.BANGLA)
                        "🏥 স্টেশন-৬ দ্রুত স্ক্রিনিং (★ সিল স্বয়ংক্রিয়)"
                    else
                        "🏥 Station 6 Fast Health Screening",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = LscSlateDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Day / Station-6 Mode Selector
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        "GENERAL" to "২য় দিন: সাধারণ",
                        "DENTAL" to "৪র্থ দিন: ডেন্টাল",
                        "EYE" to "৫ম দিন: চক্ষু"
                    ).forEach { (type, label) ->
                        FilterChip(
                            selected = checkupType == type,
                            onClick = { checkupType = type },
                            label = { Text(label, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Subgroup + Search (Full 609 Roster)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedSubgroup == "ALL",
                            onClick = { selectedSubgroup = "ALL" },
                            label = { Text("সকল", fontSize = 10.sp) }
                        )
                    }
                    items(subgroups) { sg ->
                        FilterChip(
                            selected = selectedSubgroup == sg,
                            onClick = { selectedSubgroup = sg },
                            label = { Text(sg, fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("নাম বা ক্যাম্প আইডি খুঁজুন...", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(filteredParticipants, key = { it.id }) { p ->
                        val isSel = p.id == selectedParticipant?.id
                        Surface(
                            color = if (isSel) LscEmeraldPrimary else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { selectedParticipant = p }
                        ) {
                            Text(
                                text = "${p.nameBn.take(10)} (${p.campCode})",
                                fontSize = 10.sp,
                                color = if (isSel) Color.White else LscSlateDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Mode-specific 1-tap inputs
                when (checkupType) {
                    "GENERAL" -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = heightText,
                                onValueChange = { heightText = it },
                                label = { Text("উচ্চতা (cm)", fontSize = 10.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = weightText,
                                onValueChange = { weightText = it },
                                label = { Text("ওজন (kg)", fontSize = 10.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    "DENTAL" -> {
                        Surface(
                            color = Color(0xFFE0F2FE),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { dentalBusReferred = !dentalBusReferred }
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = dentalBusReferred, onCheckedChange = { dentalBusReferred = it })
                                Text(
                                    text = "🦷 ভ্রাম্যমাণ ডেন্টাল বাসে চিকিৎসা/স্কেলিং রেফারেল",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0369A1)
                                )
                            }
                        }
                    }
                    "EYE" -> {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val vOptions = listOf("6/6 স্বাভাবিক", "6/9 মৃদু সমস্যা", "6/12 চশমা প্রয়োজন", "চক্ষু ড্রপ প্রদান")
                            items(vOptions) { vOpt ->
                                FilterChip(
                                    selected = visionNotes == vOpt,
                                    onClick = { visionNotes = vOpt },
                                    label = { Text("👁 $vOpt", fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 1-Tap Preset Diagnosis Chips
                Text(text = "⚡ ১-ট্যাপ দ্রুত রোগ নির্ণয়:", fontSize = 10.sp, color = Color.Gray)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(quickDiagnosisChips) { (dChip, advChip) ->
                        Surface(
                            color = if (diagnosis == dChip) Color(0xFFDCFCE7) else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier.clickable {
                                diagnosis = dChip
                                advice = advChip
                            }
                        ) {
                            Text(
                                text = dChip,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LscSlateDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = diagnosis,
                    onValueChange = { diagnosis = it },
                    label = { Text("রোগ নির্ণয় / লক্ষণ", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = advice,
                    onValueChange = { advice = it },
                    label = { Text("চিকিৎসকের পরামর্শ ও প্রেসক্রিপশন", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = photoDocumented, onCheckedChange = { photoDocumented = it })
                        Text(text = "📸 ফটো ডকুমেন্টেশন", fontSize = 10.5.sp, color = LscSlateDark)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isEmergency, onCheckedChange = { isEmergency = it })
                        Text(text = "🚨 জরুরি সতর্কতা", fontSize = 10.5.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("বাতিল")
                    }
                    Button(
                        onClick = {
                            selectedParticipant?.let { p ->
                                onSubmit(
                                    p,
                                    checkupType,
                                    doctor,
                                    vitals,
                                    diagnosis.ifEmpty { "স্বাভাবিক" },
                                    advice.ifEmpty { "বিশ্রাম ও পর্যাপ্ত পানি" },
                                    isEmergency,
                                    heightText.toDoubleOrNull() ?: 0.0,
                                    weightText.toDoubleOrNull() ?: 0.0,
                                    dentalBusReferred,
                                    if (checkupType == "EYE") visionNotes else "",
                                    photoDocumented
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("সংরক্ষণ ও ★ সিল")
                    }
                }
            }
        }
    }
}
