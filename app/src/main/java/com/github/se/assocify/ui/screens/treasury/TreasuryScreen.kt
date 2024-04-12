package com.github.se.assocify.ui.screens.treasury

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.MaybeRemotePhoto
import com.github.se.assocify.model.entities.Phase
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.MainNavigationBar
import kotlinx.coroutines.launch
import java.time.LocalDate

// Index of each tag for navigation
enum class PageIndex(val index: Int) {
  RECEIPT(0),
  BUDGET(1),
  BALANCE(2);

  companion object {
    val NUMBER_OF_PAGES: Int = entries.size
  }
}

/**
 * Treasury Screen composable
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TreasuryScreen(navActions: NavigationActions,
                   currentUser: CurrentUser,
                   viewModel: ReceiptViewmodel = ReceiptViewmodel(currentUser = currentUser)) {
  val viewmodelState by viewModel.uiState.collectAsState()

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
            onClick = {},
        ) {
          Icon(Icons.Outlined.Add, "Create")
        }
      }) { innerPadding ->
        Column(modifier = Modifier
          .padding(innerPadding)
          .fillMaxSize()) {
          val pagerState = rememberPagerState(pageCount = { PageIndex.NUMBER_OF_PAGES })
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
                    selected = pagerState.currentPage == PageIndex.RECEIPT.index,
                    onClick = {
                      coroutineRoute.launch {
                        pagerState.animateScrollToPage(PageIndex.RECEIPT.index)
                      }
                    },
                    text = "Receipts",
                    modifier = Modifier.testTag("myReceiptsTab"))
                TreasuryTab(
                    selected = pagerState.currentPage == PageIndex.BUDGET.index,
                    onClick = {
                      coroutineRoute.launch {
                        pagerState.animateScrollToPage(PageIndex.BUDGET.index)
                      }
                    },
                    text = "Budget",
                    modifier = Modifier.testTag("budgetTab"))
                TreasuryTab(
                    selected = pagerState.currentPage == PageIndex.BALANCE.index,
                    onClick = {
                      coroutineRoute.launch {
                        pagerState.animateScrollToPage(PageIndex.BALANCE.index)
                      }
                    },
                    text = "Balance",
                    modifier = Modifier.testTag("balanceTab"))
              }

          // Pages content
          HorizontalPager(state = pagerState, userScrollEnabled = true) { page ->
            when (page) {
              PageIndex.RECEIPT.index -> MyReceiptPage(viewModel)
              PageIndex.BUDGET.index -> BudgetPage()
              PageIndex.BALANCE.index -> BalancePage()
            }
          }
        }
      }
}

/**
 * ------------------------------------------------- * PAGES *
 * ------------------------------------------------- *
 */

/**
 * My Receipts UI page
 */
@Composable
private fun MyReceiptPage(viewModel: ReceiptViewmodel) {
  // Good practice to re-collect it as the page changes
  val viewmodelState by viewModel.uiState.collectAsState()

  val placeholderReceiptList = listOf(
    Receipt(
      uid = "1",
      payer = "John Doe",
      date = LocalDate.of(2023, 2, 15),
      incoming = false,
      cents = 5000,
      phase = Phase.Unapproved,
      title = "Grocery Shopping",
      description = "Bought groceries at the local store",
      photo = MaybeRemotePhoto.LocalFile(""),
    ),
    Receipt(
      uid = "2",
      payer = "Jane Smith",
      date = LocalDate.of(2023, 2, 14),
      incoming = true,
      cents = 2500,
      phase = Phase.Approved,
      title = "Restaurant",
      description = "Dinner with friends",
      photo = MaybeRemotePhoto.Remote(""),
    ),
    Receipt(
      uid = "3",
      payer = "Alice Johnson",
      date = LocalDate.of(2023, 2, 13),
      incoming = false,
      cents = 10000,
      phase = Phase.PaidBack,
      title = "Gas Station",
      description = "Filled up the car",
      photo = MaybeRemotePhoto.LocalFile(""),
    ),
  )

  LazyColumn(
    modifier = Modifier.testTag("ReceiptList"),
    verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header for the user receipts
    item {
      Text(
        text = "My created receipts",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 16.dp)
      )
      HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp))
    }

    if (viewmodelState.userReceipts.isNotEmpty()) {
      // First list of receipts
      viewmodelState.userReceipts.forEach { receipt ->
        item {
          ReceiptItem(receipt)
          HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp))
        }
      }
    } else {
      // Placeholder for empty list
      item {
        Text(
          text = "No receipts found. You can create one!",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(20.dp)
        )
      }
    }


    // Global receipts only appear if the user has the permission,
    // which is handled in the viewmodel whatsoever
    if (viewmodelState.allReceipts.isNotEmpty()) {
      // Header for the global receipts
      item {
        Text(
          text = "All receipts",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp))
      }
      // Second list of receipts
      viewmodelState.allReceipts.forEach { receipt ->
        item {
          ReceiptItem(receipt)
          HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp))
        }
      }
    }
  }
}

