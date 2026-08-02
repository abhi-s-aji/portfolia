package com.example.portfolia.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.portfolia.ui.screens.HomeScreen
import com.example.portfolia.ui.screens.ProfileScreen
import com.example.portfolia.ui.screens.ProjectFormScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddProject : Screen("add_project")
    object Profile : Screen("profile")
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = { fadeIn(tween(250)) + slideInHorizontally { it / 3 } },
        exitTransition = { fadeOut(tween(250)) + slideOutHorizontally { -it / 3 } },
        popEnterTransition = { fadeIn(tween(250)) + slideInHorizontally { -it / 3 } },
        popExitTransition = { fadeOut(tween(250)) + slideOutHorizontally { it / 3 } }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAddProjectClick = { navController.navigate(Screen.AddProject.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) }
            )
        }
        composable(Screen.AddProject.route) {
            ProjectFormScreen(
                onBackClick = { navController.popBackStack() },
                onProjectSaved = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
