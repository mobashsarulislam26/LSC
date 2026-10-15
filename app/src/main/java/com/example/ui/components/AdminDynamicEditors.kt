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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

/**
/ * Dialog for Adding or Editing a Camp Skill Station.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStationDialog(
    station: StationEntity? = null,
    isBangla: Boolean = true,
    onDismiss: () -> Unit,
    onSave: (StationEntity) -> Unit
) {
    var nameBn by remember { mutableStateOf(station?.nameBn ?: "") }
    var nameEn by remember { mutableStateOf(station?.nameEn ?: "") }
    var moderatorName by remember { mutableStateOf(station?.moderatorName ?: "") }
    var location by remember { mutableStateOf(station?.location ?: "") }
    var objectiveBn by remember { mutableStateOf(station?.objectiveBn ?: "") }
    var iconKey by remember { mutableStateOf(station?.iconKey ?: "quran") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (station == null)
                    (if (isBangla) "নতুন দক্ষতা স্টেশন যুক্ত করুন" else "Add New Skill Station")
                else
                    (if (isBangla) "স্টেশন সম্পাদনা করুন" else "Edit Skill Station"),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = nameBn,
                    onValueChange = { nameBn = it },
                    label = { Text(if (isBangla) "স্টেশনের নাম (বাংলা)" else "Station Name (Bangla)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("station_name_bn_input")
                )

                OutlinedTextField(
                    value = nameEn,
                    onValueChange = { nameEn = it },
                    label = { Text(if (isBangla) "স্টেশনের নাম (ইংরেজি)" else "Station Name (English)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("station_name_en_input")
                )

                OutlinedTextField(
                    value = moderatorName,
                    onValueChange = { moderatorName = it },
                    label = { Text(if (isBangla) "স্টেশন মডারেটরের নাম" else "Moderator Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(if (isBangla) "স্থান / লোকেশন" else "Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = objectiveBn,
                    onValueChange = { objectiveBn = it },
                    label = { Text(if (isBangla) "মূল উদ্দেশ্য ও বিষয়বস্তু" else "Objective") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameBn.isNotBlank()) {
                        val newOrUpdated = StationEntity(
                            id = station?.id ?: 0,
                            nameBn = nameBn.trim(),
                            nameEn = nameEn.ifBlank { nameBn }.trim(),
                            moderatorName = moderatorName.trim(),
                            location = location.trim(),
                            objectiveBn = objectiveBn.trim(),
                            iconKey = iconKey
                        )
                        onSave(newOrUpdated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                modifier = Modifier.testTag("save_station_btn")
            ) {
                Text(if (isBangla) "সংরক্ষণ করুন" else "Save Station")
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
 * Dialog for Adding or Editing a Daily Schedule Event.
 */
