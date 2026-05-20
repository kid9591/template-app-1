package com.example.todoist.data.remote

import com.example.todoist.data.remote.dto.TaskDto
import com.example.todoist.data.remote.response.NetworkResponse
import javax.inject.Inject

class RetrofitTaskSource @Inject constructor(
    private val api: TaskApiService,
) {
    suspend fun fetchTasks(): NetworkResponse<List<TaskDto>> {
        return try {
            NetworkResponse.Success(api.fetchTasks())
        } catch (e: Exception) {
            NetworkResponse.Error(e.message ?: "Unknown network error")
        }
    }
}
