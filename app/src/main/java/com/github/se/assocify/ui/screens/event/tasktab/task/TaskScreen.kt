package com.github.se.assocify.ui.screens.event.tasktab.task

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.DatePickerWithDialog
import com.github.se.assocify.ui.composables.TimePickerWithDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    navActions: NavigationActions,
) {
  Scaffold(
      modifier = Modifier.testTag("taskScreen"),
      topBar = {
        TopAppBar(
            title = { Text(modifier = Modifier.testTag("taskScreenTitle"), text = "New Task") },
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("backButton"), onClick = { navActions.back() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                  }
            })
      },
      contentWindowInsets = WindowInsets(40.dp, 20.dp, 40.dp, 0.dp),
  /*snackbarHost = {
            SnackbarHost(
                hostState = taskState.snackbarHostState,
                snackbar = { snackbarData -> Snackbar(snackbarData = snackbarData) })
        }*/ ) { paddingValues ->
    Column(
        modifier =
            Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
          OutlinedTextField(
              modifier = Modifier.testTag("titleField").fillMaxWidth(),
              value = "", // taskState.title,
              onValueChange = { /*viewModel.setTitle(it)*/},
              label = { Text("Title") },
              // isError = taskState.titleError != null,
              supportingText = { /*taskState.titleError?.let { Text(it) }*/})
          OutlinedTextField(
              modifier = Modifier.testTag("descriptionField").fillMaxWidth(),
              value = "", // receiptState.description,
              onValueChange = { /*viewModel.setDescription(it)*/},
              label = { Text("Description") },
              minLines = 3,
              supportingText = {})
          OutlinedTextField(
              modifier = Modifier.testTag("categoryField").fillMaxWidth(),
              value = "", // receiptState.category,
              onValueChange = { /*viewModel.setCategory(it)*/},
              label = { Text("Category") },
              supportingText = {})
          OutlinedTextField(
              modifier = Modifier.testTag("staffNumberField").fillMaxWidth(),
              value = "" /*taskState.staffNumber*/,
              onValueChange = { /*viewModel.setStaffNumber(it)*/},
              label = { Text("Number of Staff") },
              keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
              // isError = taskState.staffNumberError != null,
              supportingText = { /*taskState.amountError?.let { Text(it) }*/})
          DatePickerWithDialog(
              modifier = Modifier.testTag("dateField").fillMaxWidth(),
              value = "" /*taskState.date*/,
              onDateSelect = { /*viewModel.setDate(it)*/},
              label = { Text("Date") },
              // isError = taskState.dateError != null,
              supportingText = { /*taskState.dateError?.let { Text(it) }*/})
          TimePickerWithDialog(
              modifier = Modifier.testTag("timeField").fillMaxWidth(),
              value = "",
              onTimeSelect = { /*viewModel.setTime(it)*/},
              label = { Text("Time") },
              supportingText = { /*taskState.timeError?.let { Text(it) }*/})
          OutlinedTextField(
              modifier = Modifier.testTag("locationField").fillMaxWidth(),
              value = "" /*taskState.staffNumber*/,
              onValueChange = { /*viewModel.setStaffNumber(it)*/},
              label = { Text("Location") },
              // isError = taskState.staffNumberError != null,
              supportingText = { /*taskState.amountError?.let { Text(it) }*/})
          Column {
            Button(
                modifier = Modifier.testTag("saveButton").fillMaxWidth(),
                onClick = { /*viewModel.saveReceipt()*/},
                content = { Text("Save") })
            OutlinedButton(
                modifier = Modifier.testTag("deleteButton").fillMaxWidth(),
                onClick = { /*viewModel.deleteReceipt()*/},
                content = { Text("Delete" /*if (taskState.isNewTask) {
                      "Cancel"
                    } else {
                      "Delete"
                    }*/) },
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error))
          }

          Spacer(modifier = Modifier.weight(1.0f))
        }
  }
}
