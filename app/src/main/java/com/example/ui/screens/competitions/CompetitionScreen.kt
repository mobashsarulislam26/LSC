package com.example.ui.screens.competitions

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.components.BatchImportDialog
import com.example.ui.theme.*

@Composable
fun CompetitionScreen(
    language: CampLanguage,
    competitions: List<CompetitionEntity>,
    competitionScores: List<CompetitionScoreEntity>,
    groups: List<CampGroupEntity>,
    participants: List<ParticipantEntity>,
    judges: List<JudgeEntity> = emptyList(),
    isAdminUnlocked: Boolean = false,
    onRequestAdminUnlock: () -> Unit = {},
    stations: List<StationEntity> = emptyList(),
    selectedParticipant: ParticipantEntity? = null,
    onSelectParticipant: ((ParticipantEntity) -> Unit)? = null,
    onAddJudge: (nameBn: String, nameEn: String, designation: String, category: String, phone: String, notes: String) -> Unit = { _, _, _, _, _, _ -> },
    onUpdateJudge: (JudgeEntity) -> Unit = {},
    onDeleteJudge: (JudgeEntity) -> Unit = {},
    onBatchImportJudges: (String) -> Int = { 0 },
    onBatchImportVolunteers: (String) -> Int = { 0 },
    onBatchImportDutyOfficers: (String) -> Int = { 0 },
    onAwardPodiumWinner: (competitionId: Long, participant: ParticipantEntity, rank: Int, isPrizeHandedOver: Boolean, remarks: String) -> Unit = { _, _, _, _, _ -> },
    onTogglePrizeHandedOver: (CompetitionScoreEntity) -> Unit = {},
    onSubmitScore: (competitionId: Long, participant: ParticipantEntity, accuracy: Double, pres: Double, disc: Double, remarks: String) -> Unit
) {
    var selectedTab by remember { mutableStateOf("LEADERBOARD") } // "LEADERBOARD", "MILESTONES", "JUDGING", "JUDGES"
    var selectedCompId by remember(competitions) { mutableStateOf(competitions.firstOrNull()?.id ?: 1L) }
    var showScoreDialog by remember { mutableStateOf(false) }
    var showQuickPodiumSheet by remember { mutableStateOf(false) }
    var showAddJudgeDialog by remember { mutableStateOf(false) }
    var showBatchImportDialog by remember { mutableStateOf(false) }
    var judgeToEdit by remember { mutableStateOf<JudgeEntity?>(null) }
    var judgeToDelete by remember { mutableStateOf<JudgeEntity?>(null) }
    var judgeCategoryFilter by remember { mutableStateOf("ALL") }

    val totalPlannedPrizes = remember(competitions) {
        val sum = competitions.sumOf { it.prizeCount }
        if (sum > 0) sum else 835
    }
    val handedOverCount = remember(competitionScores) {
        competitionScores.count { it.isPrizeHandedOver }
    }
    val activeCompetition = remember(competitions, selectedCompId) {
        competitions.find { it.id == selectedCompId } ?: competitions.firstOrNull()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // 4-Tab Selector: Leaderboard vs Milestones vs Judge Scoring vs Judges Panel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilterChip(
                selected = selectedTab == "LEADERBOARD",
                onClick = { selectedTab = "LEADERBOARD" },
                label = { Text(if (language == CampLanguage.BANGLA) "লিডারবোর্ড" else "Leaderboard", fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LscGoldSecondary,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(1f).testTag("tab_leaderboard")
            )

            FilterChip(
                selected = selectedTab == "MILESTONES",
                onClick = { selectedTab = "MILESTONES" },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MilitaryTech, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(if (language == CampLanguage.BANGLA) "১৮ স্টার ও মাইলস্টোন" else "18★ & Milestones", fontSize = 10.sp)
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF7C3AED),
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(1.25f).testTag("tab_milestones")
            )

            FilterChip(
                selected = selectedTab == "JUDGING",
                onClick = { selectedTab = "JUDGING" },
                label = { Text(if (language == CampLanguage.BANGLA) "স্কোর ও পুরস্কার" else "Scoring", fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LscEmeraldPrimary,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(1f).testTag("tab_judging")
            )

            FilterChip(
                selected = selectedTab == "JUDGES",
                onClick = { selectedTab = "JUDGES" },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(if (language == CampLanguage.BANGLA) "বিচারক" else "Judges", fontSize = 10.sp)
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1D4ED8),
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(0.85f).testTag("tab_judges")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedTab) {
            "LEADERBOARD" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Official 2026 835 Prizes Banner
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
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
                                        text = if (language == CampLanguage.BANGLA)
                                            "🏆 ওয়ামী LSC ২০২৬ অফিশিয়াল প্রতিযোগিতা ও পুরস্কার"
                                        else
                                            "🏆 WAMY LSC 2026 Competitions & Awards",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF92400E)
                                    )
                                    Text(
                                        text = if (language == CampLanguage.BANGLA)
                                            "${competitions.size}টি ইভেন্ট • মোট বরাদ্দ: $totalPlannedPrizes টি পুরস্কার • হস্তান্তরকৃত: $handedOverCount টি"
                                        else
                                            "${competitions.size} Events • Planned: $totalPlannedPrizes Prizes • Handed over: $handedOverCount",
                                        fontSize = 11.sp,
                                        color = Color(0xFFB45309)
                                    )
                                }

                                Button(
                                    onClick = {
                                        selectedTab = "JUDGING"
                                        showQuickPodiumSheet = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (language == CampLanguage.BANGLA) "⚡ ১ম/২য়/৩য়" else "⚡ Podium",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "গ্রুপ সার্বিক পয়েন্ট তালিকা (৬টি অফিশিয়াল গ্রুপ)" else "Group Overall Standings",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                    }

                    items(groups.sortedByDescending { it.totalPoints }) { grp ->
                        GroupLeaderboardCard(group = grp, language = language)
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "সেরা পারফর্মার ও অ্যাওয়ার্ড বিজয়ী তালিকা" else "Top Participants & Award Winners",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                    }

                    items(competitionScores.sortedByDescending { it.totalScore }) { score ->
                        ParticipantScoreCard(
                            score = score,
                            language = language,
                            onToggleHandover = { onTogglePrizeHandedOver(score) }
                        )
                    }
                }
            }

            "JUDGING" -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(competitions, key = { it.id }) { comp ->
                            val isSelected = comp.id == selectedCompId
                            Surface(
                                color = if (isSelected) LscEmeraldPrimary else Color.White,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (isSelected) LscEmeraldPrimary else Color(0xFFCBD5E1)),
                                modifier = Modifier.clickable { selectedCompId = comp.id }
                            ) {
                                Text(
                                    text = "${if (language == CampLanguage.BANGLA) comp.nameBn else comp.nameEn} (${comp.prizeCount})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else LscSlateDark,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Active Event Summary Card
                    activeCompetition?.let { comp ->
                        val scopeLabel = when (comp.selectionScope) {
                            "SUBGROUP_TOP3" -> "সেশনেই প্রতি সাব-গ্রুপে ১ম/২য়/৩য়"
                            "GROUP_BEST" -> "গ্রুপ ভিত্তিক সেরা"
                            "PARADE" -> "প্যারেড ও কুচকাওয়াজ"
                            "TEAM" -> "দলীয় পুরস্কার"
                            "OUTING" -> "শিক্ষা সফর কুইজ"
                            else -> comp.selectionScope
                        }
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (language == CampLanguage.BANGLA) comp.nameBn else comp.nameEn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF065F46)
                                    )
                                    Text(
                                        text = "দিন-${comp.dayNo} • $scopeLabel • বিচারক: ${comp.judgeName}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF047857)
                                    )
                                }
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "🎁 ${comp.prizeCount}টি পুরস্কার",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Dual Fast Entry Buttons: 3-Tap Podium Picker + Detailed 100-pt Rubric
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showQuickPodiumSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.15f)
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA) "⚡ ১ম/২য়/৩য় বিজয়ী নির্বাচন" else "⚡ Quick 1st/2nd/3rd",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { showScoreDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("open_score_entry_btn")
                        ) {
                            Icon(Icons.Default.AddModerator, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA) "নতুন স্কোর এন্ট্রি" else "Enter Score",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val compScores = competitionScores.filter { it.competitionId == selectedCompId }
                        .sortedByDescending { it.totalScore }

                    Text(
                        text = if (language == CampLanguage.BANGLA)
                            "এই প্রতিযোগিতার বিজয়ী ও জমাকৃত স্কোর (${compScores.size} জন):"
                        else
                            "Submitted Scores & Winners (${compScores.size}):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (compScores.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (language == CampLanguage.BANGLA) "এখনও কোনো স্কোর বা বিজয়ী যুক্ত করা হয়নি।" else "No scores submitted yet.",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(compScores, key = { it.id }) { s ->
                                ParticipantScoreCard(
                                    score = s,
                                    language = language,
                                    onToggleHandover = { onTogglePrizeHandedOver(s) }
                                )
                            }
                        }
                    }
                }
            }

            "JUDGES" -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (language == CampLanguage.BANGLA) "ক্যাম্প বিচারকমণ্ডলী" else "Camp Judges Panel",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF1E3A8A)
                                    )
                                    Text(
                                        text = if (language == CampLanguage.BANGLA)
                                            "মোট ${judges.size} জন বিচারক নিবন্ধিত আছেন"
                                        else
                                            "${judges.size} judges registered",
                                        fontSize = 11.sp,
                                        color = Color(0xFF3B82F6)
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            if (isAdminUnlocked) {
                                                showBatchImportDialog = true
                                            } else {
                                                onRequestAdminUnlock()
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "এক্সেল", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            if (isAdminUnlocked) {
                                                showAddJudgeDialog = true
                                            } else {
                                                onRequestAdminUnlock()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("add_judge_btn")
                                    ) {
                                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (language == CampLanguage.BANGLA) "+ বিচারক" else "+ Judge",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val categories = listOf(
                        "ALL" to (if (language == CampLanguage.BANGLA) "সকল বিচারক" else "All Judges"),
                        "Quran" to (if (language == CampLanguage.BANGLA) "ক্বিরাত ও তাজবীদ" else "Quran"),
                        "Hadith" to (if (language == CampLanguage.BANGLA) "হাদিস ও দোয়া" else "Hadith"),
                        "Quiz" to (if (language == CampLanguage.BANGLA) "কুইজ ও আইকিউ" else "Quiz"),
                        "Arts" to (if (language == CampLanguage.BANGLA) "দেয়ালিকা ও রচনা" else "Arts"),
                        "Speech" to (if (language == CampLanguage.BANGLA) "বক্তৃতা ও কবিতা" else "Speech"),
                        "Sports" to (if (language == CampLanguage.BANGLA) "ক্রীড়া ও ডিসিপ্লিন" else "Sports")
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(categories) { (catKey, catLabel) ->
                            val isSel = judgeCategoryFilter == catKey
                            Surface(
                                color = if (isSel) Color(0xFF1D4ED8) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSel) Color(0xFF1D4ED8) else Color(0xFFE2E8F0)),
                                modifier = Modifier.clickable { judgeCategoryFilter = catKey }
                            ) {
                                Text(
                                    text = catLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSel) Color.White else LscSlateDark,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val filteredJudges = if (judgeCategoryFilter == "ALL") {
                        judges
                    } else {
                        judges.filter { it.assignedCategory.equals(judgeCategoryFilter, ignoreCase = true) }
                    }

                    if (filteredJudges.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Gavel,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (language == CampLanguage.BANGLA)
                                        "এই ক্যাটাগরিতে কোনো বিচারক নেই।"
                                    else
                                        "No judges found in this category.",
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredJudges, key = { it.id }) { judge ->
                                JudgeCard(
                                    judge = judge,
                                    language = language,
                                    isAdminUnlocked = isAdminUnlocked,
                                    onEdit = {
                                        if (isAdminUnlocked) {
                                            judgeToEdit = judge
                                        } else {
                                            onRequestAdminUnlock()
                                        }
                                    },
                                    onDelete = {
                                        if (isAdminUnlocked) {
                                            judgeToDelete = judge
                                        } else {
                                            onRequestAdminUnlock()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            "MILESTONES" -> {
                StationMilestoneTracker(
                    language = language,
                    participants = participants,
                    competitions = competitions,
                    competitionScores = competitionScores,
                    stations = stations,
                    selectedParticipant = selectedParticipant,
                    onSelectParticipant = { onSelectParticipant?.invoke(it) },
                    onScoreMilestoneClick = { milestone ->
                        selectedCompId = milestone.competitionId
                        showScoreDialog = true
                    }
                )
            }
        }
    }

    // Modal Dialog: Add New Judge
    if (showAddJudgeDialog) {
        AddJudgeDialog(
            language = language,
            onDismiss = { showAddJudgeDialog = false },
            onSave = { nameBn, nameEn, designation, category, phone, notes ->
                onAddJudge(nameBn, nameEn, designation, category, phone, notes)
                showAddJudgeDialog = false
            }
        )
    }

    // Modal Dialog: Edit Existing Judge
    judgeToEdit?.let { targetJudge ->
        EditJudgeDialog(
            judge = targetJudge,
            language = language,
            onDismiss = { judgeToEdit = null },
            onSave = { updated ->
                onUpdateJudge(updated)
                judgeToEdit = null
            }
        )
    }

    // Batch Excel/CSV Import Dialog
    if (showBatchImportDialog) {
        BatchImportDialog(
            language = language,
            initialTarget = "JUDGES",
            onDismiss = { showBatchImportDialog = false },
            onImportJudges = onBatchImportJudges,
            onImportVolunteers = onBatchImportVolunteers,
            onImportDutyOfficers = onBatchImportDutyOfficers
        )
    }

    // Confirmation Dialog: Delete Judge
    if (judgeToDelete != null) {
        AlertDialog(
            onDismissRequest = { judgeToDelete = null },
            title = {
                Text(
                    text = if (language == CampLanguage.BANGLA) "বিচারক অপসারণ নিশ্চিতকরণ" else "Confirm Remove Judge",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (language == CampLanguage.BANGLA)
                        "আপনি কি নিশ্চিত যে '${judgeToDelete?.nameBn}' বিচারক তালিকা থেকে মুছে ফেলতে চান?"
                    else
                        "Are you sure you want to remove '${judgeToDelete?.nameBn}' from the judges panel?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        judgeToDelete?.let { onDeleteJudge(it) }
                        judgeToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(if (language == CampLanguage.BANGLA) "মুছে ফেলুন" else "Remove")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { judgeToDelete = null }) {
                    Text(if (language == CampLanguage.BANGLA) "বাতিল" else "Cancel")
                }
            }
        )
    }

    // Modal Dialog: 3-Tap Quick Podium Winner Picker (1st / 2nd / 3rd + Instant Prize Handover)
    if (showQuickPodiumSheet) {
        QuickPodiumWinnerDialog(
            participants = participants,
            competition = activeCompetition,
            existingScores = competitionScores.filter { it.competitionId == selectedCompId },
            language = language,
            onDismiss = { showQuickPodiumSheet = false },
            onAwardRank = { participant, rank, isHandedOver, note ->
                onAwardPodiumWinner(selectedCompId, participant, rank, isHandedOver, note)
            }
        )
    }

    // Modal Dialog: Submit Detailed Judge Score (Full 609 Roster searchable!)
    if (showScoreDialog) {
        JudgeScoreEntryDialog(
            participants = participants,
            competitionId = selectedCompId,
            judges = judges,
            language = language,
            onDismiss = { showScoreDialog = false },
            onSubmit = { p, acc, pres, disc, rem ->
                onSubmitScore(selectedCompId, p, acc, pres, disc, rem)
                showScoreDialog = false
            }
        )
    }
}

/**
 * 3-Tap Podium Winner Picker for Instant Session Awards (835 Official Prizes).
 * Select Subgroup -> Tap 1st / 2nd / 3rd on any student -> Done!
 */
@Composable
fun QuickPodiumWinnerDialog(
    participants: List<ParticipantEntity>,
    competition: CompetitionEntity?,
    existingScores: List<CompetitionScoreEntity>,
    language: CampLanguage,
    onDismiss: () -> Unit,
    onAwardRank: (participant: ParticipantEntity, rank: Int, isPrizeHandedOver: Boolean, remarks: String) -> Unit
) {
    val isBangla = language == CampLanguage.BANGLA
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubgroup by remember { mutableStateOf("ALL") }
    var instantPrizeHandedOver by remember {
        mutableStateOf(competition?.selectionScope == "SUBGROUP_TOP3" || competition?.selectionScope == "OUTING")
    }

    val subgroups = remember(participants) {
        participants.map { it.subgroup }.filter { it.isNotBlank() }.distinct()
    }

    val filteredParticipants = remember(participants, searchQuery, selectedSubgroup) {
        val q = searchQuery.trim()
        participants.filter { p ->
            val matchSub = selectedSubgroup == "ALL" || p.subgroup.equals(selectedSubgroup, ignoreCase = true)
            val matchQuery = q.isBlank() ||
                    p.nameBn.contains(q, ignoreCase = true) ||
                    p.nameEn.contains(q, ignoreCase = true) ||
                    p.campCode.contains(q, ignoreCase = true) ||
                    p.fileNo.contains(q, ignoreCase = true)
            matchSub && matchQuery
        }.take(60)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBangla) "⚡ দ্রুত ১ম / ২য় / ৩য় বিজয়ী নির্বাচন" else "⚡ Fast 1st / 2nd / 3rd Winner Picker",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = LscSlateDark
                        )
                        Text(
                            text = competition?.nameBn ?: "প্রতিযোগিতা",
                            fontSize = 11.sp,
                            color = LscEmeraldPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Instant Handover Switch
                Surface(
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { instantPrizeHandedOver = !instantPrizeHandedOver }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "🎁 সেশনেই তাৎক্ষণিক পুরস্কার হস্তান্তর সম্পন্ন" else "🎁 Mark Prize Handed Over Immediately",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = instantPrizeHandedOver,
                            onCheckedChange = { instantPrizeHandedOver = it }
                        )
                    }
                }

                // Subgroup Chips (24 Official Subgroups)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedSubgroup == "ALL",
                            onClick = { selectedSubgroup = "ALL" },
                            label = { Text(if (isBangla) "সকল সাব-গ্রুপ" else "All", fontSize = 10.sp) }
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
                    placeholder = { Text(if (isBangla) "নাম, ক্যাম্প আইডি বা ফাইল নম্বর খুঁজুন..." else "Search name or ID...", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.heightIn(max = 280.dp)) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(filteredParticipants, key = { it.id }) { p ->
                            val currentRank = existingScores.find { it.participantId == p.id }?.rank ?: 0
                            Surface(
                                color = if (currentRank in 1..3) Color(0xFFF0FDF4) else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (currentRank in 1..3) Color(0xFF86EFAC) else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = p.nameBn,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = LscSlateDark
                                        )
                                        Text(
                                            text = "${p.campCode} • ${p.subgroup}",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf(
                                            1 to "🥇 ১ম",
                                            2 to "🥈 ২য়",
                                            3 to "🥉 ৩য়"
                                        ).forEach { (rk, label) ->
                                            val isPicked = currentRank == rk
                                            Surface(
                                                color = when {
                                                    isPicked && rk == 1 -> Color(0xFFD97706)
                                                    isPicked && rk == 2 -> Color(0xFF2563EB)
                                                    isPicked -> Color(0xFF059669)
                                                    else -> Color.White
                                                },
                                                shape = RoundedCornerShape(6.dp),
                                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                                modifier = Modifier.clickable {
                                                    onAwardRank(p, rk, instantPrizeHandedOver, "")
                                                }
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isPicked) Color.White else LscSlateDark,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isBangla) "সম্পন্ন" else "Done")
                }
            }
        }
    }
}

/**
 * Modern M3 Judge Card Component with Edit and Delete
 */
@Composable
fun JudgeCard(
    judge: JudgeEntity,
    language: CampLanguage,
    isAdminUnlocked: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("judge_card_${judge.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFF6FF))
                    .border(1.5.dp, Color(0xFF3B82F6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = Color(0xFF1D4ED8),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = judge.nameBn,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark,
                            fontSize = 15.sp
                        )
                        if (judge.nameEn.isNotBlank()) {
                            Text(
                                text = judge.nameEn,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(30.dp).testTag("edit_judge_${judge.id}")
                        ) {
                            Icon(
                                imageVector = if (isAdminUnlocked) Icons.Default.Edit else Icons.Default.Lock,
                                contentDescription = "Edit Judge",
                                tint = if (isAdminUnlocked) Color(0xFF1D4ED8) else Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(30.dp).testTag("delete_judge_${judge.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Remove Judge",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (judge.designation.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = judge.designation,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF0F5132),
                            fontWeight = FontWeight.SemiBold
                        ),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Text(
                            text = "ইভেন্ট: ${judge.assignedCategory}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (judge.phone.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = judge.phone,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                if (judge.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = judge.notes,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        maxLines = 2
                    )
                }
            }
        }
    }
}

/**
 * Dialog to Edit Existing Judge Info
 */
@Composable
fun EditJudgeDialog(
    judge: JudgeEntity,
    language: CampLanguage,
    onDismiss: () -> Unit,
    onSave: (JudgeEntity) -> Unit
) {
    var nameBn by remember { mutableStateOf(judge.nameBn) }
    var nameEn by remember { mutableStateOf(judge.nameEn) }
    var designation by remember { mutableStateOf(judge.designation) }
    var selectedCategory by remember { mutableStateOf(judge.assignedCategory) }
    var phone by remember { mutableStateOf(judge.phone) }
    var notes by remember { mutableStateOf(judge.notes) }

    val categories = listOf(
        "Quran" to "ক্বিরাত ও তাজবীদ",
        "Hadith" to "হাদিস ও দোয়া",
        "Quiz" to "সাধারণ জ্ঞান ও কুইজ",
        "Arts" to "দেয়ালিকা ও রচনা",
        "Speech" to "বক্তৃতা ও কবিতা",
        "Sports" to "শারীরিক সক্ষমতা",
        "General" to "সার্বিক জুরিবোর্ড"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "বিচারকের তথ্য এডিট করুন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = LscSlateDark
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nameBn,
                    onValueChange = { nameBn = it },
                    label = { Text("বিচারকের নাম (বাংলা) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = nameEn,
                    onValueChange = { nameEn = it },
                    label = { Text("Name (English)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = designation,
                    onValueChange = { designation = it },
                    label = { Text("পদবী ও প্রতিষ্ঠান") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "ইভেন্ট / ক্যাটাগরি:", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(categories) { (key, label) ->
                        val isSel = selectedCategory == key
                        Surface(
                            color = if (isSel) Color(0xFF1D4ED8) else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { selectedCategory = key }
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = if (isSel) Color.White else LscSlateDark,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("বিশেষ নোট") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("বাতিল")
                    }
                    Button(
                        onClick = {
                            if (nameBn.isNotBlank()) {
                                onSave(
                                    judge.copy(
                                        nameBn = nameBn.trim(),
                                        nameEn = nameEn.trim(),
                                        designation = designation.trim(),
                                        assignedCategory = selectedCategory,
                                        phone = phone.trim(),
                                        notes = notes.trim()
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text("সংরক্ষণ করুন")
                    }
                }
            }
        }
    }
}

/**
 * Dialog to Add New Judge
 */
@Composable
fun AddJudgeDialog(
    language: CampLanguage,
    onDismiss: () -> Unit,
    onSave: (nameBn: String, nameEn: String, designation: String, category: String, phone: String, notes: String) -> Unit
) {
    var nameBn by remember { mutableStateOf("") }
    var nameEn by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Quran") }
    var phone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "Quran" to (if (language == CampLanguage.BANGLA) "ক্বিরাত ও তাজবীদ" else "Quran"),
        "Hadith" to (if (language == CampLanguage.BANGLA) "হাদিস ও দোয়া" else "Hadith"),
        "Quiz" to (if (language == CampLanguage.BANGLA) "সাধারণ জ্ঞান ও কুইজ" else "Quiz"),
        "Arts" to (if (language == CampLanguage.BANGLA) "দেয়ালিকা ও রচনা" else "Arts"),
        "Speech" to (if (language == CampLanguage.BANGLA) "বক্তৃতা ও কবিতা" else "Speech"),
        "Sports" to (if (language == CampLanguage.BANGLA) "শারীরিক সক্ষমতা" else "Sports"),
        "General" to (if (language == CampLanguage.BANGLA) "সার্বিক জুরিবোর্ড" else "General")
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("add_judge_dialog")
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
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color(0xFF1D4ED8)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "নতুন বিচারক যুক্ত করুন" else "Add New Judge",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = LscSlateDark
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = errorMessage.orEmpty(),
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = nameBn,
                    onValueChange = {
                        nameBn = it
                        errorMessage = null
                    },
                    label = { Text(if (language == CampLanguage.BANGLA) "বিচারকের নাম (বাংলা) *" else "Judge Name (Bangla) *", fontSize = 12.sp) },
                    placeholder = { Text("উদা: মাওলানা আব্দুর রহিম", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("input_judge_name_bn"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nameEn,
                    onValueChange = { nameEn = it },
                    label = { Text(if (language == CampLanguage.BANGLA) "নাম (English - ঐচ্ছিক)" else "Name (English)", fontSize = 12.sp) },
                    placeholder = { Text("e.g. Mawlana Abdur Rahim", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("input_judge_name_en"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = designation,
                    onValueChange = { designation = it },
                    label = { Text(if (language == CampLanguage.BANGLA) "পদবী ও প্রতিষ্ঠান" else "Designation & Institution", fontSize = 12.sp) },
                    placeholder = { Text("উদা: আন্তর্জাতিক ক্বারী / অধ্যাপক", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("input_judge_designation"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (language == CampLanguage.BANGLA) "দায়িত্বপ্রাপ্ত ইভেন্ট/ক্যাটাগরি:" else "Assigned Competition Event:",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(categories) { (key, label) ->
                        val isSel = selectedCategory == key
                        Surface(
                            color = if (isSel) Color(0xFF1D4ED8) else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { selectedCategory = key }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = if (isSel) Color.White else LscSlateDark,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (language == CampLanguage.BANGLA) "মোবাইল নম্বর" else "Contact Phone", fontSize = 12.sp) },
                    placeholder = { Text("017xxxxxxxx", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("input_judge_phone"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (language == CampLanguage.BANGLA) "বিশেষ মন্তব্য / অভিজ্ঞতা" else "Notes / Bio", fontSize = 12.sp) },
                    placeholder = { Text("বিচারকের বিশেষ মূল্যায়ন দায়িত্ব...", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("input_judge_notes"),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (language == CampLanguage.BANGLA) "বাতিল" else "Cancel")
                    }

                    Button(
                        onClick = {
                            if (nameBn.isBlank()) {
                                errorMessage = if (language == CampLanguage.BANGLA)
                                    "দয়া করে বিচারকের নাম (বাংলা) লিখুন।"
                                else
                                    "Please enter judge name (Bangla)."
                            } else {
                                onSave(nameBn, nameEn, designation, selectedCategory, phone, notes)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                        modifier = Modifier.weight(1f).testTag("save_judge_btn")
                    ) {
                        Text(if (language == CampLanguage.BANGLA) "সংরক্ষণ করুন" else "Save Judge")
                    }
                }
            }
        }
    }
}

@Composable
fun GroupLeaderboardCard(group: CampGroupEntity, language: CampLanguage) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(group.colorHex))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == CampLanguage.BANGLA) group.nameBn else group.nameEn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "${group.totalPoints} পয়েন্ট",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = LscEmeraldPrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val progress = (group.totalPoints / 1000f).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(group.colorHex),
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "সদস্য: ${group.memberCount} জন", fontSize = 11.sp, color = Color.Gray)
                Text(text = "দলনেতা: ${group.leaderName}", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ParticipantScoreCard(
    score: CompetitionScoreEntity,
    language: CampLanguage,
    onToggleHandover: (() -> Unit)? = null
) {
    val medalBadge = when (score.rank) {
        1 -> "🥇 ১ম স্থান"
        2 -> "🥈 ২য় স্থান"
        3 -> "🥉 ৩য় স্থান"
        else -> null
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (medalBadge != null) {
                        Text(
                            text = "$medalBadge • ",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color(0xFFB45309)
                        )
                    }
                    Text(
                        text = score.participantName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = LscSlateDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = score.campCode,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${score.groupName} (${score.subgroup})",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                if (score.remarks.isNotBlank()) {
                    Text(
                        text = "মন্তব্য: ${score.remarks}",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = if (score.totalScore >= 90) Color(0xFFFEF3C7) else Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${"%.1f".format(score.totalScore)} / 100",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (score.totalScore >= 90) Color(0xFFB45309) else LscSlateDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (onToggleHandover != null) {
                    Surface(
                        color = if (score.isPrizeHandedOver) Color(0xFFDCFCE7) else Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(
                            1.dp,
                            if (score.isPrizeHandedOver) Color(0xFF86EFAC) else Color(0xFFFDE68A)
                        ),
                        modifier = Modifier.clickable { onToggleHandover() }
                    ) {
                        Text(
                            text = if (score.isPrizeHandedOver) "🎁 হস্তান্তর সম্পন্ন ✓" else "🎁 পুরস্কার দিন",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (score.isPrizeHandedOver) Color(0xFF15803D) else Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (score.isAwardEligible) {
                    Text(
                        text = "পুরস্কার বিজয়ী ★",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LscGoldDark
                    )
                }
            }
        }
    }
}

@Composable
fun JudgeScoreEntryDialog(
    participants: List<ParticipantEntity>,
    competitionId: Long,
    judges: List<JudgeEntity> = emptyList(),
    language: CampLanguage,
    onDismiss: () -> Unit,
    onSubmit: (participant: ParticipantEntity, accuracy: Double, presentation: Double, discipline: Double, remarks: String) -> Unit
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
            val matchQuery = q.isBlank() ||
                    p.nameBn.contains(q, ignoreCase = true) ||
                    p.nameEn.contains(q, ignoreCase = true) ||
                    p.campCode.contains(q, ignoreCase = true) ||
                    p.fileNo.contains(q, ignoreCase = true)
            matchSub && matchQuery
        }.take(80)
    }

    var selectedParticipant by remember(filteredParticipants) { mutableStateOf(filteredParticipants.firstOrNull()) }
    var selectedJudge by remember { mutableStateOf(judges.firstOrNull()?.nameBn ?: "") }
    var accuracy by remember { mutableStateOf(35f) }
    var presentation by remember { mutableStateOf(26f) }
    var discipline by remember { mutableStateOf(28f) }
    var remarks by remember { mutableStateOf("") }

    val total = (accuracy + presentation + discipline).toDouble()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (language == CampLanguage.BANGLA) "বিচারক স্কোর প্রদান ফরম (৬০৯ তালিকা)" else "Judge Score Submission",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = LscSlateDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (judges.isNotEmpty()) {
                    Text(text = "মূল্যায়নকারী বিচারক:", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(judges) { j ->
                            val isSel = j.nameBn == selectedJudge
                            Surface(
                                color = if (isSel) Color(0xFF1D4ED8) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { selectedJudge = j.nameBn }
                            ) {
                                Text(
                                    text = j.nameBn.take(14),
                                    fontSize = 10.sp,
                                    color = if (isSel) Color.White else LscSlateDark,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Subgroup filter + Fast Search so all 609 participants are reachable
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
                    placeholder = { Text("শিক্ষার্থীর নাম বা আইডি খুঁজুন (উদা: Bukhari-1-1)...", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredParticipants, key = { it.id }) { p ->
                        val isSel = p.id == selectedParticipant?.id
                        Surface(
                            color = if (isSel) LscEmeraldPrimary else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { selectedParticipant = p }
                        ) {
                            Text(
                                text = "${p.nameBn.take(12)} (${p.campCode})",
                                fontSize = 11.sp,
                                color = if (isSel) Color.White else LscSlateDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "১. বিশুদ্ধতা ও নির্ভুলতা (সর্বোচ্চ ৪০): ${accuracy.toInt()}", fontSize = 11.sp)
                Slider(value = accuracy, onValueChange = { accuracy = it }, valueRange = 0f..40f)

                Text(text = "২. উপস্থাপনা ও প্রকাশভঙ্গী (সর্বোচ্চ ৩০): ${presentation.toInt()}", fontSize = 11.sp)
                Slider(value = presentation, onValueChange = { presentation = it }, valueRange = 0f..30f)

                Text(text = "৩. শৃঙ্খলা ও আত্মবিশ্বাস (সর্বোচ্চ ৩০): ${discipline.toInt()}", fontSize = 11.sp)
                Slider(value = discipline, onValueChange = { discipline = it }, valueRange = 0f..30f)

                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "মোট স্কোর: ${"%.1f".format(total)} / ১০০",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = LscGoldDark,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    placeholder = { Text("বিচারকের সংক্ষিপ্ত মন্তব্য...", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("বাতিল")
                    }
                    Button(
                        onClick = {
                            selectedParticipant?.let { p ->
                                val fullRemarks = if (selectedJudge.isNotBlank()) "[$selectedJudge] $remarks".trim() else remarks
                                onSubmit(p, accuracy.toDouble(), presentation.toDouble(), discipline.toDouble(), fullRemarks)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("দাখিল করুন")
                    }
                }
            }
        }
    }
}
