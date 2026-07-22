package com.learnkt.kurdish_satalite_finder.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.learnkt.kurdish_satalite_finder.presentation.screens.ar.ARScreen
import com.learnkt.kurdish_satalite_finder.presentation.screens.compass.CompassScreen
import com.learnkt.kurdish_satalite_finder.presentation.screens.detail.SatelliteDetailScreen
import com.learnkt.kurdish_satalite_finder.presentation.screens.home.HomeScreen
import com.learnkt.kurdish_satalite_finder.presentation.screens.map.MapScreen
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
            composable(Screen.Map.route) {
                MapScreen(
                    onNavigateBack = { navController.popBackStack() }
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
                    },
                    onNavigateToAR = { id ->
                        navController.navigate(Screen.AR.createRoute(id))
                    }
                )
            }
            composable(Screen.LocationSettings.route) {
                com.learnkt.kurdish_satalite_finder.presentation.screens.settings.LocationSettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.ReceiverHelper.route) {
                com.learnkt.kurdish_satalite_finder.presentation.screens.tools.ReceiverHelperScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.SignalHelper.route) {
                com.learnkt.kurdish_satalite_finder.presentation.screens.tools.SignalHelperScreen(
                    onNavigateBack = { navController.popBackStack() }
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
            composable(
                route = Screen.AR.route,
                arguments = listOf(navArgument("satelliteId") { type = NavType.IntType })
            ) {
                val satelliteId = it.arguments?.getInt("satelliteId") ?: 0
                ARScreen(
                    satelliteId = satelliteId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
