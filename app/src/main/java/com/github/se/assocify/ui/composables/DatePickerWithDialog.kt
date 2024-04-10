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
import com.github.se.assocify.ui.util.DateUtil
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

enum class DateRestriction {
    ANY,
    NONE,
    FUTURE,
    FUTURE_OR_NOW,
    PAST,
    PAST_OR_NOW,
    NOW
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithDialog(
    value: String,
    onDateSelect: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    errorMessage : String? = null,
    isSelectableDate: DateRestriction = DateRestriction.ANY
) {
  var showDialog by remember { mutableStateOf(false) }
  val datePickerState =
      rememberDatePickerState(
          selectableDates =
              object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val nowStart = LocalDate.now()
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                    val nowEnd = LocalDate.now()
                        .atTime(23, 59, 59)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                    return when (isSelectableDate) {
                        DateRestriction.ANY ->
                            true
                        DateRestriction.NONE ->
                            false
                        DateRestriction.PAST_OR_NOW ->
                            utcTimeMillis <= nowEnd
                        DateRestriction.PAST ->
                            utcTimeMillis < nowStart
                        DateRestriction.NOW ->
                            utcTimeMillis in nowStart..nowEnd
                        DateRestriction.FUTURE_OR_NOW ->
                            utcTimeMillis >= nowStart
                        DateRestriction.FUTURE ->
                            utcTimeMillis > nowEnd
                    }
                }
              })
  val selectedDate =
      datePickerState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
      }

  Box {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = {},
        readOnly = true,
        label = label,
        placeholder = { Text(DateUtil.NULL_DATE_STRING) },
        isError = errorMessage != null,
        supportingText = { errorMessage?.let { Text(it) } })
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
                onDateSelect(selectedDate)
              }) {
                Text(text = "OK")
              }
        },
        dismissButton = {
          Button(
              modifier = Modifier.testTag("datePickerDialogDismiss"),
              onClick = {
                  showDialog = false
                  onDateSelect(null)
              }) {
                Text(text = "Cancel")
              }
        }) {
          DatePicker(state = datePickerState, showModeToggle = true)
        }
  }
}
