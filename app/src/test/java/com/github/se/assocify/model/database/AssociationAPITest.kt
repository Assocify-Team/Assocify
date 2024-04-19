package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.entities.Association
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MockKExtension.ConfirmVerification
class AssociationAPITest {
  @get:Rule val mockkRule = MockKRule(this)

  private var error = false
  private var response = ""

  private lateinit var assoAPI: AssociationAPI

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    Dispatchers.setMain(UnconfinedTestDispatcher())
    assoAPI =
        AssociationAPI(
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
  fun testGetAssociation() {
    val onSuccess: (Association) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response =
        """
      {
        "uid": 1,
        "name": "Test",
        "description": "Test",
        "creation_date": "2022-01-01"
      }
    """
            .trimIndent()

    assoAPI.getAssociation(1, onSuccess, onFailure)

    verify(timeout = 100) { onSuccess(any()) }
    verify(exactly = 0) { onFailure(any()) }

    // Test failure
    error = true
    assoAPI.getAssociation(1, { fail("should not succeed") }, onFailure)

    verify(timeout = 100) { onFailure(any()) }
  }

  @Test
  fun testGetAllAssociations() {
    val onSuccess: (List<Association>) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response =
        """
      [{
        "uid": 2,
        "name": "Test",
        "description": "Test",
        "creation_date": "2022-01-01"
      }, {
        "uid": 3,
        "name": "Test2",
        "description": "Test2",
        "creation_date": "2022-01-02"
      }]
    """
            .trimIndent()

    assoAPI.getAssociations(onSuccess, onFailure)

    verify(timeout = 100) { onSuccess(any()) }
    verify(exactly = 0) { onFailure(any()) }
  }

  @Test
  fun testAddAssociation() {
    val onSuccess: (Long) -> Unit = mockk(relaxed = true)

    error = false
    response =
        """
      {
        "uid": 1,
        "name": "Test",
        "description": "Test",
        "creation_date": "2022-01-01"
      }
    """
            .trimIndent()

    assoAPI.addAssociation(
        Association(1, "Test", "Test", LocalDate.now()), onSuccess, { fail("Should not fail") })

    verify(timeout = 1000) { onSuccess(any()) }
  }

  @Test
  fun testDeleteAssociation() {
    assoAPI =
        AssociationAPI(
            createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY) {
              install(Postgrest)
            })
    val onSuccess: () -> Unit = mockk(relaxed = true)

    assoAPI.deleteAssociation(1, onSuccess, { fail("Should not fail") })

    verify(timeout = 1000) { onSuccess() }
  }
}
