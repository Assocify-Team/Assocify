package com.github.se.assocify.ui.screens.event

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.se.assocify.model.entities.Event
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

// Index of each tag for navigation
enum class EventPageIndex(val index: Int) {
  TASKS(0),
  MAP(1),
  SCHEDULE(2);

  companion object {
    val NUMBER_OF_PAGES: Int = entries.size
  }
}

/**
 * An event screen that displays the tasks, map, and schedule of an event.
 *
 * @param navActions Navigation actions to navigate to other screens.
 * @param event List of events to display.
 * @param currentTab Current tab to display.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EventScreen(
    navActions: NavigationActions,
    event: List<Event> = emptyList(),
    currentTab: EventPageIndex = EventPageIndex.TASKS
) {

  val nestedScrollConnection = remember { mutableStateOf<NestedScrollConnection?>(null) }

  Scaffold(
      modifier = Modifier.testTag("eventScreen"),
      floatingActionButton = {
        FloatingActionButton(
            modifier = Modifier.testTag("floatingButtonEvent"),
            onClick = {
              /*TODO: adapt the action button depending on the current screen and modify it's navigations */ }) {
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
        CenterAlignedTopAppBar(
            title = { Text(text = "Event") },
            navigationIcon = {
              IconButton(onClick = { /*TODO: navigate to the profile page*/}) {
                Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Account")
              }
            },
            actions = {
              IconButton(onClick = { /*TODO: apply the string filtering of the tasks*/}) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
              }
            })
      }) {
        Column(
          modifier = Modifier
            .padding(it)
        ) {
          val pagerState = rememberPagerState(pageCount = { TreasuryPageIndex.NUMBER_OF_PAGES })
          val coroutineRoute = rememberCoroutineScope()

          EventFilterBar(events = event)
          TabRow(selectedTabIndex = currentTab.index) {
            EventTab(
                text = "Tasks",
                modifier = Modifier.testTag("tasksTab"),
                selected = pagerState.currentPage == EventPageIndex.TASKS.index,
                onClick = {
                  coroutineRoute.launch {
                    pagerState.animateScrollToPage(EventPageIndex.TASKS.index)
                  }
                })
            EventTab(
                text = "Map",
                modifier = Modifier.testTag("mapTab"),
                selected = pagerState.currentPage == EventPageIndex.MAP.index,
                onClick = {
                  coroutineRoute.launch { pagerState.animateScrollToPage(EventPageIndex.MAP.index) }
                })
            EventTab(
                text = "Schedule",
                modifier = Modifier.testTag("scheduleTab"),
                selected = pagerState.currentPage == EventPageIndex.SCHEDULE.index,
                onClick = {
                  coroutineRoute.launch {
                    pagerState.animateScrollToPage(EventPageIndex.SCHEDULE.index)
                  }
                })
          }
          val t1 = Task("uid1", "task 1", "the task 1", true)
          val t2 = Task("uid2", "task 2", "the task 2", false)
          val t3 = Task("uid3", "task 3", "the task 3", true)
          val testTasks = listOf(t1, t2, t3)
          HorizontalPager(state = pagerState, userScrollEnabled = true) { page ->
            when (page) {
              EventPageIndex.TASKS.ordinal -> EventTaskScreen(testTasks)
              EventPageIndex.MAP.ordinal -> EventMapScreen()
              EventPageIndex.SCHEDULE.ordinal -> EventScheduleScreen()
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

/**
 * Filter bar of the event screen.
 *
 * @param events List of events to filter.
 */
@Composable
fun EventFilterBar(events: List<Event>) {
  Row() {
    events.forEach {
      FilterChip(
          label = { Text(it.name) },
          selected = true,
          onClick = { /*TODO: apply the filtering of the tasks of the chip*/},
          modifier = Modifier.padding(8.dp))
    }
  }
}


@Preview
@Composable
fun EventScreenPreview() {
  val t1 = Task("uid", "the task 1", "a short description", true)

  EventScreen(NavigationActions(rememberNavController()), event = emptyList())
}
