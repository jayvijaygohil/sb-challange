package com.jayvijay.gitpeek.presentation.feature.repositorydetails.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jayvijay.gitpeek.presentation.util.GitPeekState
import com.jayvijay.gitpeek.presentation.util.asString

private val GoldColor = Color(0xFFFFD700)

@Composable
fun ForkCountSection(forkCountState: GitPeekState<Int>) {
    when (forkCountState) {
        is GitPeekState.Idle,
        is GitPeekState.Loading,
        -> {
            ShimmerPlaceholder()
        }

        is GitPeekState.Error -> {
            Text(
                text = forkCountState.message.asString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        is GitPeekState.Success -> {
            ForkCountDisplay(forkCount = forkCountState.data)
        }
    }
}

@Composable
fun ForkCountDisplay(forkCount: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (forkCount > 5000) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = GoldColor,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = forkCount.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = GoldColor,
            )
        } else {
            Text(
                text = forkCount.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun ShimmerPlaceholder() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "shimmer",
    )

    val shimmerColors =
        listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        )

    val brush =
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(shimmerTranslate - 200f, 0f),
            end = Offset(shimmerTranslate, 0f),
        )

    Box(
        modifier =
            Modifier
                .fillMaxWidth(0.4f)
                .height(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(brush),
    )
}
