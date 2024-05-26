package com.github.se.assocify.ui.screens.treasury.accounting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.CenteredCircularIndicator
import com.github.se.assocify.ui.composables.DropdownFilterChip
import com.github.se.assocify.ui.composables.ErrorMessage
import com.github.se.assocify.ui.util.DateUtil
import com.github.se.assocify.ui.util.PriceUtil

/** Represents the page to display in the accounting screen */
enum class AccountingPage {
  BUDGET,
  BALANCE
}

/**
 * The accounting screen displaying the budget or the balance of the association
 *
 * @param page: The page to display (either "budget" or "balance")
 * @param navigationActions: The navigation actions
 * @param accountingViewModel: The view model for the budget screen
 */
@Composable
fun AccountingScreen(
    page: AccountingPage,
    navigationActions: NavigationActions,
    accountingViewModel: AccountingViewModel
) {
  val accountingState by accountingViewModel.uiState.collectAsState()
  val subCategoryList = accountingState.subCategoryList

  if (accountingState.loading) {
    CenteredCircularIndicator()
    return
  }

  if (accountingState.error != null) {
    ErrorMessage(errorMessage = accountingState.error) { accountingViewModel.loadAccounting() }
    return
  }

  LazyColumn(
      modifier = Modifier.fillMaxWidth().testTag("AccountingScreen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
  ) {
    // display the subcategory if list is not empty
    if (subCategoryList.isNotEmpty()) {
      items(subCategoryList) {
        DisplayLine(it, "displayLine${it.name}", page, navigationActions, accountingState)
        HorizontalDivider(Modifier.fillMaxWidth())
      }
      item {
        val totalAmount =
            when (page) {
              AccountingPage.BUDGET -> {
                if (accountingState.tvaFilterActive)
                    accountingState.amountBudgetTTC
                        .filter { it.key in subCategoryList.map { it.uid } }
                        .values
                        .sum()
                else
                    accountingState.amountBudgetHT
                        .filter { it.key in subCategoryList.map { it.uid } }
                        .values
                        .sum()
              }
              AccountingPage.BALANCE -> {
                if (accountingState.tvaFilterActive)
                    accountingState.amountBalanceTTC
                        .filter { it.key in subCategoryList.map { it.uid } }
                        .values
                        .sum()
                else
                    accountingState.amountBalanceHT
                        .filter { it.key in subCategoryList.map { it.uid } }
                        .values
                        .sum()
              }
            }
        TotalLine(totalAmount = totalAmount)
      }
    } else {
      item {
        Text(
            text = "No data available with these tags",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        )
      }
    }

    item { Spacer(modifier = Modifier.height(80.dp)) }
  }
}

/**
 * The filter bar of the accounting screen
 *
 * @param accountingViewModel: The view model for the budget screen
 */
@Composable
fun AccountingFilterBar(accountingViewModel: AccountingViewModel) {
  val model by accountingViewModel.uiState.collectAsState()

  // filter bar lists
  val yearList = DateUtil.getYearList().reversed()
  val tvaList: List<String> = listOf("HT", "TTC")
  val categoryList = listOf("Global") + model.categoryList.map { it.name }
  val category = if (model.selectedCategory != null) model.selectedCategory!!.name else "Global"

  // Row of dropdown filters
  Row(Modifier.testTag("filterRow").horizontalScroll(rememberScrollState())) {
    DropdownFilterChip(model.yearFilter.toString(), yearList, "yearFilterChip") {
      accountingViewModel.onYearFilter(it.toInt())
    }

    DropdownFilterChip(category, categoryList, "categoryFilterChip") {
      accountingViewModel.onSelectedCategory(it)
    }

    DropdownFilterChip(tvaList.first(), tvaList, "tvaListTag") {
      accountingViewModel.activeTVA(it == "TTC")
    }
  }
}

/**
 * A line displaying the total amount of the budget
 *
 * @param totalAmount: The total amount of the budget
 */
@Composable
fun TotalLine(totalAmount: Int) {
  ListItem(
      modifier = Modifier.fillMaxWidth().testTag("totalLine"),
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
 * A line displaying a budget category and its amount
 *
 * @param subCategory: The budget category
 * @param testTag: The test tag of the line
 * @param page: The page to which the line belongs
 * @param navigationActions: The navigation actions to navigate to the detailed screen
 * @param accountingState: The state of the accounting screen
 */
@Composable
fun DisplayLine(
    subCategory: AccountingSubCategory,
    testTag: String,
    page: AccountingPage,
    navigationActions: NavigationActions,
    accountingState: AccountingState
) {
  val amount =
      when (page) {
        AccountingPage.BUDGET ->
            if (accountingState.tvaFilterActive)
                accountingState.amountBudgetTTC[subCategory.uid] ?: 0
            else accountingState.amountBudgetHT[subCategory.uid] ?: 0
        AccountingPage.BALANCE ->
            if (accountingState.tvaFilterActive)
                accountingState.amountBalanceTTC[subCategory.uid] ?: 0
            else accountingState.amountBalanceHT[subCategory.uid] ?: 0
      }

  ListItem(
      headlineContent = { Text(subCategory.name) },
      trailingContent = {
        Text(PriceUtil.fromCents(amount), style = MaterialTheme.typography.bodyMedium)
      },
      modifier =
          Modifier.clickable {
                when (page) {
                  AccountingPage.BUDGET ->
                      navigationActions.navigateTo(Destination.BudgetDetailed(subCategory.uid))
                  AccountingPage.BALANCE ->
                      navigationActions.navigateTo(Destination.BalanceDetailed(subCategory.uid))
                }
              }
              .testTag(testTag))
}
