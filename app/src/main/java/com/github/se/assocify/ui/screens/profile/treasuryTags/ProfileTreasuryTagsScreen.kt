package com.github.se.assocify.ui.screens.profile.treasuryTags

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.BackButton
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTreasuryTagsScreen(
    navActions: NavigationActions,
    treasuryTagsViewModel: ProfileTreasuryTagsViewModel
) {
  val state by treasuryTagsViewModel.uiState.collectAsState()
  if (state.creating || state.modify) {
    NamePopUp(treasuryTagsViewModel = treasuryTagsViewModel)
  }
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
      contentWindowInsets = WindowInsets(20.dp, 0.dp, 20.dp, 0.dp),
      floatingActionButton = {
        FloatingActionButton(
            onClick = { treasuryTagsViewModel.creating(true) },
            modifier = Modifier.testTag("addTagButton")) {
              Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
      }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(it),
        ) {
          state.treasuryTags.forEachIndexed { i, treasuryTag ->
            item {
              ListItem(
                  headlineContent = { Text(text = treasuryTag.name) },
                  trailingContent = {
                    Row {
                      IconButton(
                          onClick = { treasuryTagsViewModel.modifying(true, treasuryTag) },
                          modifier = Modifier.testTag("editTagButton")) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                          }

                      IconButton(
                          onClick = { treasuryTagsViewModel.deleteTag(treasuryTag) },
                          modifier = Modifier.testTag("deleteTagButton")) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                          }
                    }
                  })
              if (i < state.treasuryTags.size - 1) HorizontalDivider()
            }
          }
          item { Spacer(modifier = Modifier.height(80.dp)) }
        }
      }
}

@Composable
fun NamePopUp(treasuryTagsViewModel: ProfileTreasuryTagsViewModel) {
  val state by treasuryTagsViewModel.uiState.collectAsState()
  val tag = state.editedTag ?: AccountingCategory(UUID.randomUUID().toString(), "")
  var nameString by remember { mutableStateOf(tag.name) }
  Dialog(onDismissRequest = { treasuryTagsViewModel.cancelPopUp() }) {
    Card(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
      LazyColumn(
          horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
            item {
              Row(
                  modifier = Modifier.fillMaxWidth().padding(8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically) {
                    Text(state.displayedName, style = MaterialTheme.typography.titleLarge)
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close dialog",
                        modifier = Modifier.clickable { treasuryTagsViewModel.cancelPopUp() })
                  }
            }
            item {
              OutlinedTextField(
                  singleLine = true,
                  isError = nameString.isEmpty(),
                  modifier = Modifier.padding(8.dp),
                  value = nameString,
                  onValueChange = { nameString = it },
                  label = { Text("Name") },
                  supportingText = {
                    Text(if (nameString.isEmpty()) "The string should not be empty!" else "")
                  })
            }
            item {
              Row(
                  modifier = Modifier.fillMaxWidth().padding(8.dp),
                  horizontalArrangement = Arrangement.End,
              ) {
                TextButton(
                    content = { Text("Save") },
                    onClick = {
                      val newTag = AccountingCategory(tag.uid, nameString)
                      if (state.modify) treasuryTagsViewModel.modifyTag(newTag)
                      else treasuryTagsViewModel.addTag(newTag)
                      treasuryTagsViewModel.cancelPopUp()
                    },
                    modifier = Modifier,
                )
              }
            }
          }
    }
  }
}
