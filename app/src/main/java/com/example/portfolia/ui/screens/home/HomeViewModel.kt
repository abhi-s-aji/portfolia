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
