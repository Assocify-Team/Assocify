package com.github.se.assocify.ui.screens.Login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow

/** The ViewModel for the login screen */
class LoginViewModel(private val userAPI: UserAPI) : ViewModel() {
  private val _uiState = MutableStateFlow(UserUIState())
  /** Constructor of this class */
  init {
    updateUser()
  }

  /** Updates the UI state depending on the UI State */
  private fun updateUser() {
    userAPI.getAllUsers(
        { users: List<User> ->
          val user = users.find { it.uid == getCurrentUserId() }
          if (user != null) {
            _uiState.value = UserUIState(true)
          } else {
            _uiState.value = UserUIState(false)
          }
        },
        { _uiState.value = UserUIState(false) })
  }

  /** Gets the current user id */
  fun getCurrentUserId(): String {
    return Firebase.auth.currentUser!!.uid
  }

  /** Adds new user to db */
  fun addUser(user: User) {
    userAPI.addUser(
        user,
        onSuccess = {},
        onFailure = { exception ->
          Log.e("LoginViewModel", "Failed to add user: ${exception.message}")
        })
  }

  /** Returns true if the current user exists in the database and if they don't, add them to db */
  fun existUserId(): Boolean {
    val exists = _uiState.value.exist
    if (!exists) {
      val current = getCurrentUserId()
      addUser(User(getCurrentUserId(), Firebase.auth.currentUser!!.displayName!!, Role("pending")))
    }
    return exists
  }
}

/** The UI state: stores whether the user exists or not in the db */
data class UserUIState(val exist: Boolean = false)
