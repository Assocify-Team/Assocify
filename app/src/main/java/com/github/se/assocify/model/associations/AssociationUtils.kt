package com.github.se.assocify.model.associations

import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User

class AssociationUtils(
    private var user: User,
    private var assocId: String = "",
    private val associationDatabase: AssociationAPI
) {
  private var _associationState: Association? = null

  init {
    update()
  }

  fun update() {
    if (assocId != "")  associationDatabase.getAssociation(assocId){ assoc -> _associationState = assoc }
  }

  fun getAssocId(): String {
    return assocId
  }

  fun getPendingUsers(): List<User> {
    update()
    if (_associationState == null) {
      return emptyList()
    }
    return _associationState!!.members.filter { x -> x.role == Role("pending") }
  }

  fun getRecordedUsers(): List<User> {
    update()
    if (_associationState == null) {
      return emptyList()
    }
    return _associationState!!.members.filter { x -> x.role != Role("pending") }
  }

  fun getAllUsers(): List<User> {
    update()
    if (_associationState == null) {
      return emptyList()
    }
    return _associationState!!.members
  }

  fun getAssociationName(): String {
    if (_associationState == null) return ""
    return _associationState!!.name
  }

  fun getAssociationDescription(): String {
    if (_associationState == null) return ""
    return _associationState!!.description
  }

  fun getCreationDate(): String {
    if (_associationState == null) return ""
    return _associationState!!.creationDate
  }

  fun getEvents(): List<Event> {
    if (_associationState == null) return emptyList()
    return _associationState!!.events
  }

  fun acceptNewUser(uid: String, role: String) {
    if (_associationState != null &&
        (user.role == Role("president") || user.role == Role("co-president"))) {
      val userList = getPendingUsers().filter { u -> u.uid == uid }
      if (userList.isEmpty()) return
      val user = userList[0]
      val updatedUser = User(uid, user.name, Role(role))
      val ass = _associationState!!
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
    if (_associationState != null) {
      val ass = _associationState!!
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
  ): Association {
    val uid = associationDatabase.getNewId()
    val assoc = Association(uid, name, description, creationDate, status, members, events)
    associationDatabase.addAssociation(assoc)
    assocId = uid
    _associationState = assoc
    return assoc
  }

  fun deleteAssoc() {
    if (user.role != Role("president")) return
    associationDatabase.deleteAssociation(assocId)
    assocId = ""
    _associationState = null
  }

  fun getAllAssociations(): List<Association> {
    if (_associationState == null) return emptyList()
    var result : List<Association>? = null
    associationDatabase.getAssociations(){associations -> result = associations}
    
    return result!!
  }

  fun getFilteredAssociations(searchQuery: String): List<Association> {
    return getAllAssociations().filter { ass ->
      ass.name == searchQuery || ass.description == searchQuery
    }
  }
}
