package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.Budget
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.TVA
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
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
          AccountingSubCategory("uid", "name", AccountingCategory("asas"), 1))
  private val budget = Budget(listOf(budgetItem), "name", "uid")
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
    val onSuccesssss: (Budget) -> Unit = mockk(relaxed = true)
    val onSuccess: (Budget) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)
    error = false
    response = ""
    budgetAPI.getBudgets("associationUID", onSuccess = {}, onFailure = {})
  }

  @Test
  fun testAddBudgetItem() {
    val onSuccess: (Budget) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response = ""
    budgetAPI.addBudgetItem("associationUID", budgetItem, onSuccess = {}, onFailure = {})
  }

  @Test
  fun testAddBudget() {
    val onSuccess: (Budget) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response = ""
    budgetAPI.addBudget("associationUID", budget, onSuccess = {}, onFailure = {})
  }

  @Test
  fun testUpdateBudgetItem() {
    val onSuccess: (Budget) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response = ""
    budgetAPI.updateBudgetItem("associationUID", budgetItem, onSuccess = {}, onFailure = {})
  }

  @Test
  fun testUpdateBudget() {
    val onSuccess: (Budget) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response = ""
    budgetAPI.updateBudget("associationUID", budget, onSuccess = {}, onFailure = {})
  }

  @Test
  fun testDeleteBudgetItem() {
    val onSuccess: (Budget) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response = ""
    budgetAPI.deleteBudgetItem("budgetItemUID", onSuccess = {}, onFailure = {})
  }

  @Test
  fun testDeleteBudget() {
    val onSuccess: (Budget) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response = ""
    budgetAPI.deleteBudget("budgetUID", onSuccess = {}, onFailure = {})
  }
}
