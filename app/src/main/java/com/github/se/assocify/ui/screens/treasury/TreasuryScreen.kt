package com.github.se.assocify.ui.screens.treasury

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.InnerTabRow
import com.github.se.assocify.ui.composables.MainNavigationBar
import com.github.se.assocify.ui.composables.MainTopBar
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingFilterBar
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingViewModel
import com.github.se.assocify.ui.screens.treasury.accounting.accountingComposables.AddSubcategoryDialog
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceScreen
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetScreen
import com.github.se.assocify.ui.screens.treasury.receiptstab.ReceiptListScreen
import com.github.se.assocify.ui.screens.treasury.receiptstab.ReceiptListViewModel

/**
 * Treasury screen that displays the receipts, budget, and balance tabs of the association.
 *
 * @param navActions Navigation actions to navigate to other screens.
 * @param receiptListViewModel The view model for the receipt list tab.
 * @param treasuryViewModel The view model for the treasury screen.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TreasuryScreen(
    navActions: NavigationActions,
    accountingViewModel: AccountingViewModel,
    receiptListViewModel: ReceiptListViewModel,
    treasuryViewModel: TreasuryViewModel
) {

  val treasuryState by treasuryViewModel.uiState.collectAsState()
    val receiptState by receiptListViewModel.uiState.collectAsState()
  val pagerState = rememberPagerState(pageCount = { TreasuryPageIndex.entries.size })

  Scaffold(
      modifier = Modifier.testTag("treasuryScreen"),
      topBar = {
        MainTopBar(
            title = "Treasury",
            optInSearchBar = true,
            query = treasuryState.searchQuery,
            onQueryChange = { treasuryViewModel.setSearchQuery(it) },
            onSearch = { treasuryViewModel.onSearch(pagerState.currentPage) },
            page = pagerState.currentPage,
            searchTitle =
                when (pagerState.currentPage) {
                  TreasuryPageIndex.Receipts.ordinal -> "receipts"
                  TreasuryPageIndex.Budget.ordinal -> "budget"
                  TreasuryPageIndex.Balance.ordinal -> "balance"
                  else -> ""
                })
      },
      bottomBar = {
        MainNavigationBar(
            onTabSelect = { navActions.navigateToMainTab(it) },
            tabList = MAIN_TABS_LIST,
            selectedTab = Destination.Treasury)
      },
      floatingActionButton = {
        FloatingActionButton(
            modifier = Modifier.testTag("createReceipt"),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            onClick = {
              when (pagerState.currentPage) {
                TreasuryPageIndex.Receipts.ordinal -> navActions.navigateTo(Destination.NewReceipt)
                TreasuryPageIndex.Budget.ordinal -> accountingViewModel.showNewSubcategoryDialog()
                TreasuryPageIndex.Balance.ordinal -> accountingViewModel.showNewSubcategoryDialog()
              }
            }) {
              Icon(Icons.Outlined.Add, "Create")
            }
      },
      contentWindowInsets = WindowInsets(20.dp, 0.dp, 20.dp, 0.dp)) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
          if (receiptState.userCurrentRole.type != RoleType.TREASURY &&
              receiptState.userCurrentRole.type != RoleType.PRESIDENCY) {
            ReceiptListScreen(viewModel = receiptListViewModel)
          } else {
            InnerTabRow(
                tabList = TreasuryPageIndex.entries,
                pagerState = pagerState,
                switchTab = { tab -> treasuryViewModel.switchTab(tab) })

            when (pagerState.currentPage) {
              TreasuryPageIndex.Receipts.ordinal -> {}
              TreasuryPageIndex.Budget.ordinal -> {
                AccountingFilterBar(accountingViewModel)
                AddSubcategoryDialog(accountingViewModel)
              }
              TreasuryPageIndex.Balance.ordinal -> {
                AccountingFilterBar(accountingViewModel)
                AddSubcategoryDialog(accountingViewModel)
              }
            }
          }

          // Pages content
          HorizontalPager(state = pagerState, userScrollEnabled = true) { page ->
            when (page) {
              TreasuryPageIndex.Receipts.ordinal -> ReceiptListScreen(receiptListViewModel)
              TreasuryPageIndex.Budget.ordinal -> BudgetScreen(navActions, accountingViewModel)
              TreasuryPageIndex.Balance.ordinal -> BalanceScreen(navActions, accountingViewModel)
            }
          }
        }
      }
}
