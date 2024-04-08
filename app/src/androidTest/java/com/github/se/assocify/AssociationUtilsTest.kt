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
import org.mockito.Mockito
import org.mockito.Mockito.mock

class AssociationUtilsTest {
  private lateinit var db: FirebaseFirestore
  private lateinit var assoApi: AssociationAPI
  private val documentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
  private val documentReference = Mockito.mock(DocumentReference::class.java)
  private val collectionReference = Mockito.mock(CollectionReference::class.java)
  private val president = User("testId", "Carlo", Role("president"))
  private val newUser = User()
  val oldAsso =
      Association(
          "aId",
          "cassify",
          "a cool association",
          "31/09/2005",
          "active",
          listOf(president),
          emptyList())
  val oldAssoUpdated =
      Association(
          "aId",
          "cassify",
          "a cool association",
          "31/09/2005",
          "active",
          listOf(president, newUser),
          emptyList())
  val oldAssoReviewed =
      Association(
          "aId",
          "cassify",
          "a cool association",
          "31/09/2005",
          "active",
          listOf(president, User("", "", Role("newRole"))),
          emptyList())

  @Before
  fun setup() {
    db = Mockito.mock(FirebaseFirestore::class.java)
    assoApi = AssociationAPI(db)
  }

  @Test
  fun checkThatEmptyAssocWorksWell() {
    val assoUtilsNewUser = AssociationUtils(newUser, associationDatabase = assoApi)
    assert(assoUtilsNewUser.getPendingUsers() == emptyList<User>())
    assert(assoUtilsNewUser.getRecordedUsers() == emptyList<User>())
    assert(assoUtilsNewUser.getEvents() == emptyList<User>())
    assert(assoUtilsNewUser.getCreationDate() == "")
    assert(assoUtilsNewUser.getAssociationName() == "")
  }

  @Test
  fun checkAcceptNewUser() {
    Mockito.`when`(documentSnapshot.exists()).thenReturn(true)
    Mockito.`when`(documentSnapshot.toObject(Association::class.java)).thenReturn(oldAssoUpdated)
    Mockito.`when`(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))

    val task = Tasks.forResult(documentSnapshot)
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(Mockito.mock())
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)
    val assocUtilsUpdated = AssociationUtils(president, oldAssoUpdated.uid, assoApi)
    val pendingUsers = assocUtilsUpdated.getPendingUsers()
    assert(pendingUsers == listOf(newUser))

    Mockito.`when`(db.collection(Mockito.any())).thenReturn(collectionReference)
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)
    Mockito.`when`(documentReference.set(Mockito.any())).thenReturn(Tasks.forResult(null))

    assocUtilsUpdated.acceptNewUser(newUser.uid, "newRole")
    Mockito.verify(db.collection(assoApi.collectionName).document(oldAssoUpdated.uid))
        .set(oldAssoReviewed)
  }

  @Test
  fun checkRequestAssocEntry() {
    Mockito.`when`(documentSnapshot.exists()).thenReturn(true)
    Mockito.`when`(documentSnapshot.toObject(Association::class.java)).thenReturn(oldAsso)
    Mockito.`when`(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))

    val task = Tasks.forResult(documentSnapshot)
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(Mockito.mock())
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)
    val newUserUtils = AssociationUtils(newUser, oldAsso.uid, assoApi)

    Mockito.`when`(db.collection(Mockito.any())).thenReturn(collectionReference)
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)
    Mockito.`when`(documentReference.set(Mockito.any())).thenReturn(Tasks.forResult(null))

    newUserUtils.requestAssociationAccess()
    Mockito.verify(db.collection(assoApi.collectionName).document(oldAssoUpdated.uid))
        .set(oldAssoUpdated)
  }

  @Test
  fun checkCreateNewAssociation() {
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(collectionReference)
    Mockito.`when`(db.collection(Mockito.any()).document()).thenReturn(documentReference)
    Mockito.`when`(documentReference.id).thenReturn("testId")
    Mockito.`when`(documentReference.set(Mockito.any())).thenReturn(Tasks.forResult(null))

    val newAssoUtils = AssociationUtils(president, associationDatabase = assoApi)
    val name = "pollify"
    val description = "a small association to have fun"
    val creationDate = "12/06/1987"
    val status = "open"
    val newAsso =
        Association("testId", name, description, creationDate, status, emptyList(), emptyList())

    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)
    Mockito.`when`(documentReference.set(Mockito.any())).thenReturn(Tasks.forResult(null))
    newAssoUtils.createNewAssoc(name, description, creationDate, status, emptyList(), emptyList())
    Mockito.verify(db.collection(assoApi.collectionName).document(newAsso.uid)).set(newAsso)
  }
}
