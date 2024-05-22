package com.github.se.assocify.ui.screens.profile.events

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.assocify.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEventsScreen(
    navActions: NavigationActions,
    profileEventsViewModel: ProfileEventsViewModel
) {
  Scaffold(
      modifier = Modifier.testTag("ProfileEvents Screen"),
      topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Events Management") },
            navigationIcon = {
              IconButton(
                  onClick = { navActions.back() }, modifier = Modifier.testTag("backButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Arrow Back")
                  }
            })
      },
      floatingActionButton = {
        FloatingActionButton(onClick = { /*TODO*/}, modifier = Modifier.testTag("addEventButton")) {
          Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }
      },
      contentWindowInsets = WindowInsets(20.dp, 0.dp, 20.dp, 0.dp)) {
        val state by profileEventsViewModel.uiState.collectAsState()

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(it),
        ) {
          state.events.forEachIndexed { index, event ->
            item {
              ListItem(
                  headlineContent = { Text(text = event.name) },
                  supportingContent = { Text(text = event.description.ifBlank { "-" }) },
                  trailingContent = {
                    Row {
                      IconButton(
                          onClick = { /*TODO*/}, modifier = Modifier.testTag("editEventButton")) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                          }

                      IconButton(
                          onClick = { /*TODO*/}, modifier = Modifier.testTag("deleteEventButton")) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                          }
                    }
                  })
              if (index != state.events.size - 1) HorizontalDivider()
            }
          }
          item { Spacer(modifier = Modifier.height(80.dp)) }
        }
      }
}
