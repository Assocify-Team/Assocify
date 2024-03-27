package com.github.se.assocify.ui.screens.Treasury

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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Number of tabs at the top
const val NUMBER_OF_PAGES: Int = 3

// Index of each tag for navigation
const val PAGE_RECEIPT_INDEX: Int = 0
const val PAGE_BUDGET_INDEX: Int = 1
const val PAGE_BALANCE_INDEX: Int = 2

/**
 * Main treasury screen UI
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TreasuryMainScreen() {
    Scaffold(
        topBar = {
            TreasuryTopBar({}, {})
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.testTag("AddReceipt"),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                onClick = { },
            ) {
                Icon(Icons.Outlined.Add, "Create")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val pagerState = rememberPagerState(
                pageCount = { NUMBER_OF_PAGES }
            )
            val coroutineRoute = rememberCoroutineScope()

            // Tabs
            TabRow(
                modifier = Modifier.height(48.dp),
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = { },
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .size(width = 10.dp, height = 3.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
            ) {
                TreasuryTab(
                    selected = pagerState.currentPage == PAGE_RECEIPT_INDEX,
                    onClick = {
                        coroutineRoute.launch {
                            pagerState.animateScrollToPage(
                                PAGE_RECEIPT_INDEX
                            )
                        }
                    },
                    text = "My receipts"
                )
                TreasuryTab(
                    selected = pagerState.currentPage == PAGE_BUDGET_INDEX,
                    onClick = {
                        coroutineRoute.launch {
                            pagerState.animateScrollToPage(
                                PAGE_BUDGET_INDEX
                            )
                        }
                    },
                    text = "Budget"
                )
                TreasuryTab(
                    selected = pagerState.currentPage == PAGE_BALANCE_INDEX,
                    onClick = {
                        coroutineRoute.launch {
                            pagerState.animateScrollToPage(
                                PAGE_BALANCE_INDEX
                            )
                        }
                    },
                    text = "Balance"
                )
            }

            // Pages content
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = true
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (page) {
                        PAGE_RECEIPT_INDEX -> MyReceiptPage()
                        PAGE_BUDGET_INDEX -> BudgetPage()
                        PAGE_BALANCE_INDEX -> BalancePage()
                    }
                }
            }
        }
    }
}

/** ------------------------------------------------- **
 *  PAGES                                              *
 ** ------------------------------------------------- **/

/**
 * My receipts UI page
 */
@Composable
private fun MyReceiptPage() {
    //val receiptNames = listOf("Grocery Shopping", "Restaurant", "Gas Station", "Coffee Shop")
    val receiptNames = List(50) { "Receipt $it" }
    LazyColumn(
        modifier = Modifier.testTag("ReceiptList"),
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        receiptNames.forEach { receiptName ->
            item {
                ReceiptItem(receiptName)
                Divider(modifier = Modifier.padding(start = 20.dp, end = 20.dp))
            }
        }
    }
}

/**
 * Budget UI page
 */
@Composable
private fun BudgetPage() {

}

/**
 * Balance UI page
 */
@Composable
private fun BalancePage() {

}

/** ------------------------------------------------- **
 *  Elements                                           *
 ** ------------------------------------------------- **/

/**
 * Top tabs component
 */
@Composable
fun TreasuryTab(
    selected: Boolean,
    onClick: () -> Unit,
    text: String
) {
    Tab(
        selected = selected,
        onClick = onClick,
        text = {
            Text(
                text = text,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                }
            )
        }
    )
}

/**
 * Main top bar with search and account icon
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreasuryTopBar(
    onAccountClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Treasury",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = onAccountClick) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Localized description"
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

/**
 * Receipt item from the list in My Receipts page
 */
@Composable
private fun ReceiptItem(receiptName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .height(70.dp)
    ) {
        Column(
            modifier = Modifier.padding(start = 20.dp)
        ) {
            Text(
                text = "17/04/2002",
                modifier = Modifier.padding(top = 6.dp),
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFF505050),
                    letterSpacing = 0.5.sp,
                )
            )
            Text(
                text = receiptName,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color(0xFF000000),
                    letterSpacing = 0.sp,
                )
            )
            Text(
                text = "Super description mdr",
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 24.sp,
                    color = Color(0xFF505050),
                    letterSpacing = 0.sp,
                )
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "80.-",
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 24.sp,
                    color = Color(0xFF505050),
                    letterSpacing = 0.sp,
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "Arrow icon",
            )
        }
    }
}

/**
 * Android Studio preview
 */
@Preview
@Composable
private fun PreviewCardsScreen() {
    TreasuryMainScreen()
}

