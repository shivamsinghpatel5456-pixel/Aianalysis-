package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MovementDirection
import com.example.data.model.TradeAnalysis
import com.example.data.sample.SampleCharts
import com.example.ui.components.ConfidenceMeter
import com.example.ui.components.DirectionBadge
import com.example.ui.components.LevelChip
import com.example.ui.dialogs.HistoryBottomSheet
import com.example.ui.dialogs.ReportDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.BullishGreenBright
import com.example.ui.theme.NeutralYellow
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LanguageViewMode
import com.example.ui.viewmodel.TradeGuruViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(viewModel: TradeGuruViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.onImageSelectedFromPicker(uri)
        }
    }

    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BullishGreen.copy(alpha = 0.5f)),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = BullishGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "TradeGuru",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    color = BullishGreen,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "AI",
                                        color = Color.Black,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Gemini 3.1 Pro Vision",
                                color = AccentCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                actions = {
                    // History Icon with count badge
                    IconButton(
                        onClick = { viewModel.toggleHistorySheet(true) },
                        modifier = Modifier.testTag("history_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (uiState.history.isNotEmpty()) {
                                    Badge(
                                        containerColor = BullishGreen,
                                        contentColor = Color.Black
                                    ) {
                                        Text(uiState.history.size.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = TextPrimary
                            )
                        }
                    }

                    // Settings Icon
                    IconButton(
                        onClick = { viewModel.toggleSettingsDialog(true) },
                        modifier = Modifier.testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Hero / Model Status Banner
            item {
                Surface(
                    color = SurfaceDarkElevated,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box {
                        // Decorative gradient glow
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(BullishGreen, AccentCyan, AccentPurple)
                                    )
                                )
                        )
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "AI Technical Chart Vision",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Candlesticks • Support/Resistance • UP/DOWN Predictions • Hindi & English",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Surface(
                                color = BullishGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BullishGreen.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = BullishGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Vision Ready",
                                        color = BullishGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Preset Charts Row
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Or Test Realistic Preset Charts:",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "1-Tap Demo",
                            color = AccentCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(SampleCharts.presets) { preset ->
                            val isSelected = uiState.selectedPresetScenario == preset.scenario
                            Surface(
                                color = if (isSelected) SurfaceDarkElevated else SurfaceDark,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) BullishGreen else SurfaceBorder
                                ),
                                modifier = Modifier
                                    .clickable { viewModel.selectPreset(preset.scenario) }
                                    .testTag("preset_${preset.id}")
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        DirectionBadge(direction = preset.expectedMovement)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = preset.title,
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = preset.subtitle,
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Chart Upload & Preview Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Chart Screenshot Feed",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Upload from Gallery Button (Android Photo Picker)
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentCyan),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.5f)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("upload_screenshot_button")
                            ) {
                                Icon(
                                    Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload Chart", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Chart Bitmap Display
                        if (uiState.selectedBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(230.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                            ) {
                                Image(
                                    bitmap = uiState.selectedBitmap!!.asImageBitmap(),
                                    contentDescription = "Selected trading chart",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )

                                // Analyzing scanner overlay animation
                                if (uiState.isAnalyzing) {
                                    val transition = rememberInfiniteTransition(label = "scan")
                                    val scanPosition by transition.animateFloat(
                                        initialValue = 0f,
                                        targetValue = 1f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(1500, easing = LinearEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "scanPos"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.45f))
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .align(Alignment.TopCenter)
                                            .padding(top = (scanPosition * 220).dp)
                                            .background(BullishGreen)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Primary Action: Analyze Button
                        if (uiState.isAnalyzing) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = BullishGreen,
                                    modifier = Modifier.size(32.dp),
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.analysisProgressStep,
                                    color = AccentCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Button(
                                onClick = { viewModel.analyzeCurrentChart() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BullishGreen,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("analyze_chart_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Analyze Chart with Gemini Vision",
                                    color = Color.Black,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }

            // Error Message Card (if any)
            if (uiState.errorMessage != null) {
                item {
                    Surface(
                        color = Color(0xFF2A1215),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BearishRed.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = BearishRed, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analysis Issue", color = BearishRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = uiState.errorMessage!!,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.toggleSettingsDialog(true) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentCyan)
                                ) {
                                    Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Open Settings", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { viewModel.analyzeCurrentChart() },
                                    colors = ButtonDefaults.buttonColors(containerColor = BearishRed)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Retry", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Active Analysis Results Card
            if (uiState.currentAnalysis != null) {
                val analysis = uiState.currentAnalysis!!

                // 1. Primary Bias & Direction Hero Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            when (analysis.movement) {
                                MovementDirection.UP -> BullishGreen.copy(alpha = 0.6f)
                                MovementDirection.DOWN -> BearishRed.copy(alpha = 0.6f)
                                MovementDirection.SIDEWAYS -> NeutralYellow.copy(alpha = 0.6f)
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("analysis_result_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = analysis.assetSymbol,
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "Timeframe: ${analysis.timeframe}",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                                DirectionBadge(direction = analysis.movement, large = true)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            ConfidenceMeter(percent = analysis.confidencePercent)

                            Spacer(modifier = Modifier.height(14.dp))

                            // Primary Trend Pill
                            Surface(
                                color = SurfaceDarkElevated,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.ShowChart,
                                        contentDescription = null,
                                        tint = AccentCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Primary Trend: ",
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = analysis.primaryTrend,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Trade Setup (Entry, Stop Loss, Targets, R:R)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = NeutralYellow, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Trade Setup & Price Targets",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                LevelChip(label = "Entry Zone", value = analysis.tradeSetup.entryPrice, color = AccentCyan)
                                LevelChip(label = "Stop-Loss (SL)", value = analysis.tradeSetup.stopLoss, color = BearishRed)
                                LevelChip(label = "Target 1 (T1)", value = analysis.tradeSetup.target1, color = BullishGreen)
                                LevelChip(label = "Target 2 (T2)", value = analysis.tradeSetup.target2, color = BullishGreenBright)
                                LevelChip(label = "Risk-to-Reward", value = analysis.tradeSetup.riskRewardRatio, color = AccentPurple)
                            }

                            if (analysis.tradeSetup.setupRationale.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Setup Note: ${analysis.tradeSetup.setupRationale}",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // 3. Technical Patterns, Breakouts & Key Levels
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Technical Patterns & Zones",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Candlestick Patterns
                            Text(
                                text = "Candlestick Patterns Identified:",
                                color = TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                analysis.candlestickPatterns.forEach { pat ->
                                    Surface(
                                        color = SurfaceDarkElevated,
                                        shape = RoundedCornerShape(6.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BullishGreen.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = pat,
                                            color = BullishGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Support & Resistance
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Support
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Support Zones", color = BullishGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    analysis.supportLevels.forEach { lvl ->
                                        Text("• $lvl", color = TextPrimary, fontSize = 12.sp)
                                    }
                                }
                                // Resistance
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Resistance Zones", color = BearishRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    analysis.resistanceLevels.forEach { lvl ->
                                        Text("• $lvl", color = TextPrimary, fontSize = 12.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Breakout status
                            Text(
                                text = "Breakout Status: ${analysis.breakoutStatus}",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // 4. Visible Indicators (RSI, MACD, Volume, etc.)
                if (analysis.indicators.isNotEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Visible Indicators Recognized",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    analysis.indicators.forEach { ind ->
                                        Surface(
                                            color = SurfaceDarkElevated,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = ind.name,
                                                        color = AccentCyan,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                    if (ind.interpretation.isNotBlank()) {
                                                        Text(
                                                            text = ind.interpretation,
                                                            color = TextSecondary,
                                                            fontSize = 11.sp
                                                        )
                                                    }
                                                }
                                                Surface(
                                                    color = Color(0xFF0F172A),
                                                    shape = RoundedCornerShape(6.dp),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                                                ) {
                                                    Text(
                                                        text = ind.valueOrStatus,
                                                        color = TextPrimary,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Bilingual Explanation in English & Simple Hindi
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Language, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Reasoning & Explanation",
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Mode Toggle
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    FilterChip(
                                        selected = uiState.languageViewMode == LanguageViewMode.BOTH,
                                        onClick = { viewModel.setLanguageMode(LanguageViewMode.BOTH) },
                                        label = { Text("Both", fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = AccentCyan,
                                            selectedLabelColor = Color.Black
                                        )
                                    )
                                    FilterChip(
                                        selected = uiState.languageViewMode == LanguageViewMode.HINDI_ONLY,
                                        onClick = { viewModel.setLanguageMode(LanguageViewMode.HINDI_ONLY) },
                                        label = { Text("हिंदी", fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BullishGreen,
                                            selectedLabelColor = Color.Black
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // English Section
                            if (uiState.languageViewMode == LanguageViewMode.BOTH || uiState.languageViewMode == LanguageViewMode.ENGLISH_ONLY) {
                                Surface(
                                    color = SurfaceDarkElevated,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "🇬🇧 Technical Rationale (English)",
                                            color = AccentCyan,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = analysis.explanationEnglish,
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            lineHeight = 19.sp
                                        )
                                    }
                                }
                            }

                            if (uiState.languageViewMode == LanguageViewMode.BOTH) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            // Hindi Section
                            if (uiState.languageViewMode == LanguageViewMode.BOTH || uiState.languageViewMode == LanguageViewMode.HINDI_ONLY) {
                                Surface(
                                    color = Color(0xFF14241B),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BullishGreen.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "🇮🇳 सरल हिंदी में कारण व व्याख्या (Hindi)",
                                            color = BullishGreen,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = analysis.explanationHindi,
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 6. Invalidation, Realistic Scenarios & Risk Warnings
                item {
                    Surface(
                        color = Color(0xFF1A1810),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeutralYellow.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = NeutralYellow, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Invalidation & Risk Management",
                                    color = NeutralYellow,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "• Invalidation Level: ${analysis.invalidationCriteria}",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Risk Caveat: ${analysis.riskWarning}",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "⚠️ DISCLAIMER: TradeGuru AI provides mathematical & chart pattern recognition for education only. Financial markets entail high financial risk. Never trade capital you cannot afford to lose.",
                                color = TextMuted,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                // 7. Action Bar: Download/Share Report & Save to History
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.saveCurrentAnalysisToHistory(silent = false)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (uiState.isSavedToHistory) BullishGreen else AccentCyan
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                (if (uiState.isSavedToHistory) BullishGreen else AccentCyan).copy(alpha = 0.6f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("save_history_button")
                        ) {
                            Icon(
                                imageVector = if (uiState.isSavedToHistory) Icons.Default.Check else Icons.Default.Bookmark,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isSavedToHistory) "Saved to DB" else "Save Analysis",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleReportDialog(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("download_report_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Export Report",
                                color = Color.Black,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Settings Dialog
    if (uiState.showSettingsDialog) {
        SettingsDialog(
            currentApiKey = uiState.customApiKey,
            onSaveApiKey = { viewModel.setCustomApiKey(it) },
            onClearHistory = { viewModel.clearAllHistory() },
            onDismiss = { viewModel.toggleSettingsDialog(false) }
        )
    }

    // History Bottom Sheet
    if (uiState.showHistorySheet) {
        HistoryBottomSheet(
            historyList = uiState.history,
            onSelectAnalysis = { viewModel.loadAnalysisFromHistory(it) },
            onDeleteItem = { viewModel.deleteHistoryItem(it) },
            onDismiss = { viewModel.toggleHistorySheet(false) }
        )
    }

    // Report Dialog
    if (uiState.showReportDialog && uiState.currentAnalysis != null) {
        val reportText = viewModel.repository.formatReportText(uiState.currentAnalysis!!)
        ReportDialog(
            reportText = reportText,
            analysis = uiState.currentAnalysis!!,
            onDismiss = { viewModel.toggleReportDialog(false) }
        )
    }
}
