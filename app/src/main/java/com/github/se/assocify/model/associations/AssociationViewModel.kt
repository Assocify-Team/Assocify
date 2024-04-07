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

class AssociationViewModel(private var user: User, private var assocId: String = "") : ViewModel() {
  private val _associationState: MutableStateFlow<Association?> = MutableStateFlow(null)
  private val associationDatabase = AssociationAPI(Firebase.firestore)

  init {
    update()
  }

  fun update() {
    if (assocId != "") _associationState.value = associationDatabase.getAssociation(assocId)
  }

  fun getAssocId(): String {
    return assocId
  }

  fun getPendingUsers(): List<User> {
    update()
    if (_associationState.value == null) {
      return emptyList()
    }
    return _associationState.value!!.members.filter { x -> x.role == Role("pending") }
  }

  fun getRecordedUsers(): List<User> {
    update()
    if (_associationState.value == null) {
      return emptyList()
    }
    return _associationState.value!!.members.filter { x -> x.role != Role("pending") }
  }

  fun getAllUsers(): List<User> {
    update()
    if (_associationState.value == null) {
      return emptyList()
    }
    return _associationState.value!!.members
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

  fun acceptNewUser(uid: String, role: String) {
    if (_associationState.value != null &&
        (user.role == Role("president") || user.role == Role("co-president"))) {
      val userList = getPendingUsers().filter { u -> u.uid == uid }
      if (userList.isEmpty()) return
      val user = userList[0]
      val updatedUser = User(uid, user.name, Role(role))
      val ass = _associationState.value!!
      val updatedAssoc =
          Association(
              ass.uid,
              ass.name,
              ass.description,
              ass.creationDate,
              ass.status,
              ass.members.filter { us -> us.uid != uid } + updatedUser,
              ass.events)
      associationDatabase.addAssociation(updatedAssoc)
    }
  }

  fun requestAssociationAccess() {
    if (_associationState.value != null) {
      val ass = _associationState.value!!
      val updatedAssoc =
          Association(
              ass.uid,
              ass.name,
              ass.description,
              ass.creationDate,
              ass.status,
              ass.members + User(user.uid, user.name, Role("pending")),
              ass.events)
      associationDatabase.addAssociation(updatedAssoc)
    }
  }

  fun createNewAssoc(
      name: String,
      description: String,
      creationDate: String,
      status: String,
      members: List<User>,
      events: List<Event>
  ) {
    val uid = associationDatabase.getNewId()
    val assoc = Association(uid, name, description, creationDate, status, members, events)
    associationDatabase.addAssociation(assoc)
    assocId = uid
    _associationState.value = assoc
  }

  fun deleteAssoc() {
      if(user.role != Role("president")) return
    associationDatabase.deleteAssociation(assocId)
    assocId = ""
    _associationState.value = null
  }

    fun getAllAssociations(): List<Association>{
        if (_associationState.value == null) return emptyList()
        return associationDatabase.getAssociations()
    }

    fun getFilteredAssociations(searchQuery: String): List<Association>{
        return getAllAssociations().filter { ass -> ass.name == searchQuery || ass.description == searchQuery}
    }
}
