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
import com.github.se.assocify.ui.util.PriceUtil
import java.time.LocalDate
import java.util.UUID

@Composable
fun DisplayEditBalance(balanceDetailedViewModel: BalanceDetailedViewModel) {
  BalancePopUpScreen(balanceDetailedViewModel)
}

@Composable
fun DisplayCreateBalance(balanceDetailedViewModel: BalanceDetailedViewModel) {
  BalancePopUpScreen(balanceDetailedViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalancePopUpScreen(balanceDetailedViewModel: BalanceDetailedViewModel) {
  val balanceModel by balanceDetailedViewModel.uiState.collectAsState()
  val balance =
      if (balanceModel.editedBalanceItem != null) balanceModel.editedBalanceItem!!
      else
          BalanceItem(
              amount = 0,
              tva = TVA.TVA_0,
              status = Status.Pending,
              date = LocalDate.now(),
              nameItem = "",
              subcategoryUID = "",
              receiptUID = "",
              description = "",
              assignee = "",
              uid = UUID.randomUUID().toString())
  var nameString by remember { mutableStateOf(balance.nameItem) }
  var receiptUid by remember { mutableStateOf(balance.receiptUID) }
  var receiptName by remember {
    mutableStateOf(
        balanceModel.receiptList
            .filter { it.uid == balance.receiptUID }
            .map { it.title }
            .getOrElse(0) { "" })
  }
  var amountString by remember { mutableStateOf(PriceUtil.fromCents(balance.amount)) }
  var tvaString by remember { mutableStateOf(balance.tva.rate.toString()) }
  var descriptionString by remember { mutableStateOf(balance.description) }
  var date by remember { mutableStateOf(balance.date) }
  var assignee by remember { mutableStateOf((balance.assignee)) }
  var mutableStatus by remember { mutableStateOf(balance.status) }
  val titleText = if (balanceModel.editing) "Edit Balance Detail" else "Create Balance Detail"
  Dialog(onDismissRequest = { balanceDetailedViewModel.cancelPopUp() }) {
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
                        titleText,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("editDialogTitle"))
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close dialog",
                        modifier =
                            Modifier.clickable { balanceDetailedViewModel.cancelPopUp() }
                                .testTag("editSubCategoryCancelButton"))
                  }
            }
            // The name box
            item {
              OutlinedTextField(
                  singleLine = true,
                  modifier = Modifier.padding(8.dp).testTag("editDialogName"),
                  value = nameString,
                  isError = balanceModel.errorName,
                  onValueChange = {
                    nameString = it
                    balanceDetailedViewModel.checkName(nameString)
                  },
                  label = { Text("Name") },
                  supportingText = {
                    if (balanceModel.errorName) Text(balanceModel.errorNameMessage)
                  })
            }

            // The receipt selector
            item {
              var receiptExpanded by remember { mutableStateOf(false) }
              ExposedDropdownMenuBox(
                  expanded = receiptExpanded,
                  onExpandedChange = { receiptExpanded = !receiptExpanded },
                  modifier = Modifier.testTag("receiptDropdown").padding(8.dp)) {
                    OutlinedTextField(
                        isError = balanceModel.errorReceipt || balanceModel.receiptAlreadyAssigned,
                        supportingText = {
                          if (balanceModel.errorReceipt) Text("You have to choose a receipt!")
                          else if (balanceModel.receiptAlreadyAssigned)
                              Text("This receipt is already assigned!")
                        },
                        value = receiptName,
                        onValueChange = { balanceDetailedViewModel.checkReceipt(receiptName) },
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
                  singleLine = true,
                  isError = balanceModel.errorAmount,
                  modifier = Modifier.padding(8.dp),
                  value = amountString,
                  onValueChange = {
                    amountString = it
                    balanceDetailedViewModel.checkAmount(amountString)
                  },
                  label = { Text("Amount") },
                  keyboardOptions =
                      KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                  supportingText = {
                    if (balanceModel.errorAmount) Text("The amount must be a valid number!")
                  })
            }

            // The TVA box
            item {
              var balanceTvaExpanded by remember { mutableStateOf(false) }
              ExposedDropdownMenuBox(
                  expanded = balanceTvaExpanded,
                  onExpandedChange = { balanceTvaExpanded = !balanceTvaExpanded },
                  modifier = Modifier.testTag("categoryDropdown").padding(8.dp)) {
                    OutlinedTextField(
                        value = "$tvaString%",
                        onValueChange = {},
                        label = { Text("Tva") },
                        trailingIcon = {
                          ExposedDropdownMenuDefaults.TrailingIcon(expanded = balanceTvaExpanded)
                        },
                        readOnly = true,
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier =
                            Modifier.menuAnchor().clickable {
                              balanceTvaExpanded = !balanceTvaExpanded
                            })
                    ExposedDropdownMenu(
                        expanded = balanceTvaExpanded,
                        onDismissRequest = { balanceTvaExpanded = false }) {
                          TVA.entries.forEach { tva ->
                            DropdownMenuItem(
                                text = { Text(tva.toString()) },
                                onClick = {
                                  tvaString = tva.rate.toString()
                                  balanceTvaExpanded = false
                                })
                          }
                        }
                  }
            }

            // The description field
            item {
              OutlinedTextField(
                  singleLine = true,
                  modifier = Modifier.padding(8.dp),
                  value = descriptionString,
                  onValueChange = {
                    descriptionString = it
                    balanceDetailedViewModel.checkDescription(descriptionString)
                  },
                  label = { Text("Description") },
                  supportingText = {
                    if (balanceModel.errorDescription) Text("The description is too long!!")
                  })
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
                  singleLine = true,
                  isError = balanceModel.errorAssignee,
                  modifier = Modifier.padding(8.dp).testTag("editDialogAssignee"),
                  value = assignee,
                  onValueChange = {
                    assignee = it
                    balanceDetailedViewModel.checkAssignee(assignee)
                  },
                  label = { Text("Assignee") },
                  supportingText = {
                    if (balanceModel.errorAssignee) Text("You have to enter a valid assignee!")
                  })
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
                        label = { Text("Status") },
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
                  horizontalArrangement =
                      if (balanceModel.editing) Arrangement.SpaceBetween else Arrangement.End,
              ) {
                if (balanceModel.editing) {
                  Button(
                      onClick = { balanceDetailedViewModel.deleteBalanceItem(balance.uid) },
                      modifier = Modifier.testTag("editDeleteButton"),
                  ) {
                    Text("Delete")
                  }
                }
                Button(
                    onClick = {
                      balanceDetailedViewModel.checkAll(
                          nameString, receiptUid, amountString, assignee, descriptionString)
                      val newBalanceItem =
                          BalanceItem(
                              balance.uid,
                              nameString,
                              balanceModel.subCategory!!.uid,
                              receiptUid,
                              PriceUtil.toCents(amountString),
                              TVA.floatToTVA(tvaString.toFloat()),
                              descriptionString,
                              date,
                              assignee,
                              mutableStatus)
                      if (balanceModel.creating) {
                        balanceDetailedViewModel.saveCreation(newBalanceItem)
                      } else {
                        balanceDetailedViewModel.saveEditing(newBalanceItem)
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
