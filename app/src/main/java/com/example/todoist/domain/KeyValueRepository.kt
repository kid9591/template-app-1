package com.example.todoist.domain

import kotlinx.coroutines.flow.Flow

interface KeyValueRepository {

    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String): String?
    fun observeString(key: String): Flow<String?>

    suspend fun putInt(key: String, value: Int)
    suspend fun getInt(key: String): Int?
    fun observeInt(key: String): Flow<Int?>

    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun getBoolean(key: String): Boolean?
    fun observeBoolean(key: String): Flow<Boolean?>

    suspend fun putLong(key: String, value: Long)
    suspend fun getLong(key: String): Long?
    fun observeLong(key: String): Flow<Long?>

    suspend fun remove(key: String)
    suspend fun clear()
}
