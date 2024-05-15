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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.model.entities.TVA
import com.github.se.assocify.ui.composables.DatePickerWithDialog
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayEditBalance(balanceDetailedViewModel: BalanceDetailedViewModel) {
  val balanceModel by balanceDetailedViewModel.uiState.collectAsState()
  val balance = balanceModel.editedBalanceItem!!
  var nameString by remember { mutableStateOf("") }
  var subCategoryUid by remember { mutableStateOf("") }
  var subCategoryName by remember { mutableStateOf("") }
  var receiptUid by remember { mutableStateOf("") }
  var receiptName by remember { mutableStateOf("") }
  var amount by remember { mutableIntStateOf(0) }
  var tvaString by remember { mutableStateOf("") }
  var tvaTypeString by remember { mutableStateOf("") }
  var descriptionString by remember { mutableStateOf("") }
  var date by remember { mutableStateOf(LocalDate.now()) }
  var assignee by remember { mutableStateOf("") }
  var mutableStatus by remember { mutableStateOf(Status.Pending) }
  Dialog(onDismissRequest = { balanceDetailedViewModel.cancelEditing() }) {
    Card(
      modifier = Modifier
        .padding(16.dp)
        .testTag("editDialogBox"),
      shape = RoundedCornerShape(16.dp),
    ) {
      LazyColumn {
        item {
          Row{
            IconButton(onClick = { balanceDetailedViewModel.cancelEditing() }) {
              Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
            Text("Edit Balance Item", fontSize = 20.sp, modifier = Modifier.padding(16.dp))
          }
        }
        // The name box
        item {
          OutlinedTextField(
            modifier = Modifier.padding(8.dp),
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
            modifier = Modifier
              .testTag("categoryDropdown")
              .padding(8.dp)
          ) {
            OutlinedTextField(
              value = subCategoryUid,
              onValueChange = {},
              label = { Text("SubCategory") },
              trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = subcategoryExpanded)
              },
              readOnly = true,
              colors = ExposedDropdownMenuDefaults.textFieldColors(),
              modifier = Modifier
                .menuAnchor()
                .clickable { subcategoryExpanded = !subcategoryExpanded })
            ExposedDropdownMenu(
              expanded = subcategoryExpanded, onDismissRequest = { subcategoryExpanded = false }) {
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
            modifier = Modifier
              .testTag("categoryDropdown")
              .padding(8.dp)
          ) {
            OutlinedTextField(
              value = receiptUid,
              onValueChange = {},
              label = { Text("Receipt") },
              trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = receiptExpanded)
              },
              readOnly = true,
              colors = ExposedDropdownMenuDefaults.textFieldColors(),
              modifier = Modifier
                .menuAnchor()
                .clickable { receiptExpanded = !receiptExpanded })
            ExposedDropdownMenu(
              expanded = receiptExpanded, onDismissRequest = { receiptExpanded = false }) {
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
            value = amount.toString(),
            onValueChange = { amount = it.toInt() },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            supportingText = {})
        }

        //The TVA box
        item {
          var tvaExpanded by remember { mutableStateOf(false) }
          ExposedDropdownMenuBox(
            expanded = tvaExpanded,
            onExpandedChange = { tvaExpanded = !tvaExpanded },
            modifier = Modifier
              .testTag("categoryDropdown")
              .padding(8.dp)
          ) {
            OutlinedTextField(
              value = tvaString,
              onValueChange = {},
              label = { Text("Tva") },
              trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = tvaExpanded)
              },
              readOnly = true,
              colors = ExposedDropdownMenuDefaults.textFieldColors(),
              modifier = Modifier
                .menuAnchor()
                .clickable { tvaExpanded = !tvaExpanded })
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

        //The description field
        item {
          OutlinedTextField(
            modifier = Modifier.padding(8.dp),
            value = descriptionString,
            onValueChange = { descriptionString = it },
            label = { Text("Description") },
            supportingText = {})
        }
        //The date screen
        item {
          DatePickerWithDialog(value = "Date", onDateSelect = { newDate ->
            date = newDate
          })
        }


        //The assignee field
        item {
          OutlinedTextField(
            modifier = Modifier.padding(8.dp),
            value = assignee,
            onValueChange = { assignee = it },
            label = { Text("Assignee") },
            supportingText = {})
        }
        //The status picker
        item {
          var statusExpanded by remember { mutableStateOf(false) }
          ExposedDropdownMenuBox(
            expanded = statusExpanded,
            onExpandedChange = { statusExpanded = !statusExpanded },
            modifier = Modifier
              .testTag("categoryDropdown")
              .padding(8.dp)
          ) {
            OutlinedTextField(
              value = mutableStatus.name,
              onValueChange = {},
              label = { Text("Tag") },
              trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
              },
              readOnly = true,
              colors = ExposedDropdownMenuDefaults.textFieldColors(),
              modifier = Modifier
                .menuAnchor()
                .clickable { statusExpanded = !statusExpanded })
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


        //The buttons
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Button(
              onClick = { balanceDetailedViewModel.cancelEditing() },
              modifier = Modifier
                .padding(15.dp)
                .testTag("editDismissButton"),
            ) {
              Text("Dismiss")
            }
            Button(
              onClick = {
                balanceDetailedViewModel.saveEditing(
                  BalanceItem(
                    balance.uid,
                    nameString,
                    "",
                    "",
                    amount,
                    TVA.floatToTVA(tvaString.toFloat()),
                    descriptionString,
                    date,
                    assignee,
                    mutableStatus
                  )
                )
              },
              modifier = Modifier
                .padding(15.dp)
                .testTag("editConfirmButton"),
            ) {
              Text("Confirm")
            }
          }
        }
      }
    }
  }
}

