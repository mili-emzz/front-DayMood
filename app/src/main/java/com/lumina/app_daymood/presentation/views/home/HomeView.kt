package com.lumina.app_daymood.presentation.views.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.app_daymood.domain.models.EmotionModel
import com.lumina.app_daymood.presentation.viewmodels.FavoritesViewModel
import com.lumina.app_daymood.presentation.viewmodels.RecordViewModel
import com.lumina.app_daymood.presentation.viewmodels.categoryMap
import com.lumina.app_daymood.ui.theme.BackgroundColor
import com.lumina.app_daymood.ui.theme.MainColor

@Composable
fun HomeView(
    recordViewModel: RecordViewModel,
    favoritesViewModel: FavoritesViewModel,
) {
    val customEmotions = recordViewModel.uiState.emotions.filter { it.isCustom }
    val isLoading = recordViewModel.uiState.loadingCatalogs
    val favorites = favoritesViewModel.favorites

    LaunchedEffect(Unit) {
        favoritesViewModel.loadFavorites()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp, top = 28.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Descubre más",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MainColor)
                }
            }

            customEmotions.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Aún no tienes emociones guardadas",
                            fontSize = 15.sp,
                            color = Color(0xFFBBBBBB),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "¡Sube tu primera emoción desde \"Subir\"!",
                            fontSize = 13.sp,
                            color = Color(0xFFCCCCCC)
                        )
                    }
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(customEmotions, key = { it.id }) { emotion ->
                        val isFav = favorites.any { it.id == emotion.id }
                        EmotionCard(
                            emotion = emotion,
                            isFavorite = isFav,
                            onFavorite = { favoritesViewModel.addFavorite(emotion.id) },
                            categoryName = categoryMap[emotion.categoryId]
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFAE8E5)
@Composable
fun HomeViewPreview() {
    val sampleEmotions = listOf(
        EmotionModel("1", "Emperrada", "", 16, userId = "u1"),
        EmotionModel("2", "Feliz", "", 17, userId = "u1"),
        EmotionModel("3", "Fracasada", "", 18, userId = "u1"),
        EmotionModel("4", "Tristona", "", 19, userId = "u1"),
        EmotionModel("5", "Tranquila", "", 17, userId = "u1"),
        EmotionModel("6", "Despechada", "", 19, userId = "u1"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp, top = 28.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Descubre más",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.Forum,
                    contentDescription = null,
                    tint = MainColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(sampleEmotions, key = { it.id }) { emotion ->
                EmotionCard(
                    emotion = emotion,
                    isFavorite = emotion.id == "2",
                    categoryName = categoryMap[emotion.categoryId]
                )
            }
        }
    }
}
