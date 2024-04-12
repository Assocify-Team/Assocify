package com.github.se.assocify.model.associations

/*
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
    if (assocId != "")
        associationDatabase.getAssociation(assocId) { assoc -> _associationState = assoc }
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
    var result: List<Association>? = null
    associationDatabase.getAssociations() { associations -> result = associations }

    return result!!
  }

  fun getFilteredAssociations(searchQuery: String): List<Association> {
    return getAllAssociations().filter { ass ->
      ass.getName() == searchQuery || ass.getDescription() == searchQuery
    }
  }
}
*/
