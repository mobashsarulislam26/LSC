package com.example.ui.screens.dashboard

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.model.CampLanguage
import com.example.data.model.MealDistributionEntity
import com.example.data.model.ParticipantEntity
import com.example.ui.theme.LscEmeraldPrimary
import com.example.ui.theme.LscSlateDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MealHeadcountMetrics(
    val mealType: String,
    val mealTitleBn: String,
    val mealTitleEn: String,
    val dayNo: Int,
    val targetCount: Int,
    val servedCount: Int,
    val remainingCount: Int,
    val servingPercentage: Float,
    val specialDietTargetCount: Int,
    val specialDietServedCount: Int,
    val wastageRiskLevel: String, // "OPTIMAL", "PREP_CAUTION", "SURPLUS_RISK"
    val cateringAdvisoryBn: String,
    val cateringAdvisoryEn: String
)

fun deriveMealHeadcountMetrics(
    mealType: String,
    dayNo: Int,
    participants: List<ParticipantEntity>,
    mealDistributions: List<MealDistributionEntity>
): MealHeadcountMetrics {
    val totalTarget = participants.size.coerceAtLeast(1)
    val claimedRecords = mealDistributions.filter {
        it.dayNo == dayNo && it.mealType.equals(mealType, ignoreCase = true)
    }
    val servedCount = claimedRecords.size
    val remainingCount = (totalTarget - servedCount).coerceAtLeast(0)
    val servingPercentage = (servedCount.toFloat() / totalTarget.toFloat()) * 100f

    val specialDietParticipants = participants.filter {
        it.mealPref.isNotBlank() && it.mealPref != "নিয়মিত" && it.mealPref != "Regular"
    }
    val specialDietTargetCount = specialDietParticipants.size
    val specialDietIds = specialDietParticipants.map { it.id }.toSet()
    val specialDietServedCount = claimedRecords.count { specialDietIds.contains(it.participantId) }

    val remainingBn = remainingCount.toString().map {
        when (it) {
            '0' -> '০'; '1' -> '১'; '2' -> '২'; '3' -> '৩'; '4' -> '৪'
            '5' -> '৫'; '6' -> '৬'; '7' -> '৭'; '8' -> '৮'; '9' -> '৯'
            else -> it
        }
    }.joinToString("")

    val (riskLevel, advBn, advEn) = when {
        servingPercentage >= 95f -> Triple(
            "OPTIMAL",
            "চমৎকার! ৯৫%+ খাবার বিতরণ সম্পন্ন হয়েছে। অতিরিক্ত রান্না শূন্যে রেখে খাবার অপচয় পুরোপুরি রোধ করা সম্ভব হয়েছে।",
            "Optimal! 95%+ meals served. Food wastage minimized to near zero."
        )
        remainingCount in 1..80 -> Triple(
            "PREP_CAUTION",
            "ক্যাটারিং অ্যালার্ট: আর মাত্র $remainingBn জন বাকি আছেন। রান্নাঘরে শেষ ব্যাচের অতিরিক্ত রান্না বন্ধ রাখুন এবং প্রস্তুতকৃত খাবার ধীরে বণ্টন করুন যাতে অপচয় না ঘটে।",
            "Catering Alert: Only $remainingCount participants remaining. Cease surplus batch prep to prevent food wastage."
        )
        else -> Triple(
            "NORMAL_PACING",
            "খাবার পরিবেশন স্বাভাবিক গতিতে চলমান। বিশেষ ডায়েট ও অ্যালার্জিভুক্ত ${specialDietTargetCount - specialDietServedCount} জনের প্যাকেট সুরক্ষিত রাখুন।",
            "Meal serving in progress. Keep ${specialDietTargetCount - specialDietServedCount} special diet/allergy plates reserved."
        )
    }

    val (titleBn, titleEn) = when (mealType.uppercase()) {
        "BREAKFAST" -> "সকালের নাস্তা (Breakfast)" to "Breakfast Session"
        "SNACKS" -> "বিকেলের নাস্তা (Snacks)" to "Evening Snacks"
        "DINNER" -> "রাতের খাবার (Dinner)" to "Dinner Session"
        else -> "দুপুরের খাবার (Lunch)" to "Lunch Session"
    }

    return MealHeadcountMetrics(
        mealType = mealType,
        mealTitleBn = titleBn,
        mealTitleEn = titleEn,
        dayNo = dayNo,
        targetCount = totalTarget,
        servedCount = servedCount,
        remainingCount = remainingCount,
        servingPercentage = servingPercentage,
        specialDietTargetCount = specialDietTargetCount,
        specialDietServedCount = specialDietServedCount,
        wastageRiskLevel = riskLevel,
        cateringAdvisoryBn = advBn,
        cateringAdvisoryEn = advEn
    )
}

