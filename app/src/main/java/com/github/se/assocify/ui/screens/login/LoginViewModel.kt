package com.github.se.assocify.ui.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.navigation.NavigationActions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.github.jan.supabase.gotrue.user.UserInfo

/** The ViewModel for the login screen */
class LoginViewModel(private val userAPI: UserAPI, private val navActions: NavigationActions) :
    ViewModel() {

  /** Updates the userId of the UI state */
  fun updateUser(info: UserInfo, googleUser: GoogleSignInAccount) {
    CurrentUser.userUid = info.id
    CurrentUser.associationUid = "associationUid"
    userAPI.getAllUsers(
        { users: List<User> ->
          val user = users.find { it.uid == info.id }
          if (user == null) {
            val newUser =
                User(info.id, googleUser.displayName ?: googleUser.email!!, googleUser.email!!)
            addUser(newUser)
            CurrentUser.user =
                User(info.id, googleUser.displayName ?: googleUser.email!!, googleUser.email!!)
          } else {
            CurrentUser.user = user
          }
          navActions.onLogin(user != null)
        },
        { exception -> Log.e("LoginViewModel", "Failed to get users: ${exception.message}") })
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
