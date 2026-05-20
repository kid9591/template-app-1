package com.example.todoist.data.repository

import com.example.todoist.domain.TaskRepository
import com.example.todoist.domain.model.Task
import com.example.todoist.domain.model.TaskCategory
import com.example.todoist.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.util.UUID

class FakeTaskRepository : TaskRepository {

    private val today = LocalDate.now()

    private val initialTasks = listOf(
        Task("t1", "Market Research", TaskStatus.DONE, TaskCategory.WORK, today, "10:00 AM"),
        Task("t2", "Create Low-fidelity Wireframe", TaskStatus.IN_PROGRESS, TaskCategory.WORK, today, "07:00 PM"),
        Task("t3", "Competitive Analysis", TaskStatus.IN_PROGRESS, TaskCategory.PERSONAL, today, "12:00 PM"),
        Task("t4", "Uber Eats redesign", TaskStatus.IN_PROGRESS, TaskCategory.WORK, today, "09:00 PM"),
        Task("t5", "How to pitch a Design Sprint", TaskStatus.TODO, TaskCategory.STUDY, today, "03:00 PM"),
        Task("t6", "UI Design", TaskStatus.TODO, TaskCategory.WORK, today.plusDays(1), "09:00 AM"),
        Task("t7", "User Research", TaskStatus.DONE, TaskCategory.PERSONAL, today.plusDays(1), null),
        Task("t8", "Read Design Sprint Book", TaskStatus.TODO, TaskCategory.STUDY, today, "06:00 PM"),
        Task("t9", "Watch Sprint Videos", TaskStatus.DONE, TaskCategory.STUDY, today.plusDays(1), null),
    )

    private val _tasks = MutableStateFlow(initialTasks)

    override fun getTasks(): Flow<List<Task>> = _tasks

    override fun getTaskById(id: String): Flow<Task?> = _tasks.map { it.find { t -> t.id == id } }

    override suspend fun addTask(title: String, category: TaskCategory, dueDate: LocalDate?, time: String?) {
        _tasks.update { it + Task(id = UUID.randomUUID().toString(), title = title, category = category, dueDate = dueDate, time = time) }
    }

    override suspend fun updateTask(task: Task) {
        _tasks.update { tasks -> tasks.map { if (it.id == task.id) task else it } }
    }

    override suspend fun deleteTask(id: String) {
        _tasks.update { tasks -> tasks.filter { it.id != id } }
    }

    override suspend fun sync() = Unit
}
