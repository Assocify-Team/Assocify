package com.github.se.assocify.ui.screens.event.tasktab.task

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.taskGraph(navigationActions: NavigationActions) {
    composable(route = Destination.NewTask.route) { TaskScreen(navActions = navigationActions) }
    composable(Destination.EditTask("{taskUid}").route) { backStackEntry ->
        backStackEntry.arguments?.getString("taskUid")?.let {
            TaskScreen(navActions = navigationActions)
        }
    }
}
