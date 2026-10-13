package com.example.data.model

data class IndicatorObservation(
    val name: String,
    val valueOrStatus: String,
    val interpretation: String
)

data class TradeSetup(
    val entryPrice: String = "N/A",
    val stopLoss: String = "N/A",
    val target1: String = "N/A",
    val target2: String = "N/A",
    val riskRewardRatio: String = "1:2",
    val setupRationale: String = ""
)

enum class MovementDirection {
    UP,
    DOWN,
    SIDEWAYS
}

data class TradeAnalysis(
    val id: String = java.util.UUID.randomUUID().toString(),
    val assetSymbol: String = "Asset / Unknown",
    val timeframe: String = "N/A",
    val movement: MovementDirection = MovementDirection.UP,
    val confidencePercent: Int = 75,
    val primaryTrend: String = "Uptrend",
    val candlestickPatterns: List<String> = emptyList(),
    val supportLevels: List<String> = emptyList(),
    val resistanceLevels: List<String> = emptyList(),
    val breakoutStatus: String = "Consolidating near key level",
    val indicators: List<IndicatorObservation> = emptyList(),
    val tradeSetup: TradeSetup = TradeSetup(),
    val explanationEnglish: String = "",
    val explanationHindi: String = "",
    val invalidationCriteria: String = "",
    val riskWarning: String = "High volatility expected. Always use stop-loss.",
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String? = null
)
