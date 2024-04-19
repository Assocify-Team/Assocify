package com.github.se.assocify.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.MainNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("profileScreen"),
      bottomBar = {
        MainNavigationBar(
            onTabSelect = { navActions.navigateToMainTab(it) },
            tabList = MAIN_TABS_LIST,
            selectedTab = Destination.Profile)
      },
      topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Profile") },
            navigationIcon = {
              IconButton(onClick = { /* TODO onAssoClick ?? */}) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Association Profile Icon")
              }
            })
      },
      contentWindowInsets = WindowInsets(20.dp, 10.dp, 20.dp, 20.dp),
  ) { innerPadding ->
    Column(
        modifier = Modifier.padding(innerPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
          Row(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween) {

                // profile picture
                Text(text = "Profile Picture", modifier = Modifier.testTag("profilePicture"))

                Column {
                  Text("Name", modifier = Modifier.testTag("profileName"))
                  Text("Role", modifier = Modifier.testTag("profileRole"))
                }

                IconButton(
                    onClick = { /*TODO modify profile infos */},
                    modifier = Modifier.testTag("editProfile")) {
                      Icon(
                          imageVector = Icons.Filled.Edit, contentDescription = "Edit Profile Icon")
                    }
              }

          ExposedDropdownMenuBox(
              expanded = false /*expanded*/,
              onExpandedChange = {
                /*expanded = !expanded*/
              },
              modifier = Modifier.fillMaxWidth().testTag("associationDropdown")) {
                TextField(
                    value = "oui" /*selectedText*/,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                      ExposedDropdownMenuDefaults.TrailingIcon(expanded = false /*expanded*/)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth())

                ExposedDropdownMenu(
                    expanded = false /*expanded*/,
                    onDismissRequest = { /*expanded = false*/}) { /*
                                                                  coffeeDrinks.forEach { item ->
                                                                      DropdownMenuItem(
                                                                          text = { Text(text = item) },
                                                                          onClick = {
                                                                              selectedText = item
                                                                              expanded = false
                                                                              Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                                                          }
                                                                      )
                                                                  }*/}
              }

          Text(text = "Settings")

          Column {
            ListItem(
                leadingContent = {
                  Icon(
                      imageVector = Icons.Default.LightMode,
                      contentDescription = "manage roles icon")
                },
                headlineContent = { Text(text = "Theme") },
                trailingContent = {
                  Icon(
                      imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                      contentDescription = "Go to theme settings")
                },
                colors =
                    ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.testTag("Theme"))
            ListItem(
                leadingContent = {
                  Icon(imageVector = Icons.Default.Lock, contentDescription = "manage roles icon")
                },
                headlineContent = { Text(text = "Privacy/Security") },
                trailingContent = {
                  Icon(
                      imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                      contentDescription = "Go to privacy/security settings")
                },
                colors =
                    ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.testTag("Privacy"))
            ListItem(
                leadingContent = {
                  Icon(
                      imageVector = Icons.Default.Notifications,
                      contentDescription = "manage roles icon")
                },
                headlineContent = { Text(text = "Notifications") },
                trailingContent = {
                  Icon(
                      imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                      contentDescription = "Go to notification settings")
                },
                colors =
                    ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.testTag("Notifications"))
          }

          Text(text = "Manage ${"Association"}")

          Column {
            ListItem(
                leadingContent = {
                  Icon(imageVector = Icons.Default.People, contentDescription = "manage roles icon")
                },
                headlineContent = { Text(text = "Members") },
                trailingContent = {
                  Icon(
                      imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                      contentDescription = "Go to members settings")
                },
                colors =
                    ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.testTag("manageMembers"))
            ListItem(
                leadingContent = {
                  Icon(
                      imageVector = Icons.Default.ManageAccounts,
                      contentDescription = "manage roles icon")
                },
                headlineContent = { Text(text = "Roles") },
                trailingContent = {
                  Icon(
                      imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                      contentDescription = "Go to roles settings")
                },
                colors =
                    ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.testTag("manageRoles"))
          }

          TextButton(
              onClick = { /*TODO*/},
              modifier = Modifier.fillMaxWidth().testTag("logoutButton"),
              contentPadding = ButtonDefaults.TextButtonContentPadding,
              colors =
                  ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Log out Icon")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = "Log out", textAlign = TextAlign.Center)
              }
        }
  }
}
