package com.example.ui.screens.admin

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.firestore.FirestoreManager
import com.example.data.model.*
import com.example.ui.components.BulkScheduleUploadDialog
import com.example.ui.components.EditRoomDialog
import com.example.ui.components.EditScheduleEventDialog
import com.example.ui.components.EditStationDialog
import com.example.ui.components.ParticipantRosterImportDialog
import com.example.ui.screens.departments.DepartmentsSopScreen
import com.example.ui.screens.subgroups.SubgroupsDirectoryScreen
import com.example.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Route definitions for Admin Dashboard Navigation Controller.
 */
object AdminRoutes {
    const val OVERVIEW = "admin_overview"
    const val STATIONS = "admin_stations"
    const val SCHEDULES = "admin_schedules"
    const val ROOMS = "admin_rooms"
    const val DEPARTMENTS = "admin_departments"
    const val SUBGROUPS = "admin_subgroups"
    const val USERS = "admin_users"
    const val AUDIT_TRAIL = "admin_audit_trail"
}

/**
 * Full-featured Admin Dashboard UI component with Navigation Controller
 * to toggle visibility of modules for managing camp stations, event schedules,
 * room allocations, and user roles stored in Firestore.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    language: CampLanguage,
    stations: List<StationEntity>,
    schedules: List<ScheduleEventEntity>,
    rooms: List<RoomEntity>,
    groups: List<CampGroupEntity>,
    userAccounts: List<UserAccountEntity>,
    onAddStation: (StationEntity) -> Unit,
    onUpdateStation: (StationEntity) -> Unit,
    onDeleteStation: (StationEntity) -> Unit,
    onAddSchedule: (ScheduleEventEntity) -> Unit,
    onUpdateSchedule: (ScheduleEventEntity) -> Unit,
    onDeleteSchedule: (ScheduleEventEntity) -> Unit,
    onAddRoom: (RoomEntity) -> Unit,
    onUpdateRoom: (RoomEntity) -> Unit,
    onDeleteRoom: (RoomEntity) -> Unit,
    onAssignUserRole: (String, UserRole) -> Unit,
    auditLogs: List<AuditLogEntity> = emptyList(),
    participants: List<ParticipantEntity> = emptyList(),
    onSelectParticipant: ((ParticipantEntity) -> Unit)? = null,
    onBatchSubgroupAttendance: ((Long, String, Int, String, Set<Long>, String?) -> Unit)? = null,
    onBulkUploadSchedules: ((List<ScheduleEventEntity>) -> Unit)? = null,
    onImportParticipantsCsvJson: ((String) -> Unit)? = null,
    onBackToMain: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AdminRoutes.OVERVIEW

    val scope = rememberCoroutineScope()
    val isBangla = language == CampLanguage.BANGLA

    // Local dialog state for adding/editing entities
    var showEditStationDialog by remember { mutableStateOf(false) }
    var stationToEdit by remember { mutableStateOf<StationEntity?>(null) }

    var showEditScheduleDialog by remember { mutableStateOf(false) }
    var scheduleToEdit by remember { mutableStateOf<ScheduleEventEntity?>(null) }

    var showEditRoomDialog by remember { mutableStateOf(false) }
    var roomToEdit by remember { mutableStateOf<RoomEntity?>(null) }

    var showBulkScheduleUploadDialog by remember { mutableStateOf(false) }
    var showParticipantImportDialog by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedDayFilter by remember { mutableIntStateOf(1) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Header Banner with Role badge & Back button
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onBackToMain != null) {
                            IconButton(
                                onClick = onBackToMain,
                                modifier = Modifier.testTag("admin_back_btn")
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(LscEmeraldLight, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = LscEmeraldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBangla) "অ্যাডমিন কন্ট্রোল সেন্টার" else "Admin Control Center",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = LscSlateDark
                            )
                            Text(
                                text = if (isBangla) "ফায়ারস্টোর ক্লাউড সিঙ্কসহ সকল অপশন কাস্টমাইজ করুন" else "Manage live Firestore camp settings",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF16A34A), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Firestore Live",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Navigation Controller Tab Row
                ScrollableTabRow(
                    selectedTabIndex = when (currentRoute) {
                        AdminRoutes.OVERVIEW -> 0
                        AdminRoutes.STATIONS -> 1
                        AdminRoutes.SCHEDULES -> 2
                        AdminRoutes.ROOMS -> 3
                        AdminRoutes.DEPARTMENTS -> 4
                        AdminRoutes.SUBGROUPS -> 5
                        AdminRoutes.USERS -> 6
                        else -> 0
                    },
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    contentColor = LscEmeraldPrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = currentRoute == AdminRoutes.OVERVIEW,
                        onClick = { navController.navigate(AdminRoutes.OVERVIEW) { popUpTo(AdminRoutes.OVERVIEW) { inclusive = true } } },
                        text = { Text(if (isBangla) "সামগ্রিক চিত্র" else "Overview", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("admin_nav_overview")
                    )
                    Tab(
                        selected = currentRoute == AdminRoutes.STATIONS,
                        onClick = { navController.navigate(AdminRoutes.STATIONS) },
                        text = { Text(if (isBangla) "দক্ষতা স্টেশন (${stations.size})" else "Stations (${stations.size})", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Autorenew, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("admin_nav_stations")
                    )
                    Tab(
                        selected = currentRoute == AdminRoutes.SCHEDULES,
                        onClick = { navController.navigate(AdminRoutes.SCHEDULES) },
                        text = { Text(if (isBangla) "সময়সূচী (${schedules.size})" else "Schedules (${schedules.size})", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("admin_nav_schedules")
                    )
                    Tab(
                        selected = currentRoute == AdminRoutes.ROOMS,
                        onClick = { navController.navigate(AdminRoutes.ROOMS) },
                        text = { Text(if (isBangla) "আবাসন রুম (${rooms.size})" else "Rooms (${rooms.size})", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.MeetingRoom, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("admin_nav_rooms")
                    )
                    Tab(
                        selected = currentRoute == AdminRoutes.DEPARTMENTS,
                        onClick = { navController.navigate(AdminRoutes.DEPARTMENTS) },
                        text = { Text(if (isBangla) "১৩টি বিভাগ ও এসওপি" else "13 Depts & SOPs", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.CorporateFare, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("admin_nav_departments")
                    )
                    Tab(
                        selected = currentRoute == AdminRoutes.SUBGROUPS,
                        onClick = { navController.navigate(AdminRoutes.SUBGROUPS) },
                        text = { Text(if (isBangla) "২৪টি সাব-গ্রুপ" else "24 Subgroups", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("admin_nav_subgroups")
                    )
                    Tab(
                        selected = currentRoute == AdminRoutes.USERS,
                        onClick = { navController.navigate(AdminRoutes.USERS) },
                        text = { Text(if (isBangla) "ইউজার রোলস" else "User Roles", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.ManageAccounts, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("admin_nav_users")
                    )
                    Tab(
                        selected = currentRoute == AdminRoutes.AUDIT_TRAIL,
                        onClick = { navController.navigate(AdminRoutes.AUDIT_TRAIL) },
                        text = { Text(if (isBangla) "অডিট ট্রেইল (${auditLogs.size})" else "Audit Trail (${auditLogs.size})", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("admin_nav_audit_trail")
                    )
                }
            }
        }

        // Search Bar for list routes
        if (currentRoute != AdminRoutes.OVERVIEW) {
            PaddingValues(horizontal = 16.dp, vertical = 8.dp).let {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (isBangla) "তথ্য খুঁজুন..." else "Search records...",
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
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("admin_search_input")
                )
            }
        }

        // NavHost Controller Area
        NavHost(
            navController = navController,
            startDestination = AdminRoutes.OVERVIEW,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // OVERVIEW ROUTE
            composable(AdminRoutes.OVERVIEW) {
                AdminOverviewView(
                    isBangla = isBangla,
                    stationsCount = stations.size,
                    schedulesCount = schedules.size,
                    roomsCount = rooms.size,
                    usersCount = userAccounts.size,
                    onNavigateTo = { route -> navController.navigate(route) },
                    onOpenBulkScheduleUpload = { showBulkScheduleUploadDialog = true },
                    onOpenParticipantImport = { showParticipantImportDialog = true }
                )
            }

            // STATIONS ROUTE
            composable(AdminRoutes.STATIONS) {
                val filteredStations = remember(stations, searchQuery) {
                    if (searchQuery.isBlank()) stations
                    else stations.filter {
                        it.nameBn.contains(searchQuery, ignoreCase = true) ||
                                it.nameEn.contains(searchQuery, ignoreCase = true) ||
                                it.moderatorName.contains(searchQuery, ignoreCase = true) ||
                                it.location.contains(searchQuery, ignoreCase = true)
                    }
                }

                AdminStationsModule(
                    isBangla = isBangla,
                    stations = filteredStations,
                    onAddStation = {
                        stationToEdit = null
                        showEditStationDialog = true
                    },
                    onEditStation = { st ->
                        stationToEdit = st
                        showEditStationDialog = true
                    },
                    onDeleteStation = onDeleteStation
                )
            }

            // SCHEDULES ROUTE
            composable(AdminRoutes.SCHEDULES) {
                val filteredSchedules = remember(schedules, searchQuery, selectedDayFilter) {
                    schedules.filter { ev ->
                        (ev.dayNo == selectedDayFilter) &&
                                (searchQuery.isBlank() ||
                                        ev.titleBn.contains(searchQuery, ignoreCase = true) ||
                                        ev.titleEn.contains(searchQuery, ignoreCase = true) ||
                                        ev.location.contains(searchQuery, ignoreCase = true))
                    }
                }

                AdminSchedulesModule(
                    isBangla = isBangla,
                    schedules = filteredSchedules,
                    selectedDay = selectedDayFilter,
                    onSelectDay = { selectedDayFilter = it },
                    onAddSchedule = {
                        scheduleToEdit = null
                        showEditScheduleDialog = true
                    },
                    onBulkUploadSchedule = {
                        showBulkScheduleUploadDialog = true
                    },
                    onEditSchedule = { ev ->
                        scheduleToEdit = ev
                        showEditScheduleDialog = true
                    },
                    onDeleteSchedule = onDeleteSchedule
                )
            }

            // ROOMS ROUTE
            composable(AdminRoutes.ROOMS) {
                val filteredRooms = remember(rooms, searchQuery) {
                    if (searchQuery.isBlank()) rooms
                    else rooms.filter {
                        it.buildingName.contains(searchQuery, ignoreCase = true) ||
                                it.roomNo.contains(searchQuery, ignoreCase = true) ||
                                it.inChargeName.contains(searchQuery, ignoreCase = true)
                    }
                }

                AdminRoomsModule(
                    isBangla = isBangla,
                    rooms = filteredRooms,
                    onAddRoom = {
                        roomToEdit = null
                        showEditRoomDialog = true
                    },
                    onEditRoom = { rm ->
                        roomToEdit = rm
                        showEditRoomDialog = true
                    },
                    onDeleteRoom = onDeleteRoom
                )
            }

            // DEPARTMENTS & SOPS ROUTE
            composable(AdminRoutes.DEPARTMENTS) {
                DepartmentsSopScreen(language = language)
            }

            // 24 SUBGROUPS DIRECTORY ROUTE
            composable(AdminRoutes.SUBGROUPS) {
                SubgroupsDirectoryScreen(
                    language = language,
                    groups = groups,
                    participants = participants,
                    onSelectParticipant = { onSelectParticipant?.invoke(it) },
                    onBatchSubgroupAttendance = { evId, title, dNo, sg, abs, seal ->
                        onBatchSubgroupAttendance?.invoke(evId, title, dNo, sg, abs, seal)
                    }
                )
            }

            // USERS ROUTE
            composable(AdminRoutes.USERS) {
                val filteredUsers = remember(userAccounts, searchQuery) {
                    if (searchQuery.isBlank()) userAccounts
                    else userAccounts.filter {
                        it.fullName.contains(searchQuery, ignoreCase = true) ||
                                it.email.contains(searchQuery, ignoreCase = true) ||
                                it.role.contains(searchQuery, ignoreCase = true)
                    }
                }

                AdminUsersModule(
                    isBangla = isBangla,
                    users = filteredUsers,
                    onAssignRole = onAssignUserRole
                )
            }

            // AUDIT TRAIL ROUTE
            composable(AdminRoutes.AUDIT_TRAIL) {
                AdminAuditTrailModule(
                    isBangla = isBangla,
                    auditLogs = auditLogs,
                    searchQuery = searchQuery
                )
            }
        }
    }

    // Modal Dialogs for Creating & Editing
    if (showEditStationDialog) {
        EditStationDialog(
            station = stationToEdit,
            isBangla = isBangla,
            onDismiss = {
                showEditStationDialog = false
                stationToEdit = null
            },
            onSave = { st ->
                scope.launch {
                    if (stationToEdit == null) {
                        FirestoreManager.saveStation(st)
                        onAddStation(st)
                    } else {
                        FirestoreManager.saveStation(st)
                        onUpdateStation(st)
                    }
                }
                showEditStationDialog = false
                stationToEdit = null
            }
        )
    }

    if (showEditScheduleDialog) {
        EditScheduleEventDialog(
            event = scheduleToEdit,
            defaultDayNo = selectedDayFilter,
            isBangla = isBangla,
            onDismiss = {
                showEditScheduleDialog = false
                scheduleToEdit = null
            },
            onSave = { ev ->
                scope.launch {
                    if (scheduleToEdit == null) {
                        FirestoreManager.saveScheduleEvent(ev)
                        onAddSchedule(ev)
                    } else {
                        FirestoreManager.saveScheduleEvent(ev)
                        onUpdateSchedule(ev)
                    }
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
            isBangla = isBangla,
            onDismiss = {
                showEditRoomDialog = false
                roomToEdit = null
            },
            onSave = { rm ->
                scope.launch {
                    if (roomToEdit == null) {
                        FirestoreManager.saveRoom(rm)
                        onAddRoom(rm)
                    } else {
                        FirestoreManager.saveRoom(rm)
                        onUpdateRoom(rm)
                    }
                }
                showEditRoomDialog = false
                roomToEdit = null
            }
        )
    }

    if (showBulkScheduleUploadDialog) {
        BulkScheduleUploadDialog(
            language = language,
            onDismiss = { showBulkScheduleUploadDialog = false },
            onConfirmUpload = { events ->
                if (onBulkUploadSchedules != null) {
                    onBulkUploadSchedules(events)
                } else {
                    scope.launch {
                        FirestoreManager.bulkSaveSchedules(events)
                        events.forEach { onAddSchedule(it) }
                    }
                }
            }
        )
    }

    if (showParticipantImportDialog) {
        ParticipantRosterImportDialog(
            language = language,
            onDismiss = { showParticipantImportDialog = false },
            onConfirmImport = { rawJson ->
                if (onImportParticipantsCsvJson != null) {
                    onImportParticipantsCsvJson(rawJson)
                } else {
                    scope.launch {
                        com.example.data.service.ParticipantImportService.importRosterToFirestore(rawJson)
                    }
                }
            }
        )
    }
}

/**
 * Overview summary view card layout.
 */
