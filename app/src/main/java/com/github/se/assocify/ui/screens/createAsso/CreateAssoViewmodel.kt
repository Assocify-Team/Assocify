package com.github.se.assocify.ui.screens.createAsso

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CreateAssoViewmodel(currentUser: CurrentUser) : ViewModel() {
  private val _uiState = MutableStateFlow(CreateAssoUIState())
  val uiState: StateFlow<CreateAssoUIState> = _uiState

  private val assoAPI = AssociationAPI(db = Firebase.firestore)
  private val userAPI = UserAPI(db = Firebase.firestore)
  val currUser = currentUser.userUid

  // SHLAG POUR TEST
  private val bigList =
      listOf(
          User("testUserUid", "Jean1", Role("admin")),
          User("2", "Paul", Role("admin")),
          User("3", "Jacques", Role("admin")),
          User("4", "Marie", Role("admin")),
          User("5", "Jean5", Role("admin")))

  /*
   * Sets the name of the association
   */
  fun setName(name: String) {
    // need input sanitization TODO
    _uiState.value = _uiState.value.copy(name = name)
  }

  private fun sortMembers(memberList: List<User>): List<User> {
    return memberList.sortedWith(compareBy({ it.getRole().getRoleType().ordinal }, { it.getName() }))
  }

  /*
   * Opens the edit member dialog
   */
  fun addMember() {
    _uiState.value = _uiState.value.copy(openEdit = true)
  }

  /*
   * Adds the editMember to the list of members in the database
   */
  fun addMemberToList() {
    _uiState.value.editMember?.let {
        // keep the list of members sorted by role then name + can't add a member already in the list
        val memberList = sortMembers(_uiState.value.members.filter { user -> user.uid != it.uid } + it)
        _uiState.value = _uiState.value.copy(members = memberList, openEdit = false, editMember = null)
    }

  }

  fun removeMember(member: User) {
    _uiState.value = _uiState.value.copy(openEdit = false, editMember = null, members = _uiState.value.members - member)
  }

  // unsure if this is needed yet
  fun modifyMember(member: User) {
    // need input sanitization TODO
    _uiState.value = _uiState.value.copy(openEdit = true)
    _uiState.value = _uiState.value.copy(editMember = member)
  }

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
          // userAPI.getAllUsers { userList ->
          // SHLAG POUR TEST
          bigList.let { userList ->
              _uiState.value =
                  _uiState.value.copy(
                      searchMemberList =
                      userList
                          .filterNot { user -> _uiState.value.members.any { us -> us.uid == user.uid } }
                          .filter { it.getName().lowercase().contains(searchMember.lowercase()) })

              if (_uiState.value.searchMemberList.isEmpty()) {
                  _uiState.value = _uiState.value.copy(memberError = "No users found")
              } else {
                  _uiState.value = _uiState.value.copy(memberError = null)
              }
          }
      } else { //idk
          _uiState.value = _uiState.value.copy(memberError = null)
      }
  }

    fun searchError(): Boolean {
        return _uiState.value.memberError != null
    }


  /*
   * Selects a member from the search list
   */
  fun selectMember(member: User) {
    // TODO
    _uiState.value = _uiState.value.copy(editMember = member)
    _uiState.value = _uiState.value.copy(searchMember = "")
    _uiState.value = _uiState.value.copy(searchMemberList = listOf())
  }

  /*
   * Dismisses the member search dialog
   */
  fun dismissMemberSearch() {
    // TODO
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
              _uiState.value =
                  _uiState.value.copy(editMember = it.toggleRole(role))
          }
      }
  }

    fun canSaveAsso(): Boolean {
        return (_uiState.value.members.find({user -> user.uid == currUser}) != null) &&
        _uiState.value.members.all { it.getRole().getRoleType() != Role.RoleType.PENDING } &&
        _uiState.value.name.isNotBlank()

    }

  fun saveAsso() {
    // create asso today
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val asso =
        Association(
            assoAPI.getNewId(), _uiState.value.name, "", date, "", _uiState.value.members, listOf())
    assoAPI.addAssociation(asso, onSuccess = { println("Association added") }, onFailure = { println(it) })
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
