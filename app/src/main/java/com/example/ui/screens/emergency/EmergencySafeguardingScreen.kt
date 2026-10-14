package com.example.ui.screens.emergency

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.data.sample.SeedData
import com.example.ui.theme.*

@Composable
fun EmergencySafeguardingScreen(
    language: CampLanguage,
    incidents: List<IncidentEntity>,
    onReportIncident: (title: String, pCode: String?, pName: String?, severity: String, loc: String, desc: String, action: String) -> Unit
) {
    var showIncidentDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Safeguarding & Safety Protocol Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D)),
            modifier = Modifier.fillMaxWidth().testTag("emergency_banner")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == CampLanguage.BANGLA) "জরুরি যোগাযোগ ও সেইফগার্ডিং প্রোটোকল" else "Emergency & Safeguarding Directory",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Icon(Icons.Default.Emergency, contentDescription = null, tint = Color(0xFFFCA5A5))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "যেকোনো শিশু বা অংশগ্রহণকারীর অনুপস্থিতি, অসুস্থতা বা নিরাপত্তা সংক্রান্ত ঘটনায় অবিলম্বে নিচের সংশ্লিষ্ট টিমকে কল দিন।",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons Row (Report Incident / Missing Child Alert)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showIncidentDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).testTag("report_incident_btn")
            ) {
                Icon(Icons.Default.ReportProblem, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (language == CampLanguage.BANGLA) "ঘটনা রিপোর্ট করুন" else "Report Incident", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Emergency Contacts Directory
        Text(
            text = if (language == CampLanguage.BANGLA) "জরুরি টিম ডিরেক্টরি (২৪ ঘণ্টা প্রস্তুত):" else "24x7 Emergency Contact Directory:",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = LscSlateDark
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SeedData.emergencyContacts) { contact ->
                EmergencyContactRow(
                    contact = contact,
                    language = language,
                    onCall = {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (language == CampLanguage.BANGLA) "সাম্প্রতিক ঘটনার লগ ও সমাধানের অবস্থা:" else "Incident Logs & Safeguarding Audit:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = LscSlateDark
                )
            }

            if (incidents.isEmpty()) {
                item {
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "কোনো অমীমাংসিত জরুরি ঘটনা নেই। সকল অংশগ্রহণকারী নিরাপদে আছেন।",
                            fontSize = 11.sp,
                            color = Color(0xFF16A34A),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            } else {
                items(incidents) { inc ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = inc.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Surface(
                                    color = if (inc.status == "RESOLVED") Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = inc.status,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (inc.status == "RESOLVED") Color(0xFF16A34A) else Color(0xFFDC2626),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "স্থান: ${inc.location} • বর্ণনা: ${inc.description}", fontSize = 10.sp, color = Color.Gray)
                            Text(text = "গৃহীত পদক্ষেপ: ${inc.actionTaken}", fontSize = 10.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }

    if (showIncidentDialog) {
        ReportIncidentDialog(
            language = language,
            onDismiss = { showIncidentDialog = false },
            onSubmit = { title, code, name, sev, loc, desc, act ->
                onReportIncident(title, code, name, sev, loc, desc, act)
                showIncidentDialog = false
            }
        )
    }
}

@Composable
fun EmergencyContactRow(
    contact: EmergencyContact,
    language: CampLanguage,
    onCall: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFEF2F2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PhoneInTalk, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (language == CampLanguage.BANGLA) contact.roleTitleBn else contact.roleTitleEn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = LscSlateDark
                )
                Text(
                    text = "${contact.officerName} • ${contact.location}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = contact.phone,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFDC2626)
                )
            }

            Button(
                onClick = onCall,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(if (language == CampLanguage.BANGLA) "কল দিন" else "Call", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun ReportIncidentDialog(
    language: CampLanguage,
    onDismiss: () -> Unit,
    onSubmit: (String, String?, String?, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var actionTaken by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("MEDIUM") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = if (language == CampLanguage.BANGLA) "জরুরি ঘটনা / মিসিং চাইল্ড রিপোর্ট" else "Emergency / Missing Alert",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFFDC2626)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("শিরোনাম (যেমন: নামাজের পর রুমে অনুপস্থিত)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("স্থান (যেমন: ব্লক এ ডরমিটরি)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("ঘটনার বিস্তারিত বিবরণ...", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = actionTaken,
                    onValueChange = { actionTaken = it },
                    placeholder = { Text("তাৎক্ষণিক গৃহীত পদক্ষেপ...", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("বাতিল")
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSubmit(title, null, null, severity, location.ifEmpty { "ক্যাম্প প্রাঙ্গণ" }, description, actionTaken.ifEmpty { "তদন্ত চলছে" })
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("রিপোর্ট দাখিল")
                    }
                }
            }
        }
    }
}