/**
 * Budget UI page
 */
@Composable private fun BudgetPage() {}

/**
 * Balance UI page
 */
@Composable private fun BalancePage() {}

/**
 * ------------------------------------------------- * Elements *
 * ------------------------------------------------- *
 */

/**
 Top tabs component
 */
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

/**
 Main top bar with search and account icon
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreasuryTopBar(
  onAccountClick: () -> Unit,
  onSearchClick: () -> Unit,
  viewModel: ReceiptViewmodel
) {
  var searchBarVisible by remember { mutableStateOf(false) }
  var searchText by remember { mutableStateOf("") }

  var searchReceipts by remember { mutableStateOf(emptyList<Receipt>()) }

  val viewmodelState by viewModel.uiState.collectAsState()

  CenterAlignedTopAppBar(
    title = {
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
            IconButton(onClick = {
              searchText = ""
              searchReceipts = viewModel.onSearch()
            }) {
              Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back arrow")
            }
          },
          modifier = Modifier.fillMaxWidth()
        )

      }
    },
    navigationIcon = {
      if (!searchBarVisible) {
        IconButton(modifier = Modifier.testTag("accountIconButton"), onClick = onAccountClick) {
          Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Account logo")
        }
      } else {
        IconButton(modifier = Modifier.testTag("backIconButton"), onClick = {
          searchBarVisible = false
          searchText = ""
        }) {
          Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back arrow")
        }
      }
    },
    actions = {
      if (!searchBarVisible) {
        IconButton(modifier = Modifier.testTag("searchIconButton"), onClick = {
          searchBarVisible = true
          onSearchClick()
        }) {
          Icon(imageVector = Icons.Filled.Search, contentDescription = "Search receipt")
        }
      }
    },
    colors =
    TopAppBarDefaults.mediumTopAppBarColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  )
}

/** Receipt item from the list in My Receipts page */
@Composable
private fun ReceiptItem(receipt: Receipt) {
  Box(modifier = Modifier
    .fillMaxWidth()
    .padding(6.dp)
    .height(70.dp)
    .testTag("receiptItemBox")) {
    Column(modifier = Modifier.padding(start = 20.dp)) {
      Text(
          text = receipt.date.toString(),
          modifier = Modifier
            .padding(top = 6.dp)
            .testTag("receiptDateText"),
          style =
              TextStyle(
                  fontSize = 12.sp,
                  lineHeight = 16.sp,
                  color = Color(0xFF505050),
                  letterSpacing = 0.5.sp,
              ))
      Text(
          text = receipt.title,
          modifier = Modifier.testTag("receiptNameText"),
          style =
              TextStyle(
                  fontSize = 16.sp,
                  lineHeight = 24.sp,
                  color = Color(0xFF000000),
                  letterSpacing = 0.sp,
              ))
      Text(
          text = receipt.description,
          modifier = Modifier.testTag("receiptDescriptionText"),
          style =
              TextStyle(
                  fontSize = 14.sp,
                  lineHeight = 24.sp,
                  color = Color(0xFF505050),
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
              text = (receipt.cents / 100.0).toString() + ".-",
              modifier = Modifier.testTag("receiptPriceText"),
              style =
                  TextStyle(
                      fontSize = 14.sp,
                      lineHeight = 24.sp,
                      color = Color(0xFF505050),
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

/** Android Studio preview
@Preview
@Composable
private fun PreviewCardsScreen() {
    MyReceiptPage()
}
*/