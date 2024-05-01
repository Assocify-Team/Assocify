package com.github.se.assocify.ui.screens.event.tasktab.task

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.taskGraph(navigationActions: NavigationActions, taskAPI: TaskAPI) {
  composable(route = Destination.NewTask("{eventUid}").route) { backStackEntry ->
    backStackEntry.arguments?.getString("eventUid")?.let { eventUid ->
      TaskScreen(
          navActions = navigationActions,
          viewModel =
              TaskViewModel(eventUid = eventUid, navActions = navigationActions, taskApi = taskAPI))
    }
  }
  composable(route = Destination.EditTask("{eventUid}", "{taskUid}").route) { backStackEntry ->
    backStackEntry.arguments?.getString("eventUid")?.let { eventUid ->
      backStackEntry.arguments?.getString("taskUid")?.let { taskUid ->
        TaskScreen(
            navActions = navigationActions,
            viewModel =
                TaskViewModel(
                    taskUid = taskUid,
                    eventUid = eventUid,
                    navActions = navigationActions,
                    taskApi = taskAPI))
      }
    }
  }
}
