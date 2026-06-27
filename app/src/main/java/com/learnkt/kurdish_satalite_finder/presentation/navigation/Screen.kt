package com.learnkt.kurdish_satalite_finder.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object HomeTab : Screen("home_tab")
    object SatellitesTab : Screen("satellites_tab")
    object FavoritesTab : Screen("favorites_tab")
    object Detail : Screen("detail/{satelliteId}") {
        fun createRoute(satelliteId: Int) = "detail/$satelliteId"
    }
    object Compass : Screen("compass/{satelliteId}") {
        fun createRoute(satelliteId: Int) = "compass/$satelliteId"
    }
}