@Composable
private fun AdminOverviewView(
    isBangla: Boolean,
    stationsCount: Int,
    schedulesCount: Int,
    roomsCount: Int,
    usersCount: Int,
    onNavigateTo: (String) -> Unit,
    onOpenBulkScheduleUpload: () -> Unit = {},
    onOpenParticipantImport: () -> Unit = {}
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LscEmeraldPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isBangla) "স্বাগতম অ্যাডমিন ড্যাশবোর্ডে" else "Welcome to Admin Center",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBangla)
                            "এখান থেকে আপনি স্টেশনের স্থান, মডারেটর, দৈনিক রুটিন, রুম বরাদ্দ এবং স্টাফ রোল সরাসরি ক্লাউডে এডিট করতে পারবেন।"
                        else
                            "Real-time control over camp stations, routine schedule, rooming beds, and user permissions.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Firestore Cloud Bulk Utilities Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFEFF6FF), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = Color(0xFF1D4ED8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isBangla) "ফায়ারস্টোর বাল্ক ইউটিলিটিজ" else "Firestore Bulk Utilities",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = LscSlateDark
                                )
                                Text(
                                    text = if (isBangla)
                                        "সময়সূচী ও অংশগ্রহণকারী রোস্টার এক ক্লিকে সিঙ্ক করুন"
                                    else
                                        "Batch upload schedules and roster to cloud",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Firestore Live",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onOpenBulkScheduleUpload,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("overview_bulk_schedule_upload_btn")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBangla) "শিডিউল আপলোড" else "Upload Schedules",
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = onOpenParticipantImport,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("overview_import_participants_btn")
                        ) {
                            Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBangla) "রোস্টার ইম্পোর্ট" else "Import Roster",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = if (isBangla) "দ্রুত মডিউল কাস্টমাইজেশন" else "Quick Module Shortcuts",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = LscSlateDark,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricCard(
                    title = if (isBangla) "দক্ষতা স্টেশন" else "Skill Stations",
                    count = "$stationsCount টি",
                    subtitle = if (isBangla) "লোকেশন ও মডারেটর" else "Locations & Mods",
                    icon = Icons.Default.Autorenew,
                    containerColor = Color(0xFFEFF6FF),
                    contentColor = Color(0xFF1D4ED8),
                    onClick = { onNavigateTo(AdminRoutes.STATIONS) },
                    modifier = Modifier.weight(1f)
                )

                AdminMetricCard(
                    title = if (isBangla) "সময়সূচী ইভেন্ট" else "Schedule Events",
                    count = "$schedulesCount টি",
                    subtitle = if (isBangla) "৫ দিনের ইভেন্ট রুটিন" else "5-Day Routine",
                    icon = Icons.Default.Event,
                    containerColor = Color(0xFFECFDF5),
                    contentColor = Color(0xFF047857),
                    onClick = { onNavigateTo(AdminRoutes.SCHEDULES) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricCard(
                    title = if (isBangla) "আবাসন রুম" else "Room Allocations",
                    count = "$roomsCount টি",
                    subtitle = if (isBangla) "সিট ও ইনচার্জ" else "Beds & In-charge",
                    icon = Icons.Default.MeetingRoom,
                    containerColor = Color(0xFFFEF3C7),
                    contentColor = Color(0xFFB45309),
                    onClick = { onNavigateTo(AdminRoutes.ROOMS) },
                    modifier = Modifier.weight(1f)
                )

                AdminMetricCard(
                    title = if (isBangla) "ইউজার রোলস" else "User Roles",
                    count = "$usersCount জন",
                    subtitle = if (isBangla) "এক্সেস ও পারমিশন" else "Staff Access",
                    icon = Icons.Default.ManageAccounts,
                    containerColor = Color(0xFFF3E8FF),
                    contentColor = Color(0xFF6B21A8),
                    onClick = { onNavigateTo(AdminRoutes.USERS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricCard(
                    title = if (isBangla) "১৩টি বিভাগ ও এসওপি" else "13 Depts & SOPs",
                    count = "১৩টি বিভাগ",
                    subtitle = if (isBangla) "কার্যপদ্ধতি ও চেকলিস্ট" else "SOPs & Checklists",
                    icon = Icons.Default.CorporateFare,
                    containerColor = Color(0xFFDCFCE7),
                    contentColor = Color(0xFF15803D),
                    onClick = { onNavigateTo(AdminRoutes.DEPARTMENTS) },
                    modifier = Modifier.weight(1f)
                )

                AdminMetricCard(
                    title = if (isBangla) "২৪টি সাব-গ্রুপ" else "24 Subgroups",
                    count = "২৪টি সাব-গ্রুপ",
                    subtitle = if (isBangla) "মেন্টর ও ইন-চার্জ" else "Mentors Directory",
                    icon = Icons.Default.Groups,
                    containerColor = Color(0xFFE0E7FF),
                    contentColor = Color(0xFF4338CA),
                    onClick = { onNavigateTo(AdminRoutes.SUBGROUPS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    count: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = contentColor)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LscSlateDark)
            Text(count, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = contentColor)
            Text(subtitle, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun AdminStationsModule(
    isBangla: Boolean,
    stations: List<StationEntity>,
    onAddStation: () -> Unit,
    onEditStation: (StationEntity) -> Unit,
    onDeleteStation: (StationEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onAddStation,
            colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .testTag("admin_add_station_btn")
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isBangla) "নতুন দক্ষতা স্টেশন তৈরি করুন" else "Add New Station")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(stations, key = { it.id }) { st ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(st.nameBn, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LscSlateDark)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("মডারেটর: ${st.moderatorName}", fontSize = 11.sp, color = Color.Gray)
                            Text("স্থান: ${st.location}", fontSize = 11.sp, color = LscEmeraldPrimary, fontWeight = FontWeight.Medium)
                            if (st.objectiveBn.isNotBlank()) {
                                Text("উদ্দেশ্য: ${st.objectiveBn}", fontSize = 10.sp, color = Color.DarkGray, maxLines = 1)
                            }
                        }
                        Row {
                            IconButton(onClick = { onEditStation(st) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = LscEmeraldPrimary)
                            }
                            IconButton(onClick = { onDeleteStation(st) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminSchedulesModule(
    isBangla: Boolean,
    schedules: List<ScheduleEventEntity>,
    selectedDay: Int,
    onSelectDay: (Int) -> Unit,
    onAddSchedule: () -> Unit,
    onBulkUploadSchedule: () -> Unit = {},
    onEditSchedule: (ScheduleEventEntity) -> Unit,
    onDeleteSchedule: (ScheduleEventEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            (1..5).forEach { day ->
                FilterChip(
                    selected = selectedDay == day,
                    onClick = { onSelectDay(day) },
                    label = { Text(if (isBangla) "দিন $day" else "Day $day", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LscEmeraldPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onAddSchedule,
                colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("admin_add_schedule_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isBangla) "নতুন যোগ করুন" else "Add Event", fontSize = 12.sp)
            }

            Button(
                onClick = onBulkUploadSchedule,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1.3f)
                    .testTag("admin_bulk_upload_schedule_btn")
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isBangla) "বাল্ক শিডিউল আপলোড" else "Bulk Upload", fontSize = 12.sp)
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(schedules, key = { it.id }) { ev ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = Color(0xFFECFDF5),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = ev.timeSlot,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LscEmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(ev.titleBn, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LscSlateDark)
                            Text("স্থান: ${ev.location}", fontSize = 11.sp, color = Color.Gray)
                        }
                        Row {
                            IconButton(onClick = { onEditSchedule(ev) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = LscEmeraldPrimary)
                            }
                            IconButton(onClick = { onDeleteSchedule(ev) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminRoomsModule(
    isBangla: Boolean,
    rooms: List<RoomEntity>,
    onAddRoom: () -> Unit,
    onEditRoom: (RoomEntity) -> Unit,
    onDeleteRoom: (RoomEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onAddRoom,
            colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .testTag("admin_add_room_btn")
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isBangla) "নতুন আবাসন রুম যোগ করুন" else "Add New Room")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(rooms, key = { it.id }) { rm ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${rm.buildingName} - রুম ${rm.roomNo}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LscSlateDark)
                            Text("ধারনক্ষমতা: ${rm.capacity} সিট | তলা: ${rm.floor}", fontSize = 11.sp, color = Color.Gray)
                            Text("ইনচার্জ: ${rm.inChargeName} (${rm.inChargePhone})", fontSize = 11.sp, color = LscEmeraldPrimary)
                        }
                        Row {
                            IconButton(onClick = { onEditRoom(rm) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = LscEmeraldPrimary)
                            }
                            IconButton(onClick = { onDeleteRoom(rm) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminUsersModule(
    isBangla: Boolean,
    users: List<UserAccountEntity>,
    onAssignRole: (String, UserRole) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(users, key = { it.email }) { usr ->
            var expandedRolePicker by remember { mutableStateOf(false) }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(LscEmeraldLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = LscEmeraldPrimary)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(usr.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LscSlateDark)
                                Text(usr.email, fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.clickable { expandedRolePicker = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(usr.role, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF1D4ED8))
                            }
                        }

                        DropdownMenu(
                            expanded = expandedRolePicker,
                            onDismissRequest = { expandedRolePicker = false }
                        ) {
                            UserRole.values().forEach { role ->
                                DropdownMenuItem(
                                    text = { Text(role.titleEn) },
                                    onClick = {
                                        onAssignRole(usr.email, role)
                                        expandedRolePicker = false
                                    }
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
fun AdminAuditTrailModule(
    isBangla: Boolean,
    auditLogs: List<AuditLogEntity>,
    searchQuery: String
) {
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val filteredLogs = remember(auditLogs, searchQuery, selectedCategoryFilter) {
        auditLogs.filter { log ->
            val matchesCategory = when (selectedCategoryFilter) {
                "ROLE" -> log.actionType.contains("ROLE", ignoreCase = true) || log.targetEntity.contains("Role", ignoreCase = true)
                "ROOM" -> log.actionType.contains("ROOM", ignoreCase = true) || log.targetEntity.contains("Room", ignoreCase = true)
                "MEAL" -> log.actionType.contains("MEAL", ignoreCase = true) || log.actionType.contains("SCHEDULE", ignoreCase = true) || log.targetEntity.contains("Schedule", ignoreCase = true)
                "STATION" -> log.actionType.contains("STATION", ignoreCase = true) || log.targetEntity.contains("Station", ignoreCase = true)
                "IMPORT" -> log.actionType.contains("IMPORT", ignoreCase = true) || log.actionType.contains("UPLOAD", ignoreCase = true)
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                    log.userEmail.contains(searchQuery, ignoreCase = true) ||
                    log.adminId.contains(searchQuery, ignoreCase = true) ||
                    log.actionType.contains(searchQuery, ignoreCase = true) ||
                    log.targetEntity.contains(searchQuery, ignoreCase = true) ||
                    log.description.contains(searchQuery, ignoreCase = true) ||
                    log.modifiedRecordDetails.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }

    val dateFormat = remember { java.text.SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", java.util.Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Banner with Firestore Live Collection Indicator
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = LscEmeraldPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = LscEmeraldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBangla) "অ্যাডমিন অডিট ট্রেইল (Audit Trail)" else "Admin Audit Trail & Logs",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isBangla)
                                    "ইউজার রোল, রুম বরাদ্দ ও শিডিউল পরিবর্তনের ফায়ারস্টোর হিস্ট্রি"
                                else
                                    "Real-time records of role updates, room assignments & schedule edits",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Firestore: audit_trail",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Category Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                "ALL" to if (isBangla) "সকল (${auditLogs.size})" else "All (${auditLogs.size})",
                "ROLE" to if (isBangla) "ইউজার রোলস" else "Roles",
                "ROOM" to if (isBangla) "রুম বরাদ্দ" else "Rooms",
                "MEAL" to if (isBangla) "খাবার ও শিডিউল" else "Meals & Schedule",
                "STATION" to if (isBangla) "দক্ষতা স্টেশন" else "Stations",
                "IMPORT" to if (isBangla) "বাল্ক ইম্পোর্ট" else "Imports"
            )
            items(filters) { (catKey, catLabel) ->
                FilterChip(
                    selected = selectedCategoryFilter == catKey,
                    onClick = { selectedCategoryFilter = catKey },
                    label = { Text(catLabel, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LscEmeraldPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isBangla) "কোনো অডিট লগ রেকর্ড পাওয়া যায়নি" else "No audit log records found",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("audit_logs_list")
            ) {
                items(filteredLogs, key = { it.id }) { log ->
                    val (badgeBg, badgeFg) = when {
                        log.actionType.contains("ROLE", ignoreCase = true) -> Color(0xFFF3E8FF) to Color(0xFF6B21A8)
                        log.actionType.contains("ROOM", ignoreCase = true) -> Color(0xFFE0F2FE) to Color(0xFF0369A1)
                        log.actionType.contains("MEAL", ignoreCase = true) || log.actionType.contains("SCHEDULE", ignoreCase = true) -> Color(0xFFFEF3C7) to Color(0xFF92400E)
                        log.actionType.contains("STATION", ignoreCase = true) -> Color(0xFFDCFCE7) to Color(0xFF15803D)
                        log.actionType.contains("IMPORT", ignoreCase = true) || log.actionType.contains("UPLOAD", ignoreCase = true) -> Color(0xFFEDE9FE) to Color(0xFF5B21B6)
                        else -> Color(0xFFF1F5F9) to Color(0xFF334155)
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("audit_log_item_${log.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Header Row: Action badge, Target Entity, Timestamp
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        color = badgeBg,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = log.actionType,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = badgeFg,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Surface(
                                        color = Color(0xFFF8FAFC),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = log.targetEntity,
                                            fontSize = 10.sp,
                                            color = Color.DarkGray,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = dateFormat.format(java.util.Date(log.timestamp)),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Human Readable Description
                            Text(
                                text = log.description,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Admin ID & User Email Row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (log.adminId.isNotBlank()) {
                                    Surface(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Badge,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Admin: ${log.adminId}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = log.userEmail,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Modified Record Details Section (if present)
                            if (log.modifiedRecordDetails.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFFF8FAFC),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.DataObject,
                                                contentDescription = null,
                                                tint = LscEmeraldPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isBangla)
                                                    "সংশোধিত রেকর্ডের বিবরণ (Modified Details):"
                                                else
                                                    "Modified Record Details:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = LscSlateDark
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = log.modifiedRecordDetails,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFF334155),
                                            modifier = Modifier.fillMaxWidth()
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
}
