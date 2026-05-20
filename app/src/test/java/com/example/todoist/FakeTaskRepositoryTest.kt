package com.example.todoist

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.example.todoist.data.repository.FakeTaskRepository
import com.example.todoist.domain.model.Task
import com.example.todoist.domain.model.TaskCategory
import com.example.todoist.domain.model.TaskStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FakeTaskRepositoryTest {

    private lateinit var repository: FakeTaskRepository

    @BeforeEach
    fun setup() {
        repository = FakeTaskRepository()
    }

    @Test
    fun `getTasks returns all 9 initial tasks`() = runTest {
        repository.getTasks().test {
            assertThat(awaitItem()).hasSize(9)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addTask adds a new task`() = runTest {
        repository.addTask("New Task", TaskCategory.WORK)
        repository.getTasks().test {
            val tasks = awaitItem()
            assertThat(tasks).hasSize(10)
            assertThat(tasks.last().title).isEqualTo("New Task")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateTask updates existing task title`() = runTest {
        repository.getTasks().test {
            val original = awaitItem().first { it.id == "t1" }
            repository.updateTask(original.copy(title = "Updated Title", status = TaskStatus.DONE))
            val updated = awaitItem().first { it.id == "t1" }
            assertThat(updated.title).isEqualTo("Updated Title")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteTask removes task`() = runTest {
        repository.deleteTask("t1")
        repository.getTasks().test {
            val tasks = awaitItem()
            assertThat(tasks).hasSize(8)
            assertThat(tasks.none { it.id == "t1" }).isEqualTo(true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTaskById returns correct task`() = runTest {
        repository.getTaskById("t2").test {
            val task = awaitItem()
            assertThat(task).isNotNull()
            assertThat(task!!.id).isEqualTo("t2")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTaskById returns null for unknown id`() = runTest {
        repository.getTaskById("unknown").test {
            assertThat(awaitItem()).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
