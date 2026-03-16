package com.lumina.app_daymood.presentation.views.add_emotion

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lumina.app_daymood.presentation.viewmodels.AddEmotionViewModel
import com.lumina.app_daymood.ui.theme.BackgroundColor
import com.lumina.app_daymood.ui.theme.MainColor

sealed class UploadState {
    object ImageNotSelected : UploadState()
    data class UploadingImage(val progress: Float) : UploadState()
    object UploadCompleted : UploadState()
}

@Composable
fun AddEmotionScreen(
    viewModel: AddEmotionViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val uploadState = viewModel.uploadState
    val isSubmitting = viewModel.isSubmitting
    val errorMessage = viewModel.errorMessage
    val successMessage = viewModel.successMessage

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        viewModel.onImageSelected(uri)
    }

    // Launcher de permisos en runtime
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    // Función que verifica/solicita permisos y luego abre la galería
    val onOpenGallery = {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        permissionLauncher.launch(permission)
    }

    val onButtonClick = {
        viewModel.submitEmotion(onSuccess = onNavigateBack)
    }

    AddEmotionContent(
        uploadState = uploadState,
        imageUri = viewModel.imageUri,
        emotionName = viewModel.emotionName,
        onNameChange = { viewModel.emotionName = it },
        selectedCategoryId = viewModel.selectedCategoryId,
        onCategoryChange = { viewModel.selectedCategoryId = it },
        saveToFavorites = viewModel.saveToFavorites,
        onFavoritesChange = { viewModel.saveToFavorites = it },
        onRemoveImage = { viewModel.removeImage() },
        onOpenGallery = onOpenGallery,
        onButtonClick = onButtonClick,
        isLoading = isSubmitting,
        errorMessage = errorMessage,
        successMessage = successMessage,
        clearMessages = { viewModel.clearMessages() }
    )
}

@Composable
fun AddEmotionContent(
    uploadState: UploadState,
    imageUri: Uri?,
    emotionName: String,
    onNameChange: (String) -> Unit,
    selectedCategoryId: Int,
    onCategoryChange: (Int) -> Unit,
    saveToFavorites: Boolean,
    onFavoritesChange: (Boolean) -> Unit,
    onRemoveImage: () -> Unit,
    onOpenGallery: () -> Unit = {},
    onButtonClick: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    successMessage: String? = null,
    clearMessages: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage, successMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            clearMessages()
        }
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Publica más emociones",
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
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
                    is UploadState.ImageNotSelected -> {
                        ImageNotSelectedContent(
                            saveToFavorites = saveToFavorites,
                            onFavoritesChange = onFavoritesChange,
                            onOpenGallery = onOpenGallery
                        )
                    }

                    is UploadState.UploadingImage -> {
                        UploadingImageContent(
                            uploadState.progress,
                            saveToFavorites,
                            onFavoritesChange
                        )
                    }

                    is UploadState.UploadCompleted -> {
                        UploadCompletedContent(
                            emotionName = emotionName,
                            onNameChange = onNameChange,
                            selectedCategoryId = selectedCategoryId,
                            onCategoryChange = onCategoryChange,
                            saveToFavorites = saveToFavorites,
                            onFavoritesChange = onFavoritesChange,
                            imageUri = imageUri,
                            onRemoveImage = onRemoveImage
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Solo mostramos el botón "Subir emoción" cuando ya se seleccionó y subió la imagen
            if (uploadState is UploadState.UploadCompleted) {
                Button(
                    onClick = onButtonClick,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
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
}

@Composable
fun ImageNotSelectedContent(
    saveToFavorites: Boolean,
    onFavoritesChange: (Boolean) -> Unit,
    onOpenGallery: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 64.dp)
    ) {
        // El icono y el texto son clickeables para abrir la galería
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { onOpenGallery() }
                .padding(8.dp)
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
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        FavoriteSwitch(saveToFavorites, onFavoritesChange)
    }
}

@Composable
fun UploadingImageContent(
    progress: Float,
    saveToFavorites: Boolean,
    onFavoritesChange: (Boolean) -> Unit
) {
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
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            color = MainColor,
            trackColor = Color.LightGray,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
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
        FavoriteSwitch(saveToFavorites, onFavoritesChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadCompletedContent(
    emotionName: String,
    onNameChange: (String) -> Unit,
    selectedCategoryId: Int,
    onCategoryChange: (Int) -> Unit,
    saveToFavorites: Boolean,
    onFavoritesChange: (Boolean) -> Unit,
    imageUri: Uri?,
    onRemoveImage: () -> Unit
) {
    // Categorías placeholder — se reemplazarán con los datos de la API cuando se integre
    val categories = listOf(
        Pair(8, "Alegría"),
        Pair(9, "Tristeza"),
        Pair(10, "Ira"),
        Pair(11, "Miedo"),
        Pair(12, "Amor"),
        Pair(13, "Desagrado"),
        Pair(14, "Vergüenza"),
        Pair(15, "Culpa")
    )
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Reemplazo del checkmark por la imagen subida en un círculo verde claro (si aplica)
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Uploaded Emotion Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9))
                )
            } else {
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
            }
        }

        Text(
            text = "Subida completada",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        TextButton(onClick = onRemoveImage) {
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
            onValueChange = onNameChange,
            label = { Text("Nombra tu emoción...") },
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MainColor,
                unfocusedIndicatorColor = Color.LightGray,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = categories.find { it.first == selectedCategoryId }?.second
                    ?: "Selecciona Categoría",
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MainColor,
                    unfocusedBorderColor = Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(text = category.second, color = Color.Black) },
                        onClick = {
                            onCategoryChange(category.first)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        FavoriteSwitch(saveToFavorites, onFavoritesChange)
    }
}

@Composable
fun FavoriteSwitch(saveToFavorites: Boolean, onFavoritesChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = saveToFavorites,
            onCheckedChange = onFavoritesChange,
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