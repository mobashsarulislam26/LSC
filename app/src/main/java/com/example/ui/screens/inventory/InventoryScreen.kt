package com.example.ui.screens.inventory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun InventoryScreen(
    language: CampLanguage,
    inventoryItems: List<InventoryItemEntity>,
    participants: List<ParticipantEntity> = emptyList(),
    kitDistributions: List<KitDistributionEntity> = emptyList(),
    onUpdateKitDistribution: (KitDistributionEntity) -> Unit = {},
    onIssueItem: (itemId: Long, qty: Int, recipient: String, officer: String) -> Unit
) {
    val isBangla = language == CampLanguage.BANGLA
    var activeSubTab by remember { mutableStateOf("STOCK") } // "STOCK" or "KIT_CHADOR"
    var selectedCategory by remember { mutableStateOf("ALL") }
    var showIssueDialog by remember { mutableStateOf<InventoryItemEntity?>(null) }

    val totalOpening = inventoryItems.sumOf { it.openingQty }
    val totalIssued = inventoryItems.sumOf { it.issuedQty }
    val totalBalance = inventoryItems.sumOf { it.balanceQty }

    val kitByParticipant = remember(kitDistributions) {
        kitDistributions.associateBy { it.participantId }
    }
    val kitReceivedCount = remember(kitDistributions) {
        kitDistributions.count { it.kitBagStatus != "ALLOCATED" || it.giftPackStatus != "ALLOCATED" }
    }
    val chadorReceivedCount = remember(kitDistributions) {
        kitDistributions.count { it.motherChadorStatus != "ALLOCATED" }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Sub-Tab Switcher: Stock Ledger vs Student Kit & Mother's Chador Distribution
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeSubTab == "STOCK",
                onClick = { activeSubTab = "STOCK" },
                label = {
                    Text(
                        text = if (isBangla) "📦 ৩০ উপহার ও ৮৩৫ পুরস্কার স্টক" else "📦 Stock & 835 Awards",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LscEmeraldPrimary,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = activeSubTab == "KIT_CHADOR",
                onClick = { activeSubTab = "KIT_CHADOR" },
                label = {
                    Text(
                        text = if (isBangla) "🧣 কিট ও মায়েদের চাদর (${chadorReceivedCount})" else "🧣 Kit & Mother's Chador",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFD97706),
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (activeSubTab == "STOCK") {
            // Inventory Stock Summary Card (Official 2026: 30 Student Gifts + 835 Competition Prizes)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth().testTag("inventory_summary_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBangla) "উপহার (৩০টি) ও পুরস্কার (৮৩৫টি) ইনভেন্টরি লেজার" else "30 Gifts & 835 Awards Inventory Ledger",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = LscSlateDark
                            )
                            Text(
                                text = if (isBangla)
                                    "মোট ${inventoryItems.size} ক্যাটাগরির অফিশিয়াল উপহার, পোশাক, বই ও পুরস্কার"
                                else
                                    "${inventoryItems.size} Official Gift & Award Categories",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        Surface(
                            color = LscGoldLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$totalOpening ইউনিট স্টক",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = LscGoldDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InventoryStatPill(
                            label = if (isBangla) "প্রারম্ভিক স্টক" else "Opening",
                            qty = totalOpening,
                            color = Color(0xFF1D4ED8),
                            modifier = Modifier.weight(1f)
                        )
                        InventoryStatPill(
                            label = if (isBangla) "বিতরণকৃত" else "Issued",
                            qty = totalIssued,
                            color = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )
                        InventoryStatPill(
                            label = if (isBangla) "বর্তমান ব্যালেন্স" else "Balance",
                            qty = totalBalance,
                            color = LscGoldSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val categories = remember(inventoryItems) {
                listOf("ALL") + inventoryItems.map { it.category }.distinct()
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LscEmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val filteredItems = if (selectedCategory == "ALL") {
                inventoryItems
            } else {
                inventoryItems.filter { it.category.equals(selectedCategory, ignoreCase = true) }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredItems, key = { it.id }) { item ->
                    InventoryItemRow(
                        item = item,
                        language = language,
                        onIssue = { showIssueDialog = item }
                    )
                }
            }
        } else {
            StudentKitAndChadorSection(
                isBangla = isBangla,
                participants = participants,
                kitByParticipant = kitByParticipant,
                kitReceivedCount = kitReceivedCount,
                chadorReceivedCount = chadorReceivedCount,
                onUpdateKitDistribution = onUpdateKitDistribution
            )
        }
    }

    showIssueDialog?.let { targetItem ->
        IssueItemDialog(
            item = targetItem,
            language = language,
            onDismiss = { showIssueDialog = null },
            onConfirm = { qty, rec, off ->
                onIssueItem(targetItem.id, qty, rec, off)
                showIssueDialog = null
            }
        )
    }
}

@Composable
private fun StudentKitAndChadorSection(
    isBangla: Boolean,
    participants: List<ParticipantEntity>,
    kitByParticipant: Map<Long, KitDistributionEntity>,
    kitReceivedCount: Int,
    chadorReceivedCount: Int,
    onUpdateKitDistribution: (KitDistributionEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubgroup by remember { mutableStateOf("ALL") }

    val subgroups = remember(participants) {
        participants.map { it.subgroup }.filter { it.isNotBlank() }.distinct()
    }

    val filtered = remember(participants, searchQuery, selectedSubgroup) {
        val q = searchQuery.trim()
        participants.filter { p ->
            val matchSub = selectedSubgroup == "ALL" || p.subgroup.equals(selectedSubgroup, ignoreCase = true)
            val matchQ = q.isBlank() ||
                    p.nameBn.contains(q, ignoreCase = true) ||
                    p.nameEn.contains(q, ignoreCase = true) ||
                    p.campCode.contains(q, ignoreCase = true) ||
                    p.fileNo.contains(q, ignoreCase = true)
            matchSub && matchQ
        }.take(100)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
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
                    Text(
                        text = if (isBangla) "🎁 ৩০টি শিক্ষার্থী উপহার কিট ও মায়েদের চাদর" else "🎁 30-Item Kit & Mother's Chador",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF92400E)
                    )
                    Text(
                        text = if (isBangla)
                            "ছাত্র: পোলো ও ট্রাউজার | ছাত্রী: স্কার্ট ও খিমার | সকল মায়ের জন্য সম্মাননা চাদর"
                        else
                            "Boys: Polo & Trouser | Girls: Skirt & Khimar | Mother's Chador",
                        fontSize = 10.sp,
                        color = Color(0xFFB45309)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "কিট: $kitReceivedCount/${participants.size}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LscEmeraldPrimary
                    )
                    Text(
                        text = "চাদর: $chadorReceivedCount/${participants.size}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedSubgroup == "ALL",
                    onClick = { selectedSubgroup = "ALL" },
                    label = { Text(if (isBangla) "সকল সাব-গ্রুপ" else "All", fontSize = 10.sp) }
                )
            }
            items(subgroups) { sg ->
                FilterChip(
                    selected = selectedSubgroup == sg,
                    onClick = { selectedSubgroup = sg },
                    label = { Text(sg, fontSize = 10.sp) }
                )
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (isBangla) "শিক্ষার্থীর নাম, ক্যাম্প কোড বা ফাইল নং..." else "Search name or camp code...", fontSize = 11.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered, key = { it.id }) { p ->
                val isGirl = p.gender.equals("Female", ignoreCase = true)
                val existing = kitByParticipant[p.id] ?: KitDistributionEntity(
                    participantId = p.id,
                    campCode = p.campCode,
                    participantName = p.nameBn,
                    groupName = p.groupNameBn,
                    tShirtSize = "M",
                    clothingSize = "M",
                    kitBagStatus = "ALLOCATED",
                    giftPackStatus = "ALLOCATED",
                    motherChadorStatus = "ALLOCATED"
                )
                val kitDelivered = existing.kitBagStatus != "ALLOCATED"
                val chadorDelivered = existing.motherChadorStatus != "ALLOCATED"

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${p.nameBn} (${p.campCode})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = LscSlateDark
                                )
                                Text(
                                    text = "${p.subgroup} • ${if (isGirl) "ছাত্রী কিট (স্কার্ট-খিমার)" else "ছাত্র কিট (পোলো-ট্রাউজার)"}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            // 1-Tap Size Cycling
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                listOf("S", "M", "L", "XL").forEach { sz ->
                                    val isSz = existing.tShirtSize == sz
                                    Surface(
                                        color = if (isSz) Color(0xFF1D4ED8) else Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.clickable {
                                            onUpdateKitDistribution(existing.copy(tShirtSize = sz, clothingSize = sz))
                                        }
                                    ) {
                                        Text(
                                            text = sz,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSz) Color.White else LscSlateDark,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = if (kitDelivered) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (kitDelivered) Color(0xFF86EFAC) else Color(0xFFCBD5E1)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val nextStatus = if (kitDelivered) "ALLOCATED" else "DISTRIBUTED"
                                        onUpdateKitDistribution(
                                            existing.copy(
                                                kitBagStatus = nextStatus,
                                                giftPackStatus = nextStatus,
                                                receivedAt = System.currentTimeMillis()
                                            )
                                        )
                                    }
                            ) {
                                Text(
                                    text = if (kitDelivered) "✓ শিক্ষার্থী কিট প্রদান সম্পন্ন" else "○ শিক্ষার্থী কিট দিন",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (kitDelivered) Color(0xFF15803D) else LscSlateDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }

                            Surface(
                                color = if (chadorDelivered) Color(0xFFFEF3C7) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (chadorDelivered) Color(0xFFF59E0B) else Color(0xFFCBD5E1)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val nextStatus = if (chadorDelivered) "ALLOCATED" else "DISTRIBUTED"
                                        onUpdateKitDistribution(
                                            existing.copy(
                                                motherChadorStatus = nextStatus,
                                                receivedAt = System.currentTimeMillis()
                                            )
                                        )
                                    }
                            ) {
                                Text(
                                    text = if (chadorDelivered) "🧣 মায়ের চাদর প্রদান ✓" else "🧣 মায়ের চাদর দিন",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (chadorDelivered) Color(0xFF92400E) else LscSlateDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryStatPill(label: String, qty: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$qty", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = color)
            Text(text = label, fontSize = 10.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun InventoryItemRow(
    item: InventoryItemEntity,
    language: CampLanguage,
    onIssue: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth().testTag("inventory_item_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.nameBn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = LscSlateDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = item.category,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${item.nameEn} • একক: ${item.unit}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "শুরু: ${item.openingQty}", fontSize = 10.sp, color = Color.Gray)
                    Text(text = "বিতরণ: ${item.issuedQty}", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.SemiBold)
                    Text(text = "মজুদ: ${item.balanceQty} ${item.unit}", fontSize = 10.sp, color = if (item.balanceQty <= item.lowStockThreshold) Color(0xFFDC2626) else LscEmeraldPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = onIssue,
                colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(34.dp).testTag("issue_award_btn_${item.id}")
            ) {
                Text(if (language == CampLanguage.BANGLA) "বিতরণ" else "Issue", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun IssueItemDialog(
    item: InventoryItemEntity,
    language: CampLanguage,
    onDismiss: () -> Unit,
    onConfirm: (qty: Int, recipient: String, officer: String) -> Unit
) {
    var qtyText by remember { mutableStateOf("1") }
    var recipient by remember { mutableStateOf("") }
    var officer by remember { mutableStateOf("সফিউল্লাহ (উপহার ও পুরস্কার বিভাগ)") }
    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = "উপহার / পুরস্কার ইস্যু ভাউচার",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = LscSlateDark
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "আইটেম: ${item.nameBn} (বর্তমান ব্যালেন্স: ${item.balanceQty})", fontSize = 12.sp, color = LscEmeraldPrimary, fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = qtyText,
                    onValueChange = {
                        if (it.all { ch -> ch.isDigit() }) {
                            qtyText = it
                            errorText = null
                        }
                    },
                    label = { Text("ইস্যু সংখ্যা (Quantity)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorText != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = recipient,
                    onValueChange = {
                        recipient = it
                        errorText = null
                    },
                    label = { Text("গ্রহীতার নাম / ক্যাম্প আইডি / সাব-গ্রুপ") },
                    placeholder = { Text("যেমন: ১ম স্থান Bukhari-1-1") },
                    isError = errorText != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorText.orEmpty(), fontSize = 11.sp, color = Color(0xFFDC2626))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("বাতিল")
                    }
                    Button(
                        onClick = {
                            val q = qtyText.toIntOrNull()
                            when {
                                q == null || q <= 0 ->
                                    errorText = "সঠিক পরিমাণ লিখুন (Enter a valid quantity)"
                                q > item.balanceQty ->
                                    errorText = "পর্যাপ্ত স্টক নেই। অবশিষ্ট: ${item.balanceQty}"
                                recipient.isBlank() ->
                                    errorText = "গ্রহীতার নাম আবশ্যক"
                                else ->
                                    onConfirm(q, recipient.trim(), officer)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("ইস্যু নিশ্চিত করুন")
                    }
                }
            }
        }
    }
}
