package com.github.se.assocify.ui.screens.treasury

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.MainNavigationBar
import com.github.se.assocify.ui.composables.MainTopBar
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingFilterBar
import com.github.se.assocify.ui.screens.treasury.accounting.balance.BalanceScreen
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetScreen
import com.github.se.assocify.ui.screens.treasury.receiptstab.ReceiptListScreen
import com.github.se.assocify.ui.screens.treasury.receiptstab.ReceiptListViewModel
import kotlinx.coroutines.launch

// Index of each tag for navigation
enum class TreasuryPageIndex {
  Receipts,
  Budget,
  Balance;

  companion object {
    val NUMBER_OF_PAGES: Int = entries.size
  }
}

/** Treasury Screen composable */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TreasuryScreen(
    navActions: NavigationActions,
    receiptListViewModel: ReceiptListViewModel = ReceiptListViewModel(navActions),
    treasuryViewModel: TreasuryViewModel =
        TreasuryViewModel(navActions = navActions, receiptListViewModel = receiptListViewModel)
) {

  val pagerState = rememberPagerState(pageCount = { TreasuryPageIndex.NUMBER_OF_PAGES })

  Scaffold(
      modifier = Modifier.testTag("treasuryScreen"),
      topBar = { TreasuryTopBar(treasuryViewModel, pagerState.currentPage) },
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
                TreasuryPageIndex.Budget.ordinal -> {}
                TreasuryPageIndex.Balance.ordinal -> {}
              }
            }) {
              Icon(Icons.Outlined.Add, "Create")
            }
      },
      contentWindowInsets = WindowInsets(20.dp, 0.dp, 20.dp, 0.dp)) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
          val coroutineRoute = rememberCoroutineScope()

          // Tabs
          TabRow(
              modifier = Modifier.height(48.dp).testTag("tabRows"),
              selectedTabIndex = pagerState.currentPage,
              containerColor = MaterialTheme.colorScheme.background,
              contentColor = MaterialTheme.colorScheme.primary,
              divider = {},
              indicator = { tabPositions ->
                Box(
                    modifier =
                        Modifier.fillMaxSize()
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .size(width = 10.dp, height = 3.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)))
              }) {
                TreasuryPageIndex.entries.forEach {
                  TreasuryTab(
                      selected = pagerState.currentPage == it.ordinal,
                      onClick = {
                        coroutineRoute.launch { pagerState.animateScrollToPage(it.ordinal) }
                      },
                      text = it.name,
                      modifier = Modifier.testTag(it.name + "Tab"))
                }
              }

          when (pagerState.currentPage) {
            TreasuryPageIndex.Receipts.ordinal -> {}
            TreasuryPageIndex.Budget.ordinal -> AccountingFilterBar()
            TreasuryPageIndex.Balance.ordinal -> AccountingFilterBar()
          }

          // Pages content
          HorizontalPager(state = pagerState, userScrollEnabled = true) { page ->
            when (page) {
              TreasuryPageIndex.Receipts.ordinal -> ReceiptListScreen(receiptListViewModel)
              TreasuryPageIndex.Budget.ordinal -> BudgetScreen(navActions)
              TreasuryPageIndex.Balance.ordinal -> BalanceScreen(navActions)
            }
          }
        }
      }
}

/**
 * ------------------------------------------------- * Elements *
 * ------------------------------------------------- *
 */

/** Top tabs component */
@Composable
fun TreasuryTab(
    selected: Boolean,
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
  Tab(
      selected = selected,
      onClick = onClick,
      text = {
        Text(
            text = text,
            color =
                if (selected) {
                  MaterialTheme.colorScheme.primary
                } else {
                  MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                })
      },
      modifier = modifier)
}

/** Main top bar with search and account icon */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreasuryTopBar(viewModel: TreasuryViewModel, currentPage: Int) {

  val treasuryState by viewModel.uiState.collectAsState()
  // Search bar state
  MainTopBar(
      title = "Treasury",
      optInSearchBar = true,
      query = treasuryState.searchQuery,
      onQueryChange = { viewModel.setSearchQuery(it) },
      onSearch = { viewModel.onSearch(currentPage) },
      page = currentPage)
}
