package com.example.todoist.data.repository

import android.util.Log
import com.example.todoist.data.cache.room.RoomTaskSource
import com.example.todoist.data.cache.room.entity.toEntity
import com.example.todoist.data.remote.RetrofitTaskSource
import com.example.todoist.data.remote.dto.toDomain
import com.example.todoist.data.remote.response.NetworkResponse
import com.example.todoist.domain.TaskRepository
import com.example.todoist.domain.model.Task
import com.example.todoist.domain.model.TaskCategory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

private const val TAG = "OfflineFirstTaskRepo"

class OfflineFirstTaskRepository @Inject constructor(
    private val local: RoomTaskSource,
    private val remote: RetrofitTaskSource,
) : TaskRepository {

    override fun getTasks(): Flow<List<Task>> = local.observeTasks()

    override fun getTaskById(id: String): Flow<Task?> = local.observeTaskById(id)

    override suspend fun addTask(title: String, category: TaskCategory, dueDate: LocalDate?, time: String?) {
        val task = Task(id = UUID.randomUUID().toString(), title = title, category = category, dueDate = dueDate, time = time)
        local.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) = local.updateTask(task)

    override suspend fun deleteTask(id: String) = local.deleteTask(id)

    override suspend fun sync() {
        when (val result = remote.fetchTasks()) {
            is NetworkResponse.Success -> {
                val entities = result.data.map { it.toDomain().toEntity() }
                local.replaceAll(entities)
            }
            is NetworkResponse.Error -> Log.w(TAG, "Sync failed: ${result.message}")
        }
    }
}
