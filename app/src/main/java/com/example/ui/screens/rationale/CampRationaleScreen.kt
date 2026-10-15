package com.example.ui.screens.rationale

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampLanguage
import com.example.data.model.CampRationaleData
import com.example.data.model.CampRationalePillar
import com.example.data.model.ExpectedOutcomeMetric
import com.example.ui.theme.*

@Composable
fun CampRationaleScreen(
    language: CampLanguage,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isBangla = language == CampLanguage.BANGLA
    val pillars = remember { CampRationaleData.pillars }
    val outcomes = remember { CampRationaleData.expectedOutcomes }

    var expandedPillarId by remember { mutableStateOf<Int?>(1) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("rationale_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(LscEmeraldLight, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = LscEmeraldPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBangla) "ক্যাম্প রূপরেখা ও ফলাফল" else "Camp Rationale & Outcomes",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = LscSlateDark
                    )
                    Text(
                        text = if (isBangla) "মূল দর্শন, ৫টি স্তম্ভ ও প্রত্যাশিত ফলাফল (পৃ. ৩৪)" else "Vision, 5 Core Pillars & Impact Assessment",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Theme Banner
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF064E3B), Color(0xFF047857), Color(0xFF0F766E))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (isBangla) "ক্যাম্প মূল প্রতিপাদ্য" else "Core Camp Theme",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "“${if (isBangla) CampRationaleData.themeBn else CampRationaleData.themeEn}”",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFDE68A),
                                lineHeight = 28.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = CampRationaleData.rationaleDescriptionBn,
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Expected Outcomes Matrix (Page 34)
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = LscEmeraldPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBangla) "সমাপ্তি ও প্রত্যাশিত ফলাফল (পৃ. ৩৪ - Outcome Matrix)" else "Expected Outcomes & Target Metrics (P. 34)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = LscSlateDark
                    )
                }
            }

            items(outcomes, key = { it.id }) { metric ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBangla) metric.titleBn else metric.titleEn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = LscSlateDark
                                )
                                Text(
                                    text = metric.descriptionBn,
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    lineHeight = 15.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = if (metric.currentPercentage >= metric.targetPercentage) Color(0xFFDCFCE7) else Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "${metric.currentPercentage}%",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = if (metric.currentPercentage >= metric.targetPercentage) Color(0xFF15803D) else Color(0xFF1D4ED8),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { metric.currentPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (metric.currentPercentage >= metric.targetPercentage) LscEmeraldPrimary else Color(0xFF2563EB),
                            trackColor = Color(0xFFF1F5F9)
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "লক্ষ্যমাত্রা: ${metric.targetPercentage}%", fontSize = 10.sp, color = Color.Gray)
                            Text(text = "বিভাগ: ${metric.category}", fontSize = 10.sp, color = LscEmeraldPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 5 Core Pillars Section
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBangla) "ক্যাম্পের ৫টি মূল শিখন স্তম্ভ (5 Core Learning Pillars)" else "5 Core Learning Pillars",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = LscSlateDark
                    )
                }
            }

            items(pillars, key = { it.id }) { pillar ->
                val isExpanded = expandedPillarId == pillar.id

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 3.dp else 1.dp),
                    border = BorderStroke(
                        width = if (isExpanded) 1.5.dp else 1.dp,
                        color = if (isExpanded) LscEmeraldPrimary else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedPillarId = if (isExpanded) null else pillar.id }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(if (isExpanded) LscEmeraldPrimary else LscEmeraldLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${pillar.id}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isExpanded) Color.White else LscEmeraldPrimary,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBangla) pillar.titleBn else pillar.titleEn,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = LscSlateDark
                                )
                                Text(
                                    text = pillar.subtitleBn,
                                    fontSize = 11.sp,
                                    color = LscEmeraldPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                Divider(color = Color(0xFFF1F5F9))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = pillar.descriptionBn,
                                    fontSize = 12.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 17.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = Color(0xFFF0FDF4),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "নির্দিষ্ট অর্জন লক্ষ্যমাত্রা:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        pillar.targetsBn.forEach { t ->
                                            Row(
                                                modifier = Modifier.padding(vertical = 2.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Text("✓ ", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                Text(text = t, fontSize = 11.sp, color = Color(0xFF166534))
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
}
