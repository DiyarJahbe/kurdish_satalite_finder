package com.learnkt.kurdish_satalite_finder.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.learnkt.kurdish_satalite_finder.presentation.screens.compass.CompassScreen
import com.learnkt.kurdish_satalite_finder.presentation.screens.detail.SatelliteDetailScreen
import com.learnkt.kurdish_satalite_finder.presentation.screens.home.HomeScreen
import com.learnkt.kurdish_satalite_finder.presentation.screens.onboarding.OnboardingScreen
import com.learnkt.kurdish_satalite_finder.presentation.screens.splash.SplashScreen
import com.learnkt.kurdish_satalite_finder.presentation.viewmodel.OnboardingViewModel

@Composable
fun NavGraph(
    navController: NavHostController
) {
    val onboardingViewModel: OnboardingViewModel = viewModel()
    val isOnboardingCompleted by onboardingViewModel.isOnboardingCompleted.collectAsState(initial = false)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    if (isOnboardingCompleted) {
                        navController.navigate("main") {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    onboardingViewModel.setOnboardingCompleted(true)
                    navController.navigate("main") {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Main navigation with bottom tabs
        navigation(
            startDestination = Screen.HomeTab.route,
            route = "main"
        ) {
            composable(Screen.HomeTab.route) {
                HomeScreen(
                    navController = navController
                )
            }
            composable(Screen.SatellitesTab.route) {
                HomeScreen(
                    navController = navController
                )
            }
            composable(Screen.FavoritesTab.route) {
                HomeScreen(
                    navController = navController
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
}
