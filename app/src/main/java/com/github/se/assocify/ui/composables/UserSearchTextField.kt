package com.github.se.assocify.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.PopupProperties
import com.github.se.assocify.model.entities.User

/**
 * A Composable that displays a search text field with dropdown menu for user selection.
 *
 * @param modifier The modifier to be applied to the composable.
 * @param searchValue The current search text value.
 * @param userList The list of users to be displayed in the dropdown menu.
 * @param user The selected user, if any.
 * @param onUserSearch Callback function invoked when the search text changes.
 * @param onUserSelect Callback function invoked when a user is selected from the dropdown menu.
 * @param onUserDismiss Callback function invoked when the selected user is dismissed.
 * @param expanded Whether the dropdown menu is expanded or not.
 * @param label The label to be displayed above the text field, if any.
 * @param isError Whether the text field is in an error state.
 * @param supportingText Additional supporting text to be displayed below the text field, if any.
 */
@Composable
fun UserSearchTextField(
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    state: UserSearchState,
    onUserSearch: (String) -> Unit,
    onUserSelect: (User) -> Unit,
    onUserDismiss: () -> Unit,
) {
  val value = if (state.user != null) state.user.name else state.searchValue
  var textfieldSize by remember { mutableIntStateOf(0) }
  Column {
    OutlinedTextField(
        value = value,
        onValueChange = { onUserSearch(it) },
        modifier = modifier then Modifier.onSizeChanged { textfieldSize = it.width },
        readOnly = state.user != null,
        isError = state.errorText != null,
        label = label,
        supportingText = state.errorText,
        trailingIcon = {
          if (state.user != null) {
            IconButton(modifier = Modifier.testTag("userDismissButton"), onClick = onUserDismiss) {
              Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
          }
        })
    DropdownMenu(
        modifier =
            Modifier.testTag("userDropdown")
                .width(with(LocalDensity.current) { textfieldSize.toDp() }),
        expanded = state.userList.isNotEmpty(),
        onDismissRequest = {},
        properties = PopupProperties(focusable = false)) {
          state.userList.forEach { user ->
            DropdownMenuItem(
                modifier = Modifier.testTag("userDropdownItem-${user.uid}"),
                text = { Text(user.name) },
                onClick = { onUserSelect(user) })
          }
        }
  }
}

data class UserSearchState(
    val searchValue: String,
    val userList: List<User>,
    val user: User?,
    val errorText: @Composable (() -> Unit)? = null,
)
