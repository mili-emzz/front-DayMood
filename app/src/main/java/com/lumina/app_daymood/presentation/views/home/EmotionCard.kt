package com.lumina.app_daymood.presentation.views.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lumina.app_daymood.domain.models.EmotionModel
import com.lumina.app_daymood.ui.theme.MainColor

/**
 * Tarjeta reutilizable para mostrar una emoción personalizada.
 *
 * @param emotion       El modelo de emoción a mostrar.
 * @param isFavorite    Si ya está guardada como favorita.
 * @param onFavorite    Acción al presionar el corazón.
 * @param categoryName  Nombre de categoría a mostrar. Si null, usa el categoryId.
 */
@Composable
fun EmotionCard(
    emotion: EmotionModel,
    isFavorite: Boolean = false,
    onFavorite: () -> Unit = {},
    categoryName: String? = null,
    modifier: Modifier = Modifier
) {
    val displayCategory = categoryName ?: "Cat. ${emotion.categoryId}"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = emotion.imgUrl,
                    contentDescription = emotion.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = emotion.name,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2D2D2D),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayCategory,
                    fontSize = 12.sp,
                    color = Color(0xFFBBBBBB),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onFavorite,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar favorito" else "Agregar favorito",
                        tint = if (isFavorite) MainColor else Color(0xFFCCCCCC),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}