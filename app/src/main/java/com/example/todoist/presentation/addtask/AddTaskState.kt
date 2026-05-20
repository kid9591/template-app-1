package com.example.todoist.presentation.addtask

import com.example.todoist.domain.model.TaskCategory

data class AddTaskState(
    val title: String = "",
    val titleError: String? = null,
    val selectedCategory: TaskCategory = TaskCategory.WORK,
    val dueDate: String = "",
    val time: String = "",
    val isSaving: Boolean = false,
)

sealed interface AddTaskAction {
    data class TitleChanged(val title: String) : AddTaskAction
    data class CategorySelected(val category: TaskCategory) : AddTaskAction
    data class DueDateChanged(val date: String) : AddTaskAction
    data class TimeChanged(val time: String) : AddTaskAction
    data object SaveClicked : AddTaskAction
}

sealed interface AddTaskEvent {
    data object NavigateBack : AddTaskEvent
}
