package com.github.se.assocify.ui.screens.treasury.accounting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.DropdownFilterChip
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceDetailedViewModel
import com.github.se.assocify.ui.screens.treasury.accounting.balance.DisplayEditBalance
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetDetailedViewModel
import com.github.se.assocify.ui.screens.treasury.accounting.budget.DisplayCreateBudget
import com.github.se.assocify.ui.screens.treasury.accounting.budget.DisplayEditBudget
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
                        AccountingPage.BALANCE -> balanceState.subCategory!!.name
                        AccountingPage.BUDGET -> budgetState.subCategory!!.name
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
                          if (balanceState.subCategory != null) {
                            balanceDetailedViewModel.startSubCategoryEditingInBalance()
                          }
                      AccountingPage.BUDGET ->
                          if (budgetState.subCategory != null) {
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
      modifier = Modifier.fillMaxWidth().testTag("AccountingDetailedScreen"),
      floatingActionButton = {
        FloatingActionButton(
            modifier = Modifier.testTag("createNewItem"),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            onClick = {
              when (page) {
                AccountingPage.BUDGET -> budgetDetailedViewModel.startCreating()
                AccountingPage.BALANCE -> /*TODO: implement the balance popup*/ TODO()
              }
            }) {
              Icon(Icons.Outlined.Add, "Create")
            }
      },
      snackbarHost = {
        SnackbarHost(
            hostState =
                when (page) {
                  AccountingPage.BALANCE -> balanceState.snackbarState
                  AccountingPage.BUDGET -> budgetState.snackbarState
                },
            snackbar = { snackbarData -> Snackbar(snackbarData = snackbarData) })
      }) { innerPadding ->
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
        } else if (balanceState.editing && page == AccountingPage.BALANCE) {
          DisplayEditBalance(balanceDetailedViewModel)
        } else if (budgetState.creating && page == AccountingPage.BUDGET) {
          DisplayCreateBudget(budgetViewModel = budgetDetailedViewModel)
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(innerPadding),
        ) {
          item {
            Row(Modifier.testTag("filterRowDetailed").horizontalScroll(rememberScrollState())) {
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
                DisplayBalanceItem(balanceDetailedViewModel, it, "displayItem${it.uid}")
                HorizontalDivider(Modifier.fillMaxWidth())
              }

              // display total amount
              if (balanceState.balanceList.isNotEmpty()) {
                item { TotalItems(totalAmount) }
              } else {
                item {
                  Text(
                      "No items for the ${balanceState.subCategory!!.name} sheet with these filters")
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
                      "No items for the ${balanceState.subCategory!!.name} sheet with these filters",
                  )
                }
              }
            }
          }
          item { Spacer(modifier = Modifier.height(80.dp)) }
        }
      }
}

/**
 * A line displaying the total amount of the subcategory
 *
 * @param totalAmount: The total amount of the subcategory
 */
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
      trailingContent = { Text(PriceUtil.fromCents(budgetItem.amount), style = MaterialTheme.typography.bodyMedium) },
      supportingContent = {
        if (budgetItem.description.isEmpty()) Text("-") else Text(budgetItem.description)
      },
      modifier =
          Modifier.clickable { budgetDetailedViewModel.startEditing(budgetItem) }.testTag(testTag))
}

/**
 * Display the budget Item in a list
 *
 * @param balanceDetailedViewModel: The view model of the balance details
 * @param balanceItem: The budget item to display
 * @param testTag: The test tag of the item
 */
@Composable
fun DisplayBalanceItem(
    balanceDetailedViewModel: BalanceDetailedViewModel,
    balanceItem: BalanceItem,
    testTag: String
) {
  val balanceState by balanceDetailedViewModel.uiState.collectAsState()
  ListItem(
      headlineContent = { Text(balanceItem.nameItem) },
      trailingContent = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
              text =
                  if (balanceState.filterActive)
                      PriceUtil.fromCents(
                          balanceItem.amount +
                              (balanceItem.amount * balanceItem.tva.rate / 100f).toInt())
                  else PriceUtil.fromCents(balanceItem.amount),
              modifier = Modifier.padding(end = 4.dp),
              style = MaterialTheme.typography.bodyMedium)
          /*Icon(
          balanceItem.receipt!!.status.getIcon(),
          contentDescription = "Create") // TODO: add logo depending on the phase*/
        }
      },
      supportingContent = { Text(balanceItem.assignee) },
      overlineContent = { Text(balanceItem.date.toString()) },
      modifier =
          Modifier.clickable { balanceDetailedViewModel.startEditing(balanceItem) }
              .testTag(testTag))
}
