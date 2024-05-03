package com.github.se.assocify.ui.screens.event.tasktab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.event.EventScreenViewModel
import com.github.se.assocify.ui.util.DateUtil
import com.github.se.assocify.ui.util.TimeUtil

/**
 * A screen to display the different tasks that need to be completed for an event.
 *
 * @param eventTaskViewModel the view model that holds the tasks
 * @param navActions the navigation actions to navigate to other screens
 */
@Composable
fun EventTaskScreen(
    eventViewModel: EventScreenViewModel,
    eventTaskViewModel: EventTaskViewModel,
    navActions: NavigationActions
) {
  val mainState by eventViewModel.uiState.collectAsState()
  val state by eventTaskViewModel.uiState.collectAsState()

  val visibleTasks =
      state.filteredTasks.filter { t ->
        mainState.selectedEvents.any { ev -> ev.uid == t.eventUid }
      }

  if (mainState.selectedEvents.isEmpty()) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()) {
          Text("No events selected")
        }
  } else if (visibleTasks.isEmpty()) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()) {
          Text("No tasks found for selected events")
        }
  } else {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
      visibleTasks.forEach {
        item {
          ListItem(
              modifier =
                  Modifier.testTag("TaskItem").clickable {
                    navActions.navigateTo(Destination.EditTask(it.uid))
                  },
              headlineContent = { Text(it.title) },
              supportingContent = { Text(it.category) },
              trailingContent = {
                Checkbox(
                    modifier = Modifier.testTag("TaskCheckbox"),
                    checked = it.isCompleted,
                    onCheckedChange = { checked -> eventTaskViewModel.checkTask(it, checked) },
                )
              },
              overlineContent = { 
                Text(DateUtil.toString(it.startTime.toLocalDate()) +
                          " " +
                          TimeUtil.toString(it.startTime.toLocalTime()))
              })
          HorizontalDivider()
        }
      }
    }
  }
}
