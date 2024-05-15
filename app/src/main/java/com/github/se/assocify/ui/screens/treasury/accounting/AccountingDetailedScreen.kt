package com.github.se.assocify.ui.screens.treasury.accounting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.PopupProperties
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.model.entities.TVA
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.DatePickerWithDialog
import com.github.se.assocify.ui.composables.DropdownFilterChip
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceDetailedViewModel
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetDetailedViewModel
import java.time.LocalDate

/**
 * The detailed screen of a subcategory in the accounting screen
 *
 * @param page: The page to display (either "budget" or "balance")
 * @param subCategoryUid: The unique identifier of the subcategory
 * @param navigationActions: The navigation actions
 * @param budgetDetailedViewModel: The view model for the budget detailed screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountingDetailedScreen(
    page: AccountingPage,
    subCategoryUid: String,
    navigationActions: NavigationActions,
    budgetDetailedViewModel: BudgetDetailedViewModel,
    balanceDetailedViewModel: BalanceDetailedViewModel
) {

  val budgetModel by budgetDetailedViewModel.uiState.collectAsState()
  val balanceModel by balanceDetailedViewModel.uiState.collectAsState()
  val subCategory = AccountingSubCategory(subCategoryUid, subCategoryUid, "", 1205, 2023)

  val yearList = listOf("2023", "2022", "2021")
  val statusList: List<String> = listOf("All Status") + Status.entries.map { it.name }
  val tvaList: List<String> = listOf("TTC", "HT")

  val totalAmount =
      when (page) {
        AccountingPage.BUDGET -> budgetModel.budgetList.sumOf { it.amount }
        AccountingPage.BALANCE -> balanceModel.balanceList.sumOf { it.amount }
      }

  Scaffold(
      topBar = {
        CenterAlignedTopAppBar(
            title = { Text(text = subCategory.name, style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.back() },
                  modifier = Modifier.testTag("backButton")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                  }
            })
      },
      contentWindowInsets = WindowInsets(20.dp, 20.dp, 20.dp, 0.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("AccountingDetailedScreen"),
      floatingActionButton = {
        FloatingActionButton(
            modifier = Modifier.testTag("createNewItem"),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            onClick = { /*TODO: create new item*/}) {
              Icon(Icons.Outlined.Add, "Create")
            }
      },
      content = { innerPadding ->
        if (budgetModel.editing && page == AccountingPage.BUDGET) {
          DisplayEditBudget(budgetDetailedViewModel)
        } else if (page == AccountingPage.BALANCE) {
          DisplayEditBalance(balanceDetailedViewModel)
        }

        LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .padding(innerPadding),
        ) {
          item {
            Row(
              Modifier
                .testTag("filterRowDetailed")
                .horizontalScroll(rememberScrollState())) {
              // Year filter
              DropdownFilterChip(yearList.first(), yearList, "yearListTag") {
                when (page) {
                  AccountingPage.BALANCE -> balanceDetailedViewModel.onYearFilter(it.toInt())
                  AccountingPage.BUDGET -> budgetDetailedViewModel.onYearFilter(it.toInt())
                }
              }

              // Status filter for balance Items
              if (page == AccountingPage.BALANCE) {
                DropdownFilterChip(statusList.first(), statusList, "statusListTag") {
                  balanceDetailedViewModel.onStatusFilter(
                      if (it == "All Status") {
                        null
                      } else {
                        Status.valueOf(it)
                      })
                }
              }

              // Tva filter
              DropdownFilterChip(tvaList.first(), tvaList, "tvaListTag") {
                // TODO: budgetDetailedViewModel.onTVAFilter(it)
              }
            }
          }
          if (page == AccountingPage.BALANCE) {
            items(balanceModel.balanceList) {
              DisplayBalanceItem(balanceDetailedViewModel, it, "displayItem${it.uid}")
              HorizontalDivider(Modifier.fillMaxWidth())
            }
          } else if (page == AccountingPage.BUDGET) {
            items(budgetModel.budgetList) {
              DisplayBudgetItem(budgetDetailedViewModel, it, "displayItem${it.uid}")
              HorizontalDivider(Modifier.fillMaxWidth())
            }
          }

          item { TotalItems(totalAmount) }
        }
      })
}

/**
 * A line displaying the total amount of the subcategory
 *
 * @param totalAmount: The total amount of the subcategory
 */
