package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class UserRole(val titleBn: String, val titleEn: String) {
    SUPER_ADMIN("সুপার অ্যাডমিন", "Super Admin"),
    CAMP_CHIEF("ক্যাম্প চিফ", "Camp Chief"),
    GROUP_LEADER("গ্রুপ লিডার", "Group Leader"),
    MENTOR("সাব-গ্রুপ মেন্টর", "Sub-group Mentor"),
    STATION_MODERATOR("স্টেশন মডারেটর", "Station Moderator"),
    COMPETITION_JUDGE("বিচারক", "Competition Judge"),
    MEDICAL_TEAM("মেডিকেল টিম", "Medical Team"),
    INVENTORY_OFFICER("ইনভেন্টরি অফিসার", "Inventory Officer"),
    BULLETIN_EDITOR("বুলেটিন এডিটর", "Bulletin Editor"),
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
    val campCode: String, // e.g. "LSC-2026-001"
    val fileNo: String,   // e.g. "F-1001"
    val nameBn: String,
    val nameEn: String,
    val districtBn: String,
    val districtEn: String,
    val gender: String, // "Male" or "Female"
    val age: Int,
    val groupId: String, // "bukhari", "sumaiya", etc.
    val groupNameBn: String,
    val groupNameEn: String,
    val subgroup: String, // e.g. "Bukhari-1"
    val roomNo: String,   // e.g. "A-101"
    val bedNo: String,    // e.g. "Bed-02"
    val mealPref: String, // "Regular", "No Spicy", "Allergy"
    val guardianName: String,
    val guardianPhone: String,
    val isCheckedIn: Boolean = true,
    val checkInTime: Long = System.currentTimeMillis(),
    val medicalFlag: String? = null, // "Asthma", "Dust Allergy", null
    val totalScore: Double = 0.0,
    val attendancePercentage: Float = 95.0f,
    val photoAvatarSeed: Int = 1
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
    val materialsNeeded: String = "বোর্ড, মার্কার, হ্যান্ডআউট, উপকরণ কিট"
)

@Entity(tableName = "schedule_events")
data class ScheduleEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayNo: Int, // 1 to 5
    val timeSlot: String, // "05:00 - 05:45"
    val titleBn: String,
    val titleEn: String,
    val category: String, // "Jamaat", "Quran", "Station", "Meal", "Competition", "Halaqa", "Rest"
    val stationId: Int? = null,
    val location: String = "Main Hall / Ground",
    val isCurrent: Boolean = false,
    val isCompleted: Boolean = false,
    val rotationJson: String = "" // "bukhari:1,sumaiya:2,shafeyi:3,umm_ayman:4,salahuddin:5,umm_saad:6"
)

@Entity(tableName = "attendance_events")
data class AttendanceEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titleBn: String,
    val titleEn: String,
    val dayNo: Int,
    val timeLabel: String,
    val targetCount: Int = 603,
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
    val method: String = "QR_SCAN", // "QR_SCAN", "ROSTER_TAP", "MANUAL_OVERRIDE"
    val overrideReason: String? = null,
    val isSynced: Boolean = true
)

@Entity(tableName = "competitions")
data class CompetitionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameBn: String,
    val nameEn: String,
    val category: String, // "Quran", "Hadith", "Quiz", "Arts", "Speech", "Sports"
    val dayNo: Int,
    val maxScore: Int = 100,
    val judgeName: String,
    val status: String = "LIVE" // "SCHEDULED", "LIVE", "FINALIZED"
)

