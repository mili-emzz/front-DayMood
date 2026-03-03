package com.lumina.app_daymood.presentation.views.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.app_daymood.ui.theme.BackgroundColor
import com.lumina.app_daymood.ui.theme.DisabledButton
import com.lumina.app_daymood.ui.theme.MainColor


// ---------- Modelo temporal ----------
data class Comment(
    val id: String,
    val username: String,
    val content: String
)

val sampleComments = listOf(
    Comment("1", "user48865", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore."),
    Comment("2", "user746985", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
    Comment("3", "user463203", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor."),
)
// -------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsView(
    post: ForoPost = samplePosts[0],
    onBackClick: () -> Unit = {}
) {
    val comments = remember { sampleComments }
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // ── Top bar ──
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF3D3D3D)
                )
            }
            Text(
                text = "Comentarios",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3D3D3D),
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.width(48.dp)) // balance
        }

        // ── Post original (header) ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(DisabledButton)
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(text = post.tag, fontSize = 11.sp, color = Color(0xFFB07068))
            }
            Spacer(Modifier.height(6.dp))
            Text(post.username, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF3D3D3D))
            Spacer(Modifier.height(4.dp))
            Text(post.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF2D2D2D))
            Spacer(Modifier.height(4.dp))
            Text(post.content, fontSize = 13.sp, color = Color(0xFF888888))
        }

        HorizontalDivider(color = Color(0xFFE8C9C3), thickness = 0.8.dp)

        // ── Lista de comentarios ──
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
        ) {
            items(comments) { comment ->
                CommentItem(comment = comment)
                HorizontalDivider(color = Color(0xFFE8C9C3), thickness = 0.8.dp)
            }
        }

        // ── Input de nuevo comentario ──
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Escribe un comentario...", fontSize = 13.sp, color = Color(0xFFBBBBBB)) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MainColor,
                    unfocusedBorderColor = Color(0xFFE0C5C0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (commentText.isBlank()) DisabledButton else MainColor),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { if (commentText.isNotBlank()) commentText = "" },
                    enabled = commentText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = "Enviar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    ) {
        Text(comment.username, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF3D3D3D))
        Spacer(Modifier.height(4.dp))
        Text(comment.content, fontSize = 13.sp, color = Color(0xFF888888))
    }
}

@Preview(showBackground = true)
@Composable
fun CommentsViewPreview() {
    CommentsView()
}