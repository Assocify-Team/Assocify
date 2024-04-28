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
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MockKExtension.ConfirmVerification
class UserAPITest {
  @get:Rule val mockkRule = MockKRule(this)

  private lateinit var userAPI: UserAPI
  private val uuid1 = UUID.fromString("00000000-0000-0000-0000-000000000000")!!
  private val testUser1 = User(uuid1.toString(), "testName")
  private val testUserJson1 = """{"uid":"$uuid1","name":"testName"}"""

  private val uuid2 = UUID.fromString("ABCDEF00-0000-0000-0000-000000000000")!!
  private val testUser2 = User(uuid2.toString(), "testName2")
  private val testUserJson2 = """{"uid":"$uuid2","name":"testName2"}"""

  private var error = false
  private var response = ""

  @Before
  fun setup() {
    APITestUtils.setup()
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

  @Test
  fun testGetUser() {
    val onSuccess: (User) -> Unit = mockk(relaxed = true)

    error = false
    response = testUserJson1
    userAPI.getUser("testId", onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { onSuccess(testUser1) }

    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = true
    userAPI.getUser("testId", { fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testGetAllUsers() {
    val onSuccess: (List<User>) -> Unit = mockk(relaxed = true)

    error = false
    response = "[$testUserJson1, $testUserJson2]"
    userAPI.getAllUsers(onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { onSuccess(listOf(testUser1, testUser2)) }

    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = true
    userAPI.getAllUsers({ fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testAddUser() {
    val onSuccess: () -> Unit = mockk(relaxed = true)

    error = false
    userAPI.addUser(testUser1, onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { onSuccess() }

    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = true
    userAPI.addUser(testUser1, { fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testDeleteUser() {
    val onSuccess: () -> Unit = mockk(relaxed = true)

    error = false
    userAPI.deleteUser("testId", onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { onSuccess() }

    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = true
    userAPI.deleteUser("testId", { fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }
}
