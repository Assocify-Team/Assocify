package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.TVA
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import java.lang.Thread.sleep
import java.time.OffsetDateTime
import java.util.UUID

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
          category = AccountingSubCategory("subCategoryUID", "name", AccountingCategory("categoryUID"), 1)
      )



  lateinit var budgetAPI: BudgetAPI

  @Before
  @OptIn(ExperimentalCoroutinesApi::class)
  fun setup() {
    Dispatchers.setMain(UnconfinedTestDispatcher())
    budgetAPI =
        BudgetAPI(
            createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY) {
              install(Postgrest)
            })
  }

  @Test
  fun testGetBudgets() {
    error = false
    response = ""



    var listResponse = listOf<BudgetItem>()
    budgetAPI.getBudget("aa3d4ad7-c901-435a-b089-bb835f6ec560", onSuccess = { listResponse = it}, onFailure = {})
    sleep(1000)
    print(listResponse)
    assert(listResponse.isNotEmpty())
  }

  @Test
  fun testAddBudgetItem() {
    error = false
    response = ""
    val  budgetItemUpdt = budgetItem.copy(uid = UUID.randomUUID().toString())
              budgetAPI.addBudgetItem("aa3d4ad7-c901-435a-b089-bb835f6ec560", budgetItemUpdt, onSuccess = {println("SUCESS")}, onFailure = {println(it.toString())})
    sleep(1000)
  }

  @Test
  fun testUpdateBudgetItem() {
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)
    error = false
    response = ""

    var listResponse = listOf<BudgetItem>()
    budgetAPI.getBudget("aa3d4ad7-c901-435a-b089-bb835f6ec560", onSuccess = { listResponse = it}, onFailure = {})
    sleep(1000)
      println(listResponse)
    val budgetItemUpdt = listResponse[0].copy(nameItem = "NameitemUPDATED!!")
    budgetAPI.updateBudgetItem("aa3d4ad7-c901-435a-b089-bb835f6ec560",
        budgetItemUpdt, onSuccess = { println("SUCESSSS") }, onFailure = { println(it.toString()) })
    sleep(1000)
  }

  @Test
  fun testDeleteBudgetItem() {
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response = ""
    budgetAPI.deleteBudgetItem("budgetItemUID", onSuccess = {}, onFailure = {})
  }
}
