package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CampLanguage
import com.example.ui.theme.*
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Admin Security Passcode Authentication Dialog
 */
@Composable
fun AdminPasswordDialog(
    language: CampLanguage,
    onDismiss: () -> Unit,
    onUnlock: (password: String) -> Boolean
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("admin_password_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFEF3C7), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFFB45309)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "অ্যাডমিন পাসওয়ার্ড" else "Admin Authentication",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (language == CampLanguage.BANGLA)
                        "তথ্য এডিট, বিচারক/ভলান্টিয়ার সংযোজন ও এক্সেল আপলোডের জন্য অ্যাডমিন পাসওয়ার্ড দিন:"
                    else
                        "Enter the admin passcode to unlock editing and batch upload options:",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (errorText != null) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = errorText.orEmpty(),
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorText = null
                    },
                    label = { Text(if (language == CampLanguage.BANGLA) "পাসওয়ার্ড / পিন" else "Passcode / PIN") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("admin_passcode_input")
                )

                Spacer(modifier = Modifier.height(6.dp))

                Spacer(modifier = Modifier.height(18.dp))

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
                            if (password.isBlank()) {
                                errorText = if (language == CampLanguage.BANGLA) "পাসওয়ার্ড খালি রাখা যাবে না!" else "Passcode cannot be empty"
                            } else {
                                val success = onUnlock(password)
                                if (success) {
                                    onDismiss()
                                } else {
                                    errorText = if (language == CampLanguage.BANGLA) "ভুল পাসওয়ার্ড! সঠিক পাসওয়ার্ড লিখুন।" else "Incorrect password! Please try again."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        modifier = Modifier.weight(1f).testTag("admin_unlock_submit_btn")
                    ) {
                        Text(if (language == CampLanguage.BANGLA) "আনলক করুন" else "Unlock")
                    }
                }
            }
        }
    }
}

/**
 * Universal Excel / CSV Batch Upload Dialog for Judges, Volunteers, and Duty Officers
 */