@Composable
fun EditScheduleEventDialog(
    event: ScheduleEventEntity? = null,
    defaultDayNo: Int = 1,
    isBangla: Boolean = true,
    onDismiss: () -> Unit,
    onSave: (ScheduleEventEntity) -> Unit
) {
    var dayNo by remember { mutableIntStateOf(event?.dayNo ?: defaultDayNo) }
    var timeSlot by remember { mutableStateOf(event?.timeSlot ?: "08:00 AM - 09:00 AM") }
    var titleBn by remember { mutableStateOf(event?.titleBn ?: "") }
    var titleEn by remember { mutableStateOf(event?.titleEn ?: "") }
    var location by remember { mutableStateOf(event?.location ?: "") }
    var category by remember { mutableStateOf(event?.category ?: "WORKSHOP") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (event == null)
                    (if (isBangla) "নতুন সময়সূচী কার্যক্রম যুক্ত করুন" else "Add Schedule Event")
                else
                    (if (isBangla) "সময়সূচী সম্পাদনা করুন" else "Edit Schedule Event"),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
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
                    Text(
                        text = if (isBangla) "ক্যাম্প দিবস:" else "Camp Day:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        (1..4).forEach { d ->
                            FilterChip(
                                selected = dayNo == d,
                                onClick = { dayNo = d },
                                label = { Text("দিন $d") }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = timeSlot,
                    onValueChange = { timeSlot = it },
                    label = { Text(if (isBangla) "সময়সীমা (e.g. 08:00 AM - 09:00 AM)" else "Time Slot") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = titleBn,
                    onValueChange = { titleBn = it },
                    label = { Text(if (isBangla) "কার্যক্রমের নাম (বাংলা)" else "Activity Title (Bangla)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("schedule_title_bn_input")
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(if (isBangla) "স্থান / লোকেশন" else "Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titleBn.isNotBlank()) {
                        val newOrUpdated = ScheduleEventEntity(
                            id = event?.id ?: 0,
                            dayNo = dayNo,
                            timeSlot = timeSlot.trim(),
                            titleBn = titleBn.trim(),
                            titleEn = titleEn.ifBlank { titleBn }.trim(),
                            location = location.trim(),
                            category = category
                        )
                        onSave(newOrUpdated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                modifier = Modifier.testTag("save_schedule_btn")
            ) {
                Text(if (isBangla) "সংরক্ষণ করুন" else "Save Schedule")
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
 * Dialog for Adding or Editing Accommodation Room Assignments.
 */
@Composable
fun EditRoomDialog(
    room: RoomEntity? = null,
    groups: List<CampGroupEntity> = emptyList(),
    isBangla: Boolean = true,
    onDismiss: () -> Unit,
    onSave: (RoomEntity) -> Unit
) {
    var buildingName by remember { mutableStateOf(room?.buildingName ?: "তাজমহল হল") }
    var roomNo by remember { mutableStateOf(room?.roomNo ?: "") }
    var floor by remember { mutableStateOf(room?.floor ?: "১ম তলা") }
    var capacityStr by remember { mutableStateOf(room?.capacity?.toString() ?: "12") }
    var assignedGroup by remember { mutableStateOf(room?.assignedGroupId ?: "bukhari") }
    var inChargeName by remember { mutableStateOf(room?.inChargeName ?: "") }
    var inChargePhone by remember { mutableStateOf(room?.inChargePhone ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (room == null)
                    (if (isBangla) "নতুন আবাসন রুম যোগ করুন" else "Add New Room")
                else
                    (if (isBangla) "রুমের তথ্য সংশোধন করুন" else "Edit Room Info"),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = buildingName,
                    onValueChange = { buildingName = it },
                    label = { Text(if (isBangla) "ভবন / হলের নাম" else "Building Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = roomNo,
                        onValueChange = { roomNo = it },
                        label = { Text(if (isBangla) "রুম নম্বর" else "Room No") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("room_no_input")
                    )

                    OutlinedTextField(
                        value = capacityStr,
                        onValueChange = { capacityStr = it },
                        label = { Text(if (isBangla) "ধারনক্ষমতা (সিট)" else "Capacity") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = inChargeName,
                    onValueChange = { inChargeName = it },
                    label = { Text(if (isBangla) "রুম ইনচার্জ (অভিভাবক)" else "Room In-Charge") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = inChargePhone,
                    onValueChange = { inChargePhone = it },
                    label = { Text(if (isBangla) "ইনচার্জ মোবাইল নম্বর" else "Phone Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (roomNo.isNotBlank()) {
                        val cap = capacityStr.toIntOrNull() ?: 12
                        val updated = RoomEntity(
                            id = room?.id ?: 0,
                            buildingName = buildingName.trim(),
                            roomNo = roomNo.trim(),
                            floor = floor,
                            capacity = cap,
                            assignedCount = room?.assignedCount ?: 0,
                            assignedGroupId = assignedGroup,
                            inChargeName = inChargeName.trim(),
                            inChargePhone = inChargePhone.trim(),
                            notes = room?.notes ?: ""
                        )
                        onSave(updated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                modifier = Modifier.testTag("save_room_btn")
            ) {
                Text(if (isBangla) "সংরক্ষণ করুন" else "Save Room")
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
 * Full Admin Control Center for managing all dynamic options across the camp.
 */
@Composable
fun AdminControlCenterModal(
    stations: List<StationEntity>,
    schedules: List<ScheduleEventEntity>,
    rooms: List<RoomEntity>,
    users: List<UserAccountEntity>,
    isBangla: Boolean = true,
    onDismiss: () -> Unit,
    onAddStation: () -> Unit,
    onEditStation: (StationEntity) -> Unit,
    onDeleteStation: (StationEntity) -> Unit,
    onAddSchedule: () -> Unit,
    onEditSchedule: (ScheduleEventEntity) -> Unit,
    onDeleteSchedule: (ScheduleEventEntity) -> Unit,
    onAddRoom: () -> Unit,
    onEditRoom: (RoomEntity) -> Unit,
    onDeleteRoom: (RoomEntity) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = LscEmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBangla) "ক্যাম্প ডায়নামিক কনফিগারেশন কন্ট্রোল" else "Camp Dynamic Control Panel",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Tab switcher
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(if (isBangla) "দক্ষতা স্টেশন" else "Stations") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(if (isBangla) "সময়সূচী" else "Schedule") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text(if (isBangla) "আবাসন ও রুম" else "Rooms") }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text(if (isBangla) "স্টাফ অ্যাকাউন্ট" else "Users") }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(modifier = Modifier.height(380.dp)) {
                    when (selectedTab) {
                        0 -> { // Stations
                            Column(modifier = Modifier.fillMaxSize()) {
                                Button(
                                    onClick = onAddStation,
                                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isBangla) "নতুন স্টেশন যুক্ত করুন" else "Add New Station")
                                }

                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(stations) { st ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(st.nameBn, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text("মডারেটর: ${st.moderatorName} | ${st.location}", fontSize = 11.sp, color = Color.Gray)
                                                }
                                                Row {
                                                    IconButton(onClick = { onEditStation(st) }) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = LscEmeraldPrimary, modifier = Modifier.size(18.dp))
                                                    }
                                                    IconButton(onClick = { onDeleteStation(st) }) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        1 -> { // Schedule
                            Column(modifier = Modifier.fillMaxSize()) {
                                Button(
                                    onClick = onAddSchedule,
                                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isBangla) "নতুন সময়সূচী সেশন যুক্ত করুন" else "Add Schedule Event")
                                }

                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(schedules) { ev ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("দিন ${ev.dayNo} | ${ev.timeSlot}", fontSize = 11.sp, color = LscEmeraldPrimary, fontWeight = FontWeight.Bold)
                                                    Text(ev.titleBn, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text(ev.location, fontSize = 11.sp, color = Color.Gray)
                                                }
                                                Row {
                                                    IconButton(onClick = { onEditSchedule(ev) }) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = LscEmeraldPrimary, modifier = Modifier.size(18.dp))
                                                    }
                                                    IconButton(onClick = { onDeleteSchedule(ev) }) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> { // Rooms
                            Column(modifier = Modifier.fillMaxSize()) {
                                Button(
                                    onClick = onAddRoom,
                                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isBangla) "নতুন আবাসন রুম যুক্ত করুন" else "Add Room")
                                }

                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(rooms) { rm ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("${rm.buildingName} - রুম ${rm.roomNo}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text("ধারনক্ষমতা: ${rm.capacity} জন | ইনচার্জ: ${rm.inChargeName}", fontSize = 11.sp, color = Color.Gray)
                                                }
                                                Row {
                                                    IconButton(onClick = { onEditRoom(rm) }) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = LscEmeraldPrimary, modifier = Modifier.size(18.dp))
                                                    }
                                                    IconButton(onClick = { onDeleteRoom(rm) }) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        3 -> { // Staff Accounts
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(users) { usr ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = LscEmeraldPrimary)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(usr.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text("${usr.email} | ভূমিকা: ${usr.role}", fontSize = 11.sp, color = Color.Gray)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(if (isBangla) "সম্পন্ন" else "Done")
            }
        }
    )
}
