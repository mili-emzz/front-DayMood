package com.lumina.app_daymood.presentation.navigation.routes

object AuthRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE_AUTHENTICATED = "profile_authenticated"
}

object RecordRoutes {
    const val RECORD_EMOTION = "record_emotion"
    const val RECORD_HABIT = "record_habit"
    const val STADISTIC = "statistic"
}

// Helper para saber si una ruta debe ocultar el bottom nav
object NavigationHelper {
    val hiddenBottomNavRoutes = listOf(
        RecordRoutes.RECORD_EMOTION,
        RecordRoutes.RECORD_HABIT,
        AuthRoutes.LOGIN,
        AuthRoutes.REGISTER
    )

    fun shouldHideBottomNav(route: String?): Boolean {
        if (route == null) return false
        return hiddenBottomNavRoutes.any { route.startsWith(it) }
                || route.startsWith(AuthRoutes.LOGIN)
                || route.startsWith(AuthRoutes.REGISTER)
    }
}