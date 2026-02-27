package com.lumina.app_daymood.presentation.views.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lumina.app_daymood.domain.models.EmotionModel
import com.lumina.app_daymood.presentation.viewmodels.RecordViewModel
import com.lumina.app_daymood.ui.theme.BackgroundColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RecordEmotionView(
    recordViewModel: RecordViewModel,
    date: LocalDate,
    onContinueClick: () -> Unit = {}
) {
    val uiState = recordViewModel.uiState
    val emotions = uiState.emotions

    // Dividimos las emociones en páginas de 8
    val emotionPages = emotions.chunked(8).ifEmpty { listOf(emptyList()) }
    var currentPage by remember { mutableStateOf(0) }
    val currentEmotions = emotionPages[currentPage]

    // Emoción seleccionada — leemos del uiState para mantener estado entre recomposiciones
    val selectedEmotionId = uiState.selectedEmotionId

    val formattedDate = date.format(
        DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
    )

    Scaffold(
        containerColor = BackgroundColor,
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {

                // Loading mientras cargan las emociones
                if (uiState.loadingCatalogs) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFFEB4A7)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(Modifier.height(20.dp))

                            Text(
                                text = "¿Cómo te sientes hoy?",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color(0xFF2C2C2C),
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF757575),
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(Modifier.height(48.dp))

                            // Círculo de emociones con imágenes desde URL
                            Box(
                                modifier = Modifier.size(320.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                EmotionCircle(
                                    emotions = currentEmotions,
                                    selectedEmotionId = selectedEmotionId,
                                    onEmotionSelected = { emotion ->
                                        // Guardamos solo el ID en el ViewModel
                                        recordViewModel.saveEmotionSelection(
                                            emotionId = emotion.id,
                                            note = null  // nota va en RecordHabitView
                                        )
                                    }
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Navegación entre páginas
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { if (currentPage > 0) currentPage-- },
                                    enabled = currentPage > 0
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Anterior",
                                        tint = if (currentPage > 0) Color(0xFF424242) else Color(0xFFCCCCCC)
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    emotionPages.indices.forEach { index ->
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (index == currentPage) Color(0xFFFEB4A7)
                                                    else Color(0xFFE0E0E0)
                                                )
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { if (currentPage < emotionPages.size - 1) currentPage++ },
                                    enabled = currentPage < emotionPages.size - 1
                                ) {
                                    Icon(
                                        Icons.Default.ArrowForward,
                                        contentDescription = "Siguiente",
                                        tint = if (currentPage < emotionPages.size - 1) Color(0xFF424242) else Color(0xFFCCCCCC)
                                    )
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            // Continuar habilitado solo si hay emoción seleccionada
                            Button(
                                onClick = onContinueClick,
                                enabled = selectedEmotionId != null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFEB4A7),
                                    disabledContainerColor = Color(0xFFE0E0E0)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .padding(horizontal = 32.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(
                                    "Continuar",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(Modifier.height(32.dp))
                        }
                    }
                }

                // Error si falló la carga de emociones
                uiState.error?.let { error ->
                    Snackbar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    ) {
                        Text(error)
                    }
                }
            }
        }
    )
}

// Círculo de emociones — ahora carga imágenes con Coil para traerla de la api
@Composable
fun EmotionCircle(
    emotions: List<EmotionModel>,
    selectedEmotionId: String?,
    onEmotionSelected: (EmotionModel) -> Unit
) {
    val radius = 140f
    val angleStep = if (emotions.isEmpty()) 0f else 360f / emotions.size

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        emotions.forEachIndexed { index, emotion ->
            val angle = Math.toRadians((angleStep * index).toDouble())
            val x = (radius * cos(angle)).toFloat()
            val y = (radius * sin(angle)).toFloat()
            val isSelected = selectedEmotionId == emotion.id

            Box(
                modifier = Modifier
                    .offset(x = x.dp, y = y.dp)
                    .size(if (isSelected) 64.dp else 56.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFFFEB4A7) else Color.White)
                    .clickable { onEmotionSelected(emotion) },
                contentAlignment = Alignment.Center
            ) {
                // AsyncImage carga desde Firebase Storage URL
                AsyncImage(
                    model = emotion.imgUrl,
                    contentDescription = emotion.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(if (isSelected) 44.dp else 40.dp)
                )
            }
        }

        // Centro: muestra la emoción seleccionada
        val selectedEmotion = emotions.find { it.id == selectedEmotionId }
        if (selectedEmotion != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = selectedEmotion.imgUrl,
                    contentDescription = selectedEmotion.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = selectedEmotion.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF2C2C2C),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}