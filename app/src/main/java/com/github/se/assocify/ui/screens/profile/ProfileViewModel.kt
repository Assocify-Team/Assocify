package com.github.se.assocify.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(private val assoAPI: AssociationAPI, private val userAPI: UserAPI) :
    ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState

    init {
        userAPI.getUser(CurrentUser.userUid!!, { user ->
            _uiState.value = _uiState.value.copy(myName = user.name)
        }, {_uiState.value = _uiState.value.copy(myName = "name not found")})
    }

    fun setName(name: String) {
    _uiState.value = _uiState.value.copy(myName = name)
    // change in DB : unsure if this works
    CurrentUser.userUid?.let { uid ->
        userAPI.getUser(uid, { user ->
            userAPI.addUser(user.copy(name = name), {}, {})
            }, {})
        }
    }
    }

data class ProfileUIState(
    val myName: String = "name",
)