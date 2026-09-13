package com.forgefit.android.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.forgefit.android.ForgeFitApplication
import com.forgefit.android.ui.exercises.ExercisesScreen
import com.forgefit.android.ui.home.HomeScreen
import com.forgefit.android.ui.onboarding.OnboardingScreen
import com.forgefit.android.ui.profile.ProfileScreen
import com.forgefit.android.ui.progress.ProgressScreen
import com.forgefit.android.ui.workout.WorkoutScreen

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Workout : Screen("workout", "Workout", Icons.Default.FitnessCenter)
    object Exercises : Screen("exercises", "Exercises", Icons.Default.List)
    object Progress : Screen("progress", "Progress", Icons.Default.TrendingUp)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Onboarding : Screen("onboarding", "Setup", Icons.Default.AccountCircle)
}

@Composable
fun ForgeFitNavigation(
    app: ForgeFitApplication,
    startDestination: String
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            if (startDestination != Screen.Onboarding.route) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    app = app,
                    onComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.Home.route) {
                HomeScreen(app = app)
            }
            
            composable(Screen.Workout.route) {
                WorkoutScreen(app = app)
            }
            
            composable(Screen.Exercises.route) {
                ExercisesScreen(app = app)
            }
            
            composable(Screen.Progress.route) {
                ProgressScreen(app = app)
            }
            
            composable(Screen.Profile.route) {
                ProfileScreen(app = app)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Workout,
        Screen.Exercises,
        Screen.Progress,
        Screen.Profile
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
