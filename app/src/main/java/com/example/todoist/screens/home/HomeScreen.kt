package com.example.todoist.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.todoist.domain.model.Task
import com.example.todoist.domain.model.TaskCategory
import com.example.todoist.domain.model.TaskStatus
import com.example.todoist.screens.common.BottomNavBar
import com.example.todoist.screens.common.BottomNavTab
import com.example.todoist.screens.theme.CategoryPersonal
import com.example.todoist.screens.theme.CategoryStudy
import com.example.todoist.screens.theme.CategoryWork
import com.example.todoist.screens.theme.StatusDone
import com.example.todoist.screens.theme.StatusInProgress
import com.example.todoist.screens.theme.StatusTodo
import com.example.todoist.screens.theme.TodoAppTheme
import com.example.todoist.screens.util.ObserveAsEvents
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import java.time.LocalDate

@Composable
fun HomeScreenRoot(
    onNavigateToAddTask: () -> Unit,
    onNavigateToTaskDetail: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            HomeEvent.NavigateToAddTask -> onNavigateToAddTask()
            is HomeEvent.NavigateToTaskDetail -> onNavigateToTaskDetail(event.taskId)
        }
    }

    HomeScreen(state = state, onAction = viewModel::onAction)
}

@Composable
fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavBar(
                activeTab = BottomNavTab.HOME,
                onTabSelected = { tab ->
                    if (tab == BottomNavTab.ADD) onAction(HomeAction.AddTaskClicked)
                },
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "My Tasks",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "${state.tasks.size} tasks total",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(TaskFilter.entries) { filter ->
                    FilterChip(
                        label = filter.label,
                        selected = filter == state.activeFilter,
                        onClick = { onAction(HomeAction.FilterChanged(filter)) },
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                if (state.filteredTasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No tasks here",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                } else {
                    items(state.filteredTasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onTaskClick = { onAction(HomeAction.TaskClicked(task.id)) },
                            onStatusCycled = {
                                val next = when (task.status) {
                                    TaskStatus.TODO -> TaskStatus.IN_PROGRESS
                                    TaskStatus.IN_PROGRESS -> TaskStatus.DONE
                                    TaskStatus.DONE -> TaskStatus.TODO
                                }
                                onAction(HomeAction.TaskStatusChanged(task.id, next))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else Color(0xFFECE7FF))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) Color.White else MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onStatusCycled: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (categoryIcon, categoryIconColor, categoryIconBg) = when (task.category) {
        TaskCategory.WORK -> Triple(Icons.Default.Work, CategoryWork, Color(0xFFFFE4F2))
        TaskCategory.PERSONAL -> Triple(Icons.Default.Person, CategoryPersonal, Color(0xFFEDE4FF))
        TaskCategory.STUDY -> Triple(Icons.Default.Book, CategoryStudy, Color(0xFFFFE6D2))
    }

    Card(
        modifier = modifier.clickable(onClick = onTaskClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(categoryIconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    tint = categoryIconColor,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = task.category.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (task.time != null) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color(0xFFAB93FF),
                            modifier = Modifier.size(12.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = task.time,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFAB93FF),
                        )
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            StatusBadge(status = task.status, onClick = onStatusCycled)
        }
    }
}

@Composable
private fun StatusBadge(status: TaskStatus, onClick: () -> Unit) {
    val (label, textColor, bgColor) = when (status) {
        TaskStatus.DONE -> Triple("Done", StatusDone, Color(0xFFEDE0FF))
        TaskStatus.IN_PROGRESS -> Triple("In Progress", StatusInProgress, Color(0xFFFFE8E1))
        TaskStatus.TODO -> Triple("To-do", StatusTodo, Color(0xFFE3F2FF))
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}

private val PreviewTasks = listOf(
    Task(
        id = "1",
        title = "Design user flow",
        status = TaskStatus.IN_PROGRESS,
        category = TaskCategory.WORK,
        dueDate = LocalDate.of(2025, 5, 20),
        time = "10:00 AM",
    ),
    Task(
        id = "2",
        title = "Read chapter 3",
        status = TaskStatus.TODO,
        category = TaskCategory.STUDY,
        dueDate = LocalDate.of(2025, 5, 21),
        time = null,
    ),
    Task(
        id = "3",
        title = "Gym session",
        status = TaskStatus.DONE,
        category = TaskCategory.PERSONAL,
        dueDate = LocalDate.of(2025, 5, 19),
        time = "7:30 PM",
    ),
)

@Preview(showBackground = true, name = "With tasks")
@Composable
private fun HomeScreenWithTasksPreview() {
    TodoAppTheme {
        HomeScreen(
            state = HomeState(tasks = PreviewTasks),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun HomeScreenEmptyPreview() {
    TodoAppTheme {
        HomeScreen(
            state = HomeState(tasks = emptyList()),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, name = "Filter: In Progress")
@Composable
private fun HomeScreenInProgressFilterPreview() {
    TodoAppTheme {
        HomeScreen(
            state = HomeState(
                tasks = PreviewTasks,
                activeFilter = TaskFilter.IN_PROGRESS,
            ),
            onAction = {},
        )
    }
}
