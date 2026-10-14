package com.example.ui.screens.competitions

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
    onSubmitScore: (competitionId: Long, participant: ParticipantEntity, accuracy: Double, pres: Double, disc: Double, remarks: String) -> Unit
) {
    var selectedTab by remember { mutableStateOf("LEADERBOARD") } // "LEADERBOARD", "MILESTONES", "JUDGING", "JUDGES"
    var selectedCompId by remember { mutableStateOf(competitions.firstOrNull()?.id ?: 1L) }
    var showScoreDialog by remember { mutableStateOf(false) }
    var showAddJudgeDialog by remember { mutableStateOf(false) }
    var showBatchImportDialog by remember { mutableStateOf(false) }
    var judgeToEdit by remember { mutableStateOf<JudgeEntity?>(null) }
    var judgeToDelete by remember { mutableStateOf<JudgeEntity?>(null) }
    var judgeCategoryFilter by remember { mutableStateOf("ALL") }

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
                        Text(if (language == CampLanguage.BANGLA) "মাইলস্টোন" else "Milestones", fontSize = 10.sp)
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF7C3AED),
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(1.1f).testTag("tab_milestones")
            )

            FilterChip(
                selected = selectedTab == "JUDGING",
                onClick = { selectedTab = "JUDGING" },
                label = { Text(if (language == CampLanguage.BANGLA) "স্কোর" else "Scoring", fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LscEmeraldPrimary,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(0.9f).testTag("tab_judging")
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
                modifier = Modifier.weight(0.9f).testTag("tab_judges")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            "LEADERBOARD" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "গ্রুপ সার্বিক পয়েন্ট তালিকা" else "Group Overall Standings",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                    }

                    items(groups.sortedByDescending { it.totalPoints }) { grp ->
                        GroupLeaderboardCard(group = grp, language = language)
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "সেরা পারফর্মার ও অ্যাওয়ার্ড তালিকা" else "Top Participants & Award Winners",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                    }

                    items(competitionScores.sortedByDescending { it.totalScore }) { score ->
                        ParticipantScoreCard(score = score, language = language)
                    }
                }
            }

            "JUDGING" -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(competitions) { comp ->
                            val isSelected = comp.id == selectedCompId
                            Surface(
                                color = if (isSelected) LscEmeraldPrimary else Color.White,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) LscEmeraldPrimary else Color(0xFFCBD5E1)),
                                modifier = Modifier.clickable { selectedCompId = comp.id }
                            ) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) comp.nameBn else comp.nameEn,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else LscSlateDark,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showScoreDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("open_score_entry_btn")
                    ) {
                        Icon(Icons.Default.AddModerator, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (language == CampLanguage.BANGLA) "নতুন স্কোর এন্ট্রি করুন" else "Enter New Score")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (language == CampLanguage.BANGLA) "এই প্রতিযোগিতার জমাকৃত স্কোরসমূহ:" else "Submitted Scores for this Event:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val compScores = competitionScores.filter { it.competitionId == selectedCompId }
                    if (compScores.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (language == CampLanguage.BANGLA) "এখনও কোনো স্কোর জমা দেওয়া হয়নি।" else "No scores submitted yet.",
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
                            items(compScores) { s ->
                                ParticipantScoreCard(score = s, language = language)
                            }
                        }
                    }
                }
            }

            "JUDGES" -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Bar with "+ নতুন বিচারক" & "এক্সেল আপলোড"
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
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

                    // Category Filter Chips
                    val categories = listOf(
                        "ALL" to (if (language == CampLanguage.BANGLA) "সকল বিচারক" else "All Judges"),
                        "Quran" to (if (language == CampLanguage.BANGLA) "ক্বিরাত ও তাজবীদ" else "Quran"),
                        "Hadith" to (if (language == CampLanguage.BANGLA) "হাদিস মুখস্থ" else "Hadith"),
                        "Quiz" to (if (language == CampLanguage.BANGLA) "সাধারণ জ্ঞান ও কুইজ" else "Quiz"),
                        "Arts" to (if (language == CampLanguage.BANGLA) "ক্যালিগ্রাফি" else "Arts"),
                        "Speech" to (if (language == CampLanguage.BANGLA) "বক্তৃতা ও বিতর্ক" else "Speech"),
                        "Sports" to (if (language == CampLanguage.BANGLA) "ক্রীড়া ও স্কিল" else "Sports")
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
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) Color(0xFF1D4ED8) else Color(0xFFE2E8F0)),
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
    if (judgeToEdit != null) {
        EditJudgeDialog(
            judge = judgeToEdit!!,
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

    // Modal Dialog: Submit Judge Score
    if (showScoreDialog) {
        JudgeScoreEntryDialog(
            participants = participants.take(30),
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
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
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
        "Hadith" to "হাদিস মুখস্থ",
        "Quiz" to "সাধারণ জ্ঞান ও কুইজ",
        "Arts" to "ক্যালিগ্রাফি",
        "Speech" to "বক্তৃতা ও বিতর্ক",
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
        "Hadith" to (if (language == CampLanguage.BANGLA) "হাদিস মুখস্থ" else "Hadith"),
        "Quiz" to (if (language == CampLanguage.BANGLA) "সাধারণ জ্ঞান ও কুইজ" else "Quiz"),
        "Arts" to (if (language == CampLanguage.BANGLA) "ক্যালিগ্রাফি" else "Arts"),
        "Speech" to (if (language == CampLanguage.BANGLA) "বক্তৃতা ও বিতর্ক" else "Speech"),
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
                // Header
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

                // Error Banner
                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                // Inputs
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
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(group.colorHex))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${group.nameBn} (${group.nameEn})",
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
fun ParticipantScoreCard(score: CompetitionScoreEntity, language: CampLanguage) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                        fontSize = 13.sp,
                        color = if (score.totalScore >= 90) Color(0xFFB45309) else LscSlateDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (score.isAwardEligible) {
                    Text(
                        text = "মেডেল বিজয়ী ★",
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
    var selectedParticipant by remember { mutableStateOf(participants.firstOrNull()) }
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
                    .padding(18.dp)
            ) {
                Text(
                    text = if (language == CampLanguage.BANGLA) "বিচারক স্কোর প্রদান ফরম" else "Judge Score Submission",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = LscSlateDark
                )

                Spacer(modifier = Modifier.height(10.dp))

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
                                    text = j.nameBn.take(12),
                                    fontSize = 10.sp,
                                    color = if (isSel) Color.White else LscSlateDark,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(text = "অংশগ্রহণকারী নির্বাচন:", fontSize = 11.sp, color = Color.Gray)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(participants) { p ->
                        val isSel = p.id == selectedParticipant?.id
                        Surface(
                            color = if (isSel) LscEmeraldPrimary else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { selectedParticipant = p }
                        ) {
                            Text(
                                text = "${p.nameBn.take(10)} (${p.campCode})",
                                fontSize = 11.sp,
                                color = if (isSel) Color.White else LscSlateDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    placeholder = { Text("বিচারকের সংক্ষিপ্ত মন্তব্য...", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

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
