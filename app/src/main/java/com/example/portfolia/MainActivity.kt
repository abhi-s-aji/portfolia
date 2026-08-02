package com.example.portfolia

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.portfolia.ui.navigation.NavGraph
import com.example.portfolia.ui.theme.PortfoliaTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "portfolia_settings")
private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val systemDarkTheme = isSystemInDarkTheme()

            val storedTheme by dataStore.data
                .map { prefs -> prefs[DARK_THEME_KEY] }
                .collectAsStateWithLifecycle(initialValue = null)

            val isDarkTheme = storedTheme ?: systemDarkTheme

            PortfoliaTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                val scope = androidx.compose.runtime.rememberCoroutineScope()

                NavGraph(
                    navController = navController,
                    application = application,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { enabled ->
                        scope.launch {
                            dataStore.edit { prefs ->
                                prefs[DARK_THEME_KEY] = enabled
                            }
                        }
                    }
                )
            }
        }
    }
}
