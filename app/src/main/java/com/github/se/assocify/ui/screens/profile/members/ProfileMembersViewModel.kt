package com.github.se.assocify.ui.screens.profile.members

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.AssociationMember
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.ui.util.SnackbarSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileMembersViewModel(
    private val associationAPI: AssociationAPI,
    private val userAPI: UserAPI
) : ViewModel() {
  private val _uiState = MutableStateFlow(ProfileMembersUIState())
  val uiState: StateFlow<ProfileMembersUIState> = _uiState

  val snackbarSystem = SnackbarSystem(_uiState.value.snackbarHostState)

  init {
    loadMembers()
  }

  private fun loadMembers() {
    associationAPI.getMembers(
        CurrentUser.associationUid!!,
        { members -> _uiState.value = _uiState.value.copy(currMembers = members, refresh = false) },
        {
          _uiState.value = _uiState.value.copy(refresh = false)
          snackbarSystem.showSnackbar("Error loading members")
        })
  }

  fun refreshMembers() {
    _uiState.value = _uiState.value.copy(refresh = true)
    associationAPI.updateCache(
        { loadMembers() },
        {
          _uiState.value = _uiState.value.copy(refresh = false)
          snackbarSystem.showSnackbar("Error refreshing members")
        })
  }

  fun onEditMember(member: AssociationMember) {
    _uiState.value =
        _uiState.value.copy(
            updatingMember = member, showEditMemberDialog = true, newRole = member.role.type)
  }

  fun onDeleteMember(member: AssociationMember) {
    if (member.user.uid == CurrentUser.userUid) {
      snackbarSystem.showSnackbar("You cannot remove yourself")
      return
    }
    _uiState.value = _uiState.value.copy(updatingMember = member, showDeleteMemberDialog = true)
  }

  fun onEditMemberDialogDismiss() {
    _uiState.value = _uiState.value.copy(showEditMemberDialog = false, updatingMember = null)
  }

  fun onDeleteMemberDialogDismiss() {
    _uiState.value = _uiState.value.copy(showDeleteMemberDialog = false, updatingMember = null)
  }

  fun confirmDeleteMember() {
    userAPI.removeUserFromAssociation(
        _uiState.value.updatingMember!!.user.uid,
        CurrentUser.associationUid!!,
        {
          associationAPI.updateCache(
              { loadMembers() }, { snackbarSystem.showSnackbar("Error loading members") })
          _uiState.value =
              _uiState.value.copy(showDeleteMemberDialog = false, updatingMember = null)
        },
        { snackbarSystem.showSnackbar("Could not remove member") })
  }

  fun updateRole(newRole: RoleType) {
    _uiState.value = _uiState.value.copy(newRole = newRole)
  }

  fun confirmEditMember() {
    userAPI.changeRoleOfUser(
        _uiState.value.updatingMember!!.user.uid,
        CurrentUser.associationUid!!,
        _uiState.value.newRole!!,
        {
          associationAPI.updateCache(
              { loadMembers() }, { snackbarSystem.showSnackbar("Error loading members") })
          _uiState.value = _uiState.value.copy(showEditMemberDialog = false, updatingMember = null)
        },
        { snackbarSystem.showSnackbar("Could not change role") })
  }
}

data class ProfileMembersUIState(
    val refresh: Boolean = false,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val currMembers: List<AssociationMember> = emptyList(),
    val updatingMember: AssociationMember? = null,
    val newRole: RoleType? = null,
    val showEditMemberDialog: Boolean = false,
    val showDeleteMemberDialog: Boolean = false,
)
