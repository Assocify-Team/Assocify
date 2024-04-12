package com.github.se.assocify
/*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.associations.AssociationUtils
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssociationUtilsTest {
  private lateinit var assoApi: AssociationAPI
  private val documentSnapshot = mockk<DocumentSnapshot>()
  private val documentReference = mockk<DocumentReference>()
  private val collectionReference = mockk<CollectionReference>()
  private val president = User("testId", "Carlo", Role("president"))
  private val newUser = User()
  private val oldAsso =
      Association(
          "aId",
          "cassify",
          "a cool association",
          "31/09/2005",
          "active",
          listOf(president),
          emptyList())
  private val oldAssoUpdated =
      Association(
          "aId",
          "cassify",
          "a cool association",
          "31/09/2005",
          "active",
          listOf(president, newUser),
          emptyList())
  private val oldAssoReviewed =
      Association(
          "aId",
          "cassify",
          "a cool association",
          "31/09/2005",
          "active",
          listOf(president, User("", "", Role("newRole"))),
          emptyList())
  private val u1 = User("a", "1", Role("president"))
  private val u2 = User("b", "2", Role("user"))
  private val u3 = User("c", "3", Role("user"))
  private val u4 = User("d", "4", Role("pending"))
  private val u5 = User("e", "5", Role("pending"))
  private val e1 = Event("s1", "e1", "", "", "", emptyList(), emptyList())
  private val e2 = Event("s2", "e2", "", "", "", emptyList(), emptyList())
  private val e3 = Event("s3", "e3", "", "", "", emptyList(), emptyList())
  private val getterAsso =
      Association(
          "getId",
          "gettify",
          "association to test the getters",
          "31/09/2005",
          "active",
          listOf(u1, u2, u3, u4, u5),
          listOf(e1, e2, e3))

  @Before
  fun setup() {
    assoApi = mockk<AssociationAPI>()
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
    var result: Association? = null
    every { assoApi.getAssociation(any<String>(), any<(Association) -> Unit>()) } answers
        {
          val callback = arg<(Association) -> Unit>(1)
          callback.invoke(oldAssoUpdated)
          result = oldAssoUpdated
          Tasks.forResult(null)
        }

    // Assert asso is upadted and newUser is in the list of members
    val assocUtilsUpdated = AssociationUtils(president, oldAssoUpdated.uid, assoApi)
    val pendingUsers = assocUtilsUpdated.getPendingUsers()
    assert(pendingUsers == listOf(newUser))
    every { assoApi.addAssociation(any<Association>()) } answers { Tasks.forResult(null) }
    assocUtilsUpdated.acceptNewUser(newUser.uid, "newRole")
    verify { assoApi.addAssociation(oldAssoReviewed) }
  }
  /*

  @Test
  fun checkRequestAssocEntry() {
    Mockito.`when`(documentSnapshot.exists()).thenReturn(true)
    Mockito.`when`(documentSnapshot.toObject(Association::class.java)).thenReturn(oldAsso)
    Mockito.`when`(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))

    Mockito.`when`(db.collection(Mockito.any())).thenReturn(mock())
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
    assert(newAssoUtils.getAssocId() == "testId")
    assert(newAssoUtils.getAssociationDescription() == "a small association to have fun")
    Mockito.verify(db.collection(assoApi.collectionName).document(newAsso.uid)).set(newAsso)
  }

  @Test
  fun checkGetters() {
    Mockito.`when`(documentSnapshot.exists()).thenReturn(true)
    Mockito.`when`(documentSnapshot.toObject(Association::class.java)).thenReturn(getterAsso)
    Mockito.`when`(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))

    Mockito.`when`(db.collection(Mockito.any())).thenReturn(mock())
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)

    val getterUtil = AssociationUtils(newUser, "getId", assoApi)
    assert(getterUtil.getAllUsers() == listOf(u1, u2, u3, u4, u5))
    assert(getterUtil.getAssociationDescription() == "association to test the getters")
    assert(getterUtil.getAssocId() == "getId")
    assert(getterUtil.getPendingUsers() == listOf(u4, u5))
    assert(getterUtil.getRecordedUsers() == listOf(u1, u2, u3))
    assert(getterUtil.getEvents() == listOf(e1, e2, e3))
    assert(getterUtil.getCreationDate() == "31/09/2005")
    assert(getterUtil.getAssociationName() == "gettify")
  }*/
}
*/
