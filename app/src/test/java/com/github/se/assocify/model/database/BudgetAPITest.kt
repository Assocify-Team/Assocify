package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.TVA
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import java.lang.Thread.sleep
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@MockKExtension.ConfirmVerification
class BudgetAPITest {

  private var error = false

  private var response = ""

  private val budgetItem =
      BudgetItem(
          "budgetUID",
          "name",
          1,
          TVA.TVA_2,
          "lala",
          year = 2022,
          category = AccountingSubCategory("subCategoryUID", "name", 1))

  lateinit var budgetAPI: BudgetAPI

  @Before
  @OptIn(ExperimentalCoroutinesApi::class)
  fun setup() {
    Dispatchers.setMain(UnconfinedTestDispatcher())
    budgetAPI =
        BudgetAPI(
            createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY) {
              install(Postgrest)
              httpEngine = MockEngine {
                if (!error) {
                  respond(response)
                } else {
                  respondBadRequest()
                }
              }
            })
  }

  @Test
  fun testGetBudgets() {
    val onSuccess = mockk<(List<BudgetItem>) -> Unit>(relaxed = true)
    val onFailure = mockk<(Exception) -> Unit>(relaxed = true)
    error = false
    response =
        """
            [
                {
                    "item_uid": "aa3d4ad7-c901-435a-b089-bb835f6ec560",
                    "association_uid": "aa3d4ad7-c901-435a-b089-bb835f6ec560",
                    "name": "name",
                    "year": 2022,
                    "description": "lala",
                    "amount": 1,
                    "tva": 2.6,
                    "category": "subCategoryUID"
                }
            ]
        """
            .trimIndent()

    budgetAPI.getBudget("aa3d4ad7-c901-435a-b089-bb835f6ec560", onSuccess, onFailure)
    verify(timeout = 300) { onSuccess(any()) }
    verify(exactly = 0) { onFailure(any()) }
  }

  @Test
  fun testAddBudgetItem() {
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)
    val onSuccess = mockk<() -> Unit>(relaxed = true)
    error = false
    response = ""
    val budgetItemUpdt = budgetItem.copy(uid = UUID.randomUUID().toString())
    budgetAPI.addBudgetItem(
        "aa3d4ad7-c901-435a-b089-bb835f6ec560", budgetItemUpdt, onSuccess, onFailure)
    sleep(1000)
    verify(timeout = 1000) { onSuccess() }
    verify(exactly = 0) { onFailure(any()) }
  }

  @Test
  fun testUpdateBudgetItem() {
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)
    val onSuccess: () -> Unit = mockk(relaxed = true)
    error = false
    response = ""

    budgetAPI.updateBudgetItem(
        "aa3d4ad7-c901-435a-b089-bb835f6ec560", budgetItem, onSuccess, onFailure)
    verify(timeout = 1000) { onSuccess() }
    verify(exactly = 0) { onFailure(any()) }
    error = true
  }

  @Test
  fun testDeleteBudgetItem() {
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)
    val onSuccess: () -> Unit = mockk(relaxed = true)

    error = false
    response = ""
    budgetAPI.deleteBudgetItem("uiiddddd", onSuccess, onFailure)
    verify(timeout = 1000) { onSuccess() }
  }
}
