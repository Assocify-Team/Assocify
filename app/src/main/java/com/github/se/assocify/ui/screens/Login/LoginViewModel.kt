package com.github.se.assocify.ui.screens.Login

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow

/** The ViewModel for the login screen */
class LoginViewModel(private val userAPI: UserAPI) : ViewModel() {
  private val _uiState = MutableStateFlow(UserUIState(""))
  /** Constructor of this class */
  init {
    updateUser()
  }

  /** Updates the userId of the UI state */
  private fun updateUser() {
    userAPI.getAllUsers(
        { users: List<User> ->
          val user = users.find { it.uid == getCurrentUserId() }
          if (user != null) {
            _uiState.value = UserUIState(user.uid)
          } else {
            _uiState.value = UserUIState("")
          }
        },
        { _uiState.value = UserUIState("") })
  }

  /** Gets the current user id */
  fun getCurrentUserId(): String {
    return Firebase.auth.currentUser!!.uid
  }

  /** Returns true if the current user exists in the database */
  fun existUserId(): Boolean {
    return _uiState.value.userId != ""
  }
}

/** The UI state of the user */
data class UserUIState(val userId: String = "")
