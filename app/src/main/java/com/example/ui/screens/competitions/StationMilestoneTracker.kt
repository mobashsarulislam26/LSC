package com.example.ui.screens.competitions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.sample.SeedData
import com.example.ui.theme.*

/**
 * Model representing a competition milestone at a specific camp station.
 */
data class StationCompetitionMilestone(
    val stationId: Int,
    val stationNameBn: String,
    val stationNameEn: String,
    val moderatorName: String,
    val location: String,
    val competitionId: Long,
    val milestoneTitleBn: String,
    val milestoneTitleEn: String,
    val category: String,
    val maxScore: Int = 100,
    val isCompleted: Boolean = false,
    val scoreRecord: CompetitionScoreEntity? = null
)

/**
 * Aggregated participant progress across all station milestones.
 */
data class ParticipantMilestoneProgress(
    val participant: ParticipantEntity,
    val totalMilestones: Int,
    val completedCount: Int,
    val pendingCount: Int,
    val completionPercentage: Float,
    val totalScoreEarned: Double,
    val milestones: List<StationCompetitionMilestone>
)

/**
 * Helper to derive station milestones and completion progress for a participant.
 */
fun deriveParticipantStationMilestones(
    participant: ParticipantEntity,
    competitions: List<CompetitionEntity>,
    competitionScores: List<CompetitionScoreEntity>,
    stations: List<StationEntity>
): ParticipantMilestoneProgress {
    val activeStations = if (stations.isNotEmpty()) stations else SeedData.stations
    val activeCompetitions = if (competitions.isNotEmpty()) competitions else SeedData.competitions

    // Participant's scored entries
    val participantScores = competitionScores.filter {
        it.participantId == participant.id || it.campCode.equals(participant.campCode, ignoreCase = true)
    }

    // Map each station 1..6 to its primary competition challenge
    val milestones = activeStations.take(6).mapIndexed { idx, stn ->
        val comp = activeCompetitions.getOrNull(idx) ?: activeCompetitions.first()
        val score = participantScores.find { it.competitionId == comp.id }
        val isCompleted = score != null

        StationCompetitionMilestone(
            stationId = stn.id,
            stationNameBn = stn.nameBn,
            stationNameEn = stn.nameEn,
            moderatorName = stn.moderatorName,
            location = stn.location,
            competitionId = comp.id,
            milestoneTitleBn = comp.nameBn,
            milestoneTitleEn = comp.nameEn,
            category = comp.category,
            maxScore = comp.maxScore,
            isCompleted = isCompleted,
            scoreRecord = score
        )
    }

    val total = milestones.size
    val completed = milestones.count { it.isCompleted }
    val pending = total - completed
    val pct = if (total > 0) (completed.toFloat() / total) * 100f else 0f
    val totalScore = milestones.mapNotNull { it.scoreRecord?.totalScore }.sum()

    return ParticipantMilestoneProgress(
        participant = participant,
        totalMilestones = total,
        completedCount = completed,
        pendingCount = pending,
        completionPercentage = pct,
        totalScoreEarned = totalScore,
        milestones = milestones
    )
}

