package com.example.portfolia.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.portfolia.ui.screens.*

sealed class Screen(val route: String, val title: String, val icon: @Composable () -> Unit) {
    object Projects : Screen("projects", "Projects", { Icon(Icons.Default.Folder, contentDescription = null) })
    object References : Screen("references", "Links", { Icon(Icons.Default.Bookmark, contentDescription = null) })
    object Profile : Screen("profile", "Profile", { Icon(Icons.Default.Person, contentDescription = null) })
    object Settings : Screen("settings", "Settings", { Icon(Icons.Default.Settings, contentDescription = null) })
    object AddProject : Screen("add_project", "Add Project", { Icon(Icons.Default.Add, contentDescription = null) })
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var isGlassmorphism by remember { mutableStateOf(true) }

    val bottomBarScreens = listOf(
        Screen.Projects,
        Screen.References,
        Screen.Profile,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarScreens.map { it.route }) {
                NavigationBar {
                    bottomBarScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = screen.icon,
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
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
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Projects.route,
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn(tween(200)) + slideInVertically { it / 8 } },
            exitTransition = { fadeOut(tween(200)) + slideOutVertically { -it / 8 } },
            popEnterTransition = { fadeIn(tween(200)) + slideInVertically { -it / 8 } },
            popExitTransition = { fadeOut(tween(200)) + slideOutVertically { it / 8 } }
        ) {
            composable(Screen.Projects.route) {
                HomeScreen(
                    isGlassmorphism = isGlassmorphism,
                    onAddProjectClick = { navController.navigate(Screen.AddProject.route) }
                )
            }
            composable(Screen.References.route) {
                ReferencesScreen(isGlassmorphism = isGlassmorphism)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(isGlassmorphism = isGlassmorphism)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    isGlassmorphism = isGlassmorphism,
                    onGlassmorphismToggle = { isGlassmorphism = it }
                )
            }
            composable(Screen.AddProject.route) {
                ProjectFormScreen(
                    onBackClick = { navController.popBackStack() },
                    onProjectSaved = { navController.popBackStack() }
                )
            }
        }
    }
}
