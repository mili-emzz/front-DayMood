package com.lumina.app_daymood.presentation.views.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lumina.app_daymood.R
import com.lumina.app_daymood.presentation.viewmodels.RecordViewModel
import com.lumina.app_daymood.ui.theme.BackgroundColor
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarView(
    recordViewModel: RecordViewModel,
    imageResId: Int,
    onNavigateToCreate: (LocalDate) -> Unit,
    onNavigateToDetail: (LocalDate) -> Unit,
    onDiaryClick: () -> Unit
) {
    val uiState = recordViewModel.uiState
    var currentMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    var currentYear by remember { mutableStateOf(LocalDate.now().year) }

    // Cargar records del mes cuando cambia el mes o año
    LaunchedEffect(currentMonth, currentYear) {
        recordViewModel.loadRecordsByMonth(currentYear, currentMonth)
    }

    // Map de fechapara record para acceso O(1) en el grid
    val recordsByDate = uiState.monthRecords.associateBy { it.date }

    val currentDate = LocalDate.of(currentYear, currentMonth, 1)
    val previousMonth = currentDate.minusMonths(1)
    val nextMonth = currentDate.plusMonths(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 32.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Buen día",
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )

        // Botones de meses
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            listOf(previousMonth, currentDate, nextMonth).forEach { month ->
                val isSelected = month.monthValue == currentMonth && month.year == currentYear
                val monthName = month.month
                    .getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                    .replaceFirstChar { it.uppercase() }

                Button(
                    onClick = {
                        currentMonth = month.monthValue
                        currentYear = month.year
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFFFEB4A7) else Color.White
                    ),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Text(
                        monthName,
                        color = if (isSelected) Color.White else Color(0xFFFEB4A7),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Días de la semana
        val daysOfWeek = listOf("Dom", "Lun", "Mar", "Mie", "Jue", "Vie", "S")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp, start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF424242),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Grid del calendario
        val firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1)
        val yearMonth = YearMonth.of(currentYear, currentMonth)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstDayOfWeekIndex = firstDayOfMonth.dayOfWeek.value % 7
        val today = LocalDate.now()

        val daysGrid = buildList {
            repeat(firstDayOfWeekIndex) { add(null) }
            for (day in 1..daysInMonth) {
                add(LocalDate.of(currentYear, currentMonth, day))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                items(daysGrid) { date ->
                    if (date == null) {
                        Spacer(modifier = Modifier.size(40.dp))
                    } else {
                        val dateKey = recordViewModel.formatDate(date)
                        val record = recordsByDate[dateKey]
                        val isToday = date == today

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = when {
                                        isToday -> Color(0xFFFEB4A7)
                                        record != null -> Color(0xFFFFF0F0)
                                        else -> Color(0xFFF8F2EF)
                                    },
                                    shape = MaterialTheme.shapes.large
                                )
                                .clickable {
                                    if (record != null) {
                                        // Día con record → ver detalle
                                        onNavigateToDetail(date)
                                    } else {
                                        // Día sin record → ir a crear
                                        onNavigateToCreate(date)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (record != null) {
                                // Mostrar imagen de la emoción desde URL
                                AsyncImage(
                                    model = record.emotion.imgUrl,
                                    contentDescription = record.emotion.name,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(28.dp)
                                )
                            } else {
                                Text(
                                    date.dayOfMonth.toString(),
                                    color = if (isToday) Color.White else Color(0xFFFEB4A7),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        InfoContent(imageResId = imageResId, onDiaryClick = onDiaryClick)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun InfoContent(imageResId: Int, onDiaryClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFE6E6), shape = MaterialTheme.shapes.large)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("¿Cómo te sientes hoy?", fontWeight = FontWeight.Bold)
            Text(
                "Registra cómo te sientes hoy para una mejor calidad de vida",
                style = MaterialTheme.typography.bodySmall
            )
            Button(
                onClick = onDiaryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFB7B7),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Diario")
            }
        }
        Spacer(Modifier.width(12.dp))
        Box(Modifier.size(96.dp)) {
            Icon(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(96.dp)
            )
        }
    }
}