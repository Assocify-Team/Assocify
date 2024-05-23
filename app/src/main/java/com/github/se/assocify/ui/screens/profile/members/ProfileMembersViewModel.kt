package com.github.se.assocify.ui.screens.profile.members

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileMembersViewModel(navActions: NavigationActions, associationAPI: AssociationAPI) :
    ViewModel() {
  private val _uiState = MutableStateFlow(ProfileMembersUIState())
  val uiState: StateFlow<ProfileMembersUIState> = _uiState

  private val tempMemberList =
      listOf(
          User("1", "Sarah"),
          User("2", "John"),
          User("3", "Alice"),
          User("4", "Bob"),
          User("5", "Charlie"),
          User("6", "David"),
          User("7", "Eve"),
          User("8", "Frank"),
          User("9", "Grace"),
          User("10", "Hank"),
          User("11", "Ivy"),
      )

  init {
    associationAPI.getApplicants(
        CurrentUser.associationUid!!,
        { applicants -> _uiState.value = _uiState.value.copy(applicants = applicants) },
        { Log.e("members", "Error loading applicants") })
    // to debug with a big list :
    //    _uiState.value = _uiState.value.copy(applicants = tempMemberList)
    // not yet done in API : getMembers of association
    _uiState.value = _uiState.value.copy(currMembers = tempMemberList)
  }
}

data class ProfileMembersUIState(
    val currMembers: List<User> = emptyList(),
    val applicants: List<User> = emptyList()
)
