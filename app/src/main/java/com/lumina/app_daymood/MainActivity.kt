package com.lumina.app_daymood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.lumina.app_daymood.di.AppModule
import com.lumina.app_daymood.presentation.navigation.AppNavHost
import com.lumina.app_daymood.presentation.navigation.BottomNav
import com.lumina.app_daymood.presentation.viewmodels.AuthViewModel
import com.lumina.app_daymood.presentation.viewmodels.FavoritesViewModel
import com.lumina.app_daymood.presentation.viewmodels.ForumViewModel
import com.lumina.app_daymood.presentation.viewmodels.RecordViewModel
import com.lumina.app_daymood.ui.theme.App_daymoodTheme
import com.lumina.app_daymood.ui.theme.BackgroundColor

class MainActivity : ComponentActivity() {
    private val authViewModel by lazy { AppModule.provideAuthViewModel() }
    private val recordViewModel by lazy { AppModule.provideRecordViewModel() }
    private val favoritesViewModel by lazy { AppModule.provideFavoritesViewModel() }
    private val forumViewModel by lazy { AppModule.provideForumViewModel() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App_daymoodTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundColor
                ) {
                    MainScreen(
                        authViewModel = authViewModel,
                        recordViewModel = recordViewModel,
                        favoritesViewModel = favoritesViewModel,
                        forumViewModel = forumViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    recordViewModel: RecordViewModel,
    favoritesViewModel: FavoritesViewModel,
    forumViewModel: ForumViewModel
) {
    val navController = rememberNavController()

    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = {
            BottomNav(navController = navController)
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            recordViewModel = recordViewModel,
            favoritesViewModel = favoritesViewModel,
            forumViewModel = forumViewModel,
            innerPadding = innerPadding,
            authViewModel = authViewModel
        )
    }
}