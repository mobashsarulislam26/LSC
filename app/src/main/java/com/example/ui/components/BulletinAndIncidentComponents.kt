package com.example.ui.components

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BulletinEntity
import com.example.data.model.IncidentEntity
import com.example.ui.theme.*

/**
 * Dialog to Post a New Camp Bulletin / Live Announcement.
 */
@Composable
fun CreateNoticeDialog(
    dayNo: Int = 1,
    isBangla: Boolean = true,
    onDismiss: () -> Unit,
    onPublish: (BulletinEntity) -> Unit
) {
    var titleBn by remember { mutableStateOf("") }
    var summaryBn by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("ক্যাম্প কন্ট্রোল রুম") }
    var category by remember { mutableStateOf("GENERAL") } // "URGENT", "GENERAL", "HIGHLIGHT"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = LscEmeraldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBangla) "নতুন ক্যাম্প নোটিশ জারি করুন" else "Publish Camp Announcement",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isBangla) "জরুরি টাইপ:" else "Category:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = category == "GENERAL",
                            onClick = { category = "GENERAL" },
                            label = { Text("সাধারণ") }
                        )
                        FilterChip(
                            selected = category == "URGENT",
                            onClick = { category = "URGENT" },
                            label = { Text("🚨 জরুরি") }
                        )
                    }
                }

                OutlinedTextField(
                    value = titleBn,
                    onValueChange = { titleBn = it },
                    label = { Text(if (isBangla) "নোটিশ শিরোনাম" else "Notice Title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notice_title_input")
                )

                OutlinedTextField(
                    value = summaryBn,
                    onValueChange = { summaryBn = it },
                    label = { Text(if (isBangla) "বিস্তারিত বিবরণ" else "Notice Details") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text(if (isBangla) "জারি কারী কর্মকর্তা / টিম" else "Author / Department") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titleBn.isNotBlank()) {
                        val bulletin = BulletinEntity(
                            dayNo = dayNo,
                            dateStr = "আজ",
                            titleBn = titleBn.trim(),
                            titleEn = titleBn.trim(),
                            summaryBn = summaryBn.trim(),
                            summaryEn = summaryBn.trim(),
                            author = author.trim(),
                            groupSpotlight = if (category == "URGENT") "🚨 জরুরি নোটিশ" else "ক্যাম্প ঘোষণা",
                            isPublished = true,
                            status = "PUBLISHED"
                        )
                        onPublish(bulletin)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                modifier = Modifier.testTag("publish_notice_btn")
            ) {
                Text(if (isBangla) "নোটিশ প্রচার করুন" else "Publish Notice")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(if (isBangla) "বাতিল" else "Cancel")
            }
        }
    )
}

/**
 * Dialog to Log an Incident or Lost & Found Item.
 */
@Composable
fun LogIncidentDialog(
    isBangla: Boolean = true,
    onDismiss: () -> Unit,
    onSubmit: (title: String, pCode: String?, pName: String?, severity: String, location: String, desc: String, action: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var participantCode by remember { mutableStateOf("") }
    var participantName by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("MEDIUM") } // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var actionTaken by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ReportProblem, contentDescription = null, tint = Color(0xFFDC2626))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBangla) "ক্যাম্প ঘটনা বা হারানো বস্তু রেকর্ড" else "Log Incident / Lost & Found",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isBangla) "ঘটনা / বস্তুর নাম (e.g. ঘড়ি হারানো / হাত কাটা)" else "Incident Title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("incident_title_input")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = severity == "LOW",
                        onClick = { severity = "LOW" },
                        label = { Text("হারানো বস্তু") }
                    )
                    FilterChip(
                        selected = severity == "MEDIUM",
                        onClick = { severity = "MEDIUM" },
                        label = { Text("সাধারণ ঘটনা") }
                    )
                    FilterChip(
                        selected = severity == "HIGH",
                        onClick = { severity = "HIGH" },
                        label = { Text("🚨 জরুরি") }
                    )
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(if (isBangla) "ঘটনার স্থান / কোথায় পাওয়া গেছে" else "Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = participantName,
                    onValueChange = { participantName = it },
                    label = { Text(if (isBangla) "সংশ্লিষ্ট ব্যক্তির নাম (ঐচ্ছিক)" else "Participant Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(if (isBangla) "বিস্তারিত বিবরণ" else "Description") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSubmit(
                            title.trim(),
                            participantCode.ifBlank { null }?.trim(),
                            participantName.ifBlank { null }?.trim(),
                            severity,
                            location.trim(),
                            description.trim(),
                            actionTaken.ifBlank { "নথিভুক্ত করা হয়েছে" }.trim()
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                modifier = Modifier.testTag("submit_incident_btn")
            ) {
                Text(if (isBangla) "রেকর্ড সংরক্ষণ করুন" else "Log Incident")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(if (isBangla) "বাতিল" else "Cancel")
            }
        }
    )
}

/**
 * Screen showing Live Bulletins & Camp Announcements.
 */
@Composable
fun BulletinBoardScreen(
    bulletins: List<BulletinEntity>,
    isBangla: Boolean = true,
    canPost: Boolean = true,
    onPostNotice: () -> Unit,
    onDeleteNotice: (BulletinEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("bulletin_board_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBangla) "লাইভ ক্যাম্প বুলেটিন ও নোটিশ বোর্ড" else "Live Camp Bulletin Board",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = LscSlateDark
                )
                Text(
                    text = if (isBangla) "ক্যাম্পের সকল জরুরি ঘোষণা ও আপডেট একসাথে" else "All official announcements and camp updates",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            if (canPost) {
                Button(
                    onClick = onPostNotice,
                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_notice_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBangla) "নতুন নোটিশ" else "New Notice", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (bulletins.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBangla) "বর্তমানে কোনো নোটিশ নেই" else "No active notices at this time",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(bulletins) { bulletin ->
                    val isUrgent = bulletin.groupSpotlight.contains("জরুরি") || bulletin.summaryBn.contains("🚨")

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUrgent) Color(0xFFFEF2F2) else Color.White
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isUrgent) Color(0xFFEF4444) else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = if (isUrgent) Color(0xFFDC2626) else LscEmeraldPrimary,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = bulletin.groupSpotlight,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "প্রচারক: ${bulletin.author}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                    if (canPost) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                            onClick = { onDeleteNotice(bulletin) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete Notice",
                                                tint = Color.Red,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = bulletin.titleBn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isUrgent) Color(0xFF991B1B) else LscSlateDark
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = bulletin.summaryBn,
                                fontSize = 13.sp,
                                color = Color(0xFF334155),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
