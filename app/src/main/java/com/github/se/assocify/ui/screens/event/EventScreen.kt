package com.github.se.assocify.ui.screens.event

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.compose.rememberNavController
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.MainNavigationBar
import com.github.se.assocify.ui.screens.event.map.EventMapScreen
import com.github.se.assocify.ui.screens.event.schedule.EventScheduleScreen
import com.github.se.assocify.ui.screens.event.task.EventTaskScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    navActions: NavigationActions,
    event: Event = Event(),
    currentTab: EventTab = EventTab.Tasks
) {
  Scaffold(
      modifier = Modifier.testTag("eventScreen"),
      bottomBar = {
        MainNavigationBar(
            onTabSelect = { navActions.navigateToMainTab(it) },
            tabList = MAIN_TABS_LIST,
            selectedTab = Destination.Event)
      },
      topBar = { EventTopBar() }) {
        Column(modifier = Modifier.padding(it)) {
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
          when (currentTab) {
            EventTab.Tasks -> EventTaskScreen()
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

enum class EventTab(val index: Int) {
  Tasks(0),
  Map(1),
  Schedule(2)
}

@Preview
@Composable
fun EventScreenPreview() {
  EventScreen(NavigationActions(rememberNavController()))
}
