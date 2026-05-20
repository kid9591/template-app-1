package com.example.todoist

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.example.todoist.data.repository.FakeTaskRepository
import com.example.todoist.domain.model.TaskCategory
import com.example.todoist.presentation.addtask.AddTaskAction
import com.example.todoist.presentation.addtask.AddTaskEvent
import com.example.todoist.presentation.addtask.AddTaskViewModel
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
class AddTaskViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeTaskRepository
    private lateinit var viewModel: AddTaskViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
        viewModel = AddTaskViewModel(repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty with no errors`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.title).isEqualTo("")
            assertThat(state.titleError).isNull()
            assertThat(state.isSaving).isEqualTo(false)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `TitleChanged updates title and clears error`() = runTest {
        viewModel.onAction(AddTaskAction.SaveClicked)
        viewModel.onAction(AddTaskAction.TitleChanged("My Task"))
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.title).isEqualTo("My Task")
            assertThat(state.titleError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `CategorySelected updates selected category`() = runTest {
        viewModel.onAction(AddTaskAction.CategorySelected(TaskCategory.PERSONAL))
        viewModel.uiState.test {
            assertThat(awaitItem().selectedCategory).isEqualTo(TaskCategory.PERSONAL)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SaveClicked with blank title sets titleError`() = runTest {
        viewModel.onAction(AddTaskAction.SaveClicked)
        viewModel.uiState.test {
            assertThat(awaitItem().titleError).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SaveClicked with valid title emits NavigateBack`() = runTest {
        viewModel.onAction(AddTaskAction.TitleChanged("Valid Task"))
        viewModel.events.test {
            viewModel.onAction(AddTaskAction.SaveClicked)
            assertThat(awaitItem() is AddTaskEvent.NavigateBack).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SaveClicked with valid title adds task to repository`() = runTest {
        viewModel.onAction(AddTaskAction.TitleChanged("New Task"))
        viewModel.onAction(AddTaskAction.SaveClicked)
        repository.getTasks().test {
            val tasks = awaitItem()
            assertThat(tasks.size).isEqualTo(10)
            assertThat(tasks.any { it.title == "New Task" }).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
