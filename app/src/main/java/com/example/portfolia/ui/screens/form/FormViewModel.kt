package com.example.portfolia.ui.screens.form

import android.app.Application
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.portfolia.data.local.AppDatabase
import com.example.portfolia.data.model.ProjectEntity
import com.example.portfolia.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class FormUiState(
    val title: String = "",
    val subtitle: String = "",
    val description: String = "",
    val category: String = "Android",
    val coverImageUri: String? = null,
    val tagsInput: String = "",
    val githubUrl: String = "",
    val liveDemoUrl: String = "",
    val isFeatured: Boolean = false,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    // Validation errors
    val titleError: String? = null,
    val githubUrlError: String? = null,
    val liveDemoUrlError: String? = null
)

class FormViewModel(
    application: Application,
    private val editProjectId: Long?
) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val projectRepo = ProjectRepository(db.projectDao())

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    private var existingProject: ProjectEntity? = null

    init {
        if (editProjectId != null) {
            loadProject(editProjectId)
        }
    }

    private fun loadProject(id: Long) {
        viewModelScope.launch {
            val project = projectRepo.getProjectById(id).first()
            if (project != null) {
                existingProject = project
                _uiState.value = _uiState.value.copy(
                    title = project.title,
                    subtitle = project.subtitle,
                    description = project.description,
                    category = project.category,
                    coverImageUri = project.coverImageUri,
                    tagsInput = project.tags.joinToString(", "),
                    githubUrl = project.githubUrl ?: "",
                    liveDemoUrl = project.liveDemoUrl ?: "",
                    isFeatured = project.isFeatured,
                    isEditing = true
                )
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(
            title = value,
            titleError = null
        )
    }

    fun onSubtitleChange(value: String) {
        _uiState.value = _uiState.value.copy(subtitle = value)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun onCategoryChange(value: String) {
        _uiState.value = _uiState.value.copy(category = value)
    }

    fun onCoverImageSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(coverImageUri = uri?.toString())
    }

    fun onTagsInputChange(value: String) {
        _uiState.value = _uiState.value.copy(tagsInput = value)
    }

    fun onGithubUrlChange(value: String) {
        _uiState.value = _uiState.value.copy(
            githubUrl = value,
            githubUrlError = null
        )
    }

    fun onLiveDemoUrlChange(value: String) {
        _uiState.value = _uiState.value.copy(
            liveDemoUrl = value,
            liveDemoUrlError = null
        )
    }

    fun onFeaturedToggle(value: Boolean) {
        _uiState.value = _uiState.value.copy(isFeatured = value)
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        var hasError = false

        if (state.title.isBlank() || state.title.length < 3) {
            _uiState.value = _uiState.value.copy(
                titleError = if (state.title.isBlank()) "Title is required"
                else "Title must be at least 3 characters"
            )
            hasError = true
        }

        if (state.githubUrl.isNotBlank() && !isValidUrl(state.githubUrl)) {
            _uiState.value = _uiState.value.copy(githubUrlError = "Invalid URL format")
            hasError = true
        }

        if (state.liveDemoUrl.isNotBlank() && !isValidUrl(state.liveDemoUrl)) {
            _uiState.value = _uiState.value.copy(liveDemoUrlError = "Invalid URL format")
            hasError = true
        }

        return !hasError
    }

    private fun isValidUrl(url: String): Boolean {
        val fullUrl = if (!url.startsWith("http")) "https://$url" else url
        return Patterns.WEB_URL.matcher(fullUrl).matches()
    }

    fun saveProject() {
        if (!validate()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            val state = _uiState.value
            val tags = state.tagsInput
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            val project = ProjectEntity(
                id = existingProject?.id ?: 0L,
                title = state.title.trim(),
                subtitle = state.subtitle.trim(),
                description = state.description.trim(),
                category = state.category,
                coverImageUri = state.coverImageUri,
                tags = tags,
                githubUrl = state.githubUrl.ifBlank { null },
                liveDemoUrl = state.liveDemoUrl.ifBlank { null },
                isFeatured = state.isFeatured,
                createdAt = existingProject?.createdAt ?: System.currentTimeMillis()
            )

            if (existingProject != null) {
                projectRepo.updateProject(project)
            } else {
                projectRepo.insertProject(project)
            }

            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
        }
    }

    class Factory(
        private val application: Application,
        private val editProjectId: Long?
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FormViewModel(application, editProjectId) as T
        }
    }
}
