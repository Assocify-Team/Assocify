package com.github.se.assocify.ui.screens.event.tasktab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.se.assocify.model.entities.Task
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

/**
 * A screen to display the different tasks that need to be completed for an event.
 *
 * @param tasks List of tasks to be displayed.
 */
@Composable
fun EventTaskScreen(navActions: NavigationActions, tasks: List<Task>) {
  LazyColumn(modifier = Modifier.fillMaxWidth()) {
    tasks.forEach {
      item {
        ListItem(
            modifier = Modifier.clickable { navActions.navigateTo(Destination.EditTask("eventUid", it.uid)) }, // TODO: Add the correct eventUid
            headlineContent = { Text(it.title) },
            supportingContent = { Text(it.category) },
            trailingContent = {
              Checkbox(
                  checked = it.isCompleted,
                  onCheckedChange = { /*TODO what happens if the checkbox is clicked*/},
              )
            },
            overlineContent = { Text(it.startTime.toString()) })
        HorizontalDivider()
      }
    }
  }
}
