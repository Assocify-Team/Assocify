package com.github.se.assocify

import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.entities.Association
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

class AssociationAPITest {

  @Mock private lateinit var db: FirebaseFirestore

  private lateinit var assoAPI: AssociationAPI
  private val documentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
  private val documentReference = Mockito.mock(DocumentReference::class.java)
  private val collectionReference = Mockito.mock(CollectionReference::class.java)
  private val asso =
      Association(
          "testId", "testName", "testDescription", "testDate", "testStatus", listOf(), listOf())

  @Before
  fun setup() {
    db = Mockito.mock(FirebaseFirestore::class.java)
    assoAPI = AssociationAPI(db)
  }

  @Test
  fun testGetAssociation() {

    Mockito.`when`(documentSnapshot.exists()).thenReturn(true)
    Mockito.`when`(documentSnapshot.toObject(Association::class.java)).thenReturn(asso)
    Mockito.`when`(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))

    val task = Tasks.forResult(documentSnapshot)
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(Mockito.mock())
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)
    val result = assoAPI.getAssociation(asso.uid)

    Mockito.verify(db).collection(assoAPI.collectionName)
    Mockito.verify(db.collection(assoAPI.collectionName)).document(asso.uid)
    Mockito.verify(db.collection(assoAPI.collectionName).document(asso.uid)).get()
    assert(result == asso)
  }

  @Test
  fun testGetAllAssociations() {

    Mockito.`when`(documentSnapshot.exists()).thenReturn(true)
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(collectionReference)
    val query = Tasks.forResult(Mockito.mock(QuerySnapshot::class.java))
    Mockito.`when`(db.collection(Mockito.any()).get()).thenReturn(query)

    Mockito.`when`(query.result!!.documents).thenReturn(listOf(documentSnapshot))
    Mockito.`when`(
            listOf(documentSnapshot).map { document -> document.toObject(Association::class.java) })
        .thenReturn(listOf(asso))
    Mockito.`when`(documentSnapshot.toObject(Association::class.java)).thenReturn(asso)

    val result = assoAPI.getAssociations()

    Mockito.verify(db).collection(assoAPI.collectionName)
    Mockito.verify(db.collection(assoAPI.collectionName)).get()

    assert(result == listOf(asso))
  }

  @Test
  fun testAddAssociation() {
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(collectionReference)
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)
    Mockito.`when`(documentReference.set(asso)).thenReturn(Tasks.forResult(null))
    assoAPI.addAssociation(asso)
    Mockito.verify(db).collection(assoAPI.collectionName)
    Mockito.verify(db.collection(assoAPI.collectionName)).document(asso.uid)
    Mockito.verify(db.collection(assoAPI.collectionName).document(asso.uid)).set(asso)
  }

  @Test
  fun testDeleteAssociation() {
    Mockito.`when`(db.collection(Mockito.any())).thenReturn(collectionReference)
    Mockito.`when`(db.collection(Mockito.any()).document(Mockito.any()))
        .thenReturn(documentReference)
    Mockito.`when`(documentReference.delete()).thenReturn(Tasks.forResult(null))
    assoAPI.deleteAssociation(asso.uid)
    Mockito.verify(db).collection(assoAPI.collectionName)
    Mockito.verify(db.collection(assoAPI.collectionName)).document(asso.uid)
    Mockito.verify(db.collection(assoAPI.collectionName).document(asso.uid)).delete()
  }
}
