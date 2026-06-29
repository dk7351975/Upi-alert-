package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class AuthManager(private val context: Context) {
    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val MPIN = stringPreferencesKey("mpin")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    val mpin: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[MPIN]
        }
        
    val language: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LANGUAGE] ?: "en"
        }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = loggedIn
        }
    }

    suspend fun setMpin(pin: String?) {
        context.dataStore.edit { preferences ->
            if (pin == null) {
                preferences.remove(MPIN)
            } else {
                preferences[MPIN] = pin
            }
        }
    }
    
    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE] = lang
        }
    }
    
    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences.remove(MPIN)
        }
    }
}
