package com.lumina.app_daymood.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.emiliagomez.vanamiapp.navigation.Destination
import com.lumina.app_daymood.R
import com.lumina.app_daymood.presentation.navigation.routes.AuthRoutes
import com.lumina.app_daymood.presentation.navigation.routes.RecordRoutes
import com.lumina.app_daymood.presentation.viewmodels.AuthViewModel
import com.lumina.app_daymood.presentation.viewmodels.RecordViewModel
import com.lumina.app_daymood.presentation.views.auth.LoginView
import com.lumina.app_daymood.presentation.views.auth.RegisterView
import com.lumina.app_daymood.presentation.views.profile.ProfileView
import com.lumina.app_daymood.presentation.views.record.RecordEmotionView
import com.lumina.app_daymood.presentation.views.record.RecordHabitView
import java.time.LocalDate
import com.lumina.app_daymood.presentation.views.record.CalendarView


@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    recordViewModel: RecordViewModel,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val initialRoute = if (authViewModel.isAuthenticated()) {
        Destination.CALENDAR.route
    } else {
        AuthRoutes.REGISTER
    }

    NavHost(
        navController = navController,
        startDestination = initialRoute,
        modifier = modifier.padding(innerPadding)
    ) {

        // ===== CALENDARIO =====
        composable(Destination.CALENDAR.route) {
            CalendarView(
                recordViewModel = recordViewModel,
                imageResId = R.drawable.info_content,
                onNavigateToCreate = { selectedDate ->
                    Log.d("CalendarView", "Día seleccionado: $selectedDate")

                    if (authViewModel.isAuthenticated()) {
                        navController.navigate("${RecordRoutes.RECORD_EMOTION}/$selectedDate")
                    } else {
                        navController.navigate(AuthRoutes.REGISTER)
                    }
                },
                onNavigateToDetail = { selectedDate ->
                    // TODO: Implementar navegación a detalle cuando esté lista la vista
                    Log.d("CalendarView", "Navigating to detail for: $selectedDate")
                },
                onDiaryClick = {
                    if (authViewModel.isAuthenticated()) {
                        navController.navigate("${RecordRoutes.RECORD_EMOTION}/${LocalDate.now()}")
                    } else {
                        navController.navigate(AuthRoutes.REGISTER)
                    }
                }
            )
        }

        // ===== HOME =====
        composable(Destination.HOME.route) {
            if (authViewModel.isAuthenticated()) {
                // HomeView(authViewModel = authViewModel)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(AuthRoutes.REGISTER) {
                        popUpTo(Destination.HOME.route) { inclusive = true }
                    }
                }
            }
        }

        // ===== ADD (acceso rápido a registro) =====
        composable(Destination.ADD.route) {
            if (authViewModel.isAuthenticated()) {
                LaunchedEffect(Unit) {
                    navController.navigate("${RecordRoutes.RECORD_EMOTION}/${LocalDate.now()}") {
                        popUpTo(Destination.CALENDAR.route) { inclusive = false }
                    }
                }
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(AuthRoutes.REGISTER)
                }
            }
        }

        // ===== REGISTRO DE EMOCIÓN — recibe la fecha por ruta =====
        composable("${RecordRoutes.RECORD_EMOTION}/{date}") { backStackEntry ->
            val dateStr = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
            val date = runCatching { LocalDate.parse(dateStr) }.getOrDefault(LocalDate.now())

            if (authViewModel.isAuthenticated()) {
                RecordEmotionView(
                    recordViewModel = recordViewModel,
                    date = date,
                    onContinueClick = {
                        navController.navigate("${RecordRoutes.RECORD_HABIT}/$dateStr")
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(AuthRoutes.REGISTER) {
                        popUpTo(Destination.CALENDAR.route) { inclusive = false }
                    }
                }
            }
        }

        // ===== REGISTRO DE HÁBITOS — recibe la fecha por ruta =====
        composable("${RecordRoutes.RECORD_HABIT}/{date}") { backStackEntry ->
            val dateStr = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
            val date = runCatching { LocalDate.parse(dateStr) }.getOrDefault(LocalDate.now())

            if (authViewModel.isAuthenticated()) {
                RecordHabitView(
                    recordViewModel = recordViewModel,
                    date = date,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaveSuccess = {
                        navController.navigate(Destination.CALENDAR.route) {
                            // Limpiamos el backstack hasta el calendario
                            popUpTo(Destination.CALENDAR.route) { inclusive = true }
                        }
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(AuthRoutes.REGISTER) {
                        popUpTo(Destination.CALENDAR.route) { inclusive = false }
                    }
                }
            }
        }

        // ===== FAVORITES =====
        composable(Destination.FAVORITES.route) {
            if (authViewModel.isAuthenticated()) {
                // Tu FavoritesView aquí cuando la implementes
                CalendarView(
                    recordViewModel = recordViewModel,
                    imageResId = R.drawable.info_content,
                    onNavigateToCreate = {},
                    onNavigateToDetail = {},
                    onDiaryClick = {}
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(AuthRoutes.REGISTER)
                }
            }
        }

        // ===== PROFILE =====
        composable(Destination.PROFILE.route) {
            if (authViewModel.isAuthenticated()) {
                ProfileView(
                    authViewModel = authViewModel,
                    onLogout = {
                        navController.navigate(AuthRoutes.REGISTER){
                            popUpTo(0) {inclusive = true}
                        }
                    },
                    onNavigateToFav = {
                        navController.navigate(Destination.FAVORITES.route)
                    }
                )
            } else {
                // Si no está autenticado, muestra RegisterView
                LaunchedEffect(Unit) {
                    navController.navigate(AuthRoutes.REGISTER){
                        popUpTo(Destination.PROFILE.route) {inclusive = true}
                    }
                }
            }
        }

        // ===== LOGIN =====
        composable(AuthRoutes.LOGIN) {
            LoginView(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(AuthRoutes.REGISTER) {
                        popUpTo(AuthRoutes.LOGIN) { inclusive = true }
                    }
                },
                onLoginSuccess = {
                    navController.navigate(Destination.CALENDAR.route) {
                        popUpTo(AuthRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ===== REGISTER =====
        composable(AuthRoutes.REGISTER) {
            RegisterView(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(AuthRoutes.LOGIN) {
                        popUpTo(AuthRoutes.REGISTER) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(Destination.PROFILE.route) {
                        popUpTo(AuthRoutes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        // ===== ESTADISTICAS =====
        composable(Destination.STADISTIC.route) {
            if (authViewModel.isAuthenticated()) {

            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(AuthRoutes.REGISTER)
                }
            }
        }
    }
}