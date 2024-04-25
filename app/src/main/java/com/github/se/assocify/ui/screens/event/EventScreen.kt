package com.github.se.assocify.ui.screens.event

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.assocify.model.entities.Task
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.MainNavigationBar
import com.github.se.assocify.ui.screens.event.map.EventMapScreen
import com.github.se.assocify.ui.screens.event.schedule.EventScheduleScreen
import com.github.se.assocify.ui.screens.event.task.EventTaskScreen
import com.github.se.assocify.ui.screens.treasury.TreasuryPageIndex
import kotlinx.coroutines.launch

/**
 * An event screen that displays the tasks, map, and schedule of an event.
 *
 * @param navActions Navigation actions to navigate to other screens.
 * @param event List of events to display.
 * @param currentTab Current tab to display.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventScreen(navActions: NavigationActions, viewModel: EventScreenViewModel) {
  val state = viewModel.uiState.collectAsState()
  Scaffold(
      modifier = Modifier.testTag("eventScreen"),
      floatingActionButton = {
        FloatingActionButton(
            modifier = Modifier.testTag("floatingButtonEvent"),
            onClick = {
              when (state.value.currentTab) {
                EventPageIndex.TASKS -> {
                  /*TODO: implement for tasks screen*/
                }
                EventPageIndex.MAP -> {
                  /*TODO: implement for map screen*/
                }
                EventPageIndex.SCHEDULE -> {
                  /*TODO: implement for schedule screen*/
                }
              }
            }) {
              Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
      },
      bottomBar = {
        MainNavigationBar(
            onTabSelect = { navActions.navigateToMainTab(it) },
            tabList = MAIN_TABS_LIST,
            selectedTab = Destination.Event)
      },
      topBar = {
        if (state.value.searching) {
          EventSearchTopBar(viewModel)
        } else {
          EventTitleTopBar(navActions, viewModel)
        }
      }) {
        Column(modifier = Modifier.padding(it).fillMaxHeight()) {
          val pagerState = rememberPagerState(pageCount = { TreasuryPageIndex.NUMBER_OF_PAGES })
          val coroutineRoute = rememberCoroutineScope()

          EventFilterBar(viewModel)
          TabRow(selectedTabIndex = pagerState.currentPage) {
            EventTab(
                text = "Tasks",
                modifier = Modifier.testTag("tasksTab"),
                selected = pagerState.currentPage == EventPageIndex.TASKS.index,
                onClick = {
                  coroutineRoute.launch {
                    pagerState.animateScrollToPage(EventPageIndex.TASKS.index)
                    viewModel.switchTab(EventPageIndex.TASKS)
                  }
                })
            EventTab(
                text = "Map",
                modifier = Modifier.testTag("mapTab"),
                selected = pagerState.currentPage == EventPageIndex.MAP.index,
                onClick = {
                  coroutineRoute.launch {
                    pagerState.animateScrollToPage(EventPageIndex.MAP.index)
                    viewModel.switchTab(EventPageIndex.MAP)
                  }
                })
            EventTab(
                text = "Schedule",
                modifier = Modifier.testTag("scheduleTab"),
                selected = pagerState.currentPage == EventPageIndex.SCHEDULE.index,
                onClick = {
                  coroutineRoute.launch {
                    pagerState.animateScrollToPage(EventPageIndex.SCHEDULE.index)
                    viewModel.switchTab(EventPageIndex.SCHEDULE)
                  }
                })
          }
          val t1 = Task("uid1", "task 1", "the task 1", true)
          val t2 = Task("uid2", "task 2", "the task 2", false)
          val t3 = Task("uid3", "task 3", "the task 3", true)
          val testTasks = listOf(t1, t2, t3)
          HorizontalPager(state = pagerState, userScrollEnabled = true) { page ->
            when (page) {
              TreasuryPageIndex.RECEIPT.ordinal -> EventTaskScreen(testTasks)
              TreasuryPageIndex.BUDGET.ordinal -> EventMapScreen()
              TreasuryPageIndex.BALANCE.ordinal -> EventScheduleScreen()
            }
          }
        }
      }
}

/** Tab of the event screen */
@Composable
fun EventTab(selected: Boolean, onClick: () -> Unit, text: String, modifier: Modifier = Modifier) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTitleTopBar(navActions: NavigationActions, viewModel: EventScreenViewModel) {
  CenterAlignedTopAppBar(
      modifier = Modifier.testTag("topBar"),
      title = { Text(text = "Event") },
      navigationIcon = {
        IconButton(
            modifier = Modifier.testTag("eventAccountIcon"),
            onClick = { navActions.navigateToMainTab(Destination.Profile) }) {
              Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Account")
            }
      },
      actions = {
        IconButton(
            modifier = Modifier.testTag("eventSearchButton"),
            onClick = { viewModel.modifySearchingState(true) }) {
              Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
            }
      })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSearchTopBar(viewModel: EventScreenViewModel) {
  val state = viewModel.uiState.collectAsState()
  SearchBar(
      modifier = Modifier.padding(8.dp).testTag("searchBar").fillMaxWidth(),
      query = state.value.searchQuery,
      onQueryChange = { viewModel.modifySearchQuery(it) },
      onSearch = { viewModel.modifySearchingState(false) },
      active = false,
      onActiveChange = {},
      leadingIcon = {
        Icon(
            modifier = Modifier.clickable { viewModel.modifySearchingState(false) },
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Dismiss")
      },
      trailingIcon = {
        Icon(
            modifier =
                Modifier.clickable {
                  when (state.value.currentTab) {
                    EventPageIndex.TASKS -> {
                      /*TODO: implement for tasks screen*/
                    }
                    EventPageIndex.MAP -> {
                      /*TODO: implement for map screen*/
                    }
                    EventPageIndex.SCHEDULE -> {
                      /*TODO: implement for schedule screen*/
                    }
                  }
                },
            imageVector = Icons.Filled.Search,
            contentDescription = "Search")
      }) {}
}

@Composable
fun EventFilterBar(viewModel: EventScreenViewModel) {

  LazyRow() {
    item {
      val state = viewModel.uiState.collectAsState()
      state.value.events.forEach {
        FilterChip(
            modifier = Modifier.padding(8.dp).testTag("filterChipTestEvent"),
            label = { Text(text = it.name) },
            leadingIcon = {
              if (viewModel.isEventSelected(it)) {
                Icon(
                    modifier = Modifier.padding(8.dp),
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected")
              }
            },
            selected = viewModel.isEventSelected(it),
            onClick = { viewModel.setEventSelection(it, !viewModel.isEventSelected(it)) })
      }
    }
  }
}
