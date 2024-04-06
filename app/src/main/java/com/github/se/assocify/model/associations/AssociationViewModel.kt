package com.github.se.assocify.model.associations

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow

class AssociationViewModel(private var user: User, private var assocId: String) : ViewModel() {
  val _associationState: MutableStateFlow<Association?> = MutableStateFlow(null)
  private val associationDatabase = AssociationAPI(Firebase.firestore)

  init {
    updateAssoc()
  }

  private fun updateAssoc() {
    _associationState.value = associationDatabase.getAssociation(assocId)
  }

  fun getPendingUsers(): List<User> {
    if (_associationState.value == null) {
      return emptyList()
    }
    return _associationState.value!!.members.filter { x -> x.role == Role.PENDING_MEMBER }
  }

  fun getRecordedUsers(): List<User> {
    if (_associationState.value == null) {
      return emptyList()
    }
    return _associationState.value!!.members.filter { x -> x.role != Role.PENDING_MEMBER }
  }

  fun getAssociationName(): String {
    if (_associationState.value == null) return ""
    return _associationState.value!!.name
  }

  fun getAssociationDescription(): String {
    if (_associationState.value == null) return ""
    return _associationState.value!!.description
  }

  fun getCreationDate(): String {
    if (_associationState.value == null) return ""
    return _associationState.value!!.creationDate
  }

  fun getEvents(): List<Event> {
    if (_associationState.value == null) return emptyList()
    return _associationState.value!!.events
  }

  fun acceptUser(uid: String) {
    if (_associationState.value != null && (user.role == Role.CO_PRESIDENT
      || user.role == Role.PRESIDENT)
    ) {
      val userList = getPendingUsers().filter { u -> u.uid == uid }
      if (userList.isEmpty()) return
      val user = userList[0]
      val updatedUser = User(uid, user.name, Role.MEMBER)
      val ass = _associationState.value!!
      val updatedAssoc = Association(ass.uid,
        ass.name,
        ass.description,
        ass.creationDate,
        ass.status,
        ass.members.filter { us -> us.uid != uid } + updatedUser,
        ass.events)
      associationDatabase.addAssociation(updatedAssoc)
    }
  }
}
