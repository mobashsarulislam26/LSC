package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CampLanguage
import com.example.data.model.ScheduleEventEntity
import com.example.data.service.ScheduleBulkUploadService
import com.example.ui.theme.LscEmeraldLight
import com.example.ui.theme.LscEmeraldPrimary
import com.example.ui.theme.LscSlateDark

/**
 * Bulk upload utility in the Admin Dashboard to parse and store the official camp schedule data
 * (event names, timings, and locations) directly into the Firestore 'schedules' collection.
 */
@Composable
fun BulkScheduleUploadDialog(
    language: CampLanguage,
    onDismiss: () -> Unit,
    onConfirmUpload: (List<ScheduleEventEntity>) -> Unit
) {
    val isBangla = language == CampLanguage.BANGLA
    var activeTab by remember { mutableIntStateOf(0) } // 0: Official 5-Day Schedule, 1: Custom JSON/CSV Input

    var rawInputText by remember {
        mutableStateOf(
            """[
  {
    "dayNo": 1,
    "timeSlot": "05:00 - 06:00",
    "titleBn": "ফজর নামাজ ও কুরআন চর্চা",
    "titleEn": "Fajr Prayer and Quran Recitation",
    "location": "ক্যাম্প জামাত চত্বর / মসজিদ",
    "category": "Jamaat"
  },
  {
    "dayNo": 1,
    "timeSlot": "06:00 - 07:00",
    "titleBn": "প্রভাতী শরীরচর্চা ও পিটি ড্রিল",
    "titleEn": "Morning Physical Training & Drill",
    "location": "প্রধান প্যারেড মাঠ",
    "category": "Station"
  },
  {
    "dayNo": 1,
    "timeSlot": "07:00 - 08:00",
    "titleBn": "সকালের পুষ্টিকর নাস্তা ও তাবুনিরীক্ষণ",
    "titleEn": "Nutritious Breakfast & Tent Inspection",
    "location": "ডাইনিং হল ও তাঁবু এলাকা",
    "category": "Meal"
  }
]"""
        )
    }

    var parsedEvents by remember {
        mutableStateOf(ScheduleBulkUploadService.getOfficialCampSchedule())
    }

    var parseErrorMessage by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(8.dp)
                .testTag("bulk_schedule_upload_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
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
                                .size(40.dp)
                                .background(LscEmeraldLight, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = LscEmeraldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBangla) "সময়সূচী বাল্ক আপলোড উইজার্ড" else "Schedule Bulk Upload Utility",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = LscSlateDark
                            )
                            Text(
                                text = if (isBangla)
                                    "ফায়ারস্টোর 'schedules' কালেকশনে সরাসরি ডাটা আপলোড করুন"
                                else
                                    "Parse and store schedule events into Firestore 'schedules'",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_schedule_upload_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = {
                            activeTab = 0
                            parsedEvents = ScheduleBulkUploadService.getOfficialCampSchedule()
                            parseErrorMessage = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeTab == 0) LscEmeraldPrimary else Color.Transparent,
                            contentColor = if (activeTab == 0) Color.White else Color.DarkGray
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = null,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_official_schedule")
                    ) {
                        Icon(Icons.Default.EventAvailable, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBangla) "অফিসিয়াল ৫ দিনের শিডিউল" else "Official 5-Day Schedule",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            activeTab = 1
                            val res = ScheduleBulkUploadService.parseScheduleData(rawInputText)
                            parsedEvents = res
                            parseErrorMessage = if (res.isEmpty()) "কোন শিডিউল ইভেন্ট পার্স করা যায়নি" else null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeTab == 1) LscEmeraldPrimary else Color.Transparent,
                            contentColor = if (activeTab == 1) Color.White else Color.DarkGray
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = null,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_custom_schedule")
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBangla) "কাস্টম JSON / CSV ইনপুট" else "Custom JSON/CSV Input",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (activeTab == 1) {
                    // Custom text input
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (isBangla)
                                "JSON অ্যারে অথবা CSV ফরম্যাটে শিডিউল ডাটা পেস্ট করুন:"
                            else
                                "Paste schedule data in JSON Array or CSV format:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = rawInputText,
                            onValueChange = {
                                rawInputText = it
                                val parsed = ScheduleBulkUploadService.parseScheduleData(it)
                                parsedEvents = parsed
                                parseErrorMessage = if (it.isNotBlank() && parsed.isEmpty())
                                    "ইনপুট ফরম্যাট সঠিক নয়। সঠিক JSON বা CSV নিশ্চিত করুন।"
                                else null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .testTag("schedule_json_input"),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Error message if any
                parseErrorMessage?.let { err ->
                    Surface(
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = err, color = Color.Red, fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Stats and destination banner
                Surface(
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${parsedEvents.size} টি ইভেন্ট",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF15803D),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isBangla) "ফায়ারস্টোরে আপলোডের জন্য প্রস্তুত" else "Ready to upload to Firestore",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Collection: 'schedules'",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isBangla) "ইভেন্ট প্রিভিউ তালিকা (Event Preview):" else "Event Preview List:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LscSlateDark
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Scrollable Events Preview List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("schedule_preview_list")
                ) {
                    items(parsedEvents, key = { "${it.dayNo}_${it.timeSlot}_${it.titleBn}" }) { ev ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            shape = RoundedCornerShape(10.dp),
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = LscEmeraldPrimary,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (isBangla) "দিন ${ev.dayNo}" else "Day ${ev.dayNo}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(0xFFF1F5F9),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = ev.timeSlot,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.DarkGray,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(0xFFFEF3C7),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = ev.category,
                                                fontSize = 10.sp,
                                                color = Color(0xFF92400E),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = ev.titleBn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = LscSlateDark
                                    )
                                    if (ev.titleEn.isNotBlank() && ev.titleEn != ev.titleBn) {
                                        Text(text = ev.titleEn, fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Place,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(text = ev.location, fontSize = 11.sp, color = Color.DarkGray)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Footer Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isBangla) "বাতিল" else "Cancel")
                    }

                    Button(
                        onClick = {
                            if (parsedEvents.isNotEmpty()) {
                                isUploading = true
                                onConfirmUpload(parsedEvents)
                                onDismiss()
                            }
                        },
                        enabled = parsedEvents.isNotEmpty() && !isUploading,
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(2f)
                            .testTag("confirm_schedule_bulk_upload_btn")
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isBangla) "আপলোড হচ্ছে..." else "Uploading...")
                        } else {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBangla)
                                    "ফায়ারস্টোরে বাল্ক আপলোড শুরু করুন (${parsedEvents.size} টি)"
                                else
                                    "Upload to Firestore 'schedules' (${parsedEvents.size})"
                            )
                        }
                    }
                }
            }
        }
    }
}
