package com.example.portfolia.ui.navigation

import android.app.Application
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.portfolia.ui.screens.detail.DetailViewModel
import com.example.portfolia.ui.screens.detail.ProjectDetailScreen
import com.example.portfolia.ui.screens.form.FormViewModel
import com.example.portfolia.ui.screens.form.ProjectFormScreen
import com.example.portfolia.ui.screens.home.HomeScreen
import com.example.portfolia.ui.screens.home.HomeViewModel
import com.example.portfolia.ui.screens.profile.ProfileScreen
import com.example.portfolia.ui.screens.profile.ProfileViewModel
import com.example.portfolia.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    application: Application,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
        },
        exitTransition = {
            fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
        },
        popEnterTransition = {
            fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
        },
        popExitTransition = {
            fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
        }
    ) {
        composable(route = Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(application)
            )
            HomeScreen(
                viewModel = homeViewModel,
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                },
                onAddProject = {
                    navController.navigate(Screen.ProjectForm.createRoute())
                },
                onEditProject = { projectId ->
                    navController.navigate(Screen.ProjectForm.createRoute(projectId))
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.ProjectDetail.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
            val detailViewModel: DetailViewModel = viewModel(
                factory = DetailViewModel.Factory(application, projectId)
            )
            ProjectDetailScreen(
                viewModel = detailViewModel,
                onBackClick = { navController.popBackStack() },
                onEditClick = {
                    navController.navigate(Screen.ProjectForm.createRoute(projectId))
                }
            )
        }

        composable(
            route = Screen.ProjectForm.route,
            arguments = listOf(
                navArgument("projectId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: -1L
            val editId = if (projectId == -1L) null else projectId
            val formViewModel: FormViewModel = viewModel(
                factory = FormViewModel.Factory(application, editId)
            )
            ProjectFormScreen(
                viewModel = formViewModel,
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.Factory(application)
            )
            ProfileScreen(
                viewModel = profileViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                application = application,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
