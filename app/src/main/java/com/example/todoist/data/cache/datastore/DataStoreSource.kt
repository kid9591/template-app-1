package com.example.todoist.data.cache.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.todoist.domain.SimpleKVRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SimpleKVRepository {

    override suspend fun putString(key: String, value: String) {
        dataStore.edit { it[stringPreferencesKey(key)] = value }
    }

    override suspend fun getString(key: String): String? =
        dataStore.data.map { it[stringPreferencesKey(key)] }.first()

    override fun observeString(key: String): Flow<String?> =
        dataStore.data.map { it[stringPreferencesKey(key)] }

    override suspend fun putInt(key: String, value: Int) {
        dataStore.edit { it[intPreferencesKey(key)] = value }
    }

    override suspend fun getInt(key: String): Int? =
        dataStore.data.map { it[intPreferencesKey(key)] }.first()

    override fun observeInt(key: String): Flow<Int?> =
        dataStore.data.map { it[intPreferencesKey(key)] }

    override suspend fun putBoolean(key: String, value: Boolean) {
        dataStore.edit { it[booleanPreferencesKey(key)] = value }
    }

    override suspend fun getBoolean(key: String): Boolean? =
        dataStore.data.map { it[booleanPreferencesKey(key)] }.first()

    override fun observeBoolean(key: String): Flow<Boolean?> =
        dataStore.data.map { it[booleanPreferencesKey(key)] }

    override suspend fun putLong(key: String, value: Long) {
        dataStore.edit { it[longPreferencesKey(key)] = value }
    }

    override suspend fun getLong(key: String): Long? =
        dataStore.data.map { it[longPreferencesKey(key)] }.first()

    override fun observeLong(key: String): Flow<Long?> =
        dataStore.data.map { it[longPreferencesKey(key)] }

    override suspend fun remove(key: String) {
        dataStore.edit { prefs ->
            prefs.remove(stringPreferencesKey(key))
            prefs.remove(intPreferencesKey(key))
            prefs.remove(booleanPreferencesKey(key))
            prefs.remove(longPreferencesKey(key))
        }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}
