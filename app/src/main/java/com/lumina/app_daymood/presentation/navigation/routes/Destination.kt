package com.emiliagomez.vanamiapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination(
    val route: String,
    val icon: ImageVector
) {
    HOME("home", Icons.Outlined.Home),
    CALENDAR("calendar", Icons.Filled.CalendarMonth),
    ADD("add", Icons.Outlined.AddCircleOutline),
    FAVORITES("favorites", Icons.Outlined.FavoriteBorder),
    PROFILE("profile", Icons.Outlined.Person)
}