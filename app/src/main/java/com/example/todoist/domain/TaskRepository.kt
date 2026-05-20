package com.example.todoist.domain

import com.example.todoist.domain.model.Task
import com.example.todoist.domain.model.TaskCategory
import com.example.todoist.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    fun getTaskById(id: String): Flow<Task?>
    suspend fun addTask(title: String, category: TaskCategory, dueDate: LocalDate? = null, time: String? = null)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: String)
    suspend fun sync()
}
