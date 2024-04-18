package com.github.se.assocify.ui.screens.event

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.google.android.material.floatingactionbutton.FloatingActionButton

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EventScreen(
    navActions: NavigationActions,
    event: List<Event> = emptyList(),
    currentTab: EventTab = EventTab.Tasks
) {
  Scaffold(
      modifier = Modifier.testTag("eventScreen"),
      floatingActionButton = { FloatingActionButton(onClick = { /*TODO*/ }) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
      }},
      bottomBar = {
        MainNavigationBar(
            onTabSelect = { navActions.navigateToMainTab(it) },
            tabList = MAIN_TABS_LIST,
            selectedTab = Destination.Event)
      },
      topBar = { EventTopBar() }) {
        Column(modifier = Modifier.padding(it)) {
          EventFilterBar(events = event)
          TabRow(selectedTabIndex = currentTab.index) {
            Tab(
                text = { Text("Tasks") },
                selected = currentTab == EventTab.Tasks,
                onClick = { /*TODO*/})
            Tab(
                text = { Text("Map") },
                selected = currentTab == EventTab.Map,
                onClick = { /*TODO*/})
            Tab(
                text = { Text("Schedule") },
                selected = currentTab == EventTab.Schedule,
                onClick = { /*TODO*/})
          }
          val t1 = Task("task 1", "the task 1", true)
          val t2 = Task("task 2", "the task 2", false)
          val t3 = Task("task 3", "the task 3", true)
          val testTasks = listOf(t1, t2, t3)
          when (currentTab) {
            EventTab.Tasks -> EventTaskScreen(testTasks)
            EventTab.Map -> EventMapScreen()
            EventTab.Schedule -> EventScheduleScreen()
          }
        }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTopBar() {
  CenterAlignedTopAppBar(
      title = { Text(text = "Event") },
      navigationIcon = {
        IconButton(onClick = { /*TODO*/}) {
          Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Account")
        }
      },
      actions = {
        IconButton(onClick = { /*TODO*/}) {
          Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
        }
      })
}

@Composable
fun EventFilterBar(events: List<Event>) {
  Row() {
    events.forEach {
      FilterChip(
          label = { Text(it.name) },
          selected = true,
          onClick = { /*TODO*/},
          modifier = Modifier.padding(8.dp))
    }
  }
}

enum class EventTab(val index: Int) {
  Tasks(0),
  Map(1),
  Schedule(2)
}

@Preview
@Composable
fun EventScreenPreview() {
  val t1 = Task("task 1", "the task 1", true)
  val event1 =
      Event(
          "1",
          "Event 1",
          "Event 1 description",
          "2022-01-01",
          "2022-01-02",
          emptyList(),
          emptyList(),
          emptyList())
  val event2 =
      Event(
          "1",
          "Event 3",
          "Event 1 description",
          "2022-01-01",
          "2022-01-02",
          emptyList(),
          emptyList(),
          emptyList())
  val event3 =
      Event(
          "3",
          "Event 3",
          "Event 1 description",
          "2022-01-01",
          "2022-01-02",
          emptyList(),
          emptyList(),
          emptyList())
  EventScreen(NavigationActions(rememberNavController()), event = listOf(event1, event2, event3))
}
