package com.github.se.assocify.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

/**
 * A dropdown filter chip with a list of options
 *
 * @param selectedOption: The default selected option
 * @param options: The list of options
 * @param testTag: The test tag of the dropdown filter chip
 * @param onOptionSelected: The callback when an option is selected
 */
@Composable
fun DropdownFilterChip(
    selectedOption: String,
    options: List<String>,
    testTag: String,
    onOptionSelected: (String) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }
  var selectedOption1 by remember { mutableStateOf(selectedOption) }

  Box(modifier = Modifier.padding(8.dp).testTag(testTag)) {
    FilterChip(
        selected = false,
        onClick = { expanded = !expanded },
        label = { Text(selectedOption1) },
        trailingIcon = {
          Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Expand")
        },
        modifier = Modifier.testTag("filterChip"))

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        properties = PopupProperties(focusable = true),
        modifier = Modifier.testTag("dropdownMenu")) {
          options.forEach { option ->
            DropdownMenuItem(
                text = { Text(option) },
                onClick = {
                  onOptionSelected(option)
                  selectedOption1 = option
                  expanded = false
                })
          }
        }
  }
}
