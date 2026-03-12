package com.lumina.app_daymood.presentation.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.emiliagomez.vanamiapp.navigation.Destination
import com.lumina.app_daymood.presentation.navigation.routes.AuthRoutes
import com.lumina.app_daymood.presentation.navigation.routes.NavigationHelper
import com.lumina.app_daymood.ui.theme.MainColor
import com.lumina.app_daymood.ui.theme.navBarColor

@Composable
fun BottomNav(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var selectedItemRoute by remember(currentRoute) { mutableStateOf(currentRoute) }

    // Ocultar bottom nav en ciertas rutas
    if (NavigationHelper.shouldHideBottomNav(currentRoute)) {
        return
    }

    NavigationBar(
        containerColor = Color.White
    ) {
        Destination.entries.forEach { destination ->
            val isSelected = when {
                (selectedItemRoute in listOf(AuthRoutes.LOGIN, AuthRoutes.REGISTER) ||
                        selectedItemRoute == AuthRoutes.PROFILE_AUTHENTICATED) &&
                        destination == Destination.PROFILE -> true
                else -> selectedItemRoute == destination.route
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    selectedItemRoute = destination.route
                    if (currentRoute != destination.route) {
                        navController.navigate(destination.route) {
                            popUpTo(Destination.CALENDAR.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.route,
                        modifier = Modifier.size(34.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = navBarColor,
                    indicatorColor = MainColor,
                    unselectedIconColor = MainColor,
                )

            )
        }
    }
}

