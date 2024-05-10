package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.model.entities.TVA
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.http.Headers
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MockKExtension.ConfirmVerification
class BalanceAPITest {

  @get:Rule val mockkRule = MockKRule(this)

  private var error = false
  private var response = ""
  private var responseHeaders = Headers.Empty

  private val balanceItem =
      BalanceItem(
          "00000000-0000-0000-0000-000000000000",
          "Test",
          10,
          TVA.TVA_2,
          "Test",
          LocalDate.now(),
          "Test",
          Status.Approved)

  private lateinit var balanceAPI: BalanceAPI

  @Before
  fun setUp() {
    APITestUtils.setup()
    balanceAPI =
        BalanceAPI(
            createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY) {
              install(Postgrest)
              httpEngine = MockEngine {
                if (!error) {
                  respond(response, headers = responseHeaders)
                } else {
                  respondBadRequest()
                }
              }
            })
  }

  @Test
  fun testGetBalance() {
    val onSuccess: (List<BalanceItem>) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response =
        """
            [
                {
                    "uid": "00000000-0000-0000-0000-000000000000",
                    "association_uid": "00000000-0000-0000-0000-000000000000",
                    "receipt_uid": "00000000-0000-0000-0000-000000000000",
                    "category_uid": "00000000-0000-0000-0000-000000000000",
                    "name": "Test",
                    "amount": 0,
                    "tva": "TVA_0",
                    "description": "Test",
                    "date": "2021-01-01",
                    "assignee": "Test",
                    "status": "unapproved"
                }
            ]
        """
            .trimIndent()

    balanceAPI.getBalance("00000000-0000-0000-0000-000000000000", onSuccess, onFailure)

    verify(timeout = 400) { onSuccess(any()) }
  }

  @Test
  fun testAddBalance() {
    val onSuccess: () -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response = ""

    balanceAPI.addBalance(
        "aa3d2ad7-c901-436a-b089-bb8v5f6ec560",
        "cb7b1079-cb62-40b9-4f35-7667fea4748d",
        "c97a6617-2975-4aa3-8698-f5d3b204c68b",
        balanceItem,
        onSuccess,
        onFailure)

    verify(timeout = 400) { onSuccess() }
  }

  @Test
  fun testDeleteBalance() {
    val onSuccess: () -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response = ""

    balanceAPI.deleteBalance("00000000-0000-0000-0000-000000000000", onSuccess, onFailure)
    verify(timeout = 400) { onSuccess() }
  }
}
