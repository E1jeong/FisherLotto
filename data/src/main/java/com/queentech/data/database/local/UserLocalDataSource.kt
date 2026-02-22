package com.queentech.data.database.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.queentech.domain.model.login.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_NAME = stringPreferencesKey("name")
        private val KEY_BIRTH = stringPreferencesKey("birth")
        private val KEY_PHONE = stringPreferencesKey("phone")
        private val KEY_EMAIL = stringPreferencesKey("email")
    }

    // 저장
    suspend fun saveUser(user: User) {
        dataStore.edit { prefs ->
            prefs[KEY_NAME] = user.name
            prefs[KEY_EMAIL] = user.email
            prefs[KEY_BIRTH] = user.birth
            prefs[KEY_PHONE] = user.phone
        }
    }

    // 읽기 (Flow)
    val userFlow: Flow<User?> = dataStore.data.map { prefs ->
        val email = prefs[KEY_EMAIL] ?: return@map null
        User(
            name = prefs[KEY_NAME] ?: "",
            birth = prefs[KEY_BIRTH] ?: "",
            phone = prefs[KEY_PHONE] ?: "",
            email = email
        )
    }

    // 삭제 (로그아웃 시)
    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}