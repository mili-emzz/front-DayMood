package com.lumina.app_daymood.presentation.views.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.app_daymood.ui.theme.BackgroundColor
import com.lumina.app_daymood.ui.theme.DisabledButton
import com.lumina.app_daymood.ui.theme.MainColor

data class ForoPost(
    val id: String,
    val username: String,
    val title: String,
    val content: String,
    val tag: String,
    val commentCount: Int
)

val samplePosts = listOf(
    ForoPost("1", "user48865", "¿Cómo manejan la ansiedad?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "Ansiedad", 3),
    ForoPost("2", "user746985", "Rutinas de la mañana 🌅", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "Hábitos", 7),
    ForoPost("3", "user463203", "Diario de emociones", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "Reflexión", 1),
)

@Composable
fun ForoView(
    onPostClick: (ForoPost) -> Unit = {},
    onCreatePost: () -> Unit = {}
) {
    val posts = remember { samplePosts }
    var selectedTag by remember { mutableStateOf("Todos") }
    val tags = listOf("Todos", "Ansiedad", "Hábitos", "Reflexión", "Otros")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ──
            Text(
                text = "Descubre más",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3D3D3D),
                modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp)
            )

            // ── Tags chip row ──
            androidx.compose.foundation.lazy.LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(tags) { tag ->
                    TagChip(
                        label = tag,
                        selected = tag == selectedTag,
                        onClick = { selectedTag = tag }
                    )
                }
            }

            // ── Lista de posts ──
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                val filtered = if (selectedTag == "Todos") posts else posts.filter { it.tag == selectedTag }
                items(filtered) { post ->
                    ForoPostItem(
                        post = post,
                        onPostClick = { onPostClick(post) }
                    )
                    Divider(color = Color(0xFFE8C9C3), thickness = 0.8.dp)
                }
            }
        }

        // ── FAB ──
        FloatingActionButton(
            onClick = onCreatePost,
            containerColor = MainColor,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Crear post")
        }
    }
}

@Composable
fun TagChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) MainColor else Color.White)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (selected) Color.White else Color(0xFF888888),
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun ForoPostItem(post: ForoPost, onPostClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPostClick() }
            .padding(vertical = 16.dp)
    ) {
        // Tag badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(DisabledButton)
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(text = post.tag, fontSize = 11.sp, color = Color(0xFFB07068))
        }

        Spacer(Modifier.height(6.dp))

        // Username
        Text(
            text = post.username,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color(0xFF3D3D3D)
        )

        Spacer(Modifier.height(4.dp))

        // Title
        Text(
            text = post.title,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF2D2D2D)
        )

        Spacer(Modifier.height(4.dp))

        // Content preview
        Text(
            text = post.content,
            fontSize = 13.sp,
            color = Color(0xFF888888),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(10.dp))

        // Footer: comentarios
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = null,
                tint = MainColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Ver comentarios",
                fontSize = 12.sp,
                color = MainColor,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "${post.commentCount} comentarios",
                fontSize = 12.sp,
                color = Color(0xFFBBBBBB)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForoViewPreview() {
    ForoView()
}