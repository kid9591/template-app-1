package com.example.todoist.data.remote

import com.example.todoist.data.remote.dto.TaskDto
import com.example.todoist.data.util.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

private const val BASE_URL = "https://api.fake-todo.com/v1"

class KtorTaskDataSource(private val client: HttpClient) {

    suspend fun fetchTasks(): NetworkResult<List<TaskDto>> {
        return try {
            val tasks = client.get("$BASE_URL/tasks").body<List<TaskDto>>()
            NetworkResult.Success(tasks)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Unknown network error")
        }
    }
}
