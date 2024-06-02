package com.github.se.assocify.ui.screens.profile.members

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.entities.AssociationMember
import com.github.se.assocify.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileMembersViewModel(navActions: NavigationActions, associationAPI: AssociationAPI) :
    ViewModel() {
  private val _uiState = MutableStateFlow(ProfileMembersUIState())
  val uiState: StateFlow<ProfileMembersUIState> = _uiState

  init {
    associationAPI.getMembers(
        CurrentUser.associationUid!!,
        { members -> _uiState.value = _uiState.value.copy(currMembers = members) },
        { Log.e("members", "Error loading members : ${it.message}") })
  }
}

data class ProfileMembersUIState(
    val currMembers: List<AssociationMember> = emptyList(),
)
