package com.github.se.assocify.ui.screens.profile.members

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
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
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(it),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
          item { Text(text = "New requests", style = MaterialTheme.typography.titleMedium) }

          state.applicants.forEach { applicant ->
            item {
              ElevatedCard(
                  elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                  modifier = Modifier.fillMaxWidth(),
              ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically) {
                      Text(
                          text = applicant.name,
                          modifier = Modifier.padding(16.dp).weight(1f),
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis,
                      )

                      Row(horizontalArrangement = Arrangement.End) {
                        IconButton(
                            onClick = { /*TODO*/}, modifier = Modifier.testTag("rejectButton")) {
                              Icon(
                                  Icons.Default.Close,
                                  contentDescription = "Reject",
                                  tint = MaterialTheme.colorScheme.error)
                            }
                        IconButton(
                            onClick = { /*TODO*/}, modifier = Modifier.testTag("acceptButton")) {
                              Icon(
                                  Icons.Default.Check,
                                  contentDescription = "Accept",
                                  tint = Color.Green)
                            }
                      }
                    }
              }
            }
          }

          item { Text(text = "Current members", style = MaterialTheme.typography.titleMedium) }

          state.currMembers.forEachIndexed { i, member ->
            item {
              if (i == 0) HorizontalDivider()
              ListItem(
                  headlineContent = { Text(text = member.name) },
                  trailingContent = {
                    IconButton(onClick = { /*TODO*/}) {
                      Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                  })
              HorizontalDivider()
            }
          }
        }
  }
}
