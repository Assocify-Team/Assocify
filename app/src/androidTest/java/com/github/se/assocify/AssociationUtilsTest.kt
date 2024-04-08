package com.github.se.assocify

import com.github.se.assocify.model.associations.AssociationUtils
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

class AssociationUtilsTest {
  @Mock private lateinit var db: FirebaseFirestore

  private lateinit var assoUtilsPresident: AssociationUtils
  private lateinit var assoUtilsNewUser: AssociationUtils
  private val documentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
  private val documentReference = Mockito.mock(DocumentReference::class.java)
  private val collectionReference = Mockito.mock(CollectionReference::class.java)
  private val president = User("testId", "Carlo", Role("president"))
  val oldAsso =
      Association(
          "aId",
          "cassify",
          "a cool association",
          "31/09/2005",
          "active",
          listOf(president),
          emptyList())
  private val newUser = User()

    /*
  @Before
  fun setup() {

  }

  @Test
  fun checkThatEmptyAssocWorksWell() {
    assert(assoUtilsNewUser.getPendingUsers() == emptyList<User>())
    assert(assoUtilsNewUser.getRecordedUsers() == emptyList<User>())
    assert(assoUtilsNewUser.getEvents() == emptyList<User>())
    assert(assoUtilsNewUser.getCreationDate() == "")
    assert(assoUtilsNewUser.getAssociationName() == "")
  }

  @Test
  fun checkThatCreateAssocWorksWell() {
      assoUtilsPresident =
          AssociationUtils(
              user = president, assocId = oldAsso.uid, associationDatabase = AssociationAPI(db))
      assoUtilsNewUser = AssociationUtils(user = newUser, associationDatabase = AssociationAPI(db))
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(collectionReference)
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)
    Mockito.`when`(documentReference.set(oldAsso)).thenReturn(Tasks.forResult(null))

    val newAsso =
        assoUtilsNewUser.createNewAssoc("b", "desc", "date", "status", listOf(newUser), emptyList())
    val api = AssociationAPI(db)
    assert(assoUtilsNewUser.getAllUsers().isNotEmpty())
    Mockito.verify(db).collection(api.collectionName)
    Mockito.verify(db.collection(api.collectionName)).document(newAsso.uid)
    Mockito.verify(db.collection(api.collectionName).document(newAsso.uid)).set(newAsso)
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
    val i1 = assocViewModel.getAssocId()
    assocViewModel.createNewAssoc("b", description, creationDate, status, users, events)
    val i2 = assocViewModel.getAssocId()
    assocViewModel.createNewAssoc("c", description, creationDate, status, users, events)
    val i3 = assocViewModel.getAssocId()
    assocViewModel.createNewAssoc("d", description, creationDate, status, users, events)
    val i4 = assocViewModel.getAssocId()
    assocViewModel.createNewAssoc(
        "testTheAssoc", "a sample testing description", creationDate, status, users, events)
    val i = assocViewModel.getAllAssociations().size
    assert(assocViewModel.getAllAssociations().size == 5)
    assert(
        assocViewModel.getFilteredAssociations("testTheAssoc")[0].description ==
            "a sample testing description")
    assocViewModel.deleteAssoc()
  }
   */
}
