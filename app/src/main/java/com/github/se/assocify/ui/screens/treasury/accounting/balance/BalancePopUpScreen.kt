package com.github.se.assocify.ui.screens.treasury.accounting.balance

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.model.entities.TVA
import com.github.se.assocify.ui.composables.DatePickerWithDialog
import com.github.se.assocify.ui.util.DateUtil

@Composable
fun DisplayEditBalance(balanceDetailedViewModel: BalanceDetailedViewModel) {
  BalancePopUpScreen(balanceDetailedViewModel)
}

@Composable
fun DisplayAddBalance(balanceDetailedViewModel: BalanceDetailedViewModel) {
  BalancePopUpScreen(balanceDetailedViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalancePopUpScreen(balanceDetailedViewModel: BalanceDetailedViewModel) {
  val balanceModel by balanceDetailedViewModel.uiState.collectAsState()
  val balance = balanceModel.editedBalanceItem!!
  var nameString by remember { mutableStateOf(balance.nameItem) }
  var subCategoryUid by remember { mutableStateOf(balance.subcategoryUID) }
  var subCategoryName by remember {
    mutableStateOf(
        balanceModel.subCategoryList
            .filter { it.uid == balance.subcategoryUID }
            .map { it.name }
            .getOrElse(0) { "" })
  }
  var receiptUid by remember { mutableStateOf(balance.receiptUID) }
  var receiptName by remember {
    mutableStateOf(
        balanceModel.receiptList
            .filter { it.uid == balance.receiptUID }
            .map { it.title }
            .getOrElse(0) { "" })
  }
  var amountString by remember { mutableStateOf(balance.amount.toString()) }
  var tvaString by remember { mutableStateOf(balance.tva.rate.toString()) }
  var descriptionString by remember { mutableStateOf(balance.description) }
  var date by remember { mutableStateOf(balance.date) }
  var assignee by remember { mutableStateOf((balance.assignee)) }
  var mutableStatus by remember { mutableStateOf(balance.status) }
  Dialog(onDismissRequest = { balanceDetailedViewModel.cancelEditing() }) {
    Card(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp).testTag("editDialogBox"),
        shape = RoundedCornerShape(16.dp),
    ) {
      LazyColumn(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(8.dp).testTag("editDialogColumn")) {
            item {
              Row(
                  modifier = Modifier.fillMaxWidth().padding(8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Edit Balance Detail",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("editDialogTitle"))
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close dialog",
                        modifier =
                            Modifier.clickable { balanceDetailedViewModel.cancelEditing() }
                                .testTag("editSubCategoryCancelButton"))
                  }
            }
            // The name box
            item {
              OutlinedTextField(
                  modifier = Modifier.padding(8.dp).testTag("editDialogName"),
                  value = nameString,
                  onValueChange = { nameString = it },
                  label = { Text("Name") },
                  supportingText = {})
            }
            // The subcategory selector
            item {
              var subcategoryExpanded by remember { mutableStateOf(false) }
              ExposedDropdownMenuBox(
                  expanded = subcategoryExpanded,
                  onExpandedChange = { subcategoryExpanded = !subcategoryExpanded },
                  modifier = Modifier.testTag("categoryDropdown").padding(8.dp)) {
                    OutlinedTextField(
                        value = subCategoryName,
                        onValueChange = {},
                        label = { Text("SubCategory") },
                        trailingIcon = {
                          ExposedDropdownMenuDefaults.TrailingIcon(expanded = subcategoryExpanded)
                        },
                        readOnly = true,
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier =
                            Modifier.menuAnchor().clickable {
                              subcategoryExpanded = !subcategoryExpanded
                            })
                    ExposedDropdownMenu(
                        expanded = subcategoryExpanded,
                        onDismissRequest = { subcategoryExpanded = false }) {
                          balanceModel.subCategoryList.forEach { subCat ->
                            DropdownMenuItem(
                                text = { Text(subCat.name) },
                                onClick = {
                                  subCategoryUid = subCat.uid
                                  subCategoryName = subCat.name
                                  subcategoryExpanded = false
                                })
                          }
                        }
                  }
            }
            // The receipt selector
            item {
              var receiptExpanded by remember { mutableStateOf(false) }
              ExposedDropdownMenuBox(
                  expanded = receiptExpanded,
                  onExpandedChange = { receiptExpanded = !receiptExpanded },
                  modifier = Modifier.testTag("categoryDropdown").padding(8.dp)) {
                    OutlinedTextField(
                        value = receiptName,
                        onValueChange = {},
                        label = { Text("Receipt") },
                        trailingIcon = {
                          ExposedDropdownMenuDefaults.TrailingIcon(expanded = receiptExpanded)
                        },
                        readOnly = true,
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier =
                            Modifier.menuAnchor().clickable { receiptExpanded = !receiptExpanded })
                    ExposedDropdownMenu(
                        expanded = receiptExpanded,
                        onDismissRequest = { receiptExpanded = false }) {
                          balanceModel.receiptList.forEach { receipt ->
                            DropdownMenuItem(
                                text = { Text(receipt.title) },
                                onClick = {
                                  receiptUid = receipt.uid
                                  receiptName = receipt.title
                                  receiptExpanded = false
                                })
                          }
                        }
                  }
            }

            // The amount field
            item {
              OutlinedTextField(
                  modifier = Modifier.padding(8.dp),
                  value = amountString,
                  onValueChange = { amountString = it },
                  label = { Text("Amount") },
                  keyboardOptions =
                      KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                  supportingText = {})
            }

            // The TVA box
            item {
              var tvaExpanded by remember { mutableStateOf(false) }
              ExposedDropdownMenuBox(
                  expanded = tvaExpanded,
                  onExpandedChange = { tvaExpanded = !tvaExpanded },
                  modifier = Modifier.testTag("categoryDropdown").padding(8.dp)) {
                    OutlinedTextField(
                        value = tvaString + "%",
                        onValueChange = {},
                        label = { Text("Tva") },
                        trailingIcon = {
                          ExposedDropdownMenuDefaults.TrailingIcon(expanded = tvaExpanded)
                        },
                        readOnly = true,
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor().clickable { tvaExpanded = !tvaExpanded })
                    ExposedDropdownMenu(
                        expanded = tvaExpanded, onDismissRequest = { tvaExpanded = false }) {
                          TVA.entries.forEach { tva ->
                            DropdownMenuItem(
                                text = { Text(tva.toString()) },
                                onClick = {
                                  tvaString = tva.rate.toString()
                                  tvaExpanded = false
                                })
                          }
                        }
                  }
            }

            // The description field
            item {
              OutlinedTextField(
                  modifier = Modifier.padding(8.dp),
                  value = descriptionString,
                  onValueChange = { descriptionString = it },
                  label = { Text("Description") },
                  supportingText = {})
            }
            // The date screen
            item {
              DatePickerWithDialog(
                  value = DateUtil.formatDate(date),
                  onDateSelect = { newDate ->
                    if (newDate != null) {
                      date = newDate
                    }
                  },
                  modifier = Modifier.padding(8.dp),
              )
            }

            // The assignee field
            item {
              OutlinedTextField(
                  modifier = Modifier.padding(8.dp),
                  value = assignee,
                  onValueChange = { assignee = it },
                  label = { Text("Assignee") },
                  supportingText = {})
            }
            // The status picker
            item {
              var statusExpanded by remember { mutableStateOf(false) }
              ExposedDropdownMenuBox(
                  expanded = statusExpanded,
                  onExpandedChange = { statusExpanded = !statusExpanded },
                  modifier = Modifier.testTag("categoryDropdown").padding(8.dp)) {
                    OutlinedTextField(
                        value = mutableStatus.name,
                        onValueChange = {},
                        label = { Text("Tag") },
                        trailingIcon = {
                          ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                        },
                        readOnly = true,
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier =
                            Modifier.menuAnchor().clickable { statusExpanded = !statusExpanded })
                    ExposedDropdownMenu(
                        expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                          Status.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name) },
                                onClick = {
                                  mutableStatus = status
                                  statusExpanded = false
                                })
                          }
                        }
                  }
            }

            // The buttons
            item {
              Row(
                  modifier = Modifier.fillMaxWidth().padding(8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Button(
                    onClick = { balanceDetailedViewModel.deleteBalanceItem(balance.uid) },
                    modifier = Modifier.testTag("editDeleteButton"),
                ) {
                  Text("Delete")
                }
                Button(
                    onClick = {
                      balanceDetailedViewModel.saveEditing(
                          BalanceItem(
                              balance.uid,
                              nameString,
                              subCategoryUid,
                              receiptUid,
                              amountString.toInt(),
                              TVA.floatToTVA(tvaString.toFloat()),
                              descriptionString,
                              date,
                              assignee,
                              mutableStatus))
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
