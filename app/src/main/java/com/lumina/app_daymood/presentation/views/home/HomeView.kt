package com.lumina.app_daymood.presentation.views.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
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
    val uploadedEmotions = favoritesViewModel.uploadedEmotions
    val isLoading = favoritesViewModel.isLoading
    val favorites = favoritesViewModel.favorites

    LaunchedEffect(Unit) {
        favoritesViewModel.loadFavorites()
        favoritesViewModel.loadUploadedEmotions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .semantics{testTag = "homeScreen"}
            .background(BackgroundColor)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column  (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp, top = 28.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Descubre más",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Navegue y agregue emociones publicadas por usuarios",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 6.dp),
                maxLines = 1
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

            uploadedEmotions.isEmpty() -> {
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
                    items(uploadedEmotions, key = { it.id }) { emotion ->
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