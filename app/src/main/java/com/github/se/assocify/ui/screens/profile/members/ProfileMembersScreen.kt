package com.github.se.assocify.ui.screens.profile.members

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileMembersScreen(
    navActions: NavigationActions,
    profileMembersViewModel: ProfileMembersViewModel
) {
  val state by profileMembersViewModel.uiState.collectAsState()
  Scaffold(
      modifier = Modifier.testTag("Members Screen"),
      topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Members Management") },
            navigationIcon = {
              BackButton(
                  contentDescription = "Arrow Back",
                  onClick = { navActions.back() },
                  modifier = Modifier.testTag("backButton"))
            })
      },
      contentWindowInsets = WindowInsets(20.dp, 0.dp, 20.dp, 0.dp),
  ) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(it)) {
          Text(text = "New requests")
          LazyColumn {
            state.applicants.forEach {
              item { ListItem(headlineContent = { Text(text = it.name) }) }
            }
          }

          Text(text = "Current members")
          LazyColumn {
            state.currMembers.forEach {
              item { ListItem(headlineContent = { Text(text = it.name) }) }
            }
          }
        }
  }
}
