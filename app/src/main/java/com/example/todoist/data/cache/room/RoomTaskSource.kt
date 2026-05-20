package com.example.todoist.data.cache.room

import com.example.todoist.data.cache.room.dao.TaskDao
import com.example.todoist.data.cache.room.entity.TaskEntity
import com.example.todoist.data.cache.room.entity.toDomain
import com.example.todoist.data.cache.room.entity.toEntity
import com.example.todoist.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomTaskSource @Inject constructor(private val dao: TaskDao) {

    fun observeTasks(): Flow<List<Task>> = dao.observeTasks().map { it.map { e -> e.toDomain() } }

    fun observeTaskById(id: String): Flow<Task?> = dao.observeTaskById(id).map { it?.toDomain() }

    suspend fun insertTask(task: TaskEntity) = dao.insertTask(task)

    suspend fun updateTask(task: Task) = dao.insertTask(task.toEntity())

    suspend fun deleteTask(id: String) = dao.deleteTask(id)

    suspend fun replaceAll(tasks: List<TaskEntity>) {
        dao.clearTasks()
        dao.insertTasks(tasks)
    }
}
