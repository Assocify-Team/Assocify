package com.github.se.assocify.model.database

import android.net.Uri
import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.MaybeRemotePhoto
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.Status
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import java.io.File
import java.time.LocalDate
import org.junit.Assert.assertFalse
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MockKExtension.ConfirmVerification
class ReceiptsAPITest {
  @get:Rule val mockkRule = MockKRule(this)

  private lateinit var api: ReceiptAPI
  private var error = false
  private var response = ""

  private val uriMock = mockk<Uri>()

  private val remoteReceipt =
      Receipt(
          uid = "00000000-ABCD-0000-0000-000000000000",
          userId = "ABCD0001-ABCD-0000-0000-000000000001",
          date = LocalDate.EPOCH,
          cents = -100,
          status = Status.Pending,
          title = "title",
          description = "notes",
          photo = MaybeRemotePhoto.Remote("00000000-ABCD-0000-0000-000000000000"))

  private val localReceipt =
      Receipt(
          uid = "00000001-ABCD-0000-0000-000000000001",
          userId = "ABCD0001-ABCD-0000-0000-000000000001",
          date = LocalDate.EPOCH,
          cents = -100,
          status = Status.Approved,
          title = "title",
          description = "notes",
          photo = MaybeRemotePhoto.LocalFile(uriMock))

  private val remoteJson =
      """
      {
        "uid": "${remoteReceipt.uid}",
        "date": "1970-01-01",
        "cents": -100,
        "receipt_status": {
            "status": "unapproved"
        },
        "title": "title",
        "description": "notes",
        "user_id": "ABCD0001-ABCD-0000-0000-000000000001",
        "association_id": "2c256037-4185-ad67-991a-1a2b9bb1f9b3"
      }
    """
          .trimIndent()

  private val localJson =
      """
      {
        "uid": "${localReceipt.uid}",
        "date": "1970-01-01",
        "cents": -100,
        "receipt_status": {
            "status": "approved"
        },
        "title": "title",
        "description": "notes",
        "user_id": "ABCD0001-ABCD-0000-0000-000000000001",
        "association_id": "2c256037-4185-ad67-991a-1a2b9bb1f9b3"
      }
    """
          .trimIndent()

  private val receivedList =
      listOf(remoteReceipt, localReceipt.copy(photo = MaybeRemotePhoto.Remote(localReceipt.uid)))

  private val receivedListJson = "[$remoteJson, $localJson]"

