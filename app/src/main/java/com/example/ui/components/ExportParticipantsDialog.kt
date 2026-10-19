package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.data.model.ParticipantEntity
import com.example.ui.theme.LscEmeraldLight
import com.example.ui.theme.LscEmeraldPrimary
import com.example.util.CsvExportHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream

@Composable
fun ExportParticipantsDialog(
    language: CampLanguage,
    allParticipants: List<ParticipantEntity>,
    filteredParticipants: List<ParticipantEntity>,
    searchQuery: String,
    onDismiss: () -> Unit,
    onOpenDatabaseBackup: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Scope selection: whether exporting currently filtered subset or full participant list
    var exportScopeAll by remember {
        mutableStateOf(searchQuery.isBlank() || filteredParticipants.size == allParticipants.size)
    }

    val targetParticipants = if (exportScopeAll) allParticipants else filteredParticipants
    val csvString = remember(targetParticipants) {
        CsvExportHelper.generateParticipantsCsv(targetParticipants)
    }

    var copiedToClipboard by remember { mutableStateOf(false) }
    var saveStatusMessage by remember { mutableStateOf<String?>(null) }

    // Launcher for saving CSV file directly to user chosen destination
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream: OutputStream ->
                        outputStream.write(csvString.toByteArray(Charsets.UTF_8))
                        outputStream.flush()
                    }
                    withContext(Dispatchers.Main) {
                        saveStatusMessage = if (language == CampLanguage.BANGLA)
                            "সিএসভি ফাইল সফলভাবে সংরক্ষণ করা হয়েছে!"
                        else
                            "CSV report saved successfully!"
                        Toast.makeText(context, saveStatusMessage, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        val errorMsg = if (language == CampLanguage.BANGLA)
                            "সংরক্ষণ করতে সমস্যা হয়েছে: ${e.localizedMessage}"
                        else
                            "Failed to save: ${e.localizedMessage}"
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
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
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 680.dp)
                .padding(8.dp)
                .testTag("export_participants_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
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
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "অংশগ্রহণকারী রিপোর্ট এক্সপোর্ট"
                                else
                                    "Export Participant Report",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "ক্যাম্প প্রশাসনের জন্য CSV এক্সপোর্ট উইজার্ড"
                                else
                                    "CSV export wizard for camp administration",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Selection Scope: If search filter is active
                if (searchQuery.isNotBlank() && filteredParticipants.size != allParticipants.size) {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "কোন তালিকাটি এক্সপোর্ট করতে চান?"
                                else
                                    "Select records to export:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = !exportScopeAll,
                                    onClick = { exportScopeAll = false },
                                    label = {
                                        Text(
                                            text = if (language == CampLanguage.BANGLA)
                                                "বর্তমান সার্চের তালিকা (${filteredParticipants.size} জন)"
                                            else
                                                "Current search (${filteredParticipants.size})"
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF0284C7),
                                        selectedLabelColor = Color.White
                                    )
                                )
                                FilterChip(
                                    selected = exportScopeAll,
                                    onClick = { exportScopeAll = true },
                                    label = {
                                        Text(
                                            text = if (language == CampLanguage.BANGLA)
                                                "সকল অংশগ্রহণকারী (${allParticipants.size} জন)"
                                            else
                                                "All participants (${allParticipants.size})"
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = LscEmeraldPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Summary Card
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "রিপোর্টে অন্তর্ভুক্ত তথ্য:"
                                else
                                    "Report Details:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF166534)
                            )
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "মোট ${targetParticipants.size} জন রেকর্ড"
                                else
                                    "${targetParticipants.size} records",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = LscEmeraldPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "• কলামসমূহ: ক্রমিক, ক্যাম্প আইডি, ফাইল নং, নাম (বাংলা/ইংরেজি), বয়স, লিঙ্গ, জেলা, দল, সাব-গ্রুপ, রুম ও বেড নম্বর, খাবার পছন্দ, অভিভাবক ও ফোন, চেক-ইন স্থিতি, চিকিৎসা নোট, হাজিরা হার এবং ক্যাম্প স্কোর।"
                            else
                                "• Columns: SL, Camp ID, File No, Name (Bn/En), Age, Gender, District, Group, Sub-group, Room & Bed, Meal Pref, Guardian & Phone, Check-in Status, Medical Flag, Attendance %, Total Score.",
                            fontSize = 11.sp,
                            color = Color(0xFF1E293B),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "UTF-8 BOM এনকোডিং নিশ্চিত (Excel ও Google Sheets-এ বাংলা লেখা পরিষ্কার দেখাবে)"
                                else
                                    "UTF-8 BOM enabled (Unicode Bengali opens correctly in Excel & Sheets)",
                                fontSize = 10.sp,
                                color = Color(0xFF15803D),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // CSV Data Preview Box
                Text(
                    text = if (language == CampLanguage.BANGLA) "সিএসভি ডেটা প্রিভিউ:" else "CSV Data Preview:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                        .horizontalScroll(rememberScrollState())
                        .verticalScroll(rememberScrollState())
                ) {
                    val previewLines = csvString.lines().take(7).joinToString("\n")
                    Text(
                        text = previewLines,
                        color = Color(0xFFE2E8F0),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }

                saveStatusMessage?.let { statusMsg ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = statusMsg,
                        color = Color(0xFF16A34A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Row
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Share / Send via FileProvider
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                try {
                                    val file = CsvExportHelper.createCsvCacheFile(
                                        context = context,
                                        participants = targetParticipants
                                    )
                                    withContext(Dispatchers.Main) {
                                        val shareIntent = CsvExportHelper.getShareIntent(
                                            context = context,
                                            file = file,
                                            chooserTitle = if (language == CampLanguage.BANGLA)
                                                "অংশগ্রহণকারী সিএসভি রিপোর্ট পাঠান"
                                            else
                                                "Share Participant CSV Report"
                                        )
                                        context.startActivity(shareIntent)
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "শেয়ার করতে সমস্যা হয়েছে: ${e.localizedMessage}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("export_share_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "ফাইল শেয়ার করুন (WhatsApp / Drive / Gmail)"
                            else
                                "Share CSV File (WhatsApp / Drive / Email)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // 2. Save as File (System document picker)
                    Button(
                        onClick = {
                            val defaultName = "LSC_Camp_Participants_${System.currentTimeMillis()}.csv"
                            createDocumentLauncher.launch(defaultName)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("export_save_button")
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "ডিভাইসে ডাউনলোড / সেভ করুন"
                            else
                                "Save / Download to Device",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Full SQLite Room Database Backup Option
                    if (onOpenDatabaseBackup != null) {
                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onOpenDatabaseBackup()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F766E)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("export_open_db_backup_btn")
                        ) {
                            Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "সম্পূর্ণ SQLite রুম ডাটাবেজ ব্যাকআপ নিন (.db)"
                                else
                                    "Backup Full SQLite Room Database (.db)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // 3. Copy to Clipboard & Cancel Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val success = CsvExportHelper.copyToClipboard(context, csvString)
                                if (success) {
                                    copiedToClipboard = true
                                    Toast.makeText(
                                        context,
                                        if (language == CampLanguage.BANGLA) "ক্লিপবোর্ডে কপি হয়েছে!" else "Copied to clipboard!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("export_copy_button")
                        ) {
                            Icon(
                                imageVector = if (copiedToClipboard) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = if (copiedToClipboard) LscEmeraldPrimary else Color.DarkGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (copiedToClipboard)
                                    (if (language == CampLanguage.BANGLA) "কপি হয়েছে" else "Copied")
                                else
                                    (if (language == CampLanguage.BANGLA) "ক্লিপবোর্ডে কপি" else "Copy CSV"),
                                fontSize = 12.sp,
                                color = if (copiedToClipboard) LscEmeraldPrimary else Color.DarkGray
                            )
                        }

                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(0.7f)
                                .height(44.dp)
                        ) {
                            Text(
                                text = if (language == CampLanguage.BANGLA) "বন্ধ করুন" else "Close",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
