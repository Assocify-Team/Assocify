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

  fun modifyNameOpen() {
    _uiState.value = _uiState.value.copy(openEdit = true)
  }

  fun modifyName(name: String) {
    _uiState.value = _uiState.value.copy(modifyingName = name)
  }

  fun cancelModifyName() {
    _uiState.value = _uiState.value.copy(openEdit = false)
  }

  fun confirmModifyName() {
    _uiState.value = _uiState.value.copy(openEdit = false)
    _uiState.value = _uiState.value.copy(myName = _uiState.value.modifyingName)
    // change in DB : unsure if this works
    CurrentUser.userUid?.let { uid ->
      userAPI.getUser(
          uid,
          { user -> userAPI.addUser(user.copy(name = _uiState.value.modifyingName), {}, {}) },
          {})
    }
  }

  fun showBottomSheet() {
    _uiState.value = _uiState.value.copy(showPicOptions = true)
  }

  fun hideBottomSheet() {
    _uiState.value = _uiState.value.copy(showPicOptions = false)
  }

  fun setImage(uri: Uri?) {
    if (uri == null) return
    _uiState.value = _uiState.value.copy(profileImageURI = uri)
  }

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
