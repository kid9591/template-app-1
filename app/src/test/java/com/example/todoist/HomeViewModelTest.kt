package com.example.todoist

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.example.todoist.data.repository.FakeTaskRepository
import com.example.todoist.presentation.home.HomeAction
import com.example.todoist.presentation.home.HomeEvent
import com.example.todoist.presentation.home.HomeViewModel
import com.example.todoist.presentation.home.TaskFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeTaskRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
        viewModel = HomeViewModel(repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads all 9 tasks`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem().tasks.size).isEqualTo(9)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `AddTaskClicked emits NavigateToAddTask event`() = runTest {
        viewModel.events.test {
            viewModel.onAction(HomeAction.AddTaskClicked)
            assertThat(awaitItem() is HomeEvent.NavigateToAddTask).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `TaskClicked emits NavigateToTaskDetail with correct id`() = runTest {
        viewModel.events.test {
            viewModel.onAction(HomeAction.TaskClicked("t1"))
            val event = awaitItem() as HomeEvent.NavigateToTaskDetail
            assertThat(event.taskId).isEqualTo("t1")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `FilterChanged updates activeFilter and filteredTasks`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.onAction(HomeAction.FilterChanged(TaskFilter.TODO))
            val state = awaitItem()
            assertThat(state.activeFilter).isEqualTo(TaskFilter.TODO)
            assertThat(state.filteredTasks.isNotEmpty()).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `FilterChanged to IN_PROGRESS shows only in-progress tasks`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.onAction(HomeAction.FilterChanged(TaskFilter.IN_PROGRESS))
            val state = awaitItem()
            assertThat(state.filteredTasks.all {
                it.status.name == "IN_PROGRESS"
            }).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
