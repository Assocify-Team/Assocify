package com.github.se.assocify.ui.screens.createAsso

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.assocify.model.entities.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun createAssoScreen() {
// should maybe be in viewmodel or something
  var memberList by remember { mutableStateOf(emptyList<User>()) }
  var name by remember { mutableStateOf("") }

  Scaffold(
      topBar = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Text(text = "Create your association !")
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                      /* TODO : can add association logo */
                    }) {
                      Icon(Icons.Default.Person, contentDescription = "Logo")
                    }
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                )
              }
        }
      },
      bottomBar = {
        OutlinedButton(
            onClick = { /*TODO : add members to list*/}, modifier = Modifier.fillMaxWidth()) {
              Icon(Icons.Default.Add, contentDescription = "Add members")
              Text("Add members")
            }
        Button(onClick = { /*TODO : add asso to DB */}) { Text("Create") }
      },
  ) { innerPadding ->
    LazyColumn(
        modifier = Modifier.testTag("TodoList").padding(innerPadding),
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally) {
          memberList.forEach { member ->
            item {
              ListItem(
                  modifier =
                      Modifier.testTag("MemberListItem")
                          .padding(start = 16.dp, end = 16.dp)
                          .background(MaterialTheme.colorScheme.background),
                  headlineContent = { Text(member.name) },
                  overlineContent = { Text(member.role.toString()) },
                  leadingContent = { Icon(Icons.Default.Person, contentDescription = "Person") },
                  trailingContent = {
                    IconButton(
                        onClick = {
                          /*TODO : edit member from list */
                        }) {
                          Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                  },
              )
              HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp))
            }
          }
        }
  }
}
