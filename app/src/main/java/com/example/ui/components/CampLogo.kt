package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.CampLanguage
import com.example.ui.theme.*

/**
 * Standard Camp Logo Image component using the official camp emblem resource.
 */
@Composable
fun CampLogoImage(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp),
    borderWidth: Dp = 1.dp,
    borderColor: Color = Color(0xFFE2E8F0),
    backgroundColor: Color = Color.White,
    elevation: Dp = 2.dp,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .then(if (elevation > 0.dp) Modifier.shadow(elevation, shape) else Modifier)
            .clip(shape)
            .background(backgroundColor)
            .then(if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, shape) else Modifier)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_camp_logo),
            contentDescription = "Official Life Skills Camping 2026 Emblem",
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        )
    }
}

/**
 * Camp Logo Badge with title and subtitle for headers, ID cards, and banners.
 */
@Composable
fun CampLogoBadge(
    language: CampLanguage,
    modifier: Modifier = Modifier,
    logoSize: Dp = 48.dp,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CampLogoImage(
            size = logoSize,
            elevation = 3.dp,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = if (language == CampLanguage.BANGLA) "লাইফ স্কিলস ক্যাম্পিং ২০২৬" else "Life Skills Camping 2026",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = if (language == CampLanguage.BANGLA) "নিজকে গড়ো, গড়তে শেখাও • Build Thyself Help to Build" else "Build Thyself Help to Build • WAMY BD",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = LscGoldLight,
                    fontSize = 11.sp
                )
            )
        }
    }
}

/**
 * About / Camp Info Dialog showing official logo, dates, location, and stats.
 */
@Composable
fun CampAboutDialog(
    language: CampLanguage,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("camp_about_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Official Emblem
                CampLogoImage(
                    size = 110.dp,
                    shape = RoundedCornerShape(12.dp),
                    elevation = 4.dp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (language == CampLanguage.BANGLA) "লাইফ স্কিলস ক্যাম্পিং ২০২৬ (WAMY বাংলাদেশ)" else "Life Skills Camping 2026 (WAMY Bangladesh)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LscEmeraldPrimary
                    ),
                    textAlign = TextAlign.Center
                )

                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Text(
                        text = if (language == CampLanguage.BANGLA) "নিজকে গড়ো, গড়তে শেখাও (Build Thyself Help to Build)" else "Build Thyself Help to Build",
                        color = Color(0xFF92400E),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(14.dp))

                // Info Rows
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoRow(
                        icon = Icons.Default.Event,
                        label = if (language == CampLanguage.BANGLA) "সময়কাল" else "Dates",
                        value = "22–26 October 2026 (৫ দিন • ২২–২৬ অক্টোবর ২০২৬)"
                    )
                    InfoRow(
                        icon = Icons.Default.Place,
                        label = if (language == CampLanguage.BANGLA) "স্থান" else "Location",
                        value = "রাঙ্গামাটি ওয়াটারফ্রন্ট রিসোর্ট, শফিপুর, গাজীপুর"
                    )
                    InfoRow(
                        icon = Icons.Default.Groups,
                        label = if (language == CampLanguage.BANGLA) "অংশগ্রহণকারী" else "Participants",
                        value = if (language == CampLanguage.BANGLA) "৬০৯ জন এতিম শিক্ষার্থী (৬ গ্রুপ • ২৪ সাব-গ্রুপ)" else "609 Orphan Campers (6 Groups • 24 Subgroups)"
                    )
                    InfoRow(
                        icon = Icons.Default.FitnessCenter,
                        label = if (language == CampLanguage.BANGLA) "স্কিল স্টেশন" else "Stations",
                        value = if (language == CampLanguage.BANGLA) "৬টি স্টেশন • ১৮-স্টার সিল ও ৮৩৫টি পুরস্কার" else "6 Rotation Stations • 18-Star Seal & 835 Prizes"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("close_about_dialog_button")
                ) {
                    Text(if (language == CampLanguage.BANGLA) "ঠিক আছে" else "Close")
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LscEmeraldPrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = LscSlateDark
        )
    }
}
