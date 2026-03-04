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
import com.lumina.app_daymood.presentation.navigation.routes.ForumRoutes
import com.lumina.app_daymood.presentation.navigation.routes.RecordRoutes
import com.lumina.app_daymood.presentation.viewmodels.AddEmotionViewModel
import com.lumina.app_daymood.presentation.viewmodels.AuthViewModel
import com.lumina.app_daymood.presentation.viewmodels.FavoritesViewModel
import com.lumina.app_daymood.presentation.viewmodels.ForumViewModel
import com.lumina.app_daymood.presentation.viewmodels.RecordViewModel
import com.lumina.app_daymood.presentation.views.add_emotion.AddEmotionScreen
import com.lumina.app_daymood.presentation.views.auth.LoginView
import com.lumina.app_daymood.presentation.views.auth.RegisterView
import com.lumina.app_daymood.presentation.views.forum.CommentsView
import com.lumina.app_daymood.presentation.views.forum.CreatePostView
import com.lumina.app_daymood.presentation.views.forum.ForoView
import com.lumina.app_daymood.presentation.views.home.HomeView
import com.lumina.app_daymood.presentation.views.profile.ProfileView
import com.lumina.app_daymood.presentation.views.record.RecordEmotionView
import com.lumina.app_daymood.presentation.views.record.RecordHabitView
import java.time.LocalDate
import com.lumina.app_daymood.presentation.views.record.CalendarView
import androidx.compose.runtime.collectAsState


@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    recordViewModel: RecordViewModel,
    forumViewModel: ForumViewModel,
    addEmotionViewModel: AddEmotionViewModel,
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

        // ===== HOME → HomeView (emociones custom del usuario) =====
        composable(Destination.HOME.route) {
            if (authViewModel.isAuthenticated()) {
                HomeView(
                    recordViewModel = recordViewModel,
                    favoritesViewModel = favoritesViewModel,
                    onForumClick = {
                        navController.navigate(ForumRoutes.FORUM_HOME)
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(AuthRoutes.REGISTER) {
                        popUpTo(Destination.HOME.route) { inclusive = true }
                    }
                }
            }
        }

        // ===== FORUM HOME (ruta directa) =====
        composable(ForumRoutes.FORUM_HOME) {
            if (authViewModel.isAuthenticated()) {
                ForoView(
                    viewModel = forumViewModel,
                    onPostClick = { post ->
                        navController.navigate("${ForumRoutes.POST_DETAILS}/${post.id}")
                    },
                    onCreatePost = {
                        navController.navigate(ForumRoutes.CREATE_POST)
                    }
                )
            } else {
                LaunchedEffect(Unit) { navController.navigate(AuthRoutes.REGISTER) }
            }
        }

        // ===== CREATE POST =====
        composable(ForumRoutes.CREATE_POST) {
            if (authViewModel.isAuthenticated()) {
                // TODO: replace "" with the real forumId once it's available from login/session
                val forumId = authViewModel.uiState.user?.id ?: ""
                CreatePostView(
                    forumId = forumId,
                    viewModel = forumViewModel,
                    onDismiss = { navController.popBackStack() },
                    onPublishSuccess = {
                        navController.navigate(ForumRoutes.FORUM_HOME) {
                            popUpTo(ForumRoutes.CREATE_POST) { inclusive = true }
                        }
                    }
                )
            } else {
                LaunchedEffect(Unit) { navController.navigate(AuthRoutes.REGISTER) }
            }
        }

        // ===== POST DETAILS / COMMENTS =====
        composable("${ForumRoutes.POST_DETAILS}/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            // Find the post from the already-loaded list in the ViewModel
            val post = forumViewModel.forumState.collectAsState().value.posts.find { it.id == postId }
            if (authViewModel.isAuthenticated() && post != null) {
                CommentsView(
                    post = post,
                    viewModel = forumViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }

        // ===== ADD (acceso rápido a crear emoción personalizada) =====
        composable(Destination.ADD.route) {
            if (authViewModel.isAuthenticated()) {
                LaunchedEffect(Unit) {
                    navController.navigate(RecordRoutes.ADD_EMOTION) {
                        popUpTo(Destination.CALENDAR.route) { inclusive = false }
                    }
                }
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(AuthRoutes.REGISTER)
                }
            }
        }

        // ===== PANTALLA DE CREAR EMOCIÓN PERSONALIZADA =====
        composable(RecordRoutes.ADD_EMOTION) {
            if (authViewModel.isAuthenticated()) {
                AddEmotionScreen(
                    viewModel = addEmotionViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                LaunchedEffect(Unit) { navController.navigate(AuthRoutes.REGISTER) }
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

        // ===== PROFILE =====
        composable(Destination.PROFILE.route) {
            if (authViewModel.isAuthenticated()) {
                ProfileView(
                    authViewModel = authViewModel,
                    onLogout = {
                        navController.navigate(AuthRoutes.REGISTER) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToFav = {
                        navController.navigate(Destination.FAVORITES.route)
                    }
                )
            } else {
                // Si no está autenticado, muestra RegisterView
                LaunchedEffect(Unit) {
                    navController.navigate(AuthRoutes.REGISTER) {
                        popUpTo(Destination.PROFILE.route) { inclusive = true }
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
    }
}