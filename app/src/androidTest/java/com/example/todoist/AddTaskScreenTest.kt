package com.example.todoist

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.example.todoist.presentation.addtask.AddTaskAction
import com.example.todoist.presentation.addtask.AddTaskScreen
import com.example.todoist.presentation.addtask.AddTaskState
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class AddTaskScreenTest {

    @Test
    fun addTaskButtonIsDisplayed() = runComposeUiTest {
        setContent { AddTaskScreen(state = AddTaskState(), onAction = {}, onNavigateBack = {}) }

        // hasClickAction disambiguates from the TopAppBar title that also reads "Add Task"
        onNode(hasText("Add Task") and hasClickAction()).assertIsDisplayed()
    }

    @Test
    fun titleErrorIsDisplayedWhenSet() = runComposeUiTest {
        setContent {
            AddTaskScreen(
                state = AddTaskState(titleError = "Task title is required"),
                onAction = {},
                onNavigateBack = {},
            )
        }

        onNodeWithText("Task title is required").assertIsDisplayed()
    }

    @Test
    fun clickingSaveEmitsSaveClickedAction() = runComposeUiTest {
        var lastAction: AddTaskAction? = null
        setContent {
            AddTaskScreen(
                state = AddTaskState(),
                onAction = { lastAction = it },
                onNavigateBack = {},
            )
        }

        onNode(hasText("Add Task") and hasClickAction()).performClick()

        assert(lastAction is AddTaskAction.SaveClicked)
    }
}
