package com.example.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.BuildConfig
import com.example.data.model.IndicatorObservation
import com.example.data.model.MovementDirection
import com.example.data.model.TradeAnalysis
import com.example.data.model.TradeSetup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

class GeminiVisionService(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(75, TimeUnit.SECONDS)
        .readTimeout(75, TimeUnit.SECONDS)
        .writeTimeout(75, TimeUnit.SECONDS)
        .build()

    // Model mandated by prompt: gemini-3.1-pro-preview
    val modelName = "gemini-3.1-pro-preview"

    fun getActiveApiKey(customKey: String?): String {
        if (!customKey.isNullOrBlank()) return customKey.trim()
        val envKey = BuildConfig.GEMINI_API_KEY
        if (envKey.isNotBlank() && envKey != "MY_GEMINI_API_KEY") {
            return envKey
        }
        return ""
    }

    suspend fun analyzeChartImage(
        bitmap: Bitmap,
        customApiKey: String? = null
    ): Result<TradeAnalysis> = withContext(Dispatchers.IO) {
        val apiKey = getActiveApiKey(customApiKey)
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException(
                    "Gemini API key is not configured. Please add your GEMINI_API_KEY in the AI Studio Secrets panel or enter your key in App Settings."
                )
            )
        }

        try {
            // Compress bitmap to Base64
            val base64Image = bitmapToBase64(bitmap)

            val prompt = """
You are TradeGuru AI, a master technical financial analyst and chartered market technician.
Analyze this trading chart screenshot in detail.

Your task is to identify:
1. Primary Trend (Uptrend, Downtrend, Sideways range)
2. Candlestick Patterns (e.g. Bullish Engulfing, Hammer, Morning Star, Shooting Star, Doji, Marubozu, Head and Shoulders, Double Bottom, etc.)
3. Key Support and Resistance price levels/zones visible
4. Breakout or Breakdown status (Confirmed breakout, retest, false break risk, or range-bound)
5. Visible Indicators (RSI levels/divergence, MACD lines/crossover, Moving Averages EMA 20/50/200, Volume trends, Bollinger Bands)
6. Directional Bias: UP (Bullish), DOWN (Bearish), or SIDEWAYS (Neutral)
7. Confidence percentage (e.g. 60-95%)
8. Trade Setup when sufficient data exists: Entry, Stop-Loss, Target 1, Target 2, Risk-to-Reward ratio
9. EXPLANATION IN SIMPLE ENGLISH: Explain every technical reason clearly and simply.
10. EXPLANATION IN SIMPLE HINDI (सरल हिंदी): एक्सप्लेन आल रीजन्स इन सिंपल हिंदी लैंग्वेज (e.g. "यहाँ चार्ट पर क्या बन रहा है, क्यों यह ऊपर या नीचे जा सकता है, स्टॉप लॉस कहाँ होना चाहिए").
11. Realistic Invalidation Criteria and Risk Warning (Never guarantee predictions, state realistic market volatility caveats).

Respond ONLY with a valid JSON object matching this schema with no extra text or markdown wrapper:
{
  "assetSymbol": "string (e.g. BTC/USDT, NIFTY 50, EUR/USD or Detected Chart)",
  "timeframe": "string (e.g. 15m, 1H, 4H, 1D, or Unknown)",
  "movement": "UP" or "DOWN" or "SIDEWAYS",
  "confidencePercent": integer (50 to 95),
  "primaryTrend": "string",
  "candlestickPatterns": ["string", "string"],
  "supportLevels": ["string", "string"],
  "resistanceLevels": ["string", "string"],
  "breakoutStatus": "string",
  "indicators": [
    {
      "name": "string (e.g. RSI)",
      "valueOrStatus": "string (e.g. 45 - Neutral)",
      "interpretation": "string"
    }
  ],
  "tradeSetup": {
    "entryPrice": "string",
    "stopLoss": "string",
    "target1": "string",
    "target2": "string",
    "riskRewardRatio": "string (e.g. 1:2.5)",
    "setupRationale": "string"
  },
  "explanationEnglish": "string (Comprehensive explanation in clear English)",
  "explanationHindi": "string (सरल और आसान हिंदी में पूरा विश्लेषण और कारण)",
  "invalidationCriteria": "string",
  "riskWarning": "string"
}
""".trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray()
                val contentObj = JSONObject()
                val partsArray = JSONArray()

                // Text prompt part
                val textPart = JSONObject().apply {
                    put("text", prompt)
                }
                partsArray.put(textPart)

                // Image inlineData part
                val imagePart = JSONObject().apply {
                    val inlineDataObj = JSONObject().apply {
                        put("mimeType", "image/jpeg")
                        put("data", base64Image)
                    }
                    put("inlineData", inlineDataObj)
                }
                partsArray.put(imagePart)

                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
                put("contents", contentsArray)

                // Generation config
                val genConfig = JSONObject().apply {
                    put("temperature", 0.2)
                    put("topP", 0.8)
                    put("responseMimeType", "application/json")
                }
                put("generationConfig", genConfig)
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = try {
                    val errJson = JSONObject(responseBody)
                    val errorObj = errJson.optJSONObject("error")
                    errorObj?.optString("message") ?: "API Error ${response.code}"
                } catch (e: Exception) {
                    "HTTP ${response.code}: $responseBody"
                }
                return@withContext Result.failure(Exception(errorMsg))
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext Result.failure(Exception("No analysis generated from Gemini Vision."))
            }

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val rawText = parts?.optJSONObject(0)?.optString("text") ?: ""

            val cleanedJsonText = cleanJsonString(rawText)
            val parsedAnalysis = parseTradeAnalysisJson(cleanedJsonText)
            Result.success(parsedAnalysis)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun cleanJsonString(text: String): String {
        var trimmed = text.trim()
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.removePrefix("```json").trim()
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.removePrefix("```").trim()
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.removeSuffix("```").trim()
        }
        return trimmed
    }

    private fun parseTradeAnalysisJson(jsonString: String): TradeAnalysis {
        val obj = JSONObject(jsonString)

        val assetSymbol = obj.optString("assetSymbol", "Analyzed Chart")
        val timeframe = obj.optString("timeframe", "N/A")
        val movementStr = obj.optString("movement", "UP").uppercase()
        val movement = when (movementStr) {
            "DOWN", "BEARISH", "BEAR" -> MovementDirection.DOWN
            "SIDEWAYS", "NEUTRAL", "RANGE" -> MovementDirection.SIDEWAYS
            else -> MovementDirection.UP
        }
        val confidencePercent = obj.optInt("confidencePercent", 75).coerceIn(40, 99)
        val primaryTrend = obj.optString("primaryTrend", "Neutral Trend")

        val patternsList = mutableListOf<String>()
        obj.optJSONArray("candlestickPatterns")?.let { arr ->
            for (i in 0 until arr.length()) {
                patternsList.add(arr.optString(i))
            }
        }

        val supportList = mutableListOf<String>()
        obj.optJSONArray("supportLevels")?.let { arr ->
            for (i in 0 until arr.length()) {
                supportList.add(arr.optString(i))
            }
        }

        val resistanceList = mutableListOf<String>()
        obj.optJSONArray("resistanceLevels")?.let { arr ->
            for (i in 0 until arr.length()) {
                resistanceList.add(arr.optString(i))
            }
        }

        val breakoutStatus = obj.optString("breakoutStatus", "Consolidating near key level")

        val indicatorsList = mutableListOf<IndicatorObservation>()
        obj.optJSONArray("indicators")?.let { arr ->
            for (i in 0 until arr.length()) {
                val indObj = arr.optJSONObject(i)
                if (indObj != null) {
                    indicatorsList.add(
                        IndicatorObservation(
                            name = indObj.optString("name", "Indicator"),
                            valueOrStatus = indObj.optString("valueOrStatus", "Active"),
                            interpretation = indObj.optString("interpretation", "")
                        )
                    )
                }
            }
        }

        val tradeSetupObj = obj.optJSONObject("tradeSetup")
        val tradeSetup = if (tradeSetupObj != null) {
            TradeSetup(
                entryPrice = tradeSetupObj.optString("entryPrice", "N/A"),
                stopLoss = tradeSetupObj.optString("stopLoss", "N/A"),
                target1 = tradeSetupObj.optString("target1", "N/A"),
                target2 = tradeSetupObj.optString("target2", "N/A"),
                riskRewardRatio = tradeSetupObj.optString("riskRewardRatio", "1:2"),
                setupRationale = tradeSetupObj.optString("setupRationale", "")
            )
        } else {
            TradeSetup()
        }

        val explanationEnglish = obj.optString("explanationEnglish", "Analysis complete based on chart technicals.")
        val explanationHindi = obj.optString("explanationHindi", "चार्ट पर तकनीकी विश्लेषण पूरा हुआ।")
        val invalidationCriteria = obj.optString("invalidationCriteria", "Movement invalidates if key support/resistance fails.")
        val riskWarning = obj.optString("riskWarning", "Trading carries risk. Always use strict stop-loss.")

        return TradeAnalysis(
            assetSymbol = assetSymbol,
            timeframe = timeframe,
            movement = movement,
            confidencePercent = confidencePercent,
            primaryTrend = primaryTrend,
            candlestickPatterns = patternsList,
            supportLevels = supportList,
            resistanceLevels = resistanceList,
            breakoutStatus = breakoutStatus,
            indicators = indicatorsList,
            tradeSetup = tradeSetup,
            explanationEnglish = explanationEnglish,
            explanationHindi = explanationHindi,
            invalidationCriteria = invalidationCriteria,
            riskWarning = riskWarning
        )
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val maxDim = 1200
        val scaledBitmap = if (bitmap.width > maxDim || bitmap.height > maxDim) {
            val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val newW = if (ratio > 1) maxDim else (maxDim * ratio).toInt()
            val newH = if (ratio > 1) (maxDim / ratio).toInt() else maxDim
            Bitmap.createScaledBitmap(bitmap, newW, newH, true)
        } else {
            bitmap
        }

        val stream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }
}
