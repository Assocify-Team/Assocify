package com.github.se.assocify.ui.screens.profile.members

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.BackButton
import com.github.se.assocify.ui.composables.PullDownRefreshBox

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
      snackbarHost = { SnackbarHost(hostState = state.snackbarHostState) },
      contentWindowInsets = WindowInsets(20.dp, 20.dp, 20.dp, 0.dp),
  ) {
    PullDownRefreshBox(
        refreshing = state.refresh, onRefresh = { profileMembersViewModel.refreshMembers() }) {
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
                      Row {
                        IconButton(
                            onClick = { profileMembersViewModel.onEditMember(member) },
                            modifier = Modifier.testTag("editButton-$i")) {
                              Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                        IconButton(
                            onClick = { profileMembersViewModel.onDeleteMember(member) },
                            modifier = Modifier.testTag("deleteMemberButton-$i")) {
                              Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                      }
                    },
                    supportingContent = { Text(text = member.role.type.name) })
                HorizontalDivider()
              }
            }
          }

          // Edit member's role dialog
          if (state.showEditMemberDialog) {
            Dialog(onDismissRequest = { profileMembersViewModel.onEditMemberDialogDismiss() }) {
              ElevatedCard {
                Column(
                    modifier = Modifier.padding(16.dp).testTag("editMemberDialog"),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(
                          "Change ${state.updatingMember?.user?.name}'s role ?",
                          style = MaterialTheme.typography.titleMedium)

                      Spacer(modifier = Modifier.height(16.dp))

                      RoleType.entries.forEach { role ->
                        ListItem(
                            headlineContent = { Text(role.name.uppercase()) },
                            trailingContent = {
                              RadioButton(
                                  modifier = Modifier.testTag("role-${role.name}"),
                                  selected = state.newRole == role,
                                  onClick = { profileMembersViewModel.updateRole(role) })
                            },
                            modifier = Modifier.testTag("roleitem-${role.name}"))
                      }

                      Spacer(modifier = Modifier.height(16.dp))

                      Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(
                            onClick = { profileMembersViewModel.onEditMemberDialogDismiss() },
                            modifier = Modifier.wrapContentSize().testTag("cancelButton"),
                        ) {
                          Text(text = "Cancel", textAlign = TextAlign.Center)
                        }
                        OutlinedButton(
                            onClick = { profileMembersViewModel.confirmEditMember() },
                            modifier = Modifier.wrapContentSize().testTag("confirmButton")) {
                              Text(text = "Confirm", textAlign = TextAlign.Center)
                            }
                      }
                    }
              }
            }
          }

          // Confirm delete dialog
          if (state.showDeleteMemberDialog) {
            Dialog(onDismissRequest = { profileMembersViewModel.onDeleteMemberDialogDismiss() }) {
              ElevatedCard {
                Column(
                    modifier = Modifier.padding(16.dp).testTag("deleteMemberDialog"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)) {
                      Text(
                          "Are you sure you want to remove ${state.updatingMember?.user?.name} from the association?")
                      Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(
                            onClick = { profileMembersViewModel.onDeleteMemberDialogDismiss() },
                            modifier = Modifier.wrapContentSize().testTag("cancelButton"),
                        ) {
                          Text(text = "Cancel", textAlign = TextAlign.Center)
                        }
                        OutlinedButton(
                            onClick = { profileMembersViewModel.confirmDeleteMember() },
                            modifier = Modifier.wrapContentSize().testTag("confirmButton"),
                            colors =
                                ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error),
                            border = BorderStroke(1.0.dp, MaterialTheme.colorScheme.error)) {
                              Text(text = "Confirm", textAlign = TextAlign.Center)
                            }
                      }
                    }
              }
            }
          }
        }
  }
}
