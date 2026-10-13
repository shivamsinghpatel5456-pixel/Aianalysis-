package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.TradeGuruDatabase
import com.example.data.model.MovementDirection
import com.example.data.model.TradeAnalysis
import com.example.data.remote.GeminiVisionService
import com.example.data.repository.TradeAnalysisRepository
import com.example.data.sample.ChartScenario
import com.example.data.sample.SampleCharts
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LanguageViewMode {
    BOTH,
    ENGLISH_ONLY,
    HINDI_ONLY
}

data class TradeGuruUiState(
    val selectedBitmap: Bitmap? = null,
    val selectedImageUri: Uri? = null,
    val selectedPresetScenario: ChartScenario? = null,
    val currentAnalysis: TradeAnalysis? = null,
    val isAnalyzing: Boolean = false,
    val analysisProgressStep: String = "",
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val customApiKey: String = "",
    val showSettingsDialog: Boolean = false,
    val showHistorySheet: Boolean = false,
    val showReportDialog: Boolean = false,
    val languageViewMode: LanguageViewMode = LanguageViewMode.BOTH,
    val history: List<TradeAnalysis> = emptyList(),
    val isSavedToHistory: Boolean = false
)

class TradeGuruViewModel(application: Application) : AndroidViewModel(application) {

    private val db = TradeGuruDatabase.getDatabase(application)
    private val visionService = GeminiVisionService(application)
    val repository = TradeAnalysisRepository(application, db.analysisDao(), visionService)

    private val _uiState = MutableStateFlow(TradeGuruUiState())
    val uiState: StateFlow<TradeGuruUiState> = _uiState.asStateFlow()

    private val prefs = application.getSharedPreferences("tradeguru_prefs", Application.MODE_PRIVATE)

    init {
        val savedKey = prefs.getString("custom_gemini_api_key", "") ?: ""
        _uiState.update { it.copy(customApiKey = savedKey) }

        // Observe Room History
        viewModelScope.launch {
            repository.historyFlow.collect { list ->
                _uiState.update { it.copy(history = list) }
            }
        }

        // Preload default preset so the user sees a ready chart on launch!
        selectPreset(SampleCharts.presets.first().scenario)
    }

    fun selectPreset(scenario: ChartScenario) {
        val bitmap = SampleCharts.generateChartBitmap(scenario)
        _uiState.update {
            it.copy(
                selectedBitmap = bitmap,
                selectedImageUri = null,
                selectedPresetScenario = scenario,
                currentAnalysis = null,
                errorMessage = null,
                isSavedToHistory = false
            )
        }
    }

    fun onImageSelectedFromPicker(uri: Uri) {
        val bitmap = visionService.loadBitmapFromUri(uri)
        if (bitmap != null) {
            _uiState.update {
                it.copy(
                    selectedBitmap = bitmap,
                    selectedImageUri = uri,
                    selectedPresetScenario = null,
                    currentAnalysis = null,
                    errorMessage = null,
                    isSavedToHistory = false
                )
            }
        } else {
            _uiState.update { it.copy(errorMessage = "Could not load selected image.") }
        }
    }

    fun analyzeCurrentChart() {
        val bitmap = _uiState.value.selectedBitmap ?: return
        val customKey = _uiState.value.customApiKey
        val preset = _uiState.value.selectedPresetScenario

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAnalyzing = true,
                    errorMessage = null,
                    currentAnalysis = null,
                    analysisProgressStep = "Connecting to Gemini 3.1 Pro Vision..."
                )
            }

            // Animate progress steps for great UX
            val progressJob = launch {
                val steps = listOf(
                    "Connecting to Gemini 3.1 Pro Vision...",
                    "Scanning Candlestick Patterns & Price Action...",
                    "Detecting Support & Resistance Key Zones...",
                    "Calculating Entry, Stop-Loss & Targets...",
                    "Translating Technical Reasoning to Hindi & English..."
                )
                for (step in steps) {
                    _uiState.update { it.copy(analysisProgressStep = step) }
                    delay(700)
                }
            }

            val result = repository.analyzeChart(bitmap, customKey, preset)
            progressJob.cancel()

            result.onSuccess { analysis ->
                _uiState.update {
                    it.copy(
                        isAnalyzing = false,
                        currentAnalysis = analysis,
                        analysisProgressStep = "",
                        errorMessage = null,
                        isSavedToHistory = false
                    )
                }
                // Auto-save to local database for seamless history
                saveCurrentAnalysisToHistory(silent = true)
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isAnalyzing = false,
                        analysisProgressStep = "",
                        errorMessage = err.message ?: "An unexpected error occurred during vision analysis."
                    )
                }
            }
        }
    }

    fun saveCurrentAnalysisToHistory(silent: Boolean = false) {
        val analysis = _uiState.value.currentAnalysis ?: return
        val bitmap = _uiState.value.selectedBitmap
        viewModelScope.launch {
            repository.saveAnalysis(analysis, bitmap)
            _uiState.update {
                it.copy(
                    isSavedToHistory = true,
                    infoMessage = if (!silent) "Analysis saved to History!" else null
                )
            }
        }
    }

    fun loadAnalysisFromHistory(analysis: TradeAnalysis) {
        val bitmap = repository.loadSavedBitmap(analysis.imageUri)
        _uiState.update {
            it.copy(
                currentAnalysis = analysis,
                selectedBitmap = bitmap ?: it.selectedBitmap,
                selectedPresetScenario = null,
                showHistorySheet = false,
                isSavedToHistory = true,
                errorMessage = null
            )
        }
    }

    fun deleteHistoryItem(id: String) {
        viewModelScope.launch {
            repository.deleteAnalysis(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
            _uiState.update { it.copy(infoMessage = "History cleared.") }
        }
    }

    fun setCustomApiKey(key: String) {
        prefs.edit().putString("custom_gemini_api_key", key.trim()).apply()
        _uiState.update { it.copy(customApiKey = key.trim(), showSettingsDialog = false) }
    }

    fun setLanguageMode(mode: LanguageViewMode) {
        _uiState.update { it.copy(languageViewMode = mode) }
    }

    fun toggleSettingsDialog(show: Boolean) {
        _uiState.update { it.copy(showSettingsDialog = show) }
    }

    fun toggleHistorySheet(show: Boolean) {
        _uiState.update { it.copy(showHistorySheet = show) }
    }

    fun toggleReportDialog(show: Boolean) {
        _uiState.update { it.copy(showReportDialog = show) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }
}
