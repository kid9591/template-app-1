package com.example.todoist

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.example.todoist.domain.model.Task
import com.example.todoist.domain.model.TaskCategory
import com.example.todoist.domain.model.TaskStatus
import com.example.todoist.presentation.home.HomeAction
import com.example.todoist.presentation.home.HomeScreen
import com.example.todoist.presentation.home.HomeState
import com.example.todoist.presentation.home.TaskFilter
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalTestApi::class)
class HomeScreenTest {

    // Only TODO and DONE — avoids "In Progress" collision with the IN_PROGRESS status badge
    private val sampleTasks = listOf(
        Task("1", "Todo Task", TaskStatus.TODO, TaskCategory.WORK, LocalDate.now()),
        Task("2", "Done Task", TaskStatus.DONE, TaskCategory.PERSONAL, LocalDate.now()),
    )

    @Test
    fun filterChipsAreDisplayed() = runComposeUiTest {
        setContent { HomeScreen(state = HomeState(tasks = sampleTasks), onAction = {}) }

        onNodeWithText("All").assertIsDisplayed()
        onNodeWithText("To do").assertIsDisplayed()
        onNodeWithText("In Progress").assertIsDisplayed()
        onNodeWithText("Completed").assertIsDisplayed()
    }

    @Test
    fun tasksAreDisplayed() = runComposeUiTest {
        setContent { HomeScreen(state = HomeState(tasks = sampleTasks), onAction = {}) }

        onNodeWithText("Todo Task").assertIsDisplayed()
        onNodeWithText("Done Task").assertIsDisplayed()
    }

    @Test
    fun clickingFilterChipEmitsFilterChangedAction() = runComposeUiTest {
        var lastAction: HomeAction? = null
        setContent {
            HomeScreen(state = HomeState(tasks = sampleTasks), onAction = { lastAction = it })
        }

        // "To do" (space) is the chip label; status badge shows "To-do" (hyphen) — no collision
        onNodeWithText("To do").performClick()

        assert(lastAction is HomeAction.FilterChanged)
        assert((lastAction as HomeAction.FilterChanged).filter == TaskFilter.TODO)
    }
}
