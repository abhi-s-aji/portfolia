package com.example.portfolia.ui.screens.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.portfolia.data.local.AppDatabase
import com.example.portfolia.data.model.ProjectEntity
import com.example.portfolia.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetailUiState(
    val project: ProjectEntity? = null,
    val isLoading: Boolean = true,
    val isDeleted: Boolean = false
)

class DetailViewModel(
    application: Application,
    private val projectId: Long
) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val projectRepo = ProjectRepository(db.projectDao())

    val uiState: StateFlow<DetailUiState> = projectRepo.getProjectById(projectId)
        .let { flow ->
            val _state = MutableStateFlow(DetailUiState())
            viewModelScope.launch {
                flow.collect { project ->
                    _state.value = DetailUiState(
                        project = project,
                        isLoading = false,
                        isDeleted = _state.value.isDeleted
                    )
                }
            }
            _state
        }

    fun deleteProject(onDeleted: () -> Unit) {
        viewModelScope.launch {
            projectRepo.deleteProjectById(projectId)
            uiState.let {
                (it as MutableStateFlow).value = it.value.copy(isDeleted = true)
            }
            onDeleted()
        }
    }

    class Factory(
        private val application: Application,
        private val projectId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel(application, projectId) as T
        }
    }
}