@Composable
fun TotalItems(totalAmount: Int) {
  ListItem(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("totalItems"),
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
 * Display the budget Item in a list
 *
 * @param budgetDetailedViewModel: The view model of the budget details
 * @param budgetItem: The budget item to display
 * @param testTag: The test tag of the item
 */
@Composable
fun DisplayBudgetItem(
    budgetDetailedViewModel: BudgetDetailedViewModel,
    budgetItem: BudgetItem,
    testTag: String
) {
  ListItem(
      headlineContent = { Text(budgetItem.nameItem) },
      trailingContent = { Text("${budgetItem.amount}") },
      supportingContent = { Text(budgetItem.description) },
      modifier =
      Modifier
        .clickable { budgetDetailedViewModel.startEditing(budgetItem) }
        .testTag(testTag))
}

/**
 * Display the budget Item in a list
 *
 * @param balanceItem: The budget item to display
 * @param testTag: The test tag of the item
 */
@Composable
fun DisplayBalanceItem(
    balanceDetailedViewModel: BalanceDetailedViewModel,
    balanceItem: BalanceItem,
    testTag: String
) {
  ListItem(
      headlineContent = { Text(balanceItem.nameItem) },
      trailingContent = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("${balanceItem.amount}", modifier = Modifier.padding(end = 4.dp))
        }
      },
      supportingContent = { Text(balanceItem.assignee) },
      overlineContent = { Text(balanceItem.date.toString()) },
      modifier =
      Modifier
        .clickable { balanceDetailedViewModel.startEditing(balanceItem) }
        .testTag(testTag))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayEditBalance(balanceDetailedViewModel: BalanceDetailedViewModel) {
  val balanceModel by balanceDetailedViewModel.uiState.collectAsState()
  val balance = balanceModel.editedBalanceItem!!
  var nameString by remember { mutableStateOf("") }
  var subCategoryUid by remember { mutableStateOf("") }
  var receiptUid by remember { mutableStateOf("") }
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
          Text("Edit Balance Item", fontSize = 20.sp, modifier = Modifier.padding(16.dp))
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
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp)
          ) {
            var subExtended by remember { mutableStateOf(false) }
            FilterChip(
              modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
              selected = subExtended,
              onClick = { subExtended = !subExtended },
              label = { Text(subCategoryUid) },
              trailingIcon = {
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Expand")
              })
            DropdownMenu(
              modifier = Modifier,
              expanded = subExtended,
              onDismissRequest = { subExtended = false },
              properties = PopupProperties(focusable = true)
            ) {
              balanceModel.subCategoryList.forEach { subBalance ->
                DropdownMenuItem(
                  text = { Text(subBalance.name) },
                  onClick = {
                    subCategoryUid = subBalance.uid
                    subExtended = false
                  })
              }
            }
          }
        }
        // The receipt selector
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp)
          ) {
            var receiptExtended by remember { mutableStateOf(false) }
            FilterChip(
              modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
              selected = receiptExtended,
              onClick = { receiptExtended = !receiptExtended },
              label = { Text(receiptUid) },
              trailingIcon = {
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Expand")
              })
            DropdownMenu(
              modifier = Modifier,
              expanded = receiptExtended,
              onDismissRequest = { receiptExtended = false },
              properties = PopupProperties(focusable = true)
            ) {
              balanceModel.receiptList.forEach { receipt ->
                DropdownMenuItem(
                  text = { Text(receipt.title) },
                  onClick = {
                    receiptUid = receipt.uid
                    receiptExtended = false
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
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp)
          ) {
            var tvaExtended by remember { mutableStateOf(false) }
            FilterChip(
              modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
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


/**
 * Displays the popup to edit a specific budget element
 *
 * @param budgetViewModel the viewModel of the budget details
 */
@Composable
fun DisplayEditBudget(budgetViewModel: BudgetDetailedViewModel) {
  val budgetModel by budgetViewModel.uiState.collectAsState()
  val budget = budgetModel.editedBudgetItem!!
  var nameString by remember { mutableStateOf(budget.nameItem) }
  var amountString by remember { mutableStateOf(budget.amount.toString()) }
  var tvaTypeString by remember { mutableStateOf(budget.tva.toString()) }
  var tvaString by remember { mutableStateOf(budget.tva.rate.toString()) }
  var descriptionString by remember { mutableStateOf(budget.description) }
  var yearString by remember { mutableStateOf(budget.year.toString()) }

  Dialog(onDismissRequest = { budgetViewModel.cancelEditing() }) {
    Card(
        modifier = Modifier
          .padding(16.dp)
          .testTag("editDialogBox"),
        shape = RoundedCornerShape(16.dp),
    ) {
      Column() {
        Text("Edit Budget Item", fontSize = 20.sp, modifier = Modifier.padding(16.dp))
        OutlinedTextField(
            modifier = Modifier
              .padding(8.dp)
              .testTag("editNameBox"),
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
        Box(modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp)) {
          var tvaExtended by remember { mutableStateOf(false) }
          FilterChip(
              modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
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
              properties = PopupProperties(focusable = true)) {
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
            modifier = Modifier
              .fillMaxWidth()
              .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Button(
              onClick = { budgetViewModel.cancelEditing() },
              modifier = Modifier
                .padding(15.dp)
                .testTag("editDismissButton"),
          ) {
            Text("Dismiss")
          }
          Button(
              onClick = {
                budgetViewModel.saveEditing(
                    BudgetItem(
                        budget.uid,
                        nameItem = nameString,
                        amount = amountString.toInt(),
                        tva = TVA.floatToTVA(tvaString.toFloat()),
                        description = descriptionString,
                        subcategoryUID = budget.subcategoryUID,
                        year = yearString.toInt()))
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
