package com.example.todoist.screens.taskdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.todoist.domain.TaskRepository
import com.example.todoist.domain.model.Task
import com.example.todoist.screens.navigation.TaskDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TaskRepository,
) : ViewModel() {

    private val taskId: String = savedStateHandle.toRoute<TaskDetailRoute>().taskId

    private val _uiState = MutableStateFlow(TaskDetailState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<TaskDetailEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId).firstOrNull()
            if (task != null) {
                _uiState.update {
                    it.copy(
                        title = task.title,
                        category = task.category,
                        status = task.status,
                        dueDate = task.dueDate?.toString() ?: "",
                        time = task.time ?: "",
                        isLoading = false,
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onAction(action: TaskDetailAction) {
        when (action) {
            is TaskDetailAction.TitleChanged -> _uiState.update { it.copy(title = action.title, titleError = null) }
            is TaskDetailAction.CategorySelected -> _uiState.update { it.copy(category = action.category) }
            is TaskDetailAction.StatusSelected -> _uiState.update { it.copy(status = action.status) }
            is TaskDetailAction.DueDateChanged -> _uiState.update { it.copy(dueDate = action.date) }
            is TaskDetailAction.TimeChanged -> _uiState.update { it.copy(time = action.time) }
            TaskDetailAction.SaveClicked -> save()
            TaskDetailAction.DeleteClicked -> delete()
        }
    }

    private fun save() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title is required") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val dueDate = state.dueDate.takeIf { it.isNotBlank() }
                ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            val time = state.time.takeIf { it.isNotBlank() }
            repository.updateTask(
                Task(
                    id = taskId,
                    title = state.title.trim(),
                    category = state.category,
                    status = state.status,
                    dueDate = dueDate,
                    time = time,
                )
            )
            _events.send(TaskDetailEvent.NavigateBack)
        }
    }

    private fun delete() {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            _events.send(TaskDetailEvent.NavigateBack)
        }
    }
}
