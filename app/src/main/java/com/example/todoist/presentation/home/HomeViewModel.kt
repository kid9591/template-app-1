package com.example.todoist.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoist.domain.TaskRepository
import com.example.todoist.domain.model.TaskStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    private val _isLoading = MutableStateFlow(true)

    val uiState = combine(_isLoading, _filter, repository.getTasks()) { loading, filter, tasks ->
        HomeState(tasks = tasks, activeFilter = filter, isLoading = loading && tasks.isEmpty())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeState(isLoading = true),
    )

    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.sync()
            _isLoading.value = false
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.AddTaskClicked -> viewModelScope.launch {
                _events.send(HomeEvent.NavigateToAddTask)
            }
            is HomeAction.FilterChanged -> _filter.update { action.filter }
            is HomeAction.TaskClicked -> viewModelScope.launch {
                _events.send(HomeEvent.NavigateToTaskDetail(action.taskId))
            }
            is HomeAction.TaskStatusChanged -> viewModelScope.launch {
                val task = uiState.value.tasks.find { it.id == action.taskId } ?: return@launch
                repository.updateTask(task.copy(status = action.status))
            }
        }
    }
}
