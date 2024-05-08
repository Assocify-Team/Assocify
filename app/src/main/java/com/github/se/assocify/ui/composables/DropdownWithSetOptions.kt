package com.github.se.assocify.ui.composables

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/**
 * A dropdown with a list of given options, readOnly text field
 *
 * @param options: The list of options
 * @param selectedOption: The currently selected option
 * @param opened: The state of the dropdown
 * @param onOpenedChange: The callback when the dropdown is opened or closed
 * @param onSelectOption: The callback when an option is selected
 * @param leadIcon: The leading icon of the dropdown : default is null
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownWithSetOptions(
    modifier: Modifier = Modifier,
    options: List<DropdownOption>,
    selectedOption: DropdownOption,
    opened: Boolean,
    onOpenedChange: (Boolean) -> Unit,
    onSelectOption: (DropdownOption) -> Unit,
    leadIcon: (@Composable () -> Unit)? = null
) {

  ExposedDropdownMenuBox(
      expanded = opened, onExpandedChange = { onOpenedChange(true) }, modifier = modifier) {
        OutlinedTextField(
            value = selectedOption.name,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = opened) },
            modifier = Modifier.menuAnchor(),
            leadingIcon = leadIcon)

        ExposedDropdownMenu(expanded = opened, onDismissRequest = { onOpenedChange(false) }) {
          options.forEach { item ->
            DropdownMenuItem(
                text = { Text(text = item.name) },
                onClick = {
                  onSelectOption(item)
                  onOpenedChange(false)
                },
                leadingIcon = leadIcon,
                modifier = Modifier.testTag("DropdownItem-${item.uid}"))
          }
        }
      }
}

data class DropdownOption(val name: String, val uid: String) {
  override fun toString(): String {
    return name
  }
}
