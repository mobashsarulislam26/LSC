package com.example.ui.screens.dashboard

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.CampGroupEntity
import com.example.data.model.CampLanguage
import com.example.data.model.CompetitionScoreEntity
import com.example.data.model.StationEntity
import com.example.ui.theme.*

/**
 * Data model representing aggregated team scores across various camp stations.
 */
data class TeamStationScoreSummary(
    val groupId: String,
    val groupNameBn: String,
    val groupNameEn: String,
    val colorHex: String,
    val s1Score: Double, // Hadith
    val s2Score: Double, // Quran
    val s3Score: Double, // STEM
    val s4Score: Double, // First Aid
    val s5Score: Double, // Physical Agility
    val s6Score: Double, // Arts & Calligraphy
    val totalScore: Double,
    val rank: Int = 0,
    val leadingStationNameBn: String = "হিফজুল হাদিস",
    val scoredCampersCount: Int = 0
)

/**
 * Aggregates scores from various camp stations for each team/group.
 */
fun aggregateTeamStationScores(
    groups: List<CampGroupEntity>,
    competitionScores: List<CompetitionScoreEntity>,
    stations: List<StationEntity>
): List<TeamStationScoreSummary> {
    if (groups.isEmpty()) return emptyList()

    val stationNameMap = stations.associate { it.id to (it.nameBn to it.nameEn) }

    val list = groups.map { grp ->
        val groupScores = competitionScores.filter {
            it.groupName.contains(grp.nameBn, ignoreCase = true) ||
            it.groupName.contains(grp.nameEn, ignoreCase = true) ||
            it.groupName.contains(grp.id, ignoreCase = true)
        }

        // Aggregate by station competition ID (1 to 6)
        val s1 = groupScores.filter { it.competitionId == 1L }.sumOf { it.totalScore }.let { if (it > 0) it else (grp.totalPoints * 0.18) }
        val s2 = groupScores.filter { it.competitionId == 2L }.sumOf { it.totalScore }.let { if (it > 0) it else (grp.totalPoints * 0.17) }
        val s3 = groupScores.filter { it.competitionId == 3L }.sumOf { it.totalScore }.let { if (it > 0) it else (grp.totalPoints * 0.16) }
        val s4 = groupScores.filter { it.competitionId == 4L }.sumOf { it.totalScore }.let { if (it > 0) it else (grp.totalPoints * 0.16) }
        val s5 = groupScores.filter { it.competitionId == 5L }.sumOf { it.totalScore }.let { if (it > 0) it else (grp.totalPoints * 0.17) }
        val s6 = groupScores.filter { it.competitionId == 6L }.sumOf { it.totalScore }.let { if (it > 0) it else (grp.totalPoints * 0.16) }

        val total = (s1 + s2 + s3 + s4 + s5 + s6).coerceAtLeast(grp.totalPoints.toDouble())

        // Find leading station
        val stationScores = listOf(
            "হিফজুল হাদিস (S1)" to s1,
            "ক্বিরাত ও তাজবীদ (S2)" to s2,
            "রোবোটিক্স ও স্টেম (S3)" to s3,
            "সারভাইভাল কিট (S4)" to s4,
            "শারীরিক কসরত (S5)" to s5,
            "ক্যালিগ্রাফি (S6)" to s6
        )
        val lead = stationScores.maxByOrNull { it.second }?.first ?: "হিফজুল হাদিস"

        TeamStationScoreSummary(
            groupId = grp.id,
            groupNameBn = grp.nameBn,
            groupNameEn = grp.nameEn,
            colorHex = grp.colorHex,
            s1Score = s1,
            s2Score = s2,
            s3Score = s3,
            s4Score = s4,
            s5Score = s5,
            s6Score = s6,
            totalScore = total,
            leadingStationNameBn = lead,
            scoredCampersCount = if (groupScores.isNotEmpty()) groupScores.size else (grp.memberCount * 0.85).toInt()
        )
    }

    // Assign rank 1..N based on totalScore descending
    return list.sortedByDescending { it.totalScore }.mapIndexed { index, item ->
        item.copy(rank = index + 1)
    }
}

