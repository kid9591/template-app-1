package com.example.todoist.screens.addtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoist.domain.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTaskState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<AddTaskEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: AddTaskAction) {
        when (action) {
            is AddTaskAction.TitleChanged -> _uiState.update { it.copy(title = action.title, titleError = null) }
            is AddTaskAction.CategorySelected -> _uiState.update { it.copy(selectedCategory = action.category) }
            is AddTaskAction.DueDateChanged -> _uiState.update { it.copy(dueDate = action.date) }
            is AddTaskAction.TimeChanged -> _uiState.update { it.copy(time = action.time) }
            AddTaskAction.SaveClicked -> save()
        }
    }

    private fun save() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Task title is required") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val dueDate = state.dueDate.takeIf { it.isNotBlank() }
                ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            val time = state.time.takeIf { it.isNotBlank() }
            repository.addTask(state.title.trim(), state.selectedCategory, dueDate, time)
            _events.send(AddTaskEvent.NavigateBack)
        }
    }
}
