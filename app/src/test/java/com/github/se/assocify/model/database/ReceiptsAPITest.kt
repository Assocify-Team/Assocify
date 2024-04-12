package com.github.se.assocify.model.database

import android.net.Uri
import com.github.se.assocify.model.entities.MaybeRemotePhoto
import com.github.se.assocify.model.entities.Phase
import com.github.se.assocify.model.entities.Receipt
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import java.time.LocalDate
import org.junit.Assert.assertFalse
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MockKExtension.ConfirmVerification
class ReceiptsAPITest {
  @get:Rule val mockkRule = MockKRule(this)

  @MockK lateinit var storage: FirebaseStorage

  @MockK lateinit var storageReference: StorageReference

  @MockK lateinit var firestore: FirebaseFirestore

  @MockK lateinit var collectionReference: CollectionReference

  private lateinit var api: ReceiptsAPI

  private val successfulReceipt =
      Receipt(
          uid = "successful_rid",
          date = LocalDate.EPOCH,
          incoming = false,
          cents = 100,
          phase = Phase.Approved,
          title = "title",
          description = "notes",
          photo = MaybeRemotePhoto.Remote("path"))

  private val failingReceipt =
      Receipt(
          uid = "failing_rid",
          date = LocalDate.EPOCH,
          incoming = false,
          cents = 100,
          phase = Phase.Approved,
          title = "title",
          description = "notes",
          photo = MaybeRemotePhoto.LocalFile("path"))

  @Before
  fun setUp() {

    every { storage.getReference("uid/receipts") }.returns(storageReference)
    every { firestore.collection("aid/receipts/uid/list") }.returns(collectionReference)

    every { collectionReference.document("successful_rid").set(any()) }
        .answers { APITestUtils.mockSuccessfulTask<Void>() }

    every { collectionReference.document("failing_rid").set(any()) }
        .answers { APITestUtils.mockFailingTask<Void>() }

    mockkStatic(Uri::class)
    every { Uri.parse(any()) }.returns(mockk())

    api =
        spyk<ReceiptsAPI>(ReceiptsAPI("uid", "aid", storage, firestore), recordPrivateCalls = true)

    every { api["parseReceiptList"](any<QuerySnapshot>()) } returns
        listOf(successfulReceipt, failingReceipt)
  }

  @Test
  fun uploadReceipt() {
    every { storageReference.child("successful_rid") }.returns(storageReference)

    every { storageReference.putFile(any()) }.returns(APITestUtils.mockSuccessfulTaskAdvanced())

    val successMock = mockk<() -> Unit>(relaxed = true)
    api.uploadReceipt(
        successfulReceipt, { assertFalse(it) }, successMock, { _, _ -> fail("Should not fail") })

    verify { successMock.invoke() }

    every { storageReference.child("failing_rid") }.returns(storageReference)

    every { storageReference.putFile(any()) }.returns(APITestUtils.mockFailingTaskAdvanced())

    val failureMock = mockk<(Boolean, Exception) -> Unit>(relaxed = true)
    api.uploadReceipt(
        failingReceipt,
        { fail("Should not succeed (image)") },
        { fail("Should not succeed (receipt)") },
        failureMock)

    verify { failureMock.invoke(true, any()) }
  }

  @Test
  fun getUserReceipts() {
    every { collectionReference.get() }
        .returns(APITestUtils.mockSuccessfulTask<QuerySnapshot>(mockk()))

    val successMock = mockk<(List<Receipt>) -> Unit>(relaxed = true)
    api.getUserReceipts(successMock, { fail("Should not fail") })

    verify { successMock.invoke(listOf(successfulReceipt, failingReceipt)) }

    every { collectionReference.get() }.returns(APITestUtils.mockFailingTaskAdvanced())

    val failureMock = mockk<(Exception) -> Unit>(relaxed = true)
    api.getUserReceipts({ fail("Should not succeed") }, failureMock)

    verify { failureMock.invoke(any()) }
  }

  @Test
  fun getAllReceipts() {
    val collectionQuerySnapshot = mockk<QuerySnapshot>()
    val userADocumentSnapshot = mockk<DocumentSnapshot>()
    val userBDocumentSnapshot = mockk<DocumentSnapshot>()
    val userAQuerySnapshot = mockk<QuerySnapshot>()
    val userBQuerySnapshot = mockk<QuerySnapshot>()

    every { collectionQuerySnapshot.documents } returns
        listOf(userADocumentSnapshot, userBDocumentSnapshot)

    every { userADocumentSnapshot.reference.collection("list").get() } returns
        APITestUtils.mockSuccessfulTask(userAQuerySnapshot)

    every { userBDocumentSnapshot.reference.collection("list").get() } returns
        APITestUtils.mockSuccessfulTask(userBQuerySnapshot)

    every { userADocumentSnapshot.id } returns "userA"

    every { userBDocumentSnapshot.id } returns "userB"

    every { firestore.collection("aid/receipts").get() } returns
        APITestUtils.mockSuccessfulTask(collectionQuerySnapshot)

    every { api["parseReceiptList"](userAQuerySnapshot) } returns
        listOf(successfulReceipt, failingReceipt)

    every { api["parseReceiptList"](userBQuerySnapshot) } returns listOf(failingReceipt)

    val successMock = mockk<(List<Receipt>) -> Unit>(relaxed = true)

    api.getAllReceipts(successMock, { _, _ -> fail("Should not fail") })

    verify {
      successMock.invoke(listOf(successfulReceipt, failingReceipt))
      successMock.invoke(listOf(failingReceipt))
    }

    every { userADocumentSnapshot.reference.collection("list").get() } returns
        APITestUtils.mockFailingTask()

    every { userBDocumentSnapshot.reference.collection("list").get() } returns
        APITestUtils.mockFailingTask()

    val failureMock = mockk<(String?, Exception) -> Unit>(relaxed = true)
    api.getAllReceipts({ fail("Should not succeed") }, failureMock)

    verify {
      failureMock.invoke("userA", any())
      failureMock.invoke("userB", any())
    }

    every { firestore.collection("aid/receipts").get() } returns APITestUtils.mockFailingTask()

    api.getAllReceipts({ fail("Should not succeed") }, failureMock)

    verify { failureMock.invoke(null, any()) }
  }

  @Test
  fun deleteReceipt() {
    every { collectionReference.document("successful_rid").delete() }.returns(mockSuccessfulTask())

    val successMock = mockk<() -> Unit>(relaxed = true)
    api.deleteReceipt("successful_rid", successMock, { fail("Should not fail") })

    verify { successMock.invoke() }

    every { collectionReference.document("failing_rid").delete() }.returns(mockFailingTask())

    val failureMock = mockk<(Exception) -> Unit>(relaxed = true)
    api.deleteReceipt("failing_rid", { fail("Should not succeed") }, failureMock)

    verify { failureMock.invoke(any()) }
  }
}
