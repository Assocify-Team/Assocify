package com.github.se.assocify.ui.screens.treasury

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.MainNavigationBar
import com.github.se.assocify.ui.screens.treasury.accounting.FilterBar
import com.github.se.assocify.ui.screens.treasury.accounting.balance.Balance
import com.github.se.assocify.ui.screens.treasury.accounting.budget.Budget
import com.github.se.assocify.ui.util.DateUtil
import com.github.se.assocify.ui.util.PriceUtil
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
    viewModel: ReceiptListViewModel = ReceiptListViewModel(navActions)
) {
  val viewmodelState by viewModel.uiState.collectAsState()
  val pagerState = rememberPagerState(pageCount = { TreasuryPageIndex.NUMBER_OF_PAGES })
  Scaffold(
      modifier = Modifier.testTag("treasuryScreen"),
      topBar = { TreasuryTopBar({}, {}, viewModel) },
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
      }) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
          val coroutineRoute = rememberCoroutineScope()

          // Tabs
          TabRow(
              modifier = Modifier
                  .height(48.dp)
                  .testTag("tabRows"),
              selectedTabIndex = pagerState.currentPage,
              containerColor = MaterialTheme.colorScheme.background,
              contentColor = MaterialTheme.colorScheme.primary,
              divider = {},
              indicator = { tabPositions ->
                Box(
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        .size(width = 10.dp, height = 3.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        ))
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
              TreasuryPageIndex.RECEIPT.ordinal -> MyReceiptPage(viewModel)
              TreasuryPageIndex.BUDGET.ordinal -> BudgetPage(navActions)
              TreasuryPageIndex.BALANCE.ordinal -> BalancePage(navActions)
            }
          }
        }
      }
}

/**
 * ------------------------------------------------- * PAGES *
 * ------------------------------------------------- *
 */

/** My Receipts UI page */
@Composable
private fun MyReceiptPage(viewModel: ReceiptListViewModel) {
  // Good practice to re-collect it as the page changes
  val viewmodelState by viewModel.uiState.collectAsState()

  LazyColumn(
      modifier = Modifier.testTag("ReceiptList"),
      verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
      horizontalAlignment = Alignment.CenterHorizontally) {
        // Header for the user receipts
        item {
          Text(
              text = "My Receipts",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 20.dp, vertical = 16.dp))
          HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp))
        }

        if (viewmodelState.userReceipts.isNotEmpty()) {
          // First list of receipts
          viewmodelState.userReceipts.forEach { receipt ->
            item {
              ReceiptItem(receipt, viewModel)
              HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp))
            }
          }
        } else {
          // Placeholder for empty list
          item {
            Text(
                text = "No receipts found. You can create one!",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(20.dp))
          }
        }

        // Global receipts only appear if the user has the permission,
        // which is handled in the viewmodel whatsoever
        if (viewmodelState.allReceipts.isNotEmpty()) {
          // Header for the global receipts
          item {
            Text(
                text = "All Receipts",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp))
            HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp))
          }
          // Second list of receipts
          viewmodelState.allReceipts.forEach { receipt ->
            item {
              ReceiptItem(receipt, viewModel)
              HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp))
            }
          }
        }
      }
}

/** Budget UI page */
@Composable
private fun BudgetPage(navigationActions: NavigationActions) {
  Budget(navigationActions)
}

/** Balance UI page */
@Composable
private fun BalancePage(navigationActions: NavigationActions) {
  Balance(navigationActions)
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
              modifier = Modifier
                  .fillMaxWidth()
                  .testTag("receiptSearch"))
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

/** Receipt item from the list in My Receipts page */
@Composable
private fun ReceiptItem(receipt: Receipt, viewModel: ReceiptListViewModel) {
  Box(
      modifier =
      Modifier
          .fillMaxWidth()
          .padding(6.dp)
          .height(70.dp)
          .testTag("receiptItemBox")
          .clickable {
              viewModel.onReceiptClick(receipt)
          }) {
        Column(modifier = Modifier.padding(start = 20.dp)) {
          Text(
              text = DateUtil.toString(receipt.date),
              modifier = Modifier
                  .padding(top = 6.dp)
                  .testTag("receiptDateText"),
              style =
                  TextStyle(
                      fontSize = 12.sp,
                      lineHeight = 16.sp,
                      color = MaterialTheme.colorScheme.secondary,
                      letterSpacing = 0.5.sp,
                  ))
          Text(
              text = receipt.title,
              modifier = Modifier.testTag("receiptNameText"),
              style =
                  TextStyle(
                      fontSize = 16.sp,
                      lineHeight = 24.sp,
                      letterSpacing = 0.sp,
                  ))
          Text(
              text = receipt.description,
              modifier = Modifier.testTag("receiptDescriptionText"),
              style =
                  TextStyle(
                      fontSize = 12.sp,
                      lineHeight = 24.sp,
                      color = MaterialTheme.colorScheme.secondary,
                      letterSpacing = 0.sp,
                  ))
        }

        Row(
            modifier =
            Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 8.dp)
                .testTag("receiptPriceAndIconRow"),
            verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = PriceUtil.fromCents(receipt.cents),
                  modifier = Modifier.testTag("receiptPriceText"),
                  style =
                      TextStyle(
                          fontSize = 14.sp,
                          lineHeight = 24.sp,
                          color = MaterialTheme.colorScheme.secondary,
                          letterSpacing = 0.sp,
                      ))
              Spacer(modifier = Modifier.width(8.dp))
              Icon(
                  modifier = Modifier
                      .size(20.dp)
                      .testTag("shoppingCartIcon"),
                  imageVector = Icons.Filled.ShoppingCart,
                  contentDescription = "Arrow icon",
              )
            }
      }
}
