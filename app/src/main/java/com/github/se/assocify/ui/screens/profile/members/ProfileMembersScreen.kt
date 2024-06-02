package com.github.se.assocify.ui.screens.profile.members

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
      contentWindowInsets = WindowInsets(20.dp, 20.dp, 20.dp, 0.dp),
  ) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(it).testTag("membersScreen")) {
      item { Text(text = "Current members", style = MaterialTheme.typography.titleMedium) }

      item { Spacer(Modifier.height(16.dp)) }

      state.currMembers.forEachIndexed { i, member ->
        item {
          if (i == 0) HorizontalDivider()
          ListItem(
              modifier = Modifier.testTag("memberItem-${member.user.uid}"),
              headlineContent = { Text(text = member.user.name) },
              trailingContent = {
                IconButton(onClick = { /*TODO*/}, modifier = Modifier.testTag("editButton")) {
                  Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
              },
              supportingContent = { Text(text = member.role.type.name) })
          HorizontalDivider()
        }
      }
    }
  }
}
