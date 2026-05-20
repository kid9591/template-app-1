package com.example.todoist.data.remote

import com.example.todoist.data.remote.dto.TaskDto
import retrofit2.http.GET

interface TaskApiService {
    @GET("tasks")
    suspend fun fetchTasks(): List<TaskDto>
}
