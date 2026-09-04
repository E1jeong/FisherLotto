package com.queentech.data.database.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Named

class FcmLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @Named("notification_preferences")
    private val notificationPreferences: DataStore<Preferences>,
) {
    companion object {
        private val KEY_FCM_TOKEN = stringPreferencesKey("fcm_token")
        private val KEY_FCM_EMAIL = stringPreferencesKey("fcm_email")
        private val KEY_NOTIFICATION_PERMISSION_PROMPT_SHOWN =
            booleanPreferencesKey("notification_permission_prompt_shown")
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[KEY_FCM_TOKEN] = token
        }
    }

    suspend fun getToken(): String? {
        return dataStore.data.first()[KEY_FCM_TOKEN]
    }

    suspend fun saveEmail(email: String) {
        dataStore.edit { prefs ->
            prefs[KEY_FCM_EMAIL] = email
        }
    }

    suspend fun getEmail(): String? {
        return dataStore.data.first()[KEY_FCM_EMAIL]
    }

    suspend fun hasShownNotificationPermissionPrompt(): Boolean {
        return notificationPreferences.data.first()[KEY_NOTIFICATION_PERMISSION_PROMPT_SHOWN]
            ?: false
    }

    suspend fun markNotificationPermissionPromptShown() {
        notificationPreferences.edit { prefs ->
            prefs[KEY_NOTIFICATION_PERMISSION_PROMPT_SHOWN] = true
        }
    }
}
