package com.lumina.app_daymood.presentation.views.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostView(
    onDismiss: () -> Unit = {},
    onPublish: (title: String, content: String, tag: String) -> Unit = { _, _, _ -> }
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("") }

    val tags = listOf("Ansiedad", "Hábitos", "Reflexión", "Motivación", "Otros")

    val canPublish = title.isNotBlank() && content.isNotBlank() && selectedTag.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top bar ──
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cerrar",
                    tint = Color(0xFF3D3D3D)
                )
            }
            Text(
                text = "Publicación",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3D3D3D),
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (canPublish) MainColor else DisabledButton),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        if (canPublish) onPublish(title, content, selectedTag)
                    },
                    enabled = canPublish
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Publicar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0xFFE8C9C3), thickness = 0.8.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            // ── Campo Título ──
            Text("Título", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF3D3D3D))
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Título (Obligatorio)", color = Color(0xFFBBBBBB), fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MainColor,
                    unfocusedBorderColor = Color(0xFFE0C5C0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(Modifier.height(20.dp))

            // ── Campo Contenido ──
            Text("Contenido", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF3D3D3D))
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Escribe algo...", color = Color(0xFFBBBBBB), fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MainColor,
                    unfocusedBorderColor = Color(0xFFE0C5C0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                maxLines = 6
            )

            Spacer(Modifier.height(24.dp))

            // ── Selector de Tag ──
            Text("Categoría", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF3D3D3D))
            Spacer(Modifier.height(10.dp))

            // Grid 2 columnas
            val rows = tags.chunked(2)
            rows.forEach { rowTags ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowTags.forEach { tag ->
                        val isSelected = tag == selectedTag
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MainColor else Color.White)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MainColor else Color(0xFFE0C5C0),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedTag = tag }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tag,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF888888)
                            )
                        }
                    }
                    // Si la fila tiene un solo elemento, llenamos el espacio
                    if (rowTags.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(8.dp))

            // ── Botón Publicar (secundario al ícono del header) ──
            Button(
                onClick = { if (canPublish) onPublish(title, content, selectedTag) },
                enabled = canPublish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainColor,
                    disabledContainerColor = DisabledButton,
                    contentColor = Color.White,
                    disabledContentColor = Color.White
                )
            ) {
                Text("Publicar", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreatePostViewPreview() {
    CreatePostView()
}