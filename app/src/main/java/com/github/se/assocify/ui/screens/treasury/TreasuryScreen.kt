package com.github.se.assocify.ui.screens.treasury

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.MainNavigationBar
import com.github.se.assocify.ui.screens.treasury.accounting.FilterBar
import com.github.se.assocify.ui.screens.treasury.accounting.balance.Balance
import com.github.se.assocify.ui.screens.treasury.accounting.budget.Budget
import com.github.se.assocify.ui.screens.treasury.receiptstab.ReceiptListScreen
import com.github.se.assocify.ui.screens.treasury.receiptstab.ReceiptListViewModel
import kotlinx.coroutines.launch

// Index of each tag for navigation
enum class TreasuryPageIndex {
  RECEIPT,
  BUDGET,
  BALANCE;

  companion object {
    val NUMBER_OF_PAGES: Int = entries.size
  }
}

/** Treasury Screen composable */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TreasuryScreen(
    navActions: NavigationActions,
    receiptListViewModel: ReceiptListViewModel = ReceiptListViewModel(navActions)
) {

  val pagerState = rememberPagerState(pageCount = { TreasuryPageIndex.NUMBER_OF_PAGES })

  Scaffold(
      modifier = Modifier.testTag("treasuryScreen"),
      topBar = { TreasuryTopBar({}, {}, receiptListViewModel) },
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
              if (pagerState.currentPage == TreasuryPageIndex.RECEIPT.ordinal) {
                navActions.navigateTo(Destination.NewReceipt)
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
                TreasuryTab(
                    selected = pagerState.currentPage == TreasuryPageIndex.RECEIPT.ordinal,
                    onClick = {
                      coroutineRoute.launch {
                        pagerState.animateScrollToPage(TreasuryPageIndex.RECEIPT.ordinal)
                      }
                    },
                    text = "Receipts",
                    modifier = Modifier.testTag("myReceiptsTab"))
                TreasuryTab(
                    selected = pagerState.currentPage == TreasuryPageIndex.BUDGET.ordinal,
                    onClick = {
                      coroutineRoute.launch {
                        pagerState.animateScrollToPage(TreasuryPageIndex.BUDGET.ordinal)
                      }
                    },
                    text = "Budget",
                    modifier = Modifier.testTag("budgetTab"))
                TreasuryTab(
                    selected = pagerState.currentPage == TreasuryPageIndex.BALANCE.ordinal,
                    onClick = {
                      coroutineRoute.launch {
                        pagerState.animateScrollToPage(TreasuryPageIndex.BALANCE.ordinal)
                      }
                    },
                    text = "Balance",
                    modifier = Modifier.testTag("balanceTab"))
              }

          when (pagerState.currentPage) {
            TreasuryPageIndex.RECEIPT.ordinal -> null
            TreasuryPageIndex.BUDGET.ordinal -> FilterBar()
            TreasuryPageIndex.BALANCE.ordinal -> FilterBar()
          }

          // Pages content
          HorizontalPager(state = pagerState, userScrollEnabled = true) { page ->
            when (page) {
              TreasuryPageIndex.RECEIPT.ordinal -> ReceiptListScreen(receiptListViewModel)
              TreasuryPageIndex.BUDGET.ordinal -> Budget(navActions)
              TreasuryPageIndex.BALANCE.ordinal -> Balance(navActions)
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
fun TreasuryTopBar(
    onAccountClick: () -> Unit,
    onSearchClick: () -> Unit,
    viewModel: ReceiptListViewModel
) {
  // Search bar state
  var searchBarVisible by remember { mutableStateOf(false) }
  var searchText by remember { mutableStateOf("") }
  var searchReceipts by remember { mutableStateOf(emptyList<Receipt>()) }

  CenterAlignedTopAppBar(
      title = {
        // If the search icon is clicked, the top bar is replaced
        if (!searchBarVisible) {
          Text(text = "Treasury", style = MaterialTheme.typography.headlineSmall)
        } else {
          TextField(
              value = searchText,
              onValueChange = {
                searchText = it

                viewModel.setSearchQuery(it)
                searchReceipts = viewModel.onSearch()
              },
              label = { Text("Search receipts") },
              leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search icon")
              },
              trailingIcon = {
                IconButton(
                    onClick = {
                      searchText = ""
                      searchReceipts = viewModel.onSearch()
                    }) {
                      Icon(
                          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                          contentDescription = "Back arrow")
                    }
              },
              modifier = Modifier.fillMaxWidth().testTag("receiptSearch"))
        }
      },
      navigationIcon = {
        if (!searchBarVisible) {
          IconButton(modifier = Modifier.testTag("accountIconButton"), onClick = onAccountClick) {
            Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Account logo")
          }
        } else {
          IconButton(
              modifier = Modifier.testTag("backIconButton"),
              onClick = {
                searchBarVisible = false
                searchText = ""
              }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back arrow")
              }
        }
      },
      actions = {
        if (!searchBarVisible) {
          IconButton(
              modifier = Modifier.testTag("searchIconButton"),
              onClick = {
                searchBarVisible = true
                onSearchClick()
              }) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search receipt")
              }
        }
      },
      colors =
          TopAppBarDefaults.mediumTopAppBarColors(
              containerColor = MaterialTheme.colorScheme.surface))
}
