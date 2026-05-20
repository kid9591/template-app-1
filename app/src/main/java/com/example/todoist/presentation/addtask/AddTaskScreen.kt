package com.example.todoist.presentation.addtask

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoist.domain.model.TaskCategory
import com.example.todoist.presentation.theme.TodoAppTheme
import com.example.todoist.presentation.util.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddTaskScreenRoot(
    onNavigateBack: () -> Unit,
    viewModel: AddTaskViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            AddTaskEvent.NavigateBack -> onNavigateBack()
        }
    }

    AddTaskScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    state: AddTaskState,
    onAction: (AddTaskAction) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Task",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Task Title",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = state.title,
                onValueChange = { onAction(AddTaskAction.TitleChanged(it)) },
                placeholder = { Text("e.g. Design user flow") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.titleError != null,
                supportingText = state.titleError?.let { { Text(it) } },
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Category",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskCategory.entries.forEach { category ->
                    val selected = category == state.selectedCategory
                    Text(
                        text = category.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .then(
                                if (selected) Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                else Modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                            )
                            .clickable { onAction(AddTaskAction.CategorySelected(category)) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Due Date (YYYY-MM-DD)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = state.dueDate,
                onValueChange = { onAction(AddTaskAction.DueDateChanged(it)) },
                placeholder = { Text("e.g. 2025-05-20") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(),
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Time (optional)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = state.time,
                onValueChange = { onAction(AddTaskAction.TimeChanged(it)) },
                placeholder = { Text("e.g. 10:00 AM") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(),
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { onAction(AddTaskAction.SaveClicked) },
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = if (state.isSaving) "Saving…" else "Add Task",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun AddTaskScreenEmptyPreview() {
    TodoAppTheme {
        AddTaskScreen(
            state = AddTaskState(),
            onAction = {},
            onNavigateBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Filled")
@Composable
private fun AddTaskScreenFilledPreview() {
    TodoAppTheme {
        AddTaskScreen(
            state = AddTaskState(
                title = "Design user flow",
                selectedCategory = TaskCategory.PERSONAL,
                dueDate = "2025-05-20",
                time = "10:00 AM",
            ),
            onAction = {},
            onNavigateBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Title error")
@Composable
private fun AddTaskScreenErrorPreview() {
    TodoAppTheme {
        AddTaskScreen(
            state = AddTaskState(
                title = "",
                titleError = "Title can't be empty",
            ),
            onAction = {},
            onNavigateBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Saving")
@Composable
private fun AddTaskScreenSavingPreview() {
    TodoAppTheme {
        AddTaskScreen(
            state = AddTaskState(
                title = "Review PRs",
                selectedCategory = TaskCategory.WORK,
                isSaving = true,
            ),
            onAction = {},
            onNavigateBack = {},
        )
    }
}
