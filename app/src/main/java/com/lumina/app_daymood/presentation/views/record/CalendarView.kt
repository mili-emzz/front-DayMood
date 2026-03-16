package com.lumina.app_daymood.presentation.views.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

    val scrollState = rememberScrollState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, currentMonth, currentYear) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                recordViewModel.loadRecordsByMonth(currentYear.toString(), currentMonth)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        recordViewModel.loadRecordsByMonth(currentYear.toString(), currentMonth)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val recordsByDate = uiState.monthRecords.associateBy { it.date.substringBefore("T") }
    val currentDate = LocalDate.of(currentYear, currentMonth, 1)
    val previousMonth = currentDate.minusMonths(1)
    val nextMonth = currentDate.plusMonths(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            // Agregamos scroll vertical para que nada se corte
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cabecera
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Buen día",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onNavigateToStats) {
                    Icon(
                        imageVector = Icons.Filled.BarChart,
                        contentDescription = "Estadísticas",
                        tint = MainColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = "Estadísticas",
                    style = MaterialTheme.typography.labelSmall,
                    color = MainColor
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(previousMonth, currentDate, nextMonth).forEach { month ->
                val isSelected = month.monthValue == currentMonth && month.year == currentYear
                val monthName = month.month
                    .getDisplayName(TextStyle.SHORT, Locale("es", "ES")) // SHORT para ahorrar espacio
                    .replaceFirstChar { it.uppercase() }

                Button(
                    onClick = {
                        currentMonth = month.monthValue
                        currentYear = month.year
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MainColor else Color.White
                    ),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text(
                        monthName,
                        color = if (isSelected) Color.White else Color(0xFFFEB4A7),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Reemplazamos LazyVerticalGrid por un Grid manual para que funcione bien dentro del scroll
        CalendarGrid(
            currentYear = currentYear,
            currentMonth = currentMonth,
            recordsByDate = recordsByDate,
            recordViewModel = recordViewModel,
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToCreate = onNavigateToCreate
        )

        Spacer(Modifier.height(24.dp))

        InfoContent(imageResId = imageResId, onDiaryClick = onDiaryClick)
        
        // Un pequeño espacio al final para que el scroll no quede pegado al borde
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun CalendarGrid(
    currentYear: Int,
    currentMonth: Int,
    recordsByDate: Map<String, com.lumina.app_daymood.domain.models.RecordModel>,
    recordViewModel: RecordViewModel,
    onNavigateToDetail: (LocalDate) -> Unit,
    onNavigateToCreate: (LocalDate) -> Unit
) {
    val firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1)
    val daysInMonth = YearMonth.of(currentYear, currentMonth).lengthOfMonth()
    val firstDayOfWeekIndex = firstDayOfMonth.dayOfWeek.value % 7
    val today = LocalDate.now()

    val daysGrid = buildList {
        repeat(firstDayOfWeekIndex) { add(null) }
        for (day in 1..daysInMonth) {
            add(LocalDate.of(currentYear, currentMonth, day))
        }
    }

    val daysOfWeek = listOf("Dom", "Lun", "Mar", "Mie", "Jue", "Vie", "Sab")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .padding(12.dp)
    ) {
        // Días de la semana
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }
        }
        
        Spacer(Modifier.height(8.dp))

        // Filas del calendario
        daysGrid.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                week.forEach { date ->
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f), contentAlignment = Alignment.Center) {
                        if (date != null) {
                            val dateKey = recordViewModel.formatDate(date)
                            val record = recordsByDate[dateKey]
                            val isToday = date == today

                            Box(
                                modifier = Modifier
                                    .fillMaxSize(0.9f)
                                    .background(
                                        color = if (isToday) MainColor else BackgroundColor,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .clickable {
                                        if (record != null) onNavigateToDetail(date)
                                        else onNavigateToCreate(date)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (record != null) {
                                    AsyncImage(
                                        model = record.emotion.imgUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.small)
                                    )
                                } else {
                                    Text(
                                        text = date.dayOfMonth.toString(),
                                        color = if (isToday) Color.White else Color(0xFFFEB4A7),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
                // Si la semana no tiene 7 días (al final), rellenamos con espacios vacíos
                if (week.size < 7) {
                    repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
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
            Text(
                text = "¿Cómo te sientes hoy?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Registra tus emociones y mantén un seguimiento de tu bienestar.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
            Text(
                text = "¡Si los datos de la app no se reflejan inmediatamente, intenta entrar y salir de la app!",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onDiaryClick,
                colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Ir al Diario", style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(Modifier.width(8.dp))
        Icon(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(80.dp)
        )
    }
}
