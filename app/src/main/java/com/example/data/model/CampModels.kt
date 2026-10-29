package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class UserRole(val titleBn: String, val titleEn: String) {
    SUPER_ADMIN("সুপার অ্যাডমিন", "Super Admin"),
    CAMP_CHIEF("ক্যাম্প চিফ", "Camp Chief"),
    STATION_MANAGER("স্টেশন ম্যানেজার", "Station Manager"),
    GROUP_LEADER("গ্রুপ লিডার", "Group Leader"),
    MENTOR("সাব-গ্রুপ ইনচার্জ", "Sub-group In-charge"),
    STATION_MODERATOR("স্টেশন মডারেটর", "Station Moderator"),
    COMPETITION_JUDGE("বিচারক", "Competition Judge"),
    MEDICAL_TEAM("মেডিকেল টিম", "Medical Team"),
    INVENTORY_OFFICER("উপহার ও পুরস্কার অফিসার", "Gifts & Inventory Officer"),
    BULLETIN_EDITOR("বুলেটিন ও মিডিয়া এডিটর", "Bulletin & Media Editor"),
    VOLUNTEER("চেক-ইন ভলান্টিয়ার", "Check-in Volunteer"),
    PARTICIPANT("অংশগ্রহণকারী", "Participant")
}

enum class CampLanguage {
    BANGLA, ENGLISH
}

@Entity(
    tableName = "participants",
    indices = [
        Index(value = ["campCode"], unique = true),
        Index(value = ["groupId"]),
        Index(value = ["fileNo"])
    ]
)
data class ParticipantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val campCode: String, // e.g. "Bukhari-1-1"
    val fileNo: String,   // e.g. "EDAS-1258"
    val nameBn: String,
    val nameEn: String,
    val districtBn: String,
    val districtEn: String,
    val gender: String, // "Male" or "Female"
    val age: Int,
    val groupId: String, // "bukhari", "shafeyi", "salahuddin", "sumaiya", "umm_ayman", "umm_saad"
    val groupNameBn: String,
    val groupNameEn: String,
    val subgroup: String, // e.g. "Bukhari-01"
    val roomNo: String,   // e.g. "P1-Down-A"
    val bedNo: String,    // e.g. "Bed-01"
    val mealPref: String, // "নিয়মিত", "ঝাল কম", "Allergy"
    val guardianName: String,
    val guardianPhone: String,
    val isCheckedIn: Boolean = true,
    val checkInTime: Long = System.currentTimeMillis(),
    val medicalFlag: String? = null, // "Asthma", "Dust Allergy", null
    val totalScore: Double = 0.0,
    val attendancePercentage: Float = 95.0f,
    val photoAvatarSeed: Int = 1,
    val classLevel: String = "৪র্থ শ্রেণি",
    val organization: String = "WAMY-OSP",
    val completedStationSeals: String = "" // Comma-separated e.g. "S1D2,S2D2,S3D2..." (18 official star seals)
)

typealias Participant = ParticipantEntity

@Entity(tableName = "camp_groups")
data class CampGroupEntity(
    @PrimaryKey val id: String,
    val nameBn: String,
    val nameEn: String,
    val leaderName: String,
    val leaderPhone: String,
    val colorHex: Long,
    val memberCount: Int,
    val totalPoints: Int = 0,
    val mentorName: String = "",
    val mentorPhone: String = "",
    val subgroupMentors: String = ""
)

@Entity(tableName = "volunteers")
data class VolunteerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameBn: String,
    val nameEn: String = "",
    val role: String = "ক্যাম্প ভলান্টিয়ার",
    val team: String = "লজিস্টিকস ও সাপোর্ট",
    val phone: String = "",
    val assignedArea: String = "সাধারণ",
    val notes: String = "",
    val isPresent: Boolean = true
)

@Entity(tableName = "duty_officers")
data class DutyOfficerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roleTitleBn: String,
    val roleTitleEn: String = "",
    val officerName: String,
    val phone: String = "",
    val location: String = "",
    val dutyShift: String = "সার্বক্ষণিক"
)

