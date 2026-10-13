package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AnalysisHistoryEntity::class], version = 1, exportSchema = false)
abstract class TradeGuruDatabase : RoomDatabase() {
    abstract fun analysisDao(): AnalysisDao

    companion object {
        @Volatile
        private var INSTANCE: TradeGuruDatabase? = null

        fun getDatabase(context: Context): TradeGuruDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TradeGuruDatabase::class.java,
                    "tradeguru_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
