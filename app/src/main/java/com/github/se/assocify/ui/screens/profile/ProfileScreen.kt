package com.github.se.assocify.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.github.se.assocify.ui.composables.DropdownOption
import com.github.se.assocify.ui.composables.DropdownWithSetOptions
import com.github.se.assocify.ui.composables.MainNavigationBar
import com.github.se.assocify.ui.composables.MainTopBar
import com.github.se.assocify.ui.composables.PhotoSelectionSheet

/**
 * Profile screen that displays the user's information, a way to change your current association and
 * settings : personal settings (preferences, privacy/security, notifications) which lead to other
 * pages and association's settings (if admin).
 *
 * @param navActions: NavigationActions object that contains the navigation actions.
 * @param viewmodel: ProfileViewModel object that contains the logic of the profile screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navActions: NavigationActions, viewmodel: ProfileViewModel) {
  val state by viewmodel.uiState.collectAsState()

  Scaffold(
      modifier = Modifier.testTag("profileScreen"),
      bottomBar = {
        MainNavigationBar(
            onTabSelect = { navActions.navigateToMainTab(it) },
            tabList = MAIN_TABS_LIST,
            selectedTab = Destination.Profile)
      },
      topBar = { MainTopBar(title = "Profile", optInSearchBar = false) },
      contentWindowInsets = WindowInsets(20.dp, 10.dp, 20.dp, 20.dp),
      snackbarHost = {
        SnackbarHost(
            hostState = state.snackbarHostState,
            snackbar = { snackbarData -> Snackbar(snackbarData = snackbarData) })
      }) { innerPadding ->
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
                                  .clickable { viewmodel.controlBottomSheet(true) }
                                  .testTag("profilePicture"),
                          model = state.profileImageURI,
                          contentDescription = "profile picture",
                          contentScale = ContentScale.Crop)
                    } else {
                      IconButton(
                          modifier = Modifier.testTag("default profile icon").size(80.dp),
                          onClick = { viewmodel.controlBottomSheet(true) }) {
                            Icon(
                                modifier = Modifier.fillMaxSize(),
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = "default profile icon")
                          }
                    }

                    // personal information : name and role (depends on current association)
                    Column(modifier = Modifier.testTag("profileInfos").weight(1f)) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            state.myName,
                            modifier = Modifier.testTag("profileName").weight(1f),
                            style = MaterialTheme.typography.headlineSmall)

                        // edit name button
                        IconButton(
                            onClick = { viewmodel.controlNameEdit(true) },
                            modifier = Modifier.testTag("editProfile")) {
                              Icon(
                                  imageVector = Icons.Filled.Edit,
                                  contentDescription = "Edit Profile Icon")
                            }
                      }

                      Text(state.currentRole.type.name, modifier = Modifier.testTag("profileRole"))
                    }
                  }

              // Change_association dropdown
              DropdownWithSetOptions(
                  options = state.myAssociations,
                  selectedOption =
                      DropdownOption(state.selectedAssociation.name, state.selectedAssociation.uid),
                  opened = state.openAssociationDropdown,
                  onOpenedChange = { viewmodel.controlAssociationDropdown(it) },
                  onSelectOption = { viewmodel.setAssociation(it) },
                  leadIcon = {
                    Icon(
                        imageVector = Icons.Default.People, contentDescription = "Association Logo")
                  },
                  modifier =
                      Modifier.testTag("associationDropdown").align(Alignment.CenterHorizontally))

              Text(text = "Settings", style = MaterialTheme.typography.titleMedium)

              Column(
                  modifier =
                      Modifier.fillMaxWidth()
                          .testTag("settingsList")
                          .clip(RoundedCornerShape(12.dp))) {
                    MySettings.entries.forEach { setting ->
                      ListItem(
                          leadingContent = {
                            Icon(
                                imageVector = setting.getIcon(),
                                contentDescription = "${setting.name} icon")
                          },
                          headlineContent = { Text(text = setting.name) },
                          trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                                contentDescription = "Go to ${setting.name} settings")
                          },
                          colors =
                              ListItemDefaults.colors(
                                  containerColor = MaterialTheme.colorScheme.primaryContainer),
                          modifier =
                              Modifier.testTag(setting.name).clickable {
                                navActions.navigateTo(setting.getDestination())
                              })
                    }
                  }

              // The below part is association dependent, only available if you're an admin !
              Text(
                  text = "Manage ${state.selectedAssociation.name}",
                  style = MaterialTheme.typography.titleMedium)

              Column(
                  modifier =
                      Modifier.fillMaxWidth()
                          .testTag("manageAssociationList")
                          .clip(RoundedCornerShape(12.dp))) {
                    AssociationSettings.entries.forEach { s ->
                      ListItem(
                          leadingContent = {
                            Icon(imageVector = s.getIcon(), contentDescription = "${s.name} icon")
                          },
                          headlineContent = { Text(text = s.name) },
                          trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                                contentDescription = "Go to ${s.name} settings")
                          },
                          colors =
                              ListItemDefaults.colors(
                                  containerColor = MaterialTheme.colorScheme.primaryContainer),
                          modifier =
                              Modifier.testTag(s.name).clickable {
                                navActions.navigateTo(s.getDestination())
                              })
                    }
                  }

              // log out button (for everyone)
              TextButton(
                  onClick = { viewmodel.logout() },
                  modifier = Modifier.fillMaxWidth().testTag("logoutButton"),
                  contentPadding = ButtonDefaults.TextButtonContentPadding,
                  colors =
                      ButtonDefaults.textButtonColors(
                          contentColor = MaterialTheme.colorScheme.error)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Log out Icon")
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(text = "Log out", textAlign = TextAlign.Center)
                  }
            }

        // open dialog to edit member
        if (state.openEdit) {
          Dialog(onDismissRequest = { viewmodel.controlNameEdit(false) }) {
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

        // open bottom sheet to select a (profile) picture
        PhotoSelectionSheet(
            visible = state.showPicOptions,
            hideSheet = { viewmodel.controlBottomSheet(false) },
            setImageUri = { viewmodel.setImage(it) },
            signalCameraPermissionDenied = { viewmodel.signalCameraPermissionDenied() })
      }
}
