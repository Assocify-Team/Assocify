package com.github.se.assocify.ui.screens.Login

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel(private val userAPI: UserAPI) : ViewModel() {
  private val _uiState = MutableStateFlow(UserUIState())
  val uiState: StateFlow<UserUIState> = _uiState

  init {
    updateUser()
  }

  fun updateUser() {
    userAPI.getAllUsers { users: List<User> -> _uiState.value = UserUIState(users = users) }
    // userAPI.getUser(Firebase.auth.currentUser!!.uid){user: User -> _uiState.value =
    // UserUIState(user = user)}
  }

  fun getCurrentUserId(): String {
    return Firebase.auth.currentUser!!.uid
  }

  fun existUserId(): Boolean {
    val currentUserId = getCurrentUserId()
    return _uiState.value.users.any { user -> user.uid == currentUserId }
  }
}

data class UserUIState(val users: List<User> = listOf())
// data class UserUIState(val user: User = User())
