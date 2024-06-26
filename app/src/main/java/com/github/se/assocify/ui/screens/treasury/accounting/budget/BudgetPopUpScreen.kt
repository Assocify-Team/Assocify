package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.TVA
import com.github.se.assocify.ui.util.PriceUtil
import java.time.Year
import java.util.UUID

/**
 * Displays the popup to edit a specific budget element
 *
 * @param budgetViewModel the viewModel of the budget details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetPopUpScreen(budgetViewModel: BudgetDetailedViewModel) {
  val budgetModel by budgetViewModel.uiState.collectAsState()
  val budget =
      if (budgetModel.editedBudgetItem == null)
          BudgetItem(
              uid = UUID.randomUUID().toString(),
              nameItem = "",
              amount = 0,
              tva = TVA.TVA_0,
              description = "",
              subcategoryUID = budgetModel.subCategory!!.uid,
              year = Year.now().value)
      else budgetModel.editedBudgetItem!!
  var nameString by remember { mutableStateOf(budget.nameItem) }
  var amountString by remember { mutableStateOf(PriceUtil.fromCents(budget.amount)) }
  var tvaString by remember { mutableStateOf(budget.tva.rate.toString()) }
  var descriptionString by remember { mutableStateOf(budget.description) }

  Dialog(
      onDismissRequest = {
        if (budgetModel.editing) budgetViewModel.cancelEditing()
        else budgetViewModel.cancelCreating()
      }) {
        Card(
            modifier =
                Modifier.padding(vertical = 16.dp, horizontal = 8.dp).testTag("editDialogBox"),
            shape = RoundedCornerShape(16.dp),
        ) {
          LazyColumn(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(8.dp).testTag("editDialogColumn")) {
                item {
                  val mainText =
                      if (budgetModel.creating) "Create Budget Item" else "Edit Budget Item"
                  Row(
                      modifier = Modifier.fillMaxWidth().padding(8.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            mainText,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.testTag("editDialogTitle"))
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close dialog",
                            modifier =
                                Modifier.clickable {
                                      if (budgetModel.editing) budgetViewModel.cancelEditing()
                                      else budgetViewModel.cancelCreating()
                                    }
                                    .testTag("editSubCategoryCancelButton"))
                      }
                }
                item {
                  OutlinedTextField(
                      singleLine = true,
                      modifier = Modifier.padding(8.dp).testTag("editNameBox"),
                      value = nameString,
                      isError = budgetModel.titleError,
                      onValueChange = {
                        nameString = it
                        budgetViewModel.setTitle(it)
                      },
                      label = { Text("Name") },
                      supportingText = {
                        if (budgetModel.titleError) Text(budgetModel.titleErrorString)
                      })
                }
                item {
                  OutlinedTextField(
                      singleLine = true,
                      modifier = Modifier.padding(8.dp).testTag("editAmountBox"),
                      value = amountString,
                      isError = budgetModel.amountError,
                      onValueChange = {
                        amountString = it
                        budgetViewModel.setAmount(it)
                      },
                      label = { Text("Amount HT") },
                      keyboardOptions =
                          KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                      supportingText = {
                        if (budgetModel.amountError) Text("You need to input a correct amount!!")
                      })
                }
                item {
                  var budgetTvaExpanded by remember { mutableStateOf(false) }
                  ExposedDropdownMenuBox(
                      expanded = budgetTvaExpanded,
                      onExpandedChange = { budgetTvaExpanded = !budgetTvaExpanded },
                      modifier = Modifier.testTag("categoryDropdown").padding(8.dp)) {
                        OutlinedTextField(
                            value = "$tvaString%",
                            onValueChange = {},
                            label = { Text("Tva") },
                            trailingIcon = {
                              ExposedDropdownMenuDefaults.TrailingIcon(expanded = budgetTvaExpanded)
                            },
                            readOnly = true,
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier =
                                Modifier.menuAnchor().clickable {
                                  budgetTvaExpanded = !budgetTvaExpanded
                                })
                        ExposedDropdownMenu(
                            expanded = budgetTvaExpanded,
                            onDismissRequest = { budgetTvaExpanded = false }) {
                              TVA.entries.forEach { tva ->
                                DropdownMenuItem(
                                    text = { Text(tva.toString()) },
                                    onClick = {
                                      tvaString = tva.rate.toString()
                                      budgetTvaExpanded = false
                                    })
                              }
                            }
                      }
                }
                item {
                  OutlinedTextField(
                      singleLine = true,
                      isError = budgetModel.descriptionError,
                      modifier = Modifier.padding(8.dp),
                      value = descriptionString,
                      onValueChange = {
                        descriptionString = it
                        budgetViewModel.setDescription(descriptionString)
                      },
                      label = { Text("Description") },
                      supportingText = {
                        if (budgetModel.descriptionError) Text("The description is too long!!")
                      })
                }
                item {
                  Row(
                      modifier = Modifier.fillMaxWidth().padding(8.dp),
                      horizontalArrangement = Arrangement.End,
                  ) {
                    if (budgetModel.editing) {
                      TextButton(
                          onClick = { budgetViewModel.deleteEditing() },
                          modifier = Modifier.testTag("deleteButton"),
                      ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                      }
                    }
                    TextButton(
                        onClick = {
                          budgetViewModel.setTitle(nameString)
                          budgetViewModel.setAmount(amountString)
                          budgetViewModel.setDescription(descriptionString)
                          if (budgetModel.creating && amountString.toDoubleOrNull() != null) {
                            budgetViewModel.saveCreating(
                                BudgetItem(
                                    budget.uid,
                                    nameItem = nameString,
                                    amount = PriceUtil.toCents(amountString),
                                    tva = TVA.floatToTVA(tvaString.toFloat()),
                                    description = descriptionString,
                                    subcategoryUID = budget.subcategoryUID,
                                    year = budgetModel.subCategory!!.year))
                          } else if (amountString.toDoubleOrNull() != null) {
                            budgetViewModel.saveEditing(
                                BudgetItem(
                                    budget.uid,
                                    nameItem = nameString,
                                    amount = PriceUtil.toCents(amountString),
                                    tva = TVA.floatToTVA(tvaString.toFloat()),
                                    description = descriptionString,
                                    subcategoryUID = budget.subcategoryUID,
                                    year = budgetModel.subCategory!!.year))
                          }
                        },
                        modifier = Modifier.testTag("editConfirmButton"),
                    ) {
                      Text("Confirm")
                    }
                  }
                }
              }
        }
      }
}
