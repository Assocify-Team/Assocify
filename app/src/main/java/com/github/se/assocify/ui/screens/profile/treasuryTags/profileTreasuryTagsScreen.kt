package com.github.se.assocify.ui.screens.profile.treasuryTags

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
fun ProfileTreasuryTagsScreen(
    navActions: NavigationActions,
    treasuryTagsViewModel: ProfileTreasuryTagsViewModel
) {
  val state by treasuryTagsViewModel.uiState.collectAsState()

  Scaffold(
      modifier = Modifier.testTag("TreasuryTags Screen"),
      topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Treasury Tags Management") },
            navigationIcon = {
              BackButton(
                  contentDescription = "Arrow Back",
                  onClick = { navActions.back() },
                  modifier = Modifier.testTag("backButton"))
            })
      },
      contentWindowInsets = WindowInsets(20.dp, 10.dp, 20.dp, 20.dp)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(it),
        ) {
          state.treasuryTags.forEach { treasuryTag ->
            item {
              ListItem(
                  headlineContent = { Text(text = treasuryTag.name) },
                  trailingContent = {
                    if (treasuryTag.uid == "add") {
                      IconButton(
                          onClick = { /*TODO*/}, modifier = Modifier.testTag("addTagButton")) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                          }
                    } else {
                      Row {
                        IconButton(
                            onClick = { /*TODO*/}, modifier = Modifier.testTag("editTagButton")) {
                              Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }

                        IconButton(
                            onClick = { /*TODO*/}, modifier = Modifier.testTag("deleteTagButton")) {
                              Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                      }
                    }
                  })
            }
          }
        }
      }
}
