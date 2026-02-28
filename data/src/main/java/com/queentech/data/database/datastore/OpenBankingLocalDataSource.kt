package com.queentech.data.database.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OpenBankingLocalDataSource @Inject constructor(
    // DataStoreModule에서 @Provides로 제공되는 인스턴스가 자동으로 주입됩니다!
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("openbanking_access_token")
        private val KEY_USER_SEQ_NO = stringPreferencesKey("openbanking_user_seq_no")
    }

    // 1. 토큰 및 UserSeqNo 저장
    suspend fun saveTokens(accessToken: String, userSeqNo: String) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_USER_SEQ_NO] = userSeqNo
        }
    }

    // 2. Access Token 읽기 (Flow)
    val accessTokenFlow: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_ACCESS_TOKEN]
    }

    // 3. User Sequence No 읽기 (Flow)
    val userSeqNoFlow: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_USER_SEQ_NO]
    }

    // 4. 오픈뱅킹 연결 해제(또는 로그아웃) 시 토큰만 쏙 지우기
    suspend fun clearTokens() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_USER_SEQ_NO)
        }
    }
}