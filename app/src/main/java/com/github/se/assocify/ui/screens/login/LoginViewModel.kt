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
class LoginViewModel(private val navActions: NavigationActions, private val userAPI: UserAPI) :
    ViewModel() {

  /** Updates the userId of the UI state */
  fun updateUser(info: UserInfo, googleUser: GoogleSignInAccount) {
    CurrentUser.userUid = info.id

    userAPI.getAllUsers(
        { users: List<User> ->
          val user = users.find { it.uid == info.id }
          if (user == null) {
            val newUser =
                User(info.id, googleUser.displayName ?: googleUser.email!!, googleUser.email!!)
            addUser(newUser)
            navActions.onLogin(false)
          } else {
            userAPI.getCurrentUserAssociations(
                { associations ->
                  if (associations.isEmpty()) {
                    navActions.onLogin(false)
                  } else {
                    CurrentUser.associationUid = associations.first().uid
                    navActions.onLogin(true)
                  }
                },
                { exception -> Log.e("Login", "Failed to get associations: ${exception.message}") })
          }
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
