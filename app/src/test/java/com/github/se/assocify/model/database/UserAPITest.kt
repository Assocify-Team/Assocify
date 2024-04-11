package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
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
class UserAPITest {
  @get:Rule val mockkRule = MockKRule(this)
  @MockK private lateinit var db: FirebaseFirestore

  @MockK private lateinit var documentSnapshot: DocumentSnapshot
  @MockK private lateinit var documentReference: DocumentReference
  @MockK private lateinit var collectionReference: CollectionReference

  @MockK private lateinit var query: QuerySnapshot

  private lateinit var userAPI: UserAPI
  private val testUser = User("testId", "testName", Role("testId"))

  @Before
  fun setup() {
    every { documentSnapshot.exists() } returns true
    every { documentSnapshot.toObject(User::class.java) } returns testUser
    every { db.collection("users").document("testId") } returns documentReference
    every { db.collection("users") } returns collectionReference
    every { collectionReference.document("testId") } returns documentReference
    every { query.documents } returns listOf(documentSnapshot)

    userAPI = UserAPI(db)
  }

  @Test
  fun testGetUser() {
    every { documentReference.get() } returns APITestUtils.mockSuccessfulTask(documentSnapshot)

    val successMock = mockk<(User) -> Unit>(relaxed = true)
    userAPI.getUser(testUser.uid, successMock, { fail("Should not fail") })

    verify(timeout = 100) { successMock.invoke(testUser) }

    every { documentReference.get() } returns APITestUtils.mockFailingTask()

    val failureMock = mockk<(Exception) -> Unit>(relaxed = true)
    userAPI.getUser(testUser.uid, { fail("Should not succeed") }, failureMock)

    verify(timeout = 100) { failureMock.invoke(any()) }
  }

  @Test
  fun testGetAllUsers() {
    every { collectionReference.get() } returns APITestUtils.mockSuccessfulTask(query)

    val successMock = mockk<(List<User>) -> Unit>(relaxed = true)

    userAPI.getAllUsers(successMock, { fail("Should not fail") })

    verify { successMock.invoke(listOf(testUser)) }

    every { collectionReference.get() } returns APITestUtils.mockFailingTask()

    val failureMock = mockk<(Exception) -> Unit>(relaxed = true)

    userAPI.getAllUsers({ fail("Should not succeed") }, failureMock)

    verify { failureMock.invoke(any()) }
  }

  @Test
  fun testAddUser() {
    every { documentReference.set(testUser) } returns APITestUtils.mockSuccessfulTask()

    val successMock = mockk<() -> Unit>(relaxed = true)

    userAPI.addUser(testUser, successMock, { fail("Should not fail") })

    verify { successMock.invoke() }

    every { documentReference.set(testUser) } returns APITestUtils.mockFailingTask()

    val failureMock = mockk<(Exception) -> Unit>(relaxed = true)

    userAPI.addUser(testUser, { fail("Should not succeed") }, failureMock)

    verify { failureMock.invoke(any()) }
  }

  @Test
  fun testDeleteUser() {
    every { documentReference.delete() } returns APITestUtils.mockSuccessfulTask()

    val successMock = mockk<() -> Unit>(relaxed = true)

    userAPI.deleteUser(testUser.uid, successMock, { fail("Should not fail") })

    verify { successMock.invoke() }

    every { documentReference.delete() } returns APITestUtils.mockFailingTask()

    val failureMock = mockk<(Exception) -> Unit>(relaxed = true)

    userAPI.deleteUser(testUser.uid, { fail("Should not succeed") }, failureMock)

    verify { failureMock.invoke(any()) }
  }
}
