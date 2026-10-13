package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisDao {
    @Query("SELECT * FROM analysis_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<AnalysisHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(entity: AnalysisHistoryEntity)

    @Delete
    suspend fun deleteAnalysis(entity: AnalysisHistoryEntity)

    @Query("DELETE FROM analysis_history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM analysis_history")
    suspend fun clearAll()
}
