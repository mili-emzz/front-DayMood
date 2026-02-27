package com.lumina.app_daymood.presentation.views.add_emotion
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lumina.app_daymood.ui.theme.BackgroundColor
import com.lumina.app_daymood.ui.theme.MainColor

sealed class UploadState {
    object ImageNotSelected : UploadState()
    data class UploadingImage(val progress: Float) : UploadState()
    object UploadCompleted : UploadState()
}

@Preview
@Composable
fun AddEmotionScreen() {
    var uploadState by remember { mutableStateOf<UploadState>(UploadState.ImageNotSelected) }

    val onButtonClick = {
        uploadState = when (uploadState) {
            is UploadState.ImageNotSelected -> UploadState.UploadingImage(0.0f)
            is UploadState.UploadingImage -> UploadState.UploadCompleted
            is UploadState.UploadCompleted -> UploadState.ImageNotSelected
        }
    }

    AddEmotionContent(
        uploadState = uploadState,
        onButtonClick = onButtonClick
    )
}

@Composable
fun AddEmotionContent(uploadState: UploadState, onButtonClick: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Publica más emociones",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )
        Text(
            text = "Comparte imágenes para representar más emociones",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (uploadState) {
                is UploadState.ImageNotSelected -> ImageNotSelectedContent()
                is UploadState.UploadingImage -> UploadingImageContent(uploadState.progress)
                is UploadState.UploadCompleted -> UploadCompletedContent()
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        when (uploadState) {
            is UploadState.ImageNotSelected, is UploadState.UploadingImage -> {
                OutlinedButton(
                    onClick = onButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(1.dp, MainColor),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MainColor)
                ) {
                    Text(
                        text = "Subir emoción",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            is UploadState.UploadCompleted -> {
                Button(
                    onClick = onButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(1.dp, MainColor), // Contorno
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                ) {
                    Text(
                        text = "Subir emoción",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ImageNotSelectedContent() {
    var saveToFavorites by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 64.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.CloudUpload,
            contentDescription = "Upload Icon",
            tint = Color.Gray,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "Sube tu archivos aquí",
            color = MainColor,
            textDecoration = TextDecoration.Underline,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "PNG o JPG",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = saveToFavorites,
                onCheckedChange = { saveToFavorites = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MainColor,
                    checkedTrackColor = MainColor.copy(alpha = 0.4f),
                    checkedBorderColor = MainColor,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedBorderColor = Color.Gray
                )
            )
            Text(
                text = "Guardar en favoritos",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun UploadingImageContent(progress: Float) {
    var saveToFavorites by remember { mutableStateOf(true) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 64.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Image,
            contentDescription = "Uploading Image",
            tint = Color.Gray,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            color = MainColor,
            trackColor = Color.LightGray
        )
        Text(
            text = "${(progress * 100).toInt()}% completed",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "Subiendo imagen...",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = saveToFavorites,
                onCheckedChange = { saveToFavorites = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MainColor,
                    checkedTrackColor = MainColor.copy(alpha = 0.4f),
                    checkedBorderColor = MainColor,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedBorderColor = Color.Gray
                )
            )
            Text(
                text = "Guardar en favoritos",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun UploadCompletedContent() {
    var emotionName by remember { mutableStateOf("") }
    var saveToFavorites by remember { mutableStateOf(true) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9))
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Success",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(40.dp)
            )
        }
        Text(
            text = "Subida completada",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        TextButton(onClick = { /* TODO: Handle delete */ }) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Eliminar subida",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Eliminar subida",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = emotionName,
            onValueChange = { emotionName = it },
            label = { Text("Nombra tu emoción...") },
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MainColor,
                unfocusedIndicatorColor = Color.LightGray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = saveToFavorites,
                onCheckedChange = { saveToFavorites = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MainColor,
                    checkedTrackColor = MainColor.copy(alpha = 0.4f),
                    checkedBorderColor = MainColor,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedBorderColor = Color.Gray
                )
            )
            Text(
                text = "Guardar en favoritos",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "State 1: Image Not Selected")
@Composable
fun AddEmotionScreen_Preview_ImageNotSelected() {
    AddEmotionContent(
        uploadState = UploadState.ImageNotSelected,
        onButtonClick = {}
    )
}

@Preview(showBackground = true, name = "State 2: Uploading Image")
@Composable
fun AddEmotionScreen_Preview_UploadingImage() {
    AddEmotionContent(
        uploadState = UploadState.UploadingImage(0.76f),
        onButtonClick = {}
    )
}

@Preview(showBackground = true, name = "State 3: Upload Completed")
@Composable
fun AddEmotionScreen_Preview_UploadCompleted() {
    AddEmotionContent(
        uploadState = UploadState.UploadCompleted,
        onButtonClick = {}
    )
}