package com.github.se.assocify.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.MainNavigationBar
import com.github.se.assocify.ui.composables.PhotoSelectionSheet

/**
 * Profile screen that displays the user's information, a way to change your current association and
 * settings : personal settings (theme, privacy/security, notifications) which lead to other pages
 * and association's settings (if admin).
 *
 * @param navActions: NavigationActions object that contains the navigation actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navActions: NavigationActions, viewmodel: ProfileViewModel) {
  // for 'change association' part
  val listAsso = listOf("Association1", "Association2", "Association3")
  var expanded by remember { mutableStateOf(false) }
  var selectedText by remember { mutableStateOf(listAsso[0]) }

  val state by viewmodel.uiState.collectAsState()

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
        modifier =
            Modifier.padding(innerPadding).verticalScroll(rememberScrollState()).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
          Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)) {

                // profile picture

                if (state.profileImageURI != null) {
                  AsyncImage(
                      modifier =
                          Modifier.size(80.dp)
                              .clip(CircleShape) // Clip the image to a circle shape
                              .aspectRatio(1f)
                              .clickable { viewmodel.showBottomSheet() }
                              .testTag("profilePicture"),
                      model = state.profileImageURI,
                      contentDescription = "profile picture",
                      contentScale = ContentScale.Crop)
                } else {
                  Image(
                      modifier =
                          Modifier.testTag("default profile icon").size(80.dp).clickable {
                            viewmodel.showBottomSheet()
                          },
                      imageVector = Icons.Outlined.AccountCircle,
                      contentDescription = "default profile icon")
                }

                // personal information (depends on current association)
                Column(modifier = Modifier.testTag("profileInfos").weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        state.myName,
                        modifier = Modifier.testTag("profileName").weight(1f),
                        style = MaterialTheme.typography.headlineSmall)

                    // edit name button
                    IconButton(
                        onClick = { viewmodel.modifyNameOpen() },
                        modifier = Modifier.testTag("editProfile")) {
                          Icon(
                              imageVector = Icons.Filled.Edit,
                              contentDescription = "Edit Profile Icon")
                        }
                  }

                  Text("Role", modifier = Modifier.testTag("profileRole"))
                }
              }

          // Change_association dropdown
          ExposedDropdownMenuBox(
              expanded = expanded,
              onExpandedChange = { expanded = !expanded },
              modifier =
                  Modifier.testTag("associationDropdown").align(Alignment.CenterHorizontally)) {
                OutlinedTextField(
                    value = selectedText,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                      ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.menuAnchor(),
                    leadingIcon = {
                      Icon(
                          imageVector = Icons.Default.People, // todo
                          contentDescription = "Association Logo")
                    })

                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                  listAsso.forEach { item -> // todo get asso from DB
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                          selectedText = item
                          expanded = false
                        },
                        leadingIcon = {
                          Icon(
                              imageVector = Icons.Default.People, // todo
                              contentDescription = "Association Logo")
                        },
                        modifier = Modifier.testTag("associationDropdownItem"))
                  }
                }
              }

          Text(text = "Settings", style = MaterialTheme.typography.titleMedium)

          Column(
              modifier =
                  Modifier.fillMaxWidth().testTag("settingsList").clip(RoundedCornerShape(12.dp))) {
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
                    modifier =
                        Modifier.testTag("Theme").clickable {
                          navActions.navigateTo(Destination.ProfileTheme)
                        })
                ListItem(
                    leadingContent = {
                      Icon(
                          imageVector = Icons.Default.Lock,
                          contentDescription = "manage roles icon")
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
                    modifier =
                        Modifier.testTag("Privacy").clickable {
                          navActions.navigateTo(Destination.ProfileSecurityPrivacy)
                        })
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
                    modifier =
                        Modifier.testTag("Notifications").clickable {
                          navActions.navigateTo(Destination.ProfileNotifications)
                        })
              }

          // The below part is association dependent, only available if you're an admin !
          Text(text = "Manage $selectedText", style = MaterialTheme.typography.titleMedium)

          Column(
              modifier =
                  Modifier.fillMaxWidth()
                      .testTag("manageAssociationList")
                      .clip(RoundedCornerShape(12.dp))) {
                ListItem(
                    leadingContent = {
                      Icon(
                          imageVector = Icons.Default.People,
                          contentDescription = "manage roles icon")
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

    // open dialog to edit member
    if (state.openEdit) {
      Dialog(onDismissRequest = { viewmodel.cancelModifyName() }) {
        ElevatedCard {
          Column(
              modifier = Modifier.padding(16.dp).fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = state.modifyingName,
                    onValueChange = { viewmodel.modifyName(it) },
                    label = { Text("Edit your name") },
                    modifier = Modifier.fillMaxWidth().testTag("editName"))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                      OutlinedButton(
                          onClick = { viewmodel.confirmModifyName() },
                          modifier =
                              Modifier.wrapContentSize()
                                  .weight(1f)
                                  .testTag("confirmModifyButton")) {
                            Text(text = "Confirm", textAlign = TextAlign.Center)
                          }
                    }
              }
        }
      }
    }

    PhotoSelectionSheet(
        visible = state.showPicOptions,
        hideSheet = { viewmodel.hideBottomSheet() },
        setImageUri = { viewmodel.setImage(it) },
        signalCameraPermissionDenied = { viewmodel.signalCameraPermissionDenied() })
  }
}
