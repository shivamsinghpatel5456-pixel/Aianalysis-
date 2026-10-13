package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.MovementDirection
import com.example.data.sample.ChartScenario
import com.example.data.sample.SampleCharts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches TradeGuru AI`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TradeGuru AI", appName)
    }

    @Test
    fun `sample charts generator produces valid bitmap and fallback analysis`() {
        val bitmap = SampleCharts.generateChartBitmap(ChartScenario.BTC_BULLISH_BREAKOUT)
        assertNotNull(bitmap)
        assertTrue(bitmap.width > 0 && bitmap.height > 0)

        val analysis = SampleCharts.getFallbackAnalysis(ChartScenario.BTC_BULLISH_BREAKOUT)
        assertEquals(MovementDirection.UP, analysis.movement)
        assertTrue(analysis.confidencePercent >= 70)
        assertTrue(analysis.explanationHindi.isNotBlank())
        assertTrue(analysis.explanationEnglish.isNotBlank())
        assertTrue(analysis.candlestickPatterns.isNotEmpty())
    }
}
