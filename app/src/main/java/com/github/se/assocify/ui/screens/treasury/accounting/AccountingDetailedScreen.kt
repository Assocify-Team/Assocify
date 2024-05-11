package com.github.se.assocify.ui.screens.treasury.accounting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.MaybeRemotePhoto
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.model.entities.TVA
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.DropdownFilterChip
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
    budgetDetailedViewModel: BudgetDetailedViewModel
) {
  val budgetModel by budgetDetailedViewModel.uiState.collectAsState()
  val subCategory =
      AccountingSubCategory(subCategoryUid, subCategoryUid, AccountingCategory("Pole"), 1205)

  // TODO: fetch from balance detailed view model
  val receipt =
      Receipt(
          "1",
          "receipt1",
          "url",
          LocalDate.now(),
          100,
          Status.Pending,
          MaybeRemotePhoto.Remote("path"))
  val balanceItems =
      listOf(
          BalanceItem(
              "1",
              "pair of scissors",
              "",
              "00000000-0000-0000-0000-000000000000",
              5,
              TVA.TVA_8,
              "scissors for paper cutting",
              LocalDate.of(2024, 4, 14),
              "François Théron",
              Status.Pending),
          BalanceItem(
              "2",
              "sweaters",
              "",
              "00000000-0000-0000-0000-000000000000",
              1000,
              TVA.TVA_8,
              "order for 1000 sweaters",
              LocalDate.of(2024, 3, 11),
              "Rayan Boucheny",
              Status.Archived),
          BalanceItem(
              "3",
              "chairs",
              "",
              "00000000-0000-0000-0000-000000000000",
              200,
              TVA.TVA_8,
              "order for 200 chairs",
              LocalDate.of(2024, 1, 14),
              "Sidonie Bouthors",
              Status.Reimbursed))

  val yearList = listOf("2023", "2022", "2021")
  val statusList: List<String> = listOf("All Status") + Status.entries.map { it.name }
  val tvaList: List<String> = listOf("TTC", "HT")

  var selectedStatus by remember { mutableStateOf(statusList.first()) }

  val filteredBalanceList =
      if (selectedStatus == statusList.first()) // display everything under the status category
       balanceItems
      else balanceItems.filter { it.status.toString() == selectedStatus }

  val totalAmount =
      when (page) {
        AccountingPage.BUDGET -> budgetModel.budgetList.sumOf { it.amount }
        AccountingPage.BALANCE -> filteredBalanceList.sumOf { it.amount }
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
      modifier = Modifier.fillMaxWidth().testTag("AccountingDetailedScreen"),
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
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(innerPadding),
        ) {
          item {
            Row(Modifier.testTag("filterRowDetailed").horizontalScroll(rememberScrollState())) {
              DropdownFilterChip(yearList.first(), yearList, "yearListTag") {
                budgetDetailedViewModel.onYearFilter(it.toInt())
              }
              if (page == AccountingPage.BALANCE) {
                DropdownFilterChip(statusList.first(), statusList, "statusListTag") {
                  selectedStatus = it
                }
              }

              // TODO: change amount given TVA
              DropdownFilterChip(tvaList.first(), tvaList, "tvaListTag") {
                // TODO: budgetDetailedViewModel.onTVAFilter(it)
              }
            }
          }
          if (page == AccountingPage.BALANCE) {
            items(filteredBalanceList) {
              DisplayBalanceItem(it, "displayItem${it.uid}")
              HorizontalDivider(Modifier.fillMaxWidth())
            }
          } else if (page == AccountingPage.BUDGET) {
            items(budgetModel.budgetList) {
              DisplayBudgetItem(it, "displayItem${it.uid}")
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

/**
 * Display the budget Item in a list
 *
 * @param budgetItem: The budget item to display
 * @param testTag: The test tag of the item
 */
@Composable
fun DisplayBudgetItem(budgetItem: BudgetItem, testTag: String) {
  ListItem(
      headlineContent = { Text(budgetItem.nameItem) },
      trailingContent = { Text("${budgetItem.amount}") },
      supportingContent = { Text(budgetItem.description) },
      modifier = Modifier.clickable { /*TODO: edit and view details*/}.testTag(testTag))
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
          Text("${balanceItem.amount}", modifier = Modifier.padding(end = 4.dp))
          /* TODO update according to new db changes
          Icon(

              balanceItem.receipt!!.status.getIcon(),
              contentDescription = "Create") // TODO: add logo depending on the phase

             */
        }
      },
      supportingContent = { Text(balanceItem.assignee) },
      overlineContent = { Text(balanceItem.date.toString()) },
      modifier = Modifier.clickable { /*TODO: edit and view details*/}.testTag(testTag))
}
