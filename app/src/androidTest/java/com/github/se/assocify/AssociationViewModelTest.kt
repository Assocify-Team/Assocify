package com.github.se.assocify

import com.github.se.assocify.model.associations.AssociationViewModel
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.junit.Test

class AssociationViewModelTest {

  @Test
  fun checkThatEmptyAssocWorksWell() {
    val db = AssociationAPI(Firebase.firestore)
    val user = User(db.getNewId(), "Carlo", Role("president"))
    val assocViewModel = AssociationViewModel(user)
    assert(assocViewModel.getPendingUsers() == emptyList<User>())
    assert(assocViewModel.getAllUsers() == emptyList<User>())
    assert(assocViewModel.getRecordedUsers() == emptyList<User>())
    assert(assocViewModel.getEvents() == emptyList<User>())
    assert(assocViewModel.getCreationDate() == "")
    assert(assocViewModel.getAssociationName() == "")
  }

  @Test
  fun checkThatCreateAssocWorksWell() {
    val db = AssociationAPI(Firebase.firestore)
    val user = User(db.getNewId(), "Carlo", Role("president"))
    val user2 = User(db.getNewId(), "Jonathan", Role("co-president"))
    val user3 = User(db.getNewId(), "Bigio", Role("pending"))
    val assocViewModel = AssociationViewModel(user)
    val name = "critify"
    val description = "A cool association to have fun with friends!!"
    val creationDate = "today"
    val status = "new"
    val users = listOf(user, user2, user3)
    val events = listOf(Event("a", "b", emptyList(), emptyList()))
    assocViewModel.createNewAssoc(name, description, creationDate, status, users, events)
    assert(assocViewModel.getPendingUsers() == listOf(user3))
    assert(assocViewModel.getAllUsers() == users)
    assert(assocViewModel.getRecordedUsers() == listOf(user, user2))
    assert(assocViewModel.getEvents() == events)
    assert(assocViewModel.getCreationDate() == creationDate)
    assert(assocViewModel.getAssociationName() == name)

    assocViewModel.deleteAssoc()
    assert(assocViewModel.getAllUsers() == emptyList<User>())
  }

  @Test
  fun checkThatAcceptationWorks() {
    val db = AssociationAPI(Firebase.firestore)
    val user = User(db.getNewId(), "Carlo", Role("president"))
    val user2 = User(db.getNewId(), "Jonathan", Role("co-president"))
    val user3 = User(db.getNewId(), "Bigio", Role("pending"))
    val assocViewModel = AssociationViewModel(user)
    val name = "critify"
    val description = "A cool association to have fun with friends!!"
    val creationDate = "today"
    val status = "new"
    val users = listOf(user, user2, user3)
    val events = listOf(Event("a", "b", emptyList(), emptyList()))
    assocViewModel.createNewAssoc(name, description, creationDate, status, users, events)
    assert(assocViewModel.getPendingUsers() == listOf(user3))
    assocViewModel.acceptNewUser(user3.uid, "captain")
    assert(assocViewModel.getPendingUsers() == emptyList<User>())
    val x = assocViewModel.getRecordedUsers()
    val newUser3 = User(user3.uid, user3.name, Role("captain"))
    assert(assocViewModel.getRecordedUsers() == listOf(user, user2, newUser3))
    assocViewModel.deleteAssoc()
  }

  @Test
  fun checkThatRequestWorks() {
    val db = AssociationAPI(Firebase.firestore)
    val user = User(db.getNewId(), "Carlo", Role("president"))
    val assocViewModel = AssociationViewModel(user)
    val name = "critify"
    val description = "A cool association to have fun with friends!!"
    val creationDate = "today"
    val status = "new"
    val users = listOf(user)
    val events = listOf(Event("a", "b", emptyList(), emptyList()))
    assocViewModel.createNewAssoc(name, description, creationDate, status, users, events)

    val newUser = User(db.getNewId(), "Bigio", Role("pending"))
    val inscriptionAssociationModel = AssociationViewModel(newUser, assocViewModel.getAssocId())
    inscriptionAssociationModel.requestAssociationAccess()
    assert(assocViewModel.getPendingUsers().contains(newUser))
    assocViewModel.deleteAssoc()
  }

  @Test
  fun checkThatFiltersWork() {
    val db = AssociationAPI(Firebase.firestore)
    val user = User(db.getNewId(), "Carlo", Role("president"))
    val assocViewModel = AssociationViewModel(user)
    val name = "critify"
    val description = "A cool association to have fun with friends!!"
    val creationDate = "today"
    val status = "new"
    val users = listOf(user)
    val events = listOf(Event("a", "b", emptyList(), emptyList()))
    assocViewModel.createNewAssoc("a", description, creationDate, status, users, events)
    assocViewModel.createNewAssoc("b", description, creationDate, status, users, events)
    assocViewModel.createNewAssoc("c", description, creationDate, status, users, events)
    assocViewModel.createNewAssoc("d", description, creationDate, status, users, events)
    assocViewModel.createNewAssoc(
        "testTheAssoc", "a sample testing description", creationDate, status, users, events)
    assert(assocViewModel.getAllAssociations().size == 5)
    assert(
        assocViewModel.getFilteredAssociations("testTheAssoc")[0].description ==
            "a sample testing description")
  }
}
