package com.github.se.assocify.ui.screens.treasury.accounting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

/**
 * The accounting screen displaying the budget or the balance of the association
 *
 * @param page: The page to display (either "budget" or "balance")
 */
@Composable
fun Accounting(
    page: String,
    yearList: List<String>,
    categoryList: List<String>,
    budgetLines: List<Pair<String, String>>,
    categoryMapping: Map<String, List<String>>
) { // TODO: fetch all these list from viewmodel
  var selectedYear by remember { mutableStateOf(yearList.first()) }
  var selectedCategory by remember { mutableStateOf(categoryList.first()) }

  val filteredBudgetLines =
      budgetLines.filter { (category, _) ->
        categoryMapping[selectedCategory]?.contains(category) == true
      }

  LazyColumn(modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("AccountingScreen")) {
    item {
      Row(Modifier.testTag("filterRow")) {
        DropdownFilterChip(selectedYear, yearList, "yearFilterChip") { selectedYear = it }
        DropdownFilterChip(selectedCategory, categoryList, "categoryFilterChip") {
          selectedCategory = it
        }
      }
    }

    items(filteredBudgetLines) { (category, amount) ->
      DisplayLine(category, amount, "displayLine$category")
      HorizontalDivider(Modifier.fillMaxWidth().padding(vertical = 8.dp))
    }

    item {
      val totalAmount = filteredBudgetLines.sumOf { it.second.toInt() }
      TotalLine(totalAmount = totalAmount)
    }
  }
}

/**
 * A line displaying the total amount of the budget
 *
 * @param totalAmount: The total amount of the budget
 */
@Composable
fun TotalLine(totalAmount: Int) {
  ListItem(
      modifier = Modifier.fillMaxWidth().background(Color.LightGray).testTag("totalLine"),
      headlineContent = {
        Text(
            text = "Total",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
      },
      trailingContent = {
        Text(
            text = "$totalAmount",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
      },
      colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer))
}

/**
 * A dropdown filter chip with a list of options
 *
 * @param selectedOption1: The default selected option
 * @param options: The list of options
 * @param testTag: The test tag of the dropdown filter chip
 * @param onOptionSelected: The callback when an option is selected
 */
@Composable
fun DropdownFilterChip(
    selectedOption1: String,
    options: List<String>,
    testTag: String,
    onOptionSelected: (String) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }
  var selectedOption by remember { mutableStateOf(selectedOption1) }

  Box(modifier = Modifier.padding(8.dp).testTag(testTag)) {
    FilterChip(
        selected = false,
        onClick = { expanded = !expanded },
        label = { Text(selectedOption) },
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
                  selectedOption = option
                  expanded = false
                })
          }
        }
  }
}

/** A line displaying a budget category and its amount */
@Composable
fun DisplayLine(category: String, amount: String, testTag: String) {
  ListItem(
      headlineContent = { Text(category) },
      trailingContent = { Text(amount) },
      modifier =
          Modifier.clickable { /*TODO: open screen of the selected budget category*/}
              .testTag(testTag),
  )
}