@Entity(tableName = "judges")
data class JudgeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameBn: String,
    val nameEn: String = "",
    val designation: String = "", // e.g. "মুফাসসিরে কুরআন", "আন্তর্জাতিক ক্বারী", "অধ্যাপক ও গবেষক"
    val assignedCategory: String = "সাধারণ", // "Quran", "Hadith", "Quiz", "Arts", "Speech", "Sports"
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
    val isAwardEligible: Boolean = false
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
    val vitals: String, // "BP: 105/70, Temp: 98.4°F, Pulse: 76"
    val diagnosis: String,
    val adviceAndMeds: String,
    val isEmergencyFlag: Boolean = false,
    val priority: String = "NORMAL", // "HIGH", "NORMAL", "LOW"
    val status: String = "PENDING", // "PENDING", "ATTENDED", "RESOLVED"
    val followUpDate: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
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
    val category: String = "MEDICAL", // "LOST_AND_FOUND", "MEDICAL", "SAFETY", "INFRASTRUCTURE", "GENERAL"
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
    val buildingName: String, // e.g. "তাজমহল হল", "শহীদ মিনার ভবন"
    val roomNo: String,       // e.g. "১০১", "১০২"
    val floor: String = "১ম তলা",
    val capacity: Int,        // e.g. 12
    val assignedCount: Int = 0,
    val assignedGroupId: String = "bukhari", // "bukhari", "sumaiya", etc.
    val inChargeName: String = "",
    val inChargePhone: String = "",
    val notes: String = ""
)

@Entity(tableName = "travel_vehicles")
data class TravelVehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleNo: String, // e.g. "বাস ১ (ঢাকা মেট্রো ব-১১-২৩৪৫)"
    val driverName: String,
    val driverPhone: String,
    val inChargeName: String,
    val inChargePhone: String,
    val capacity: Int = 40,
    val assignedGroupId: String = "bukhari",
    val departureHeadcount: Int = 0,
    val returnHeadcount: Int = 0,
    val status: String = "SCHEDULED", // "SCHEDULED", "BOARDING", "IN_TRANSIT", "RETURNED"
    val destination: String = "ঐতিহাসিক সোনারগাঁ ও পানাম নগর"
)

@Entity(tableName = "duty_rosters")
data class DutyRosterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val committeeKey: String, // "MANAGEMENT", "DISCIPLINE", "DINING", "ACCOUNTS", "EVALUATION", "MEDIA", "HEALTH", "TRANSPORT"
    val committeeNameBn: String,
    val taskTitle: String,
    val assignedPersonName: String,
    val assignedPersonPhone: String = "",
    val shiftTime: String = "সকাল ০৭:০০ - দুপুর ০১:০০",
    val location: String = "ক্যাম্প গ্রাউন্ড",
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
    val deliveredBy: String = "লজিস্টিকস টিম"
)

@Entity(tableName = "camp_goal_metrics")
data class CampGoalMetricEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val metricTitleBn: String,
    val targetValue: Double,
    val currentValue: Double,
    val unit: String = "%",
    val category: String = "হাজিরা ও অংশগ্রহণ" // "হাজিরা", "সময়নিষ্ঠতা", "স্বাস্থ্য", "ফিডব্যাক"
)

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val email: String,
    val fullName: String,
    val role: String, // "SUPER_ADMIN", "CAMP_CHIEF", "REGISTRATION", "MEDICAL", "JUDGE", "LOGISTICS", "GROUP_LEADER"
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
    return when (role.uppercase()) {
        "SUPER_ADMIN" -> UserRole.SUPER_ADMIN
        "CAMP_CHIEF" -> UserRole.CAMP_CHIEF
        "REGISTRATION", "VOLUNTEER" -> UserRole.VOLUNTEER
        "MEDICAL", "MEDICAL_TEAM" -> UserRole.MEDICAL_TEAM
        "JUDGE", "COMPETITION_JUDGE" -> UserRole.COMPETITION_JUDGE
        "LOGISTICS", "INVENTORY" -> UserRole.INVENTORY_OFFICER
        "GROUP_LEADER" -> UserRole.GROUP_LEADER
        "MENTOR" -> UserRole.MENTOR
        "STATION_MODERATOR" -> UserRole.STATION_MODERATOR
        "BULLETIN" -> UserRole.BULLETIN_EDITOR
        else -> UserRole.CAMP_CHIEF
    }
}

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val adminId: String = "",
    val userEmail: String,
    val actionType: String, // "USER_ROLE_UPDATE", "ROOM_ASSIGNMENT", "MEAL_SCHEDULE_EDIT", "STATION_UPDATE", etc.
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
