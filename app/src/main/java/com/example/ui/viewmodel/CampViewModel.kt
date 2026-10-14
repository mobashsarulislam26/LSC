package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.CampRepository
import com.example.util.NetworkConnectivityMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScanResult {
    data class Success(val title: String, val message: String, val participant: ParticipantEntity) : ScanResult()
    data class Warning(val title: String, val message: String) : ScanResult()
    data class Error(val title: String, val message: String) : ScanResult()
}

class CampViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CampRepository.getInstance(application)

    // Network Connectivity Monitor
    val connectivityMonitor = NetworkConnectivityMonitor(application, viewModelScope)
    val isNetworkAvailable: StateFlow<Boolean> = connectivityMonitor.isNetworkAvailable

    // Language & Role State
    private val _language = MutableStateFlow(CampLanguage.BANGLA)
    val language: StateFlow<CampLanguage> = _language.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.CAMP_CHIEF)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Low Connectivity Mode & Sync State
    private val _isLowConnectivityMode = MutableStateFlow(false)
    val isLowConnectivityMode: StateFlow<Boolean> = _isLowConnectivityMode.asStateFlow()

    // Effective offline state: either user forced low-connectivity mode or device has no network
    val isOffline: StateFlow<Boolean> = combine(_isLowConnectivityMode, isNetworkAvailable) { lowConn, online ->
        lowConn || !online
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun isOfflineModeActive(): Boolean = _isLowConnectivityMode.value || !isNetworkAvailable.value

    private val _isAutoSyncEnabled = MutableStateFlow(true)
    val isAutoSyncEnabled: StateFlow<Boolean> = _isAutoSyncEnabled.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime.asStateFlow()

    val cachedSyncItems = repository.cachedSyncItems.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val pendingSyncCount = repository.pendingCachedSyncCount.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // Filters & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGroupFilter = MutableStateFlow<String?>(null)
    val selectedGroupFilter: StateFlow<String?> = _selectedGroupFilter.asStateFlow()

    private val _selectedDay = MutableStateFlow(1)
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    // Detail & Dialog States
    private val _selectedParticipant = MutableStateFlow<ParticipantEntity?>(null)
    val selectedParticipant: StateFlow<ParticipantEntity?> = _selectedParticipant.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val selectedParticipantHealthStatus: StateFlow<ParticipantHealthStatusEntity?> = _selectedParticipant
        .flatMapLatest { participant ->
            if (participant != null) {
                repository.observeParticipantHealthStatus(participant.id)
            } else {
                flowOf(null)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _scanFeedback = MutableStateFlow<ScanResult?>(null)
    val scanFeedback: StateFlow<ScanResult?> = _scanFeedback.asStateFlow()

    private val _statusNotification = MutableStateFlow<String?>(null)
    val statusNotification: StateFlow<String?> = _statusNotification.asStateFlow()

    // Admin Access Protection (লগইন পাসওয়ার্ড এর মাধ্যমে এডিট সুবিধা)
    private val _isAdminUnlocked = MutableStateFlow(false)
    val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

    // Only a salted hash of the admin passcode is kept in memory, never the plaintext.
    private val _adminPasswordHash = MutableStateFlow(hashPasscode(DEFAULT_ADMIN_PASSCODE, ADMIN_PASSCODE_SALT))

    fun unlockAdmin(pass: String): Boolean {
        val candidate = hashPasscode(pass.trim(), ADMIN_PASSCODE_SALT)
        val isValid = constantTimeEquals(candidate, _adminPasswordHash.value)
        if (isValid) {
            _isAdminUnlocked.value = true
            _statusNotification.value = "অ্যাডমিন এডিট মোড আনলক সফল হয়েছে!"
        }
        return isValid
    }

    fun lockAdmin() {
        _isAdminUnlocked.value = false
        _statusNotification.value = "অ্যাডমিন মোড লক করা হয়েছে।"
    }

    fun setAdminPassword(newPass: String) {
        if (newPass.isNotBlank()) {
            _adminPasswordHash.value = hashPasscode(newPass.trim(), ADMIN_PASSCODE_SALT)
            _statusNotification.value = "অ্যাডমিন পাসওয়ার্ড সফলভাবে আপডেট হয়েছে।"
        }
    }

    private companion object {
        const val ADMIN_PASSCODE_SALT = "lsc-camp-companion-v1"
        const val DEFAULT_ADMIN_PASSCODE = "Lsc@Admin2026"

        fun hashPasscode(passcode: String, salt: String): String {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            return digest.digest((salt + passcode).toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it) }
        }

        fun constantTimeEquals(a: String, b: String): Boolean {
            if (a.length != b.length) return false
            var result = 0
            for (i in a.indices) result = result or (a[i].code xor b[i].code)
            return result == 0
        }
    }

    // Repository Flows
    val allParticipants = repository.participants.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val participantCount = repository.participantCount.stateIn(viewModelScope, SharingStarted.Lazily, 0)
    val groups = repository.groups.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val stations = repository.stations.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val scheduleEvents = repository.schedule.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val attendanceEvents = repository.attendanceEvents.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val competitions = repository.competitions.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val competitionScores = repository.competitionScores.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val judges = repository.judges.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val healthRecords = repository.healthRecords.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val inventoryItems = repository.inventoryItems.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val bulletins = repository.bulletins.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val incidents = repository.incidents.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val volunteers = repository.volunteers.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val dutyOfficers = repository.dutyOfficers.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val healthStatuses = repository.healthStatuses.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val attendanceRecords = repository.attendanceRecords.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val rooms = repository.rooms.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val mealDistributions = repository.mealDistributions.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Active quick alerts (unresolved incidents: OPEN or IN_PROGRESS)
    val activeQuickAlerts: StateFlow<List<IncidentEntity>> = repository.incidents
        .map { list -> list.filter { it.status != "RESOLVED" } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // High-priority push notification broadcast for immediate in-app banner alert
    private val _latestPushAlert = MutableStateFlow<IncidentEntity?>(null)
    val latestPushAlert: StateFlow<IncidentEntity?> = _latestPushAlert.asStateFlow()

    // Real-time Pending High-Priority Health Log Entries
    val pendingHighPriorityHealthAlerts: StateFlow<List<HealthRecordEntity>> = repository.healthRecords
        .map { records ->
            records.filter { record ->
                (record.isEmergencyFlag || record.priority == "HIGH" || record.checkupType == "EMERGENCY") &&
                record.status != "RESOLVED"
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Combined search and group-filtered participants
    val participants = combine(
        repository.participants,
        _searchQuery,
        _selectedGroupFilter
    ) { all, query, groupFilter ->
        val cleanQuery = query.trim()
        all.filter { p ->
            val matchesQuery = cleanQuery.isBlank() ||
                    p.nameBn.contains(cleanQuery, ignoreCase = true) ||
                    p.nameEn.contains(cleanQuery, ignoreCase = true) ||
                    p.campCode.contains(cleanQuery, ignoreCase = true) ||
                    p.fileNo.contains(cleanQuery, ignoreCase = true) ||
                    p.id.toString().contains(cleanQuery) ||
                    p.districtBn.contains(cleanQuery, ignoreCase = true) ||
                    p.districtEn.contains(cleanQuery, ignoreCase = true) ||
                    p.subgroup.contains(cleanQuery, ignoreCase = true)

            val matchesGroup = groupFilter == null || p.groupId == groupFilter
            matchesQuery && matchesGroup
        }
    }.flowOn(Dispatchers.Default)
    .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.ensureSeeded()
        }
        // Auto-sync listener: automatically syncs when network is restored and low-connectivity mode is inactive
        viewModelScope.launch {
            isNetworkAvailable.collect { online ->
                if (online && !_isLowConnectivityMode.value && _isAutoSyncEnabled.value) {
                    val count = repository.getPendingCachedSyncCount()
                    if (count > 0) {
                        triggerSync(isAutomatic = true)
                    }
                }
            }
        }
    }

    // Role & Language Actions
    fun setLanguage(lang: CampLanguage) {
        _language.value = lang
    }

    fun setRole(role: UserRole) {
        _currentRole.value = role
        // If Group Leader or Mentor, optionally auto-filter group
        if (role == UserRole.GROUP_LEADER) {
            _selectedGroupFilter.value = "bukhari"
        }
    }

    fun toggleOfflineMode() {
        setLowConnectivityMode(!_isLowConnectivityMode.value)
    }

    fun setLowConnectivityMode(enabled: Boolean) {
        _isLowConnectivityMode.value = enabled
        if (enabled) {
            _statusNotification.value = "কম কানেক্টিভিটি মোড সক্রিয় (সকল নতুন ডেটা লোকাল ক্যাশে জমা হবে)"
        } else {
            _statusNotification.value = "অনলাইন মোড সক্রিয় (স্বয়ংক্রিয় সিঙ্ক চালু)"
            if (isNetworkAvailable.value && _isAutoSyncEnabled.value) {
                viewModelScope.launch {
                    val count = repository.getPendingCachedSyncCount()
                    if (count > 0) {
                        triggerSync(isAutomatic = true)
                    }
                }
            }
        }
    }

    fun setAutoSyncEnabled(enabled: Boolean) {
        _isAutoSyncEnabled.value = enabled
    }

    fun simulateNetworkAvailability(available: Boolean) {
        connectivityMonitor.simulateNetworkAvailability(available)
        val stateText = if (available) "ইন্টারনেট সংযোগ শনাক্ত হয়েছে (Online detected)" else "ইন্টারনেট সংযোগ বিচ্ছিন্ন (Disconnected)"
        _statusNotification.value = "নেটওয়ার্ক সিমুলেশন: $stateText"
    }

    fun resetNetworkSimulation() {
        connectivityMonitor.resetSimulation()
        _statusNotification.value = "প্রকৃত নেটওয়ার্ক মোডে ফিরে আসা হয়েছে"
    }

    fun clearSyncedHistory() {
        viewModelScope.launch {
            val count = repository.clearSyncedCacheHistory()
            _statusNotification.value = "সিঙ্ক ইতিহাস পরিষ্কার করা হয়েছে ($count টি রেকর্ড)"
        }
    }

    fun triggerSync(isAutomatic: Boolean = false) {
        if (_isSyncing.value) return
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                kotlinx.coroutines.delay(400) // Realistic brief delay for visual synchronization indicator
                val result = repository.syncPendingCachedItems()
                _lastSyncTime.value = System.currentTimeMillis()
                if (result.totalSynced > 0) {
                    val prefix = if (isAutomatic) "স্বয়ংক্রিয় সিঙ্ক (Auto-synced): " else "সিঙ্ক সম্পন্ন: "
                    _statusNotification.value = "$prefix${result.message}"
                } else if (!isAutomatic) {
                    _statusNotification.value = "সকল ডেটা ক্লাউডের সাথে আপ-টু-ডেট রয়েছে।"
                }
            } catch (e: Exception) {
                _statusNotification.value = "সিঙ্ক ব্যর্থ হয়েছে: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setGroupFilter(groupId: String?) {
        _selectedGroupFilter.value = groupId
    }

    fun setSelectedDay(day: Int) {
        _selectedDay.value = day
    }

    fun selectParticipant(participant: ParticipantEntity?) {
        _selectedParticipant.value = participant
    }

    fun addParticipant(participant: ParticipantEntity) {
        viewModelScope.launch {
            repository.insertParticipant(participant)
            _statusNotification.value = "নতুন অংশগ্রহণকারী যোগ করা হয়েছে: ${participant.nameBn}"
        }
    }

    fun updateParticipant(participant: ParticipantEntity) {
        viewModelScope.launch {
            repository.updateParticipant(participant)
            if (_selectedParticipant.value?.id == participant.id) {
                _selectedParticipant.value = participant
            }
            _statusNotification.value = "তথ্য আপডেট করা হয়েছে: ${participant.nameBn}"
        }
    }

    fun deleteParticipant(participant: ParticipantEntity) {
        viewModelScope.launch {
            repository.deleteParticipant(participant)
            if (_selectedParticipant.value?.id == participant.id) {
                _selectedParticipant.value = null
            }
            _statusNotification.value = "অংশগ্রহণকারী মুছে ফেলা হয়েছে: ${participant.nameBn}"
        }
    }

    fun clearScanFeedback() {
        _scanFeedback.value = null
    }

    fun clearStatusNotification() {
        _statusNotification.value = null
    }

    // Universal QR Scan Simulation / Handling
    fun processUniversalScan(campCode: String, mode: String, extraArg: String = "") {
        viewModelScope.launch {
            val participant = repository.getParticipantByCampCode(campCode)
            if (participant == null) {
                _scanFeedback.value = ScanResult.Error(
                    title = "অপরিচিত কিউআর কোড (Invalid QR)",
                    message = "ক্যাম্প আইডি $campCode ডেটাবেজে পাওয়া যায়নি।"
                )
                return@launch
            }

            when (mode) {
                "CHECK_IN" -> {
                    _scanFeedback.value = ScanResult.Success(
                        title = "ক্যাম্পে উপস্থিতি গৃহীত (Camp Checked-in)",
                        message = "${participant.nameBn} (${participant.groupNameBn} - ${participant.subgroup})\nরুম: ${participant.roomNo}",
                        participant = participant
                    )
                }
                "MEAL" -> {
                    val (success, msg) = repository.claimMeal(_selectedDay.value, extraArg.ifEmpty { "LUNCH" }, participant)
                    if (success) {
                        _scanFeedback.value = ScanResult.Success(
                            title = "খাবার বিতরণ সম্পন্ন (Meal Claimed)",
                            message = "${participant.nameBn} - $msg\nখাবারের ধরন: ${if (participant.mealPref != "নিয়মিত") "সতর্কতা: " + participant.mealPref else "নরমাল মেনু"}",
                            participant = participant
                        )
                    } else {
                        _scanFeedback.value = ScanResult.Warning(
                            title = "দ্বৈত দাবি রোধ (Duplicate Meal Blocked)",
                            message = "${participant.nameBn} ইতিমধ্যে এই বেলার খাবার গ্রহণ করেছেন!"
                        )
                    }
                }
                "ATTENDANCE" -> {
                    val eventId = extraArg.toLongOrNull() ?: 2L
                    val offline = isOffline.value
                    repository.recordAttendance(eventId, participant, "PRESENT", "QR_SCAN", isOfflineMode = offline)
                    val offlineNote = if (offline) " [📦 অফলাইন ক্যাশে সংরক্ষিত]" else ""
                    _scanFeedback.value = ScanResult.Success(
                        title = if (offline) "হাজিরা গৃহীত (অফলাইন ক্যাশ)" else "হাজিরা গৃহীত (Attendance Marked)",
                        message = "${participant.nameBn} - সময়মতো উপস্থিত হয়েছে।$offlineNote\nগ্রুপ: ${participant.groupNameBn}",
                        participant = participant
                    )
                }
                "STATION" -> {
                    val stationName = extraArg.ifEmpty { "স্টেশন কার্যক্রম" }
                    val offline = isOffline.value
                    repository.recordAttendance(4L, participant, "PRESENT", "QR_SCAN", "Station: $stationName", isOfflineMode = offline)
                    val offlineNote = if (offline) "\n[📦 কম কানেক্টিভিটি মোড: লোকাল ক্যাশে সংরক্ষিত]" else ""
                    _scanFeedback.value = ScanResult.Success(
                        title = if (offline) "স্টেশন হাজিরা গৃহীত (অফলাইন ক্যাশ)" else "স্টেশন হাজিরা গৃহীত (Station Attendance)",
                        message = "${participant.nameBn} (${participant.campCode})\nস্টেশন হাজিরা সফলভাবে লিপিবদ্ধ হয়েছে।$offlineNote\nগ্রুপ: ${participant.groupNameBn} (${participant.subgroup})",
                        participant = participant
                    )
                }
                "AWARD" -> {
                    _scanFeedback.value = ScanResult.Success(
                        title = "পুরস্কার গ্রহীতা যাচাই (Award Verified)",
                        message = "${participant.nameBn} ($campCode)\nপুরস্কার ইস্যু করার জন্য অনুমোদিত।",
                        participant = participant
                    )
                }
                else -> {
                    _scanFeedback.value = ScanResult.Success(
                        title = "আইডি কার্ড ভেরিফাইড (ID Verified)",
                        message = "${participant.nameBn}\n${participant.groupNameBn} | ${participant.districtBn}",
                        participant = participant
                    )
                }
            }
        }
    }

    fun markAttendanceDirect(eventId: Long, participant: ParticipantEntity, status: String, reason: String? = null) {
        viewModelScope.launch {
            val offline = isOffline.value
            repository.recordAttendance(eventId, participant, status, "ROSTER_TAP", reason, isOfflineMode = offline)
            if (offline) {
                _statusNotification.value = "${participant.nameBn}: $status হাজিরা অফলাইনে ক্যাশ করা হয়েছে (স্বয়ংক্রিয় সিঙ্ক হবে)।"
            } else {
                _statusNotification.value = "${participant.nameBn}: $status হাজিরা রেকর্ড করা হয়েছে।"
            }
        }
    }

    fun submitJudgeScore(
        competitionId: Long,
        participant: ParticipantEntity,
        accuracy: Double,
        presentation: Double,
        discipline: Double,
        remarks: String
    ) {
        viewModelScope.launch {
            val total = accuracy + presentation + discipline
            val score = CompetitionScoreEntity(
                competitionId = competitionId,
                participantId = participant.id,
                campCode = participant.campCode,
                participantName = participant.nameBn,
                groupName = participant.groupNameBn,
                subgroup = participant.subgroup,
                accuracyScore = accuracy,
                presentationScore = presentation,
                disciplineScore = discipline,
                totalScore = total,
                remarks = remarks,
                rank = if (total >= 90) 1 else if (total >= 80) 2 else 3,
                isAwardEligible = total >= 80
            )
            repository.submitCompetitionScore(score)
            _statusNotification.value = "স্কোর সফলভাবে সংরক্ষিত: $total/100 (${participant.nameBn})"
        }
    }

    fun addJudge(
        nameBn: String,
        nameEn: String = "",
        designation: String,
        assignedCategory: String,
        phone: String = "",
        notes: String = ""
    ) {
        viewModelScope.launch {
            val judge = JudgeEntity(
                nameBn = nameBn.trim(),
                nameEn = nameEn.trim(),
                designation = designation.trim(),
                assignedCategory = assignedCategory.trim(),
                phone = phone.trim(),
                notes = notes.trim(),
                isActive = true
            )
            repository.addJudge(judge)
            _statusNotification.value = "নতুন বিচারক যুক্ত করা হয়েছে: $nameBn"
        }
    }

    fun updateJudge(judge: JudgeEntity) {
        viewModelScope.launch {
            repository.updateJudge(judge)
            _statusNotification.value = "বিচারকের তথ্য আপডেট করা হয়েছে: ${judge.nameBn}"
        }
    }

    fun deleteJudge(judge: JudgeEntity) {
        viewModelScope.launch {
            repository.deleteJudge(judge)
            _statusNotification.value = "বিচারক সরানো হয়েছে: ${judge.nameBn}"
        }
    }

    fun updateScheduleEvent(event: ScheduleEventEntity) {
        viewModelScope.launch {
            repository.updateScheduleEvent(event)
            _statusNotification.value = "সময়সূচি ও সেশন আপডেট সম্পন্ন: ${event.titleBn}"
        }
    }

    fun updateGroup(group: CampGroupEntity) {
        viewModelScope.launch {
            repository.updateGroup(group)
            _statusNotification.value = "গ্রুপ ও মেন্টর তথ্য আপডেট হয়েছে: ${group.nameBn}"
        }
    }

    fun publishBulletin(
        titleBn: String,
        titleEn: String = "",
        summaryBn: String,
        summaryEn: String = "",
        author: String = "ক্যাম্প প্রশাসন",
        groupSpotlight: String = "ক্যাম্পের সার্বিক কার্যক্রম",
        dayNo: Int = 1,
        dateStr: String = ""
    ) {
        viewModelScope.launch {
            val date = if (dateStr.isNotBlank()) dateStr else "দিন $dayNo • সরাসরি বিজ্ঞপ্তি"
            val bulletin = BulletinEntity(
                dayNo = dayNo,
                dateStr = date,
                titleBn = titleBn.trim(),
                titleEn = if (titleEn.isNotBlank()) titleEn.trim() else titleBn.trim(),
                summaryBn = summaryBn.trim(),
                summaryEn = summaryEn.trim(),
                author = author.trim(),
                groupSpotlight = groupSpotlight.trim(),
                likesCount = 0,
                isPublished = true
            )
            repository.publishBulletin(bulletin)
            _statusNotification.value = "নতুন বুলেটিন সফলভাবে প্রকাশ করা হয়েছে!"
        }
    }

    fun batchImportJudges(content: String): Int {
        val lines = content.lines().filter { it.isNotBlank() }
        val newJudges = mutableListOf<JudgeEntity>()
        for (line in lines) {
            val tokens = splitCsvOrTsv(line)
            if (tokens.isEmpty()) continue
            // Skip header if present
            if (tokens[0].contains("নাম") || tokens[0].equals("name", ignoreCase = true)) continue
            val nameBn = tokens.getOrNull(0) ?: continue
            val designation = tokens.getOrNull(1) ?: "সম্মানিত বিচারক"
            val category = tokens.getOrNull(2) ?: "সাধারণ"
            val phone = tokens.getOrNull(3) ?: ""
            val notes = tokens.getOrNull(4) ?: ""
            newJudges.add(
                JudgeEntity(
                    nameBn = nameBn.trim(),
                    designation = designation.trim(),
                    assignedCategory = category.trim(),
                    phone = phone.trim(),
                    notes = notes.trim()
                )
            )
        }
        if (newJudges.isNotEmpty()) {
            viewModelScope.launch {
                repository.addJudges(newJudges)
                _statusNotification.value = "${newJudges.size} জন বিচারক সফলভাবে এক্সেল থেকে ইমপোর্ট করা হয়েছে!"
            }
        }
        return newJudges.size
    }

    fun batchImportVolunteers(content: String): Int {
        val lines = content.lines().filter { it.isNotBlank() }
        val newVolunteers = mutableListOf<VolunteerEntity>()
        for (line in lines) {
            val tokens = splitCsvOrTsv(line)
            if (tokens.isEmpty()) continue
            if (tokens[0].contains("নাম") || tokens[0].equals("name", ignoreCase = true)) continue
            val nameBn = tokens.getOrNull(0) ?: continue
            val role = tokens.getOrNull(1) ?: "ক্যাম্প ভলান্টিয়ার"
            val team = tokens.getOrNull(2) ?: "সাধারণ"
            val phone = tokens.getOrNull(3) ?: ""
            val area = tokens.getOrNull(4) ?: ""
            newVolunteers.add(
                VolunteerEntity(
                    nameBn = nameBn.trim(),
                    role = role.trim(),
                    team = team.trim(),
                    phone = phone.trim(),
                    assignedArea = area.trim()
                )
            )
        }
        if (newVolunteers.isNotEmpty()) {
            viewModelScope.launch {
                repository.addVolunteers(newVolunteers)
                _statusNotification.value = "${newVolunteers.size} জন ভলান্টিয়ার এক্সেল থেকে সফলভাবে যুক্ত করা হয়েছে!"
            }
        }
        return newVolunteers.size
    }

    fun batchImportDutyOfficers(content: String): Int {
        val lines = content.lines().filter { it.isNotBlank() }
        val newOfficers = mutableListOf<DutyOfficerEntity>()
        for (line in lines) {
            val tokens = splitCsvOrTsv(line)
            if (tokens.isEmpty()) continue
            if (tokens[0].contains("পদবী") || tokens[0].contains("দায়িত্ব") || tokens[0].equals("role", ignoreCase = true)) continue
            val roleTitle = tokens.getOrNull(0) ?: continue
            val name = tokens.getOrNull(1) ?: continue
            val phone = tokens.getOrNull(2) ?: ""
            val location = tokens.getOrNull(3) ?: ""
            newOfficers.add(
                DutyOfficerEntity(
                    roleTitleBn = roleTitle.trim(),
                    officerName = name.trim(),
                    phone = phone.trim(),
                    location = location.trim()
                )
            )
        }
        if (newOfficers.isNotEmpty()) {
            viewModelScope.launch {
                repository.addDutyOfficers(newOfficers)
                _statusNotification.value = "${newOfficers.size} জন দায়িত্বপ্রাপ্ত কর্মকর্তা এক্সেল থেকে যুক্ত করা হয়েছে!"
            }
        }
        return newOfficers.size
    }

    private fun splitCsvOrTsv(line: String): List<String> {
        val delimiter = if (line.contains("\t")) "\t" else ","
        return line.split(delimiter).map { it.trim().trim('"', '\'') }
    }

    fun submitHealthCheckup(
        participant: ParticipantEntity,
        checkupType: String,
        doctor: String,
        vitals: String,
        diagnosis: String,
        advice: String,
        isEmergency: Boolean
    ) {
        viewModelScope.launch {
            val offline = isOffline.value
            val record = HealthRecordEntity(
                participantId = participant.id,
                campCode = participant.campCode,
                participantName = participant.nameBn,
                groupName = participant.groupNameBn,
                checkupType = checkupType,
                doctorName = doctor,
                vitals = vitals,
                diagnosis = diagnosis,
                adviceAndMeds = advice,
                isEmergencyFlag = isEmergency,
                priority = if (isEmergency) "HIGH" else "NORMAL",
                status = if (isEmergency) "PENDING" else "RESOLVED"
            )
            repository.submitHealthRecord(record, isOfflineMode = offline)
            if (offline) {
                _statusNotification.value = "স্বাস্থ্য স্ক্রিনিং অফলাইন ক্যাশে সংরক্ষিত (${participant.nameBn}) - অনলাইনে সিঙ্ক হবে"
            } else {
                _statusNotification.value = "স্বাস্থ্য স্ক্রিনিং সফলভাবে যুক্ত হয়েছে (${participant.nameBn})"
            }
        }
    }

    fun getHealthRecordsForParticipant(participantId: Long): Flow<List<HealthRecordEntity>> {
        return repository.getHealthRecordsForParticipant(participantId)
    }

    fun attendHighPriorityHealthAlert(recordId: Long) {
        viewModelScope.launch {
            val record = healthRecords.value.find { it.id == recordId } ?: return@launch
            val updated = record.copy(status = "ATTENDED")
            repository.updateHealthRecord(updated)
            _statusNotification.value = "মেডিকেল সতর্কতা গৃহীত হয়েছে: ${record.participantName} (মেডিকেল টিম উপস্থিত)"
        }
    }

    fun resolveHighPriorityHealthAlert(recordId: Long, resolutionNotes: String = "") {
        viewModelScope.launch {
            val record = healthRecords.value.find { it.id == recordId } ?: return@launch
            val updated = record.copy(
                status = "RESOLVED",
                adviceAndMeds = if (resolutionNotes.isNotBlank()) "${record.adviceAndMeds} | সমাধান: $resolutionNotes" else record.adviceAndMeds
            )
            repository.updateHealthRecord(updated)
            _statusNotification.value = "মেডিকেল সতর্কতা সমাধান সম্পন্ন: ${record.participantName}"
        }
    }

    fun issueAwardInventory(itemId: Long, qty: Int, recipient: String, officer: String) {
        viewModelScope.launch {
            val (success, message) = repository.issueInventoryItem(itemId, qty, recipient, officer)
            _statusNotification.value = if (success) message else "⚠ $message"
        }
    }

    fun issueMealClaim(dayNo: Int, mealType: String, participant: ParticipantEntity, onComplete: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            val (success, message) = repository.claimMeal(dayNo, mealType, participant)
            if (success) {
                _statusNotification.value = "✓ ${participant.nameBn}: খাবার বিতরণ সম্পন্ন ($mealType)"
            } else {
                _statusNotification.value = "⚠ ${participant.nameBn}: $message"
            }
            onComplete?.invoke(success, message)
        }
    }

    fun reportIncident(
        title: String,
        pCode: String?,
        pName: String?,
        severity: String,
        location: String,
        desc: String,
        action: String
    ) {
        viewModelScope.launch {
            val incident = IncidentEntity(
                title = title,
                participantCampCode = pCode,
                participantName = pName,
                severity = severity,
                location = location,
                description = desc,
                actionTaken = action,
                reporter = _currentRole.value.titleBn
            )
            repository.reportIncident(incident)
            _statusNotification.value = "জরুরি ঘটনা রেকর্ড ও কর্তৃপক্ষকে অবহিত করা হয়েছে"
        }
    }

    fun sendQuickAlert(
        title: String,
        category: String,
        severity: String,
        location: String,
        desc: String,
        participant: ParticipantEntity?
    ) {
        viewModelScope.launch {
            val incident = IncidentEntity(
                title = title,
                participantCampCode = participant?.campCode,
                participantName = participant?.nameBn,
                severity = severity,
                location = location,
                description = desc,
                actionTaken = "ক্যাম্প অ্যাডমিনিস্ট্রেশনে তাৎক্ষণিক পুশ অ্যালার্ট প্রেরিত",
                reporter = _currentRole.value.titleBn,
                status = "OPEN"
            )
            val insertedId = repository.reportIncident(incident)
            val dispatchedIncident = incident.copy(id = insertedId)
            _latestPushAlert.value = dispatchedIncident
            _statusNotification.value = "🚨 জরুরি পুশ অ্যালার্ট পাঠানো হয়েছে: $title"
        }
    }

    fun acknowledgeQuickAlert(alertId: Long) {
        viewModelScope.launch {
            val alert = incidents.value.find { it.id == alertId } ?: return@launch
            val updated = alert.copy(
                status = "IN_PROGRESS",
                actionTaken = "কর্তৃপক্ষ কর্তৃক গৃহীত হয়েছে এবং রেসপন্স টিম প্রস্তুত।"
            )
            repository.updateIncident(updated)
            if (_latestPushAlert.value?.id == alertId) {
                _latestPushAlert.value = null
            }
            _statusNotification.value = "অ্যালার্ট #${alertId} গৃহীত হয়েছে (In Progress)"
        }
    }

    fun resolveQuickAlert(alertId: Long, resolutionNotes: String = "") {
        viewModelScope.launch {
            val alert = incidents.value.find { it.id == alertId } ?: return@launch
            val notes = resolutionNotes.ifBlank { "পরিস্থিতি স্বাভাবিক ও সমাধান সম্পন্ন।" }
            val updated = alert.copy(
                status = "RESOLVED",
                actionTaken = "${alert.actionTaken.ifBlank { "গৃহীত" }} | সমাধান: $notes"
            )
            repository.updateIncident(updated)
            if (_latestPushAlert.value?.id == alertId) {
                _latestPushAlert.value = null
            }
            _statusNotification.value = "অ্যালার্ট #${alertId} সফলভাবে সমাধান (RESOLVED) করা হয়েছে।"
        }
    }

    fun dismissPushAlert() {
        _latestPushAlert.value = null
    }

    fun importCsv(csvContent: String) {
        viewModelScope.launch {
            val count = repository.importParticipantsFromCsv(csvContent)
            _statusNotification.value = "সফলভাবে $count জন নতুন অংশগ্রহণকারী ইমপোর্ট সম্পন্ন হয়েছে!"
        }
    }

    fun recordDailyHealthCheckIn(participant: ParticipantEntity, status: String, notes: String = "") {
        viewModelScope.launch {
            val offline = isOffline.value
            repository.recordDailyHealthCheckIn(participant, status, notes, isOfflineMode = offline)
            val statusBn = when (status) {
                "Feeling well" -> "সুস্থ ও স্বাভাবিক (Feeling well)"
                "Needs rest" -> "বিশ্রাম প্রয়োজন (Needs rest)"
                else -> status
            }
            if (offline) {
                _statusNotification.value = "স্বাস্থ্য চেক-ইন অফলাইন ক্যাশে সংরক্ষিত: $statusBn (${participant.nameBn})"
            } else {
                _statusNotification.value = "স্বাস্থ্য চেক-ইন সফলভাবে সংরক্ষিত: $statusBn (${participant.nameBn})"
            }
        }
    }
}