/**
 * Live Meal Headcount & Wastage Prep Counter Card.
 * Allows the camp admin and catering managers to track real-time meal claims,
 * double-dine prevention, and food surplus reduction advisory.
 */
@Composable
fun LiveMealHeadcountCard(
    participants: List<ParticipantEntity>,
    mealDistributions: List<MealDistributionEntity>,
    language: CampLanguage,
    onOpenScanner: () -> Unit,
    modifier: Modifier = Modifier,
    selectedDay: Int = 1
) {
    val defaultMealType = remember {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when (hour) {
            in 6..10 -> "BREAKFAST"
            in 11..15 -> "LUNCH"
            in 16..18 -> "SNACKS"
            in 19..23 -> "DINNER"
            else -> "BREAKFAST"
        }
    }
    var selectedMealType by remember { mutableStateOf(defaultMealType) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    val metrics = remember(selectedMealType, selectedDay, participants, mealDistributions) {
        deriveMealHeadcountMetrics(selectedMealType, selectedDay, participants, mealDistributions)
    }

    val recentClaims = remember(selectedMealType, selectedDay, mealDistributions) {
        mealDistributions
            .filter { it.dayNo == selectedDay && it.mealType.equals(selectedMealType, ignoreCase = true) }
            .sortedByDescending { it.claimedAt }
            .take(5)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_meal_headcount_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Dining Icon and Live Tag
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
                            .background(Color(0xFFEA580C).copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = null,
                            tint = Color(0xFFEA580C),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "খাবারের লাইভ হেডকাউন্ট ও অপচয় রোধ"
                                else
                                    "Live Meal Headcount & Wastage Prep",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = LscSlateDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFD97706))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "ক্যাটারিং টিমের রিয়েল-টাইম খাদ্য প্রস্তুতি ও বিতরণ সমন্বয়"
                            else
                                "Real-time kitchen prep & dining queue synchronization",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                FilledTonalButton(
                    onClick = onOpenScanner,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFFFFF7ED),
                        contentColor = Color(0xFFEA580C)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("scan_meal_qr_btn")
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (language == CampLanguage.BANGLA) "মিল স্ক্যান" else "Scan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Meal Session Filter Chips (Breakfast, Lunch, Snacks, Dinner)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val mealOptions = listOf(
                    "BREAKFAST" to ("নাস্তা" to "Breakfast"),
                    "LUNCH" to ("দুপুর" to "Lunch"),
                    "SNACKS" to ("স্ন্যাক্স" to "Snacks"),
                    "DINNER" to ("রাত" to "Dinner")
                )
                mealOptions.forEach { (typeKey, labels) ->
                    val isSelected = selectedMealType == typeKey
                    Surface(
                        color = if (isSelected) Color(0xFFEA580C) else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFFEA580C) else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedMealType = typeKey }
                            .testTag("meal_chip_$typeKey")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = if (language == CampLanguage.BANGLA) labels.first else labels.second,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else LscSlateDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main KPI Metrics 4-Box Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Served Count Box
                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                    modifier = Modifier.weight(1f).testTag("meal_served_count_box")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "বিতরণ সম্পন্ন" else "Served",
                            fontSize = 10.sp,
                            color = Color(0xFF047857),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${metrics.servedCount}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF065F46)
                        )
                        Text(
                            text = "${"%.1f".format(metrics.servingPercentage)}%",
                            fontSize = 10.sp,
                            color = Color(0xFF059669),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Remaining Count Box (Critical for Wastage)
                Surface(
                    color = if (metrics.remainingCount > 0) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (metrics.remainingCount > 0) Color(0xFFFDE68A) else Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f).testTag("meal_remaining_count_box")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "এখনও বাকি" else "Remaining",
                            fontSize = 10.sp,
                            color = Color(0xFFB45309),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${metrics.remainingCount}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (metrics.remainingCount > 0) Color(0xFFB45309) else Color.Gray
                        )
                        Text(
                            text = if (language == CampLanguage.BANGLA) "জন অংশগ্রহণকারী" else "persons",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Total Target Headcount Box
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "মোট কোটা" else "Target",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${metrics.targetCount}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = LscSlateDark
                        )
                        Text(
                            text = if (language == CampLanguage.BANGLA) "নিবন্ধিত" else "Target total",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Special Diet / Medical Flag Box
                Surface(
                    color = Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "বিশেষ ডায়েট" else "Diet / Allergy",
                            fontSize = 10.sp,
                            color = Color(0xFFB91C1C),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${metrics.specialDietServedCount}/${metrics.specialDietTargetCount}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                        Text(
                            text = if (language == CampLanguage.BANGLA) "অ্যালার্জি/ডায়েট" else "Special packs",
                            fontSize = 10.sp,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Linear Progress Bar for Visual Representation
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (language == CampLanguage.BANGLA) "${metrics.mealTitleBn} পরিবেশন অগ্রগতি" else "${metrics.mealTitleEn} Progress",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LscSlateDark
                    )
                    Text(
                        text = "${metrics.servedCount} / ${metrics.targetCount} (${"%.1f".format(metrics.servingPercentage)}%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (metrics.servingPercentage / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .testTag("meal_serving_progress_bar"),
                    color = Color(0xFF059669),
                    trackColor = Color(0xFFF1F5F9)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Catering Prep & Food Wastage Advisory Banner
            Surface(
                color = when (metrics.wastageRiskLevel) {
                    "OPTIMAL" -> Color(0xFFECFDF5)
                    "PREP_CAUTION" -> Color(0xFFFFFBEB)
                    else -> Color(0xFFEFF6FF)
                },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                    1.dp,
                    when (metrics.wastageRiskLevel) {
                        "OPTIMAL" -> Color(0xFFA7F3D0)
                        "PREP_CAUTION" -> Color(0xFFFDE68A)
                        else -> Color(0xFFBFDBFE)
                    }
                ),
                modifier = Modifier.fillMaxWidth().testTag("catering_advisory_banner")
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = if (metrics.wastageRiskLevel == "PREP_CAUTION") Icons.Default.Warning else Icons.Default.SoupKitchen,
                        contentDescription = null,
                        tint = if (metrics.wastageRiskLevel == "PREP_CAUTION") Color(0xFFD97706) else Color(0xFF059669),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "ক্যাটারিং ও খাদ্য অপচয় রোধ পরামর্শ" else "Catering Prep & Wastage Prevention",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (metrics.wastageRiskLevel == "PREP_CAUTION") Color(0xFFB45309) else Color(0xFF065F46)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) metrics.cateringAdvisoryBn else metrics.cateringAdvisoryEn,
                            fontSize = 11.sp,
                            color = LscSlateDark,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Recent Claims Mini Ticker
            if (recentClaims.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == CampLanguage.BANGLA) "সর্বশেষ খাবার গ্রহণকারীগণ" else "Recently Served",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = if (language == CampLanguage.BANGLA) "${metrics.servedCount} জন নিবন্ধিত" else "${metrics.servedCount} Registered",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth().testTag("recent_meal_claims_row")
                ) {
                    items(recentClaims) { claim ->
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = claim.campCode,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF2563EB)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = claim.participantName,
                                    fontSize = 10.sp,
                                    color = LscSlateDark
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = timeFormat.format(Date(claim.claimedAt)),
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
