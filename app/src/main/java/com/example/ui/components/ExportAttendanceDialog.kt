package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AttendanceEventEntity
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.CampLanguage
import com.example.data.model.StationEntity
import com.example.ui.theme.LscEmeraldLight
import com.example.ui.theme.LscEmeraldPrimary
import com.example.ui.theme.LscGoldSecondary
import com.example.ui.theme.LscSlateDark
import com.example.util.CsvExportHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExportAttendanceDialog(
    language: CampLanguage,
    attendanceRecords: List<AttendanceRecordEntity>,
    attendanceEvents: List<AttendanceEventEntity>,
    stations: List<StationEntity>,
    initialEventId: Long? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isBangla = language == CampLanguage.BANGLA

    var selectedEventFilter by remember { mutableStateOf<Long?>(initialEventId) }
    var selectedStatusFilter by remember { mutableStateOf<String?>(null) } // "ALL", "PRESENT", "LATE", "ABSENT"

    val filteredRecords = remember(attendanceRecords, selectedEventFilter, selectedStatusFilter) {
        attendanceRecords.filter { record ->
            val matchesEvent = selectedEventFilter == null || record.eventId == selectedEventFilter
            val matchesStatus = selectedStatusFilter == null || record.status.equals(selectedStatusFilter, ignoreCase = true)
            matchesEvent && matchesStatus
        }
    }

    val csvString = remember(filteredRecords, attendanceEvents, stations) {
        CsvExportHelper.generateAttendanceCsv(filteredRecords, attendanceEvents, stations)
    }

    var copiedToClipboard by remember { mutableStateOf(false) }
    var saveStatusMessage by remember { mutableStateOf<String?>(null) }

    // Launcher for saving CSV file directly to user chosen destination
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openOutputStream(uri)?.use { outputStream: OutputStream ->
                            outputStream.write(csvString.toByteArray(Charsets.UTF_8))
                            outputStream.flush()
                        }
                    }
                    saveStatusMessage = if (isBangla)
                        "✅ ফাইল সফলভাবে সংরক্ষণ করা হয়েছে!"
                    else
                        "✅ CSV File successfully saved!"
                    Toast.makeText(context, saveStatusMessage, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    saveStatusMessage = if (isBangla)
                        "সংরক্ষণ ব্যর্থ: ${e.localizedMessage}"
                    else
                        "Save failed: ${e.localizedMessage}"
                    Toast.makeText(context, saveStatusMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val presentCount = filteredRecords.count { it.status.equals("PRESENT", ignoreCase = true) }
    val lateCount = filteredRecords.count { it.status.equals("LATE", ignoreCase = true) }
    val absentCount = filteredRecords.count { it.status.equals("ABSENT", ignoreCase = true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .testTag("export_attendance_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            tonalElevation = 6.dp
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
                                .clip(CircleShape)
                                .background(LscEmeraldLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = null,
                                tint = LscEmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBangla) "হাজিরা রেকর্ড CSV এক্সপোর্ট" else "Export Attendance CSV",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = LscSlateDark
                            )
                            Text(
                                text = if (isBangla)
                                    "রুম ডাটাবেজ থেকে রিয়েল-টাইম হাজিরা এক্সপোর্ট ও শেয়ার"
                                else
                                    "Export and share real-time attendance from Room DB",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable filters and preview content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Quick Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            label = if (isBangla) "মোট রেকর্ড" else "Total Records",
                            value = "${filteredRecords.size}",
                            color = LscEmeraldPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = if (isBangla) "উপস্থিত" else "Present",
                            value = "$presentCount",
                            color = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = if (isBangla) "দেরিতে" else "Late",
                            value = "$lateCount",
                            color = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = if (isBangla) "অনুপস্থিত" else "Absent",
                            value = "$absentCount",
                            color = Color(0xFFEF4444),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Event / Station Filter Row
                    Text(
                        text = if (isBangla) "ইভেন্ট / স্টেশন নির্বাচন:" else "Select Event / Station:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = LscSlateDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedEventFilter == null,
                            onClick = { selectedEventFilter = null },
                            label = { Text(if (isBangla) "সকল ইভেন্ট (${attendanceRecords.size})" else "All Events", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LscEmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )

                        attendanceEvents.forEach { ev ->
                            val count = attendanceRecords.count { it.eventId == ev.id }
                            FilterChip(
                                selected = selectedEventFilter == ev.id,
                                onClick = { selectedEventFilter = if (selectedEventFilter == ev.id) null else ev.id },
                                label = {
                                    Text(
                                        text = "${if (isBangla) ev.titleBn else ev.titleEn} ($count)",
                                        fontSize = 11.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LscEmeraldPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        stations.forEach { st ->
                            val count = attendanceRecords.count { it.eventId == st.id.toLong() }
                            FilterChip(
                                selected = selectedEventFilter == st.id.toLong(),
                                onClick = {
                                    selectedEventFilter = if (selectedEventFilter == st.id.toLong()) null else st.id.toLong()
                                },
                                label = {
                                    Text(
                                        text = "স্টেশন ${st.id}: ${if (isBangla) st.nameBn else st.nameEn} ($count)",
                                        fontSize = 11.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LscGoldSecondary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Filter Row
                    Text(
                        text = if (isBangla) "স্ট্যাটাস ফিল্টার:" else "Status Filter:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = LscSlateDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedStatusFilter == null,
                            onClick = { selectedStatusFilter = null },
                            label = { Text(if (isBangla) "সকল স্ট্যাটাস" else "All Status", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LscEmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedStatusFilter == "PRESENT",
                            onClick = { selectedStatusFilter = if (selectedStatusFilter == "PRESENT") null else "PRESENT" },
                            label = { Text(if (isBangla) "উপস্থিত" else "Present", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF10B981),
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedStatusFilter == "LATE",
                            onClick = { selectedStatusFilter = if (selectedStatusFilter == "LATE") null else "LATE" },
                            label = { Text(if (isBangla) "দেরিতে" else "Late", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFF59E0B),
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedStatusFilter == "ABSENT",
                            onClick = { selectedStatusFilter = if (selectedStatusFilter == "ABSENT") null else "ABSENT" },
                            label = { Text(if (isBangla) "অনুপস্থিত" else "Absent", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFEF4444),
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // CSV Preview
                    Text(
                        text = if (isBangla) "CSV প্রিভিউ (প্রথম ৭ লাইন):" else "CSV Preview (First 7 Lines):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = LscSlateDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        val previewLines = csvString.lines().take(8).joinToString("\n")
                        Text(
                            text = previewLines,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = Color(0xFF334155),
                            modifier = Modifier
                                .padding(12.dp)
                                .horizontalScroll(rememberScrollState())
                        )
                    }

                    saveStatusMessage?.let { statusMsg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = statusMsg,
                            color = if (statusMsg.startsWith("✅")) Color(0xFF16A34A) else Color(0xFFDC2626),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Copy to clipboard
                    OutlinedButton(
                        onClick = {
                            val success = CsvExportHelper.copyToClipboard(context, csvString, "LSC Attendance CSV")
                            copiedToClipboard = success
                            Toast.makeText(
                                context,
                                if (isBangla) "ক্লিপবোর্ডে কপি করা হয়েছে!" else "Copied to clipboard!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_attendance_copy_btn")
                    ) {
                        Icon(
                            imageVector = if (copiedToClipboard) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBangla) "কপি করুন" else "Copy",
                            fontSize = 12.sp
                        )
                    }

                    // Save to device
                    Button(
                        onClick = {
                            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                            val defaultName = "LSC_Camp_Attendance_${timestamp}.csv"
                            createDocumentLauncher.launch(defaultName)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LscGoldSecondary),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("export_attendance_save_btn")
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBangla) "ডিভাইসে সেভ" else "Save File",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Share CSV
                    Button(
                        onClick = {
                            try {
                                val cacheFile = CsvExportHelper.createAttendanceCsvCacheFile(
                                    context = context,
                                    records = filteredRecords,
                                    events = attendanceEvents,
                                    stations = stations
                                )
                                val shareIntent = CsvExportHelper.getAttendanceShareIntent(context, cacheFile)
                                context.startActivity(shareIntent)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "শেয়ার করতে সমস্যা: ${e.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("export_attendance_share_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBangla) "শেয়ার করুন" else "Share CSV",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.DarkGray
            )
        }
    }
}
