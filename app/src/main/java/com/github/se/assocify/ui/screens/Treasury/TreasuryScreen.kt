package com.github.se.assocify.ui.screens.Treasury

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.github.se.assocify.ui.theme.AssocifyTheme

const val NUMBER_OF_PAGES: Int = 3
const val PAGE_RECEIPT_INDEX: Int = 0
const val PAGE_BUDGET_INDEX: Int = 1
const val PAGE_BALANCE_INDEX: Int = 2

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TreasuryMainScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Treasury",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Localized description"
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
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
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = { },
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .size(width = 1.dp, height = 4.dp)
                            .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
                    )
                }
            ) {
                // My receipts
                Tab(
                    selected = pagerState.currentPage == PAGE_RECEIPT_INDEX,
                    onClick = { coroutineRoute.launch {
                        pagerState.animateScrollToPage(PAGE_RECEIPT_INDEX)
                    } },
                    text = { Text("My receipts") }
                )
                // Budget
                Tab(
                    selected = pagerState.currentPage == PAGE_BUDGET_INDEX,
                    onClick = { coroutineRoute.launch {
                        pagerState.animateScrollToPage(PAGE_BUDGET_INDEX)
                    } },
                    text = { Text("Budget") }
                )
                // Balance
                Tab(
                    selected = pagerState.currentPage == PAGE_BALANCE_INDEX,
                    onClick = { coroutineRoute.launch {
                        pagerState.animateScrollToPage(PAGE_BALANCE_INDEX)
                    } },
                    text = { Text("Balance") }
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

@Composable
private fun MyReceiptPage() {

}

@Composable
private fun BudgetPage() {

}

@Composable
private fun BalancePage() {

}

@Preview
@Composable
private fun PreviewCardsScreen() {
    TreasuryMainScreen()
}