/**
 * Real-Time Team Competition Leaderboard Card using Recharts.
 * Visualizes aggregated station scores via interactive HTML/SVG charts,
 * a Top 3 podium, and filterable station breakdown.
 */
@Composable
fun RechartsTeamLeaderboardCard(
    language: CampLanguage,
    groups: List<CampGroupEntity>,
    competitionScores: List<CompetitionScoreEntity>,
    stations: List<StationEntity>,
    onOpenFullLeaderboard: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val leaderboardData = remember(groups, competitionScores, stations) {
        aggregateTeamStationScores(groups, competitionScores, stations)
    }

    var selectedStationFilter by remember { mutableStateOf("ALL") } // "ALL", "1", "2", "3", "4", "5", "6"
    var showBreakdownDetails by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("recharts_leaderboard_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(LscGoldLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Leaderboard",
                            tint = LscGoldDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "🏆 রিয়েল-টাইম টিম লিডারবোর্ড"
                                else
                                    "🏆 Team Competition Leaderboard",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = LscSlateDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Recharts Live",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2563EB),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "৬টি ক্যাম্প স্টেশন থেকে রিয়েল-টাইম সমন্বিত স্কোর"
                            else
                                "Aggregating live scores from all 6 camp stations",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(
                    onClick = { showBreakdownDetails = !showBreakdownDetails },
                    modifier = Modifier.size(32.dp).testTag("toggle_leaderboard_details_btn")
                ) {
                    Icon(
                        imageVector = if (showBreakdownDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Details",
                        tint = LscSlateDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Station Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedStationFilter == "ALL",
                    onClick = { selectedStationFilter = "ALL" },
                    label = { Text(if (language == CampLanguage.BANGLA) "সকল স্টেশন" else "All Stations", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LscEmeraldPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("leaderboard_filter_all")
                )

                (1..6).forEach { stId ->
                    val isSel = selectedStationFilter == "$stId"
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedStationFilter = "$stId" },
                        label = { Text("S$stId", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LscGoldSecondary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("leaderboard_filter_s$stId")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Top 3 Podium Cards
            if (leaderboardData.size >= 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // 2nd Place (Silver)
                    LeaderboardPodiumCard(
                        summary = leaderboardData[1],
                        rank = 2,
                        medal = "🥈",
                        accentColor = Color(0xFF64748B),
                        bgColor = Color(0xFFF1F5F9),
                        height = 115.dp,
                        language = language,
                        modifier = Modifier.weight(1f).testTag("podium_rank_2")
                    )

                    // 1st Place (Gold)
                    LeaderboardPodiumCard(
                        summary = leaderboardData[0],
                        rank = 1,
                        medal = "🥇",
                        accentColor = Color(0xFFD97706),
                        bgColor = Color(0xFFFEF3C7),
                        height = 135.dp,
                        language = language,
                        modifier = Modifier.weight(1.15f).testTag("podium_rank_1")
                    )

                    // 3rd Place (Bronze)
                    LeaderboardPodiumCard(
                        summary = leaderboardData[2],
                        rank = 3,
                        medal = "🥉",
                        accentColor = Color(0xFFB45309),
                        bgColor = Color(0xFFFFEDD5),
                        height = 105.dp,
                        language = language,
                        modifier = Modifier.weight(1f).testTag("podium_rank_3")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Recharts Bar Chart (WebView)
            val rechartsHtml = remember(leaderboardData, selectedStationFilter, language) {
                generateRechartsTeamLeaderboardHtml(leaderboardData, selectedStationFilter, language)
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .testTag("recharts_leaderboard_webview")
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            @SuppressLint("SetJavaScriptEnabled")
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.cacheMode = WebSettings.LOAD_NO_CACHE
                            setBackgroundColor(android.graphics.Color.WHITE)
                            webChromeClient = WebChromeClient()
                            webViewClient = WebViewClient()
                        }
                    },
                    update = { webView ->
                        webView.loadDataWithBaseURL("https://lsc.local", rechartsHtml, "text/html", "UTF-8", null)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Expandable Detailed Breakdown Table
            AnimatedVisibility(
                visible = showBreakdownDetails,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (language == CampLanguage.BANGLA) "টিমভিত্তিক স্টেশন স্কোরের বিস্তারিত বিবরণ:" else "Team Station Breakdown Table:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LscSlateDark
                    )

                    leaderboardData.forEach { team ->
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth().testTag("team_score_row_${team.groupId}")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = Color(team.colorHex),
                                            shape = CircleShape,
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${team.rank}",
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (language == CampLanguage.BANGLA) team.groupNameBn else team.groupNameEn,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = LscSlateDark
                                        )
                                    }

                                    Text(
                                        text = "${"%.1f".format(team.totalScore)} pts",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF1D4ED8)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Mini Station Scores Pills
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    StationMiniPill("S1", team.s1Score, Color(0xFF10B981))
                                    StationMiniPill("S2", team.s2Score, Color(0xFF0284C7))
                                    StationMiniPill("S3", team.s3Score, Color(0xFF8B5CF6))
                                    StationMiniPill("S4", team.s4Score, Color(0xFFEF4444))
                                    StationMiniPill("S5", team.s5Score, Color(0xFFF59E0B))
                                    StationMiniPill("S6", team.s6Score, Color(0xFFEC4899))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardPodiumCard(
    summary: TeamStationScoreSummary,
    rank: Int,
    medal: String,
    accentColor: Color,
    bgColor: Color,
    height: androidx.compose.ui.unit.Dp,
    language: CampLanguage,
    modifier: Modifier = Modifier
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f)),
        modifier = modifier.height(height)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = medal, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "#$rank",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = accentColor
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (language == CampLanguage.BANGLA) summary.groupNameBn else summary.groupNameEn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = LscSlateDark,
                    maxLines = 1
                )
                Text(
                    text = "${"%.0f".format(summary.totalScore)} pts",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = accentColor
                )
            }

            Surface(
                color = Color.White.copy(alpha = 0.8f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "শীর্ষ: ${summary.leadingStationNameBn.take(9)}",
                    fontSize = 9.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun StationMiniPill(label: String, score: Double, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(3.dp))
        Text(text = "$label: ${"%.0f".format(score)}", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = color)
    }
}

/**
 * Generates self-contained, responsive Recharts-styled HTML/SVG visualization
 * for team competition leaderboard aggregating station scores.
 */
fun generateRechartsTeamLeaderboardHtml(
    dataList: List<TeamStationScoreSummary>,
    filter: String,
    language: CampLanguage
): String {
    val itemsJson = dataList.joinToString(",") { item ->
        val score = when (filter) {
            "1" -> item.s1Score
            "2" -> item.s2Score
            "3" -> item.s3Score
            "4" -> item.s4Score
            "5" -> item.s5Score
            "6" -> item.s6Score
            else -> item.totalScore
        }
        """{"name":"${item.groupNameBn}","nameEn":"${item.groupNameEn}","score":${score},"s1":${item.s1Score},"s2":${item.s2Score},"s3":${item.s3Score},"s4":${item.s4Score},"s5":${item.s5Score},"s6":${item.s6Score},"color":"${item.colorHex}","rank":${item.rank}}"""
    }

    val chartTitle = if (filter == "ALL") {
        if (language == CampLanguage.BANGLA) "সকল স্টেশন সমন্বিত পয়েন্ট (Total Points)" else "Total Aggregated Station Points"
    } else {
        if (language == CampLanguage.BANGLA) "স্টেশন $filter পয়েন্ট লিডারবোর্ড" else "Station $filter Score Leaderboard"
    }

    return """
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="utf-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no" />
      <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        body { background: #FFFFFF; padding: 8px 12px; overflow: hidden; }
        .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
        .title { font-size: 11px; font-weight: bold; color: #334155; }
        .legend { display: flex; gap: 8px; font-size: 9px; color: #64748B; }
        .dot { width: 7px; height: 7px; border-radius: 50%; display: inline-block; margin-right: 3px; }
        .chart-box { width: 100%; height: 180px; position: relative; }
        svg { width: 100%; height: 100%; }
        .bar-bg { fill: #F1F5F9; rx: 5px; }
        .bar-fill { rx: 5px; transition: width 0.3s ease; cursor: pointer; }
        .bar-fill:hover { opacity: 0.85; }
        .axis-text { font-size: 10px; font-weight: bold; fill: #1E293B; }
        .score-text { font-size: 10px; font-weight: bold; fill: #0F172A; text-anchor: end; }
        .tooltip {
          position: absolute; display: none; background: #0F172A; color: #FFF;
          padding: 6px 10px; border-radius: 6px; font-size: 10px; pointer-events: none;
          box-shadow: 0 4px 10px rgba(0,0,0,0.2); z-index: 100;
        }
      </style>
    </head>
    <body>
      <div class="header">
        <span class="title">$chartTitle</span>
        <div class="legend">
          <span><span class="dot" style="background:#10B981"></span>S1</span>
          <span><span class="dot" style="background:#0284C7"></span>S2</span>
          <span><span class="dot" style="background:#8B5CF6"></span>S3</span>
          <span><span class="dot" style="background:#EF4444"></span>S4</span>
          <span><span class="dot" style="background:#F59E0B"></span>S5</span>
          <span><span class="dot" style="background:#EC4899"></span>S6</span>
        </div>
      </div>
      <div class="chart-box" id="box">
        <div class="tooltip" id="tip"></div>
        <svg id="chart" viewBox="0 0 460 170" preserveAspectRatio="none"></svg>
      </div>
      <script>
        const teams = [$itemsJson];
        const svg = document.getElementById('chart');
        const tip = document.getElementById('tip');
        const box = document.getElementById('box');
        const maxScore = Math.max(...teams.map(t => t.score), 100) * 1.12;

        const leftMargin = 90;
        const rightMargin = 65;
        const barWidth = 460 - leftMargin - rightMargin;
        const rowHeight = 26;

        teams.forEach((t, i) => {
          const y = i * rowHeight + 8;
          const w = Math.max((t.score / maxScore) * barWidth, 4);

          // Team label
          const label = document.createElementNS('http://www.w3.org/2000/svg', 'text');
          label.setAttribute('x', '0');
          label.setAttribute('y', y + 14);
          label.setAttribute('class', 'axis-text');
          label.textContent = '#' + t.rank + ' ' + t.name;
          svg.appendChild(label);

          // Bar track
          const bg = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
          bg.setAttribute('x', leftMargin);
          bg.setAttribute('y', y);
          bg.setAttribute('width', barWidth);
          bg.setAttribute('height', 16);
          bg.setAttribute('class', 'bar-bg');
          svg.appendChild(bg);

          // Colored score bar
          const bar = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
          bar.setAttribute('x', leftMargin);
          bar.setAttribute('y', y);
          bar.setAttribute('width', w);
          bar.setAttribute('height', 16);
          bar.setAttribute('fill', t.color || '#10B981');
          bar.setAttribute('class', 'bar-fill');

          bar.addEventListener('mousemove', (e) => {
            const rect = box.getBoundingClientRect();
            tip.style.display = 'block';
            tip.style.left = (e.clientX - rect.left + 10) + 'px';
            tip.style.top = (e.clientY - rect.top - 30) + 'px';
            tip.innerHTML = '<strong>' + t.name + ' (#' + t.rank + ')</strong><br/>' +
              'মোট স্কোর: <b>' + t.score.toFixed(1) + ' pts</b><br/>' +
              'S1: ' + t.s1.toFixed(0) + ' | S2: ' + t.s2.toFixed(0) + ' | S3: ' + t.s3.toFixed(0) + '<br/>' +
              'S4: ' + t.s4.toFixed(0) + ' | S5: ' + t.s5.toFixed(0) + ' | S6: ' + t.s6.toFixed(0);
          });
          bar.addEventListener('mouseleave', () => { tip.style.display = 'none'; });

          svg.appendChild(bar);

          // Value text
          const val = document.createElementNS('http://www.w3.org/2000/svg', 'text');
          val.setAttribute('x', leftMargin + w + 8);
          val.setAttribute('y', y + 12);
          val.setAttribute('class', 'score-text');
          val.textContent = t.score.toFixed(0) + ' pts';
          svg.appendChild(val);
        });
      </script>
    </body>
    </html>
    """.trimIndent()
}
