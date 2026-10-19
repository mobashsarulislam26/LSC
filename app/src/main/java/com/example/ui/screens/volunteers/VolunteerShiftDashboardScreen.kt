package com.example.ui.screens.volunteers

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.CampLanguage
import com.example.data.model.VolunteerShiftEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VolunteerShiftDashboardScreen(
    language: CampLanguage,
    shifts: List<VolunteerShiftEntity>,
    onConfirmAttendance: (Long) -> Unit,
    onCheckInShift: (Long) -> Unit,
    onAddShift: (VolunteerShiftEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDayFilter by remember { mutableIntStateOf(0) } // 0 = All Days, 1..5 = Day
    var selectedStatusFilter by remember { mutableStateOf("ALL") } // "ALL", "SCHEDULED", "CONFIRMED", "CHECKED_IN", "COMPLETED"
    var showAddShiftDialog by remember { mutableStateOf(false) }

    val filteredShifts = remember(shifts, selectedDayFilter, selectedStatusFilter) {
        shifts.filter { shift ->
            val matchesDay = if (selectedDayFilter == 0) true else shift.dayNo == selectedDayFilter
            val matchesStatus = if (selectedStatusFilter == "ALL") true else shift.status.equals(selectedStatusFilter, ignoreCase = true)
            matchesDay && matchesStatus
        }
    }

    val totalShifts = shifts.size
    val confirmedCount = shifts.count { it.status == "CONFIRMED" || it.status == "CHECKED_IN" || it.status == "COMPLETED" }
    val scheduledCount = shifts.count { it.status == "SCHEDULED" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header & Volunteer KPI Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LscEmeraldPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth().testTag("volunteer_shift_header_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "ভলান্টিয়ার শিফট & ডিউটি ড্যাশবোর্ড" else "Volunteer Shift Dashboard",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "ডিউটি অ্যাসাইনমেন্ট, সময়সূচি ও উপস্থিতি নিশ্চিতকরণ" else "Task assignments, shift schedules & attendance confirmation",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        IconButton(
                            onClick = { showAddShiftDialog = true },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(LscGoldSecondary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Shift", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 KPI Summary Tiles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "মোট শিফট" else "Total Shifts",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "$totalShifts",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "উপস্থিতি নিশ্চিত" else "Confirmed",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "$confirmedCount",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LscGoldLight
                                )
                            }
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "অপেক্ষামান" else "Scheduled",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "$scheduledCount",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Day Selector Bar
        item {
            Column {
                Text(
                    text = if (language == CampLanguage.BANGLA) "ক্যাম্প দিন নির্বাচন করুন" else "Select Camp Day",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LscSlateDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedDayFilter == 0,
                            onClick = { selectedDayFilter = 0 },
                            label = { Text(if (language == CampLanguage.BANGLA) "সকল দিন" else "All Days", fontSize = 11.sp) }
                        )
                    }
                    items(5) { index ->
                        val day = index + 1
                        FilterChip(
                            selected = selectedDayFilter == day,
                            onClick = { selectedDayFilter = day },
                            label = { Text(if (language == CampLanguage.BANGLA) "দিন $day" else "Day $day", fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Status Filter Chips Bar
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val statuses = listOf(
                    "ALL" to (if (language == CampLanguage.BANGLA) "সব স্থিতি" else "All Status"),
                    "SCHEDULED" to (if (language == CampLanguage.BANGLA) "অপেক্ষমাণ" else "Scheduled"),
                    "CONFIRMED" to (if (language == CampLanguage.BANGLA) "নিশ্চিতকৃত" else "Confirmed"),
                    "CHECKED_IN" to (if (language == CampLanguage.BANGLA) "ডিউটিতে চলমান" else "Checked In"),
                    "COMPLETED" to (if (language == CampLanguage.BANGLA) "সম্পন্ন" else "Completed")
                )
                items(statuses) { (key, labelText) ->
                    FilterChip(
                        selected = selectedStatusFilter == key,
                        onClick = { selectedStatusFilter = key },
                        label = { Text(labelText, fontSize = 11.sp) }
                    )
                }
            }
        }

        // Empty State or Shift Cards
        if (filteredShifts.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "কোনো ভলান্টিয়ার শিফট পাওয়া যায়নি" else "No volunteer shifts found",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LscSlateDark
                        )
                        Text(
                            text = if (language == CampLanguage.BANGLA) "অন্য দিন বা ফিল্টার নির্বাচন করে নতুন শিফট যোগ করুন।" else "Select another day or click '+' to assign a new shift.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(filteredShifts, key = { it.id }) { shift ->
                VolunteerShiftCard(
                    shift = shift,
                    language = language,
                    onConfirmAttendance = { onConfirmAttendance(shift.id) },
                    onCheckInShift = { onCheckInShift(shift.id) }
                )
            }
        }
    }

    if (showAddShiftDialog) {
        AddVolunteerShiftDialog(
            language = language,
            onDismiss = { showAddShiftDialog = false },
            onSave = { newShift ->
                onAddShift(newShift)
                showAddShiftDialog = false
            }
        )
    }
}

@Composable
fun VolunteerShiftCard(
    shift: VolunteerShiftEntity,
    language: CampLanguage,
    onConfirmAttendance: () -> Unit,
    onCheckInShift: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag("volunteer_shift_card_${shift.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Day Badge & Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (language == CampLanguage.BANGLA) "দিন ${shift.dayNo}" else "Day ${shift.dayNo}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D4ED8),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                val (statusBg, statusFg, statusLabel) = when (shift.status.uppercase()) {
                    "CONFIRMED" -> Triple(
                        Color(0xFFECFDF5),
                        Color(0xFF047857),
                        if (language == CampLanguage.BANGLA) "✅ উপস্থিতি নিশ্চিত" else "✅ Confirmed"
                    )
                    "CHECKED_IN" -> Triple(
                        Color(0xFFFEF3C7),
                        Color(0xFFB45309),
                        if (language == CampLanguage.BANGLA) "⏱️ ডিউটিতে চলমান" else "⏱️ On Duty"
                    )
                    "COMPLETED" -> Triple(
                        Color(0xFFF3E8FF),
                        Color(0xFF7E22CE),
                        if (language == CampLanguage.BANGLA) "🏆 সম্পন্ন" else "🏆 Completed"
                    )
                    else -> Triple(
                        Color(0xFFFFFBEB),
                        Color(0xFFD97706),
                        if (language == CampLanguage.BANGLA) "⏳ অপেক্ষমাণ (Unconfirmed)" else "⏳ Scheduled"
                    )
                }

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, statusFg.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = statusLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusFg,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Task Title
            Text(
                text = shift.taskTitleBn,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = LscSlateDark
            )
            if (shift.taskTitleEn.isNotBlank()) {
                Text(
                    text = shift.taskTitleEn,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Location & Time Info Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = LscEmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = shift.departmentOrLocation,
                        fontSize = 11.sp,
                        color = LscSlateDark,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${shift.startTime} - ${shift.endTime}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LscSlateDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Assigned Volunteer Details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "দায়িত্বপ্রাপ্ত: ${shift.volunteerName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LscSlateDark
                    )
                    Text(
                        text = shift.volunteerRole,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            if (shift.taskDescription.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "📋 কাজ: ${shift.taskDescription}",
                    fontSize = 11.sp,
                    color = Color(0xFF475569)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Attendance Confirmation & Check-In Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (shift.status == "SCHEDULED") {
                    Button(
                        onClick = onConfirmAttendance,
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "উপস্থিতি নিশ্চিত করুন" else "Confirm Attendance",
                            fontSize = 11.sp
                        )
                    }
                } else if (shift.status == "CONFIRMED") {
                    Button(
                        onClick = onCheckInShift,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "ডিউটি শুরু করুন (Check In)" else "Start Duty (Check In)",
                            fontSize = 11.sp
                        )
                    }
                } else if (shift.status == "CHECKED_IN") {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA) "ডিউটি চলমান রয়েছে" else "Duty Currently Active",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddVolunteerShiftDialog(
    language: CampLanguage,
    onDismiss: () -> Unit,
    onSave: (VolunteerShiftEntity) -> Unit
) {
    var volunteerName by remember { mutableStateOf("") }
    var volunteerRole by remember { mutableStateOf("ক্যাম্প ভলান্টিয়ার") }
    var taskTitleBn by remember { mutableStateOf("") }
    var taskTitleEn by remember { mutableStateOf("") }
    var departmentOrLocation by remember { mutableStateOf("") }
    var dayNo by remember { mutableIntStateOf(1) }
    var startTime by remember { mutableStateOf("সকাল ০৮:০০") }
    var endTime by remember { mutableStateOf("দুপুর ১২:০০") }
    var taskDescription by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (language == CampLanguage.BANGLA) "নতুন ভলান্টিয়ার শিফট অ্যাসাইন" else "Assign New Volunteer Shift",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = volunteerName,
                    onValueChange = { volunteerName = it },
                    label = { Text("ভলান্টিয়ারের নাম") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = taskTitleBn,
                    onValueChange = { taskTitleBn = it },
                    label = { Text("কাজের শিরোনাম (বাংলা)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = departmentOrLocation,
                    onValueChange = { departmentOrLocation = it },
                    label = { Text("বিভাগ / স্থান (e.g. সেন্ট্রাল ডাইনিং)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("শুরুর সময়") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("শেষের সময়") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("কাজের বিস্তারিত বিবরণ") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (volunteerName.isNotBlank() && taskTitleBn.isNotBlank()) {
                        onSave(
                            VolunteerShiftEntity(
                                volunteerName = volunteerName,
                                volunteerRole = volunteerRole,
                                taskTitleBn = taskTitleBn,
                                taskTitleEn = taskTitleEn,
                                departmentOrLocation = departmentOrLocation.ifBlank { "ক্যাম্প গ্রাউন্ড" },
                                dayNo = dayNo,
                                startTime = startTime,
                                endTime = endTime,
                                taskDescription = taskDescription,
                                status = "SCHEDULED"
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary)
            ) {
                Text("সংরক্ষণ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}
