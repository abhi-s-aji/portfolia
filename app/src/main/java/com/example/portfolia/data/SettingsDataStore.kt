package com.example.portfolia.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    companion object {
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val CURRENT_ACCENT = stringPreferencesKey("current_accent")
        val LAYOUT_DENSITY = stringPreferencesKey("layout_density")
        val DEFAULT_EXPORT_FORMAT = stringPreferencesKey("default_export_format")
        val DEFAULT_WEBSITE_VIBE = stringPreferencesKey("default_website_vibe")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_ONBOARDING_COMPLETED] ?: false
    }

    val currentAccent: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CURRENT_ACCENT] ?: "OBSIDIAN_DARK"
    }

    val layoutDensity: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LAYOUT_DENSITY] ?: "COMFORTABLE"
    }

    val defaultExportFormat: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_EXPORT_FORMAT] ?: "MARKDOWN"
    }

    val defaultWebsiteVibe: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_WEBSITE_VIBE] ?: "OBSIDIAN_DARK"
    }

    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: "SYSTEM"
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setCurrentAccent(accent: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENT_ACCENT] = accent
        }
    }

    suspend fun setLayoutDensity(density: String) {
        context.dataStore.edit { preferences ->
            preferences[LAYOUT_DENSITY] = density
        }
    }

    suspend fun setDefaultExportFormat(format: String) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_EXPORT_FORMAT] = format
        }
    }

    suspend fun setDefaultWebsiteVibe(vibe: String) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_WEBSITE_VIBE] = vibe
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
