package com.github.se.assocify

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class UserAPITest {

  @Mock private lateinit var db: FirebaseFirestore

  private lateinit var userAPI: UserAPI
  private val documentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
  private val documentReference = Mockito.mock(DocumentReference::class.java)
  private val collectionReference = Mockito.mock(CollectionReference::class.java)
  private val user = User("testId", "testName", Role("testId"))

  @Before
  fun setup() {
    db = Mockito.mock(FirebaseFirestore::class.java)
    userAPI = UserAPI(db)
  }

  @Test
  fun testGetUser() {

    Mockito.`when`(documentSnapshot.exists()).thenReturn(true)
    Mockito.`when`(documentSnapshot.toObject(User::class.java)).thenReturn(user)
    Mockito.`when`(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))

    Tasks.forResult(documentSnapshot)
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(Mockito.mock())
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)
    var result: User? = null
    Tasks.await(userAPI.getUser(user.uid) { user: User -> result = user })

    Mockito.verify(db).collection(userAPI.collectionName)
    Mockito.verify(db.collection(userAPI.collectionName)).document(user.uid)
    Mockito.verify(db.collection(userAPI.collectionName).document(user.uid)).get()
    assert(result == user)
  }

  @Test
  fun testGetAllUsers() {
    Mockito.`when`(documentSnapshot.exists()).thenReturn(true)
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(collectionReference)
    val query = Tasks.forResult(Mockito.mock(QuerySnapshot::class.java))
    Mockito.`when`(db.collection(Mockito.any()).get()).thenReturn(query)

    Mockito.`when`(query.result!!.documents).thenReturn(listOf(documentSnapshot))
    Mockito.`when`(listOf(documentSnapshot).map { document -> document.toObject(User::class.java) })
        .thenReturn(listOf(user))
    Mockito.`when`(documentSnapshot.toObject(User::class.java)).thenReturn(user)
    var result: List<User>? = null
    Tasks.await(userAPI.getAllUsers { users: List<User> -> result = users })
    Mockito.verify(db).collection(userAPI.collectionName)
    Mockito.verify(db.collection(userAPI.collectionName)).get()
    assert(result?.size == 1)
    assert(result?.get(0) == user)
  }

  @Test
  fun testAddUser() {
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(collectionReference)
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)
    Mockito.`when`(documentReference.set(user)).thenReturn(Tasks.forResult(null))
    Tasks.await(userAPI.addUser(user))
    Mockito.verify(db).collection(userAPI.collectionName)
    Mockito.verify(db.collection(userAPI.collectionName)).document(user.uid)
    Mockito.verify(db.collection(userAPI.collectionName).document(user.uid)).set(user)
  }

  @Test
  fun testDeleteUser() {
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(collectionReference)
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)
    Mockito.`when`(documentReference.delete()).thenReturn(Tasks.forResult(null))
    Tasks.await(userAPI.deleteUser(user.uid))
    Mockito.verify(db).collection(userAPI.collectionName)
    Mockito.verify(db.collection(userAPI.collectionName)).document(user.uid)
    Mockito.verify(db.collection(userAPI.collectionName).document(user.uid)).delete()
  }
}
