package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analysis_history")
data class AnalysisHistoryEntity(
    @PrimaryKey
    val id: String,
    val assetSymbol: String,
    val movement: String,
    val confidencePercent: Int,
    val primaryTrend: String,
    val candlestickPatternsJson: String,
    val supportLevelsJson: String,
    val resistanceLevelsJson: String,
    val breakoutStatus: String,
    val entryPrice: String,
    val stopLoss: String,
    val target1: String,
    val target2: String,
    val riskRewardRatio: String,
    val explanationEnglish: String,
    val explanationHindi: String,
    val invalidationCriteria: String,
    val riskWarning: String,
    val timestamp: Long,
    val imagePath: String?
)
