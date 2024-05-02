package com.github.se.assocify.ui.screens.event

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.MainNavigationBar
import com.github.se.assocify.ui.screens.event.maptab.EventMapScreen
import com.github.se.assocify.ui.screens.event.scheduletab.EventScheduleScreen
import com.github.se.assocify.ui.screens.event.tasktab.EventTaskScreen
import com.github.se.assocify.ui.screens.event.tasktab.EventTaskViewModel
import java.time.OffsetDateTime
import kotlinx.coroutines.launch

/**
 * An event screen that displays the tasks, map, and schedule of an event.
 *
 * @param navActions Navigation actions to navigate to other screens.
 * @param eventScreenViewModel The view model for the event screen.
 * @param taskListViewModel The view model for the task list.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventScreen(
    navActions: NavigationActions,
    eventScreenViewModel: EventScreenViewModel,
    taskListViewModel: EventTaskViewModel
) {
  val state = eventScreenViewModel.uiState.collectAsState()
  Scaffold(
      modifier = Modifier.testTag("eventScreen"),
      floatingActionButton = {
        FloatingActionButton(
            modifier = Modifier.testTag("floatingButtonEvent"),
            onClick = {
              when (state.value.currentTab) {
                EventPageIndex.TASKS -> {
                  navActions.navigateTo(Destination.NewTask("eventUid")) // TODO : event uid
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
        if (state.value.stateBarDisplay) {
          EventSearchTopBar(eventScreenViewModel)
        } else {
          EventTitleTopBar(navActions, eventScreenViewModel)
        }
      }) {
        if (state.value.error) {
          Column(
              modifier = Modifier.padding(it).fillMaxSize().testTag("errorText"),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center) {
                Text(state.value.errorText)
              }
        } else {
          Column(modifier = Modifier.padding(it).fillMaxHeight()) {
            val pagerState = rememberPagerState(pageCount = { EventPageIndex.NUMBER_OF_PAGES })
            val coroutineRoute = rememberCoroutineScope()

            EventFilterBar(eventScreenViewModel)
            TabRow(selectedTabIndex = pagerState.currentPage) {
              EventTab(
                  text = "Tasks",
                  modifier = Modifier.testTag("tasksTab"),
                  selected = pagerState.currentPage == EventPageIndex.TASKS.index,
                  onClick = {
                    coroutineRoute.launch {
                      pagerState.animateScrollToPage(EventPageIndex.TASKS.index)
                      eventScreenViewModel.switchTab(EventPageIndex.TASKS)
                    }
                  })
              EventTab(
                  text = "Map",
                  modifier = Modifier.testTag("mapTab"),
                  selected = pagerState.currentPage == EventPageIndex.MAP.index,
                  onClick = {
                    coroutineRoute.launch {
                      pagerState.animateScrollToPage(EventPageIndex.MAP.index)
                      eventScreenViewModel.switchTab(EventPageIndex.MAP)
                    }
                  })
              EventTab(
                  text = "Schedule",
                  modifier = Modifier.testTag("scheduleTab"),
                  selected = pagerState.currentPage == EventPageIndex.SCHEDULE.index,
                  onClick = {
                    coroutineRoute.launch {
                      pagerState.animateScrollToPage(EventPageIndex.SCHEDULE.index)
                      eventScreenViewModel.switchTab(EventPageIndex.SCHEDULE)
                    }
                  })
            }
            HorizontalPager(state = pagerState, userScrollEnabled = true) { page ->
              when (page) {
                EventPageIndex.TASKS.index ->
                    EventTaskScreen(eventScreenViewModel, taskListViewModel, navActions)
                EventPageIndex.MAP.index -> EventMapScreen()
                EventPageIndex.SCHEDULE.index -> EventScheduleScreen()
              }
            }
          }
        }
      }
}

/**
 * A tab for the event screen.
 *
 * @param selected Whether the tab is selected.
 * @param onClick The action to perform when the tab is clicked.
 * @param text The text to display on the tab.
 * @param modifier The modifier for the tab.
 */
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

/**
 * The Top Bar of the Event Screen when not searching.
 *
 * @param navActions The navigation actions to navigate to other screens.
 * @param eventScreenViewModel The view model for the event screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTitleTopBar(navActions: NavigationActions, eventScreenViewModel: EventScreenViewModel) {
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
            onClick = { eventScreenViewModel.modifySearchingState(true) }) {
              Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
            }
      })
}

/**
 * The Top Bar of the Event Screen when searching.
 *
 * @param eventScreenViewModel The view model for the event screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSearchTopBar(eventScreenViewModel: EventScreenViewModel) {
  val state = eventScreenViewModel.uiState.collectAsState()
  SearchBar(
      modifier = Modifier.padding(8.dp).testTag("searchBar").fillMaxWidth(),
      query = state.value.searchQuery,
      onQueryChange = { eventScreenViewModel.modifySearchQuery(it) },
      onSearch = { eventScreenViewModel.modifySearchingState(false) },
      active = false,
      onActiveChange = {},
      leadingIcon = {
        Icon(
            modifier =
                Modifier.clickable { eventScreenViewModel.deactivateSearch() }
                    .testTag("dismissBarButton"),
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Dismiss")
      },
      trailingIcon = {
        Icon(
            modifier =
                Modifier.clickable { eventScreenViewModel.searchTaskLists() }
                    .testTag("searchBarButton"),
            imageVector = Icons.Filled.Search,
            contentDescription = "Search")
      }) {}
}

/**
 * The filter bar for the event screen that contains the chips.
 *
 * @param eventScreenViewModel The view model for the event screen.
 */
@Composable
fun EventFilterBar(eventScreenViewModel: EventScreenViewModel) {
  LazyRow {
    item {
      val state = eventScreenViewModel.uiState.collectAsState()
      state.value.events.forEach {
        FilterChip(
            modifier = Modifier.padding(8.dp).testTag("filterChipTestEvent"),
            label = { Text(text = it.name) },
            leadingIcon = {
              if (eventScreenViewModel.isEventSelected(it)) {
                Icon(
                    modifier = Modifier.padding(8.dp).size(FilterChipDefaults.IconSize),
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Selected")
              }
            },
            selected = eventScreenViewModel.isEventSelected(it),
            onClick = {
              eventScreenViewModel.setEventSelection(it, !eventScreenViewModel.isEventSelected(it))
            })
      }
    }
  }
}
