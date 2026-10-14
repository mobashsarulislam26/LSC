package com.example.ui.screens.bulletin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BulletinEntity
import com.example.data.model.CampLanguage
import com.example.ui.theme.*

@Composable
fun BulletinScreen(
    language: CampLanguage,
    bulletins: List<BulletinEntity>,
    isAdminUnlocked: Boolean = false,
    onRequestAdminUnlock: () -> Unit = {},
    onPublishBulletin: (titleBn: String, titleEn: String, summaryBn: String, summaryEn: String, author: String, groupSpotlight: String, dayNo: Int, dateStr: String) -> Unit = { _, _, _, _, _, _, _, _ -> }
) {
    var showPublishDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Daily Wall Magazine & Gazette Banner with Publish Action
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
            modifier = Modifier.fillMaxWidth().testTag("bulletin_header_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == CampLanguage.BANGLA) "ক্যাম্প বার্তা ও দেয়াল পত্রিকা" else "Camp Gazette & Wall Magazine",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Surface(
                        color = LscGoldSecondary,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "লাইভ প্রকাশনা",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "প্রতিদিনের অভিজ্ঞতা, নোটিশ, শিশুদের রচিত কবিতা, স্টেশন রিপোর্ট ও গ্রুপ স্পটলাইট নিয়ে ডিজিটাল বুলেটিন।",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Direct Publish Live Bulletin Action Button
                Button(
                    onClick = {
                        if (isAdminUnlocked) {
                            showPublishDialog = true
                        } else {
                            onRequestAdminUnlock()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LscGoldSecondary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("publish_bulletin_trigger_btn")
                ) {
                    Icon(
                        imageVector = if (isAdminUnlocked) Icons.Default.Campaign else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == CampLanguage.BANGLA)
                            (if (isAdminUnlocked) "+ সরাসরি নতুন বুলেটিন প্রকাশ করুন" else "বুলেটিন প্রকাশ করতে আনলক করুন")
                        else
                            (if (isAdminUnlocked) "+ Publish Live Bulletin" else "Unlock to Publish Bulletin"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bulletins Feed
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(bulletins, key = { it.id }) { bulletin ->
                BulletinArticleCard(bulletin = bulletin, language = language)
            }

            // Wall Magazine Highlights
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoStories, contentDescription = null, tint = LscGoldSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "দেয়াল পত্রিকা সাবমিশন - দিন ১",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = LscSlateDark
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "আজকের দেয়াল পত্রিকায় ৩টি গ্রুপ থেকে মোট ৬টি প্রবন্ধ, ২টি ইসলামিক ক্যালিগ্রাফি ও ৪টি অনুপ্রেরণাদায়ী কবিতা জমা পড়েছে।",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }

    // Modal Dialog to Publish Live Bulletin
    if (showPublishDialog) {
        PublishBulletinDialog(
            language = language,
            onDismiss = { showPublishDialog = false },
            onPublish = { titleBn, titleEn, summaryBn, summaryEn, author, groupSpotlight, dayNo, dateStr ->
                onPublishBulletin(titleBn, titleEn, summaryBn, summaryEn, author, groupSpotlight, dayNo, dateStr)
                showPublishDialog = false
            }
        )
    }
}

/**
 * Direct Live Bulletin Publishing Dialog
 */
@Composable
fun PublishBulletinDialog(
    language: CampLanguage,
    onDismiss: () -> Unit,
    onPublish: (titleBn: String, titleEn: String, summaryBn: String, summaryEn: String, author: String, groupSpotlight: String, dayNo: Int, dateStr: String) -> Unit
) {
    var titleBn by remember { mutableStateOf("") }
    var titleEn by remember { mutableStateOf("") }
    var summaryBn by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("ক্যাম্প মিডিয়া ও পাবলিকেশন টিম") }
    var groupSpotlight by remember { mutableStateOf("ইমাম বুখারী ও সালাহউদ্দীন আইয়ুবী গ্রুপ") }
    var selectedDay by remember { mutableStateOf(1) }
    var categoryBadge by remember { mutableStateOf("জরুরি নোটিশ") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("জরুরি নোটিশ", "দৈনিক বুলেটিন", "প্রতিযোগিতার ফলাফল", "ডাইনিং আপডেট", "স্টেশন হাইলাইট")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("publish_bulletin_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = LscGoldDark)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "সরাসরি বুলেটিন প্রকাশ" else "Publish Live Bulletin",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

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

                // Category Chips
                Text(text = "বুলেটিন ক্যাটাগরি:", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(categories) { cat ->
                        val isSel = categoryBadge == cat
                        Surface(
                            color = if (isSel) LscGoldDark else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { categoryBadge = cat }
                        ) {
                            Text(
                                text = cat,
                                fontSize = 10.sp,
                                color = if (isSel) Color.White else LscSlateDark,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Day Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "ক্যাম্প দিন:", fontSize = 11.sp, color = Color.Gray)
                    (1..5).forEach { day ->
                        val isDaySel = selectedDay == day
                        Surface(
                            color = if (isDaySel) LscEmeraldPrimary else Color(0xFFF1F5F9),
                            shape = CircleShape,
                            modifier = Modifier.clickable { selectedDay = day }
                        ) {
                            Text(
                                text = "$day",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDaySel) Color.White else LscSlateDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = titleBn,
                    onValueChange = {
                        titleBn = it
                        errorMessage = null
                    },
                    label = { Text("বুলেটিনের শিরোনাম (বাংলা) *") },
                    placeholder = { Text("উদা: দ্বিতীয় দিনের রাতের সাংস্কৃতিক অনুষ্ঠান স্থগিত") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_bulletin_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = summaryBn,
                    onValueChange = { summaryBn = it },
                    label = { Text("বিস্তারিত বিবরণ ও বার্তা *") },
                    placeholder = { Text("বিজ্ঞপ্তির সম্পূর্ণ তথ্য বা সংবাদ এখানে লিখুন...") },
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth().height(110.dp).testTag("input_bulletin_content")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("লেখক / প্রকাশনা কর্তৃপক্ষ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = groupSpotlight,
                    onValueChange = { groupSpotlight = it },
                    label = { Text("গ্রুপ স্পটলাইট / বিশেষ স্বীকৃতি") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
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
                            if (titleBn.isBlank() || summaryBn.isBlank()) {
                                errorMessage = "শিরোনাম ও বিস্তারিত বিবরণ উভয়ই পূরণ করতে হবে!"
                            } else {
                                val fullTitle = "[$categoryBadge] $titleBn".trim()
                                onPublish(
                                    fullTitle,
                                    fullTitle,
                                    summaryBn,
                                    summaryBn,
                                    author,
                                    groupSpotlight,
                                    selectedDay,
                                    "দিন $selectedDay • লাইভ প্রকাশনা"
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        modifier = Modifier.weight(1.2f).testTag("submit_bulletin_btn")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (language == CampLanguage.BANGLA) "প্রকাশ করুন" else "Publish")
                    }
                }
            }
        }
    }
}

@Composable
fun BulletinArticleCard(
    bulletin: BulletinEntity,
    language: CampLanguage
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth().testTag("bulletin_item_${bulletin.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = bulletin.dateStr,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = bulletin.author,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (language == CampLanguage.BANGLA) bulletin.titleBn else bulletin.titleEn,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = LscSlateDark,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (language == CampLanguage.BANGLA) bulletin.summaryBn else bulletin.summaryEn,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF334155)),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            if (bulletin.groupSpotlight.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = LscGoldSecondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "স্পটলাইট: ${bulletin.groupSpotlight}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LscSlateDark
                        )
                    }
                }
            }
        }
    }
}
