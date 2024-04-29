package com.github.se.assocify.ui.screens.event

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.event.tasktab.task.taskGraph

fun NavGraphBuilder.eventGraph(
    navigationActions: NavigationActions,
    eventAPI: EventAPI,
    taskAPI: TaskAPI
) {
  composable(route = Destination.Event.route) {
    EventScreen(navigationActions, EventScreenViewModel(eventAPI), taskAPI)
  }
  taskGraph(navigationActions)
}
