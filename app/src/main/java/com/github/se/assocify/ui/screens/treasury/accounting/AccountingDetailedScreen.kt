package com.github.se.assocify.ui.screens.treasury.accounting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
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
import java.time.LocalDate

/**
 * The detailed screen of a subcategory in the accounting screen
 *
 * @param page: The page to display (either "budget" or "balance")
 * @param subCategoryUid: The unique identifier of the subcategory
 * @param navigationActions: The navigation actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountingDetailedScreen(
    page: AccountingPage,
    subCategoryUid: String,
    navigationActions: NavigationActions
) {
  // TODO: get subcategory from db
  val subCategory =
      AccountingSubCategory(subCategoryUid, "Logistics Pole", AccountingCategory("Pole"), 1205)
  // TODO: fetch from db
  val receipt =
      Receipt(
          "1",
          "receipt1",
          "url",
          LocalDate.now(),
          100,
          true,
          Status.Unapproved,
          MaybeRemotePhoto.Remote("path"))
  val budgetItems =
      listOf(
          BudgetItem(
              "1", "pair of scissors", 5, TVA.TVA_8, "scissors for paper cutting", subCategory),
          BudgetItem("2", "sweaters", 1000, TVA.TVA_8, "order for 1000 sweaters", subCategory),
          BudgetItem("3", "chairs", 200, TVA.TVA_8, "order for 200 chairs", subCategory))
  val balanceItems =
      listOf(
          BalanceItem(
              "1",
              "pair of scissors",
              5,
              TVA.TVA_8,
              "scissors for paper cutting",
              subCategory,
              LocalDate.of(2024, 4, 14),
              receipt,
              "François Théron",
              Status.Unapproved),
          BalanceItem(
              "2",
              "sweaters",
              1000,
              TVA.TVA_8,
              "order for 1000 sweaters",
              subCategory,
              LocalDate.of(2024, 3, 11),
              receipt,
              "Rayan Boucheny",
              Status.Archived),
          BalanceItem(
              "3",
              "chairs",
              200,
              TVA.TVA_8,
              "order for 200 chairs",
              subCategory,
              LocalDate.of(2024, 1, 14),
              receipt,
              "Sidonie Bouthors",
              Status.PaidBack))

  val yearList = listOf("2023", "2022", "2021")
  val statusList: List<String> =
      listOf("Status", *enumValues<Status>().map { it.name }.toTypedArray())
  val tvaList: List<String> = listOf("TTC", "HT")

  var selectedYear by remember { mutableStateOf(yearList.first()) }
  var selectedStatus by remember { mutableStateOf(statusList.first()) }
  var selectedTVA by remember { mutableStateOf(tvaList.first()) }
  val filteredBalanceList =
      if (selectedStatus == "Status") // display everything under the status category
       balanceItems
      else balanceItems.filter { it.status.toString() == selectedStatus }

  val totalAmount =
      when (page) {
        AccountingPage.BUDGET -> budgetItems.sumOf { it.amount }
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
            Row(Modifier.testTag("filterRowDetailed")) {
              DropdownFilterChip(yearList.first(), yearList, "yearListTag") { selectedYear = it }
              if (page == AccountingPage.BALANCE) {
                DropdownFilterChip(statusList.first(), statusList, "statusListTag") {
                  selectedStatus = it
                }
              }

              // TODO: change amount given TVA
              DropdownFilterChip(selectedTVA, tvaList, "tvaListTag") { selectedTVA = it }
            }
          }
          if (page == AccountingPage.BALANCE) {
            items(filteredBalanceList) {
              DisplayBalanceItem(it, "displayItem${it.uid}")
              HorizontalDivider(Modifier.fillMaxWidth())
            }
          } else if (page == AccountingPage.BUDGET) {
            items(budgetItems) {
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
 * @param page: The page to which the total amount belongs
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
          Icon(
              Icons.AutoMirrored.Filled.Help,
              contentDescription = "Create") // TODO: add logo depending on the phase
        }
      },
      supportingContent = { Text(balanceItem.assignee) },
      overlineContent = { Text(balanceItem.date.toString()) },
      modifier = Modifier.clickable { /*TODO: edit and view details*/}.testTag(testTag))
}