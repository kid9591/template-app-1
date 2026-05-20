package com.example.todoist.data.remote.dto

import com.example.todoist.domain.model.Task
import com.example.todoist.domain.model.TaskCategory
import com.example.todoist.domain.model.TaskStatus
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class TaskDto(
    val id: String,
    val title: String,
    val status: String,
    val category: String,
    val dueDate: String? = null,
    val time: String? = null,
)

fun TaskDto.toDomain(): Task = Task(
    id = id,
    title = title,
    status = runCatching { TaskStatus.valueOf(status) }.getOrDefault(TaskStatus.TODO),
    category = runCatching { TaskCategory.valueOf(category) }.getOrDefault(TaskCategory.WORK),
    dueDate = dueDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
    time = time,
)
