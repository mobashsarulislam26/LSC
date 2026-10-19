package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.data.model.ParticipantEntity
import com.example.data.sample.SeedData
import com.example.data.service.ParticipantImportService
import com.example.ui.theme.LscEmeraldLight
import com.example.ui.theme.LscEmeraldPrimary
import com.example.ui.theme.LscSlateDark

/**
 * Dialog to import participant names and profiles from a CSV-formatted JSON structure
 * directly into the Firestore 'participants' collection to ensure the attendance roster is up to date.
 */
@Composable
fun ParticipantRosterImportDialog(
    language: CampLanguage,
    onDismiss: () -> Unit,
    onConfirmImport: (rawJsonContent: String) -> Unit
) {
    val isBangla = language == CampLanguage.BANGLA
    var activeTab by remember { mutableIntStateOf(0) } // 0: Official 72 Participants Roster, 1: Custom JSON/CSV Input

    var rawInputText by remember {
        mutableStateOf(
            """[
  {
    "campCode": "B-01",
    "fileNo": "F-1001",
    "nameBn": "মুহাম্মদ আব্দুল্লাহ",
    "nameEn": "Muhammad Abdullah",
    "groupId": "bukhari",
    "groupNameBn": "বুখারী",
    "subgroup": "বুখারী ১",
    "roomNo": "১০১",
    "guardianName": "আব্দুর রহমান",
    "guardianPhone": "01711-100001",
    "districtBn": "ঢাকা"
  },
  {
    "campCode": "B-02",
    "fileNo": "F-1002",
    "nameBn": "হাফিজ আহমেদ",
    "nameEn": "Hafiz Ahmed",
    "groupId": "bukhari",
    "groupNameBn": "বুখারী",
    "subgroup": "বুখারী ১",
    "roomNo": "১০১",
    "guardianName": "আহমেদ উল্লাহ",
    "guardianPhone": "01711-100002",
    "districtBn": "চট্টগ্রাম"
  },
  {
    "campCode": "M-01",
    "fileNo": "F-1013",
    "nameBn": "হাসান মাহমুদ",
    "nameEn": "Hasan Mahmud",
    "groupId": "muslim",
    "groupNameBn": "মুসলিম",
    "subgroup": "মুসলিম ১",
    "roomNo": "১০২",
    "guardianName": "মাহমুদুর রহমান",
    "guardianPhone": "01711-100013",
    "districtBn": "সিলেট"
  }
]"""
        )
    }

    var parsedParticipants by remember {
        mutableStateOf(SeedData.generateParticipants())
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
                .testTag("participant_roster_import_dialog")
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
                                imageVector = Icons.Default.GroupAdd,
                                contentDescription = null,
                                tint = LscEmeraldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBangla) "অংশগ্রহণকারী রোস্টার ইম্পোর্ট" else "Participant Roster Import Service",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = LscSlateDark
                            )
                            Text(
                                text = if (isBangla)
                                    "CSV-ফরম্যাটেড JSON হতে ফায়ারস্টোর 'participants' কালেকশনে সরাসরি সংরক্ষণ"
                                else
                                    "Import from CSV-formatted JSON directly into Firestore 'participants'",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_participant_import_btn")
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
                            parsedParticipants = SeedData.generateParticipants()
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
                            .testTag("tab_official_roster")
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBangla) "অফিসিয়াল ৭২ জন রোস্টার" else "Official 72 Roster",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            activeTab = 1
                            val res = ParticipantImportService.parseCsvFormattedJson(rawInputText)
                            parsedParticipants = res
                            parseErrorMessage = if (res.isEmpty()) "কোন অংশগ্রহণকারী পার্স করা যায়নি" else null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeTab == 1) LscEmeraldPrimary else Color.Transparent,
                            contentColor = if (activeTab == 1) Color.White else Color.DarkGray
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = null,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_custom_csv_json")
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBangla) "কাস্টম CSV-ফরম্যাটেড JSON" else "Custom CSV-JSON",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (activeTab == 1) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (isBangla)
                                "CSV-ফরম্যাটেড JSON অ্যারে অথবা { \"csv\": \"...\" } পেস্ট করুন:"
                            else
                                "Paste CSV-formatted JSON structure:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = rawInputText,
                            onValueChange = {
                                rawInputText = it
                                val parsed = ParticipantImportService.parseCsvFormattedJson(it)
                                parsedParticipants = parsed
                                parseErrorMessage = if (it.isNotBlank() && parsed.isEmpty())
                                    "কোন অংশগ্রহণকারী পার্স করা যায়নি। ফরম্যাট পরীক্ষা করুন।"
                                else null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .testTag("participant_json_input"),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Error alert
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

                // Stats and Destination
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
                                    text = "${parsedParticipants.size} জন",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF15803D),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isBangla) "হাজিরা রোস্টার আপডেট" else "Attendance Roster Ready",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Collection: 'participants'",
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
                    text = if (isBangla) "অংশগ্রহণকারী প্রিভিউ তালিকা (Roster Preview):" else "Roster Preview List:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LscSlateDark
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Scrollable Participants Preview List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("participant_preview_list")
                ) {
                    items(parsedParticipants, key = { "${it.campCode}_${it.id}" }) { p ->
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
                                                text = p.campCode,
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
                                                text = p.fileNo,
                                                fontSize = 10.sp,
                                                color = Color.DarkGray,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(0xFFE0F2FE),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "${p.groupNameBn} (${p.subgroup})",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF0369A1),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = p.nameBn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = LscSlateDark
                                    )
                                    Row(
                                        modifier = Modifier.padding(top = 2.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(text = "রুম: ${p.roomNo}", fontSize = 11.sp, color = Color.Gray)
                                        Text(text = "অভিভাবক: ${p.guardianPhone}", fontSize = 11.sp, color = Color.Gray)
                                        Text(text = "জেলা: ${p.districtBn}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Footer Actions
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
                            if (parsedParticipants.isNotEmpty()) {
                                isUploading = true
                                // If tab 0, pass JSON serialized of official participants or raw input
                                val jsonToSend = if (activeTab == 0) {
                                    val arr = org.json.JSONArray()
                                    parsedParticipants.forEach { p ->
                                        val obj = org.json.JSONObject()
                                        obj.put("campCode", p.campCode)
                                        obj.put("fileNo", p.fileNo)
                                        obj.put("nameBn", p.nameBn)
                                        obj.put("nameEn", p.nameEn)
                                        obj.put("groupId", p.groupId)
                                        obj.put("groupNameBn", p.groupNameBn)
                                        obj.put("groupNameEn", p.groupNameEn)
                                        obj.put("subgroup", p.subgroup)
                                        obj.put("roomNo", p.roomNo)
                                        obj.put("bedNo", p.bedNo)
                                        obj.put("districtBn", p.districtBn)
                                        obj.put("districtEn", p.districtEn)
                                        obj.put("gender", p.gender)
                                        obj.put("age", p.age)
                                        obj.put("guardianName", p.guardianName)
                                        obj.put("guardianPhone", p.guardianPhone)
                                        obj.put("isCheckedIn", p.isCheckedIn)
                                        arr.put(obj)
                                    }
                                    arr.toString()
                                } else {
                                    rawInputText
                                }
                                onConfirmImport(jsonToSend)
                                onDismiss()
                            }
                        },
                        enabled = parsedParticipants.isNotEmpty() && !isUploading,
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(2f)
                            .testTag("confirm_participant_import_btn")
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
                                    "ফায়ারস্টোর 'participants' এ সংরক্ষণ (${parsedParticipants.size} জন)"
                                else
                                    "Save to Firestore 'participants' (${parsedParticipants.size})"
                            )
                        }
                    }
                }
            }
        }
    }
}
