package com.example.todoist.screens.taskdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoist.domain.model.TaskCategory
import com.example.todoist.domain.model.TaskStatus
import com.example.todoist.screens.theme.TodoAppTheme
import com.example.todoist.screens.util.ObserveAsEvents
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun TaskDetailScreenRoot(
    onNavigateBack: () -> Unit,
    viewModel: TaskDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            TaskDetailEvent.NavigateBack -> onNavigateBack()
        }
    }

    TaskDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    state: TaskDetailState,
    onAction: (TaskDetailAction) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Task Detail",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onAction(TaskDetailAction.DeleteClicked) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete task",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            // Title
            SectionLabel("Task Title")
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = state.title,
                onValueChange = { onAction(TaskDetailAction.TitleChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.titleError != null,
                supportingText = state.titleError?.let { { Text(it) } },
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(Modifier.height(20.dp))

            // Category
            SectionLabel("Category")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskCategory.entries.forEach { category ->
                    SelectionChip(
                        label = category.name.lowercase().replaceFirstChar { it.uppercase() },
                        selected = category == state.category,
                        onClick = { onAction(TaskDetailAction.CategorySelected(category)) },
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Status
            SectionLabel("Status")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskStatus.entries.forEach { status ->
                    val label = when (status) {
                        TaskStatus.TODO -> "To do"
                        TaskStatus.IN_PROGRESS -> "In Progress"
                        TaskStatus.DONE -> "Done"
                    }
                    SelectionChip(
                        label = label,
                        selected = status == state.status,
                        onClick = { onAction(TaskDetailAction.StatusSelected(status)) },
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Due date
            SectionLabel("Due Date (YYYY-MM-DD)")
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = state.dueDate,
                onValueChange = { onAction(TaskDetailAction.DueDateChanged(it)) },
                placeholder = { Text("e.g. 2025-05-20") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(Modifier.height(12.dp))

            // Time
            SectionLabel("Time (optional)")
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = state.time,
                onValueChange = { onAction(TaskDetailAction.TimeChanged(it)) },
                placeholder = { Text("e.g. 10:00 AM") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { onAction(TaskDetailAction.SaveClicked) },
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
                    text = if (state.isSaving) "Saving…" else "Save Changes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun SelectionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (selected) Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                else Modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Preview(showBackground = true, name = "Loaded")
@Composable
private fun TaskDetailScreenLoadedPreview() {
    TodoAppTheme {
        TaskDetailScreen(
            state = TaskDetailState(
                title = "Design user flow",
                category = TaskCategory.WORK,
                status = TaskStatus.IN_PROGRESS,
                dueDate = "2025-05-20",
                time = "10:00 AM",
                isLoading = false,
            ),
            onAction = {},
            onNavigateBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun TaskDetailScreenLoadingPreview() {
    TodoAppTheme {
        TaskDetailScreen(
            state = TaskDetailState(isLoading = true),
            onAction = {},
            onNavigateBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Title error")
@Composable
private fun TaskDetailScreenErrorPreview() {
    TodoAppTheme {
        TaskDetailScreen(
            state = TaskDetailState(
                title = "",
                titleError = "Title can't be empty",
                category = TaskCategory.STUDY,
                status = TaskStatus.TODO,
                isLoading = false,
            ),
            onAction = {},
            onNavigateBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Saving")
@Composable
private fun TaskDetailScreenSavingPreview() {
    TodoAppTheme {
        TaskDetailScreen(
            state = TaskDetailState(
                title = "Gym session",
                category = TaskCategory.PERSONAL,
                status = TaskStatus.DONE,
                dueDate = "2025-05-19",
                time = "7:30 PM",
                isLoading = false,
                isSaving = true,
            ),
            onAction = {},
            onNavigateBack = {},
        )
    }
}
