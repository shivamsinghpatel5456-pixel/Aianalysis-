package com.example.data.sample

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.example.data.model.MovementDirection
import com.example.data.model.TradeAnalysis
import com.example.data.model.TradeSetup
import com.example.data.model.IndicatorObservation

enum class ChartScenario {
    BTC_BULLISH_BREAKOUT,
    NIFTY_SUPPORT_BOUNCE,
    EURUSD_BEARISH_BREAKDOWN,
    TSLA_SIDEWAYS_RANGE
}

data class SampleChartInfo(
    val id: String,
    val title: String,
    val subtitle: String,
    val scenario: ChartScenario,
    val expectedMovement: MovementDirection,
    val description: String
)

object SampleCharts {
    val presets = listOf(
        SampleChartInfo(
            id = "preset_btc",
            title = "BTC/USDT 4H Breakout",
            subtitle = "Bullish Pennant & Rising Volume",
            scenario = ChartScenario.BTC_BULLISH_BREAKOUT,
            expectedMovement = MovementDirection.UP,
            description = "Consolidation triangle breakout above $68,200 with bullish engulfing confirmation."
        ),
        SampleChartInfo(
            id = "preset_nifty",
            title = "NIFTY 50 Daily Support",
            subtitle = "Double Bottom & Hammer at Demand Zone",
            scenario = ChartScenario.NIFTY_SUPPORT_BOUNCE,
            expectedMovement = MovementDirection.UP,
            description = "Strong hammer candle at major 23,800 demand zone with RSI bullish divergence."
        ),
        SampleChartInfo(
            id = "preset_eurusd",
            title = "EUR/USD 1H Breakdown",
            subtitle = "Head & Shoulders Neckline Breach",
            scenario = ChartScenario.EURUSD_BEARISH_BREAKDOWN,
            expectedMovement = MovementDirection.DOWN,
            description = "Clear neckline violation at 1.0820 with strong bearish marubozu and expanding volume."
        ),
        SampleChartInfo(
            id = "preset_tsla",
            title = "TSLA 15M Sideways Range",
            subtitle = "Tight Rectangle Box Consolidation",
            scenario = ChartScenario.TSLA_SIDEWAYS_RANGE,
            expectedMovement = MovementDirection.SIDEWAYS,
            description = "Price moving inside a tight 210 - 215 channel, waiting for clear directional breakout."
        )
    )

    /**
     * Programmatically renders a high-definition realistic candlestick chart bitmap.
     * This ensures the Gemini Vision model receives a crisp, real candlestick chart to recognize!
     */
    fun generateChartBitmap(scenario: ChartScenario, width: Int = 900, height: Int = 600): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background
        val bgPaint = Paint().apply { color = Color.parseColor("#0F172A") }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Grid lines
        val gridPaint = Paint().apply {
            color = Color.parseColor("#1E293B")
            strokeWidth = 1.5f
        }
        for (x in 60 until width - 80 step 100) {
            canvas.drawLine(x.toFloat(), 40f, x.toFloat(), (height - 120).toFloat(), gridPaint)
        }
        for (y in 60 until height - 120 step 60) {
            canvas.drawLine(40f, y.toFloat(), (width - 40).toFloat(), y.toFloat(), gridPaint)
        }

        // Title and Header
        val textPaint = Paint().apply {
            isAntiAlias = true
            textSize = 28f
            color = Color.parseColor("#F8FAFC")
            isFakeBoldText = true
        }
        val subTextPaint = Paint().apply {
            isAntiAlias = true
            textSize = 18f
            color = Color.parseColor("#94A3B8")
        }

        val title = when (scenario) {
            ChartScenario.BTC_BULLISH_BREAKOUT -> "BTC/USDT • 4H • BINANCE"
            ChartScenario.NIFTY_SUPPORT_BOUNCE -> "NIFTY 50 • 1D • NSE"
            ChartScenario.EURUSD_BEARISH_BREAKDOWN -> "EUR/USD • 1H • FOREX"
            ChartScenario.TSLA_SIDEWAYS_RANGE -> "TSLA • 15M • NASDAQ"
        }
        canvas.drawText(title, 40f, 40f, textPaint)
        canvas.drawText("TradeGuru AI Vision Feed - Volume & RSI Active", 40f, 65f, subTextPaint)

