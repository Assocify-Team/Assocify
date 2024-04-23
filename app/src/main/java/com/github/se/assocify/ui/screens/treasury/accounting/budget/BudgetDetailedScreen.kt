package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.TVA
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.DropdownFilterChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetailedScreen(subCategoryUid: String, navigationActions: NavigationActions) {
  // TODO: get subcategory from db
  val subCategory =
      AccountingSubCategory(subCategoryUid, "Logistics Pole", AccountingCategory("Pole"), 1205)
  // TODO: fetch from db
  val budgetItems =
      listOf(
          BudgetItem(
              "1", "pair of scissors", 5, TVA.TVA_8, "scissors for paper cutting", subCategory),
          BudgetItem("2", "sweaters", 1000, TVA.TVA_8, "order for 1000 sweaters", subCategory),
          BudgetItem("3", "chairs", 200, TVA.TVA_8, "order for 200 chairs", subCategory),
      )

  val yearList = listOf("2023", "2022", "2021")
  var selectedYear by remember { mutableStateOf(yearList.first()) }
  val totalAmount = budgetItems.sumOf { it.amount }
  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text(text = subCategory.name, style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.back() },
                  modifier = Modifier.testTag("backButton")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                  }
            })
      },
      contentWindowInsets = WindowInsets(10.dp, 10.dp, 10.dp, 0.dp),
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("BudgetDetailedScreen"),
      floatingActionButton = {
        FloatingActionButton(
            modifier = Modifier.testTag("createNewBudgetItem"),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            onClick = { /*TODO: create new item*/}) {
              Icon(Icons.Outlined.Add, "Create")
            }
      },
      content = { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(innerPadding),
        ) {
          item {
            DropdownFilterChip(yearList.first(), yearList, "yearListTag") { selectedYear = it }
          }

          items(budgetItems) {
            DisplayItem(it)
            HorizontalDivider(Modifier.fillMaxWidth().padding(vertical = 8.dp))
          }

          item { TotalItems(totalAmount) }
        }
      })
}

@Composable
fun TotalItems(totalAmount: Int) {
  ListItem(
      modifier = Modifier.fillMaxWidth().testTag("totalItems"),
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

@Composable
fun DisplayItem(budgetItem: BudgetItem) {
  ListItem(
      headlineContent = { Text(budgetItem.nameItem) },
      trailingContent = { Text("${budgetItem.amount}") },
      supportingContent = { Text(budgetItem.description) },
      modifier = Modifier.clickable { /*TODO: edit details*/})
}
