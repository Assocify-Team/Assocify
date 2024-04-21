package com.github.se.assocify.ui.screens.treasury.accounting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
fun Budget() {
  val yearList =
      listOf("2023", "2022", "2021") // TODO: start from 2021 until current year (dynamically=
  val categoryList = listOf("Global", "Category", "Commissions", "Events", "Projects", "Other")
  Row {
    DropdownFilterChip("2023", yearList)
    DropdownFilterChip("Global", categoryList)
  }
  // list of budget lines

  // last line must be total
  // add budget line button
  // total
  // clickable budget
}

@Composable
fun DropdownFilterChip(selectedOption1: String, options: List<String>) {
  var expanded by remember { mutableStateOf(false) }
  var selectedOption by remember { mutableStateOf(selectedOption1) }
  Box(modifier = Modifier.padding(8.dp)) {
    FilterChip(
        selected = false,
        onClick = { expanded = !expanded },
        label = { Text(selectedOption) },
        trailingIcon = {
          Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Expand")
        })

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        properties = PopupProperties(focusable = true)) {
          options.forEach { option ->
            DropdownMenuItem(
                text = { Text(option) },
                onClick = {
                  selectedOption = option
                  expanded = false
                })
          }
        }
  }
}

@Composable
fun DisplayBudgetLine(category: String, amount: String) {
  ListItem(
      headlineContent = { Text(category) },
      trailingContent = { /*total amount*/},
      modifier = Modifier.clickable { /*TODO: open screen of the selected budget category*/})
}