        // Candlestick candles generation
        val greenPaint = Paint().apply {
            color = Color.parseColor("#10B981")
            isAntiAlias = true
        }
        val redPaint = Paint().apply {
            color = Color.parseColor("#EF4444")
            isAntiAlias = true
        }
        val wickPaint = Paint().apply {
            strokeWidth = 2.5f
            isAntiAlias = true
        }

        // Generate synthetic prices based on scenario
        val candleCount = 28
        val candleWidth = 16f
        val startX = 60f
        val stepX = (width - 160f) / candleCount

        val chartTop = 100f
        val chartBottom = height - 160f
        val chartHeight = chartBottom - chartTop

        // Simulated price series (0.0 to 1.0)
        val prices = mutableListOf<Triple<Float, Float, Pair<Float, Float>>>() // open, close, high, low

        var curr = when (scenario) {
            ChartScenario.BTC_BULLISH_BREAKOUT -> 0.4f
            ChartScenario.NIFTY_SUPPORT_BOUNCE -> 0.6f
            ChartScenario.EURUSD_BEARISH_BREAKDOWN -> 0.7f
            ChartScenario.TSLA_SIDEWAYS_RANGE -> 0.5f
        }

        for (i in 0 until candleCount) {
            val delta = when (scenario) {
                ChartScenario.BTC_BULLISH_BREAKOUT -> {
                    if (i > 18) 0.05f else (if (i % 2 == 0) 0.015f else -0.01f)
                }
                ChartScenario.NIFTY_SUPPORT_BOUNCE -> {
                    if (i < 12) -0.03f else (if (i == 13) 0.005f else 0.04f)
                }
                ChartScenario.EURUSD_BEARISH_BREAKDOWN -> {
                    if (i > 16) -0.045f else (if (i % 3 == 0) 0.02f else -0.01f)
                }
                ChartScenario.TSLA_SIDEWAYS_RANGE -> {
                    if (i % 4 < 2) 0.03f else -0.03f
                }
            }
            val open = curr
            var close = (open + delta + ((Math.random().toFloat() - 0.5f) * 0.03f)).coerceIn(0.15f, 0.9f)
            if (scenario == ChartScenario.NIFTY_SUPPORT_BOUNCE && i == 13) {
                // Pin bar / Hammer candle
                close = open + 0.01f
            }
            val high = maxOf(open, close) + (Math.random().toFloat() * 0.04f).coerceAtLeast(0.01f)
            val low = if (scenario == ChartScenario.NIFTY_SUPPORT_BOUNCE && i == 13) {
                minOf(open, close) - 0.15f // long lower shadow hammer
            } else {
                minOf(open, close) - (Math.random().toFloat() * 0.03f).coerceAtLeast(0.01f)
            }
            prices.add(Triple(open, close, Pair(high, low)))
            curr = close
        }

        // Draw Candlesticks
        for (i in 0 until candleCount) {
            val (open, close, highLow) = prices[i]
            val (high, low) = highLow
            val cx = startX + i * stepX
            val isBullish = close >= open

            val pHighY = chartBottom - high * chartHeight
            val pLowY = chartBottom - low * chartHeight
            val pOpenY = chartBottom - open * chartHeight
            val pCloseY = chartBottom - close * chartHeight

            val paint = if (isBullish) greenPaint else redPaint
            wickPaint.color = paint.color

            // Wick
            canvas.drawLine(cx, pHighY, cx, pLowY, wickPaint)

            // Body
            val topBody = minOf(pOpenY, pCloseY)
            val bottomBody = maxOf(pOpenY, pCloseY).coerceAtLeast(topBody + 3f)
            canvas.drawRect(
                cx - candleWidth / 2,
                topBody,
                cx + candleWidth / 2,
                bottomBody,
                paint
            )

            // Volume bar at bottom
            val volHeight = (Math.abs(close - open) * 400f + 20f).coerceIn(15f, 70f)
            val volPaint = Paint().apply {
                color = if (isBullish) Color.parseColor("#22C55E") else Color.parseColor("#EF4444")
                alpha = 130
            }
            canvas.drawRect(
                cx - candleWidth / 2,
                (height - 40f) - volHeight,
                cx + candleWidth / 2,
                height - 40f,
                volPaint
            )
        }

