package com.github.se.assocify.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.se.assocify.model.entities.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchTextField(
    modifier: Modifier = Modifier,
    searchValue: String,
    userList: List<User>,
    user: User? = null,
    onUserSearch: (String) -> Unit,
    onUserSelect: (User) -> Unit,
    onUserDismiss: () -> Unit,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val value = user?.name ?: searchValue

  OutlinedTextField(
      value = value,
      onValueChange = onUserSearch,
      modifier = modifier,
      readOnly = user != null,
      isError = isError,
      label = label,
      supportingText = supportingText,
      trailingIcon = {
        if (user != null) {
            IconButton(onClick = onUserDismiss) {
                Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
        } else {
            IconButton(onClick = { expanded = !expanded }) {
                if (expanded) {
                    Icon(Icons.Default.ArrowDropUp, contentDescription = "Collapse")
                } else {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                }
            }
        }
      })
    DropdownMenu(expanded = user == null && value.isNotEmpty(), onDismissRequest = { expanded = !expanded} ) {
        userList.forEach {user ->
            DropdownMenuItem(text = { Text(user.name) },
                onClick = {
                    onUserSelect(user)
                })
        }

    }
}
