package com.github.se.assocify.ui.screens.event.task

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.se.assocify.model.entities.Task

@Composable
fun EventTaskScreen(tasks: List<Task>) {
  LazyColumn(modifier = Modifier.fillMaxWidth()) {
    tasks.forEach {
      item {
        ListItem(
            headlineContent = { Text(it.name) },
            supportingContent = { Text(it.category) },
            trailingContent = {
              Checkbox(
                  checked = it.isCompleted,
                  onCheckedChange = { /*TODO what happens if the button is clicked*/},
              )
            },
            overlineContent = { Text(it.startTime) })
        HorizontalDivider()
      }
    }
  }
}
