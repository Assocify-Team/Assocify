package com.github.se.assocify.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithDialog(
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
    dateValue: String,
    onDateSelected: (LocalDate?) -> Unit
) {
  var showDialog by remember { mutableStateOf(false) }
  val datePickerState =
      rememberDatePickerState(
          selectableDates =
              object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                  return utcTimeMillis >=
                      LocalDate.now()
                          .atStartOfDay()
                          .atZone(ZoneId.systemDefault())
                          .toInstant()
                          .toEpochMilli()
                }
              })
  val selectedDate =
      datePickerState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
      }

  Box {
    OutlinedTextField(
        modifier = modifier,
        value = dateValue,
        onValueChange = {},
        readOnly = true,
        label = { label() },
        placeholder = { Text("--/--/--") })
    Box(modifier = Modifier.matchParentSize().alpha(0f).clickable(onClick = { showDialog = true }))
  }

  if (showDialog) {
    DatePickerDialog(
        modifier = Modifier.testTag("datePickerDialog"),
        onDismissRequest = { showDialog = false },
        confirmButton = {
          Button(
              onClick = {
                showDialog = false
                onDateSelected(selectedDate)
              }) {
                Text(text = "OK")
              }
        },
        dismissButton = {
          Button(
              modifier = Modifier.testTag("datePickerDialogDismiss"),
              onClick = { showDialog = false }) {
                Text(text = "Cancel")
              }
        }) {
          DatePicker(state = datePickerState, showModeToggle = true)
        }
  }
}
