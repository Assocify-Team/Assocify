package com.github.se.assocify.ui.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.navigation.NavigationActions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

/** The ViewModel for the login screen */
class LoginViewModel(private val userAPI: UserAPI, private val navActions: NavigationActions) :
    ViewModel() {

  /** Updates the userId of the UI state */
  fun updateUser() {
    CurrentUser.userUid = getCurrentUser()!!.uid
    CurrentUser.associationUid = "associationUid"
    userAPI.getAllUsers(
        { users: List<User> ->
          val user = users.find { it.uid == getCurrentUser()!!.uid }
          if (user == null) {
            addUser(User(getCurrentUser()!!.uid, getCurrentUser()!!.displayName!!, Role()))
          }
          navActions.onLogin(user != null)
        },
        { exception -> Log.e("LoginViewModel", "Failed to get users: ${exception.message}") })
  }

  /** Gets the current user id */
  private fun getCurrentUser(): FirebaseUser? {
    return Firebase.auth.currentUser!!
  }

  /** Adds a user to the database */
  private fun addUser(user: User) {
    userAPI.addUser(
        user,
        onFailure = { exception ->
          Log.e("LoginViewModel", "Failed to add user: ${exception.message}")
        })
  }
}
