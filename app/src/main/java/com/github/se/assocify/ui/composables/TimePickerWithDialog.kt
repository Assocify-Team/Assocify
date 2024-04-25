package com.github.se.assocify.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.github.se.assocify.ui.util.TimeUtil
import java.time.LocalTime

/**
 * A Composable that displays a Time TextField, that opens a TimePickerDialog when clicked.
 *
 * NOTE: Much of this code is from the Jetpack Compose implementation of TimePickerDialog
 * for Material Design 3, that is yet to be released. Official recommendation is to
 * use the existing code as is, until the official implementation is released.
 *
 * @param value The current value of the Date Field, formatted as a string.
 * @param onTimeSelect Callback function invoked when a date is selected or the dialog is dismissed.
 * @param modifier The modifier to be applied to the composable.
 * @param label The label to be displayed above the text field.
 * @param isSelectableDate The restriction on selectable dates.
 * @param isError Whether the DatePicker is in an error state.
 * @param supportingText Additional supporting text to be displayed below the DatePicker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerWithDialog(
    value: String,
    onTimeSelect: (LocalTime?) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(initialHour = 8, initialMinute = 0, is24Hour = true)
    val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)

    Box {
        OutlinedTextField(
            modifier = modifier,
            value = value,
            onValueChange = {},
            readOnly = true,
            label = label,
            placeholder = { Text(TimeUtil.NULL_TIME_STRING) },
            isError = isError,
            supportingText = supportingText)
        Box(modifier = Modifier
            .matchParentSize()
            .alpha(0f)
            .clickable(onClick = { showDialog = true }))
    }

    val showingPicker = remember { mutableStateOf(true) }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)
                    .background(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface
                    ),
            ) {
                if (LocalConfiguration.current.screenHeightDp > 400) {
                    // Make this take the entire viewport. This will guarantee that Screen readers
                    // focus the toggle first.
                    Box(
                        Modifier
                            .fillMaxSize()
                            .semantics {
                                @Suppress("DEPRECATION")
                                isContainer = true
                            }
                    ) {
                        IconButton(
                            modifier = Modifier
                                // This is a workaround so that the Icon comes up first
                                // in the talkback traversal order. So that users of a11y
                                // services can use the text input. When talkback traversal
                                // order is customizable we can remove this.
                                .size(64.dp, 72.dp)
                                .align(Alignment.BottomStart)
                                .zIndex(5f),
                            onClick = { showingPicker.value = !showingPicker.value }) {
                            val icon = if (showingPicker.value) {
                                Icons.Outlined.Keyboard
                            } else {
                                Icons.Outlined.Schedule
                            }
                            Icon(
                                icon,
                                contentDescription = if (showingPicker.value) {
                                    "Switch to Text Input"
                                } else {
                                    "Switch to Touch Input"
                                }
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        text = "Select time",
                        style = MaterialTheme.typography.labelMedium
                    )
                    if (showingPicker.value && LocalConfiguration.current.screenHeightDp > 400) {
                        TimePicker(state = timePickerState)
                    } else {
                        TimeInput(state = timePickerState)
                    }
                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = { showDialog = false }
                        ) { Text("Cancel") }
                        TextButton(
                            onClick = { onTimeSelect(selectedTime); showDialog = false }
                        ) { Text("OK") }
                    }
                }
            }
        }
    }
}

