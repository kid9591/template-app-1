package com.example.todoist.data.remote.response

sealed interface NetworkResponse<out T> {
    data class Success<T>(val data: T) : NetworkResponse<T>
    data class Error(val message: String) : NetworkResponse<Nothing>
}
