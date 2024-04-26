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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val assoAPI: AssociationAPI, private val userAPI: UserAPI) :
    ViewModel() {
  private val _uiState = MutableStateFlow(ProfileUIState())
  val uiState: StateFlow<ProfileUIState> = _uiState

  init {
    userAPI.getUser(
        CurrentUser.userUid!!,
        { user -> _uiState.value = _uiState.value.copy(myName = user.name) },
        { _uiState.value = _uiState.value.copy(myName = "name not found") })
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
   * This function is used to confirm the name change of the user. It updates the user's name in the
   * database. It shows a snackbar if the name change was successful or not.
   */
  fun confirmModifyName() {
    _uiState.value = _uiState.value.copy(openEdit = false, myName = _uiState.value.modifyingName)
    CurrentUser.userUid?.let { uid ->
      userAPI.getUser(
          uid,
          { user ->
            userAPI.addUser(
                user.copy(name = _uiState.value.modifyingName),
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
          },
          {
            CoroutineScope(Dispatchers.Main).launch {
              _uiState.value.snackbarHostState.showSnackbar(
                  message = "Current user not found", duration = SnackbarDuration.Short)
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

  /** This function is used to singal to the user that the camera permission was denied. */
  fun signalCameraPermissionDenied() {
    CoroutineScope(Dispatchers.Main).launch {
      _uiState.value.snackbarHostState.showSnackbar(
          message = "Camera permission denied", duration = SnackbarDuration.Short)
    }
  }
}

data class ProfileUIState(
    val myName: String = "",
    val modifyingName: String = myName,
    val openEdit: Boolean = false,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val showPicOptions: Boolean = false,
    val profileImageURI: Uri? = null
)

/**
 * This enum class is used to represent the settings of the user. Contains a function to get the
 * icon corresponding to the setting.
 */
enum class MySettings {
  Theme,
  Privacy,
  Notifications;

  fun getIcon(): ImageVector {
    return when (this) {
      Theme -> Icons.Default.LightMode
      Privacy -> Icons.Default.Lock
      Notifications -> Icons.Default.Notifications
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
}
