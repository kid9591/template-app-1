package com.example.todoist.presentation.home

import com.example.todoist.domain.model.Task
import com.example.todoist.domain.model.TaskStatus

data class HomeState(
    val tasks: List<Task> = emptyList(),
    val activeFilter: TaskFilter = TaskFilter.ALL,
    val isLoading: Boolean = false,
) {
    val filteredTasks: List<Task>
        get() = when (activeFilter) {
            TaskFilter.ALL -> tasks
            TaskFilter.TODO -> tasks.filter { it.status == TaskStatus.TODO }
            TaskFilter.IN_PROGRESS -> tasks.filter { it.status == TaskStatus.IN_PROGRESS }
            TaskFilter.COMPLETED -> tasks.filter { it.status == TaskStatus.DONE }
        }
}

enum class TaskFilter(val label: String) {
    ALL("All"),
    TODO("To do"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
}

sealed interface HomeAction {
    data object AddTaskClicked : HomeAction
    data class FilterChanged(val filter: TaskFilter) : HomeAction
    data class TaskClicked(val taskId: String) : HomeAction
    data class TaskStatusChanged(val taskId: String, val status: TaskStatus) : HomeAction
}

sealed interface HomeEvent {
    data object NavigateToAddTask : HomeEvent
    data class NavigateToTaskDetail(val taskId: String) : HomeEvent
}
