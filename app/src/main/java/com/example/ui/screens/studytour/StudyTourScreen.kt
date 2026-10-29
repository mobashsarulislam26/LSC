package com.example.ui.screens.studytour

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampLanguage
import com.example.data.model.TravelVehicleEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyTourScreen(
    language: CampLanguage,
    vehicles: List<TravelVehicleEntity>,
    onUpdateVehicle: (TravelVehicleEntity) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isBangla = language == CampLanguage.BANGLA
    val context = LocalContext.current
    var activeTourDay by remember { mutableIntStateOf(3) } // 3 = Dhaka & Metro Rail, 4 = Nandan Park

    val tourChecklist = remember {
        mutableStateMapOf(
            0 to true,  // সকল বাসের যান্ত্রিক ফিটনেস ও জ্বালানি পরীক্ষা
            1 to true,  // প্রতিটি বাসে সাব-গ্রুপ ইন-চার্জ, ফার্স্ট এইড কিট ও পানীয় জল
            2 to true,  // মেট্রোরেল গ্রুপ পাস ও র‍্যাপিড এন্ট্রি লাইন ব্রিফিং
            3 to false, // বোর্ডিং হেডকাউন্ট ১০০% যাচাই (Zero-Lost-Child Protocol)
            4 to false, // জ্ঞান অন্বেষণ কুইজ শিট ও ২৪টি পুরস্কার বিতরণ
            5 to false  // সফর শেষে রিসোর্টে ১০০% নিরাপদ প্রত্যাবর্তন হেডকাউন্ট
        )
    }

    // Track 4-stage checkpoint verification per bus: "${vehicleId}_${stageIdx}" -> Boolean
    val busCheckpointState = remember { mutableStateMapOf<String, Boolean>() }

    val totalDeparture = vehicles.sumOf { it.departureHeadcount }
    val totalReturn = vehicles.sumOf { it.returnHeadcount }
    val totalCapacity = vehicles.sumOf { it.capacity }

    val dhakaSpots = listOf(
        "১. জাতীয় চিড়িয়াখানা (মিরপুর)",
        "২. জাতীয় সংসদ ভবন প্রাঙ্গণ",
        "৩. ঢাকা বিশ্ববিদ্যালয় ও জাতীয় কবির সমাধি",
        "৪. বায়তুল মোকাররম জাতীয় মসজিদ",
        "৫. সামরিক জাদুঘর (বিজয় সরণি)",
        "৬. মেট্রোরেল ভ্রমণ (আগারগাঁও–উত্তরা) 🚝"
    )

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
                        IconButton(onClick = onBack, modifier = Modifier.testTag("tour_back_btn")) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFFFEF3C7), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint = Color(0xFFD97706)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBangla) "শিক্ষা সফর, মেট্রোরেল ও নন্দন পার্ক বহর" else "Study Tour, Metro Rail & Nandan Park",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                        Text(
                            text = if (isBangla)
                                "৩য় দিন: ঢাকা ও মেট্রোরেল ভ্রমণ • ৪র্থ দিন: নন্দন পার্ক (ইন-চার্জ: মাহাদী হাসান)"
                            else
                                "Day 3: Dhaka & Metro Rail • Day 4: Nandan Park Fleet",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "${vehicles.size} ${if (isBangla) "টি বাস" else "Buses"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Day 3 vs Day 4 Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = activeTourDay == 3,
                        onClick = { activeTourDay = 3 },
                        label = {
                            Text(
                                text = if (isBangla) "🚝 ৩য় দিন: ঢাকা সফর ও মেট্রোরেল" else "🚝 Day 3: Dhaka & Metro Rail",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LscEmeraldPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1.15f)
                    )
                    FilterChip(
                        selected = activeTourDay == 4,
                        onClick = { activeTourDay = 4 },
                        label = {
                            Text(
                                text = if (isBangla) "🎡 ৪র্থ দিন: নন্দন পার্ক ভ্রমণ" else "🎡 Day 4: Nandan Park",
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

                Spacer(modifier = Modifier.height(10.dp))

                // Aggregate Metrics Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = if (isBangla) "যাত্রা হেডকাউন্ট" else "Departure", fontSize = 10.sp, color = Color(0xFF166534))
                            Text(text = "$totalDeparture / $totalCapacity", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = if (isBangla) "প্রত্যাবর্তন হেডকাউন্ট" else "Return", fontSize = 10.sp, color = Color(0xFF1E40AF))
                            Text(text = "$totalReturn / $totalCapacity", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = if (isBangla) "গন্তব্য" else "Destination", fontSize = 10.sp, color = Color(0xFF92400E))
                            Text(
                                text = if (activeTourDay == 3) "ঢাকা ও মেট্রোরেল" else "নন্দন পার্ক",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Official Route & Quiz Highlight Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (activeTourDay == 3) Color(0xFFF0FDF4) else Color(0xFFFFFBEB)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (activeTourDay == 3) Color(0xFF86EFAC) else Color(0xFFFDE68A)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        if (activeTourDay == 3) {
                            Text(
                                text = "🗺 ৩য় দিনের অফিশিয়াল সফর রুট ও জ্ঞান অন্বেষণ কুইজ (২৪টি পুরস্কার)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF065F46)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            dhakaSpots.forEach { spot ->
                                Text(
                                    text = "✓ $spot",
                                    fontSize = 11.sp,
                                    color = Color(0xFF047857),
                                    modifier = Modifier.padding(vertical = 1.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                            ) {
                                Text(
                                    text = "🏆 সফর চলাকালীন 'জ্ঞান অন্বেষণ কুইজ' অনুষ্ঠিত হবে এবং ২৪টি সাব-গ্রুপে ২৪টি পুরস্কার প্রদান করা হবে।",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF15803D),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        } else {
                            Text(
                                text = "🎡 ৪র্থ দিনের নন্দন পার্ক আনন্দ ভ্রমণ নির্দেশিকা",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF92400E)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• সকালের নাস্তার পর সাব-গ্রুপ সারিবদ্ধভাবে নন্দন পার্কে প্রবেশ।\n• রাইড জোনে মেন্টরের তত্ত্বাবধানে নিরাপত্তা নিশ্চিতকরণ।\n• যোহর ও মধ্যাহ্নভোজের পূর্বে রিসোর্টে প্রত্যাবর্তন ও বৈকালিক স্টেশন সেশনে অংশগ্রহণ।",
                                fontSize = 11.sp,
                                color = Color(0xFFB45309),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Safety Checklist Section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = LscEmeraldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBangla) "জিরো-লস্ট-চাইল্ড ও সফর নিরাপত্তা চেকলিস্ট" else "Zero-Lost-Child Safety Checklist",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = LscSlateDark
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        val checklistItems = listOf(
                            "সকল বাসের যান্ত্রিক ফিটনেস, ব্রেক, টায়ার ও জ্বালানি পরীক্ষা সম্পন্ন",
                            "প্রতিটি বাসে সাব-গ্রুপ মেন্টর, ফার্স্ট এইড কিট ও নিরাপদ খাবার পানি নিশ্চিত",
                            "মেট্রোরেল স্টেশন (আগারগাঁও–উত্তরা) গ্রুপ পাস ও সারিবদ্ধ বোর্ডিং ব্রিফিং",
                            "যাত্রা শুরুর প্রাক্কালে ১০০% নির্ভুল বোর্ডিং হেডকাউন্ট যাচাই",
                            "জ্ঞান অন্বেষণ কুইজ প্রশ্নপত্র ও ২৪টি তাৎক্ষণিক পুরস্কার সংরক্ষণ",
                            "সফর শেষে ফিরতি বাসে শতভাগ উপস্থিতি মিলিয়ে রিসোর্টে প্রত্যাবর্তন"
                        )

                        checklistItems.forEachIndexed { idx, item ->
                            val isChecked = tourChecklist[idx] ?: false
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { tourChecklist[idx] = !isChecked }
                                    .padding(vertical = 2.dp)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { tourChecklist[idx] = !isChecked },
                                    colors = CheckboxDefaults.colors(checkedColor = LscEmeraldPrimary),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item,
                                    fontSize = 11.sp,
                                    color = if (isChecked) Color.Gray else Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = if (isBangla) "সংরক্ষিত বাস বহর ও ৪-ধাপ চেকপয়েন্ট হেডকাউন্ট:" else "Assigned Buses & 4-Stage Checkpoint Control:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = LscSlateDark,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            items(vehicles, key = { it.id }) { vehicle ->
                BusCard(
                    vehicle = vehicle,
                    isBangla = isBangla,
                    checkpointState = busCheckpointState,
                    onToggleCheckpoint = { key ->
                        busCheckpointState[key] = !(busCheckpointState[key] ?: false)
                    },
                    onUpdateVehicle = onUpdateVehicle,
                    onCall = { phone ->
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
private fun BusCard(
    vehicle: TravelVehicleEntity,
    isBangla: Boolean,
    checkpointState: Map<String, Boolean>,
    onToggleCheckpoint: (String) -> Unit,
    onUpdateVehicle: (TravelVehicleEntity) -> Unit,
    onCall: (String) -> Unit
) {
    val checkpoints = listOf(
        "বাসে ওঠা",
        "স্পট চেক",
        "মেট্রোরেল এন্ট্রি",
        "রিসোর্টে প্রত্যাবর্তন"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bus_card_${vehicle.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFFFEF3C7), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vehicle.vehicleNo,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = LscSlateDark
                    )
                    Text(
                        text = "নির্ধারিত দল: ${vehicle.assignedGroupId.uppercase()}",
                        fontSize = 11.sp,
                        color = LscEmeraldPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    color = when (vehicle.status) {
                        "BOARDING" -> Color(0xFFFEF3C7)
                        "IN_TRANSIT" -> Color(0xFFEFF6FF)
                        "RETURNED" -> Color(0xFFDCFCE7)
                        else -> Color(0xFFF1F5F9)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable {
                        val nextStatus = when (vehicle.status) {
                            "SCHEDULED" -> "BOARDING"
                            "BOARDING" -> "IN_TRANSIT"
                            "IN_TRANSIT" -> "RETURNED"
                            else -> "SCHEDULED"
                        }
                        onUpdateVehicle(vehicle.copy(status = nextStatus))
                    }
                ) {
                    Text(
                        text = when (vehicle.status) {
                            "BOARDING" -> "বোর্ডিং চলছে"
                            "IN_TRANSIT" -> "চলমান বহর"
                            "RETURNED" -> "প্রত্যাবর্তিত"
                            else -> "শিডিউলড"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (vehicle.status) {
                            "BOARDING" -> Color(0xFFB45309)
                            "IN_TRANSIT" -> Color(0xFF1D4ED8)
                            "RETURNED" -> Color(0xFF15803D)
                            else -> Color(0xFF475569)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4-Stage Zero-Lost-Child Checkpoint Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(checkpoints.indices.toList()) { idx ->
                    val cpKey = "${vehicle.id}_$idx"
                    val done = checkpointState[cpKey] ?: false
                    Surface(
                        color = if (done) Color(0xFFDCFCE7) else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(
                            1.dp,
                            if (done) Color(0xFF86EFAC) else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.clickable {
                            onToggleCheckpoint(cpKey)
                            if (idx == 0 && !done) {
                                onUpdateVehicle(vehicle.copy(departureHeadcount = vehicle.capacity, status = "BOARDING"))
                            } else if (idx == 3 && !done) {
                                onUpdateVehicle(vehicle.copy(returnHeadcount = vehicle.capacity, status = "RETURNED"))
                            }
                        }
                    ) {
                        Text(
                            text = "${if (done) "✓" else "○"} ${checkpoints[idx]}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (done) Color(0xFF15803D) else LscSlateDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "চালক: ${vehicle.driverName}", fontSize = 11.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = { onCall(vehicle.driverPhone) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call Driver", tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "ইন-চার্জ: ${vehicle.inChargeName}", fontSize = 11.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = { onCall(vehicle.inChargePhone) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call Officer", tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Text(
                        text = "আসন: ${vehicle.capacity}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            // Headcount Adjusters + 1-Tap Full Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = if (isBangla) "যাত্রা হেডকাউন্ট:" else "Departure:", fontSize = 10.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (vehicle.departureHeadcount > 0) {
                                    onUpdateVehicle(vehicle.copy(departureHeadcount = vehicle.departureHeadcount - 1))
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Minus", modifier = Modifier.size(18.dp), tint = Color.Gray)
                        }
                        Text(
                            text = "${vehicle.departureHeadcount}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF15803D),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        IconButton(
                            onClick = {
                                if (vehicle.departureHeadcount < vehicle.capacity) {
                                    onUpdateVehicle(vehicle.copy(departureHeadcount = vehicle.departureHeadcount + 1))
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = "Add", modifier = Modifier.size(18.dp), tint = Color(0xFF15803D))
                        }
                        TextButton(
                            onClick = { onUpdateVehicle(vehicle.copy(departureHeadcount = vehicle.capacity, status = "BOARDING")) },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text("১০০%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                        }
                    }
                }

                Column {
                    Text(text = if (isBangla) "প্রত্যাবর্তন হেডকাউন্ট:" else "Return:", fontSize = 10.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (vehicle.returnHeadcount > 0) {
                                    onUpdateVehicle(vehicle.copy(returnHeadcount = vehicle.returnHeadcount - 1))
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Minus", modifier = Modifier.size(18.dp), tint = Color.Gray)
                        }
                        Text(
                            text = "${vehicle.returnHeadcount}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF1D4ED8),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        IconButton(
                            onClick = {
                                if (vehicle.returnHeadcount < vehicle.capacity) {
                                    onUpdateVehicle(vehicle.copy(returnHeadcount = vehicle.returnHeadcount + 1))
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = "Add", modifier = Modifier.size(18.dp), tint = Color(0xFF1D4ED8))
                        }
                        TextButton(
                            onClick = { onUpdateVehicle(vehicle.copy(returnHeadcount = vehicle.capacity, status = "RETURNED")) },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text("১০০%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                        }
                    }
                }
            }
        }
    }
}