@Entity(tableName = "stations")
data class StationEntity(
    @PrimaryKey val id: Int, // 1 to 6
    val nameBn: String,
    val nameEn: String,
    val moderatorName: String,
    val location: String,
    val objectiveBn: String,
    val iconKey: String,
    val materialsNeeded: String = "বোর্ড, মার্কার, হ্যান্ডআউট, উপকরণ কিট",
    val day2TopicBn: String = "",
    val day4TopicBn: String = "",
    val day5TopicBn: String = "",
    val subModerators: String = ""
)

@Entity(tableName = "schedule_events")
data class ScheduleEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayNo: Int, // 1 to 5
    val timeSlot: String, // "05:00 - 05:30"
    val titleBn: String,
    val titleEn: String,
    val category: String, // "Jamaat", "Quran", "Station", "Meal", "Competition", "Halaqa", "Physical", "Transport", "Rest"
    val stationId: Int? = null,
    val location: String = "Main Hall / Ground",
    val isCurrent: Boolean = false,
    val isCompleted: Boolean = false,
    val rotationJson: String = "" // "bukhari:1,salahuddin:2,umm_ayman:3,sumaiya:4,umm_saad:5,shafeyi:6"
)

@Entity(tableName = "attendance_events")
data class AttendanceEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titleBn: String,
    val titleEn: String,
    val dayNo: Int,
    val timeLabel: String,
    val targetCount: Int = 609,
    val presentCount: Int = 0,
    val lateCount: Int = 0,
    val absentCount: Int = 0,
    val isClosed: Boolean = false
)

@Entity(tableName = "attendance_records")
data class AttendanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventId: Long,
    val participantId: Long,
    val campCode: String,
    val participantName: String,
    val groupName: String,
    val subgroup: String,
    val status: String, // "PRESENT", "LATE", "ABSENT", "EXCUSED", "MEDICAL"
    val scannedAt: Long = System.currentTimeMillis(),
    val method: String = "QR_SCAN", // "QR_SCAN", "ROSTER_TAP", "BATCH_SUBGROUP", "MANUAL_OVERRIDE"
    val overrideReason: String? = null,
    val isSynced: Boolean = true
)

@Entity(tableName = "competitions")
data class CompetitionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameBn: String,
    val nameEn: String,
    val category: String, // "Quran", "Hadith", "Quiz", "Arts", "Speech", "Sports", "Team", "Parade", "Tour"
    val dayNo: Int,
    val maxScore: Int = 100,
    val judgeName: String,
    val status: String = "LIVE", // "SCHEDULED", "LIVE", "FINALIZED"
    val prizeCount: Int = 72,
    val isTeamEvent: Boolean = false,
    val selectionScope: String = "SUBGROUP_TOP3" // "SUBGROUP_TOP3", "GROUP_BEST", "PARADE", "TEAM", "OUTING"
)

@Entity(tableName = "judges")
data class JudgeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameBn: String,
    val nameEn: String = "",
    val designation: String = "",
    val assignedCategory: String = "সাধারণ",
    val phone: String = "",
    val email: String = "",
    val notes: String = "",
    val isActive: Boolean = true
)

@Entity(tableName = "competition_scores")
data class CompetitionScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val competitionId: Long,
    val participantId: Long,
    val campCode: String,
    val participantName: String,
    val groupName: String,
    val subgroup: String,
    val accuracyScore: Double,
    val presentationScore: Double,
    val disciplineScore: Double,
    val totalScore: Double,
    val remarks: String = "",
    val rank: Int = 0,
    val isAwardEligible: Boolean = false,
    val isPrizeHandedOver: Boolean = false
)

@Entity(tableName = "health_records")
data class HealthRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val participantId: Long,
    val campCode: String,
    val participantName: String,
    val groupName: String,
    val checkupType: String, // "GENERAL", "DENTAL", "EYE", "EMERGENCY"
    val doctorName: String,
    val vitals: String,
    val diagnosis: String,
    val adviceAndMeds: String,
    val isEmergencyFlag: Boolean = false,
    val priority: String = "NORMAL", // "HIGH", "NORMAL", "LOW"
    val status: String = "PENDING", // "PENDING", "ATTENDED", "RESOLVED"
    val followUpDate: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true,
    val heightCm: String = "",
    val weightKg: String = "",
    val dentalBusReferred: Boolean = false,
    val visionNotes: String = "",
    val photoDocumented: Boolean = false
)

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameBn: String,
    val nameEn: String,
    val category: String, // "AWARD", "GIFT", "STATIONERY", "HEALTH", "KIT"
    val openingQty: Int,
    val receivedQty: Int = 0,
    val issuedQty: Int = 0,
    val balanceQty: Int,
    val lowStockThreshold: Int = 10,
    val unit: String = "টি"
)