        // Draw Support/Resistance Trendlines
        val linePaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        val lineTextPaint = Paint().apply {
            textSize = 15f
            isAntiAlias = true
            isFakeBoldText = true
        }

        when (scenario) {
            ChartScenario.BTC_BULLISH_BREAKOUT -> {
                // Resistance line being broken
                linePaint.color = Color.parseColor("#EF4444")
                val resY = chartBottom - 0.65f * chartHeight
                canvas.drawLine(startX, resY, width - 60f, resY, linePaint)
                lineTextPaint.color = Color.parseColor("#EF4444")
                canvas.drawText("RESISTANCE / BREAKOUT ZONE ($68,200)", width - 360f, resY - 10f, lineTextPaint)
            }
            ChartScenario.NIFTY_SUPPORT_BOUNCE -> {
                // Major Support line
                linePaint.color = Color.parseColor("#10B981")
                val supY = chartBottom - 0.28f * chartHeight
                canvas.drawLine(startX, supY, width - 60f, supY, linePaint)
                lineTextPaint.color = Color.parseColor("#10B981")
                canvas.drawText("MAJOR DEMAND / SUPPORT ZONE (23,800)", width - 380f, supY + 22f, lineTextPaint)
            }
            ChartScenario.EURUSD_BEARISH_BREAKDOWN -> {
                // Neckline breakdown
                linePaint.color = Color.parseColor("#F59E0B")
                val neckY = chartBottom - 0.45f * chartHeight
                canvas.drawLine(startX, neckY, width - 60f, neckY, linePaint)
                lineTextPaint.color = Color.parseColor("#F59E0B")
                canvas.drawText("H&S NECKLINE BREAKDOWN (1.0820)", width - 340f, neckY - 10f, lineTextPaint)
            }
            ChartScenario.TSLA_SIDEWAYS_RANGE -> {
                // Support and Resistance channel
                linePaint.color = Color.parseColor("#38BDF8")
                val resY = chartBottom - 0.70f * chartHeight
                val supY = chartBottom - 0.35f * chartHeight
                canvas.drawLine(startX, resY, width - 60f, resY, linePaint)
                canvas.drawLine(startX, supY, width - 60f, supY, linePaint)
                lineTextPaint.color = Color.parseColor("#38BDF8")
                canvas.drawText("RANGE HIGH ($215.00)", width - 260f, resY - 10f, lineTextPaint)
                canvas.drawText("RANGE LOW ($210.00)", width - 260f, supY + 22f, lineTextPaint)
            }
        }

