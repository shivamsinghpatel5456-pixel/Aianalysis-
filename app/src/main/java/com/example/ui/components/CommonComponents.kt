package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MovementDirection
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BearishRedBg
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.BullishGreenBg
import com.example.ui.theme.NeutralYellow
import com.example.ui.theme.NeutralYellowBg
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDarkElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DirectionBadge(
    direction: MovementDirection,
    modifier: Modifier = Modifier,
    large: Boolean = false
) {
    val (label, icon, color, bg) = when (direction) {
        MovementDirection.UP -> Quadruple("UP (BULLISH)", Icons.Default.ArrowUpward, BullishGreen, BullishGreenBg)
        MovementDirection.DOWN -> Quadruple("DOWN (BEARISH)", Icons.Default.ArrowDownward, BearishRed, BearishRedBg)
        MovementDirection.SIDEWAYS -> Quadruple("SIDEWAYS (NEUTRAL)", Icons.Default.ArrowForward, NeutralYellow, NeutralYellowBg)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(if (large) 12.dp else 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = if (large) 16.dp else 10.dp, vertical = if (large) 8.dp else 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(if (large) 22.dp else 16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = if (large) 16.sp else 12.sp
            )
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun ConfidenceMeter(
    percent: Int,
    modifier: Modifier = Modifier
) {
    val color = when {
        percent >= 80 -> BullishGreen
        percent >= 65 -> NeutralYellow
        else -> BearishRed
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AI Confidence Score",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$percent%",
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = SurfaceBorder,
        )
    }
}

@Composable
fun LevelChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceDarkElevated,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$label: ",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