@Composable
fun BatchImportDialog(
    language: CampLanguage,
    initialTarget: String = "JUDGES", // "JUDGES", "VOLUNTEERS", "OFFICERS"
    onDismiss: () -> Unit,
    onImportJudges: (String) -> Int,
    onImportVolunteers: (String) -> Int,
    onImportDutyOfficers: (String) -> Int
) {
    val context = LocalContext.current
    var selectedTarget by remember { mutableStateOf(initialTarget) }
    var spreadsheetText by remember { mutableStateOf("") }
    var importResultMsg by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    // File picker launcher for CSV or text spreadsheets
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    BufferedReader(InputStreamReader(stream)).use { reader ->
                        spreadsheetText = reader.readText()
                    }
                }
            } catch (e: Exception) {
                importResultMsg = "ফাইল পড়তে সমস্যা হয়েছে: ${e.localizedMessage}"
                isSuccess = false
            }
        }
    }

    val sampleTemplate = when (selectedTarget) {
        "JUDGES" -> "মাওলানা আব্দুল গাফফার,মুহাদ্দিস ও খতীব,Hadith,01712334455,হাদিস বিচারক প্রধান\nক্বারী মাহমুদুল হাসান,আন্তর্জাতিক ক্বারী,Quran,01815667788,ক্বিরাত ও তাজবীদ বিচারক"
        "VOLUNTEERS" -> "তানজীম আহমেদ,স্টেশন সমন্বয়ক,স্টেশন টিম,01711000111,স্টেশন-১\nমাহবুবুর রহমান,মেডিকেল সহায়ক,মেডিকেল টিম,01822000222,ক্লিনিক ও অ্যাম্বুলেন্স"
        else -> "ক্যাম্প চিফ,ড. এনামুল হক চৌধুরী,01711000111,এডমিন ব্লক ১০১\nনিরাপত্তা ইন-চার্জ,ক্যাপ্টেন মোজাম্মেল হক,01912333444,সেন্ট্রাল গেইট"
    }

    val headersHint = when (selectedTarget) {
        "JUDGES" -> "কলাম ক্রম: নাম, পদবী, ইভেন্ট/ক্যাটাগরি, মোবাইল, মন্তব্য"
        "VOLUNTEERS" -> "কলাম ক্রম: নাম, ভূমিকা, টিম, মোবাইল নম্বর, দায়িত্ব এলাকা"
        else -> "কলাম ক্রম: দায়িত্ব/পদবী, কর্মকর্তার নাম, মোবাইল নম্বর, অবস্থান/রুম"
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("batch_import_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
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
                                .background(Color(0xFFDCFCE7), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = null,
                                tint = Color(0xFF16A34A)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "এক্সেল / স্প্রেডশীট বাল্ক ইমপোর্ট" else "Excel / CSV Bulk Upload",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Target Selector Chips
                Text(
                    text = if (language == CampLanguage.BANGLA) "কি ধরনের তথ্য আপলোড করতে চান?" else "Select import target:",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedTarget == "JUDGES",
                        onClick = {
                            selectedTarget = "JUDGES"
                            importResultMsg = null
                        },
                        label = { Text("বিচারকমণ্ডলী", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1D4ED8),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedTarget == "VOLUNTEERS",
                        onClick = {
                            selectedTarget = "VOLUNTEERS"
                            importResultMsg = null
                        },
                        label = { Text("ভলান্টিয়ার", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0F766E),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedTarget == "OFFICERS",
                        onClick = {
                            selectedTarget = "OFFICERS"
                            importResultMsg = null
                        },
                        label = { Text("দায়িত্বপ্রাপ্ত কর্মকর্তা", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFB45309),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1.3f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // File Upload Button & Template Insert
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("*/*") },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == CampLanguage.BANGLA) "ফাইল বাছুন (.csv)" else "Browse CSV", fontSize = 11.sp)
                    }

                    TextButton(
                        onClick = { spreadsheetText = sampleTemplate },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == CampLanguage.BANGLA) "নমুনা ফরম্যাট বসান" else "Load Template", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Hints
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = headersHint,
                        fontSize = 10.sp,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Large Input Box for CSV / Excel Paste
                OutlinedTextField(
                    value = spreadsheetText,
                    onValueChange = {
                        spreadsheetText = it
                        importResultMsg = null
                    },
                    placeholder = {
                        Text(
                            text = "এক্সেল বা গুগল শিট থেকে কপি করে এখানে পেস্ট করুন, অথবা ফাইল আপলোড করুন...",
                            fontSize = 11.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("batch_import_text_input"),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                )

                if (importResultMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = if (isSuccess) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = importResultMsg.orEmpty(),
                            color = if (isSuccess) Color(0xFF15803D) else Color(0xFFDC2626),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (language == CampLanguage.BANGLA) "বন্ধ করুন" else "Close")
                    }

                    Button(
                        onClick = {
                            if (spreadsheetText.isBlank()) {
                                importResultMsg = if (language == CampLanguage.BANGLA) "দয়া করে এক্সেল ডেটা পেস্ট করুন বা ফাইল আপলোড করুন।" else "Please enter data or choose a file."
                                isSuccess = false
                            } else {
                                val count = when (selectedTarget) {
                                    "JUDGES" -> onImportJudges(spreadsheetText)
                                    "VOLUNTEERS" -> onImportVolunteers(spreadsheetText)
                                    else -> onImportDutyOfficers(spreadsheetText)
                                }
                                if (count > 0) {
                                    isSuccess = true
                                    importResultMsg = if (language == CampLanguage.BANGLA)
                                        "সফলভাবে $count টি তথ্য ডাটাবেজে যুক্ত হয়েছে!"
                                    else
                                        "Successfully imported $count records!"
                                } else {
                                    isSuccess = false
                                    importResultMsg = if (language == CampLanguage.BANGLA)
                                        "কোনো সঠিক সারি পাওয়া যায়নি। ফরম্যাট পরীক্ষা করুন।"
                                    else
                                        "No valid rows found. Please check format."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        modifier = Modifier.weight(1.2f).testTag("batch_import_submit_btn")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (language == CampLanguage.BANGLA) "ইমপোর্ট সম্পন্ন করুন" else "Import All")
                    }
                }
            }
        }
    }
}
