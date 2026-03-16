package com.lumina.app_daymood.presentation.views.stats

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.app_daymood.presentation.viewmodels.StatsViewModel
import com.lumina.app_daymood.ui.theme.BackgroundColor
import com.lumina.app_daymood.ui.theme.MainColor

private val BarColor      = Color(0xFFFEB4A7)
private val BarColorHigh  = Color(0xFFFC8C7A)
private val TextDark      = Color(0xFF3D3D3D)
private val TextMuted     = Color(0xFF9E9E9E)
private val GridLine      = Color(0xFFE0C5C0)

data class EmotionStat(
    val label: String,
    val count: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsView(
    onBackClick: () -> Unit = {},
    statsViewModel: StatsViewModel? = null
) {
    val uiState = statsViewModel?.uiState
    val stats = uiState?.stats?.takeIf { it.isNotEmpty() }

    LaunchedEffect(Unit) {
        statsViewModel?.loadStats()
    }

    val maxCount = stats?.maxOfOrNull { it.count } ?: 1

    Scaffold(

        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TextDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor
                )
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        when {
            uiState?.isLoading == true -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MainColor)
                }
            }
            uiState?.error != null && uiState.stats.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "Error inesperado",
                        color = TextMuted,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Estadísticas",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Descubre qué categoría de la emoción\npredomino esta semana",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextMuted,
                        lineHeight = 20.sp
                    )

                    Spacer(Modifier.height(40.dp))

                    BarChart(
                        stats = stats,
                        maxCount = maxCount,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                    )

                    Spacer(Modifier.height(32.dp))

                    val topEmotion = stats?.maxByOrNull { it.count }
                    if (topEmotion != null) {
                        SummaryCard(topEmotion = topEmotion, totalDays = stats.sumOf { it.count })
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun BarChart(
    stats: List<EmotionStat>?,
    maxCount: Int,
    modifier: Modifier = Modifier
) {
    val gridLines = maxCount
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
    }

    BoxWithConstraints(modifier = modifier) {
        val chartHeight = maxHeight - 32.dp  // reserva para labels

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in gridLines downTo 1) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "$i",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.width(20.dp),
                        textAlign = TextAlign.End
                    )
                    Spacer(Modifier.width(8.dp))
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp,
                        color = GridLine
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
                .padding(start = 28.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            stats?.forEach { stat ->
                val fraction = (stat.count.toFloat() / maxCount) * animProgress.value
                val isHighest = stat.count == maxCount
                val barColor = if (isHighest) BarColorHigh else BarColor

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                        .fillMaxHeight(fraction)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    barColor,
                                    barColor.copy(alpha = 0.7f)
                                )
                            )
                        )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(start = 28.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            stats?.forEach { stat ->
                Text(
                    text = stat.label,
                    fontSize = 11.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SummaryCard(topEmotion: EmotionStat, totalDays: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Esta semana",
                fontSize = 12.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "La emoción más frecuente fue ",
                    fontSize = 14.sp,
                    color = TextDark
                )
                Text(
                    text = topEmotion.label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainColor
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Registraste ${topEmotion.count} de $totalDays días con esta emoción",
                fontSize = 13.sp,
                color = TextMuted
            )
        }
    }
}
