package com.github.se.assocify

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.FirebaseApi
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class FirebaseTest {

  @Mock private lateinit var db: FirebaseFirestore
  private val documentReference = Mockito.mock(DocumentReference::class.java)
  private lateinit var assoAPI: FirebaseApi
  private val collectionReference = Mockito.mock(CollectionReference::class.java)
  private val uid = "testId"

  @Before
  fun setup() {
    db = Mockito.mock(FirebaseFirestore::class.java)
    assoAPI = AssociationAPI(db)
  }

  @Test
  fun testGetNewId() {
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(Mockito.mock())
    Mockito.`when`(db.collection(Mockito.any()).document()).thenReturn(documentReference)
    Mockito.`when`(documentReference.id).thenReturn(uid)
    val result = assoAPI.getNewId()
    Mockito.verify(db).collection(assoAPI.collectionName)
    Mockito.verify(db.collection(assoAPI.collectionName)).document()
    Assert.assertEquals(uid, result)
  }

  @Test
  fun testDelete() {
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(collectionReference)
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()).delete())
        .thenReturn(Tasks.forResult(null))
    Tasks.await(assoAPI.delete(uid))
    Mockito.verify(db).collection(assoAPI.collectionName)
    Mockito.verify(db.collection(assoAPI.collectionName)).document(uid)
    Mockito.verify(db.collection(assoAPI.collectionName).document(uid)).delete()
  }
}