@Entity(tableName = "inventory_transactions")
data class InventoryTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: Long,
    val itemName: String,
    val type: String, // "ISSUE", "RECEIVE", "DAMAGE"
    val qty: Int,
    val recipient: String,
    val officerName: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bulletins")
data class BulletinEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayNo: Int,
    val dateStr: String,
    val titleBn: String,
    val titleEn: String,
    val summaryBn: String,
    val summaryEn: String,
    val author: String,
    val groupSpotlight: String,
    val likesCount: Int = 24,
    val isPublished: Boolean = true,
    val status: String = "PUBLISHED", // "DRAFT", "UNDER_REVIEW", "APPROVED", "PUBLISHED"
    val hasImageConsent: Boolean = true,
    val wallMagazineContributions: Int = 6
)

@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "MEDICAL", // "LOST_AND_FOUND", "MEDICAL", "SAFETY", "INFRASTRUCTURE", "EVALUATION_FEEDBACK", "GENERAL"
    val participantCampCode: String? = null,
    val participantName: String? = null,
    val severity: String = "MEDIUM", // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    val location: String = "ক্যাম্প গ্রাউন্ড",
    val description: String = "",
    val actionTaken: String = "",
    val reporter: String = "স্টাফ/অংশগ্রহণকারী",
    val status: String = "OPEN", // "OPEN", "IN_PROGRESS", "RESOLVED"
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
)

@Entity(
    tableName = "meal_distributions",
    indices = [
        Index(value = ["dayNo", "mealType", "participantId"], unique = true)
    ]
)
data class MealDistributionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayNo: Int,
    val mealType: String, // "BREAKFAST", "LUNCH", "DINNER", "SNACKS"
    val participantId: Long,
    val campCode: String,
    val participantName: String,
    val claimedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "camp_rooms")
data class RoomEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val buildingName: String, // e.g. "Platinum-1", "Golden-2", "Rajonigandha / Silver"
    val roomNo: String,       // e.g. "P1-Down-A", "Golden-2-A", "Rajo-101"
    val floor: String = "১ম তলা",
    val capacity: Int,
    val assignedCount: Int = 0,
    val assignedGroupId: String = "bukhari",
    val inChargeName: String = "",
    val inChargePhone: String = "",
    val notes: String = "",
    val cleanlinessScore: Int = 0,
    val lastInspectedAt: Long? = null
)

@Entity(tableName = "travel_vehicles")
data class TravelVehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleNo: String,
    val driverName: String,
    val driverPhone: String,
    val inChargeName: String,
    val inChargePhone: String,
    val capacity: Int = 42,
    val assignedGroupId: String = "bukhari",
    val departureHeadcount: Int = 0,
    val returnHeadcount: Int = 0,
    val status: String = "SCHEDULED", // "SCHEDULED", "BOARDING", "IN_TRANSIT", "RETURNED"
    val destination: String = "জাতীয় চিড়িয়াখানা, সংসদ ভবন, সামরিক জাদুঘর ও মেট্রোরেল"
)

@Entity(tableName = "duty_rosters")
data class DutyRosterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val committeeKey: String,
    val committeeNameBn: String,
    val taskTitle: String,
    val assignedPersonName: String,
    val assignedPersonPhone: String = "",
    val shiftTime: String = "সকাল ০৭:০০ - দুপুর ০১:৩০",
    val location: String = "রাঙামাটি ওয়াটারফ্রন্ট রিসোর্ট",
    val status: String = "PENDING", // "PENDING", "IN_PROGRESS", "COMPLETED"
    val notes: String = ""
)

