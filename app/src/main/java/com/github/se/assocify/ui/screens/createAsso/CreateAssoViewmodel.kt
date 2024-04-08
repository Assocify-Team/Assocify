package com.github.se.assocify.ui.screens.createAsso

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CreateAssoViewmodel(userList: List<User>) : ViewModel() {
    private val _uiState = MutableStateFlow(userList)
    val uiState: StateFlow<List<User>> = _uiState

}
