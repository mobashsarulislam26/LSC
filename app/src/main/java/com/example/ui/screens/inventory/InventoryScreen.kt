package com.example.ui.screens.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun InventoryScreen(
    language: CampLanguage,
    inventoryItems: List<InventoryItemEntity>,
    onIssueItem: (itemId: Long, qty: Int, recipient: String, officer: String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    var showIssueDialog by remember { mutableStateOf<InventoryItemEntity?>(null) }

    val totalOpening = inventoryItems.sumOf { it.openingQty }
    val totalIssued = inventoryItems.sumOf { it.issuedQty }
    val totalBalance = inventoryItems.sumOf { it.balanceQty }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Inventory Stock Summary Card (PRD: 799 units across 28 items)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth().testTag("inventory_summary_card")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "উপহার ও পুরস্কার ইনভেন্টরি লেজার" else "Awards & Gifts Inventory Ledger",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = LscSlateDark
                        )
                        Text(
                            text = if (language == CampLanguage.BANGLA) "মোট ২৮ ক্যাটাগরির ক্যাম্প পণ্য ও মেডেল" else "28 Distinct Master Items Configured",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Surface(
                        color = LscGoldLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "৭৯৯ ইউনিট স্টক",
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
                        label = if (language == CampLanguage.BANGLA) "প্রারম্ভিক স্টক" else "Opening",
                        qty = totalOpening,
                        color = Color(0xFF1D4ED8),
                        modifier = Modifier.weight(1f)
                    )
                    InventoryStatPill(
                        label = if (language == CampLanguage.BANGLA) "বিতরণকৃত" else "Issued",
                        qty = totalIssued,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                    InventoryStatPill(
                        label = if (language == CampLanguage.BANGLA) "বর্তমান ব্যালেন্স" else "Balance",
                        qty = totalBalance,
                        color = LscGoldSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val categories = listOf("ALL", "AWARD", "GIFT", "STATIONERY", "KIT", "HEALTH")
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
            inventoryItems.filter { it.category == selectedCategory }
        }

        // Inventory Items List
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
    }

    if (showIssueDialog != null) {
        IssueItemDialog(
            item = showIssueDialog!!,
            language = language,
            onDismiss = { showIssueDialog = null },
            onConfirm = { qty, rec, off ->
                onIssueItem(showIssueDialog!!.id, qty, rec, off)
                showIssueDialog = null
            }
        )
    }
}

@Composable
fun InventoryStatPill(label: String, qty: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f)),
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
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
    var officer by remember { mutableStateOf("ইনভেন্টরি অফিসার") }
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
                    label = { Text("গ্রহীতার নাম / ক্যাম্প আইডি / স্টেশন") },
                    placeholder = { Text("যেমন: ১ম স্থান LSC-2026-001") },
                    isError = errorText != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorText!!, fontSize = 11.sp, color = Color(0xFFDC2626))
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
