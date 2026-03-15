package com.lumina.app_daymood.presentation.views.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
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
import com.lumina.app_daymood.ui.theme.MainColor
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.draw.clip
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CalendarView(
    recordViewModel: RecordViewModel,
    imageResId: Int,
    onNavigateToCreate: (LocalDate) -> Unit,
    onNavigateToDetail: (LocalDate) -> Unit,
    onDiaryClick: () -> Unit,
    onNavigateToStats: () -> Unit = {}
) {
    val uiState = recordViewModel.uiState
    var currentMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    var currentYear by remember { mutableStateOf(LocalDate.now().year) }

    // Cargar records del mes cuando cambia el mes o año, o cuando la vista se reanuda
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, currentMonth, currentYear) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                recordViewModel.loadRecordsByMonth(currentYear.toString(), currentMonth)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        // Cargar inmediatamente cuando se instancia o cambian el mes/año
        recordViewModel.loadRecordsByMonth(currentYear.toString(), currentMonth)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val recordsByDate = uiState.monthRecords.associateBy { it.date.substringBefore("T") }

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Buen día",
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                IconButton(onClick = onNavigateToStats) {
                    Icon(
                        imageVector = Icons.Filled.BarChart,
                        contentDescription = "Estadísticas",
                        tint = MainColor,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Text(
                    text = "Estadísticas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MainColor
                )
            }
        }

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
                        containerColor = if (isSelected) MainColor else Color.White
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
        val daysOfWeek = listOf("Dom", "Lun", "Mar", "Mie", "Jue", "Vie", "Sab")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp, start = 15.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(4.dp))

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
                    .height(300.dp)
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
                                .size(50.dp)
                                .background(
                                    color = when {
                                        isToday -> MainColor
                                        record != null -> BackgroundColor
                                        else -> BackgroundColor
                                    },
                                    shape = MaterialTheme.shapes.large
                                )
                                .clickable {
                                    if (record != null) {
                                        onNavigateToDetail(date)
                                    } else {
                                        onNavigateToCreate(date)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (record != null) {
                                AsyncImage(
                                    model = record.emotion.imgUrl,
                                    contentDescription = record.emotion.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(MaterialTheme.shapes.large)
                                )
                            } else {
                                Text(
                                    date.dayOfMonth.toString(),
                                    color = if (isToday) Color.White else Color(0xFFFEB4A7),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        InfoContent(imageResId = imageResId, onDiaryClick = onDiaryClick)
    }
}

@Composable
fun InfoContent(imageResId: Int, onDiaryClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFEAE6), shape = MaterialTheme.shapes.large)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("¿Cómo te sientes hoy?", fontWeight = FontWeight.Bold)
            Column{
                Text(
                    "Registrar cómo te sientes hoy",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "¡Esta es una version 1, algunas funciones pueden que no estén disponibles aún!",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(
                onClick = onDiaryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainColor,
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