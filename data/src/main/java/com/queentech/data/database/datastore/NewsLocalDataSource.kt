package com.queentech.data.database.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class NewsCache(
    val json: String,
    val fetchedAtEpochMillis: Long,
)

class NewsLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_NEWS_CACHE = stringPreferencesKey("news_cache")
        private val KEY_NEWS_FETCHED_AT = longPreferencesKey("news_fetched_at")
    }

    suspend fun getCache(): NewsCache? {
        val prefs = dataStore.data.first()
        val json = prefs[KEY_NEWS_CACHE] ?: return null
        return NewsCache(
            json = json,
            fetchedAtEpochMillis = prefs[KEY_NEWS_FETCHED_AT] ?: 0L,
        )
    }

    suspend fun saveCache(json: String, fetchedAtEpochMillis: Long) {
        dataStore.edit { prefs ->
            prefs[KEY_NEWS_CACHE] = json
            prefs[KEY_NEWS_FETCHED_AT] = fetchedAtEpochMillis
        }
    }
}
