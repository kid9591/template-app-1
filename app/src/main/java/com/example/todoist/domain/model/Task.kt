package com.example.todoist.domain.model

import java.time.LocalDate
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val status: TaskStatus = TaskStatus.TODO,
    val category: TaskCategory = TaskCategory.WORK,
    val dueDate: LocalDate? = null,
    val time: String? = null,
)
