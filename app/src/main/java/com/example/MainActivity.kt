package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.screens.attendance.AttendanceScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.bulletin.BulletinScreen
import com.example.ui.screens.competitions.CompetitionScreen
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.dashboard.CampDashboardScreen
import com.example.ui.screens.dashboard.RechartsDashboardScreen
import com.example.ui.screens.dashboard.RoleBasedDashboardWrapper
import com.example.ui.screens.departments.DepartmentsSopScreen
import com.example.ui.screens.emergency.EmergencySafeguardingScreen
import com.example.ui.screens.health.HealthScreen
import com.example.ui.screens.inventory.InventoryScreen
import com.example.ui.screens.participants.ParticipantListScreen
import com.example.ui.screens.rationale.CampRationaleScreen
import com.example.ui.screens.stations.StationRotationScreen
import com.example.ui.screens.studytour.StudyTourScreen
import com.example.ui.screens.subgroups.SubgroupsDirectoryScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                LscMainApp()
            }
        }
    }
}

@Composable
fun LscMainApp(
    campViewModel: CampViewModel = viewModel(),
    loginViewModel: LoginViewModel = viewModel()
) {
    val authState by loginViewModel.authState.collectAsStateWithLifecycle()
    val firestoreUserRole by loginViewModel.userRole.collectAsStateWithLifecycle()

    LaunchedEffect(firestoreUserRole) {
        campViewModel.setRole(firestoreUserRole)
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            android.util.Log.d("MainActivity", "POST_NOTIFICATIONS granted: $isGranted")
        }
        LaunchedEffect(Unit) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    val isLoggedIn by campViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentUser by campViewModel.currentUserAccount.collectAsStateWithLifecycle()
    val language by campViewModel.language.collectAsStateWithLifecycle()
    val currentRole by campViewModel.currentRole.collectAsStateWithLifecycle()
    val isOffline by campViewModel.isOffline.collectAsStateWithLifecycle()
    val isLowConnectivityMode by campViewModel.isLowConnectivityMode.collectAsStateWithLifecycle()
    val isNetworkAvailable by campViewModel.isNetworkAvailable.collectAsStateWithLifecycle()
    val isAutoSyncEnabled by campViewModel.isAutoSyncEnabled.collectAsStateWithLifecycle()
    val isSyncing by campViewModel.isSyncing.collectAsStateWithLifecycle()
    val lastSyncTime by campViewModel.lastSyncTime.collectAsStateWithLifecycle()
    val cachedSyncItems by campViewModel.cachedSyncItems.collectAsStateWithLifecycle()
    val pendingSyncCount by campViewModel.pendingSyncCount.collectAsStateWithLifecycle()
    val isAdminUnlocked by campViewModel.isAdminUnlocked.collectAsStateWithLifecycle()

    val participantCount by campViewModel.participantCount.collectAsStateWithLifecycle()
    val groups by campViewModel.groups.collectAsStateWithLifecycle()
    val stations by campViewModel.stations.collectAsStateWithLifecycle()
    val scheduleEvents by campViewModel.scheduleEvents.collectAsStateWithLifecycle()
    val attendanceEvents by campViewModel.attendanceEvents.collectAsStateWithLifecycle()
    val bulletins by campViewModel.bulletins.collectAsStateWithLifecycle()
    val incidents by campViewModel.incidents.collectAsStateWithLifecycle()

    val selectedParticipant by campViewModel.selectedParticipant.collectAsStateWithLifecycle()
    val allParticipants by campViewModel.allParticipants.collectAsStateWithLifecycle()
    val scanFeedback by campViewModel.scanFeedback.collectAsStateWithLifecycle()
    val statusNotification by campViewModel.statusNotification.collectAsStateWithLifecycle()
    val activeQuickAlerts by campViewModel.activeQuickAlerts.collectAsStateWithLifecycle()
    val latestPushAlert by campViewModel.latestPushAlert.collectAsStateWithLifecycle()
    val pendingHighPriorityHealthAlerts by campViewModel.pendingHighPriorityHealthAlerts.collectAsStateWithLifecycle()
    val attendanceRecords by campViewModel.attendanceRecords.collectAsStateWithLifecycle()
    val competitionScores by campViewModel.competitionScores.collectAsStateWithLifecycle()
    val healthStatuses by campViewModel.healthStatuses.collectAsStateWithLifecycle()
    val rooms by campViewModel.rooms.collectAsStateWithLifecycle()
    val mealDistributions by campViewModel.mealDistributions.collectAsStateWithLifecycle()
    val userAccounts by campViewModel.userAccounts.collectAsStateWithLifecycle()
    val vehicles by campViewModel.vehicles.collectAsStateWithLifecycle()

    var currentTab by remember { mutableIntStateOf(0) }
    var showUniversalScanner by remember { mutableStateOf(false) }
    var showQuickAlertDialog by remember { mutableStateOf(false) }
    var showDigitalIdCard by remember { mutableStateOf<ParticipantEntity?>(null) }
    var showCertificateDialog by remember { mutableStateOf<ParticipantEntity?>(null) }
    var showExcelImportDialog by remember { mutableStateOf(false) }
    var showExportParticipantsDialog by remember { mutableStateOf(false) }
    var showAddEditParticipantDialog by remember { mutableStateOf(false) }
    var participantToEdit by remember { mutableStateOf<ParticipantEntity?>(null) }
    var showAdminAuthDialog by remember { mutableStateOf(false) }
    var showBatchUploadDialog by remember { mutableStateOf(false) }
    var showBackupDatabaseDialog by remember { mutableStateOf(false) }
    var showLowConnectivityDialog by remember { mutableStateOf(false) }
    var selectedMoreModule by remember { mutableStateOf("COMPETITION") } // "COMPETITION", "HEALTH", "INVENTORY", "BULLETIN", "EMERGENCY"

    // Dynamic Options & Control Center State
    var showAdminControlCenterDialog by remember { mutableStateOf(false) }
    var showEditStationDialog by remember { mutableStateOf(false) }
    var stationToEdit by remember { mutableStateOf<StationEntity?>(null) }
    var showEditScheduleDialog by remember { mutableStateOf(false) }
    var scheduleToEdit by remember { mutableStateOf<ScheduleEventEntity?>(null) }
    var showEditRoomDialog by remember { mutableStateOf(false) }
    var roomToEdit by remember { mutableStateOf<RoomEntity?>(null) }
    var showCreateNoticeDialog by remember { mutableStateOf(false) }
    var showLogIncidentDialog by remember { mutableStateOf(false) }

    // Authentication Guard: The app CANNOT be used without login ("login ছাড়া ব্যবহার করা যাবে না")
    if (!isLoggedIn) {
        LoginScreen(
            campViewModel = campViewModel,
            language = language,
            onLanguageToggle = { campViewModel.toggleLanguage() }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CampTopBar(
                language = language,
                currentRole = currentRole,
                isOffline = isOffline,
                pendingSyncCount = pendingSyncCount,
                isAdminUnlocked = isAdminUnlocked,
                currentUser = currentUser,
                onLogout = { campViewModel.logout() },
                onAdminClick = {
                    if (!isAdminUnlocked) {
                        showAdminAuthDialog = true
                    } else {
                        showAdminControlCenterDialog = true
                    }
                },
                onBackupClick = { showBackupDatabaseDialog = true },
                onLanguageToggle = {
                    campViewModel.setLanguage(if (language == CampLanguage.BANGLA) CampLanguage.ENGLISH else CampLanguage.BANGLA)
                },
                onRoleSelected = { role -> campViewModel.setRole(role) },
                onToggleOffline = { campViewModel.toggleOfflineMode() },
                onTriggerSync = { campViewModel.triggerSync() },
                onOpenScanner = { showUniversalScanner = true },
                onOpenSyncManager = { showLowConnectivityDialog = true }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text(if (language == CampLanguage.BANGLA) "ড্যাশবোর্ড" else "Dashboard", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = LscEmeraldPrimary, indicatorColor = LscEmeraldLight),
                    modifier = Modifier.testTag("nav_tab_dashboard")
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(Icons.Default.PeopleAlt, contentDescription = "Participants") },
                    label = { Text(if (language == CampLanguage.BANGLA) "অংশগ্রহণকারী" else "Roster", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = LscEmeraldPrimary, indicatorColor = LscEmeraldLight),
                    modifier = Modifier.testTag("nav_tab_participants")
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = { Icon(Icons.Default.FactCheck, contentDescription = "Attendance") },
                    label = { Text(if (language == CampLanguage.BANGLA) "হাজিরা" else "Attendance", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = LscEmeraldPrimary, indicatorColor = LscEmeraldLight),
                    modifier = Modifier.testTag("nav_tab_attendance")
                )
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = { Icon(Icons.Default.Autorenew, contentDescription = "Stations") },
                    label = { Text(if (language == CampLanguage.BANGLA) "স্টেশন" else "Stations", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = LscEmeraldPrimary, indicatorColor = LscEmeraldLight),
                    modifier = Modifier.testTag("nav_tab_stations")
                )
                NavigationBarItem(
                    selected = currentTab == 4,
                    onClick = { currentTab = 4 },
                    icon = { Icon(Icons.Default.Category, contentDescription = "Modules") },
                    label = { Text(if (language == CampLanguage.BANGLA) "মডিউল" else "Modules", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = LscEmeraldPrimary, indicatorColor = LscEmeraldLight),
                    modifier = Modifier.testTag("nav_tab_modules")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
        ) {
            when (currentTab) {
                0 -> {
                    CampDashboardScreen(
                        language = language,
                        currentRole = currentRole,
                        groups = groups,
                        stations = stations,
                        scheduleEvents = scheduleEvents,
                        participantCount = participantCount,
                        attendanceEvents = attendanceEvents,
                        bulletins = bulletins,
                        incidents = incidents,
                        onNavigateToTab = { tabIndex ->
                            if (tabIndex in 0..3) {
                                currentTab = tabIndex
                            } else if (tabIndex == 4) {
                                currentTab = 4
                                selectedMoreModule = "COMPETITION"
                            } else if (tabIndex == 6) {
                                currentTab = 4
                                selectedMoreModule = "INVENTORY"
                            } else if (tabIndex == 7) {
                                currentTab = 4
                                selectedMoreModule = "BULLETIN"
                            } else if (tabIndex == 8) {
                                currentTab = 4
                                selectedMoreModule = "CHARTS"
                            } else if (tabIndex == 9) {
                                currentTab = 4
                                selectedMoreModule = "DEPARTMENTS"
                            } else if (tabIndex == 10) {
                                currentTab = 4
                                selectedMoreModule = "SUBGROUPS"
                            } else if (tabIndex == 11) {
                                currentTab = 4
                                selectedMoreModule = "STUDY_TOUR"
                            } else if (tabIndex == 12) {
                                currentTab = 4
                                selectedMoreModule = "RATIONALE"
                            }
                        },
                        onOpenScanner = { showUniversalScanner = true },
                        onOpenCharts = {
                            currentTab = 4
                            selectedMoreModule = "CHARTS"
                        },
                        onOpenBackupDatabase = {
                            showBackupDatabaseDialog = true
                        },
                        participants = allParticipants,
                        selectedParticipant = selectedParticipant,
                        onSelectParticipant = { campViewModel.selectParticipant(it) },
                        activeAlerts = activeQuickAlerts,
                        pendingHealthAlerts = pendingHighPriorityHealthAlerts,
                        onOpenSendAlertDialog = { showQuickAlertDialog = true },
                        onAcknowledgeAlert = { alertId -> campViewModel.acknowledgeQuickAlert(alertId) },
                        onResolveAlert = { alertId, notes -> campViewModel.resolveQuickAlert(alertId, notes) },
                        onAttendHealthAlert = { recordId -> campViewModel.attendHighPriorityHealthAlert(recordId) },
                        onResolveHealthAlert = { recordId, notes -> campViewModel.resolveHighPriorityHealthAlert(recordId, notes) },
                        competitionScores = competitionScores,
                        attendanceRecords = attendanceRecords,
                        mealDistributions = mealDistributions,
                        rooms = rooms
                    )
                }
                1 -> {
                    val participants by campViewModel.participants.collectAsStateWithLifecycle()
                    val searchQuery by campViewModel.searchQuery.collectAsStateWithLifecycle()
                    val selectedGroupFilter by campViewModel.selectedGroupFilter.collectAsStateWithLifecycle()

                    ParticipantListScreen(
                        language = language,
                        participants = participants,
                        groups = groups,
                        searchQuery = searchQuery,
                        selectedGroupFilter = selectedGroupFilter,
                        healthStatuses = healthStatuses,
                        onSearchChange = { campViewModel.setSearchQuery(it) },
                        onGroupFilterChange = { campViewModel.setGroupFilter(it) },
                        onSelectParticipant = { p -> campViewModel.selectParticipant(p) },
                        onOpenImportDialog = { showExcelImportDialog = true },
                        onOpenExportDialog = { showExportParticipantsDialog = true },
                        onAddParticipant = {
                            participantToEdit = null
                            showAddEditParticipantDialog = true
                        }
                    )
                }
                2 -> {
                    val participants by campViewModel.participants.collectAsStateWithLifecycle()

                    AttendanceScreen(
                        language = language,
                        attendanceEvents = attendanceEvents,
                        attendanceRecords = attendanceRecords,
                        stations = stations,
                        participants = participants,
                        groups = groups,
                        onMarkAttendance = { evId, p, st ->
                            campViewModel.markAttendanceDirect(evId, p, st)
                        },
                        onOpenScanner = { showUniversalScanner = true }
                    )
                }
                3 -> {
                    val selectedDay by campViewModel.selectedDay.collectAsStateWithLifecycle()

                    StationRotationScreen(
                        language = language,
                        stations = stations,
                        scheduleEvents = scheduleEvents,
                        groups = groups,
                        selectedDay = selectedDay,
                        isAdminUnlocked = isAdminUnlocked,
                        onRequestAdminUnlock = { showAdminAuthDialog = true },
                        onDaySelect = { campViewModel.setSelectedDay(it) },
                        onUpdateScheduleEvent = { campViewModel.updateScheduleEvent(it) },
                        onUpdateGroup = { campViewModel.updateGroup(it) },
                        onOpenScanner = { showUniversalScanner = true }
                    )
                }
                4 -> {
                    // Modules Hub (Competitions, Health, Inventory, Bulletin, Emergency)
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Module switcher bar
                        ScrollableTabRow(
                            selectedTabIndex = when (selectedMoreModule) {
                                "CHARTS" -> 0
                                "COMPETITION" -> 1
                                "HEALTH" -> 2
                                "INVENTORY" -> 3
                                "BULLETIN" -> 4
                                "EMERGENCY" -> 5
                                "DEPARTMENTS" -> 6
                                "SUBGROUPS" -> 7
                                "STUDY_TOUR" -> 8
                                "RATIONALE" -> 9
                                else -> 0
                            },
                            containerColor = Color.White,
                            contentColor = LscEmeraldPrimary,
                            edgePadding = 12.dp
                        ) {
                            Tab(
                                selected = selectedMoreModule == "CHARTS",
                                onClick = { selectedMoreModule = "CHARTS" },
                                text = { Text(if (language == CampLanguage.BANGLA) "রিচার্টস ও চার্টস" else "Recharts & Trends", fontSize = 11.sp, fontWeight = if (selectedMoreModule == "CHARTS") FontWeight.Bold else FontWeight.Normal) },
                                icon = { Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.testTag("module_tab_recharts")
                            )
                            Tab(
                                selected = selectedMoreModule == "COMPETITION",
                                onClick = { selectedMoreModule = "COMPETITION" },
                                text = { Text(if (language == CampLanguage.BANGLA) "প্রতিযোগিতা ও স্কোর" else "Competitions", fontSize = 11.sp) }
                            )
                            Tab(
                                selected = selectedMoreModule == "HEALTH",
                                onClick = { selectedMoreModule = "HEALTH" },
                                text = { Text(if (language == CampLanguage.BANGLA) "মেডিকেল ও স্বাস্থ্য" else "Health & Medical", fontSize = 11.sp) }
                            )
                            Tab(
                                selected = selectedMoreModule == "INVENTORY",
                                onClick = { selectedMoreModule = "INVENTORY" },
                                text = { Text(if (language == CampLanguage.BANGLA) "পুরস্কার ইনভেন্টরি" else "Awards Stock", fontSize = 11.sp) }
                            )
                            Tab(
                                selected = selectedMoreModule == "BULLETIN",
                                onClick = { selectedMoreModule = "BULLETIN" },
                                text = { Text(if (language == CampLanguage.BANGLA) "দেয়াল পত্রিকা" else "Bulletin", fontSize = 11.sp) }
                            )
                            Tab(
                                selected = selectedMoreModule == "EMERGENCY",
                                onClick = { selectedMoreModule = "EMERGENCY" },
                                text = { Text(if (language == CampLanguage.BANGLA) "জরুরি সেবা" else "Emergency", fontSize = 11.sp) }
                            )
                            Tab(
                                selected = selectedMoreModule == "DEPARTMENTS",
                                onClick = { selectedMoreModule = "DEPARTMENTS" },
                                text = { Text(if (language == CampLanguage.BANGLA) "১২টি বিভাগ ও এসওপি" else "12 Depts & SOPs", fontSize = 11.sp, fontWeight = if (selectedMoreModule == "DEPARTMENTS") FontWeight.Bold else FontWeight.Normal) },
                                icon = { Icon(Icons.Default.CorporateFare, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.testTag("module_tab_departments")
                            )
                            Tab(
                                selected = selectedMoreModule == "SUBGROUPS",
                                onClick = { selectedMoreModule = "SUBGROUPS" },
                                text = { Text(if (language == CampLanguage.BANGLA) "২৪টি সাব-গ্রুপ" else "24 Subgroups", fontSize = 11.sp, fontWeight = if (selectedMoreModule == "SUBGROUPS") FontWeight.Bold else FontWeight.Normal) },
                                icon = { Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.testTag("module_tab_subgroups")
                            )
                            Tab(
                                selected = selectedMoreModule == "STUDY_TOUR",
                                onClick = { selectedMoreModule = "STUDY_TOUR" },
                                text = { Text(if (language == CampLanguage.BANGLA) "শিক্ষা সফর ও বাস" else "Study Tour Fleet", fontSize = 11.sp, fontWeight = if (selectedMoreModule == "STUDY_TOUR") FontWeight.Bold else FontWeight.Normal) },
                                icon = { Icon(Icons.Default.DirectionsBus, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.testTag("module_tab_studytour")
                            )
                            Tab(
                                selected = selectedMoreModule == "RATIONALE",
                                onClick = { selectedMoreModule = "RATIONALE" },
                                text = { Text(if (language == CampLanguage.BANGLA) "রূপরেখা ও ফলাফল" else "Rationale & Outcomes", fontSize = 11.sp, fontWeight = if (selectedMoreModule == "RATIONALE") FontWeight.Bold else FontWeight.Normal) },
                                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.testTag("module_tab_rationale")
                            )
                        }

                        when (selectedMoreModule) {
                            "CHARTS" -> {
                                val healthStatuses by campViewModel.healthStatuses.collectAsStateWithLifecycle()
                                RechartsDashboardScreen(
                                    language = language,
                                    attendanceEvents = attendanceEvents,
                                    healthStatuses = healthStatuses,
                                    groups = groups,
                                    stations = stations,
                                    participantCount = participantCount,
                                    onBack = { currentTab = 0 }
                                )
                            }
                            "COMPETITION" -> {
                                val participants by campViewModel.participants.collectAsStateWithLifecycle()
                                val competitions by campViewModel.competitions.collectAsStateWithLifecycle()
                                val competitionScores by campViewModel.competitionScores.collectAsStateWithLifecycle()
                                val judges by campViewModel.judges.collectAsStateWithLifecycle()

                                CompetitionScreen(
                                    language = language,
                                    competitions = competitions,
                                    competitionScores = competitionScores,
                                    groups = groups,
                                    participants = participants,
                                    judges = judges,
                                    isAdminUnlocked = isAdminUnlocked,
                                    onRequestAdminUnlock = { showAdminAuthDialog = true },
                                    stations = stations,
                                    selectedParticipant = selectedParticipant,
                                    onSelectParticipant = { campViewModel.selectParticipant(it) },
                                    onAddJudge = { nameBn, nameEn, designation, category, phone, notes ->
                                        campViewModel.addJudge(nameBn, nameEn, designation, category, phone, notes)
                                    },
                                    onUpdateJudge = { judge ->
                                        campViewModel.updateJudge(judge)
                                    },
                                    onDeleteJudge = { judge ->
                                        campViewModel.deleteJudge(judge)
                                    },
                                    onBatchImportJudges = { campViewModel.batchImportJudges(it) },
                                    onBatchImportVolunteers = { campViewModel.batchImportVolunteers(it) },
                                    onBatchImportDutyOfficers = { campViewModel.batchImportDutyOfficers(it) },
                                    onSubmitScore = { cId, p, acc, pres, disc, rem ->
                                        campViewModel.submitJudgeScore(cId, p, acc, pres, disc, rem)
                                    }
                                )
                            }
                            "HEALTH" -> {
                                val participants by campViewModel.participants.collectAsStateWithLifecycle()
                                val healthRecords by campViewModel.healthRecords.collectAsStateWithLifecycle()

                                HealthScreen(
                                    language = language,
                                    currentRole = currentRole,
                                    healthRecords = healthRecords,
                                    participants = participants,
                                    onSubmitRecord = { p, t, doc, v, diag, adv, isEm ->
                                        campViewModel.submitHealthCheckup(p, t, doc, v, diag, adv, isEm)
                                    }
                                )
                            }
                            "INVENTORY" -> {
                                val inventoryItems by campViewModel.inventoryItems.collectAsStateWithLifecycle()

                                InventoryScreen(
                                    language = language,
                                    inventoryItems = inventoryItems,
                                    onIssueItem = { itemId, qty, recipient, officer ->
                                        campViewModel.issueAwardInventory(itemId, qty, recipient, officer)
                                    }
                                )
                            }
                            "BULLETIN" -> {
                                BulletinScreen(
                                    language = language,
                                    bulletins = bulletins,
                                    isAdminUnlocked = isAdminUnlocked,
                                    onRequestAdminUnlock = { showAdminAuthDialog = true },
                                    onPublishBulletin = { titleBn, titleEn, sBn, sEn, auth, spot, day, dt ->
                                        campViewModel.publishBulletin(titleBn, titleEn, sBn, sEn, auth, spot, day, dt)
                                    }
                                )
                            }
                            "EMERGENCY" -> {
                                EmergencySafeguardingScreen(
                                    language = language,
                                    incidents = incidents,
                                    onReportIncident = { title, code, name, sev, loc, desc, act ->
                                        campViewModel.reportIncident(title, code, name, sev, loc, desc, act)
                                    }
                                )
                            }
                            "DEPARTMENTS" -> {
                                DepartmentsSopScreen(
                                    language = language,
                                    onBack = { currentTab = 0 }
                                )
                            }
                            "SUBGROUPS" -> {
                                val participants by campViewModel.participants.collectAsStateWithLifecycle()
                                SubgroupsDirectoryScreen(
                                    language = language,
                                    groups = groups,
                                    participants = participants,
                                    onSelectParticipant = { campViewModel.selectParticipant(it) },
                                    onBack = { currentTab = 0 }
                                )
                            }
                            "STUDY_TOUR" -> {
                                StudyTourScreen(
                                    language = language,
                                    vehicles = vehicles,
                                    onUpdateVehicle = { campViewModel.updateVehicle(it) },
                                    onBack = { currentTab = 0 }
                                )
                            }
                            "RATIONALE" -> {
                                CampRationaleScreen(
                                    language = language,
                                    onBack = { currentTab = 0 }
                                )
                            }
                        }
                    }
                }
            }

            // High-Priority In-App Push Notification Banner
            latestPushAlert?.let { pushAlert ->
                InAppPushNotificationBanner(
                    alert = pushAlert,
                    language = language,
                    onViewOnDashboard = {
                        currentTab = 0
                        campViewModel.dismissPushAlert()
                    },
                    onDismiss = {
                        campViewModel.dismissPushAlert()
                    },
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }

            // Notification Snackbar / Feedback Banner
            if (statusNotification != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { campViewModel.clearStatusNotification() }) {
                            Text("ঠিক আছে", color = LscGoldLight)
                        }
                    }
                ) {
                    Text(text = statusNotification!!, fontSize = 12.sp)
                }
            }

            // Scan Result Dialog
            if (scanFeedback != null) {
                AlertDialog(
                    onDismissRequest = { campViewModel.clearScanFeedback() },
                    title = {
                        Text(
                            text = when (val res = scanFeedback!!) {
                                is ScanResult.Success -> res.title
                                is ScanResult.Warning -> res.title
                                is ScanResult.Error -> res.title
                            },
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = when (val res = scanFeedback!!) {
                                is ScanResult.Success -> res.message
                                is ScanResult.Warning -> res.message
                                is ScanResult.Error -> res.message
                            }
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { campViewModel.clearScanFeedback() },
                            colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary)
                        ) {
                            Text("ঠিক আছে")
                        }
                    }
                )
            }
        }
    }

    // Modal Dialogs
    if (showUniversalScanner) {
        UniversalScannerDialog(
            language = language,
            initialMode = if (currentTab == 3) "STATION" else "ATTENDANCE",
            onDismiss = { showUniversalScanner = false },
            onScanCode = { code, mode ->
                campViewModel.processUniversalScan(code, mode)
                showUniversalScanner = false
            }
        )
    }

    if (showQuickAlertDialog) {
        QuickAlertDialog(
            language = language,
            currentRole = currentRole,
            participants = allParticipants,
            stations = stations,
            onDismiss = { showQuickAlertDialog = false },
            onSendAlert = { title, category, severity, location, desc, camper ->
                campViewModel.sendQuickAlert(
                    title = title,
                    category = category,
                    severity = severity,
                    location = location,
                    desc = desc,
                    participant = camper
                )
                showQuickAlertDialog = false
            }
        )
    }

    if (selectedParticipant != null) {
        val healthStatus by campViewModel.selectedParticipantHealthStatus.collectAsStateWithLifecycle()
        val participantMedicalRecords by remember(selectedParticipant!!.id) {
            campViewModel.getHealthRecordsForParticipant(selectedParticipant!!.id)
        }.collectAsStateWithLifecycle(initialValue = emptyList())

        ParticipantDetailDialog(
            participant = selectedParticipant!!,
            language = language,
            onDismiss = { campViewModel.selectParticipant(null) },
            onViewIdCard = {
                showDigitalIdCard = selectedParticipant
            },
            onGenerateCertificate = {
                showCertificateDialog = selectedParticipant
            },
            onEditParticipant = {
                participantToEdit = selectedParticipant
                showAddEditParticipantDialog = true
            },
            healthStatus = healthStatus,
            medicalRecords = participantMedicalRecords,
            onRecordHealthCheckIn = { status, notes ->
                campViewModel.recordDailyHealthCheckIn(selectedParticipant!!, status, notes)
            }
        )
    }

    if (showAddEditParticipantDialog) {
        AddEditParticipantDialog(
            participant = participantToEdit,
            groups = groups,
            language = language,
            onDismiss = {
                showAddEditParticipantDialog = false
                participantToEdit = null
            },
            onSave = { p ->
                if (participantToEdit == null) {
                    campViewModel.addParticipant(p)
                } else {
                    campViewModel.updateParticipant(p)
                }
                showAddEditParticipantDialog = false
                participantToEdit = null
            },
            onDelete = { p ->
                campViewModel.deleteParticipant(p)
                showAddEditParticipantDialog = false
                participantToEdit = null
            }
        )
    }

    if (showDigitalIdCard != null) {
        DigitalIdCard(
            participant = showDigitalIdCard!!,
            language = language,
            onClose = { showDigitalIdCard = null }
        )
    }

    if (showCertificateDialog != null) {
        CertificateGeneratorDialog(
            participant = showCertificateDialog!!,
            isBangla = language == CampLanguage.BANGLA,
            onDismiss = { showCertificateDialog = null }
        )
    }

    if (showAdminControlCenterDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showAdminControlCenterDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val stationsList by campViewModel.stations.collectAsStateWithLifecycle()
            val schedulesList by campViewModel.scheduleEvents.collectAsStateWithLifecycle()
            val roomsList by campViewModel.rooms.collectAsStateWithLifecycle()
            val usersList by campViewModel.userAccounts.collectAsStateWithLifecycle()

            AdminDashboardScreen(
                language = language,
                stations = stationsList,
                schedules = schedulesList,
                rooms = roomsList,
                groups = groups,
                userAccounts = usersList,
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
                    }
                },
                onBackToMain = { showAdminControlCenterDialog = false },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    if (showEditStationDialog) {
        EditStationDialog(
            station = stationToEdit,
            isBangla = language == CampLanguage.BANGLA,
            onDismiss = {
                showEditStationDialog = false
                stationToEdit = null
            },
            onSave = { st ->
                if (stationToEdit == null) {
                    campViewModel.addStation(st)
                } else {
                    campViewModel.updateStation(st)
                }
                showEditStationDialog = false
                stationToEdit = null
            }
        )
    }

    if (showEditScheduleDialog) {
        EditScheduleEventDialog(
            event = scheduleToEdit,
            isBangla = language == CampLanguage.BANGLA,
            onDismiss = {
                showEditScheduleDialog = false
                scheduleToEdit = null
            },
            onSave = { ev ->
                if (scheduleToEdit == null) {
                    campViewModel.addScheduleEvent(ev)
                } else {
                    campViewModel.updateScheduleEvent(ev)
                }
                showEditScheduleDialog = false
                scheduleToEdit = null
            }
        )
    }

    if (showEditRoomDialog) {
        EditRoomDialog(
            room = roomToEdit,
            groups = groups,
            isBangla = language == CampLanguage.BANGLA,
            onDismiss = {
                showEditRoomDialog = false
                roomToEdit = null
            },
            onSave = { rm ->
                if (roomToEdit == null) {
                    campViewModel.addRoom(rm)
                } else {
                    campViewModel.updateRoom(rm)
                }
                showEditRoomDialog = false
                roomToEdit = null
            }
        )
    }

    if (showCreateNoticeDialog) {
        CreateNoticeDialog(
            isBangla = language == CampLanguage.BANGLA,
            onDismiss = { showCreateNoticeDialog = false },
            onPublish = { notice ->
                campViewModel.addBulletinNotice(notice)
                showCreateNoticeDialog = false
            }
        )
    }

    if (showLogIncidentDialog) {
        LogIncidentDialog(
            isBangla = language == CampLanguage.BANGLA,
            onDismiss = { showLogIncidentDialog = false },
            onSubmit = { title, pCode, pName, severity, location, desc, action ->
                campViewModel.reportIncident(title, pCode, pName, severity, location, desc, action)
                showLogIncidentDialog = false
            }
        )
    }

    if (showExcelImportDialog) {
        ExcelImportDialog(
            language = language,
            onDismiss = { showExcelImportDialog = false },
            onConfirmImport = { csv ->
                campViewModel.importCsv(csv)
            }
        )
    }

    if (showExportParticipantsDialog) {
        val allParticipants by campViewModel.allParticipants.collectAsStateWithLifecycle()
        val filteredParticipants by campViewModel.participants.collectAsStateWithLifecycle()
        val searchQuery by campViewModel.searchQuery.collectAsStateWithLifecycle()

        ExportParticipantsDialog(
            language = language,
            allParticipants = allParticipants,
            filteredParticipants = filteredParticipants,
            searchQuery = searchQuery,
            onDismiss = { showExportParticipantsDialog = false },
            onOpenDatabaseBackup = {
                showExportParticipantsDialog = false
                showBackupDatabaseDialog = true
            }
        )
    }

    if (showBackupDatabaseDialog) {
        BackupDatabaseDialog(
            language = language,
            participantCount = participantCount,
            onDismiss = { showBackupDatabaseDialog = false }
        )
    }

    if (showAdminAuthDialog) {
        AdminPasswordDialog(
            language = language,
            onDismiss = { showAdminAuthDialog = false },
            onUnlock = { pass -> campViewModel.unlockAdmin(pass) }
        )
    }

    if (showBatchUploadDialog) {
        BatchImportDialog(
            language = language,
            initialTarget = "JUDGES",
            onDismiss = { showBatchUploadDialog = false },
            onImportJudges = { campViewModel.batchImportJudges(it) },
            onImportVolunteers = { campViewModel.batchImportVolunteers(it) },
            onImportDutyOfficers = { campViewModel.batchImportDutyOfficers(it) }
        )
    }

    if (showLowConnectivityDialog) {
        LowConnectivityDialog(
            language = language,
            isLowConnectivityMode = isLowConnectivityMode,
            isNetworkAvailable = isNetworkAvailable,
            isAutoSyncEnabled = isAutoSyncEnabled,
            isSyncing = isSyncing,
            pendingSyncCount = pendingSyncCount,
            lastSyncTime = lastSyncTime,
            cachedItems = cachedSyncItems,
            onToggleLowConnectivity = { campViewModel.setLowConnectivityMode(it) },
            onToggleAutoSync = { campViewModel.setAutoSyncEnabled(it) },
            onTriggerSync = { campViewModel.triggerSync() },
            onSimulateNetwork = { campViewModel.simulateNetworkAvailability(it) },
            onResetSimulation = { campViewModel.resetNetworkSimulation() },
            onClearSyncedHistory = { campViewModel.clearSyncedHistory() },
            onDismiss = { showLowConnectivityDialog = false }
        )
    }
}
