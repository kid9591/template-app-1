package com.example.todoist.screens.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoist.screens.addtask.AddTaskScreenRoot
import com.example.todoist.screens.home.HomeScreenRoot
import com.example.todoist.screens.taskdetail.TaskDetailScreenRoot
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object AddTaskRoute

@Serializable
data class TaskDetailRoute(val taskId: String)

@Composable
fun ApplicationNavigationGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
    ) {
        composable<HomeRoute> {
            HomeScreenRoot(
                onNavigateToAddTask = { navController.navigate(AddTaskRoute) },
                onNavigateToTaskDetail = { taskId -> navController.navigate(TaskDetailRoute(taskId)) },
            )
        }

        composable<AddTaskRoute> {
            AddTaskScreenRoot(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable<TaskDetailRoute> {
            TaskDetailScreenRoot(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
