package com.lumina.app_daymood.presentation.views.record.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.app_daymood.R
import com.lumina.app_daymood.ui.theme.MainColor

// Chip individual de hábito
@Composable
fun HabitChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    iconRes: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MainColor else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) MainColor else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
        ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = if (isSelected) Color.White else MainColor,
            modifier = Modifier.size(36.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else MainColor,
            maxLines = 1
        )
    }
}

fun habitIconRes(name: String): Int = when (name) {
    // Sueño
    "+8 horas"        -> R.drawable.habit_sleep_8h
    "6-7 horas"       -> R.drawable.habit_sleep_6h
    "4-5 horas"       -> R.drawable.habit_sleep_4h
    "Menos de 4 horas"-> R.drawable.habit_sleep_lowest
    // Alimentación
    "Balanceada"      -> R.drawable.habit_food_balanced
    "Aceptable"       -> R.drawable.habit_food_ok
    "Irregular"       -> R.drawable.habit_food_irregular
    "Insuficiente"    -> R.drawable.habit_food_bad
    // Social
    "Excelentes"      -> R.drawable.habit_social_great
    "Positivas"       -> R.drawable.habit_social_positive
    "Neutras"         -> R.drawable.habit_social_neutral
    "Conflictivas"    -> R.drawable.habit_social_conflict
    // Actividad física
    "+60 minutos"     -> R.drawable.habit_activity_high
    "30-60 min"       -> R.drawable.habit_activity_mid
    "Menos de 30 minutos" -> R.drawable.habit_activity_low
    "Ninguna"         -> R.drawable.habit_activity_none
    // Autocuidad
    "Completo" -> R.drawable.habit_selfc_complete
    "Parcial" -> R.drawable.habit_selfc_parcial
    "Minimo" -> R.drawable.habit_selfc_min
    "Nulo" -> R.drawable.habit_selfc_nule
    //Control adicciones
    "Moderadas" -> R.drawable.habit_adic_mod
    "Controladas" -> R.drawable.habit_adic_cont
    "Excesivas" -> R.drawable.habit_adic_exc
    "Sin control" -> R.drawable.habit_adic_woc
    //Escuela/trabajp
    "Satisfactorio" -> R.drawable.habit_work_sats
    "Pudo ser mejor" -> R.drawable.habit_work_accep
    "Estrés" -> R.drawable.habit_work_stress
    "Agotador" -> R.drawable.habit_work_tired
    else              -> R.drawable.habit_default
}

@Preview(showBackground = true)
@Composable
fun HabitChipPreview() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        // Sin seleccionar
        HabitChip(
            label = "+8 horas",
            isSelected = false,
            onClick = {},
            iconRes = R.drawable.habit_sleep_8h, // ícono del sistema, temporal
            modifier = Modifier.width(100.dp)
        )
        // Seleccionado
        HabitChip(
            label = "+8 horas",
            isSelected = true,
            onClick = {},
            iconRes = R.drawable.habit_sleep_8h,
            modifier = Modifier.width(100.dp)
        )
    }
}