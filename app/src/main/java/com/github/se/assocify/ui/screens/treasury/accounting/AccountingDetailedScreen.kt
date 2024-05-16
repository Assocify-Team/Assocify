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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.model.entities.TVA
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.DropdownFilterChip
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceDetailedViewModel
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceItemState
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetDetailedViewModel
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetItemState
import com.github.se.assocify.ui.screens.treasury.accounting.budget.DisplayCreateBudget
import com.github.se.assocify.ui.screens.treasury.accounting.budget.DisplayEditBudget
import com.github.se.assocify.ui.util.DateUtil
import com.github.se.assocify.ui.util.PriceUtil

/**
 * The detailed screen of a subcategory in the accounting screen
 *
 * @param page: The page to display (either "budget" or "balance")
 * @param navigationActions: The navigation actions
 * @param budgetDetailedViewModel: The view model for the budget detailed screen
 * @param balanceDetailedViewModel: The view model for the balance detailed screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountingDetailedScreen(
    page: AccountingPage,
    navigationActions: NavigationActions,
    budgetDetailedViewModel: BudgetDetailedViewModel,
    balanceDetailedViewModel: BalanceDetailedViewModel
) {

  val budgetState by budgetDetailedViewModel.uiState.collectAsState()
  val balanceState by balanceDetailedViewModel.uiState.collectAsState()

  val yearList = DateUtil.getYearList()
  val statusList: List<String> = listOf("All Status") + Status.entries.map { it.name }
  val tvaList: List<String> = listOf("HT", "TTC")

  val totalAmount =
      when (page) {
        AccountingPage.BUDGET ->
            if (!budgetState.filterActive) budgetState.budgetList.sumOf { it.amount }
            else
                budgetState.budgetList.sumOf {
                  it.amount + (it.amount * it.tva.rate / 100f).toInt()
                }
        AccountingPage.BALANCE ->
            if (!balanceState.filterActive) balanceState.balanceList.sumOf { it.amount }
            else
                balanceState.balanceList.sumOf {
                  it.amount + (it.amount * it.tva.rate / 100f).toInt()
                }
      }

  Scaffold(
      topBar = {
        CenterAlignedTopAppBar(
            title = {
              Text(
                  text =
                      when (page) {
                        AccountingPage.BALANCE -> balanceState.subCategory.name
                        AccountingPage.BUDGET -> budgetState.subCategory.name
                      },
                  style = MaterialTheme.typography.titleLarge)
            },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.back() },
                  modifier = Modifier.testTag("backButton")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                  }
            },
            actions = {
              IconButton(
                  onClick = {
                    // Sets the editing state to true
                    when (page) {
                      AccountingPage.BALANCE ->
                          if (balanceState.subCategory.name !=
                              "") { // TODO: modify this with loading
                            balanceDetailedViewModel.startSubCategoryEditingInBalance()
                          }
                      AccountingPage.BUDGET ->
                          if (budgetState.subCategory.name !=
                              "") { // TODO: modify this with loading
                            budgetDetailedViewModel.startSubCategoryEditingInBudget()
                          }
                    }
                  },
                  modifier = Modifier.testTag("editSubCat")) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit")
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
            onClick = {
              when(page){
                AccountingPage.BUDGET -> budgetDetailedViewModel.startCreating()
                AccountingPage.BALANCE -> /*TODO: implement the balance popup*/ TODO()
              }
            }) {
              Icon(Icons.Outlined.Add, "Create")
            }
      },
      content = { innerPadding ->
        // Call the various editing popups
        if (budgetState.editing && page == AccountingPage.BUDGET) {
          DisplayEditBudget(budgetDetailedViewModel)
        } else if ((budgetState.subCatEditing && page == AccountingPage.BUDGET) ||
            (balanceState.subCatEditing && page == AccountingPage.BALANCE)) {
          DisplayEditSubCategory(
              page,
              budgetDetailedViewModel,
              balanceDetailedViewModel,
              navigationActions,
              balanceState,
              budgetState)
        } else if(budgetState.creating && page == AccountingPage.BUDGET){
          DisplayCreateBudget(budgetViewModel = budgetDetailedViewModel)
        }

        LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
          // Display the filter chips
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
                balanceDetailedViewModel.modifyTVAFilter(it == "TTC")
                budgetDetailedViewModel.modifyTVAFilter(it == "TTC")
              }
            }
          }

          // Display the items
          when (page) {
            AccountingPage.BALANCE -> {
              items(balanceState.balanceList) {
                DisplayBalanceItem(it, "displayItem${it.uid}")
                HorizontalDivider(Modifier.fillMaxWidth())
              }

              // display total amount
              if (balanceState.balanceList.isNotEmpty()) {
                item { TotalItems(totalAmount) }
              } else {
                item {
                  Text("No items for the ${balanceState.subCategory.name} sheet with these filters")
                }
              }
            }
            AccountingPage.BUDGET -> {
              items(budgetState.budgetList) {
                DisplayBudgetItem(budgetDetailedViewModel, it, "displayItem${it.uid}")
                HorizontalDivider(Modifier.fillMaxWidth())
              }

              // display total amount
              if (budgetState.budgetList.isNotEmpty()) {
                item { TotalItems(totalAmount) }
              } else {
                item {
                  Text(
                      "No items for the ${balanceState.subCategory.name} sheet with these filters",
                  )
                }
              }
            }
          }
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
            text = PriceUtil.fromCents(totalAmount),
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
      trailingContent = { Text(PriceUtil.fromCents(budgetItem.amount)) },
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
fun DisplayBalanceItem(balanceItem: BalanceItem, testTag: String) {
  ListItem(
      headlineContent = { Text(balanceItem.nameItem) },
      trailingContent = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
              PriceUtil.fromCents(balanceItem.amount),
              modifier = Modifier.padding(end = 4.dp),
              style = MaterialTheme.typography.bodyMedium)
          /*Icon(
          balanceItem.receipt!!.status.getIcon(),
          contentDescription = "Create") // TODO: add logo depending on the phase*/
        }
      },
      supportingContent = { Text(balanceItem.assignee) },
      overlineContent = { Text(balanceItem.date.toString()) },
      modifier = Modifier
        .clickable {}
        .testTag(testTag))
}

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
            Modifier
              .padding(vertical = 16.dp, horizontal = 8.dp)
              .testTag("editSubCategoryDialog"),
            shape = RoundedCornerShape(16.dp),
        ) {
          Column(
              modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
              horizontalAlignment = Alignment.CenterHorizontally) {
                // Title
                Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Text("Edit ${subCategory.name}", style = MaterialTheme.typography.titleLarge)
                  Icon(
                      Icons.Default.Close,
                      contentDescription = "Close dialog",
                      modifier =
                      Modifier
                        .clickable {
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
                    modifier = Modifier
                      .testTag("editSubCategoryNameBox")
                      .padding(8.dp),
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    supportingText = {})
                OutlinedTextField(
                    modifier = Modifier
                      .testTag("editSubCategoryYearBox")
                      .padding(8.dp),
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Year") },
                    supportingText = {})

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                      .testTag("categoryDropdown")
                      .padding(8.dp)) {
                      OutlinedTextField(
                          value = selectedCategory,
                          onValueChange = {},
                          label = { Text("Tag") },
                          trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                          },
                          readOnly = true,
                          colors = ExposedDropdownMenuDefaults.textFieldColors(),
                          modifier = Modifier
                            .menuAnchor()
                            .clickable { expanded = !expanded })
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
              modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
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
