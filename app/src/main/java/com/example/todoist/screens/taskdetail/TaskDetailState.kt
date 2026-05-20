package com.example.todoist.screens.taskdetail

import com.example.todoist.domain.model.TaskCategory
import com.example.todoist.domain.model.TaskStatus

data class TaskDetailState(
    val title: String = "",
    val category: TaskCategory = TaskCategory.WORK,
    val status: TaskStatus = TaskStatus.TODO,
    val dueDate: String = "",
    val time: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val titleError: String? = null,
)

sealed interface TaskDetailAction {
    data class TitleChanged(val title: String) : TaskDetailAction
    data class CategorySelected(val category: TaskCategory) : TaskDetailAction
    data class StatusSelected(val status: TaskStatus) : TaskDetailAction
    data class DueDateChanged(val date: String) : TaskDetailAction
    data class TimeChanged(val time: String) : TaskDetailAction
    data object SaveClicked : TaskDetailAction
    data object DeleteClicked : TaskDetailAction
}

sealed interface TaskDetailEvent {
    data object NavigateBack : TaskDetailEvent
}
