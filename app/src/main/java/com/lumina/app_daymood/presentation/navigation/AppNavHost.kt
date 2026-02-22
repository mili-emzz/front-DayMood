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
import com.lumina.app_daymood.presentation.views.record.CalendarView
import com.lumina.app_daymood.presentation.views.record.RecordEmotionView
import com.lumina.app_daymood.presentation.views.record.RecordHabitView

// Importa tus otras vistas aquí:
// import com.lumina.app_daymood.presentation.views.calendar.CalendarView
// import com.lumina.app_daymood.presentation.views.record.RecordEmotionView
// import com.lumina.app_daymood.presentation.views.record.RecordHabitView
// import com.lumina.app_daymood.presentation.views.profile.ProfileAuthenticatedView

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    recordViewModel: RecordViewModel,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destination.CALENDAR.route,
        modifier = modifier.padding(innerPadding)
    ) {

        // ===== CALENDARIO =====
        composable(Destination.CALENDAR.route) {
            CalendarView(
                recordViewModel = recordViewModel,
                onDayClick = { selectedDate ->
                    Log.d("CalendarView", "Día seleccionado: $selectedDate")

                    if (authViewModel.isAuthenticated()) {
                        navController.navigate(RecordRoutes.RECORD_EMOTION)
                    } else {
                        navController.navigate(AuthRoutes.REGISTER)
                    }
                },
                imageResId = R.drawable.info_content,
                onDiaryClick = {
                    if (authViewModel.isAuthenticated()) {
                        navController.navigate(RecordRoutes.RECORD_EMOTION)
                    } else {
                        navController.navigate(AuthRoutes.REGISTER)
                    }
                }
            )
        }

        // ===== HOME =====
        composable(Destination.HOME.route) {
            if (authViewModel.isAuthenticated()) {
                // Tu HomeView aquí
                // HomeView(authViewModel = authViewModel)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(AuthRoutes.REGISTER)
                }
            }
        }

        // ===== ADD (acceso rápido a registro) =====
        composable(Destination.ADD.route) {
            if (authViewModel.isAuthenticated()) {
                LaunchedEffect(Unit) {
                    navController.navigate(RecordRoutes.RECORD_EMOTION) {
                        popUpTo(Destination.CALENDAR.route) { inclusive = false }
                    }
                }
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(AuthRoutes.REGISTER)
                }
            }
        }

        // ===== REGISTRO DE EMOCIÓN =====
        composable(RecordRoutes.RECORD_EMOTION) {
            if (authViewModel.isAuthenticated()) {
                RecordEmotionView(
                    recordViewModel = recordViewModel,
                    onContinueClick = {
                        navController.navigate(RecordRoutes.RECORD_HABIT)
                    },
                    onBackClick = {
                        navController.popBackStack()
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

        // ===== REGISTRO DE HÁBITOS =====
        composable(RecordRoutes.RECORD_HABIT) {
            if (authViewModel.isAuthenticated()) {
                RecordHabitView(
                    recordViewModel = recordViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaveSuccess = {
                        navController.navigate(Destination.CALENDAR.route) {
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
                    onDayClick = {},
                    imageResId = R.drawable.info_content,
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
                        authViewModel.logout()
                        navController.navigate(Destination.PROFILE.route) {
                            popUpTo(Destination.PROFILE.route) { inclusive = true }
                        }
                    },
                    onNavigateToFav = {
                        navController.navigate(Destination.FAVORITES.route)
                    }
                )
            } else {
                // Si no está autenticado, muestra RegisterView
                RegisterView(
                    authViewModel = authViewModel,
                    onNavigateToLogin = {
                        navController.navigate(AuthRoutes.LOGIN)
                    },
                    onRegisterSuccess = {
                        recordViewModel.loadUserRecords()
                        navController.navigate(Destination.PROFILE.route) {
                            popUpTo(Destination.PROFILE.route) { inclusive = true }
                        }
                    }
                )
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
                    recordViewModel.loadUserRecords()
                    navController.navigate(Destination.PROFILE.route) {
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
                    recordViewModel.loadUserRecords()
                    navController.navigate(Destination.PROFILE.route) {
                        popUpTo(AuthRoutes.REGISTER) { inclusive = true }
                    }
                }
            )
        }
    }
}