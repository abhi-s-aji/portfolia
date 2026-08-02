package com.example.portfolia.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.portfolia.ui.components.AmbientGlassBackground
import com.example.portfolia.ui.screens.*
import com.example.portfolia.ui.theme.ThemeAccent

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
    var currentAccent by remember { mutableStateOf(ThemeAccent.RED) }

    val bottomBarScreens = listOf(
        Screen.Projects,
        Screen.References,
        Screen.Profile,
        Screen.Settings
    )

    AmbientGlassBackground(enabled = isGlassmorphism, accent = currentAccent) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (currentRoute in bottomBarScreens.map { it.route }) {
                    NavigationBar(
                        containerColor = if (isGlassmorphism) Color.Black.copy(alpha = 0.55f) else Color(0xFF1C1C1E),
                        contentColor = Color.White,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(32.dp))
                    ) {
                        bottomBarScreens.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            NavigationBarItem(
                                icon = screen.icon,
                                label = { Text(screen.title) },
                                selected = isSelected,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    unselectedIconColor = Color(0xFF8E8E93),
                                    selectedTextColor = Color.White,
                                    unselectedTextColor = Color(0xFF8E8E93),
                                    indicatorColor = currentAccent.primary.copy(alpha = 0.35f)
                                ),
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
                // Smooth fade transitions with zero harsh flashes
                enterTransition = { fadeIn(tween(280, easing = FastOutSlowInEasing)) },
                exitTransition = { fadeOut(tween(280, easing = FastOutSlowInEasing)) }
            ) {
                composable(Screen.Projects.route) {
                    HomeScreen(
                        isGlassmorphism = isGlassmorphism,
                        accent = currentAccent,
                        onAddProjectClick = { navController.navigate(Screen.AddProject.route) }
                    )
                }
                composable(Screen.References.route) {
                    ReferencesScreen(isGlassmorphism = isGlassmorphism, accent = currentAccent)
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(isGlassmorphism = isGlassmorphism, accent = currentAccent)
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        isGlassmorphism = isGlassmorphism,
                        accent = currentAccent,
                        onGlassmorphismToggle = { isGlassmorphism = it },
                        onAccentSelected = { currentAccent = it }
                    )
                }
                composable(Screen.AddProject.route) {
                    ProjectFormScreen(
                        accent = currentAccent,
                        onBackClick = { navController.popBackStack() },
                        onProjectSaved = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
