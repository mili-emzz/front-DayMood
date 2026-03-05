package com.lumina.app_daymood.presentation.navigation.routes

object AuthRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE_AUTHENTICATED = "profile_authenticated"
}

object RecordRoutes {
    const val RECORD_EMOTION = "record_emotion"
    const val RECORD_HABIT = "record_habit"
}

object ForumRoutes {
    const val FORUM_HOME = "forum_home"
    const val CREATE_POST = "create_post"
    const val POST_DETAILS = "post_details"
}

// Helper para saber si una ruta debe ocultar el bottom nav
object NavigationHelper {
    val hiddenBottomNavRoutes = listOf(
        RecordRoutes.RECORD_EMOTION,
        RecordRoutes.RECORD_HABIT,
        AuthRoutes.LOGIN,
        AuthRoutes.REGISTER,
        ForumRoutes.CREATE_POST,
    )

    fun shouldHideBottomNav(route: String?): Boolean {
        if (route == null) return false
        return hiddenBottomNavRoutes.any { route.startsWith(it) }
    }
}