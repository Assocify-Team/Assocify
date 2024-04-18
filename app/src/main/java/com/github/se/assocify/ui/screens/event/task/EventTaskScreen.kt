package com.github.se.assocify.ui.screens.event.task

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.github.se.assocify.model.entities.Task

@Composable
fun EventTaskScreen(tasks: List<Task>) {
  tasks.forEach {
    ListItem(headlineContent = { Text(it.name) })
    HorizontalDivider()
  }
}
