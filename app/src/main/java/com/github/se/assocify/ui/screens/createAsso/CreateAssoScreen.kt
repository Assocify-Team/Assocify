package com.github.se.assocify.ui.screens.createAsso

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.se.assocify.R
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAssoScreen(viewmodel: CreateAssoViewmodel = CreateAssoViewmodel()) {

  val state by viewmodel.uiState.collectAsState()
    var editMember by remember { mutableStateOf(User()) }

  Scaffold(
      modifier = Modifier.testTag("createAssoScreen"),
      topBar = {
        TopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("TopAppBar"),
            navigationIcon = {
              IconButton(onClick = { /* TODO : go back to previous screen */}) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
              }
            },
            title = { Text(text = "Create your association") })
      },
      contentWindowInsets = WindowInsets(20.dp, 10.dp, 20.dp, 20.dp)) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Row(
              horizontalArrangement = Arrangement.spacedBy(15.dp),
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()) {
                OutlinedIconButton(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .testTag("logo"),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("name"))
              }

          LazyColumn(
              modifier = Modifier
                  .fillMaxWidth()
                  .weight(1f)
                  .testTag("MemberList"),
              verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
              horizontalAlignment = Alignment.CenterHorizontally) {
                state.members.forEach { member ->
                  item {
                    ListItem(
                        modifier =
                        Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .testTag("MemberListItem"),
                        headlineContent = { Text(member.name) },
                        overlineContent = { Text(member.role.name) },
                        leadingContent = {
                          Icon(Icons.Default.Person, contentDescription = "Person")
                        },
                        trailingContent = {
                          IconButton(
                              onClick = {
                                /*TODO : edit member from list */
                              }) {
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
                    onClick = { /* TODO : add members to list : need to open listDialog... call other function?? */
                              editMember = User("uid", "", Role())},
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("addMember")) {
                      Icon(Icons.Default.Add, contentDescription = "Add members")
                      Text("Add members")
                    }
                Button( // TODO : should be disabled if no member/name empty ?
                    onClick = { viewmodel.saveAsso() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create")) {
                      Text("Create")
                    }
              }
        }

      // open dialog to edit member
      if (editMember.uid != "") {
          Dialog(onDismissRequest = { editMember = User() }) {
              ElevatedCard {
                  // temporary UI to see if dialog opens
                  Column(
                      modifier = Modifier
                          .padding(16.dp)
                          .fillMaxWidth(),
                      horizontalAlignment = Alignment.CenterHorizontally) {
                      Text("Edit member")
                      // TODO : add fields to edit member
                  }
              }
          }
      }

      }
}

