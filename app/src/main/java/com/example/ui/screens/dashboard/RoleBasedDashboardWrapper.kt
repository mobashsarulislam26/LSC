package com.example.ui.screens.dashboard

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.bulletin.BulletinScreen
import com.example.ui.screens.health.HealthScreen
import com.example.ui.screens.participants.ParticipantListScreen
import com.example.ui.screens.stations.StationRotationScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuthState
import com.example.ui.viewmodel.CampViewModel
import com.example.ui.viewmodel.LoginViewModel

/**
 * Composable Wrapper that handles role-based redirection upon successful authentication.
 * Fetches the user's role document from Firestore (via LoginViewModel) and dynamically
 * redirects users to their role-specific dashboard view.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleBasedDashboardWrapper(
    loginViewModel: LoginViewModel,
    campViewModel: CampViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authState by loginViewModel.authState.collectAsStateWithLifecycle()
    val userRole by loginViewModel.userRole.collectAsStateWithLifecycle()
    val language by campViewModel.language.collectAsStateWithLifecycle()

    val isBangla = language == CampLanguage.BANGLA

    // Observe Firestore dynamic data
    val stations by campViewModel.stations.collectAsStateWithLifecycle()
    val scheduleEvents by campViewModel.scheduleEvents.collectAsStateWithLifecycle()
    val rooms by campViewModel.rooms.collectAsStateWithLifecycle()
    val groups by campViewModel.groups.collectAsStateWithLifecycle()
    val userAccounts by campViewModel.userAccounts.collectAsStateWithLifecycle()
    val participants by campViewModel.participants.collectAsStateWithLifecycle()
    val healthRecords by campViewModel.healthRecords.collectAsStateWithLifecycle()
    val bulletins by campViewModel.bulletins.collectAsStateWithLifecycle()
    val auditLogs by campViewModel.auditLogs.collectAsStateWithLifecycle()

    var activeRoleOverride by remember { mutableStateOf<UserRole?>(null) }
    val effectiveRole = activeRoleOverride ?: userRole

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = LscEmeraldLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isBangla) effectiveRole.titleBn else effectiveRole.titleEn,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LscEmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "ড্যাশবোর্ড" else "Dashboard",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                actions = {
                    // Role Switcher Dropdown for Testing/Admin Overrides
                    var showRoleMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showRoleMenu = true }) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Switch View Role")
                    }
                    DropdownMenu(
                        expanded = showRoleMenu,
                        onDismissRequest = { showRoleMenu = false }
                    ) {
                        UserRole.values().forEach { r ->
                            DropdownMenuItem(
                                text = { Text(if (isBangla) r.titleBn else r.titleEn) },
                                onClick = {
                                    activeRoleOverride = r
                                    showRoleMenu = false
                                }
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            loginViewModel.signOut()
                            onLogout()
                        },
                        modifier = Modifier.testTag("role_dashboard_logout_btn")
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8FAFC))
        ) {
            Crossfade(targetState = effectiveRole, label = "RoleDashboardCrossfade") { role ->
                when (role) {
                    UserRole.SUPER_ADMIN, UserRole.CAMP_CHIEF -> {
                        // Full Admin Dashboard
                        AdminDashboardScreen(
                            language = language,
                            stations = stations,
                            schedules = scheduleEvents,
                            rooms = rooms,
                            groups = groups,
                            userAccounts = userAccounts,
                            auditLogs = auditLogs,
                            onAddStation = { campViewModel.addStation(it) },
                            onUpdateStation = { campViewModel.updateStation(it) },
                            onDeleteStation = { campViewModel.deleteStation(it) },
                            onAddSchedule = { campViewModel.addScheduleEvent(it) },
                            onUpdateSchedule = { campViewModel.updateScheduleEvent(it) },
                            onDeleteSchedule = { campViewModel.deleteScheduleEvent(it) },
                            onAddRoom = { campViewModel.addRoom(it) },
                            onUpdateRoom = { campViewModel.updateRoom(it) },
                            onDeleteRoom = { campViewModel.deleteRoom(it) },
                            onAssignUserRole = { uid, r ->
                                (authState as? AuthState.Authenticated)?.user?.let { user ->
                                    loginViewModel.assignRoleAndProceed(user, r)
                                    campViewModel.logAdminAuditTrail(
                                        actionType = "USER_ROLE_UPDATE",
                                        targetEntity = "UserRole",
                                        description = "ব্যবহারকারী রোল পরিবর্তন: $uid -> ${r.titleBn} (${r.name})",
                                        adminId = user.uid,
                                        adminEmail = user.email ?: "admin@lsc.org",
                                        modifiedRecordDetails = """{"targetUid": "$uid", "newRole": "${r.name}", "newRoleTitleBn": "${r.titleBn}"}"""
                                    )
                                }
                            },
                            onBulkUploadSchedules = { campViewModel.bulkUploadSchedules(it) },
                            onImportParticipantsCsvJson = { campViewModel.importParticipantsCsvJson(it) }
                        )
                    }

                    UserRole.STATION_MODERATOR -> {
                        // Focused Station Rotation & Evaluation Dashboard
                        StationRotationScreen(
                            language = language,
                            stations = stations,
                            scheduleEvents = scheduleEvents,
                            groups = groups,
                            selectedDay = 1,
                            isAdminUnlocked = true,
                            onRequestAdminUnlock = {},
                            onDaySelect = {},
                            onUpdateScheduleEvent = { campViewModel.updateScheduleEvent(it) },
                            onUpdateGroup = { campViewModel.updateGroup(it) },
                            onOpenScanner = {}
                        )
                    }

                    UserRole.GROUP_LEADER, UserRole.MENTOR, UserRole.VOLUNTEER -> {
                        // Focused Roster & Room Allocation Dashboard
                        ParticipantListScreen(
                            language = language,
                            participants = participants,
                            groups = groups,
                            searchQuery = "",
                            selectedGroupFilter = null,
                            healthStatuses = emptyList(),
                            onSearchChange = {},
                            onGroupFilterChange = {},
                            onSelectParticipant = { campViewModel.selectParticipant(it) },
                            onOpenImportDialog = {},
                            onOpenExportDialog = {},
                            onAddParticipant = {}
                        )
                    }

                    UserRole.MEDICAL_TEAM -> {
                        // Health & Emergency Safeguarding Dashboard
                        HealthScreen(
                            language = language,
                            currentRole = UserRole.MEDICAL_TEAM,
                            healthRecords = healthRecords,
                            participants = participants,
                            onSubmitRecord = { p, t, doc, v, diag, adv, isEm ->
                                campViewModel.submitHealthCheckup(p, t, doc, v, diag, adv, isEm)
                            }
                        )
                    }

                    UserRole.COMPETITION_JUDGE, UserRole.INVENTORY_OFFICER, UserRole.BULLETIN_EDITOR, UserRole.PARTICIPANT -> {
                        // Participant Bulletin & Personal Schedule View
                        BulletinScreen(
                            language = language,
                            bulletins = bulletins,
                            isAdminUnlocked = false,
                            onRequestAdminUnlock = {},
                            onPublishBulletin = { _, _, _, _, _, _, _, _ -> }
                        )
                    }
                }
            }
        }
    }
}
