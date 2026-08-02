package com.example.portfolia.ui.navigation

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.portfolia.ui.components.AmbientGlassBackground
import com.example.portfolia.ui.screens.*
import com.example.portfolia.ui.theme.GlassIntensity
import com.example.portfolia.ui.theme.LayoutDensity
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
    var currentAccent by remember { mutableStateOf(ThemeAccent.SAPPHIRE) }
    var currentIntensity by remember { mutableStateOf(GlassIntensity.MEDIUM) }
    var currentDensity by remember { mutableStateOf(LayoutDensity.COMFORTABLE) }

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
                        containerColor = Color(0xFF12141A).copy(alpha = 0.90f),
                        contentColor = Color.White,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .graphicsLayer { shadowElevation = 8f }
                    ) {
                        bottomBarScreens.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            NavigationBarItem(
                                icon = screen.icon,
                                label = { Text(screen.title) },
                                selected = isSelected,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    unselectedIconColor = Color(0xFF6E7178),
                                    selectedTextColor = Color.White,
                                    unselectedTextColor = Color(0xFF6E7178),
                                    indicatorColor = currentAccent.primary.copy(alpha = 0.30f)
                                ),
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
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
                enterTransition = { fadeIn(tween(200)) },
                exitTransition = { fadeOut(tween(200)) }
            ) {
                composable(Screen.Projects.route) {
                    HomeScreen(
                        isGlassmorphism = isGlassmorphism,
                        accent = currentAccent,
                        intensity = currentIntensity,
                        density = currentDensity,
                        onAddProjectClick = { navController.navigate(Screen.AddProject.route) }
                    )
                }
                composable(Screen.References.route) {
                    ReferencesScreen(
                        isGlassmorphism = isGlassmorphism,
                        accent = currentAccent,
                        intensity = currentIntensity,
                        density = currentDensity
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        isGlassmorphism = isGlassmorphism,
                        accent = currentAccent,
                        intensity = currentIntensity
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        isGlassmorphism = isGlassmorphism,
                        accent = currentAccent,
                        intensity = currentIntensity,
                        density = currentDensity,
                        onGlassmorphismToggle = { isGlassmorphism = it },
                        onAccentSelected = { currentAccent = it },
                        onIntensitySelected = { currentIntensity = it },
                        onDensitySelected = { currentDensity = it }
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
