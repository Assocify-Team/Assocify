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
 * A dropdown with a list of given options, with a readOnly text field displaying the selected
 * option If the list of options is empty, the dropdown is disabled
 *
 * @param modifier: The modifier (alignment, testTag...)
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
) {

  ExposedDropdownMenuBox(
      expanded = opened,
      onExpandedChange = { if (options.size > 1) onOpenedChange(true) },
      modifier = modifier) {
        OutlinedTextField(
            enabled = options.size > 1,
            value = selectedOption.name,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
              if (options.size > 1) ExposedDropdownMenuDefaults.TrailingIcon(expanded = opened)
            },
            modifier = Modifier.menuAnchor(),
            leadingIcon = selectedOption.leadIcon)

        ExposedDropdownMenu(expanded = opened, onDismissRequest = { onOpenedChange(false) }) {
          options.forEach { item ->
            DropdownMenuItem(
                text = { Text(text = item.name) },
                onClick = {
                  onSelectOption(item)
                  onOpenedChange(false)
                },
                leadingIcon = item.leadIcon,
                modifier = Modifier.testTag("DropdownItem-${item.uid}"))
          }
        }
      }
}

/** An option for the dropdown : has a name (displayed) and a uid (for uniqueness) */
data class DropdownOption(
    val name: String,
    val uid: String,
    val leadIcon: (@Composable () -> Unit)? = null
)
