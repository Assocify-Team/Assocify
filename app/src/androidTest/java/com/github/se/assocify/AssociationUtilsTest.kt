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
  private lateinit var assoApi: AssociationAPI

  @Before
  fun setup() {
    assoApi = Mockito.mock(AssociationAPI::class.java)
  }

  @Test
  fun checkThatEmptyAssocWorksWell() {
    assoUtilsNewUser = AssociationUtils(newUser, associationDatabase = assoApi)
    assert(assoUtilsNewUser.getPendingUsers() == emptyList<User>())
    assert(assoUtilsNewUser.getRecordedUsers() == emptyList<User>())
    assert(assoUtilsNewUser.getEvents() == emptyList<User>())
    assert(assoUtilsNewUser.getCreationDate() == "")
    assert(assoUtilsNewUser.getAssociationName() == "")
  }

  @Test
  fun checkRequestWorks() {
    // adding the value to the db
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(collectionReference)
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)
    Mockito.`when`(documentReference.set(oldAsso)).thenReturn(Tasks.forResult(null))
    assoApi.addAssociation(oldAsso)

    // adding the utils inside
    Mockito.`when`(documentSnapshot.exists()).thenReturn(true)
    Mockito.`when`(documentSnapshot.toObject(Association::class.java)).thenReturn(oldAsso)
    Mockito.`when`(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))

    val task = Tasks.forResult(documentSnapshot)
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(Mockito.mock())
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)
    assoUtilsPresident = AssociationUtils(president, oldAsso.uid, assoApi)
    assoUtilsNewUser = AssociationUtils(newUser, oldAsso.uid, assoApi)

    // adding the
    assoUtilsNewUser.requestAssociationAccess()

    Mockito.verify(db).collection(assoApi.collectionName)
    Mockito.verify(db.collection(assoApi.collectionName)).document(oldAsso.uid)
    Mockito.verify(db.collection(assoApi.collectionName).document(oldAsso.uid)).set(oldAssoUpdated)
    assert(assoUtilsPresident.getPendingUsers() == listOf(newUser))
  }
}
