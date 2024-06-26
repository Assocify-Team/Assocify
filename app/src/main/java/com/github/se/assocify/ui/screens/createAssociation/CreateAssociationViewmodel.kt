package com.github.se.assocify.ui.screens.createAssociation

import android.net.Uri
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.AssociationMember
import com.github.se.assocify.model.entities.PermissionRole
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.util.SnackbarSystem
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateAssociationViewmodel(
    private val assoAPI: AssociationAPI,
    private val userAPI: UserAPI,
    private val navActions: NavigationActions,
) : ViewModel() {
  private val _uiState = MutableStateFlow(CreateAssoUIState())
  val uiState: StateFlow<CreateAssoUIState> = _uiState

  private val snackbarSystem = SnackbarSystem(uiState.value.snackbarHostState)

  private var association =
      Association(UUID.randomUUID().toString(), _uiState.value.name, "", LocalDate.now())
  private val roles =
      RoleType.entries.associateWith {
        PermissionRole(UUID.randomUUID().toString(), association.uid, it)
      }

  /*
   * Sets the name of the association : can be any string
   */
  fun setName(name: String) {
    association = association.copy(name = name.trim())
    _uiState.value = _uiState.value.copy(name = name)
    if (assoAPI.associationNameValid(name)) {
      _uiState.value = _uiState.value.copy(nameError = null)
    } else {
      val error = if (name.isBlank()) "Name is required" else "Name is invalid or already used"
      _uiState.value = _uiState.value.copy(nameError = error)
    }
    updateSavable()
  }

  /*
   * Utility function : Sorts the members by role then name
   */
  private fun sortMembers(memberList: List<AssociationMember>): List<AssociationMember> {
    return memberList.sortedWith(compareBy({ it.role.type }, { it.user.name }))
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
          sortMembers(_uiState.value.members.filter { user -> user.user.uid != it.user.uid } + it)
      _uiState.value =
          _uiState.value.copy(members = memberList, openEdit = false, editMember = null)
      updateSavable()
    }
  }

  /*
   * Removes a member from the list of members
   */
  fun removeMember(member: AssociationMember) {
    _uiState.value =
        _uiState.value.copy(
            openEdit = false, editMember = null, members = _uiState.value.members - member)
    updateSavable()
  }

  /*
   * Opens the edit member dialog when modifying a member
   */
  fun modifyMember(member: AssociationMember) {
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
    _uiState.value = _uiState.value.copy(searchMember = searchMember)

    userAPI.getAllUsers(
        onSuccess = { userList ->
          _uiState.value =
              _uiState.value.copy(
                  searchMemberList =
                      userList
                          .filterNot { user ->
                            _uiState.value.members.any { us -> us.user.uid == user.uid }
                          }
                          .filter { it.name.lowercase().contains(searchMember.lowercase()) }
                          .take(4))
          if (_uiState.value.searchMemberList.isEmpty()) {
            _uiState.value = _uiState.value.copy(memberError = "No users found")
          } else {
            _uiState.value = _uiState.value.copy(memberError = null)
          }
        },
        onFailure = { exception ->
          Log.e("CreateAssoViewModel", "Failed to get users:${exception.message}")
        })
  }

  /*
   * Selects a member from the search list
   */
  fun selectMember(member: User) {
    roles[RoleType.MEMBER]?.let {
      val associationMember = AssociationMember(member, association, it)
      _uiState.value =
          _uiState.value.copy(
              editMember = associationMember, searchMember = "", searchMemberList = listOf())
    }
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
  fun modifyMemberRole(role: RoleType) {
    _uiState.value.editMember?.let { member ->
      roles[role]?.let {
        _uiState.value = _uiState.value.copy(openEdit = true, editMember = member.copy(role = it))
      }
    }
  }

  /*
   * Updates the savable state of the association
   */
  private fun updateSavable() {
    _uiState.value =
        _uiState.value.copy(
            savable =
                (_uiState.value.members.any { member -> member.user.uid == CurrentUser.userUid }) &&
                    _uiState.value.nameError == null)
  }

  var currentlySaving = false
  /*
   * Saves the association in the database
   */
  fun saveAsso() {
    if (currentlySaving) return

    currentlySaving = true
    assoAPI.addAssociation(
        association,
        onSuccess = {
          assoAPI.initAssociation(
              roles.values,
              _uiState.value.members,
              {
                CurrentUser.associationUid = association.uid
                currentlySaving = false
                // Navigate in both cases, so that the error shows up in the profile
                userAPI.updateCurrentUserAssociationCache(
                    { navActions.goFromCreateAsso() }, { navActions.goFromCreateAsso() })
              },
              {}) // Exception not dealt with, to do in v1.1 (use server funcs, it's atomic)
          if (_uiState.value.imageUri != null) {
            assoAPI.setLogo(
                association.uid,
                _uiState.value.imageUri!!,
                {},
                { exception ->
                  // We might already have navigated away, there's no way to show a snackbar...
                  Log.e("CreateAssoViewModel", "Failed to set logo: ${exception.message}")
                })
          }
        },
        onFailure = { exception ->
          currentlySaving = false
          Log.e("CreateAssoViewModel", "Failed to add asso: ${exception.message}")
          snackbarSystem.showSnackbar("Failed to create association.", "Retry", this::saveAsso)
        })
  }

  fun controlBottomSheet(show: Boolean) {
    _uiState.value = _uiState.value.copy(showBottomSheet = show)
  }

  fun setLogo(image: Uri?) {
    if (image == null) return
    _uiState.value = _uiState.value.copy(imageUri = image)
  }

  fun signalCameraPermissionDenied() {
    CoroutineScope(Dispatchers.Main).launch {
      _uiState.value.snackbarHostState.showSnackbar(
          message = "Camera permission denied", duration = SnackbarDuration.Short)
    }
  }
}

data class CreateAssoUIState(
    val name: String = "",
    val members: List<AssociationMember> = listOf(), // list of the members of the association
    val openEdit: Boolean = false, // whether the edit member dialog is open
    val editMember: AssociationMember? = null, // the member being edited (in the dialog)
    val searchMember: String = "", // the name of the member being searched
    val searchMemberList: List<User> = listOf(), // the list of members found in the search
    val memberError: String? = null, // error message when no member is found
    val savable: Boolean =
        (members.any { member -> member.user.uid == CurrentUser.userUid }) &&
            name.isNotBlank(), // whether the association can be saved
    // the snackbar host state
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val nameError: String? = null, // error message when the name is invalid
    val showBottomSheet: Boolean = false, // whether the bottom sheet is shown
    val imageUri: Uri? = null // the image URI of the logo
)
