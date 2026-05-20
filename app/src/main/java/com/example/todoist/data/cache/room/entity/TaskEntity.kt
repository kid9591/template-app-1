package com.example.todoist.data.cache.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoist.domain.model.Task
import com.example.todoist.domain.model.TaskCategory
import com.example.todoist.domain.model.TaskStatus
import java.time.LocalDate

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val status: String,
    val category: String,
    val dueDate: Long?,
    val time: String?,
)

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    status = runCatching { TaskStatus.valueOf(status) }.getOrDefault(TaskStatus.TODO),
    category = runCatching { TaskCategory.valueOf(category) }.getOrDefault(TaskCategory.WORK),
    dueDate = dueDate?.let { LocalDate.ofEpochDay(it) },
    time = time,
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    status = status.name,
    category = category.name,
    dueDate = dueDate?.toEpochDay(),
    time = time,
)
