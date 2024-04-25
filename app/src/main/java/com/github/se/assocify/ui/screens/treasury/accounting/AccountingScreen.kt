package com.github.se.assocify.ui.screens.treasury.accounting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.DropdownFilterChip

/** Represents the page to display in the accounting screen */
enum class AccountingPage {
  BUDGET,
  BALANCE
}

/**
 * The accounting screen displaying the budget or the balance of the association
 *
 * @param page: The page to display (either "budget" or "balance")
 * @param subCategoryList: The list of subcategories to display
 * @param navigationActions: The navigation actions
 */
@Composable
fun Accounting(
    page: AccountingPage,
    subCategoryList: List<AccountingSubCategory>,
    navigationActions: NavigationActions
) { // TODO: fetch all these list from viewmodel
  val yearList =
      listOf("2023", "2022", "2021") // TODO: start from 2021 until current year (dynamically)

  val categoryList =
      listOf(
          AccountingCategory("Global"),
          AccountingCategory("Pole"),
          AccountingCategory("Events"),
          AccountingCategory("Commission"),
          AccountingCategory("Fees"))

  val tvaList: List<String> = listOf("TTC", "HT")

  var selectedYear by remember { mutableStateOf(yearList.first()) }
  var selectedCategory by remember { mutableStateOf(categoryList.first().name) }
  var selectedTVA by remember { mutableStateOf(tvaList.first()) }

  val filteredSubCategoryList =
      if (selectedCategory == "Global") // display everything under the global category
       subCategoryList
      else subCategoryList.filter { it.category.name == selectedCategory }

    LazyColumn(modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp).testTag("AccountingScreen")) {


    items(filteredSubCategoryList) {
      DisplayLine(it, "displayLine${it.name}", page, navigationActions)
      HorizontalDivider(Modifier.fillMaxWidth())
    }

    item {
      val totalAmount = filteredSubCategoryList.sumOf { it.amount }
      TotalLine(totalAmount = totalAmount)
    }
  }
}

@Composable
fun FilterBar(){
    val yearList =
        listOf("2023", "2022", "2021") // TODO: start from 2021 until current year (dynamically)

    val categoryList =
        listOf(
            AccountingCategory("Global"),
            AccountingCategory("Pole"),
            AccountingCategory("Events"),
            AccountingCategory("Commission"),
            AccountingCategory("Fees"))

    val tvaList: List<String> = listOf("TTC", "HT")

    var selectedYear by remember { mutableStateOf(yearList.first()) }
    var selectedCategory by remember { mutableStateOf(categoryList.first().name) }
    var selectedTVA by remember { mutableStateOf(tvaList.first()) }

    Row(Modifier.padding(horizontal = 20.dp).testTag("filterRow").horizontalScroll(rememberScrollState())) {
        DropdownFilterChip(selectedYear, yearList, "yearFilterChip") { selectedYear = it }
        DropdownFilterChip(selectedCategory, categoryList.map { it.name }, "categoryFilterChip") {
            selectedCategory = it
        }
        // TODO: change amount given TVA
        DropdownFilterChip(selectedTVA, tvaList, "tvaListTag") { selectedTVA = it }
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
            text = "$totalAmount",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
      },
      colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer))
}

/**
 * A line displaying a budget category and its amount
 *
 * @param category: The budget category
 * @param testTag: The test tag of the line
 * @param page: The page to which the line belongs
 * @param navigationActions: The navigation actions to navigate to the detailed screen
 */
@Composable
fun DisplayLine(
    category: AccountingSubCategory,
    testTag: String,
    page: AccountingPage,
    navigationActions: NavigationActions
) {
  ListItem(
      headlineContent = { Text(category.name) },
      trailingContent = { Text("${category.amount}") },
      modifier =
          Modifier.clickable {
                if (page == AccountingPage.BUDGET) {
                  navigationActions.navigateTo(Destination.BudgetDetailed(category.uid))
                } else {
                  navigationActions.navigateTo(Destination.BalanceDetailed(category.uid))
                }
              }
              .testTag(testTag),
  )
}
