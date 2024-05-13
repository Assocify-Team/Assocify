package com.github.se.assocify.ui.screens.event.tasktab.task

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.taskGraph(
    navigationActions: NavigationActions,
    taskAPI: TaskAPI,
    eventAPI: EventAPI
) {
  composable(route = Destination.NewTask.route) {
    val taskViewModel = remember {
      TaskViewModel(navActions = navigationActions, taskApi = taskAPI, eventApi = eventAPI)
    }
    TaskScreen(navigationActions, taskViewModel)
  }
  composable(route = Destination.EditTask("{taskUid}").route) { backStackEntry ->
    backStackEntry.arguments?.getString("taskUid")?.let { taskUid ->
      val taskViewModel = remember {
        TaskViewModel(
            taskUid = taskUid,
            navActions = navigationActions,
            taskApi = taskAPI,
            eventApi = eventAPI)
      }
      TaskScreen(navigationActions, taskViewModel)
    }
  }
}
