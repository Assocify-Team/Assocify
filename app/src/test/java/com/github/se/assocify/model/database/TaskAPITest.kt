package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MockKExtension.ConfirmVerification
class TaskAPITest {
  @get:Rule val mockkRule = MockKRule(this)

  private var error = false
  private var response = ""
  private lateinit var taskAPI: TaskAPI

  @Before
  fun setup() {
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

  @Test fun testGetAssociation() {}
}
