package com.github.se.assocify.ui.screens.event.task

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

/**
 * A screen to display the different tasks that need to be completed for an event.
 *
 * @param tasks List of tasks to be displayed.
 */
@Composable
fun EventTaskScreen(eventTaskViewModel: EventTaskViewModel) {
  val state = eventTaskViewModel.uiState.collectAsState()
  LazyColumn(modifier = Modifier.fillMaxWidth()) {
    state.value.tasks.forEach {
      item {
        ListItem(
            modifier = Modifier.clickable { /*TODO: navigate to the task details screen*/},
            headlineContent = { Text(it.name) },
            supportingContent = { Text(it.category) },
            trailingContent = {
              Checkbox(
                  checked = it.isCompleted,
                  onCheckedChange = { /*TODO what happens if the checkbox is clicked*/},
              )
            },
            overlineContent = { Text(it.startTime) })
        HorizontalDivider()
      }
    }
  }
}
