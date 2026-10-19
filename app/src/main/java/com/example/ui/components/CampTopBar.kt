package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampLanguage
import com.example.data.model.UserAccountEntity
import com.example.data.model.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampTopBar(
    language: CampLanguage,
    currentRole: UserRole,
    isOffline: Boolean,
    pendingSyncCount: Int,
    activeDay: Int = 1,
    isAdminUnlocked: Boolean = false,
    onAdminClick: () -> Unit = {},
    onBackupClick: () -> Unit = {},
    onLanguageToggle: () -> Unit,
    onRoleSelected: (UserRole) -> Unit,
    onToggleOffline: () -> Unit,
    onTriggerSync: () -> Unit,
    onOpenScanner: () -> Unit,
    onOpenSyncManager: () -> Unit = onTriggerSync,
    currentUser: UserAccountEntity? = null,
    onLogout: () -> Unit = {}
) {
    var showRoleMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val activeDayBn = remember(activeDay) {
        activeDay.toString().map {
            when (it) {
                '0' -> '০'; '1' -> '১'; '2' -> '২'; '3' -> '৩'; '4' -> '৪'
                '5' -> '৫'; '6' -> '৬'; '7' -> '৭'; '8' -> '৮'; '9' -> '৯'
                else -> it
            }
        }.joinToString("")
    }

    if (showAboutDialog) {
        CampAboutDialog(
            language = language,
            onDismiss = { showAboutDialog = false }
        )
    }

    Surface(
        color = LscEmeraldPrimary,
        tonalElevation = 6.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(LscEmeraldDark, LscEmeraldPrimary)
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Tier 1: Brand Identity & Primary Quick Actions (Language, Scanner, Sync)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Title & Active Camp Day
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    CampLogoImage(
                        size = 40.dp,
                        elevation = 4.dp,
                        onClick = { showAboutDialog = true },
                        modifier = Modifier.testTag("app_logo_topbar")
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (language == CampLanguage.BANGLA) "ক্যাম্প বন্ধু" else "LSC Companion",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = LscGoldSecondary,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "দিন $activeDayBn" else "Day $activeDay",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (language == CampLanguage.BANGLA) "আবাসিক জীবন দক্ষতা ক্যাম্প ২০২৬" else "Residential Life Skills Camp 2026",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.82f),
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Primary Quick Actions (Language, Universal QR Scanner, Cloud Sync)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Language Switcher Pill
                    Surface(
                        color = Color.White.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .testTag("lang_toggle_btn")
                            .clickable { onLanguageToggle() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA) "বাংলা" else "EN",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Universal Scanner Button
                    IconButton(
                        onClick = onOpenScanner,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LscGoldSecondary)
                            .testTag("universal_scan_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Universal QR Scanner",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Sync / Low Connectivity Manager Button
                    Box {
                        IconButton(
                            onClick = onOpenSyncManager,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isOffline) Color(0xFFDC2626)
                                    else if (pendingSyncCount > 0) Color(0xFFD97706)
                                    else Color.White.copy(alpha = 0.18f)
                                )
                                .testTag("sync_btn")
                        ) {
                            Icon(
                                imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudSync,
                                contentDescription = "Sync Manager",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        if (pendingSyncCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (pendingSyncCount > 9) "9+" else "$pendingSyncCount",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tier 2: Horizontally Scrollable Control & Status Strip (Role, Connectivity, Admin, Backup, Logout)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Role Picker Pill
                Box {
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .testTag("role_picker_chip")
                            .clickable { showRoleMenu = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ManageAccounts,
                                contentDescription = null,
                                tint = LscGoldLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "ভূমিকা: ${currentRole.titleBn}"
                                else
                                    "Role: ${currentRole.titleEn}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showRoleMenu,
                        onDismissRequest = { showRoleMenu = false }
                    ) {
                        UserRole.values().forEach { role ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = if (language == CampLanguage.BANGLA) role.titleBn else role.titleEn,
                                            fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = if (language == CampLanguage.BANGLA) role.titleEn else role.titleBn,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                },
                                leadingIcon = {
                                    if (role == currentRole) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = LscEmeraldPrimary)
                                    }
                                },
                                onClick = {
                                    onRoleSelected(role)
                                    showRoleMenu = false
                                }
                            )
                        }
                    }
                }

                // Offline / Low Connectivity Status Chip
                Surface(
                    color = (if (isOffline) Color(0xFFDC2626) else if (pendingSyncCount > 0) Color(0xFFD97706) else Color(0xFF10B981)).copy(alpha = 0.25f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        1.dp,
                        (if (isOffline) Color(0xFFFCA5A5) else if (pendingSyncCount > 0) Color(0xFFFDE68A) else Color(0xFF6EE7B7)).copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .clickable { onOpenSyncManager() }
                        .testTag("connectivity_status_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isOffline) Color(0xFFEF4444) else if (pendingSyncCount > 0) Color(0xFFF59E0B) else Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isOffline) {
                                if (language == CampLanguage.BANGLA) "কম কানেক্টিভিটি ($pendingSyncCount ক্যাশ)" else "Low Conn ($pendingSyncCount cached)"
                            } else {
                                if (pendingSyncCount > 0) {
                                    if (language == CampLanguage.BANGLA) "$pendingSyncCount টি সিঙ্ক অপেক্ষমান" else "$pendingSyncCount pending sync"
                                } else {
                                    if (language == CampLanguage.BANGLA) "অনলাইন সিঙ্ক" else "Cloud Synced"
                                }
                            },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Admin Lock/Unlock Status Chip
                Surface(
                    color = if (isAdminUnlocked) Color(0xFF10B981) else Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .testTag("admin_lock_btn")
                        .clickable { onAdminClick() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isAdminUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = "Admin Edit Mode",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "এডমিন" else "Admin",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Database Backup Chip
                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .testTag("topbar_backup_db_btn")
                        .clickable { onBackupClick() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = "Backup Database",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "ব্যাকআপ" else "Backup",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Logout Chip
                Surface(
                    color = Color(0xFFEF4444).copy(alpha = 0.32f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5).copy(alpha = 0.45f)),
                    modifier = Modifier
                        .testTag("topbar_logout_btn")
                        .clickable { onLogout() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == CampLanguage.BANGLA) "লগআউট" else "Logout",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
