package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

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
        CachedSyncItemEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class CampDatabase : RoomDatabase() {

    abstract fun campDao(): CampDao
    abstract fun participantDao(): ParticipantDao
    abstract fun participantHealthDao(): ParticipantHealthDao

    companion object {
        @Volatile
        private var INSTANCE: CampDatabase? = null

        fun getDatabase(context: Context): CampDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CampDatabase::class.java,
                    "lsc_camp_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

typealias AppDatabase = CampDatabase
