package com.github.se.assocify.ui.screens.profile.members

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.AssociationMember
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileMembersViewModel(navActions: NavigationActions, private val associationAPI: AssociationAPI, private val userAPI: UserAPI) :
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
    _uiState.value =
        _uiState.value.copy(
            updatingMember = member, showEditMemberDialog = true, newRole = member.role.type)
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
      userAPI.removeUserFromAssociation(_uiState.value.updatingMember!!.user.uid, CurrentUser.associationUid!!,
          { _uiState.value = _uiState.value.copy(showDeleteMemberDialog = false, updatingMember = null) },
          { Log.e("members", "Error removing user from association : ${it.message}") })
  }

  fun updateRole(newRole: RoleType) {
    _uiState.value = _uiState.value.copy(newRole = newRole)
  }

  fun confirmEditMember() {
    userAPI.changeRoleOfUser(
        _uiState.value.updatingMember!!.user.uid,
        CurrentUser.associationUid!!,
        _uiState.value.newRole!!,
        { _uiState.value = _uiState.value.copy(showEditMemberDialog = false, updatingMember = null) },
        { Log.e("members", "Error changing role of user : ${it.message}") })
  }
}

data class ProfileMembersUIState(
    val currMembers: List<AssociationMember> = emptyList(),
    val updatingMember: AssociationMember? = null,
    val newRole: RoleType? = null,
    val showEditMemberDialog: Boolean = false,
    val showDeleteMemberDialog: Boolean = false,
)
