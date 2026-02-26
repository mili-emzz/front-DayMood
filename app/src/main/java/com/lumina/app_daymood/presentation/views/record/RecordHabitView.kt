package com.lumina.app_daymood.presentation.views.record

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.app_daymood.domain.models.HabitModel as Habit
import com.lumina.app_daymood.presentation.viewmodels.RecordViewModel
import com.lumina.app_daymood.ui.theme.MainColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Categorías fijas con sus IDs de categoryId de la BD
private data class HabitCategory(
    val categoryId: Int,
    val title: String
)

private val HABIT_CATEGORIES = listOf(
    HabitCategory(categoryId = 3, title = "Salud"),
    HabitCategory(categoryId = 4, title = "Bienestar mental"),
    HabitCategory(categoryId = 5, title = "Social"),
    HabitCategory(categoryId = 6, title = "Productividad")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordHabitView(
    recordViewModel: RecordViewModel,
    date: LocalDate,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState = recordViewModel.uiState

    // Hábitos seleccionados localmente (Set de IDs). Al final no se necesita category como un model XD
    var selectedHabitIds by remember { mutableStateOf<Set<String>>(emptySet()) }

    // Nota vive aquí en RecordHabitView
    var note by remember { mutableStateOf("") }

    val formattedDate = date.format(
        DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
    )

    // Navegar cuando el guardado fue exitoso
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            recordViewModel.clearSuccess()
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "¿Qué has estado haciendo hoy?",
                        color = Color(0xFF8B4545),
                        fontSize = 16.sp
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
        containerColor = Color(0xFFFFF0F0)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF757575),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (uiState.loadingCatalogs) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MainColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    // Renderizamos una sección por cada categoría fija mostrando los hábitos dinámicos que correspondan a ese categoryId
                    HABIT_CATEGORIES.forEach { category ->
                        val habitsForCategory = uiState.habits.filter {
                            it.categoryId == category.categoryId
                        }

                        // Solo mostramos la categoría si tiene hábitos
                        if (habitsForCategory.isNotEmpty()) {
                            HabitCategorySection(
                                title = category.title,
                                habits = habitsForCategory,
                                selectedHabitIds = selectedHabitIds,
                                onHabitToggle = { habitId ->
                                    selectedHabitIds = if (habitId in selectedHabitIds) {
                                        selectedHabitIds - habitId
                                    } else {
                                        selectedHabitIds + habitId
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
                    color = Color(0xFF8B4545),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
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
                        focusedTextColor = Color(0xFF8B4545),
                        unfocusedTextColor = Color(0xFF8B4545)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                // Contador de caracteres
                Text(
                    text = "${note.length}/500",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Error si algo salió mal
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
                        // Actualizamos la nota en el ViewModel justo antes de guardar
                        recordViewModel.saveEmotionSelection(
                            emotionId = uiState.selectedEmotionId ?: "",
                            note = note.ifBlank { null }
                        )
                        recordViewModel.saveRecord(
                            date = recordViewModel.formatDate(date),
                            habitIds = selectedHabitIds.toList()
                        )
                    },
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF4C2C2),
                        disabledContainerColor = Color(0xFFE0E0E0)
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

// Sección de categoría con sus hábitos como chips toggleables
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
            color = Color(0xFF8B4545),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Chips en filas wrap manual con chunked
        habits.chunked(3).forEach { rowHabits ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                rowHabits.forEach { habit ->
                    val isSelected = habit.id in selectedHabitIds
                    HabitChip(
                        label = habit.name,
                        isSelected = isSelected,
                        onClick = { onHabitToggle(habit.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Rellenar espacios vacíos si la fila no está completa
                repeat(3 - rowHabits.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// Chip individual de hábito
@Composable
private fun HabitChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFF4C2C2) else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFFF4C2C2) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else Color(0xFF8B4545),
            maxLines = 1
        )
    }
}