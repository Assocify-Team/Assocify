package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Association
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MockKExtension.ConfirmVerification
class AssociationAPITest {
  @get:Rule val mockkRule = MockKRule(this)

  @MockK private lateinit var db: FirebaseFirestore

  @MockK private lateinit var documentSnapshot: DocumentSnapshot
  @MockK private lateinit var documentReference: DocumentReference
  @MockK private lateinit var collectionReference: CollectionReference

  @MockK private lateinit var query: QuerySnapshot

  private val testAssociation =
      Association(
          "testId", "testName", "testDescription", "testDate", "testStatus", listOf(), listOf())

  private lateinit var assoAPI: AssociationAPI

  @Before
  fun setup() {
    every { documentSnapshot.exists() } returns true
    every { documentSnapshot.toObject(Association::class.java) } returns testAssociation
    every { db.collection("associations").document("testId") } returns documentReference
    every { db.collection("associations") } returns collectionReference
    every { collectionReference.document("testId") } returns documentReference
    every { query.documents } returns listOf(documentSnapshot)

    assoAPI = AssociationAPI(db)
  }

  @Test
  fun testGetAssociation() {
    every { documentReference.get() } returns APITestUtils.mockSuccessfulTask(documentSnapshot)

    val successMock = mockk<(Association) -> Unit>(relaxed = true)
    assoAPI.getAssociation(testAssociation.uid, successMock, { fail("Should not fail") })

    verify(timeout = 100) { successMock.invoke(testAssociation) }

    every { documentReference.get() } returns APITestUtils.mockFailingTask()

    val failureMock = mockk<(Exception) -> Unit>(relaxed = true)
    assoAPI.getAssociation(testAssociation.uid, { fail("Should not succeed") }, failureMock)

    verify(timeout = 100) { failureMock.invoke(any()) }
  }

  @Test
  fun testGetAllAssociations() {
    every { collectionReference.get() } returns APITestUtils.mockSuccessfulTask(query)

    val successMock = mockk<(List<Association>) -> Unit>(relaxed = true)
    assoAPI.getAssociations(successMock, { fail("Should not fail") })

    verify(timeout = 100) { successMock.invoke(listOf(testAssociation)) }

    every { collectionReference.get() } returns APITestUtils.mockFailingTask()

    val failureMock = mockk<(Exception) -> Unit>(relaxed = true)
    assoAPI.getAssociations({ fail("Should not succeed") }, failureMock)

    verify(timeout = 100) { failureMock.invoke(any()) }
  }

  @Test
  fun testAddAssociation() {
    every { documentReference.set(testAssociation) } returns APITestUtils.mockSuccessfulTask()

    val successMock = mockk<() -> Unit>(relaxed = true)
    assoAPI.addAssociation(testAssociation, successMock, { fail("Should not fail") })

    verify(timeout = 100) { successMock.invoke() }

    every { documentReference.set(testAssociation) } returns APITestUtils.mockFailingTask()

    val failureMock = mockk<(Exception) -> Unit>(relaxed = true)
    assoAPI.addAssociation(testAssociation, { fail("Should not succeed") }, failureMock)

    verify(timeout = 100) { failureMock.invoke(any()) }
  }

  @Test
  fun testDeleteAssociation() {
    every { documentReference.delete() } returns APITestUtils.mockSuccessfulTask()

    val successMock = mockk<() -> Unit>(relaxed = true)
    assoAPI.deleteAssociation(testAssociation.uid, successMock, { fail("Should not fail") })

    verify(timeout = 100) { successMock.invoke() }

    every { documentReference.delete() } returns APITestUtils.mockFailingTask()

    val failureMock = mockk<(Exception) -> Unit>(relaxed = true)
    assoAPI.deleteAssociation(testAssociation.uid, { fail("Should not succeed") }, failureMock)
  }
}
