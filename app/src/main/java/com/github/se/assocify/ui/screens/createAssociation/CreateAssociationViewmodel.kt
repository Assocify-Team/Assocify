package com.github.se.assocify.ui.screens.createAssociation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CreateAssociationViewmodel(
    private val assoAPI: AssociationAPI,
    private val userAPI: UserAPI
) : ViewModel() {
  private val _uiState = MutableStateFlow(CreateAssoUIState())
  val uiState: StateFlow<CreateAssoUIState> = _uiState

  /*
   * Sets the name of the association
   */
  fun setName(name: String) {
    // need input sanitization TODO
    _uiState.value = _uiState.value.copy(name = name)
  }

  /*
   * Utility function : Sorts the members by role then name
   */
  private fun sortMembers(memberList: List<User>): List<User> {
    return memberList.sortedWith(
        compareBy({ it.getRole().getRoleType().ordinal }, { it.getName() }))
  }

  /*
   * Opens the edit member dialog when adding a member
   */
  fun addMember() {
    _uiState.value = _uiState.value.copy(openEdit = true)
  }

  /*
   * Adds the editMember to the list of members in the list of members
   */
  fun addMemberToList() {
    _uiState.value.editMember?.let {
      // keep the list of members sorted by role then name + can't add a member already in the list
      val memberList =
          sortMembers(_uiState.value.members.filter { user -> user.uid != it.uid } + it)
      _uiState.value =
          _uiState.value.copy(members = memberList, openEdit = false, editMember = null)
    }
  }

  /*
   * Removes a member from the list of members
   */
  fun removeMember(member: User) {
    _uiState.value =
        _uiState.value.copy(
            openEdit = false, editMember = null, members = _uiState.value.members - member)
  }

  /*
   * Opens the edit member dialog when modifying a member
   */
  fun modifyMember(member: User) {
    _uiState.value = _uiState.value.copy(openEdit = true)
    _uiState.value = _uiState.value.copy(editMember = member)
  }

  /*
   * Cancels the modification of a member (on dismiss)
   */
  fun cancelModifyMember() {
    _uiState.value = _uiState.value.copy(openEdit = false)
    _uiState.value = _uiState.value.copy(editMember = null)
  }

  /*
   * Searches for member with corresponding name in the database
   */
  fun searchMember(searchMember: String) {
    _uiState.value = _uiState.value.copy(searchMember = searchMember) // ?
    // TODO
    if (searchMember.isNotBlank()) {
      userAPI.getAllUsers(
          onSuccess = { userList ->
            _uiState.value =
                _uiState.value.copy(
                    searchMemberList =
                        userList
                            .filterNot { user ->
                              _uiState.value.members.any { us -> us.uid == user.uid }
                            }
                            .filter { it.getName().lowercase().contains(searchMember.lowercase()) })
          },
          onFailure = { exception ->
            Log.e("CreateAssoViewModel", "Failed to get users:${exception.message}")
          })

      if (_uiState.value.searchMemberList.isEmpty()) {
        _uiState.value = _uiState.value.copy(memberError = "No users found")
      } else {
        _uiState.value = _uiState.value.copy(memberError = null)
      }
    } else { // idk
      _uiState.value = _uiState.value.copy(memberError = null)
    }
  }

  /*
   * Checks if there is an error in the search of member
   */
  fun searchError(): Boolean {
    return _uiState.value.memberError != null
  }

  /*
   * Selects a member from the search list
   */
  fun selectMember(member: User) {
    _uiState.value = _uiState.value.copy(editMember = member)
    _uiState.value = _uiState.value.copy(searchMember = "")
    _uiState.value = _uiState.value.copy(searchMemberList = listOf())
  }

  /*
   * Dismisses the member search dialog
   */
  fun dismissMemberSearch() {
    _uiState.value = _uiState.value.copy(editMember = null)
    _uiState.value = _uiState.value.copy(searchMember = "")
    _uiState.value = _uiState.value.copy(searchMemberList = listOf())
  }

  /*
   * Gives/removes role to the member
   */
  fun modifyMemberRole(role: String) {
    if (_uiState.value.editMember != null) {
      _uiState.value.editMember?.let {
        _uiState.value = _uiState.value.copy(editMember = it.toggleRole(role))
      }
    }
  }

  /*
   * Checks if the association can be saved : the current user is a member, all members have a role and the name of the association is not blank
   */
  fun canSaveAsso(): Boolean {
    return (_uiState.value.members.find { user -> user.uid == CurrentUser.userUid } != null) &&
        _uiState.value.members.all { it.getRole().getRoleType() != Role.RoleType.PENDING } &&
        _uiState.value.name.isNotBlank()
  }

  /*
   * Saves the association in the database
   */
  fun saveAsso() {
    // create asso today
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val asso =
        Association(
            assoAPI.getNewId(), _uiState.value.name, "", date, "", _uiState.value.members, listOf())
    assoAPI.addAssociation(
        asso,
        onSuccess = { /* navigate to home page with the new association as the current asso */},
        onFailure = { exception ->
          Log.e("CreateAssoViewModel", "Failed to add asso: ${exception.message}")
        })
  }
}

data class CreateAssoUIState(
    val name: String = "",
    val members: List<User> = listOf(),
    val openEdit: Boolean = false,
    val editMember: User? = null,
    val searchMember: String = "",
    val searchMemberList: List<User> = listOf(),
    val memberError: String? = null,
    // there should be a logo val but not implemented yet
)
