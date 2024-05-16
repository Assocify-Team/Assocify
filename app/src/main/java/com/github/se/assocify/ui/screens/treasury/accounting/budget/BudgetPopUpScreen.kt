package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.PopupProperties
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.TVA
import java.time.Year
import java.util.UUID


@Composable
fun DisplayEditBudget(budgetViewModel: BudgetDetailedViewModel) {
  BudgetPopUpScreen(budgetViewModel)
}

@Composable
fun DisplayCreateBudget(budgetViewModel: BudgetDetailedViewModel){
  BudgetPopUpScreen(budgetViewModel)
}

/**
 * Displays the popup to edit a specific budget element
 *
 * @param budgetViewModel the viewModel of the budget details
 */
@Composable
fun BudgetPopUpScreen(budgetViewModel: BudgetDetailedViewModel) {
  val budgetModel by budgetViewModel.uiState.collectAsState()
  val budget = if (budgetModel.editedBudgetItem == null) BudgetItem(uid = UUID.randomUUID().toString(),nameItem = "", amount = 0, tva = TVA.TVA_0, description = "", subcategoryUID = "", year =Year.now().value.toInt()) else budgetModel.editedBudgetItem!!
  var nameString by remember { mutableStateOf(budget.nameItem) }
  var amountString by remember { mutableStateOf(budget.amount.toString()) }
  var tvaTypeString by remember { mutableStateOf(budget.tva.toString()) }
  var tvaString by remember { mutableStateOf(budget.tva.rate.toString()) }
  var descriptionString by remember { mutableStateOf(budget.description) }
  var yearString by remember { mutableStateOf(budget.year.toString()) }

  Dialog(onDismissRequest = { budgetViewModel.cancelEditing() }) {
    Card(
      modifier = Modifier.padding(16.dp).testTag("editDialogBox"),
      shape = RoundedCornerShape(16.dp),
    ) {
      Column() {
        val mainText = if (budgetModel.editedBudgetItem == null) "Create Budget Item" else "Edit Budget Item"
        Text(mainText, fontSize = 20.sp, modifier = Modifier.padding(16.dp))
        OutlinedTextField(
          modifier = Modifier.padding(8.dp).testTag("editNameBox"),
          value = nameString,
          onValueChange = { nameString = it },
          label = { Text("Name") },
          supportingText = {})
        OutlinedTextField(
          modifier = Modifier.padding(8.dp),
          value = amountString,
          onValueChange = { amountString = it },
          label = { Text("Amount") },
          keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
          supportingText = {})
        Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
          var tvaExtended by remember { mutableStateOf(false) }
          FilterChip(
            modifier = Modifier.fillMaxWidth().height(60.dp),
            selected = tvaExtended,
            onClick = { tvaExtended = !tvaExtended },
            label = { Text(tvaTypeString) },
            trailingIcon = {
              Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Expand")
            })
          DropdownMenu(
            modifier = Modifier,
            expanded = tvaExtended,
            onDismissRequest = { tvaExtended = false },
            properties = PopupProperties(focusable = true)
          ) {
            TVA.entries.forEach { tva ->
              DropdownMenuItem(
                text = { Text(tva.toString()) },
                onClick = {
                  tvaTypeString = tva.toString()
                  tvaString = tva.rate.toString()
                  tvaExtended = false
                })
            }
          }
        }
        OutlinedTextField(
          modifier = Modifier.padding(8.dp),
          value = descriptionString,
          onValueChange = { descriptionString = it },
          label = { Text("Description") },
          supportingText = {})
        OutlinedTextField(
          modifier = Modifier.padding(8.dp),
          value = yearString,
          onValueChange = { yearString = it },
          label = { Text("Year") },
          keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
          supportingText = {})
        Row(
          modifier = Modifier.fillMaxWidth().padding(15.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Button(
            onClick = { budgetViewModel.cancelEditing() },
            modifier = Modifier.padding(15.dp).testTag("editDismissButton"),
          ) {
            Text("Dismiss")
          }
          Button(
            onClick = {
              if(budgetModel.editedBudgetItem != null){
                budgetViewModel.saveEditing(
                  BudgetItem(
                    budget.uid,
                    nameItem = nameString,
                    amount = amountString.toInt(),
                    tva = TVA.floatToTVA(tvaString.toFloat()),
                    description = descriptionString,
                    subcategoryUID = budget.subcategoryUID,
                    year = yearString.toInt())
                )
              } else{
                budgetViewModel.saveCreating(
                  BudgetItem(
                    budget.uid,
                    nameItem = nameString,
                    amount = amountString.toInt(),
                    tva = TVA.floatToTVA(tvaString.toFloat()),
                    description = descriptionString,
                    subcategoryUID = budget.subcategoryUID,
                    year = yearString.toInt())
                )
              }
            },
            modifier = Modifier.padding(15.dp).testTag("editConfirmButton"),
          ) {
            Text("Confirm")
          }
        }
      }
    }
  }
}
