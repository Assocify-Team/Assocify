package com.github.se.assocify.ui.composables

import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun UserSearchTextField(
    value: String,
    onUserSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null
) {
  OutlinedTextField(
      value = value,
      onValueChange = {},
      modifier = modifier,
      label = label,
      supportingText = supportingText)
}
