package com.github.se.assocify.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * An inner tab row
 *
 * @param tabList The list of tabs.
 * @param pagerState The state of the pager.
 * @param switchTab The action to perform when the tab is switched.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T : Enum<*>> InnerTabRow(tabList: List<T>, pagerState: PagerState, switchTab: (T) -> Unit) {
  val coroutineRoute = rememberCoroutineScope()

  TabRow(
      selectedTabIndex = pagerState.currentPage,
      containerColor = MaterialTheme.colorScheme.background,
      contentColor = MaterialTheme.colorScheme.primary,
      divider = {},
      indicator = { TabIndicator(it, pagerState) },
  ) {
    tabList.forEach { tab ->
      InnerTab(
          text = tab.name,
          modifier = Modifier.testTag(tab.name.lowercase() + "Tab"),
          selected = pagerState.currentPage == tab.ordinal,
          onClick = {
            coroutineRoute.launch {
              pagerState.animateScrollToPage(tab.ordinal)
              switchTab(tab)
            }
          })
    }
  }
}

/**
 * An inner tab
 *
 * @param selected Whether the tab is selected.
 * @param onClick The action to perform when the tab is clicked.
 * @param text The text to display on the tab.
 * @param modifier The modifier for the tab.
 */
@Composable
fun InnerTab(selected: Boolean, onClick: () -> Unit, text: String, modifier: Modifier = Modifier) {
  Tab(
      modifier = modifier,
      selected = selected,
      onClick = onClick,
      text = {
        Text(
            text = text,
            color =
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
      })
}

/**
 * The tab indicator
 *
 * @param tabPositions The positions of the tabs.
 * @param pagerState The state of the pager.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabIndicator(tabPositions: List<TabPosition>, pagerState: PagerState) {
  Box(
      modifier =
          Modifier.fillMaxSize()
              .tabIndicatorOffset(tabPositions[pagerState.currentPage])
              .size(width = 10.dp, height = 3.dp)
              .background(
                  color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)))
}
