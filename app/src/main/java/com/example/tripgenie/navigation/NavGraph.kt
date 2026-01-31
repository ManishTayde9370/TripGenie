package com.example.tripgenie.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tripgenie.MainViewModel
import com.example.tripgenie.ui.screens.*

@Composable
fun NavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel = viewModel()
) {
    val destinationToNavigate by mainViewModel.startDestination.collectAsState()

    // Shared ViewModels for different flows
    val flightViewModel: FlightViewModel = viewModel()
    val hotelViewModel: HotelViewModel = viewModel()

    LaunchedEffect(destinationToNavigate) {
        if (destinationToNavigate != Screen.Splash.route) {
            navController.navigate(destinationToNavigate) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onTimeout = {
                mainViewModel.calculateNextDestination()
            })
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onFinish = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate(Screen.Signup.route)
                }
            )
        }
        composable(Screen.Signup.route) {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTripPlanner = { navController.navigate(Screen.TripPlanner.createRoute()) },
                onNavigateToSafety = { navController.navigate(Screen.Safety.route) },
                onNavigateToLanguage = { navController.navigate(Screen.LanguageAssistant.route) },
                onNavigateToEvents = { navController.navigate(Screen.Events.route) },
                onNavigateToGreenTravel = { navController.navigate(Screen.GreenTravel.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToTouristDiscovery = { navController.navigate(Screen.TouristDiscovery.route) },
                onNavigateToFlights = { navController.navigate(Screen.FlightComparison.route) },
                onNavigateToHotels = { navController.navigate(Screen.HotelComparison.route) }
            )
        }
        composable(
            route = Screen.TripPlanner.route,
            arguments = listOf(navArgument("city") { type = NavType.StringType; nullable = true; defaultValue = null })
        ) { backStackEntry ->
            val city = backStackEntry.arguments?.getString("city")
            TripPlannerScreen(
                initialCity = city,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Safety.route) {
            SafetyScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.LanguageAssistant.route) {
            LanguageAssistantScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Events.route) {
            EventsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.GreenTravel.route) {
            GreenTravelScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Feedback.route) {
            FeedbackScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.TouristDiscovery.route) {
            TouristDiscoveryScreen(
                onBack = { navController.popBackStack() },
                onCityClick = { city ->
                    navController.navigate(Screen.TripPlanner.createRoute(city))
                }
            )
        }
        composable(Screen.FlightComparison.route) {
            FlightComparisonScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDetails = { navController.navigate(Screen.FlightDetails.route) },
                viewModel = flightViewModel
            )
        }
        composable(Screen.FlightDetails.route) {
            FlightDetailsScreen(
                onBack = { navController.popBackStack() },
                viewModel = flightViewModel
            )
        }
        composable(Screen.HotelComparison.route) {
            HotelComparisonScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDetails = { navController.navigate(Screen.HotelDetails.route) },
                viewModel = hotelViewModel
            )
        }
        composable(Screen.HotelDetails.route) {
            HotelDetailsScreen(
                onBack = { navController.popBackStack() },
                viewModel = hotelViewModel
            )
        }
    }
}
