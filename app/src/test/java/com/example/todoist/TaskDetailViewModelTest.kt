package com.example.todoist

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.example.todoist.data.repository.FakeTaskRepository
import com.example.todoist.presentation.taskdetail.TaskDetailAction
import com.example.todoist.presentation.taskdetail.TaskDetailEvent
import com.example.todoist.presentation.taskdetail.TaskDetailViewModel
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
class TaskDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeTaskRepository
    private lateinit var viewModel: TaskDetailViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
        viewModel = TaskDetailViewModel(
            savedStateHandle = SavedStateHandle(),
            repository = repository,
            taskId = "t1",
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads task from repository`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.title).isEqualTo("Market Research")
            assertThat(state.isLoading).isEqualTo(false)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `TitleChanged updates title and clears error`() = runTest {
        viewModel.onAction(TaskDetailAction.TitleChanged(""))
        viewModel.onAction(TaskDetailAction.SaveClicked)
        viewModel.onAction(TaskDetailAction.TitleChanged("Fixed Title"))
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.title).isEqualTo("Fixed Title")
            assertThat(state.titleError).isEqualTo(null)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SaveClicked with blank title sets titleError`() = runTest {
        viewModel.onAction(TaskDetailAction.TitleChanged(""))
        viewModel.onAction(TaskDetailAction.SaveClicked)
        viewModel.uiState.test {
            assertThat(awaitItem().titleError).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SaveClicked with valid title emits NavigateBack`() = runTest {
        viewModel.events.test {
            viewModel.onAction(TaskDetailAction.SaveClicked)
            assertThat(awaitItem() is TaskDetailEvent.NavigateBack).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `DeleteClicked deletes task and emits NavigateBack`() = runTest {
        viewModel.events.test {
            viewModel.onAction(TaskDetailAction.DeleteClicked)
            assertThat(awaitItem() is TaskDetailEvent.NavigateBack).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
        repository.getTasks().test {
            assertThat(awaitItem().none { it.id == "t1" }).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
