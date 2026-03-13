package com.lumina.app_daymood.presentation.views.record

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.app_daymood.presentation.viewmodels.RecordUiState
import com.lumina.app_daymood.domain.models.HabitModel as Habit
import com.lumina.app_daymood.presentation.viewmodels.RecordViewModel
import com.lumina.app_daymood.presentation.views.record.components.HabitChip
import com.lumina.app_daymood.presentation.views.record.components.habitIconRes
import com.lumina.app_daymood.ui.theme.BackgroundColor
import com.lumina.app_daymood.ui.theme.DisabledButton
import com.lumina.app_daymood.ui.theme.MainColor
import com.lumina.app_daymood.ui.theme.borderLines
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordHabitView(
    recordViewModel: RecordViewModel,
    date: LocalDate,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState = recordViewModel.uiState

    RecordHabitViewContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onSaveSuccess = onSaveSuccess,
        onClearSuccess = { recordViewModel.clearSuccess() },
        onSaveClick = { noteText, selectedHabitIds ->
            recordViewModel.saveEmotionSelection(
                emotionId = uiState.selectedEmotionId ?: "",
                note = noteText.ifBlank { null }
            )
            recordViewModel.saveRecord(
                date = recordViewModel.formatDate(date),
                habitIds = selectedHabitIds.toList(),
                noteToSave = noteText.ifBlank { "" }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordHabitViewContent(
    uiState: RecordUiState,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    onClearSuccess: () -> Unit,
    onSaveClick: (String, Set<String>) -> Unit
) {
    var selectedHabitIds by remember { mutableStateOf<Set<String>>(emptySet()) }

    // Nota vive aquí en RecordHabitView
    var note by remember { mutableStateOf("") }

    // Navegar cuando el guardado fue exitoso
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onClearSuccess()
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "¿Qué has estado haciendo hoy?",
                        color = Color.Black,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFFF0F0)
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color(0xFF8B4545)
                        )
                    }
                }
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {

                if (uiState.loadingCatalogs) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MainColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    // Renderizamos las categorías dinámicas provenientes de la API
                    uiState.habitCategories.forEach { category ->
                        if (category.habits.isNotEmpty()) {
                            HabitCategorySection(
                                title = category.categoryName,
                                habits = category.habits,
                                selectedHabitIds = selectedHabitIds,
                                onHabitToggle = { habitId ->
                                    val isSelected = habitId in selectedHabitIds
                                    if (isSelected) {
                                        // Deseleccionar si ya estaba
                                        selectedHabitIds = selectedHabitIds - habitId
                                    } else {
                                        val otherHabitIdsInCategory = category.habits.map { it.id }.toSet()
                                        selectedHabitIds = (selectedHabitIds - otherHabitIdsInCategory) + habitId
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                // Nota acá
                Text(
                    text = "Nota",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontSize = 20.sp
                )

                TextField(
                    value = note,
                    onValueChange = { if (it.length <= 200) note = it },
                    placeholder = { Text("Agregar nota... (opcional)", color = Color(0xFFD4B5B5)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MainColor,
                        unfocusedTextColor = DisabledButton
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                // Contador de caracteres (Límite 200)
                Text(
                    text = "${note.length}/200",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                uiState.error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        onSaveClick(note, selectedHabitIds)
                    },
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainColor,
                        disabledContainerColor = DisabledButton
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Guardar",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}


@Composable
private fun HabitCategorySection(
    title: String,
    habits: List<Habit>,
    selectedHabitIds: Set<String>,
    onHabitToggle: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            color = borderLines,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        habits.chunked(3).forEach { rowHabits ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                rowHabits.forEach { habit ->
                    HabitChip(
                        label = habit.name,
                        isSelected = habit.id in selectedHabitIds,
                        onClick = { onHabitToggle(habit.id) },
                        iconRes = habitIconRes(habit.name),   // ← nuevo
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(3 - rowHabits.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}