package com.example.ui.screens.participants

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
import com.example.data.model.*
import com.example.ui.components.ParticipantQrDialog
import com.example.ui.theme.*

@Composable
fun ParticipantListScreen(
    language: CampLanguage,
    participants: List<ParticipantEntity>,
    groups: List<CampGroupEntity>,
    searchQuery: String,
    selectedGroupFilter: String?,
    onSearchChange: (String) -> Unit,
    onGroupFilterChange: (String?) -> Unit,
    onSelectParticipant: (ParticipantEntity) -> Unit,
    onOpenImportDialog: () -> Unit,
    onOpenExportDialog: () -> Unit = {},
    onAddParticipant: () -> Unit = {},
    healthStatuses: List<ParticipantHealthStatusEntity> = emptyList()
) {
    var qrParticipant by remember { mutableStateOf<ParticipantEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar at Top & Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("participant_search_bar"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        text = if (language == CampLanguage.BANGLA)
                            "নাম অথবা আইডি দিয়ে খুঁজুন (উদাঃ LSC-2026-001)..."
                        else
                            "Search by participant name or ID...",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (searchQuery.isNotEmpty()) LscEmeraldPrimary else Color.Gray
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchChange("") },
                            modifier = Modifier.testTag("clear_search_btn")
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear Search", tint = Color.Gray)
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("participant_search_field"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LscEmeraldPrimary,
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF8FAFC)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Export CSV Button
            IconButton(
                onClick = onOpenExportDialog,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0F2FE))
                    .testTag("export_csv_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = "Export CSV Report",
                    tint = Color(0xFF0284C7)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onOpenImportDialog,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LscEmeraldLight)
                    .testTag("open_import_btn")
            ) {
                Icon(Icons.Default.UploadFile, contentDescription = "Import Excel/CSV", tint = LscEmeraldPrimary)
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onAddParticipant,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LscEmeraldPrimary)
                    .testTag("add_participant_btn")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Participant", tint = Color.White)
            }
        }

        // Active Search Query Banner
        if (searchQuery.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = Color(0xFFEFF6FF),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = Color(0xFF1D4ED8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "অনুসন্ধান: \"$searchQuery\" (${participants.size} জন পাওয়া গেছে)"
                            else
                                "Query: \"$searchQuery\" (${participants.size} found)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1D4ED8)
                        )
                    }
                    Text(
                        text = if (language == CampLanguage.BANGLA) "মুছুন" else "Clear",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626),
                        modifier = Modifier
                            .clickable { onSearchChange("") }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Group Filter Horizontal Scroll
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedGroupFilter == null,
                    onClick = { onGroupFilterChange(null) },
                    label = {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "সকল (${participants.size})" else "All (${participants.size})",
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LscEmeraldPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }

            items(groups) { grp ->
                FilterChip(
                    selected = selectedGroupFilter == grp.id,
                    onClick = { onGroupFilterChange(if (selectedGroupFilter == grp.id) null else grp.id) },
                    label = {
                        Text(
                            text = if (language == CampLanguage.BANGLA) grp.nameBn else grp.nameEn,
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(grp.colorHex),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Headcount & Sub-info Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (language == CampLanguage.BANGLA)
                        "মোট প্রাপ্ত তালিকা: ${participants.size} জন"
                    else
                        "Found: ${participants.size} participants",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = Color(0xFFE0F2FE),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .clickable { onOpenExportDialog() }
                        .testTag("quick_export_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "CSV রিপোর্ট" else "Export CSV",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                    }
                }
            }

            Text(
                text = if (language == CampLanguage.BANGLA) "হাজিরা লক্ষ্যমাত্রা: ≥৯০%" else "Target: ≥90%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = LscEmeraldPrimary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Participants List
        if (participants.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 96.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (searchQuery.isNotBlank()) Icons.Default.Search else Icons.Default.Group,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) {
                            if (language == CampLanguage.BANGLA) "‘$searchQuery’ দিয়ে কোনো অংশগ্রহণকারী পাওয়া যায়নি" else "No participants found matching '$searchQuery'"
                        } else {
                            if (language == CampLanguage.BANGLA) "কোনো অংশগ্রহণকারী পাওয়া যায়নি" else "No participants found"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = LscSlateDark,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) {
                            if (language == CampLanguage.BANGLA)
                                "নাম বা আইডি নম্বর পরীক্ষা করুন অথবা অনুসন্ধান রিসেট করুন।"
                            else
                                "Please verify the participant name or ID, or reset the search."
                        } else {
                            if (language == CampLanguage.BANGLA)
                                "নতুন অংশগ্রহণকারী যোগ করুন অথবা এক্সেল ফাইল থেকে ইম্পোর্ট করুন।"
                            else
                                "Add a new participant or import from Excel/CSV file."
                        },
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (searchQuery.isNotBlank()) {
                        Button(
                            onClick = { onSearchChange("") },
                            colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA) "অনুসন্ধান রিসেট করুন" else "Reset Search",
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Button(
                            onClick = onAddParticipant,
                            colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA) "অংশগ্রহণকারী যুক্ত করুন" else "Add Participant",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("participants_lazy_list"),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(participants, key = { it.id }) { participant ->
                    val health = healthStatuses.find { it.participantId == participant.id || it.campCode == participant.campCode }
                    ParticipantCard(
                        participant = participant,
                        healthStatus = health,
                        language = language,
                        onClick = { onSelectParticipant(participant) },
                        onOpenQr = { qrParticipant = participant }
                    )
                }
            }
        }
    }

    qrParticipant?.let { targetParticipant ->
        ParticipantQrDialog(
            participant = targetParticipant,
            language = language,
            onDismiss = { qrParticipant = null }
        )
    }
}

@Composable
fun ParticipantCard(
    participant: ParticipantEntity,
    language: CampLanguage,
    onClick: () -> Unit,
    onOpenQr: () -> Unit = {},
    healthStatus: ParticipantHealthStatusEntity? = null
) {
    val (healthColor, healthLabel) = when (healthStatus?.healthCondition?.lowercase()) {
        "critical" -> Color(0xFFDC2626) to (if (language == CampLanguage.BANGLA) "জরুরি" else "Critical")
        "requires attention" -> Color(0xFFEA580C) to (if (language == CampLanguage.BANGLA) "নজরদারি" else "Attention")
        "needs rest", "mild symptoms" -> Color(0xFFD97706) to (if (language == CampLanguage.BANGLA) "বিশ্রাম" else "Rest")
        else -> if (participant.medicalFlag != null) Color(0xFFEA580C) to "চিকিৎসা" else Color(0xFF059669) to (if (language == CampLanguage.BANGLA) "সুস্থ" else "Healthy")
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("participant_item_${participant.campCode}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Seeded Avatar with Initial
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(LscEmeraldLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = participant.nameBn.firstOrNull()?.toString() ?: "P",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = LscEmeraldPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = participant.nameBn,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                        if (participant.nameEn.isNotBlank()) {
                            Text(
                                text = participant.nameEn,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Color-coded Health Indicator Pill
                        Surface(
                            color = healthColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, healthColor.copy(alpha = 0.35f)),
                            modifier = Modifier.testTag("health_indicator_${participant.campCode}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
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
                                    color = healthColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Attendance Percentage
                        Surface(
                            color = if (participant.attendancePercentage >= 90f) Color(0xFFECFDF5) else Color(0xFFFEF2F2),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${"%.0f".format(participant.attendancePercentage)}%",
                                color = if (participant.attendancePercentage >= 90f) Color(0xFF059669) else Color(0xFFDC2626),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = participant.campCode,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2563EB)
                    )
                    Text(text = " • ", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = "${participant.groupNameBn} (${participant.subgroup})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = LscEmeraldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${participant.age} বছর (${participant.gender}) • ${participant.districtBn} • কক্ষ ${participant.roomNo}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    if (participant.medicalFlag != null) {
                        Text(
                            text = "⚠ চিকিৎসা",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Fast QR Code Pass Button
            IconButton(
                onClick = onOpenQr,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEFF6FF))
                    .testTag("participant_qr_btn_${participant.campCode}")
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = "View QR Code",
                    tint = Color(0xFF1D4ED8),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