  @Before
  fun setUp() {
    APITestUtils.setup()
    mockkStatic(Uri::class)
    every { Uri.parse(any()) }.returns(mockk())

    CurrentUser.userUid = "2c256037-ad67-4185-991a-1a2b9bb1f9b3"
    CurrentUser.associationUid = "2c256037-4185-ad67-991a-1a2b9bb1f9b3"
    val cachePath = File("cache").toPath()
    api =
        ReceiptAPI(
            createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY) {
              install(Postgrest)
              install(Storage)
              httpEngine = MockEngine {
                if (!error) {
                  respond(response)
                } else {
                  respondBadRequest()
                }
              }
            },
            cachePath)
  }

  @Test
  fun uploadReceipt() {
    val successMock = mockk<() -> Unit>(relaxed = true)
    api.uploadReceipt(
        remoteReceipt,
        { assertFalse(it) },
        successMock,
        { _, e -> fail("Should not fail, failed with $e") })
    error = false
    verify(timeout = 1000) { successMock() }

    error = true
    val failureMock = mockk<(Boolean, Exception) -> Unit>(relaxed = true)
    api.uploadReceipt(
        localReceipt,
        { fail("Should not succeed (image)") },
        { fail("Should not succeed (receipt)") },
        failureMock)

    verify(timeout = 1000) {
      failureMock(true, any())
      failureMock(false, any())
    }
  }

  @Test
  fun getUserReceipts() {
    val successMock = mockk<(List<Receipt>) -> Unit>(relaxed = true)

    response = receivedListJson

    api.getUserReceipts(successMock, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { successMock(receivedList) }

    error = true
    // Test the cache; this shouldn't ping remote
    api.getUserReceipts(successMock, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { successMock(receivedList) }

    // Change the UID to invalidate the cache
    CurrentUser.userUid = "DEADBEEF-1000-0000-0000-000000000000"

    val failureMock = mockk<(Exception) -> Unit>(relaxed = true)
    api.getUserReceipts({ fail("Should not succeed") }, failureMock)

    verify(timeout = 1000) { failureMock.invoke(any()) }
  }

  @Test
  fun getReceipt() {
    val successMock = mockk<(Receipt) -> Unit>(relaxed = true)

    response = "[$remoteJson]"

    api.getReceipt(remoteReceipt.uid, successMock, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { successMock(remoteReceipt) }
    clearMocks(successMock)

    error = true

    // Test the cache; this shouldn't ping remote
    api.getReceipt(remoteReceipt.uid, successMock, { fail("Should not fail, failed with $it") })
    verify(timeout = 1000) { successMock(remoteReceipt) }
    clearMocks(successMock)

    val failureMock1 = mockk<(Exception) -> Unit>(relaxed = true)
    api.getReceipt(localReceipt.uid, { fail("Should not succeed") }, failureMock1)
    verify(timeout = 1000) { failureMock1.invoke(any()) }

    // Change the UID to invalidate the cache
    CurrentUser.userUid = "DEADBEEF-1000-0000-0000-000000000000"

    val failureMock = mockk<(Exception) -> Unit>(relaxed = true)
    api.getReceipt(remoteReceipt.uid, { fail("Should not succeed") }, failureMock)

    verify(timeout = 1000) { failureMock.invoke(any()) }
  }

  @Test
  fun getReceiptImage() {
    // Can't really test success, sadly.

    val failureMock = mockk<(Exception) -> Unit>(relaxed = true)
    error = true
    api.getReceiptImage(remoteReceipt.uid, { fail("Should not succeed") }, failureMock)
    verify(timeout = 1000) { failureMock.invoke(any()) }
  }

  @Test
  fun getAllReceipts() {
    val successMock = mockk<(List<Receipt>) -> Unit>(relaxed = true)

    response = receivedListJson

    api.getAllReceipts(successMock, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { successMock(receivedList) }

    error = true
    // Test the cache; this shouldn't ping remote
    api.getAllReceipts(successMock, { fail("Should not fail, failed with $it") })
    verify(timeout = 1000) { successMock(receivedList) }

    // Change the UID to invalidate the cache
    CurrentUser.userUid = "DEADBEEF-1000-0000-0000-000000000000"

    val failureMock = mockk<(Exception) -> Unit>(relaxed = true)
    api.getAllReceipts({ fail("Should not succeed") }, failureMock)

    verify(timeout = 1000) { failureMock(any()) }
  }

  @Test
  fun deleteReceipt() {
    val successMock = mockk<() -> Unit>(relaxed = true)
    api.deleteReceipt(
        "2c256037-ad67-4185-991a-1a2b9bb1f9b3",
        successMock,
        { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { successMock() }

    error = true
    // Change the UID to invalidate the cache
    CurrentUser.userUid = "DEADBEEF-1000-0000-0000-000000000000"

    val failureMock = mockk<(Exception) -> Unit>(relaxed = true)
    api.deleteReceipt("failing_rid", { fail("Should not succeed") }, failureMock)

    verify(timeout = 1000) { failureMock(any()) }
  }

  @Test
  fun testUpdateCaches() {
    val successMock = mockk<(Boolean, List<Receipt>) -> Unit>(relaxed = true)
    val failureMock = mockk<(Boolean, Exception) -> Unit>(relaxed = true)
    response = receivedListJson

    api.updateCaches(successMock, { _, _ -> fail() })
    verify(timeout = 1000) {
      successMock(true, receivedList)
      successMock(false, receivedList)
    }

    val successMockGet = mockk<(Receipt) -> Unit>(relaxed = true)
    api.getReceipt(remoteReceipt.uid, successMockGet, { fail() })
    verify(timeout = 1000) { successMockGet(remoteReceipt) }

    error = true
    api.updateCaches(successMock, failureMock)
    verify(timeout = 1000) { failureMock.invoke(any(), any()) }
  }
}
