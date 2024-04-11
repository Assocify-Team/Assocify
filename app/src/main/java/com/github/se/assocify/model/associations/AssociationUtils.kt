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

  private fun update() {
    if (assocId != "") _associationState = associationDatabase.getAssociation(assocId)
  }

  fun getAssocId(): String {
    return assocId
  }

  fun getPendingUsers(): List<User> {
    update()
    if (_associationState == null) {
      return emptyList()
    }
    return _associationState!!.getMembers().filter { x -> x.getRole() == Role("pending") }
  }

  fun getRecordedUsers(): List<User> {
    update()
    if (_associationState == null) {
      return emptyList()
    }
    return _associationState!!.getMembers().filter { x -> x.getRole() != Role("pending") }
  }

  fun getAllUsers(): List<User> {
    update()
    if (_associationState == null) {
      return emptyList()
    }
    return _associationState!!.getMembers()
  }

  fun getAssociationName(): String {
    if (_associationState == null) return ""
    return _associationState!!.getName()
  }

  fun getAssociationDescription(): String {
    if (_associationState == null) return ""
    return _associationState!!.getDescription()
  }

  fun getCreationDate(): String {
    if (_associationState == null) return ""
    return _associationState!!.getCreationDate()
  }

  fun getEvents(): List<Event> {
    if (_associationState == null) return emptyList()
    return _associationState!!.getEvents()
  }

  fun acceptNewUser(uid: String, role: String) {
    if (_associationState != null &&
        (user.getRole() == Role("president") || user.getRole() == Role("co-president"))) {
      val userList = getPendingUsers().filter { u -> u.uid == uid }
      if (userList.isEmpty()) return
      val user = userList[0]
      val updatedUser = User(uid, user.getName(), Role(role))
      val ass = _associationState!!
      val updatedAssoc =
          Association(
              ass.uid,
              ass.getName(),
              ass.getDescription(),
              ass.getCreationDate(),
              ass.getStatus(),
              ass.getMembers().filter { us -> us.uid != uid } + updatedUser,
              ass.getEvents())
      associationDatabase.addAssociation(updatedAssoc)
    }
  }

  fun requestAssociationAccess() {
    if (_associationState != null) {
      val ass = _associationState!!
      val updatedAssoc =
          Association(
              ass.uid,
              ass.getName(),
              ass.getDescription(),
              ass.getCreationDate(),
              ass.getStatus(),
              ass.getMembers() + User(user.uid, user.getName(), Role("pending")),
              ass.getEvents())
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
    if (user.getRole() != Role("president")) return
    associationDatabase.deleteAssociation(assocId)
    assocId = ""
    _associationState = null
  }

  private fun getAllAssociations(): List<Association> {
    if (_associationState == null) return emptyList()
    return associationDatabase.getAssociations()
  }

  fun getFilteredAssociations(searchQuery: String): List<Association> {
    return getAllAssociations().filter { ass ->
      ass.getName() == searchQuery || ass.getDescription() == searchQuery
    }
  }
}
