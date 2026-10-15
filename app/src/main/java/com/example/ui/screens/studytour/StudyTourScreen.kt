package com.example.ui.screens.studytour

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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

    // Safety rules checklist
    val tourChecklist = remember {
        mutableStateMapOf(
            0 to true,  // বাসের যান্ত্রিক ফিটনেস ও জ্বালানি পরীক্ষা
            1 to true,  // চালকদের ওভারস্পিডিং নিষিদ্ধ লিখিত ব্রিফিং
            2 to true,  // প্রতিটি বাসে ফার্স্ট এইড কিট ও পানীয় জল
            3 to false, // বোর্ডিং হেডকাউন্ট ১০০% নিশ্চিতকরণ
            4 to false  // শিক্ষা সফর শেষে প্রত্যাবর্তন হেডকাউন্ট
        )
    }

    val totalDeparture = vehicles.sumOf { it.departureHeadcount }
    val totalReturn = vehicles.sumOf { it.returnHeadcount }
    val totalCapacity = vehicles.sumOf { it.capacity }

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
                            text = if (isBangla) "শিক্ষা সফর ও পরিবহন ব্যবস্থাপনা" else "Study Tour & Travel Hub",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                        Text(
                            text = if (isBangla) "ঐতিহাসিক সোনারগাঁ শিক্ষা সফর ও বাস বহর নিরাপত্তা" else "Excursion Bus Fleet & Headcount Verification",
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

                Spacer(modifier = Modifier.height(14.dp))

                // Aggregate Metrics Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = if (isBangla) "যাত্রা হেডকাউন্ট" else "Departure Count", fontSize = 10.sp, color = Color(0xFF166534))
                            Text(text = "$totalDeparture / $totalCapacity", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = if (isBangla) "প্রত্যাবর্তন হেডকাউন্ট" else "Return Count", fontSize = 10.sp, color = Color(0xFF1E40AF))
                            Text(text = "$totalReturn / $totalCapacity", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = if (isBangla) "গন্তব্য স্থান" else "Destination", fontSize = 10.sp, color = Color(0xFF92400E))
                            Text(text = "সোনারগাঁ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
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
                                text = if (isBangla) "সফর নিরাপত্তা ও প্রস্তুতি চেকলিস্ট" else "Study Tour Safety Checklist",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = LscSlateDark
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        val checklistItems = listOf(
                            "সকল বাসের যান্ত্রিক ফিটনেস, ব্রেক, টায়ার ও জ্বালানি পরীক্ষা সম্পন্ন",
                            "চালক ও সহকারীদের ট্রাফিক আইন ও গতিসীমা (ম্যাক্স ৫০ কিমি/ঘণ্টা) লিখিত ব্রিফিং",
                            "প্রতিটি বাসে সাব-গ্রুপ মেন্টর, ফার্স্ট এইড কিট ও নিরাপদ খাবার পানি নিশ্চিত",
                            "যাত্রা শুরুর প্রাক্কালে ১০০% নির্ভুল বোর্ডিং হেডকাউন্ট যাচাই",
                            "ঐতিহাসিক স্থানে পরিদর্শন শেষে ফিরতি বাসে শতভাগ উপস্থিতি নিশ্চিতকরণ"
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

            // Buses List
            item {
                Text(
                    text = if (isBangla) "সংরক্ষিত বাস বহর ও হেডকাউন্ট নিয়ন্ত্রণ:" else "Assigned Buses & Fleet Control:",
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
    onUpdateVehicle: (TravelVehicleEntity) -> Unit,
    onCall: (String) -> Unit
) {
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
            // Bus title and Status
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

                // Status chip dropdown / toggle
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

            Spacer(modifier = Modifier.height(10.dp))

            // Driver & In-Charge Contact
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

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            // Headcount Adjusters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Departure Counter
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
                    }
                }

                // Return Counter
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
                    }
                }
            }
        }
    }
}
