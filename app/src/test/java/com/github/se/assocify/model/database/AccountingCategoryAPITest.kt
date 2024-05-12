package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.AccountingCategory
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.http.Headers
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AccountingCategoryAPITest {

  @get:Rule val mockkRule = MockKRule(this)
  lateinit var api: AccountingCategoryAPI
  private var error = false
  private var response = ""
  private var responseHeaders = Headers.Empty

  @Before
  fun setUp() {
    APITestUtils.setup()
    api =
        AccountingCategoryAPI(
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
  fun testGetAccountingCategory() {

    val onSuccess: (List<AccountingCategory>) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response =
        """
            [
                {
                    "uid": "13379999-0000-0000-0000-000000000000",
                    "association_uid": "${CurrentUser.associationUid}",
                    "name": "Test Category"
                }
            ]
        """
            .trimIndent()

    api.getCategories(CurrentUser.associationUid.toString(), onSuccess, onFailure)
    verify(timeout = 400) { onSuccess(any()) }
  }

  @Test
  fun testAddAccountingCategory() {
    val onSuccess: () -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response = ""

    api.addCategory(
        CurrentUser.associationUid.toString(),
        AccountingCategory(UUID.randomUUID().toString(), "Test Category"),
        onSuccess,
        onFailure)

    verify(timeout = 400) { onSuccess() }
  }

  @Test
  fun testUpdateAccountingCategory() {
    val onSuccess: () -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response = ""

    api.updateCategory(
        CurrentUser.associationUid.toString(),
        AccountingCategory("11d14f0c-bbb5-4288-9ea9-22013bb18398", "Test Category"),
        onSuccess,
        onFailure)

    verify(timeout = 400) { onSuccess() }
  }

  @Test
  fun testDeleteAccountingCategory() {
    val onSuccess: () -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response = ""

    api.deleteCategory(
        AccountingCategory("11d14f0c-bbb5-4288-9ea9-22013bb18398", "Test Category"),
        onSuccess,
        onFailure)

    verify(timeout = 400) { onSuccess() }
  }
}
