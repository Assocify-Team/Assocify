package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.entities.AccountingSubCategory
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.http.Headers
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AccountingSubCategoryTest {

  @get:Rule val mockkRule = MockKRule(this)
  lateinit var api: AccountingSubCategoryAPI
  private var error = false
  private var response = ""
  private var responseHeaders = Headers.Empty

  @Before
  fun setUp() {
    APITestUtils.setup()
    api =
        AccountingSubCategoryAPI(
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
  fun testGetAccountingSubCategory() {

    val onSuccess: (List<AccountingSubCategory>) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response =
        """
                [
                    {
                        "uid": "13379999-0000-0000-0000-000000000000",
                        "category_uid": "13379999-0000-0000-0000-000000000000",
                        "association_uid": "cb7b1079-cb62-40b9-9f35-7667fea4748d",
                        "name": "Test SubCategory",
                        "amount": 1,
                        "year": 2022
                    }
                ]
            """
            .trimIndent()
    api.getSubCategories("cb7b1079-cb62-40b9-9f35-7667fea4748d", onSuccess, onFailure)

    verify(timeout = 400) { onSuccess(any()) }
  }

  @Test
  fun testAddAccountingSubCategory() {

    val onSuccess: () -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response = ""
    api.addSubCategory(
        "cb7b1079-cb62-40b9-9f35-7667fea4748d",
        "cb7b1079-cb62-40b9-9f35-7667fea4748d",
        AccountingSubCategory(
            "13379999-0000-0000-0000-000000000000",
            "13379999-0000-0000-0000-000000000000",
            "Test SubCategory",
            1, 2022),
        onSuccess,
        onFailure)
    verify(timeout = 400) { onSuccess() }
  }

  @Test
  fun testDeleteAccountingSubCategory() {

    val onSuccess: () -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response = ""
    api.deleteSubCategory(
        AccountingSubCategory("13379999-0000-0000-0000-000000000000", "Test SubCategory",
            "", 2, 2022),
        onSuccess,
        onFailure)
    verify(timeout = 400) { onSuccess() }
  }

  @Test
  fun testUpdateAccountingSubCategory() {

    val onSuccess: () -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response = ""
    api.updateSubCategory(
        "cb7b1079-cb62-40b9-9f35-7667fea4748d",
        AccountingSubCategory(
            "cb7b1079-cb62-40b9-9f35-7667fea4748d",
            "13379999-0000-0000-0000-000000000000",
            "Test SubCategory",
            1, 2022),
        onSuccess,
        onFailure)
    verify(timeout = 400) { onSuccess() }
  }
}
