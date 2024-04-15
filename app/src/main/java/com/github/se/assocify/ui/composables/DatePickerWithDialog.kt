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

/** An enumeration representing different date restrictions for selectable dates. */
enum class DateRestriction {
  /** Any date can be selected. */
  ANY,
  /** No date is selectable. */
  NONE,
  /** Only future dates are selectable. */
  FUTURE,
  /** Future dates or the current date are selectable. */
  FUTURE_OR_NOW,
  /** Only past dates are selectable. */
  PAST,
  /** Past dates or the current date are selectable. */
  PAST_OR_NOW,
  /** Only the current date is selectable. */
  NOW
}

/**
 * A Composable that displays a Date TextField, that opens a DatePickerDialog when clicked.
 *
 * @param value The current value of the Date Field, formatted as a string.
 * @param onDateSelect Callback function invoked when a date is selected or the dialog is dismissed.
 * @param modifier The modifier to be applied to the composable.
 * @param label The label to be displayed above the text field.
 * @param isSelectableDate The restriction on selectable dates.
 * @param isError Whether the DatePicker is in an error state.
 * @param supportingText Additional supporting text to be displayed below the DatePicker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithDialog(
    value: String,
    onDateSelect: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    isSelectableDate: DateRestriction = DateRestriction.ANY,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null
) {
  var showDialog by remember { mutableStateOf(false) }
  val datePickerState =
      rememberDatePickerState(
          selectableDates =
              object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                  val nowStart =
                      LocalDate.now()
                          .atStartOfDay()
                          .atZone(ZoneId.systemDefault())
                          .toInstant()
                          .toEpochMilli()
                  val nowEnd =
                      LocalDate.now()
                          .atTime(23, 59, 59)
                          .atZone(ZoneId.systemDefault())
                          .toInstant()
                          .toEpochMilli()
                  return when (isSelectableDate) {
                    DateRestriction.ANY -> true
                    DateRestriction.NONE -> false
                    DateRestriction.PAST_OR_NOW -> utcTimeMillis <= nowEnd
                    DateRestriction.PAST -> utcTimeMillis < nowStart
                    DateRestriction.NOW -> utcTimeMillis in nowStart..nowEnd
                    DateRestriction.FUTURE_OR_NOW -> utcTimeMillis >= nowStart
                    DateRestriction.FUTURE -> utcTimeMillis > nowEnd
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
        isError = isError,
        supportingText = supportingText)
    Box(modifier = Modifier.matchParentSize().alpha(0f).clickable(onClick = { showDialog = true }))
  }

  if (showDialog) {
    DatePickerDialog(
        modifier = Modifier.testTag("datePickerDialog"),
        onDismissRequest = { showDialog = false },
        confirmButton = {
          Button(
              modifier = Modifier.testTag("datePickerDialogOk"),
              onClick = {
                showDialog = false
                onDateSelect(selectedDate)
              }) {
                Text(text = "OK")
              }
        },
        dismissButton = {
          Button(
              modifier = Modifier.testTag("datePickerDialogCancel"),
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