        return bitmap
    }

    /**
     * Fallback realistic analysis in case no API key is provided or offline.
     */
    fun getFallbackAnalysis(scenario: ChartScenario): TradeAnalysis {
        return when (scenario) {
            ChartScenario.BTC_BULLISH_BREAKOUT -> TradeAnalysis(
                assetSymbol = "BTC/USDT",
                timeframe = "4H",
                movement = MovementDirection.UP,
                confidencePercent = 84,
                primaryTrend = "Strong Bullish Uptrend with Higher Highs",
                candlestickPatterns = listOf("Bullish Pennant Breakout", "Strong Green Marubozu", "Three White Soldiers"),
                supportLevels = listOf("$67,400 (Local Support)", "$65,800 (Major Demand)"),
                resistanceLevels = listOf("$69,500 (Next Psychological)", "$71,200 (All-Time High Test)"),
                breakoutStatus = "Confirmed Bullish Breakout above $68,200 with heavy buy volume",
                indicators = listOf(
                    IndicatorObservation("RSI (14)", "64.5", "Healthy bullish momentum, not yet overbought"),
                    IndicatorObservation("MACD", "Bullish Crossover", "Histogram expanding upwards positively"),
                    IndicatorObservation("20/50 EMA", "Golden Cross", "Price trading comfortably above both EMAs")
                ),
                tradeSetup = TradeSetup(
                    entryPrice = "$68,350",
                    stopLoss = "$67,200",
                    target1 = "$70,100",
                    target2 = "$71,500",
                    riskRewardRatio = "1:2.7",
                    setupRationale = "Long entry on candle close above breakout consolidation."
                ),
                explanationEnglish = "The BTC/USDT 4H chart exhibits a textbook bullish pennant pattern following an aggressive impulse leg. The breakout candle closed cleanly above the downward sloping trendline with a 45% surge in buy volume. Both 20 and 50 period EMAs are acting as dynamic support, indicating high buyer dominance and favorable continuation toward higher targets.",
                explanationHindi = "BTC/USDT के 4-घंटे के चार्ट पर एक बहुत ही स्पष्ट 'Bullish Pennant Breakout' दिख रहा है। कीमत ने $68,200 के कड़े रेजिस्टेंस को भारी वॉल्यूम के साथ पार किया है। 20 और 50 EMA के ऊपर मजबूत हरी मोमबत्तियाँ (Candles) बन रही हैं, जिससे आगे UP (तेजी) की मूवमेंट की बहुत अधिक संभावना बनती है। स्टॉप लॉस $67,200 के नीचे रखना सुरक्षित रहेगा।",
                invalidationCriteria = "A 4H close back below $67,200 would invalidate this breakout and suggest a false break.",
                riskWarning = "Crypto markets exhibit sudden volatility during weekend sessions. Do not risk more than 1-2% of capital."
            )
            ChartScenario.NIFTY_SUPPORT_BOUNCE -> TradeAnalysis(
                assetSymbol = "NIFTY 50",
                timeframe = "Daily (1D)",
                movement = MovementDirection.UP,
                confidencePercent = 79,
                primaryTrend = "Short-term Reversal from Major Support",
                candlestickPatterns = listOf("Bullish Hammer", "Morning Star Confirmation", "Double Bottom (W Pattern)"),
                supportLevels = listOf("23,800 (Institutional Support Zone)", "23,650 (Swing Low)"),
                resistanceLevels = listOf("24,250 (Gap Fill Level)", "24,500 (200 DMA)"),
                breakoutStatus = "Rebound off historical demand zone with rejection wicks",
                indicators = listOf(
                    IndicatorObservation("RSI (14)", "34.0 (Exiting Oversold)", "Clear bullish divergence against price lows"),
                    IndicatorObservation("Stochastic", "K/D Bullish Cross", "Oversold bounce trigger confirmed"),
                    IndicatorObservation("Volume", "Climactic Buying", "Long lower wicks indicate strong dip buying")
                ),
                tradeSetup = TradeSetup(
                    entryPrice = "23,880",
                    stopLoss = "23,760",
                    target1 = "24,150",
                    target2 = "24,380",
                    riskRewardRatio = "1:2.5",
                    setupRationale = "Long reversal on hammer high breakout near demand zone."
                ),
                explanationEnglish = "NIFTY 50 touched its institutional support base around 23,800 and formed a prominent bullish hammer candlestick with a long lower shadow. This indicates strong rejection of lower price levels by smart money buyers. Accompanied by bullish divergence on RSI, odds favor a multi-day upside bounce toward the 24,250 gap resistance.",
                explanationHindi = "NIFTY 50 ने 23,800 के बड़े सपोर्ट लेवल को छूकर एक शानदार 'Bullish Hammer' कैंडल बनाया है। नीचे की लम्बी विक (Wick) यह दर्शाती है कि निचले स्तरों पर खरीदार (Buyers) तुरंत सक्रिय हो रहे हैं। RSI में भी बुलिश डायवर्जेंस है, इसलिए यहाँ से मार्केट में UP साइड बाउंस आने की ठोस उम्मीद है।",
                invalidationCriteria = "A daily close below 23,760 invalidates the demand zone bounce.",
                riskWarning = "Watch out for global market cues and macro RBI policy announcements."
            )
            ChartScenario.EURUSD_BEARISH_BREAKDOWN -> TradeAnalysis(
                assetSymbol = "EUR/USD",
                timeframe = "1H",
                movement = MovementDirection.DOWN,
                confidencePercent = 82,
                primaryTrend = "Bearish Trend Resumption",
                candlestickPatterns = listOf("Head and Shoulders Pattern", "Bearish Marubozu", "Three Black Crows"),
                supportLevels = listOf("1.0770 (Key Support)", "1.0720 (Major Target)"),
                resistanceLevels = listOf("1.0825 (Broken Neckline)", "1.0860 (Right Shoulder)"),
                breakoutStatus = "Confirmed Bearish Breakdown below 1.0820 neckline",
                indicators = listOf(
                    IndicatorObservation("RSI (14)", "38.2", "Trending downward with no divergence"),
                    IndicatorObservation("MACD", "Bearish Zero-Line Cross", "Signal lines diverging sharply lower"),
                    IndicatorObservation("EMA 20/50", "Bearish Alignment", "Fast EMA pulling away from Slow EMA")
                ),
                tradeSetup = TradeSetup(
                    entryPrice = "1.0815",
                    stopLoss = "1.0845",
                    target1 = "1.0770",
                    target2 = "1.0725",
                    riskRewardRatio = "1:3.0",
                    setupRationale = "Short trade on retest or continuation of neckline breakdown."
                ),
                explanationEnglish = "The EUR/USD 1-hour chart has completed a classic Head and Shoulders reversal structure. Price decisively violated the horizontal neckline at 1.0820 with an expansive bearish marubozu candle. The breakdown is backed by downward trending MACD and bearish moving average alignment, targeting the next support at 1.0770.",
                explanationHindi = "EUR/USD के 1H चार्ट पर साफ़ 'Head and Shoulders' पैटर्न बना है और कीमत ने 1.0820 की नेकलाइन को नीचे की तरफ तोड़ दिया है (Breakdown)। बड़ी लाल कैंडल विक्रेताओं (Sellers) का दबाव दिखा रही है। यहाँ से DOWN (मंदी) की दिशा में जाने की प्रबल संभावना है। रीटेस्ट पर शॉर्ट ट्रेड अच्छा रिस्क-रिवार्ड देता है।",
                invalidationCriteria = "Price climbing and holding back above 1.0845 negates the head and shoulders pattern.",
                riskWarning = "High-impact US CPI or ECB rate decisions can trigger sharp sudden spikes."
            )
            ChartScenario.TSLA_SIDEWAYS_RANGE -> TradeAnalysis(
                assetSymbol = "TSLA",
                timeframe = "15M",
                movement = MovementDirection.SIDEWAYS,
                confidencePercent = 76,
                primaryTrend = "Horizontal Consolidation / Non-directional",
                candlestickPatterns = listOf("Spinning Tops", "High Wave Candles", "Alternating Green/Red Range"),
                supportLevels = listOf("$210.00 (Range Low Demand)", "$208.50 (False Break Low)"),
                resistanceLevels = listOf("$215.00 (Range High Supply)", "$217.20 (Breakout Ceiling)"),
                breakoutStatus = "Bound within $210 - $215 trading box; no confirmed breakout yet",
                indicators = listOf(
                    IndicatorObservation("RSI (14)", "49.5", "Oscillating around the 50 neutral equilibrium line"),
                    IndicatorObservation("Bollinger Bands", "Squeezing / Contracting", "Volatility drying up prior to expansion"),
                    IndicatorObservation("Volume", "Drying Volume", "Low volume typical of range-bound phase")
                ),
                tradeSetup = TradeSetup(
                    entryPrice = "Wait for breakout above $215.20 or breakdown below $209.80",
                    stopLoss = "Opposite range boundary",
                    target1 = "$220.00 (Bullish Target)",
                    target2 = "$205.00 (Bearish Target)",
                    riskRewardRatio = "1:2.0",
                    setupRationale = "Avoid trading in the middle of chop; buy range low or trade the breakout."
                ),
                explanationEnglish = "TSLA is currently trapped in a narrow sideways rectangle between $210 and $215. Moving averages are completely flat and intertwined, while RSI sits right at the 50 neutral mark with Bollinger Bands contracting. Patience is recommended until a definitive breakout candle closes outside the boundary with volume expansion.",
                explanationHindi = "TSLA का चार्ट इस समय $210 और $215 के बीच एक साइडवेज (SIDEWAYS) बक्से में घूम रहा है। न तो खरीदार पूरी तरह हावी हैं और न ही विक्रेता। बीच में कोई नई पोजीशन न बनाएं। $215 के ऊपर ब्रेकआउट होने या $210 के नीचे ब्रेकडाउन होने की प्रतीक्षा करें।",
                invalidationCriteria = "A sustained 15M candle close outside the $210-$215 boundaries ends the sideways phase.",
                riskWarning = "Range chop often results in multiple stop-loss hits for trend followers."
            )
        }
    }
}