@Entity(tableName = "kit_distributions")
data class KitDistributionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val participantId: Long,
    val campCode: String,
    val participantName: String,
    val groupName: String,
    val tShirtSize: String = "M", // "S", "M", "L", "XL", "XXL"
    val kitBagStatus: String = "ALLOCATED", // "ALLOCATED", "DISTRIBUTED", "CONFIRMED"
    val giftPackStatus: String = "ALLOCATED", // "ALLOCATED", "DISTRIBUTED", "CONFIRMED"
    val idBadgeStatus: String = "DISTRIBUTED",
    val notebookStatus: String = "DISTRIBUTED",
    val receivedAt: Long? = null,
    val deliveredBy: String = "উপহার ও সম্মাননা কমিটি",
    val clothingSize: String = "M",
    val shoeSize: String = "36",
    val motherChadorStatus: String = "DISTRIBUTED", // "ALLOCATED", "DISTRIBUTED", "CONFIRMED"
    val distributedItemsCount: Int = 30
)

@Entity(tableName = "camp_goal_metrics")
data class CampGoalMetricEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val metricTitleBn: String,
    val targetValue: Double,
    val currentValue: Double,
    val unit: String = "%",
    val category: String = "হাজিরা ও অংশগ্রহণ"
)

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val email: String,
    val fullName: String,
    val role: String, // "SUPER_ADMIN", "CAMP_CHIEF", "STATION_MANAGER", "REGISTRATION", "MEDICAL", "JUDGE", "LOGISTICS", "GROUP_LEADER", "MENTOR", "STATION_MODERATOR", "BULLETIN_EDITOR"
    val passwordHash: String,
    val phone: String = "",
    val isActive: Boolean = true,
    val lastLoginTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "volunteer_shifts")
data class VolunteerShiftEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val volunteerId: Long = 0,
    val volunteerName: String,
    val volunteerRole: String = "ক্যাম্প ভলান্টিয়ার",
    val taskTitleBn: String,
    val taskTitleEn: String = "",
    val departmentOrLocation: String,
    val dayNo: Int = 1,
    val startTime: String,
    val endTime: String,
    val startTimeMillis: Long = System.currentTimeMillis(),
    val endTimeMillis: Long = System.currentTimeMillis() + 4 * 3600 * 1000L,
    val taskDescription: String = "",
    val status: String = "SCHEDULED", // "SCHEDULED", "CONFIRMED", "CHECKED_IN", "COMPLETED", "ABSENT"
    val confirmedAt: Long? = null,
    val checkedInAt: Long? = null,
    val notes: String = ""
)

fun UserAccountEntity.toUserRole(): UserRole {
    return when (role.trim().uppercase()) {
        "SUPER_ADMIN", "ADMIN" -> UserRole.SUPER_ADMIN
        "CAMP_CHIEF", "CHIEF" -> UserRole.CAMP_CHIEF
        "STATION_MANAGER", "STATION_DIRECTOR" -> UserRole.STATION_MANAGER
        "REGISTRATION", "VOLUNTEER", "CHECK_IN_VOLUNTEER" -> UserRole.VOLUNTEER
        "MEDICAL", "MEDICAL_TEAM", "DOCTOR" -> UserRole.MEDICAL_TEAM
        "JUDGE", "COMPETITION_JUDGE" -> UserRole.COMPETITION_JUDGE
        "LOGISTICS", "INVENTORY", "INVENTORY_OFFICER", "FOOD", "GIFT" -> UserRole.INVENTORY_OFFICER
        "GROUP_LEADER", "LEADER" -> UserRole.GROUP_LEADER
        "MENTOR", "SUBGROUP_MENTOR", "SUBGROUP_INCHARGE" -> UserRole.MENTOR
        "STATION_MODERATOR", "MODERATOR" -> UserRole.STATION_MODERATOR
        "BULLETIN", "BULLETIN_EDITOR", "MEDIA" -> UserRole.BULLETIN_EDITOR
        "PARTICIPANT", "CAMPER" -> UserRole.PARTICIPANT
        else -> UserRole.VOLUNTEER
    }
}

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val adminId: String = "",
    val userEmail: String,
    val actionType: String,
    val targetEntity: String,
    val description: String,
    val modifiedRecordDetails: String = ""
)

data class EmergencyContact(
    val roleTitleBn: String,
    val roleTitleEn: String,
    val officerName: String,
    val phone: String,
    val location: String,
    val isAvailable24x7: Boolean = true
)
