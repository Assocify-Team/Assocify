package com.github.se.assocify.ui.screens.profile

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.Icon
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
import com.github.se.assocify.ui.util.SnackbarSystem
import com.github.se.assocify.ui.util.SyncSystem
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

  private val snackbarSystem = SnackbarSystem(_uiState.value.snackbarHostState)

  private val loadSystem =
      SyncSystem(
          { _uiState.value = _uiState.value.copy(loading = false, refresh = false, error = null) },
          { error ->
            _uiState.value = _uiState.value.copy(loading = false, refresh = false, error = error)
          })

  private val refreshSystem =
      SyncSystem(
          { loadProfile() },
          { error ->
            _uiState.value = _uiState.value.copy(refresh = false)
            snackbarSystem.showSnackbar(error)
          })

  init {
    loadProfile()
  }

  /**
   * This function is used to load the profile of the user. It gets the user's name, associations
   * and the current association. It also gets the user's role in the association.
   */
  fun loadProfile() {

    if (!loadSystem.start(4)) return

    _uiState.value = _uiState.value.copy(loading = true, error = null)

    userAPI.getUser(
        CurrentUser.userUid!!,
        { user ->
          _uiState.value = _uiState.value.copy(myName = user.name, modifyingName = user.name)
          loadSystem.end()
        },
        { loadSystem.end("Error loading profile") })
    userAPI.getCurrentUserAssociations(
        { associations ->
          _uiState.value =
              _uiState.value.copy(
                  myAssociations =
                      associations.map {
                        DropdownOption(it.name, it.uid)
                        /* TODO fetch association logo, else by default :*/
                        {
                          Icon(
                              imageVector = Icons.Default.People,
                              contentDescription = "Association Logo")
                        }
                      } + _uiState.value.defaultJoinAsso)
          loadSystem.end()
        },
        {
          _uiState.value = _uiState.value.copy(myAssociations = emptyList())
          loadSystem.end("Error loading your associations")
        })
    assoAPI.getAssociation(
        CurrentUser.associationUid!!,
        { association ->
          _uiState.value =
              _uiState.value.copy(
                  selectedAssociation =
                      DropdownOption(association.name, association.uid)
                      /* TODO fetch association logo */
                      {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Association Logo")
                      })
          loadSystem.end()
        },
        {
          if (_uiState.value.myAssociations.isNotEmpty()) {
            _uiState.value =
                _uiState.value.copy(selectedAssociation = _uiState.value.myAssociations[0])
          }
          loadSystem.end("Error loading current association")
        })
    userAPI.getCurrentUserRole(
        { role ->
          _uiState.value = _uiState.value.copy(currentRole = role)
          setRoleCapacities()
          loadSystem.end()
        },
        { loadSystem.end("Error loading role") })

    // This one is separate from the main loading system because it's not critical,
    // therefore it doesn't need to block the screen in loading state
    userAPI.getProfilePicture(
        "${CurrentUser.userUid!!}.jpg",
        { uri -> _uiState.value = _uiState.value.copy(profileImageURI = uri) },
        { snackbarSystem.showSnackbar("Error loading profile picture") })
  }

  fun refreshProfile() {
    if (!refreshSystem.start(2)) return
    _uiState.value = _uiState.value.copy(refresh = true)

    assoAPI.updateCache({ refreshSystem.end() }, { refreshSystem.end("Could not refresh") })
    userAPI.updateUserCache({ refreshSystem.end() }, { refreshSystem.end("Could not refresh") })
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
   * This function is used to set the association of the user. It goes to selectAssociation screen
   * if the user wants to join an other association.
   *
   * @param association the association
   */
  fun setAssociation(association: DropdownOption) {
    if (association.uid == "join") {
      navActions.navigateTo(Destination.SelectAsso)
      return
    }
    val oldAssociationUid = CurrentUser.associationUid
    CurrentUser.associationUid = association.uid
      assoAPI.getLogo(
          CurrentUser.associationUid!!,
          { uri -> CurrentUser.setAssociationLogo(uri) },
          { CurrentUser.setAssociationLogo(null) })
    userAPI.getCurrentUserRole(
        { role ->
          _uiState.value = _uiState.value.copy(selectedAssociation = association)
          _uiState.value = _uiState.value.copy(currentRole = role)
          setRoleCapacities()
        },
        {
          CurrentUser.associationUid = oldAssociationUid
          snackbarSystem.showSnackbar("Couldn't switch association")
        })

  }

  /**
   * This function is used to set the role capacities of the user. It sets the user as an admin if
   * they are a president, treasurer or committee of the association.
   */
  private fun setRoleCapacities() {
    when (_uiState.value.currentRole.type) {
      RoleType.PRESIDENCY -> _uiState.value = _uiState.value.copy(isAdmin = true)
      RoleType.TREASURY -> _uiState.value = _uiState.value.copy(isAdmin = true)
      RoleType.COMMITTEE -> _uiState.value = _uiState.value.copy(isAdmin = true)
      RoleType.STAFF -> _uiState.value = _uiState.value.copy(isAdmin = false)
      RoleType.MEMBER -> _uiState.value = _uiState.value.copy(isAdmin = false)
    }
  }

  /**
   * This function is used to confirm the name change of the user. It updates the user's name in the
   * database. It shows a snackbar if the name change was successful or not.
   */
  fun confirmModifyName() {
    CurrentUser.userUid?.let { uid ->
      userAPI.setDisplayName(
          uid,
          _uiState.value.modifyingName,
          {
            _uiState.value =
                _uiState.value.copy(openEdit = false, myName = _uiState.value.modifyingName)
          },
          {
            _uiState.value = _uiState.value.copy(openEdit = false)
            snackbarSystem.showSnackbar("Couldn't change name")
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

    userAPI.setProfilePicture(
        "${CurrentUser.userUid!!}.jpg",
        uri,
        {},
        {
          CoroutineScope(Dispatchers.Main).launch {
            _uiState.value.snackbarHostState.showSnackbar(
                message = "Couldn't change profile picture", duration = SnackbarDuration.Short)
          }
        })
    _uiState.value = _uiState.value.copy(profileImageURI = uri)
  }

  /** This function is used to signal to the user that the camera permission was denied. */
  fun signalCameraPermissionDenied() {
    snackbarSystem.showSnackbar("Camera permission denied")
  }

  fun logout() {
    navActions.onLogout()
  }
}

data class ProfileUIState(
    // whether the screen in loading
    val loading: Boolean = false,
    // the error message, if any
    val error: String? = null,
    // whether the profile is being refreshed
    val refresh: Boolean = false,
    // true if the user is an admin, false if not
    val isAdmin: Boolean = false,
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
    // default dropdown option is join an association
    val defaultJoinAsso: DropdownOption =
        DropdownOption("Add association", "join") {
          Icon(
              imageVector = Icons.Default.GroupAdd,
              contentDescription = "Join an other association")
        },
    // the associations of the user
    val myAssociations: List<DropdownOption> = listOf(defaultJoinAsso),
    // true if the association dropdown should be shown, false if should be hidden
    val openAssociationDropdown: Boolean = false,
    // the selected (current) association
    val selectedAssociation: DropdownOption = defaultJoinAsso,
    // current role of the user in the association
    val currentRole: PermissionRole =
        PermissionRole(CurrentUser.userUid!!, CurrentUser.associationUid!!, RoleType.MEMBER)
)

/**
 * This enum class is used to represent the settings of the user. Contains a function to get the
 * icon corresponding to the setting.
 */
enum class MySettings {
  Preferences;

  fun getIcon(): ImageVector {
    return when (this) {
      Preferences -> Icons.Default.LightMode
    }
  }

  fun getDestination(): Destination {
    return when (this) {
      Preferences -> Destination.ProfilePreferences
    }
  }
}

/**
 * This enum class is used to represent the settings manageable of the association. Contains a
 * function to get the icon corresponding to the setting.
 */
enum class AssociationSettings {
  Members,
  TreasuryTags,
  Events;

  fun getIcon(): ImageVector {
    return when (this) {
      Members -> Icons.Outlined.People
      TreasuryTags -> Icons.Outlined.Savings
      Events -> Icons.Outlined.Event
    }
  }

  override fun toString(): String {
    return when (this) {
      Members -> "Members"
      TreasuryTags -> "Treasury Tags"
      Events -> "Events"
    }
  }

  fun getDestination(): Destination {
    return when (this) {
      Members -> Destination.ProfileMembers
      TreasuryTags -> Destination.ProfileTreasuryTags
      Events -> Destination.ProfileEvents
    }
  }
}
