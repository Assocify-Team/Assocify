package com.github.se.assocify.ui.screens.profile.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.DatePickerWithDialog
import com.github.se.assocify.ui.util.DateTimeUtil
import com.github.se.assocify.ui.util.DateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEventsScreen(
    navActions: NavigationActions,
    profileEventsViewModel: ProfileEventsViewModel
) {
  val state by profileEventsViewModel.uiState.collectAsState()

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
        FloatingActionButton(
            onClick = { profileEventsViewModel.openAddEvent() },
            modifier = Modifier.testTag("addEventButton")) {
              Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
      },
      contentWindowInsets = WindowInsets(20.dp, 0.dp, 20.dp, 0.dp)) {
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
                          onClick = { profileEventsViewModel.modifyEvent(event) },
                          modifier = Modifier.testTag("editEventButton")) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                          }

                      IconButton(
                          onClick = { profileEventsViewModel.openDeleteDialogue(event) },
                          modifier = Modifier.testTag("deleteEventButton")) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                          }
                    }
                  })
              if (index != state.events.size - 1) HorizontalDivider()
            }
          }
          item { Spacer(modifier = Modifier.height(80.dp)) }

          // open dialog to edit/add event
          if (state.openDialogue) {
            item {
              Dialog(onDismissRequest = { profileEventsViewModel.clearModifyingEvent() }) {
                ElevatedCard {
                  Column(
                      modifier = Modifier.padding(16.dp).fillMaxWidth(),
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // name of the event
                        OutlinedTextField(
                            value = state.modifyingEvent?.name ?: state.newName,
                            singleLine = true,
                            onValueChange = { profileEventsViewModel.updateNewName(it) },
                            label = { Text("Edit name") },
                            modifier = Modifier.fillMaxWidth().testTag("editName"))

                        // description of the event
                        OutlinedTextField(
                            value = state.modifyingEvent?.description ?: state.newDescription,
                            singleLine = true,
                            onValueChange = { profileEventsViewModel.updateNewDescription(it) },
                            label = { Text("Edit description") },
                            modifier = Modifier.fillMaxWidth().testTag("editDescription"))

                        // confirm button
                        OutlinedButton(
                            onClick = { if (state.modifyingEvent != null) profileEventsViewModel.updateCurrentEvent() else profileEventsViewModel.confirmAddEvent() },
                            modifier = Modifier.wrapContentSize().testTag("confirmButton")) {
                              Text(text = "Confirm", textAlign = TextAlign.Center)
                            }
                      }
                }
              }
            }
          }

            // confirm delete dialog
            if (state.deleteDialogue) {
                item{
                    Dialog(onDismissRequest = { profileEventsViewModel.clearDeleteDialogue() }) {
                        ElevatedCard {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text("Are you sure you want to delete the event ${state.deletingEvent?.name}?")
                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        OutlinedButton(
                                            onClick = { profileEventsViewModel.clearDeleteDialogue() },
                                            modifier = Modifier.wrapContentSize().testTag("cancelButton"),
                                            ) {
                                                Text(text = "Cancel", textAlign = TextAlign.Center)
                                            }
                                        OutlinedButton(
                                            onClick = { profileEventsViewModel.deleteEvent(state.deletingEvent!!) },
                                            modifier = Modifier.wrapContentSize().testTag("confirmButton"),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.error),
                                            border = BorderStroke(1.0.dp, MaterialTheme.colorScheme.error)
                                        ) {
                                                Text(text = "Confirm", textAlign = TextAlign.Center)
                                            }
                                    }
                                }
                        }
                    }
                }
            }
        }
      }
}
