package com.github.se.assocify.ui.screens.createAsso

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.se.assocify.R
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.ui.composables.UserSearchTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAssoScreen(
    currentUser: CurrentUser,
    viewmodel: CreateAssoViewmodel = CreateAssoViewmodel(currentUser)
) {

  val state by viewmodel.uiState.collectAsState()

  Scaffold(
      modifier = Modifier.testTag("createAssoScreen"),
      topBar = {
        TopAppBar(
            modifier = Modifier.fillMaxWidth().testTag("TopAppBar"),
            navigationIcon = {
              IconButton(onClick = { /* TODO : go back to previous screen */}) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
              }
            },
            title = { Text(text = "Create your association") })
      },
      contentWindowInsets = WindowInsets(20.dp, 10.dp, 20.dp, 20.dp)) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Row(
              horizontalArrangement = Arrangement.spacedBy(15.dp),
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()) {
                OutlinedIconButton(
                    modifier = Modifier.padding(top = 8.dp).testTag("logo"),
                    onClick = {
                      /* TODO : can add association logo // note : nowhere to put it yet because picture not handled in DB */
                    }) {
                      Icon(
                          painter = painterResource(id = R.drawable.landscape),
                          contentDescription = "Logo")
                    }
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewmodel.setName(it) },
                    label = { Text("Association Name") },
                    modifier = Modifier.fillMaxWidth().testTag("name"))
              }

          LazyColumn(
              modifier = Modifier.fillMaxWidth().weight(1f).testTag("MemberList"),
              verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
              horizontalAlignment = Alignment.CenterHorizontally) {
                state.members.forEach { member ->
                  item {
                    ListItem(
                        modifier =
                            Modifier.clip(RoundedCornerShape(10.dp))
                                .testTag("MemberListItem-${member.getName()}"),
                        headlineContent = { Text(member.getName()) },
                        overlineContent = { Text(member.getRole().name) },
                        leadingContent = {
                          Icon(Icons.Default.Person, contentDescription = "Person")
                        },
                        trailingContent = {
                          IconButton(
                              onClick = { viewmodel.modifyMember(member) },
                              modifier = Modifier.testTag("editMember-${member.getName()}")) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                              }
                        },
                    )
                    HorizontalDivider()
                  }
                }
              }

          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { viewmodel.addMember() },
                    modifier = Modifier.fillMaxWidth().testTag("addMember")) {
                      Icon(Icons.Default.Add, contentDescription = "Add members")
                      Text("Add members")
                    }
                Button(
                    onClick = { viewmodel.saveAsso() /*TODO then navigate to home*/ },
                    modifier = Modifier.fillMaxWidth().testTag("create"),
                    enabled = viewmodel.canSaveAsso()) {
                      Text("Create")
                    }
              }
        }

        // open dialog to edit member
        if (state.openEdit) {
          Dialog(onDismissRequest = { viewmodel.cancelModifyMember() }) {
            ElevatedCard {
              // temporary UI to see if dialog opens
              Column(
                  modifier = Modifier.padding(16.dp).fillMaxWidth(),
                  horizontalAlignment = Alignment.CenterHorizontally) {
                    UserSearchTextField(
                        modifier = Modifier.testTag("memberSearchField").fillMaxWidth(),
                        searchValue =
                            state.searchMember, // ce qui est tapé dans barre -> vide quand y a un
                        // user
                        userList = state.searchMemberList, // ce qui apparait dans la liste
                        user = state.editMember, // ce qui est sélectionné -> null quand tu cherche
                        onUserSearch = { viewmodel.searchMember(it) }, // on value change
                        onUserSelect = {
                          viewmodel.selectMember(it)
                        }, // quand tu click qur user de dropdown
                        onUserDismiss = { viewmodel.dismissMemberSearch() }, // click sur croix
                        expanded = state.searchMemberList.isNotEmpty(), // dropdown ouvert ou pas
                        label = { Text("Name") },
                        isError = viewmodel.searchError(),
                        supportingText = state.memberError?.let { { Text(it) } })
                    if (state.editMember != null) {
                      // maybe can't select role before selecting member ?
                      Role.RoleType.entries
                          .filter { role -> role != Role.RoleType.PENDING }
                          .forEach { role ->
                            ListItem(
                                headlineContent = { Text(role.name.uppercase()) },
                                trailingContent = {
                                  RadioButton(
                                      selected = state.editMember!!.hasRole(role.name),
                                      onClick = { viewmodel.modifyMemberRole(role.name) })
                                },
                                modifier = Modifier.testTag("role-${role.name.uppercase()}"))
                          }

                      Row(
                          modifier = Modifier.fillMaxWidth().padding(4.dp),
                          horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedButton(
                                onClick = { viewmodel.removeMember(state.editMember!!) },
                                modifier =
                                    Modifier.wrapContentSize().weight(1f).testTag("deleteMember"),
                                colors =
                                    ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)) {
                                  Text("Delete", textAlign = TextAlign.Center)
                                }

                            OutlinedButton(
                                onClick = { viewmodel.addMemberToList() },
                                modifier =
                                    Modifier.wrapContentSize()
                                        .weight(1f)
                                        .testTag("addMemberButton")) {
                                  Text(text = "Save", textAlign = TextAlign.Center)
                                }
                          }
                    }
                  }
            }
          }
        }
      }
}
