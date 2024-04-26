package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.entities.User
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
class UserAPITest {
  @get:Rule val mockkRule = MockKRule(this)

  private lateinit var userAPI: UserAPI
  private val testUser = User("testId", "testName")
  private var error = false
  private var response = ""

  @Before
  fun setup() {
    userAPI =
        UserAPI(
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

  @Test fun testGetUser() {}

  @Test fun testGetAllUsers() {}

  @Test fun testAddUser() {}

  @Test fun testDeleteUser() {}
}
