package com.github.se.assocify.ui.screens.profile.members

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.entities.AssociationMember
import com.github.se.assocify.model.entities.RoleType
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

  fun onEditMember(member: AssociationMember) {
    _uiState.value = _uiState.value.copy(updatingMember = member, showEditMemberDialog = true)
  }

  fun onDeleteMember(member: AssociationMember) {
    _uiState.value = _uiState.value.copy(updatingMember = member, showDeleteMemberDialog = true)
  }

  fun onEditMemberDialogDismiss() {
    _uiState.value = _uiState.value.copy(showEditMemberDialog = false, updatingMember = null)
  }

  fun onDeleteMemberDialogDismiss() {
    _uiState.value = _uiState.value.copy(showDeleteMemberDialog = false, updatingMember = null)
  }

  fun confirmDeleteMember() {
    _uiState.value = _uiState.value.copy(showDeleteMemberDialog = false, updatingMember = null)
  }

  fun updateRole(newRole: RoleType) {
    _uiState.value = _uiState.value.copy(newRole = newRole)
  }

  fun confirmEditMember() {
    // update the member role in DB
    _uiState.value =
        _uiState.value.copy(showEditMemberDialog = false, updatingMember = null, newRole = null)
  }
}

data class ProfileMembersUIState(
    val currMembers: List<AssociationMember> = emptyList(),
    val updatingMember: AssociationMember? = null,
    val newRole: RoleType? = null,
    val showEditMemberDialog: Boolean = false,
    val showDeleteMemberDialog: Boolean = false,
)
