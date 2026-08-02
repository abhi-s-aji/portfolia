package com.example.portfolia.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.portfolia.data.local.AppDatabase
import com.example.portfolia.data.model.ProjectEntity
import com.example.portfolia.data.repository.ProjectRepository
import com.example.portfolia.data.repository.ProfileRepository
import com.example.portfolia.data.model.UserProfileEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val projects: List<ProjectEntity> = emptyList(),
    val featuredProjects: List<ProjectEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val profile: UserProfileEntity? = null,
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val projectRepo = ProjectRepository(db.projectDao())
    private val profileRepo = ProfileRepository(db.profileDao())

    init {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            // Seed profile if empty
            if (profileRepo.getProfileSnapshot() == null) {
                profileRepo.saveProfile(
                    UserProfileEntity(
                        id = 1L,
                        fullName = "Abhi S Aji",
                        headline = "Android & Web Developer",
                        bio = "Passionate developer building modern mobile and web applications with beautiful interfaces and clean architecture.",
                        email = "developer@example.com",
                        githubHandle = "abhi-s-aji",
                        linkedinHandle = "abhi-s-aji"
                    )
                )
            }
            // Seed projects if empty
            if (projectRepo.getAllProjectsSnapshot().isEmpty()) {
                projectRepo.insertProject(
                    ProjectEntity(
                        title = "Portfolia App",
                        subtitle = "Developer Portfolio Android App",
                        description = "A premium Android application designed to showcase a developer's projects and resume. Built entirely with Jetpack Compose, Room database, and Material Design 3.",
                        category = "Android",
                        tags = listOf("Kotlin", "Jetpack Compose", "Room", "Material 3", "MVVM"),
                        githubUrl = "https://github.com/abhi-s-aji/portfolia",
                        isFeatured = true
                    )
                )
                projectRepo.insertProject(
                    ProjectEntity(
                        title = "Portfolia Web Portal",
                        subtitle = "Portfolio Web Application",
                        description = "A modern web portal built using React and Tailwind CSS, featuring smooth animations, responsive layout, and direct integration with GitHub API.",
                        category = "Web",
                        tags = listOf("React", "TypeScript", "Tailwind CSS", "Vite"),
                        githubUrl = "https://github.com/abhi-s-aji/portfolia-web",
                        isFeatured = false
                    )
                )
            }
        }
    }

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("All")

    private val filteredProjects = combine(_searchQuery, _selectedCategory) { query, category ->
        Pair(query, category)
    }.flatMapLatest { (query, category) ->
        projectRepo.searchProjects(query, category)
    }

    private val featuredProjects = projectRepo.getFeaturedProjects()
    private val profile = profileRepo.getProfile()

    val uiState: StateFlow<HomeUiState> = combine(
        filteredProjects,
        featuredProjects,
        profile,
        _searchQuery,
        _selectedCategory
    ) { projects, featured, prof, query, category ->
        HomeUiState(
            projects = projects,
            featuredProjects = featured,
            searchQuery = query,
            selectedCategory = category,
            profile = prof,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            projectRepo.deleteProject(project)
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(application) as T
        }
    }
}
