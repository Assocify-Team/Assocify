package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.entities.Task
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import junit.framework.TestCase.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MockKExtension.ConfirmVerification
class TaskAPITest {
  @get:Rule val mockkRule = MockKRule(this)

  private var error = false
  private var response = ""
  private val uuid1: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

  private lateinit var taskAPI: TaskAPI

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    Dispatchers.setMain(UnconfinedTestDispatcher())
    taskAPI =
        TaskAPI(
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
  fun testGetTask() {
    val onSuccess: (Task) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response =
        """  
        {
          "uid": "$uuid1"
          "title": "testName",
          "description": "description",
          "is_completed": false,
          "start_time": "2021-10-10",
          "people_needed": 0,
          "category": "Committee",
          "location": "Here",
          "event_id": "eventUid"
        }
    """
            .trimIndent()

    taskAPI.getTask(uuid1.toString(), onSuccess, onFailure)

    verify(timeout = 1000) { onSuccess(any()) }
    verify(exactly = 0) { onFailure(any()) }

    // Test failure
    error = true
    taskAPI.getTask("testUid", { fail("should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test fun testGetAssociation() {}
}
