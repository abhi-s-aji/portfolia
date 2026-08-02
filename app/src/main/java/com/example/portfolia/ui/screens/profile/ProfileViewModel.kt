package com.example.portfolia.ui.screens.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.portfolia.data.local.AppDatabase
import com.example.portfolia.data.model.UserProfileEntity
import com.example.portfolia.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val fullName: String = "",
    val headline: String = "",
    val bio: String = "",
    val email: String = "",
    val githubHandle: String = "",
    val linkedinHandle: String = "",
    val avatarUri: String? = null,
    val resumePdfUri: String? = null,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val hasExistingProfile: Boolean = false
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val profileRepo = ProfileRepository(db.profileDao())

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            profileRepo.getProfile().collect { profile ->
                if (profile != null) {
                    _uiState.value = _uiState.value.copy(
                        fullName = profile.fullName,
                        headline = profile.headline,
                        bio = profile.bio,
                        email = profile.email,
                        githubHandle = profile.githubHandle,
                        linkedinHandle = profile.linkedinHandle,
                        avatarUri = profile.avatarUri,
                        resumePdfUri = profile.resumePdfUri,
                        hasExistingProfile = true
                    )
                }
            }
        }
    }

    fun toggleEditing() {
        _uiState.value = _uiState.value.copy(isEditing = !_uiState.value.isEditing)
    }

    fun onFullNameChange(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value)
    }

    fun onHeadlineChange(value: String) {
        _uiState.value = _uiState.value.copy(headline = value)
    }

    fun onBioChange(value: String) {
        _uiState.value = _uiState.value.copy(bio = value)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun onGithubHandleChange(value: String) {
        _uiState.value = _uiState.value.copy(githubHandle = value)
    }

    fun onLinkedinHandleChange(value: String) {
        _uiState.value = _uiState.value.copy(linkedinHandle = value)
    }

    fun onAvatarSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(avatarUri = uri?.toString())
    }

    fun onResumeSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(resumePdfUri = uri?.toString())
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            val state = _uiState.value
            val profile = UserProfileEntity(
                id = 1L,
                fullName = state.fullName.trim(),
                headline = state.headline.trim(),
                bio = state.bio.trim(),
                email = state.email.trim(),
                githubHandle = state.githubHandle.trim(),
                linkedinHandle = state.linkedinHandle.trim(),
                avatarUri = state.avatarUri,
                resumePdfUri = state.resumePdfUri
            )

            profileRepo.saveProfile(profile)
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                isEditing = false,
                hasExistingProfile = true
            )
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(application) as T
        }
    }
}
