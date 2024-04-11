package com.github.se.assocify.ui.screens.Login

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel(private val userAPI: UserAPI) : ViewModel() {
    private val _uiState = MutableStateFlow(UserUIState())
    val uiState: StateFlow<UserUIState> = _uiState

    private user
    init {
        updateUsers()
    }

    fun updateUsers(){
        userAPI.getAllUsers().addOnSuccessListener  { it ->
            _uiState.value = UserUIState(users = it)
        }
        _uiState.value = UserUIState()
    }
    fun getUser() {

    }
}

data class UserUIState(val users: List<User> = listOf())