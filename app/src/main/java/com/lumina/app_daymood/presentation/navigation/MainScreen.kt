package com.lumina.app_daymood.presentation.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.emiliagomez.vanamiapp.navigation.Destination
import com.lumina.app_daymood.presentation.navigation.routes.AuthRoutes
import com.lumina.app_daymood.presentation.navigation.routes.RecordRoutes
import com.lumina.app_daymood.presentation.viewmodels.AuthViewModel
import com.lumina.app_daymood.presentation.viewmodels.RecordViewModel
import com.lumina.app_daymood.ui.theme.BackgroundColor
import com.lumina.app_daymood.ui.theme.MainColor
import com.lumina.app_daymood.ui.theme.navBarColor


@Composable
fun MainScreen(
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val recordViewModel: RecordViewModel = viewModel()

    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = {
            BottomNav(navController = navController)
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            recordViewModel = recordViewModel,
            innerPadding = innerPadding,
            authViewModel = authViewModel,
        )
    }
}


@Composable
fun BottomNav(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hideBottomNav = currentRoute in listOf(
        RecordRoutes.RECORD_EMOTION,
        RecordRoutes.RECORD_HABIT,
    )

    if (!hideBottomNav) {
        NavigationBar(
            containerColor = Color.White
        ) {
            Destination.entries.forEach { destination ->
                val isSelected = when {
                    currentRoute in listOf(AuthRoutes.LOGIN, AuthRoutes.REGISTER) && destination == Destination.PROFILE -> true
                    currentRoute == AuthRoutes.PROFILE_AUTHENTICATED && destination == Destination.PROFILE -> true
                    else -> currentRoute == destination.route
                }

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
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
                            tint = if (isSelected) MainColor else navBarColor,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                )
            }
        }
    }
}