package com.github.se.assocify.ui.screens.treasury.accounting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceDetailedViewModel
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceItemState
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetDetailedViewModel
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetItemState

/**
 * Displays the popup to edit a specific subcategory
 *
 * @param page: The page to display (either "budget" or "balance")
 * @param budgetViewModel the viewModel of the budget details
 * @param balanceViewModel the viewModel of the balance details
 * @param balanceState the state of the balance
 * @param budgetState the state of the budget
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayEditSubCategory(
    page: AccountingPage,
    budgetViewModel: BudgetDetailedViewModel,
    balanceViewModel: BalanceDetailedViewModel,
    navigationActions: NavigationActions,
    balanceState: BalanceItemState,
    budgetState: BudgetItemState
) {
  val subCategory =
      when (page) {
        AccountingPage.BALANCE -> balanceState.subCategory
        AccountingPage.BUDGET -> budgetState.subCategory
      }

  val categoryList =
      when (page) {
        AccountingPage.BALANCE -> balanceState.categoryList
        AccountingPage.BUDGET -> budgetState.categoryList
      }

  var name by remember { mutableStateOf(subCategory.name) }
  var categoryUid by remember { mutableStateOf(subCategory.categoryUID) }
  var year by remember { mutableStateOf(subCategory.year.toString()) }
  var expanded by remember { mutableStateOf(false) }
  var selectedCategory by remember {
    mutableStateOf(categoryList.find { it.uid == subCategory.categoryUID }?.name ?: "No tag")
  }
  Dialog(
      onDismissRequest = {
        when (page) {
          AccountingPage.BALANCE -> balanceViewModel.cancelSubCategoryEditingInBalance()
          AccountingPage.BUDGET -> budgetViewModel.cancelSubCategoryEditingInBudget()
        }
      },
      properties = DialogProperties()) {
        Card(
            modifier =
                Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
                    .testTag("editSubCategoryDialog"),
            shape = RoundedCornerShape(16.dp),
        ) {
          Column(
              modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp),
              horizontalAlignment = Alignment.CenterHorizontally) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Text("Edit ${subCategory.name}", style = MaterialTheme.typography.titleLarge)
                  Icon(
                      Icons.Default.Close,
                      contentDescription = "Close dialog",
                      modifier =
                          Modifier.clickable {
                                when (page) {
                                  AccountingPage.BALANCE ->
                                      balanceViewModel.cancelSubCategoryEditingInBalance()
                                  AccountingPage.BUDGET ->
                                      budgetViewModel.cancelSubCategoryEditingInBudget()
                                }
                              }
                              .testTag("editSubCategoryCancelButton"))
                }

                // Edit fields
                OutlinedTextField(
                    modifier = Modifier.testTag("editSubCategoryNameBox").padding(8.dp),
                    value = name,
                    singleLine = true,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    supportingText = {})
                OutlinedTextField(
                    modifier = Modifier.testTag("editSubCategoryYearBox").padding(8.dp),
                    value = year,
                    singleLine = true,
                    onValueChange = { year = it },
                    label = { Text("Year") },
                    supportingText = {})

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.testTag("categoryDropdown").padding(8.dp)) {
                      OutlinedTextField(
                          value = selectedCategory,
                          singleLine = true,
                          onValueChange = {},
                          label = { Text("Tag") },
                          trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                          },
                          readOnly = true,
                          colors = ExposedDropdownMenuDefaults.textFieldColors(),
                          modifier = Modifier.menuAnchor().clickable { expanded = !expanded })
                      ExposedDropdownMenu(
                          expanded = expanded, onDismissRequest = { expanded = false }) {
                            categoryList.forEach { category ->
                              DropdownMenuItem(
                                  text = { Text(category.name) },
                                  onClick = {
                                    categoryUid = category.uid
                                    selectedCategory = category.name
                                    expanded = false
                                  })
                            }
                          }
                    }
              }

          // Buttons
          Row(
              modifier = Modifier.fillMaxWidth().padding(8.dp),
              horizontalArrangement = Arrangement.Absolute.Right) {
                // Delete button
                TextButton(
                    onClick = {
                      when (page) {
                        AccountingPage.BALANCE -> balanceViewModel.deleteSubCategoryInBalance()
                        AccountingPage.BUDGET -> budgetViewModel.deleteSubCategoryInBudget()
                      }
                      navigationActions.back()
                    },
                    modifier = Modifier.testTag("editSubCategoryDeleteButton")) {
                      Text("Delete", color = MaterialTheme.colorScheme.error)
                    }

                // Save button
                TextButton(
                    onClick = {
                      when (page) {
                        AccountingPage.BALANCE ->
                            balanceViewModel.saveSubCategoryEditingInBalance(
                                name, categoryUid, year.toInt())
                        AccountingPage.BUDGET ->
                            budgetViewModel.saveSubCategoryEditingInBudget(
                                name, categoryUid, year.toInt())
                      }
                    },
                    modifier = Modifier.testTag("editSubCategorySaveButton"),
                ) {
                  Text("Save")
                }
              }
        }
      }
}
