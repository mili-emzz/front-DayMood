package com.lumina.app_daymood.presentation.views.record

import androidx.compose.runtime.Composable
import com.lumina.app_daymood.presentation.viewmodels.RecordViewModel
import java.time.LocalDate

@Composable
fun CalendarView(
    recordViewModel: RecordViewModel,
    onDayClick: (LocalDate) -> Unit = {},
    imageResId: Int,
    onDiaryClick: () -> Unit
) {
}