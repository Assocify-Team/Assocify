package com.github.se.assocify.ui.screens.createAsso

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun createAssoScreen() {
  // should maybe be in viewmodel or something
    val bigList = listOf(User("1", "jean", Role("com")), User("2", "roger", Role("tres")), User("1", "jean", Role("com")), User("1", "jean", Role("com")), User("1", "jean", Role("com")), User("1", "jean", Role("com")), User("1", "jean", Role("com")), User("1", "jean", Role("com")), User("2", "roger", Role("tres")),User("2", "roger", Role("tres")),User("2", "roger", Role("tres")), User("2", "roger", Role("tres")),User("2", "roger", Role("tres")),User("2", "roger", Role("tres")))
  val smallList = listOf(User("1", "jean", Role("com")), User("2", "roger", Role("tres")))
    var memberList by remember { mutableStateOf(bigList) }
  var name by remember { mutableStateOf("") }

  Scaffold(
      topBar = {
          TopAppBar(
              modifier = Modifier.fillMaxWidth(),
              navigationIcon = {
                  IconButton(onClick = { /*TODO : go back to previous screen*/ }) {
                      Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                  }
              },
              title = {
                  Text(text = "Create your association")
              }
          )
      },
      contentWindowInsets = WindowInsets(50.dp, 10.dp, 50.dp, 20.dp)
  ) { innerPadding ->
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
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedIconButton(
                modifier = Modifier.padding(top = 8.dp)/*.background(MaterialTheme.colorScheme.primary)*/,
                onClick = {
                    /* TODO : can add association logo */
                }) {
                Icon(Icons.Default.Person, contentDescription = "Logo")
            }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()/*.background(MaterialTheme.colorScheme.secondary)*/
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .testTag("MemberList"),
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally) {
            memberList.forEach { member ->
                item {
                    ListItem(
                        modifier =
                        Modifier
                            .testTag("MemberListItem")
                        ,
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        headlineContent = { Text(member.name) },
                        overlineContent = { Text(member.role.name) },
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
//        Spacer(Modifier.weight(0.5f))
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { /*TODO : add members to list*/}, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = "Add members")
                Text("Add members")
            }
            Button(onClick = { /*TODO : add asso to DB */}, modifier = Modifier.fillMaxWidth()) { Text("Create") }
        }
//        Spacer(Modifier.weight(0.1f))
    }
  }
}
