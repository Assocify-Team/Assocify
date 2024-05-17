package com.github.se.assocify.ui.screens.event.tasktab.task

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.BackButton
import com.github.se.assocify.ui.composables.CenteredCircularIndicator
import com.github.se.assocify.ui.composables.DatePickerWithDialog
import com.github.se.assocify.ui.composables.ErrorMessage
import com.github.se.assocify.ui.composables.TimePickerWithDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(navActions: NavigationActions, viewModel: TaskViewModel) {
  val taskState by viewModel.uiState.collectAsState()

  Scaffold(
      modifier = Modifier.testTag("taskScreen"),
      topBar = {
        TopAppBar(
            title = {
              Text(modifier = Modifier.testTag("taskScreenTitle"), text = taskState.pageTitle)
            },
            navigationIcon = {
              BackButton(
                  contentDescription = "Back",
                  onClick = { navActions.back() },
                  modifier = Modifier.testTag("backButton"))
            })
      },
      contentWindowInsets = WindowInsets(40.dp, 20.dp, 40.dp, 0.dp),
      snackbarHost = {
        SnackbarHost(
            hostState = taskState.snackbarHostState,
            snackbar = { snackbarData -> Snackbar(snackbarData = snackbarData) })
      }) { paddingValues ->
        if (taskState.loading) {
          CenteredCircularIndicator()
          return@Scaffold
        }

        if (taskState.error != null) {
          ErrorMessage(errorMessage = taskState.error) { viewModel.loadTask() }
          return@Scaffold
        }

        Column(
            modifier =
                Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Box(
                  modifier =
                      Modifier.testTag("eventDropdownChip").padding(bottom = 5.dp).fillMaxWidth()) {
                    var eventExpanded by remember { mutableStateOf(false) }
                    FilterChip(
                        modifier = Modifier.testTag("eventChip"),
                        selected = false,
                        onClick = { eventExpanded = !eventExpanded },
                        label = { Text(taskState.event?.name ?: "Select Event") },
                        trailingIcon = {
                          Icon(
                              imageVector = Icons.Filled.ArrowDropDown,
                              contentDescription = "Expand")
                        })
                    DropdownMenu(
                        modifier = Modifier.testTag("eventDropdownMenu"),
                        expanded = eventExpanded,
                        onDismissRequest = { eventExpanded = false },
                        properties = PopupProperties(focusable = true)) {
                          taskState.eventList.forEach { event ->
                            DropdownMenuItem(
                                text = { Text(event.name) },
                                onClick = {
                                  viewModel.setEvent(event)
                                  eventExpanded = false
                                })
                          }
                        }
                  }
              OutlinedTextField(
                  modifier = Modifier.testTag("titleField").fillMaxWidth(),
                  value = taskState.title,
                  singleLine = true,
                  onValueChange = { viewModel.setTitle(it) },
                  label = { Text("Title") },
                  isError = taskState.titleError != null,
                  supportingText = { taskState.titleError?.let { Text(it) } })
              OutlinedTextField(
                  modifier = Modifier.testTag("descriptionField").fillMaxWidth(),
                  value = taskState.description,
                  onValueChange = { viewModel.setDescription(it) },
                  label = { Text("Description") },
                  minLines = 3,
                  supportingText = {})
              OutlinedTextField(
                  modifier = Modifier.testTag("categoryField").fillMaxWidth(),
                  value = taskState.category,
                  singleLine = true,
                  onValueChange = { viewModel.setCategory(it) },
                  label = { Text("Category") },
                  supportingText = {})
              OutlinedTextField(
                  modifier = Modifier.testTag("staffNumberField").fillMaxWidth(),
                  value = taskState.staffNumber,
                  singleLine = true,
                  onValueChange = { viewModel.setStaffNumber(it) },
                  label = { Text("Number of Staff") },
                  keyboardOptions =
                      KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                  isError = taskState.staffNumberError != null,
                  supportingText = { taskState.staffNumberError?.let { Text(it) } })
              DatePickerWithDialog(
                  modifier = Modifier.testTag("dateField").fillMaxWidth(),
                  value = taskState.date,
                  onDateSelect = { viewModel.setDate(it) },
                  label = { Text("Date") },
                  isError = taskState.dateError != null,
                  supportingText = { taskState.dateError?.let { Text(it) } })
              TimePickerWithDialog(
                  modifier = Modifier.testTag("timeField").fillMaxWidth(),
                  value = taskState.time,
                  onTimeSelect = { viewModel.setTime(it) },
                  label = { Text("Time") },
                  isError = taskState.timeError != null,
                  errorText = { taskState.timeError?.let { Text(it) } })
              TimePickerWithDialog(
                  modifier = Modifier.testTag("durationField").fillMaxWidth(),
                  value = taskState.duration,
                  onTimeSelect = { viewModel.setDuration(it) },
                  label = { Text("Duration") },
                  isError = taskState.durationError != null,
                  errorText = { taskState.durationError?.let { Text(it) } },
                  dialogTitle = "Select Duration",
                  switchModes = false)
              Column {
                Button(
                    modifier = Modifier.testTag("saveButton").fillMaxWidth(),
                    onClick = { viewModel.saveTask() },
                    content = { Text("Save") })
                OutlinedButton(
                    modifier = Modifier.testTag("deleteButton").fillMaxWidth(),
                    onClick = { viewModel.deleteTask() },
                    content = {
                      Text(
                          if (taskState.isNewTask) {
                            "Cancel"
                          } else {
                            "Delete"
                          })
                    },
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error))
              }

              Spacer(modifier = Modifier.weight(1.0f))
            }
      }
}