@Composable
fun StationMilestoneTracker(
    language: CampLanguage,
    participants: List<ParticipantEntity>,
    competitions: List<CompetitionEntity>,
    competitionScores: List<CompetitionScoreEntity>,
    stations: List<StationEntity>,
    selectedParticipant: ParticipantEntity?,
    onSelectParticipant: (ParticipantEntity) -> Unit,
    onScoreMilestoneClick: (StationCompetitionMilestone) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var filterStatus by remember { mutableStateOf("ALL") } // "ALL", "COMPLETED", "PENDING"
    var showParticipantPicker by remember { mutableStateOf(false) }

    // Active participant: either passed from outside or defaults to first participant
    val activeParticipant = remember(selectedParticipant, participants) {
        selectedParticipant ?: participants.firstOrNull() ?: SeedData.generateParticipants().first()
    }

    val progress = remember(activeParticipant, competitions, competitionScores, stations) {
        deriveParticipantStationMilestones(activeParticipant, competitions, competitionScores, stations)
    }

    val displayedMilestones = remember(progress, filterStatus) {
        when (filterStatus) {
            "COMPLETED" -> progress.milestones.filter { it.isCompleted }
            "PENDING" -> progress.milestones.filter { !it.isCompleted }
            else -> progress.milestones
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("station_milestone_tracker"),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Participant Context & Selector Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("milestone_participant_banner")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF7C3AED)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = activeParticipant.nameBn.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = activeParticipant.nameBn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = LscSlateDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFFF5F3FF),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(1.dp, Color(0xFFDDD6FE))
                                    ) {
                                        Text(
                                            text = activeParticipant.campCode,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF6D28D9),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${activeParticipant.groupNameBn} (${activeParticipant.groupNameEn}) • ${activeParticipant.subgroup}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { showParticipantPicker = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6D28D9)),
                            border = BorderStroke(1.dp, Color(0xFFDDD6FE)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("milestone_switch_participant_btn")
                        ) {
                            Icon(Icons.Default.PeopleAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA) "অন্যান্য" else "Switch",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Visual Progress Metric Card with Progress Bar
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("milestone_progress_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "স্টেশন প্রতিযোগিতা মাইলস্টোন অগ্রগতি"
                                else
                                    "Station Milestone Completion Progress",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF581C87)
                            )
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "৬টি নির্ধারিত স্টেশনে সম্পন্ন প্রতিযোগিতার মাইলফলক"
                                else
                                    "Milestones completed across 6 assigned camp stations",
                                fontSize = 11.sp,
                                color = Color(0xFF7E22CE)
                            )
                        }

                        // Percentage Badge
                        Surface(
                            color = if (progress.completionPercentage >= 80f) Color(0xFF10B981) else Color(0xFF7C3AED),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "${progress.completionPercentage.toInt()}%",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Linear Progress Bar
                    LinearProgressIndicator(
                        progress = { progress.completionPercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .testTag("milestone_linear_progress"),
                        color = Color(0xFF7C3AED),
                        trackColor = Color(0xFFE9D5FF)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Metric Counters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MilestoneStatBox(
                            title = if (language == CampLanguage.BANGLA) "সম্পন্ন" else "Completed",
                            value = "${progress.completedCount}/${progress.totalMilestones}",
                            icon = Icons.Default.CheckCircle,
                            color = Color(0xFF16A34A),
                            modifier = Modifier.weight(1f)
                        )

                        MilestoneStatBox(
                            title = if (language == CampLanguage.BANGLA) "বাকি আছে" else "Pending",
                            value = "${progress.pendingCount}",
                            icon = Icons.Default.Pending,
                            color = Color(0xFFD97706),
                            modifier = Modifier.weight(1f)
                        )

                        MilestoneStatBox(
                            title = if (language == CampLanguage.BANGLA) "মোট স্কোর" else "Points",
                            value = "${progress.totalScoreEarned.toInt()} pts",
                            icon = Icons.Default.EmojiEvents,
                            color = Color(0xFF7C3AED),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Filter Chips Row (All, Completed, Pending)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = filterStatus == "ALL",
                    onClick = { filterStatus = "ALL" },
                    label = { Text(if (language == CampLanguage.BANGLA) "সকল মাইলস্টোন (${progress.totalMilestones})" else "All (${progress.totalMilestones})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF7C3AED),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).testTag("filter_milestones_all")
                )

                FilterChip(
                    selected = filterStatus == "COMPLETED",
                    onClick = { filterStatus = "COMPLETED" },
                    label = { Text(if (language == CampLanguage.BANGLA) "সম্পন্ন (${progress.completedCount})" else "Completed (${progress.completedCount})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF16A34A),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).testTag("filter_milestones_completed")
                )

                FilterChip(
                    selected = filterStatus == "PENDING",
                    onClick = { filterStatus = "PENDING" },
                    label = { Text(if (language == CampLanguage.BANGLA) "বাকি (${progress.pendingCount})" else "Pending (${progress.pendingCount})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFD97706),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).testTag("filter_milestones_pending")
                )
            }
        }

        // Milestone Cards List
        if (displayedMilestones.isEmpty()) {
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "এই ক্যাটাগরিতে কোনো মাইলস্টোন নেই" else "No milestones found in this category",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(displayedMilestones, key = { it.stationId }) { milestone ->
                StationMilestoneCard(
                    milestone = milestone,
                    language = language,
                    onScoreClick = { onScoreMilestoneClick(milestone) }
                )
            }
        }
    }

    if (showParticipantPicker) {
        MilestoneParticipantPickerDialog(
            language = language,
            participants = participants,
            currentSelected = activeParticipant,
            onDismiss = { showParticipantPicker = false },
            onSelect = { p ->
                onSelectParticipant(p)
                showParticipantPicker = false
            }
        )
    }
}

@Composable
fun StationMilestoneCard(
    milestone: StationCompetitionMilestone,
    language: CampLanguage,
    onScoreClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isCompleted = milestone.isCompleted

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFFF0FDF4) else Color.White
        ),
        border = BorderStroke(
            1.dp,
            if (isCompleted) Color(0xFF86EFAC) else Color(0xFFE2E8F0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("station_milestone_card_${milestone.stationId}")
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Station Badge, Status Pill, and Expand Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = when (milestone.stationId) {
                            1 -> Color(0xFF1D4ED8)
                            2 -> Color(0xFF0284C7)
                            3 -> Color(0xFF7C3AED)
                            4 -> Color(0xFF059669)
                            5 -> Color(0xFFD97706)
                            else -> Color(0xFFDC2626)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "স্টেশন ${milestone.stationId}",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = milestone.category,
                            color = Color(0xFF475569),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Completed vs Pending Status Badge
                Surface(
                    color = if (isCompleted) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isCompleted) Color(0xFF86EFAC) else Color(0xFFFDE68A))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                            contentDescription = null,
                            tint = if (isCompleted) Color(0xFF16A34A) else Color(0xFFD97706),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isCompleted) {
                                if (language == CampLanguage.BANGLA) "সম্পন্ন" else "Completed"
                            } else {
                                if (language == CampLanguage.BANGLA) "অপেক্ষমান" else "Pending"
                            },
                            color = if (isCompleted) Color(0xFF15803D) else Color(0xFFB45309),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Milestone Competition Title
            Text(
                text = if (language == CampLanguage.BANGLA) milestone.milestoneTitleBn else milestone.milestoneTitleEn,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = LscSlateDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Station Name & Venue
            Text(
                text = "${if (language == CampLanguage.BANGLA) milestone.stationNameBn else milestone.stationNameEn} • ${milestone.location}",
                fontSize = 11.sp,
                color = Color.Gray
            )

            // Score Banner (If Completed)
            if (isCompleted && milestone.scoreRecord != null) {
                val sc = milestone.scoreRecord
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = LscGoldSecondary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "প্রাপ্ত মোট স্কোর" else "Score Earned",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "${sc.totalScore.toInt()}/100",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }

                        if (sc.rank > 0) {
                            Surface(
                                color = if (sc.rank == 1) LscGoldLight else Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "স্থান: ${sc.rank}ম" else "Rank: #${sc.rank}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sc.rank == 1) LscGoldDark else Color(0xFF1D4ED8),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Expandable details (Breakdown & Remarks)
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "মডারেটর: ${milestone.moderatorName}",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )

                    if (isCompleted && milestone.scoreRecord != null) {
                        val sc = milestone.scoreRecord
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "নির্ভুলতা: ${sc.accuracyScore.toInt()}/40", fontSize = 11.sp, color = Color.DarkGray)
                            Text(text = "উপস্থাপনা: ${sc.presentationScore.toInt()}/30", fontSize = 11.sp, color = Color.DarkGray)
                            Text(text = "শৃঙ্খলা: ${sc.disciplineScore.toInt()}/30", fontSize = 11.sp, color = Color.DarkGray)
                        }

                        if (sc.remarks.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "মন্তব্য: ${sc.remarks}",
                                fontSize = 11.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "এই স্টেশনে রোটেশনের সময় অংশগ্রহণকারী এই প্রতিযোগিতার চ্যালেঞ্জে অংশ নেবেন।"
                            else
                                "Participant will complete this challenge during rotation at this station.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MilestoneStatBox(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LscSlateDark)
            Text(text = title, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun MilestoneParticipantPickerDialog(
    language: CampLanguage,
    participants: List<ParticipantEntity>,
    currentSelected: ParticipantEntity,
    onDismiss: () -> Unit,
    onSelect: (ParticipantEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(participants, searchQuery) {
        val q = searchQuery.trim()
        if (q.isBlank()) {
            participants.take(30)
        } else {
            participants.filter {
                it.nameBn.contains(q, ignoreCase = true) ||
                        it.nameEn.contains(q, ignoreCase = true) ||
                        it.campCode.contains(q, ignoreCase = true) ||
                        it.groupNameBn.contains(q, ignoreCase = true)
            }.take(30)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (language == CampLanguage.BANGLA) "অংশগ্রহণকারী নির্বাচন" else "Select Participant",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(if (language == CampLanguage.BANGLA) "নাম বা ক্যাম্প কোড..." else "Search...", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.heightIn(max = 260.dp)) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(filtered) { p ->
                            val isSel = p.id == currentSelected.id
                            Surface(
                                color = if (isSel) Color(0xFFF3E8FF) else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSel) Color(0xFF7C3AED) else Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(p) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = p.nameBn, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "${p.campCode} • ${p.groupNameBn}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    if (isSel) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == CampLanguage.BANGLA) "বাতিল" else "Cancel")
            }
        }
    )
}
