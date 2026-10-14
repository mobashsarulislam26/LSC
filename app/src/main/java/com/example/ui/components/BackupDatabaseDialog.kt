package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CampLanguage
import com.example.ui.theme.*
import com.example.util.BackupItem
import com.example.util.BackupResult
import com.example.util.DatabaseBackupHelper
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Material 3 Dialog for the 'Backup Database' feature.
 * Saves a copy of the SQLite Room file to the device's document storage
 * for manual recovery, with SAF export and sharing support.
 */
@Composable
fun BackupDatabaseDialog(
    language: CampLanguage,
    participantCount: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isProcessing by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    var latestBackupFile by remember { mutableStateOf<File?>(null) }
    var showInstructions by remember { mutableStateOf(false) }

    // Live DB stats and existing backup items
    val liveStats = remember { DatabaseBackupHelper.getLiveDatabaseStats(context) }
    var existingBackups by remember { mutableStateOf(listOf<BackupItem>()) }

    // Refresh existing backups list
    fun reloadBackups() {
        existingBackups = DatabaseBackupHelper.listExistingBackups(context)
    }

    LaunchedEffect(Unit) {
        reloadBackups()
    }

    // Storage Access Framework (SAF) Document Picker Launcher
    val defaultBackupName = remember {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
        "lsc_camp_database_backup_$timeStamp.db"
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                isProcessing = true
                val res = DatabaseBackupHelper.backupDatabaseToUri(context, uri)
                isProcessing = false
                when (res) {
                    is BackupResult.Success -> {
                        isSuccess = true
                        resultMessage = if (language == CampLanguage.BANGLA)
                            "সফলভাবে ডকুমেন্ট ফোল্ডারে ব্যাকআপ সেভ হয়েছে (${res.formattedSize})"
                        else
                            "Successfully saved backup to chosen document storage (${res.formattedSize})"
                        Toast.makeText(context, resultMessage, Toast.LENGTH_LONG).show()
                    }
                    is BackupResult.Error -> {
                        isSuccess = false
                        resultMessage = res.errorMessage
                    }
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f)
                .testTag("backup_database_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(LscEmeraldPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Storage,
                                contentDescription = null,
                                tint = LscEmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "ডাটাবেজ ব্যাকআপ ও রিকভারি"
                                else
                                    "Backup Database (SQLite Room)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = LscSlateDark
                            )
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "ম্যানুয়াল রিকভারির জন্য ডকুমেন্ট স্টোরেজে ব্যাকআপ"
                                else
                                    "Save SQLite copy to device document storage",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_backup_dialog_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Live Database Health & Stats Card
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (language == CampLanguage.BANGLA) "লাইভ রুম ডাটাবেজ তথ্য" else "Live SQLite Room Database",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = LscSlateDark
                                    )
                                    Surface(
                                        color = Color(0xFFDCFCE7),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "SQLite 3 • Room v6",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF16A34A),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = if (language == CampLanguage.BANGLA) "ডাটাবেজ ফাইল" else "Database Name",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = "lsc_camp_database.db",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = LscSlateDark
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = if (language == CampLanguage.BANGLA) "বর্তমান সাইজ" else "File Size",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = liveStats.second,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = LscEmeraldPrimary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (language == CampLanguage.BANGLA)
                                            "অন্তর্ভুক্ত: $participantCount জন অংশগ্রহণকারী, হাজিরা, স্বাস্থ্য ও রোটেশন"
                                        else
                                            "Includes: $participantCount participants, attendance, health, matrix",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }

                    // Status Message / Alert Banner
                    if (resultMessage != null) {
                        item {
                            Surface(
                                color = if (isSuccess) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (isSuccess) Color(0xFF86EFAC) else Color(0xFFFCA5A5)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("backup_result_banner")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                                        contentDescription = null,
                                        tint = if (isSuccess) Color(0xFF16A34A) else Color(0xFFDC2626),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = resultMessage!!,
                                            color = if (isSuccess) Color(0xFF14532D) else Color(0xFF7F1D1D),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (isSuccess && latestBackupFile != null) {
                                            Text(
                                                text = "পাথ: ${latestBackupFile!!.absolutePath}",
                                                color = Color(0xFF15803D),
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Action Buttons Card (Primary Backup Triggers)
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "ব্যাকআপ অপশনসমূহ" else "Backup Actions",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = LscSlateDark
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Button 1: Instant Document Storage Backup
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            isProcessing = true
                                            resultMessage = null
                                            val res = DatabaseBackupHelper.backupDatabaseToDocuments(context)
                                            isProcessing = false
                                            when (res) {
                                                is BackupResult.Success -> {
                                                    isSuccess = true
                                                    latestBackupFile = res.file
                                                    resultMessage = if (language == CampLanguage.BANGLA)
                                                        "সফলভাবে ডিভাইসের ডকুমেন্টে ব্যাকআপ তৈরি হয়েছে: ${res.fileName} (${res.formattedSize})"
                                                    else
                                                        "Successfully created backup copy: ${res.fileName} (${res.formattedSize})"
                                                    reloadBackups()
                                                    Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show()
                                                }
                                                is BackupResult.Error -> {
                                                    isSuccess = false
                                                    resultMessage = res.errorMessage
                                                }
                                            }
                                        }
                                    },
                                    enabled = !isProcessing,
                                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("backup_to_documents_btn")
                                ) {
                                    if (isProcessing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (language == CampLanguage.BANGLA) "ব্যাকআপ প্রক্রিয়াকরণ..." else "Backing up...",
                                            fontSize = 13.sp
                                        )
                                    } else {
                                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (language == CampLanguage.BANGLA)
                                                "ডিভাইস ডকুমেন্টে ব্যাকআপ নিন (Instant Backup)"
                                            else
                                                "Backup to Device Documents Storage",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Button 2: SAF Document Location Picker
                                OutlinedButton(
                                    onClick = {
                                        createDocumentLauncher.launch(defaultBackupName)
                                    },
                                    enabled = !isProcessing,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("backup_saf_picker_btn")
                                ) {
                                    Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (language == CampLanguage.BANGLA)
                                            "ডকুমেন্ট লোকেশন বেছে নিন (SAF Picker)"
                                        else
                                            "Select Custom Document Folder (SAF)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                // Button 3: Share latest backup if exists
                                if (latestBackupFile != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = {
                                            DatabaseBackupHelper.shareBackupFile(context, latestBackupFile!!)
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1D4ED8)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("share_latest_backup_btn")
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (language == CampLanguage.BANGLA)
                                                "নতুন ব্যাকআপ ফাইল শেয়ার / ড্রাইভে পাঠান"
                                            else
                                                "Share Backup File (Drive / Email / PC)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Existing Backups List on Device
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA)
                                        "ডিভাইসে সংরক্ষিত ব্যাকআপসমূহ (${existingBackups.size})"
                                    else
                                        "Existing Backups on Device (${existingBackups.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = LscSlateDark
                                )
                                TextButton(onClick = { reloadBackups() }) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (language == CampLanguage.BANGLA) "রিফ্রেশ" else "Refresh", fontSize = 11.sp)
                                }
                            }

                            if (existingBackups.isEmpty()) {
                                Surface(
                                    color = Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (language == CampLanguage.BANGLA)
                                            "এখনও কোনো ব্যাকআপ ফাইল তৈরি করা হয়নি। ওপরের বাটনে ট্যাপ করে ব্যাকআপ নিন।"
                                        else
                                            "No backups created yet. Tap the button above to generate one.",
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    items(existingBackups) { backup ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("backup_item_${backup.fileName}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE0F2FE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.InsertDriveFile,
                                            contentDescription = null,
                                            tint = Color(0xFF0284C7),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = backup.fileName,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = LscSlateDark,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${backup.formattedSize} • ${backup.formattedDate}",
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                            if (backup.isValidSqlite) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = Color(0xFFDCFCE7),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "SQLite 3",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF16A34A),
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        DatabaseBackupHelper.shareBackupFile(context, backup.file)
                                    },
                                    modifier = Modifier.testTag("share_btn_${backup.fileName}")
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", tint = LscEmeraldPrimary)
                                }
                            }
                        }
                    }

                    // Manual Recovery Guide Accordion
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7).copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showInstructions = !showInstructions }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (language == CampLanguage.BANGLA) "ম্যানুয়াল রিকভারি নির্দেশিকা" else "Manual Recovery Guide",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF92400E)
                                        )
                                    }
                                    Icon(
                                        imageVector = if (showInstructions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = Color(0xFF92400E)
                                    )
                                }

                                AnimatedVisibility(visible = showInstructions) {
                                    Column(modifier = Modifier.padding(top = 8.dp)) {
                                        Text(
                                            text = if (language == CampLanguage.BANGLA)
                                                "১. ব্যাকআপ ফাইলটি একটি সম্পূর্ণ স্বতন্ত্র SQLite ডাটাবেজ ফাইল (.db)।\n" +
                                                "২. ডিভাইস নষ্ট হলে বা অ্যাপ রি-ইনস্টল করতে হলে এই ফাইলটি সুরক্ষিতভাবে কম্পিউটার, গুগল ড্রাইভ বা পেনড্রাইভে সংরক্ষণ করুন।\n" +
                                                "৩. যেকোনো স্ট্যান্ডার্ড SQLite ব্রাউজার (DB Browser for SQLite) দিয়ে এই ফাইলের সকল টেবিল ও অংশগ্রহণকারীদের পূর্ণ ডাটা পড়া ও এক্সপোর্ট করা যাবে।"
                                            else
                                                "1. The backup file is a clean, self-contained SQLite database (.db).\n" +
                                                "2. In the event of device failure or app reinstallation, keep this file safe on Google Drive, PC, or SD Card.\n" +
                                                "3. All tables and entries can be inspected and restored using DB Browser for SQLite or standard Room restore routines.",
                                            fontSize = 11.sp,
                                            color = Color(0xFF78350F),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Close Button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dismiss_backup_dialog_btn")
                ) {
                    Text(if (language == CampLanguage.BANGLA) "বন্ধ করুন" else "Close")
                }
            }
        }
    }
}
