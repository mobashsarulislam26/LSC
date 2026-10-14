package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.model.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampTopBar(
    language: CampLanguage,
    currentRole: UserRole,
    isOffline: Boolean,
    pendingSyncCount: Int,
    isAdminUnlocked: Boolean = false,
    onAdminClick: () -> Unit = {},
    onBackupClick: () -> Unit = {},
    onLanguageToggle: () -> Unit,
    onRoleSelected: (UserRole) -> Unit,
    onToggleOffline: () -> Unit,
    onTriggerSync: () -> Unit,
    onOpenScanner: () -> Unit,
    onOpenSyncManager: () -> Unit = onTriggerSync
) {
    var showRoleMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    if (showAboutDialog) {
        CampAboutDialog(
            language = language,
            onDismiss = { showAboutDialog = false }
        )
    }

    Surface(
        color = MaterialTheme.colorScheme.primary,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Main Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Title & Camp Day
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    CampLogoImage(
                        size = 42.dp,
                        elevation = 4.dp,
                        onClick = { showAboutDialog = true },
                        modifier = Modifier.testTag("app_logo_topbar")
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (language == CampLanguage.BANGLA) "ক্যাম্প বন্ধু" else "LSC Companion",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = LscGoldSecondary,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (language == CampLanguage.BANGLA) "দিন ১" else "Day 1",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (language == CampLanguage.BANGLA) "আবাসিক জীবন দক্ষতা ক্যাম্প" else "Residential Life Skills Camp",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Action Icons (Language, Admin Lock, QR Scanner, Sync Status, Database Backup)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Database Backup Button
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .testTag("topbar_backup_db_btn")
                            .clickable { onBackupClick() }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = "Backup Database",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Admin Lock/Unlock Status Button
                    Surface(
                        color = if (isAdminUnlocked) Color(0xFF10B981) else Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .testTag("admin_lock_btn")
                            .clickable { onAdminClick() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isAdminUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = "Admin Edit Mode",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            if (isAdminUnlocked) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "এডমিন",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Language Switcher
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .testTag("lang_toggle_btn")
                            .clickable { onLanguageToggle() }
                    ) {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "বাংলা" else "EN",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Universal Scanner Button
                    IconButton(
                        onClick = onOpenScanner,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(LscGoldSecondary)
                            .testTag("universal_scan_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Universal QR Scanner",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Sync / Low Connectivity Manager Button
                    Box {
                        IconButton(
                            onClick = onOpenSyncManager,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isOffline) Color(0xFFDC2626) else if (pendingSyncCount > 0) Color(0xFFD97706) else Color.White.copy(alpha = 0.2f))
                                .testTag("sync_btn")
                        ) {
                            Icon(
                                imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudSync,
                                contentDescription = "Sync Manager",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
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

            // Sub-row: Active Role & Switcher + Offline warning indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Role Picker Pill
                Box {
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .testTag("role_picker_chip")
                            .clickable { showRoleMenu = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ManageAccounts,
                                contentDescription = null,
                                tint = LscGoldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA)
                                    "ভূমিকা: ${currentRole.titleBn}"
                                else
                                    "Role: ${currentRole.titleEn}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
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

                // Offline / Low Connectivity indicator
                Surface(
                    color = (if (isOffline) Color(0xFFDC2626) else if (pendingSyncCount > 0) Color(0xFFD97706) else Color(0xFF10B981)).copy(alpha = 0.25f),
                    shape = RoundedCornerShape(12.dp),
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
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isOffline) Color(0xFFEF4444) else if (pendingSyncCount > 0) Color(0xFFF59E0B) else Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
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
            }
        }
    }
}
