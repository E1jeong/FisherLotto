package com.queentech.data.database.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BillingLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_REISSUE_PENDING = booleanPreferencesKey("reissue_pending")
    }

    // 서버가 이번주 예상번호를 재발급했지만 앱이 아직 반영하지 못한 상태.
    // DataStore는 다른 키가 바뀌어도 스냅샷을 다시 방출하므로 distinctUntilChanged가 필요하다.
    val reissuePendingFlow: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[KEY_REISSUE_PENDING] ?: false }
        .distinctUntilChanged()

    suspend fun setReissuePending(pending: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_REISSUE_PENDING] = pending
        }
    }
}
