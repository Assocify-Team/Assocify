package com.github.se.assocify.ui.screens.event.tasktab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

/**
 * A screen to display the different tasks that need to be completed for an event.
 *
 * @param eventTaskViewModel the view model that holds the tasks
 * @param navActions the navigation actions to navigate to other screens
 */
@Composable
fun EventTaskScreen(eventTaskViewModel: EventTaskViewModel, navActions: NavigationActions) {
  val state = eventTaskViewModel.uiState.collectAsState()
  LazyColumn(modifier = Modifier.fillMaxWidth()) {
    state.value.tasks.forEach {
      item {
        ListItem(
            modifier = Modifier.clickable { navActions.navigateTo(Destination.EditTask(it.uid)) },
            headlineContent = { Text(it.title) },
            supportingContent = { Text(it.category) },
            trailingContent = {
              Checkbox(
                  checked = it.isCompleted,
                  onCheckedChange = { checked -> eventTaskViewModel.checkTask(it, checked) },
              )
            },
            overlineContent = { Text(it.startTime.toString()) })
        HorizontalDivider()
      }
    }
  }
}
