package com.example.ui.screens.departments

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampLanguage
import com.example.data.model.CampRationaleData
import com.example.data.model.DepartmentSop
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentsSopScreen(
    language: CampLanguage,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isBangla = language == CampLanguage.BANGLA
    val context = LocalContext.current
    val allDepartments = remember { CampRationaleData.departments }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var expandedDeptId by remember { mutableStateOf<Int?>(1) } // Default expand first department

    // Keep track of checked checklist items: "deptId_index" -> Boolean
    val checkedItems = remember { mutableStateMapOf<String, Boolean>() }

    val filteredDepartments = remember(searchQuery, selectedCategory) {
        allDepartments.filter { dept ->
            val matchesCategory = selectedCategory == null || dept.key == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    dept.nameBn.contains(searchQuery, ignoreCase = true) ||
                    dept.nameEn.contains(searchQuery, ignoreCase = true) ||
                    dept.inChargeName.contains(searchQuery, ignoreCase = true) ||
                    dept.responsibilitiesBn.any { it.contains(searchQuery, ignoreCase = true) } ||
                    dept.sopStepsBn.any { it.contains(searchQuery, ignoreCase = true) }
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (onBack != null) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("dept_sop_back_btn")) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(LscEmeraldLight, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CorporateFare,
                            contentDescription = null,
                            tint = LscEmeraldPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBangla) "১২টি বিভাগ ও এসওপি নির্দেশিকা" else "12 Departments & SOPs",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                        Text(
                            text = if (isBangla) "ক্যাম্প কার্যপদ্ধতি, দায়িত্ব বণ্টন ও চেকলিস্ট (পৃ. ২৮-৩৩)" else "Standard Operating Procedures & Job Descriptions",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Surface(
                        color = LscEmeraldPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "${allDepartments.size} ${if (isBangla) "বিভাগ" else "Depts"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LscEmeraldPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (isBangla) "বিভাগ, কর্মকর্তা বা দায়িত্ব লিখে খুঁজুন..." else "Search departments, staff, duties...",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFFF1F5F9),
                        focusedBorderColor = LscEmeraldPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dept_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text(if (isBangla) "সকল বিভাগ" else "All Departments", fontSize = 11.sp) }
                        )
                    }
                    items(allDepartments) { dept ->
                        FilterChip(
                            selected = selectedCategory == dept.key,
                            onClick = {
                                selectedCategory = if (selectedCategory == dept.key) null else dept.key
                            },
                            label = {
                                Text(
                                    text = if (isBangla) dept.nameBn.substringBefore(" বিভাগ") else dept.nameEn.substringBefore(" "),
                                    fontSize = 11.sp
                                )
                            }
                        )
                    }
                }
            }
        }

        // Department List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredDepartments, key = { it.id }) { dept ->
                val isExpanded = expandedDeptId == dept.id

                DepartmentCard(
                    dept = dept,
                    isBangla = isBangla,
                    isExpanded = isExpanded,
                    checkedItems = checkedItems,
                    onToggleExpand = {
                        expandedDeptId = if (isExpanded) null else dept.id
                    },
                    onToggleCheck = { key ->
                        checkedItems[key] = !(checkedItems[key] ?: false)
                    },
                    onCallInCharge = { phone ->
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    }
                )
            }
        }
    }
}

@Composable
private fun DepartmentCard(
    dept: DepartmentSop,
    isBangla: Boolean,
    isExpanded: Boolean,
    checkedItems: Map<String, Boolean>,
    onToggleExpand: () -> Unit,
    onToggleCheck: (String) -> Unit,
    onCallInCharge: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 4.dp else 1.dp),
        border = BorderStroke(
            width = if (isExpanded) 1.5.dp else 1.dp,
            color = if (isExpanded) LscEmeraldPrimary else Color(0xFFE2E8F0)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() }
            .testTag("dept_card_${dept.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isExpanded) LscEmeraldPrimary else LscEmeraldLight,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${dept.id}",
                        fontWeight = FontWeight.Bold,
                        color = if (isExpanded) Color.White else LscEmeraldPrimary,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBangla) dept.nameBn else dept.nameEn,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = LscSlateDark
                    )
                    Text(
                        text = "ইন-চার্জ: ${dept.inChargeName}",
                        fontSize = 12.sp,
                        color = LscEmeraldPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = { onCallInCharge(dept.inChargePhone) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFDCFCE7), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call",
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Info Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = dept.location, fontSize = 10.sp, color = Color.DarkGray)
                    }
                }

                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFD97706))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = dept.shiftTime, fontSize = 10.sp, color = Color(0xFF92400E))
                    }
                }
            }

            // Expanded Details Section
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Divider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Objective
                    Text(
                        text = if (isBangla) "মূল লক্ষ্য ও উদ্দেশ্য:" else "Core Objective:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = LscSlateDark
                    )
                    Text(
                        text = dept.coreObjectiveBn,
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Responsibilities
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Assignment, contentDescription = null, tint = LscEmeraldPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBangla) "সুনির্দিষ্ট দায়িত্ব ও কার্যপরিধি:" else "Job Responsibilities:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = LscEmeraldPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            dept.responsibilitiesBn.forEach { resp ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("• ", color = LscEmeraldPrimary, fontWeight = FontWeight.Bold)
                                    Text(text = resp, fontSize = 11.sp, color = Color.DarkGray, lineHeight = 16.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // SOP Steps
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountTree, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBangla) "স্ট্যান্ডার্ড অপারেটিং প্রসিডিউর (SOP):" else "Standard Operating Procedures (SOP):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF15803D)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            dept.sopStepsBn.forEach { step ->
                                Text(
                                    text = step,
                                    fontSize = 11.sp,
                                    color = Color(0xFF166534),
                                    lineHeight = 17.sp,
                                    modifier = Modifier.padding(vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Checklist
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Checklist, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBangla) "বিভাগীয় অ্যাকশন চেকলিস্ট:" else "Department Action Checklist:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF92400E)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            dept.checklistBn.forEachIndexed { idx, item ->
                                val key = "${dept.id}_$idx"
                                val isChecked = checkedItems[key] ?: false

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onToggleCheck(key) }
                                        .padding(vertical = 2.dp)
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { onToggleCheck(key) },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF16A34A)),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = item,
                                        fontSize = 11.sp,
                                        color = if (isChecked) Color.Gray else Color(0xFF78350F)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
