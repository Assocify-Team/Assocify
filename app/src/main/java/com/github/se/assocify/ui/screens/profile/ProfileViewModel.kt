package com.github.se.assocify.ui.screens.profile

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.PermissionRole
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.DropdownOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * This ViewModel is used to manage the UI state of the profile screen. It is used to get the user's
 * name, associations, and the current association. It is also used to modify the user's name and
 * association. It will be used to manage the navigations between the different settings screens.
 *
 * @property assoAPI the association API
 * @property userAPI the user API
 * @property navActions the navigation actions
 */
class ProfileViewModel(
    private val assoAPI: AssociationAPI,
    private val userAPI: UserAPI,
    private val navActions: NavigationActions
) : ViewModel() {
  private val _uiState = MutableStateFlow(ProfileUIState())
  val uiState: StateFlow<ProfileUIState> = _uiState

  init {
    userAPI.getUser(
        CurrentUser.userUid!!,
        { user ->
          _uiState.value = _uiState.value.copy(myName = user.name, modifyingName = user.name)
        },
        { _uiState.value = _uiState.value.copy(myName = "name not found") })
    userAPI.getCurrentUserAssociations(
        { associations ->
          _uiState.value =
              _uiState.value.copy(
                  myAssociations = associations.map { DropdownOption(it.name, it.uid) })
        },
        { _uiState.value = _uiState.value.copy(myAssociations = emptyList()) })

    assoAPI.getAssociation(
        CurrentUser.associationUid!!,
        { association ->
          _uiState.value =
              _uiState.value.copy(
                  selectedAssociation = DropdownOption(association.name, association.uid))
        },
        {
          _uiState.value =
              _uiState.value.copy(selectedAssociation = _uiState.value.myAssociations[0])
        })
    userAPI.getCurrentUserRole(
        { role -> _uiState.value = _uiState.value.copy(currentRole = role) }, {})
  }

  /**
   * This function is used to modify the name of the (current) user as they're editing it.
   *
   * @param name the new name of the user
   */
  fun modifyName(name: String) {
    _uiState.value = _uiState.value.copy(modifyingName = name)
  }

  /**
   * This function is used to control the visibility of the name edit field.
   *
   * @param show true if the name edit field should be shown, false if should be hidden
   */
  fun controlNameEdit(show: Boolean) {
    _uiState.value = _uiState.value.copy(openEdit = show)
  }

  /**
   * This function is used to control the visibility of the association dropdown.
   *
   * @param show true if the association dropdown should be shown, false if should be hidden
   */
  fun controlAssociationDropdown(show: Boolean) {
    _uiState.value = _uiState.value.copy(openAssociationDropdown = show)
  }

  /**
   * This function is used to set the association of the user.
   *
   * @param association the association
   */
  fun setAssociation(association: DropdownOption) {
    CurrentUser.associationUid = association.uid
    _uiState.value = _uiState.value.copy(selectedAssociation = association)
    userAPI.getCurrentUserRole(
        { role -> _uiState.value = _uiState.value.copy(currentRole = role) }, {})
  }

  /**
   * This function is used to confirm the name change of the user. It updates the user's name in the
   * database. It shows a snackbar if the name change was successful or not.
   */
  fun confirmModifyName() {
    _uiState.value = _uiState.value.copy(openEdit = false, myName = _uiState.value.modifyingName)
    CurrentUser.userUid?.let { uid ->
      userAPI.setDisplayName(
          uid,
          _uiState.value.modifyingName,
          {
            CoroutineScope(Dispatchers.Main).launch {
              _uiState.value.snackbarHostState.showSnackbar(
                  message = "Name changed !", duration = SnackbarDuration.Short)
            }
          },
          {
            CoroutineScope(Dispatchers.Main).launch {
              _uiState.value.snackbarHostState.showSnackbar(
                  message = "Couldn't change name", duration = SnackbarDuration.Short)
            }
          })
    }
  }

  /**
   * This function is used to control the visibility of the bottom sheet.
   *
   * @param show true if the bottom sheet should be shown, false if should be hidden
   */
  fun controlBottomSheet(show: Boolean) {
    _uiState.value = _uiState.value.copy(showPicOptions = show)
  }

  /**
   * This function is used to set the profile image of the user.
   *
   * @param uri the uri of the image
   */
  fun setImage(uri: Uri?) {
    if (uri == null) return
    _uiState.value = _uiState.value.copy(profileImageURI = uri)
  }

  /** This function is used to signal to the user that the camera permission was denied. */
  fun signalCameraPermissionDenied() {
    CoroutineScope(Dispatchers.Main).launch {
      _uiState.value.snackbarHostState.showSnackbar(
          message = "Camera permission denied", duration = SnackbarDuration.Short)
    }
  }

  fun logout() {
    navActions.onLogout()
  }
}

data class ProfileUIState(
    // the name of the user
    val myName: String = "",
    // the name of the user as they're editing it
    val modifyingName: String = myName,
    // true if the name edit field should be shown, false if should be hidden
    val openEdit: Boolean = false,
    // the snackbar host state
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    // true if the bottom sheet should be shown, false if should be hidden
    val showPicOptions: Boolean = false,
    // the uri of the profile image
    val profileImageURI: Uri? = null,
    // the associations of the user
    val myAssociations: List<DropdownOption> = emptyList(),
    // true if the association dropdown should be shown, false if should be hidden
    val openAssociationDropdown: Boolean = false,
    // the selected (current) association - TODO idk what to do with the temporary association
    val selectedAssociation: DropdownOption = DropdownOption("", "temp"),
    // current role of the user in the association
    val currentRole: PermissionRole =
        PermissionRole(CurrentUser.userUid!!, CurrentUser.associationUid!!, RoleType.MEMBER)
)

/**
 * This enum class is used to represent the settings of the user. Contains a function to get the
 * icon corresponding to the setting.
 */
enum class MySettings {
  Preferences,
  Privacy,
  Notifications;

  fun getIcon(): ImageVector {
    return when (this) {
      Preferences -> Icons.Default.LightMode
      Privacy -> Icons.Default.Lock
      Notifications -> Icons.Default.Notifications
    }
  }

  fun getDestination(): Destination {
    return when (this) {
      Preferences -> Destination.ProfilePreferences
      Privacy -> Destination.ProfileSecurityPrivacy
      Notifications -> Destination.ProfileNotifications
    }
  }
}

/**
 * This enum class is used to represent the settings manageable of the association. Contains a
 * function to get the icon corresponding to the setting.
 */
enum class AssociationSettings {
  Members,
  Roles;

  fun getIcon(): ImageVector {
    return when (this) {
      Members -> Icons.Default.People
      Roles -> Icons.Default.ManageAccounts
    }
  }

  fun getDestination(): Destination {
    return when (this) {
      Members -> Destination.ProfileMembers
      Roles -> Destination.ProfileRoles
    }
  }
}
