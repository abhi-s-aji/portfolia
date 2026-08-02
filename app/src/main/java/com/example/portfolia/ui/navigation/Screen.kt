package com.example.portfolia.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object ProjectDetail : Screen("project_detail/{projectId}") {
        fun createRoute(projectId: Long) = "project_detail/$projectId"
    }
    data object ProjectForm : Screen("project_form?projectId={projectId}") {
        fun createRoute(projectId: Long? = null): String {
            return if (projectId != null) "project_form?projectId=$projectId"
            else "project_form"
        }
    }
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
}
