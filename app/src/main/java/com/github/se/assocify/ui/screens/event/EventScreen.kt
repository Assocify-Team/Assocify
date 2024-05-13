package com.github.se.assocify.ui.screens.event

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.CenteredCircularIndicator
import com.github.se.assocify.ui.composables.ErrorMessage
import com.github.se.assocify.ui.composables.InnerTabRow
import com.github.se.assocify.ui.composables.MainNavigationBar
import com.github.se.assocify.ui.composables.MainTopBar
import com.github.se.assocify.ui.screens.event.maptab.EventMapScreen
import com.github.se.assocify.ui.screens.event.scheduletab.EventScheduleScreen
import com.github.se.assocify.ui.screens.event.tasktab.EventTaskScreen

/**
 * An event screen that displays the tasks, map, and schedule of an event.
 *
 * @param navActions Navigation actions to navigate to other screens.
 * @param eventScreenViewModel The view model for the event screen.
 * @param taskListViewModel The view model for the task list.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventScreen(navActions: NavigationActions, eventScreenViewModel: EventScreenViewModel) {
  val state by eventScreenViewModel.uiState.collectAsState()

  Scaffold(
      modifier = Modifier.testTag("eventScreen"),
      topBar = {
        MainTopBar(
            title = "Event",
            optInSearchBar = true,
            query = state.searchQuery,
            onQueryChange = { eventScreenViewModel.setSearchQuery(it) },
            onSearch = { eventScreenViewModel.searchTaskLists() },
            page = state.currentTab.ordinal)
      },
      bottomBar = {
        MainNavigationBar(
            onTabSelect = { navActions.navigateToMainTab(it) },
            tabList = MAIN_TABS_LIST,
            selectedTab = Destination.Event)
      },
      floatingActionButton = {
        FloatingActionButton(
            modifier = Modifier.testTag("floatingButtonEvent"),
            onClick = {
              when (state.currentTab) {
                EventPageIndex.Tasks -> {
                  navActions.navigateTo(Destination.NewTask) // TODO : event uid
                }
                EventPageIndex.Map -> {
                  /*TODO: implement for map screen*/
                }
                EventPageIndex.Schedule -> {
                  /*TODO: implement for schedule screen*/
                }
              }
            }) {
              Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
      },
      contentWindowInsets = WindowInsets(20.dp, 0.dp, 20.dp, 0.dp),
      snackbarHost = {
        SnackbarHost(
            hostState = state.snackbarHostState,
            snackbar = { snackbarData ->
              Snackbar(snackbarData = snackbarData, modifier = Modifier.testTag("snackbar"))
            })
      }) {
        if (state.loading) {
          CenteredCircularIndicator()
          return@Scaffold
        }

        if (state.error != null) {
          ErrorMessage(errorMessage = state.error) {
            eventScreenViewModel.fetchEvents()
            eventScreenViewModel.taskListViewModel.updateTasks()
          }
          return@Scaffold
        }

        Column(modifier = Modifier.padding(it).fillMaxHeight()) {
          val pagerState = rememberPagerState(pageCount = { EventPageIndex.entries.size })

          EventFilterBar(eventScreenViewModel)

          InnerTabRow(
              tabList = EventPageIndex.entries,
              pagerState = pagerState,
              switchTab = { tab -> eventScreenViewModel.switchTab(tab) })

          HorizontalPager(state = pagerState, userScrollEnabled = true) { page ->
            when (page) {
              EventPageIndex.Tasks.ordinal ->
                  EventTaskScreen(
                      eventScreenViewModel, eventScreenViewModel.taskListViewModel, navActions)
              EventPageIndex.Map.ordinal -> EventMapScreen()
              EventPageIndex.Schedule.ordinal ->
                  EventScheduleScreen(eventScreenViewModel.scheduleViewModel)
            }
          }
        }
      }
}

/**
 * The filter bar for the event screen that contains the chips.
 *
 * @param eventScreenViewModel The view model for the event screen.
 */
@Composable
fun EventFilterBar(eventScreenViewModel: EventScreenViewModel) {

  val state by eventScreenViewModel.uiState.collectAsState()

  LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    state.events.forEach {
      item {
        FilterChip(
            modifier = Modifier.testTag("filterChipTestEvent"),
            label = { Text(text = it.name) },
            leadingIcon = {
              if (eventScreenViewModel.isEventSelected(it)) {
                Icon(
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
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
