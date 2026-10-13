package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.data.local.AnalysisDao
import com.example.data.local.AnalysisHistoryEntity
import com.example.data.model.IndicatorObservation
import com.example.data.model.MovementDirection
import com.example.data.model.TradeAnalysis
import com.example.data.model.TradeSetup
import com.example.data.remote.GeminiVisionService
import com.example.data.sample.ChartScenario
import com.example.data.sample.SampleCharts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream

class TradeAnalysisRepository(
    private val context: Context,
    private val dao: AnalysisDao,
    private val visionService: GeminiVisionService
) {
    val historyFlow: Flow<List<TradeAnalysis>> = dao.getAllHistory().map { list ->
        list.map { entity -> mapEntityToAnalysis(entity) }
    }

    suspend fun analyzeChart(
        bitmap: Bitmap,
        customApiKey: String? = null,
        presetScenario: ChartScenario? = null
    ): Result<TradeAnalysis> = withContext(Dispatchers.IO) {
        val activeKey = visionService.getActiveApiKey(customApiKey)

        if (activeKey.isNotBlank()) {
            // Attempt Gemini Vision first
            val result = visionService.analyzeChartImage(bitmap, customApiKey)
            if (result.isSuccess) {
                return@withContext result
            }
            // If failed due to network / quota and a preset was used, provide intelligent fallback
            if (presetScenario != null) {
                val fallback = SampleCharts.getFallbackAnalysis(presetScenario)
                return@withContext Result.success(fallback)
            }
            return@withContext result
        } else {
            // If no API key configured and user selected a preset, return realistic simulation
            if (presetScenario != null) {
                return@withContext Result.success(SampleCharts.getFallbackAnalysis(presetScenario))
            } else {
                return@withContext Result.failure(
                    IllegalStateException(
                        "No Gemini API Key found. Configure GEMINI_API_KEY in the Secrets panel, or tap Settings to enter your key. You can also try preset demo charts anytime!"
                    )
                )
            }
        }
    }

    suspend fun saveAnalysis(analysis: TradeAnalysis, bitmap: Bitmap?): String = withContext(Dispatchers.IO) {
        var savedPath: String? = null
        if (bitmap != null) {
            try {
                val filename = "chart_${analysis.id}.jpg"
                val file = File(context.filesDir, filename)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                savedPath = file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val patternsJson = JSONArray(analysis.candlestickPatterns).toString()
        val supportJson = JSONArray(analysis.supportLevels).toString()
        val resistanceJson = JSONArray(analysis.resistanceLevels).toString()

        val entity = AnalysisHistoryEntity(
            id = analysis.id,
            assetSymbol = analysis.assetSymbol,
            movement = analysis.movement.name,
            confidencePercent = analysis.confidencePercent,
            primaryTrend = analysis.primaryTrend,
            candlestickPatternsJson = patternsJson,
            supportLevelsJson = supportJson,
            resistanceLevelsJson = resistanceJson,
            breakoutStatus = analysis.breakoutStatus,
            entryPrice = analysis.tradeSetup.entryPrice,
            stopLoss = analysis.tradeSetup.stopLoss,
            target1 = analysis.tradeSetup.target1,
            target2 = analysis.tradeSetup.target2,
            riskRewardRatio = analysis.tradeSetup.riskRewardRatio,
            explanationEnglish = analysis.explanationEnglish,
            explanationHindi = analysis.explanationHindi,
            invalidationCriteria = analysis.invalidationCriteria,
            riskWarning = analysis.riskWarning,
            timestamp = analysis.timestamp,
            imagePath = savedPath
        )
        dao.insertAnalysis(entity)
        analysis.id
    }

    suspend fun deleteAnalysis(id: String) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
        try {
            val file = File(context.filesDir, "chart_$id.jpg")
            if (file.exists()) file.delete()
        } catch (_: Exception) {}
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        dao.clearAll()
    }

    fun loadSavedBitmap(imagePath: String?): Bitmap? {
        if (imagePath.isNullOrBlank()) return null
        return try {
            val file = File(imagePath)
            if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
        } catch (e: Exception) {
            null
        }
    }

    private fun mapEntityToAnalysis(entity: AnalysisHistoryEntity): TradeAnalysis {
        val patterns = parseJsonArray(entity.candlestickPatternsJson)
        val support = parseJsonArray(entity.supportLevelsJson)
        val resistance = parseJsonArray(entity.resistanceLevelsJson)

        val movement = try {
            MovementDirection.valueOf(entity.movement)
        } catch (_: Exception) {
            MovementDirection.UP
        }

        return TradeAnalysis(
            id = entity.id,
            assetSymbol = entity.assetSymbol,
            movement = movement,
            confidencePercent = entity.confidencePercent,
            primaryTrend = entity.primaryTrend,
            candlestickPatterns = patterns,
            supportLevels = support,
            resistanceLevels = resistance,
            breakoutStatus = entity.breakoutStatus,
            tradeSetup = TradeSetup(
                entryPrice = entity.entryPrice,
                stopLoss = entity.stopLoss,
                target1 = entity.target1,
                target2 = entity.target2,
                riskRewardRatio = entity.riskRewardRatio
            ),
            explanationEnglish = entity.explanationEnglish,
            explanationHindi = entity.explanationHindi,
            invalidationCriteria = entity.invalidationCriteria,
            riskWarning = entity.riskWarning,
            timestamp = entity.timestamp,
            imageUri = entity.imagePath
        )
    }

    private fun parseJsonArray(jsonStr: String): List<String> {
        val list = mutableListOf<String>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                list.add(arr.getString(i))
            }
        } catch (_: Exception) {}
        return list
    }

    fun formatReportText(analysis: TradeAnalysis): String {
        val dirEmoji = when (analysis.movement) {
            MovementDirection.UP -> "🟢 BULLISH / UP"
            MovementDirection.DOWN -> "🔴 BEARISH / DOWN"
            MovementDirection.SIDEWAYS -> "🟡 NEUTRAL / SIDEWAYS"
        }

        return """
╔═══════════════════════════════════════════════╗
   TradeGuru AI — Vision Chart Analysis Report
╚═══════════════════════════════════════════════╝
📊 Asset: ${analysis.assetSymbol} (${analysis.timeframe})
🎯 Predicted Direction: $dirEmoji
⚡ Confidence Score: ${analysis.confidencePercent}%
📈 Primary Trend: ${analysis.primaryTrend}
🔍 Candlestick Patterns: ${analysis.candlestickPatterns.joinToString(", ")}

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🎯 TRADE SETUP & LEVELS:
• Entry Zone: ${analysis.tradeSetup.entryPrice}
• Stop-Loss (SL): ${analysis.tradeSetup.stopLoss}
• Target 1: ${analysis.tradeSetup.target1}
• Target 2: ${analysis.tradeSetup.target2}
• Risk-to-Reward: ${analysis.tradeSetup.riskRewardRatio}
• Breakout Status: ${analysis.breakoutStatus}

🛡️ Key Support: ${analysis.supportLevels.joinToString(" | ")}
🧱 Key Resistance: ${analysis.resistanceLevels.joinToString(" | ")}

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📖 ENGLISH TECHNICAL RATIONALE:
${analysis.explanationEnglish}

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🇮🇳 सरल हिंदी में कारण व विश्लेषण:
${analysis.explanationHindi}

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
⚠️ INVALIDATION & RISK WARNING:
• Invalidation: ${analysis.invalidationCriteria}
• Risk Caveat: ${analysis.riskWarning}

⚠️ DISCLAIMER: For educational purposes only. Financial markets involve substantial risk. Never trade without proper risk management.
        """.trimIndent()
    }
}
