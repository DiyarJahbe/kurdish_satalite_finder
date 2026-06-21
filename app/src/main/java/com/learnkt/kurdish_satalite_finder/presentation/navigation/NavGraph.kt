package com.learnkt.kurdish_satalite_finder.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.*
import com.learnkt.kurdish_satalite_finder.presentation.screens.compass.CompassScreen
import com.learnkt.kurdish_satalite_finder.presentation.screens.detail.SatelliteDetailScreen
import com.learnkt.kurdish_satalite_finder.presentation.screens.home.HomeScreen
import com.learnkt.kurdish_satalite_finder.presentation.screens.list.SatelliteListScreen
import com.learnkt.kurdish_satalite_finder.presentation.screens.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToList = { navController.navigate(Screen.List.route) },
                onNavigateToFavorites = { navController.navigate(Screen.List.route) }, // Filter favorites in list
                onNavigateToCompass = { navController.navigate(Screen.List.route) } // Navigate to list first to select satellite
            )
        }
        composable(Screen.List.route) {
            SatelliteListScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("satelliteId") { type = NavType.IntType })
        ) {
            SatelliteDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCompass = { id ->
                    navController.navigate(Screen.Compass.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.Compass.route,
            arguments = listOf(navArgument("satelliteId") { type = NavType.IntType })
        ) {
            CompassScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
