package com.example.ui.screens.subgroups

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampGroupEntity
import com.example.data.model.CampLanguage
import com.example.data.model.CampRationaleData
import com.example.data.model.ParticipantEntity
import com.example.data.model.SubgroupInfo
import com.example.ui.screens.stations.SubgroupBatchAttendanceDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubgroupsDirectoryScreen(
    language: CampLanguage,
    groups: List<CampGroupEntity>,
    participants: List<ParticipantEntity>,
    onSelectParticipant: (ParticipantEntity) -> Unit,
    onBatchSubgroupAttendance: (
        eventId: Long,
        eventTitleBn: String,
        dayNo: Int,
        subgroupKey: String,
        absentIds: Set<Long>,
        stationSealCode: String?
    ) -> Unit = { _, _, _, _, _, _ -> },
    activeDay: Int = 2,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isBangla = language == CampLanguage.BANGLA
    val context = LocalContext.current
    val allSubgroups = remember { CampRationaleData.subgroups }

    var searchQuery by remember { mutableStateOf("") }
    var selectedGroupFilter by remember { mutableStateOf<String?>(null) }
    var viewingSubgroupRoster by remember { mutableStateOf<SubgroupInfo?>(null) }
    var batchRollCallSubgroup by remember { mutableStateOf<SubgroupInfo?>(null) }
    val eveningBriefingDone = remember { mutableStateMapOf<String, Boolean>() }

    val filteredSubgroups = remember(searchQuery, selectedGroupFilter) {
        allSubgroups.filter { sub ->
            val matchesGroup = selectedGroupFilter == null || sub.groupId == selectedGroupFilter
            val matchesSearch = searchQuery.isBlank() ||
                    sub.subgroupCode.contains(searchQuery, ignoreCase = true) ||
                    sub.mentorName.contains(searchQuery, ignoreCase = true) ||
                    sub.mentorPhone.contains(searchQuery, ignoreCase = true) ||
                    sub.roomNo.contains(searchQuery, ignoreCase = true) ||
                    sub.buildingName.contains(searchQuery, ignoreCase = true) ||
                    sub.groupNameBn.contains(searchQuery, ignoreCase = true)
            matchesGroup && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (onBack != null) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("subgroups_back_btn")) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFFE0E7FF), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = Color(0xFF4338CA)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBangla) "২৪টি সাব-গ্রুপ ও ইন-চার্জ ককপিট" else "24 Subgroups & In-Charges Hub",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                        Text(
                            text = if (isBangla) "৬টি মূল দল • ২৪টি সাব-গ্রুপ (৬০৯ জন) • ১-ট্যাপ ব্যাচ হাজিরা" else "6 Main Groups • 24 Subgroups (609) • 1-Tap Roll-Call",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Surface(
                        color = Color(0xFFE0E7FF),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "${allSubgroups.size} ${if (isBangla) "সাব-গ্রুপ" else "Subgroups"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4338CA),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (isBangla) "সাব-গ্রুপ, ইন-চার্জ বা রুম নম্বর খুঁজুন..." else "Search subgroup, mentor or room...",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFFF1F5F9),
                        focusedBorderColor = Color(0xFF4338CA)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("subgroup_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedGroupFilter == null,
                            onClick = { selectedGroupFilter = null },
                            label = { Text(if (isBangla) "সকল (২৪টি)" else "All (24)", fontSize = 11.sp) }
                        )
                    }
                    items(groups) { grp ->
                        FilterChip(
                            selected = selectedGroupFilter == grp.id,
                            onClick = {
                                selectedGroupFilter = if (selectedGroupFilter == grp.id) null else grp.id
                            },
                            label = {
                                Text(
                                    text = if (isBangla) grp.nameBn else grp.nameEn,
                                    fontSize = 11.sp
                                )
                            }
                        )
                    }
                }
            }
        }

        // Subgroups Cards List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredSubgroups, key = { it.id }) { sub ->
                val groupColor = Color(sub.colorHex)
                val isBriefed = eveningBriefingDone[sub.id] ?: false

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("subgroup_card_${sub.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(groupColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${sub.subgroupNo}",
                                    fontWeight = FontWeight.Bold,
                                    color = groupColor,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = sub.subgroupCode,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = LscSlateDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = groupColor.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (isBangla) sub.groupNameBn else sub.groupNameEn,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = groupColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "ইন-চার্জ: ${sub.mentorName}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E293B),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            IconButton(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${sub.mentorPhone}"))
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFDCFCE7), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call Mentor",
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.MeetingRoom, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "কক্ষ ${sub.roomNo} • ${sub.buildingName}",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray
                                )
                            }

                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${sub.memberCount} জন সদস্য",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF334155),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Fast 1-Tap Actions: Evening Feedback, 1-Tap Roll-Call, View Roster
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (isBriefed) Color(0xFFDCFCE7) else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isBriefed) Color(0xFF86EFAC) else Color(0xFFE2E8F0)),
                                modifier = Modifier.clickable { eveningBriefingDone[sub.id] = !isBriefed }
                            ) {
                                Text(
                                    text = if (isBriefed) "🌙 সান্ধ্য মূল্যায়ন ✓" else "🌙 সান্ধ্য মূল্যায়ন",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isBriefed) Color(0xFF15803D) else Color.DarkGray,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { batchRollCallSubgroup = sub },
                                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text(
                                        text = if (isBangla) "⚡ ১-ট্যাপ হাজিরা" else "⚡ Roll-Call",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = { viewingSubgroupRoster = sub },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text(
                                        text = if (isBangla) "সদস্য তালিকা" else "Members",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 1-Tap Subgroup Batch Attendance Dialog
    batchRollCallSubgroup?.let { targetSub ->
        SubgroupBatchAttendanceDialog(
            language = language,
            dialogTitleBn = "⚡ ${targetSub.subgroupCode} — ১-ট্যাপ হাজিরা",
            dialogSubtitleBn = "ইনচার্জ: ${targetSub.mentorName} • কক্ষ: ${targetSub.roomNo}",
            participants = participants,
            initialGroupId = targetSub.groupId,
            initialSubgroup = targetSub.officialSubgroupKey.ifBlank { targetSub.subgroupCode },
            stationSealCode = null,
            onDismiss = { batchRollCallSubgroup = null },
            onConfirmBatch = { subgroupKey, absentIds ->
                onBatchSubgroupAttendance(
                    2L,
                    "${targetSub.subgroupCode} সাব-গ্রুপ রোল-কল",
                    activeDay,
                    subgroupKey,
                    absentIds,
                    null
                )
            }
        )
    }

    // Modal Dialog showing exact roster of students in that subgroup
    viewingSubgroupRoster?.let { sub ->
        val groupParticipants = remember(sub.groupId, sub.subgroupNo, sub.subgroupCode, participants) {
            participants.filter { p ->
                p.subgroup.equals(sub.subgroupCode, ignoreCase = true) ||
                        (p.groupId == sub.groupId && p.subgroup.endsWith("-${sub.subgroupNo}"))
            }
        }

        AlertDialog(
            onDismissRequest = { viewingSubgroupRoster = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = Color(sub.colorHex))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = sub.subgroupCode,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "ইন-চার্জ: ${sub.mentorName} (${sub.mentorPhone})",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            },
            text = {
                Column(modifier = Modifier.heightIn(max = 380.dp)) {
                    Text(
                        text = "নির্ধারিত সদস্য (${groupParticipants.size} জন):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LscSlateDark,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (groupParticipants.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "এই সাব-গ্রুপে সদস্য ডাটা লোড হচ্ছে...",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(groupParticipants, key = { it.id }) { p ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSelectParticipant(p)
                                            viewingSubgroupRoster = null
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = p.campCode,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(sub.colorHex)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (isBangla) p.nameBn else p.nameEn,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "সংস্থা: ${p.organization} • জেলা: ${p.districtBn} • রুম: ${p.roomNo}",
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewingSubgroupRoster = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(sub.colorHex))
                ) {
                    Text("বন্ধ করুন")
                }
            }
        )
    }
}
