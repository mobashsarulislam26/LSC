package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import com.example.data.model.CampGroupEntity
import com.example.data.model.CampLanguage
import com.example.data.model.ParticipantEntity
import com.example.data.model.ScheduleEventEntity
import com.example.ui.theme.LscEmeraldPrimary
import com.example.ui.theme.LscSlateDark

/**
 * Dialog to edit a Camp Schedule / Session Event (সময়, সেশন, ভেন্যু)
 */
@Composable
fun EditScheduleDialog(
    event: ScheduleEventEntity,
    language: CampLanguage,
    onDismiss: () -> Unit,
    onSave: (ScheduleEventEntity) -> Unit
) {
    var titleBn by remember { mutableStateOf(event.titleBn) }
    var titleEn by remember { mutableStateOf(event.titleEn) }
    var timeSlot by remember { mutableStateOf(event.timeSlot) }
    var location by remember { mutableStateOf(event.location) }
    var category by remember { mutableStateOf(event.category) }
    var dayNo by remember { mutableStateOf(event.dayNo.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("edit_schedule_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EditCalendar, contentDescription = null, tint = LscEmeraldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "সেশন ও সময়সূচি এডিট" else "Edit Session Schedule",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = titleBn,
                    onValueChange = { titleBn = it },
                    label = { Text("সেশনের শিরোনাম (বাংলা) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_schedule_title_bn")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = titleEn,
                    onValueChange = { titleEn = it },
                    label = { Text("Session Title (English)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = timeSlot,
                        onValueChange = { timeSlot = it },
                        label = { Text("সময় (যেমন: 05:00 - 05:45)") },
                        singleLine = true,
                        modifier = Modifier.weight(1.5f).testTag("input_schedule_timeslot")
                    )

                    OutlinedTextField(
                        value = dayNo,
                        onValueChange = { dayNo = it },
                        label = { Text("ক্যাম্পের দিন (১-৫)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("স্থান / ভেন্যু (Location)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_schedule_location")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("ক্যাটাগরি (Jamaat, Station, Meal, Competition)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text(if (language == CampLanguage.BANGLA) "বাতিল" else "Cancel")
                    }
                    Button(
                        onClick = {
                            val parsedDay = dayNo.toIntOrNull() ?: event.dayNo
                            onSave(
                                event.copy(
                                    titleBn = titleBn.trim(),
                                    titleEn = titleEn.trim(),
                                    timeSlot = timeSlot.trim(),
                                    location = location.trim(),
                                    category = category.trim(),
                                    dayNo = parsedDay
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        modifier = Modifier.weight(1.2f).testTag("save_schedule_btn")
                    ) {
                        Text(if (language == CampLanguage.BANGLA) "আপডেট করুন" else "Save Changes")
                    }
                }
            }
        }
    }
}

/**
 * Dialog to edit Camp Group info (দলনেতা, সাব-গ্রুপ মেন্টর)
 */
@Composable
fun EditGroupDialog(
    group: CampGroupEntity,
    language: CampLanguage,
    onDismiss: () -> Unit,
    onSave: (CampGroupEntity) -> Unit
) {
    var leaderName by remember { mutableStateOf(group.leaderName) }
    var leaderPhone by remember { mutableStateOf(group.leaderPhone) }
    var mentorName by remember { mutableStateOf(group.mentorName) }
    var mentorPhone by remember { mutableStateOf(group.mentorPhone) }
    var subgroupMentors by remember { mutableStateOf(group.subgroupMentors) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("edit_group_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Group, contentDescription = null, tint = Color(group.colorHex))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${group.nameBn} - লিডার ও মেন্টর এডিট",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark,
                            fontSize = 15.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "গ্রুপ লিডার (Group Leader):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = leaderName,
                    onValueChange = { leaderName = it },
                    label = { Text("গ্রুপ লিডারের নাম") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_group_leader_name")
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = leaderPhone,
                    onValueChange = { leaderPhone = it },
                    label = { Text("লিডারের মোবাইল নম্বর") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_group_leader_phone")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "প্রধান মেন্টর / প্রশিক্ষক (Chief Mentor):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = mentorName,
                    onValueChange = { mentorName = it },
                    label = { Text("মেন্টরের নাম") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_group_mentor_name")
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = mentorPhone,
                    onValueChange = { mentorPhone = it },
                    label = { Text("মেন্টরের মোবাইল নম্বর") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "সাব-গ্রুপ মেন্টরদের দায়িত্ব (Subgroup Mentors):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = subgroupMentors,
                    onValueChange = { subgroupMentors = it },
                    label = { Text("সাব-গ্রুপ ভিত্তিক মেন্টরদের নাম") },
                    placeholder = { Text("উদা: সাব-১: তানভীর (০১৭১..), সাব-২: তারেক (০১৮..)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("input_subgroup_mentors")
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text(if (language == CampLanguage.BANGLA) "বাতিল" else "Cancel")
                    }
                    Button(
                        onClick = {
                            onSave(
                                group.copy(
                                    leaderName = leaderName.trim(),
                                    leaderPhone = leaderPhone.trim(),
                                    mentorName = mentorName.trim(),
                                    mentorPhone = mentorPhone.trim(),
                                    subgroupMentors = subgroupMentors.trim()
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        modifier = Modifier.weight(1.2f).testTag("save_group_btn")
                    ) {
                        Text(if (language == CampLanguage.BANGLA) "সংরক্ষণ করুন" else "Save Changes")
                    }
                }
            }
        }
    }
}

/**
 * Dialog to create or edit a camp participant record locally in Room database.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditParticipantDialog(
    participant: ParticipantEntity? = null,
    groups: List<CampGroupEntity>,
    language: CampLanguage,
    onDismiss: () -> Unit,
    onSave: (ParticipantEntity) -> Unit,
    onDelete: ((ParticipantEntity) -> Unit)? = null
) {
    val isEditing = participant != null

    var nameBn by remember { mutableStateOf(participant?.nameBn ?: "") }
    var nameEn by remember { mutableStateOf(participant?.nameEn ?: "") }
    var campCode by remember { mutableStateOf(participant?.campCode ?: "LSC-2026-${(100..999).random()}") }
    var fileNo by remember { mutableStateOf(participant?.fileNo ?: "F-${(1000..9999).random()}") }
    var districtBn by remember { mutableStateOf(participant?.districtBn ?: "ঢাকা") }
    var districtEn by remember { mutableStateOf(participant?.districtEn ?: "Dhaka") }
    var gender by remember { mutableStateOf(participant?.gender ?: "Male") }
    var age by remember { mutableStateOf(participant?.age?.toString() ?: "17") }
    var selectedGroupId by remember { mutableStateOf(participant?.groupId ?: groups.firstOrNull()?.id ?: "bukhari") }
    var subgroup by remember { mutableStateOf(participant?.subgroup ?: "দল-১") }
    var roomNo by remember { mutableStateOf(participant?.roomNo ?: "কক্ষ-১০১") }
    var bedNo by remember { mutableStateOf(participant?.bedNo ?: "বেড-০১") }
    var mealPref by remember { mutableStateOf(participant?.mealPref ?: "নিয়মিত") }
    var guardianName by remember { mutableStateOf(participant?.guardianName ?: "") }
    var guardianPhone by remember { mutableStateOf(participant?.guardianPhone ?: "") }
    var medicalFlag by remember { mutableStateOf(participant?.medicalFlag ?: "") }

    var groupExpanded by remember { mutableStateOf(false) }
    val selectedGroup = groups.find { it.id == selectedGroupId } ?: groups.firstOrNull()

    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("add_edit_participant_dialog")
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
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = LscEmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEditing) {
                                if (language == CampLanguage.BANGLA) "অংশগ্রহণকারী সম্পাদনা" else "Edit Participant"
                            } else {
                                if (language == CampLanguage.BANGLA) "নতুন অংশগ্রহণকারী যোগ করুন" else "Add New Participant"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = if (language == CampLanguage.BANGLA)
                        "রুম ডাটাবেজে রেকর্ড সরাসরি সংরক্ষণ বা আপডেট হবে।"
                    else
                        "Records are stored locally in Room database.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Name Bangla
                OutlinedTextField(
                    value = nameBn,
                    onValueChange = { nameBn = it },
                    label = { Text(if (language == CampLanguage.BANGLA) "নাম (বাংলায়) *" else "Name (Bangla) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_participant_name_bn")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Name English
                OutlinedTextField(
                    value = nameEn,
                    onValueChange = { nameEn = it },
                    label = { Text(if (language == CampLanguage.BANGLA) "নাম (ইংরেজিতে)" else "Name (English)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_participant_name_en")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Code and File No
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = campCode,
                        onValueChange = { campCode = it },
                        label = { Text("ক্যাম্প আইডি (Code) *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_participant_code")
                    )
                    OutlinedTextField(
                        value = fileNo,
                        onValueChange = { fileNo = it },
                        label = { Text("ফাইল নম্বর") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_participant_file")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Group selector
                ExposedDropdownMenuBox(
                    expanded = groupExpanded,
                    onExpandedChange = { groupExpanded = !groupExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedGroup?.let { "${it.nameBn} (${it.nameEn})" } ?: selectedGroupId,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (language == CampLanguage.BANGLA) "গ্রুপ / কাফেলা *" else "Camp Group *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("input_participant_group")
                    )
                    ExposedDropdownMenu(
                        expanded = groupExpanded,
                        onDismissRequest = { groupExpanded = false }
                    ) {
                        groups.forEach { grp ->
                            DropdownMenuItem(
                                text = { Text("${grp.nameBn} (${grp.nameEn})") },
                                onClick = {
                                    selectedGroupId = grp.id
                                    groupExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Age & Gender
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
                        label = { Text(if (language == CampLanguage.BANGLA) "বয়স (Age) *" else "Age *") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("input_participant_age")
                    )
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { gender = it },
                        label = { Text(if (language == CampLanguage.BANGLA) "লিঙ্গ (Gender)" else "Gender") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_participant_gender")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Meal Preference
                OutlinedTextField(
                    value = mealPref,
                    onValueChange = { mealPref = it },
                    label = { Text(if (language == CampLanguage.BANGLA) "খাবার পছন্দ (Meal Preference)" else "Meal Preference") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_participant_mealPref")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subgroup & District
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = subgroup,
                        onValueChange = { subgroup = it },
                        label = { Text("সাব-গ্রুপ") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_participant_subgroup")
                    )
                    OutlinedTextField(
                        value = districtBn,
                        onValueChange = { districtBn = it },
                        label = { Text("জেলা") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_participant_district")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Room & Bed
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = roomNo,
                        onValueChange = { roomNo = it },
                        label = { Text("কক্ষ নম্বর") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_participant_room")
                    )
                    OutlinedTextField(
                        value = bedNo,
                        onValueChange = { bedNo = it },
                        label = { Text("বেড নম্বর") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_participant_bed")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Guardian info
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = guardianName,
                        onValueChange = { guardianName = it },
                        label = { Text("অভিভাবক") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_participant_guardian")
                    )
                    OutlinedTextField(
                        value = guardianPhone,
                        onValueChange = { guardianPhone = it },
                        label = { Text("অভিভাবকের ফোন") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_participant_phone")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Medical / Allergy Note
                OutlinedTextField(
                    value = medicalFlag,
                    onValueChange = { medicalFlag = it },
                    label = { Text(if (language == CampLanguage.BANGLA) "স্বাস্থ্য বা অ্যালার্জি নোট (ঐচ্ছিক)" else "Medical Note (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_participant_medical")
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isEditing && onDelete != null) {
                        OutlinedButton(
                            onClick = { showDeleteConfirm = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                            modifier = Modifier.weight(1f).testTag("delete_participant_btn")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (language == CampLanguage.BANGLA) "মুছুন" else "Delete")
                        }
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (language == CampLanguage.BANGLA) "বাতিল" else "Cancel")
                    }

                    Button(
                        onClick = {
                            if (nameBn.isNotBlank() && campCode.isNotBlank()) {
                                val savedParticipant = (participant ?: ParticipantEntity(
                                    campCode = campCode.trim(),
                                    fileNo = fileNo.trim(),
                                    nameBn = nameBn.trim(),
                                    nameEn = nameEn.ifBlank { nameBn }.trim(),
                                    districtBn = districtBn.trim(),
                                    districtEn = districtEn.ifBlank { districtBn }.trim(),
                                    gender = gender,
                                    age = age.toIntOrNull() ?: 17,
                                    groupId = selectedGroup?.id ?: selectedGroupId,
                                    groupNameBn = selectedGroup?.nameBn ?: "কাফেলা",
                                    groupNameEn = selectedGroup?.nameEn ?: "Group",
                                    subgroup = subgroup.trim(),
                                    roomNo = roomNo.trim(),
                                    bedNo = bedNo.trim(),
                                    mealPref = mealPref.trim(),
                                    guardianName = guardianName.trim(),
                                    guardianPhone = guardianPhone.trim(),
                                    medicalFlag = medicalFlag.ifBlank { null }?.trim()
                                )).copy(
                                    nameBn = nameBn.trim(),
                                    nameEn = nameEn.ifBlank { nameBn }.trim(),
                                    campCode = campCode.trim(),
                                    fileNo = fileNo.trim(),
                                    districtBn = districtBn.trim(),
                                    districtEn = districtEn.ifBlank { districtBn }.trim(),
                                    age = age.toIntOrNull() ?: (participant?.age ?: 17),
                                    gender = gender.ifBlank { participant?.gender ?: "Male" },
                                    groupId = selectedGroup?.id ?: selectedGroupId,
                                    groupNameBn = selectedGroup?.nameBn ?: (participant?.groupNameBn ?: "কাফেলা"),
                                    groupNameEn = selectedGroup?.nameEn ?: (participant?.groupNameEn ?: "Group"),
                                    subgroup = subgroup.trim(),
                                    roomNo = roomNo.trim(),
                                    bedNo = bedNo.trim(),
                                    mealPref = mealPref.trim(),
                                    guardianName = guardianName.trim(),
                                    guardianPhone = guardianPhone.trim(),
                                    medicalFlag = medicalFlag.ifBlank { null }?.trim()
                                )
                                onSave(savedParticipant)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        modifier = Modifier.weight(1.3f).testTag("save_participant_btn")
                    ) {
                        Text(
                            text = if (isEditing) {
                                if (language == CampLanguage.BANGLA) "আপডেট করুন" else "Update"
                            } else {
                                if (language == CampLanguage.BANGLA) "সংরক্ষণ করুন" else "Save"
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteConfirm && participant != null && onDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(if (language == CampLanguage.BANGLA) "অংশগ্রহণকারী মুছবেন?" else "Delete Participant?") },
            text = {
                Text(
                    if (language == CampLanguage.BANGLA)
                        "আপনি কি নিশ্চিত যে '${participant.nameBn}' এর রেকর্ডটি রুম ডাটাবেজ থেকে মুছে ফেলতে চান?"
                    else
                        "Are you sure you want to delete '${participant.nameBn}' from Room database?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete(participant)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text(if (language == CampLanguage.BANGLA) "হ্যাঁ, মুছুন" else "Yes, Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) {
                    Text(if (language == CampLanguage.BANGLA) "বাতিল" else "Cancel")
                }
            }
        )
    }
}

