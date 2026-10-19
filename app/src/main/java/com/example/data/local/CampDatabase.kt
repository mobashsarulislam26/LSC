package com.example.data.local

import android.content.Context
import android.util.Base64
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.security.SecureRandom

@Database(
    entities = [
        ParticipantEntity::class,
        CampGroupEntity::class,
        StationEntity::class,
        ScheduleEventEntity::class,
        AttendanceEventEntity::class,
        AttendanceRecordEntity::class,
        CompetitionEntity::class,
        CompetitionScoreEntity::class,
        HealthRecordEntity::class,
        InventoryItemEntity::class,
        InventoryTransactionEntity::class,
        BulletinEntity::class,
        IncidentEntity::class,
        MealDistributionEntity::class,
        JudgeEntity::class,
        VolunteerEntity::class,
        DutyOfficerEntity::class,
        RoomEntity::class,
        TravelVehicleEntity::class,
        DutyRosterEntity::class,
        KitDistributionEntity::class,
        CampGoalMetricEntity::class,
        UserAccountEntity::class,
        AuditLogEntity::class,
        ParticipantHealthStatusEntity::class,
        CachedSyncItemEntity::class,
        VolunteerShiftEntity::class
    ],
    version = 11,
    exportSchema = false
)
abstract class CampDatabase : RoomDatabase() {

    abstract fun campDao(): CampDao
    abstract fun participantDao(): ParticipantDao
    abstract fun stationDao(): StationDao
    abstract fun participantHealthDao(): ParticipantHealthDao

    companion object {
        @Volatile
        private var INSTANCE: CampDatabase? = null

        /**
         * Securely retrieves or generates a persistent encryption passphrase
         * for SQLCipher database encryption.
         */
        private fun getPassphrase(context: Context): ByteArray {
            val prefs = context.getSharedPreferences("lsc_db_security_prefs", Context.MODE_PRIVATE)
            var keyBase64 = prefs.getString("db_enc_passphrase", null)
            if (keyBase64.isNullOrEmpty()) {
                val randomBytes = ByteArray(32)
                SecureRandom().nextBytes(randomBytes)
                keyBase64 = Base64.encodeToString(randomBytes, Base64.NO_WRAP)
                prefs.edit().putString("db_enc_passphrase", keyBase64).apply()
            }
            return Base64.decode(keyBase64, Base64.NO_WRAP)
        }

        // --- Non-destructive explicit Room migrations ---
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `participant_health_status` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `participantId` INTEGER NOT NULL, `campCode` TEXT NOT NULL, `participantName` TEXT NOT NULL, `medicationNeeds` TEXT NOT NULL, `allergies` TEXT NOT NULL, `dailyCheckInLogs` TEXT NOT NULL, `hasMedicationNeeds` INTEGER NOT NULL, `hasAllergies` INTEGER NOT NULL, `isDailyCheckedIn` INTEGER NOT NULL, `lastCheckInDate` TEXT NOT NULL, `lastCheckInTime` INTEGER NOT NULL, `vitalsSummary` TEXT NOT NULL, `healthCondition` TEXT NOT NULL, `bloodGroup` TEXT NOT NULL, `emergencyContactName` TEXT NOT NULL, `emergencyContactPhone` TEXT NOT NULL, `doctorOrNurseNotes` TEXT NOT NULL, `specialDietNotes` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `cached_sync_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `itemType` TEXT NOT NULL, `participantId` INTEGER NOT NULL, `campCode` TEXT NOT NULL, `participantName` TEXT NOT NULL, `summary` TEXT NOT NULL, `payloadJson` TEXT NOT NULL, `cachedAt` INTEGER NOT NULL, `status` TEXT NOT NULL, `syncedAt` INTEGER)")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `incidents` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `incidentType` TEXT NOT NULL, `severity` TEXT NOT NULL, `location` TEXT NOT NULL, `reportedBy` TEXT NOT NULL, `description` TEXT NOT NULL, `status` TEXT NOT NULL, `reportedAt` INTEGER NOT NULL, `actionTaken` TEXT NOT NULL)")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `meal_distributions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `participantId` INTEGER NOT NULL, `mealType` TEXT NOT NULL, `date` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `distributedBy` TEXT NOT NULL, `status` TEXT NOT NULL)")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `health_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `participantId` INTEGER NOT NULL, `recordedAt` INTEGER NOT NULL, `condition` TEXT NOT NULL, `notes` TEXT NOT NULL, `treatmentGiven` TEXT NOT NULL, `attendedBy` TEXT NOT NULL)")
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `attendance_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `eventId` INTEGER NOT NULL, `participantId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `status` TEXT NOT NULL, `verifiedBy` TEXT NOT NULL)")
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `audit_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `action` TEXT NOT NULL, `user` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `details` TEXT NOT NULL)")
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_participant_health_status_participantId` ON `participant_health_status` (`participantId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_cached_sync_items_status` ON `cached_sync_items` (`status`)")
            }
        }

        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_meal_distributions_dayNo_mealType_participantId` ON `meal_distributions` (`dayNo`, `mealType`, `participantId`)")
            }
        }

        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `volunteer_shifts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `volunteerId` INTEGER NOT NULL, `volunteerName` TEXT NOT NULL, `volunteerRole` TEXT NOT NULL, `taskTitleBn` TEXT NOT NULL, `taskTitleEn` TEXT NOT NULL, `departmentOrLocation` TEXT NOT NULL, `dayNo` INTEGER NOT NULL, `startTime` TEXT NOT NULL, `endTime` TEXT NOT NULL, `startTimeMillis` INTEGER NOT NULL, `endTimeMillis` INTEGER NOT NULL, `taskDescription` TEXT NOT NULL, `status` TEXT NOT NULL, `confirmedAt` INTEGER, `checkedInAt` INTEGER, `notes` TEXT NOT NULL)")
            }
        }

        private val ALL_MIGRATIONS = arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6,
            MIGRATION_6_7,
            MIGRATION_7_8,
            MIGRATION_8_9,
            MIGRATION_9_10,
            MIGRATION_10_11
        )

        private fun isRobolectricTest(): Boolean {
            return try {
                Class.forName("org.robolectric.Robolectric") != null ||
                    android.os.Build.FINGERPRINT == "unknown" ||
                    android.os.Build.MODEL.contains("robolectric", ignoreCase = true)
            } catch (e: Exception) {
                false
            }
        }

        fun getDatabase(context: Context): CampDatabase {
            return INSTANCE ?: synchronized(this) {
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    CampDatabase::class.java,
                    "lsc_camp_database"
                )
                    .addMigrations(*ALL_MIGRATIONS)

                if (!isRobolectricTest()) {
                    val isSqlcipherAvailable = try {
                        System.loadLibrary("sqlcipher")
                        true
                    } catch (e: Throwable) {
                        android.util.Log.w("CampDatabase", "SQLCipher native library not loaded (${e.message}), using standard SQLite helper.")
                        false
                    }

                    if (isSqlcipherAvailable) {
                        try {
                            val passphrase = getPassphrase(context)
                            val factory = SupportOpenHelperFactory(passphrase)
                            builder.openHelperFactory(factory)
                        } catch (e: Throwable) {
                            android.util.Log.e("CampDatabase", "Failed to configure SQLCipher open helper factory: ${e.message}")
                        }
                    }
                }

                val instance = builder.build()
                INSTANCE = instance
                instance
            }
        }
    }
}

typealias AppDatabase = CampDatabase
