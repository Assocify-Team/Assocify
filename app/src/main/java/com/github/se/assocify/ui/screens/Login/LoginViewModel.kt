package com.github.se.assocify.ui.screens.Login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow

/** The ViewModel for the login screen */
class LoginViewModel(private val userAPI: UserAPI) : ViewModel() {
  private val _uiState = MutableStateFlow(UserUIState())

  /** Updates the userId of the UI state */
   fun updateUser() {
    userAPI.getAllUsers(
        { users: List<User> ->
          val user = users.find { it.uid == getCurrentUser()!!.uid }
          if (user != null) {
            _uiState.value = UserUIState(user, true)
          } else {
            _uiState.value = UserUIState(User(getCurrentUser()!!.uid, getCurrentUser()!!.displayName!!, Role()), false)
          }
        },
        { exception -> Log.e("LoginViewModel", "Failed to get users: ${exception.message}")})
  }

  /** Gets the current user id */
  fun getCurrentUser(): FirebaseUser?{
    return Firebase.auth.currentUser!!
  }

  /** Gets the current user name */
    fun getCurrentUserName(): String {
        return Firebase.auth.currentUser!!.displayName!!
    }

  /** Adds a user to the database */
  fun addUser(user: User) {
    userAPI.addUser(
      user,
      onSuccess = {},
      onFailure = { exception ->
        Log.e("LoginViewModel", "Failed to add user: ${exception.message}")
      })
  }

  /** Returns true if the current user exists in the database and if they not, add them to database*/
  fun existUserId(): Boolean {
    val exist = _uiState.value.exists
    val user = _uiState.value.user
    if (!exist) {
      addUser(user)
    }
    return exist
  }
}

/** The UI state of the user */
data class UserUIState(val user: User = User(), val exists: Boolean = false)
