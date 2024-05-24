package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.PermissionRole
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.model.entities.User
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.mockk.clearMocks
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import java.io.File
import java.lang.Thread.sleep
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
    error = true
    val cachePath = File("cache").toPath()
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
            },
            cachePath)
    error = false
    sleep(300) // Sleep to let the init pass by
  }

  @Test
  fun testGetUser() {
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = true

    userAPI.getUser("testId", { fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }

    val onSuccess: (User) -> Unit = mockk(relaxed = true)

    error = false
    response = "[$testUserJson1, $testUserJson2]"
    userAPI.getUser(testUser1.uid, onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { onSuccess(testUser1) }

    error = true

    clearMocks(onSuccess)
    userAPI.getUser(testUser1.uid, onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { onSuccess(testUser1) }
  }

  @Test
  fun testGetAllUsers() {
    error = true
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    userAPI.getAllUsers({ fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }

    val onSuccess: (List<User>) -> Unit = mockk(relaxed = true)
    error = false
    response = "[$testUserJson1, $testUserJson2]"
    userAPI.getAllUsers(onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { onSuccess(listOf(testUser1, testUser2)) }

    error = true

    clearMocks(onSuccess)
    userAPI.getAllUsers(onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { onSuccess(listOf(testUser1, testUser2)) }
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

  @Test
  fun testRequestJoin() {
    val onSuccess: () -> Unit = mockk(relaxed = true)

    error = false
    userAPI.requestJoin(
        APITestUtils.ASSOCIATION.uid, onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { onSuccess() }

    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = true
    userAPI.requestJoin(APITestUtils.ASSOCIATION.uid, { fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testGetCurrentUserAssociations() {
    val onSuccess: (List<Association>) -> Unit = mockk(relaxed = true)

    error = false
    response =
        """
      [{
        "user_id": "$uuid1",
        "role_id": "$uuid1",
        "association_id": "${APITestUtils.ASSOCIATION.uid}",
        "type": "presidency",
        "association_name": "Test",
        "association_description": "Test",
        "association_creation_date": "2022-01-01"
      }]
    """
            .trimIndent()
    userAPI.getCurrentUserAssociations(onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { onSuccess(any()) }

    error = true

    clearMocks(onSuccess)
    // Test cache
    userAPI.getCurrentUserAssociations(onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { onSuccess(any()) }

    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    // Change user to invalidate cache
    CurrentUser.userUid = "DEADBEEF-1000-0000-0000-000000000000"

    userAPI.getCurrentUserAssociations({ fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  // Note: these tests aren't super useful, but we don't have a good enough test setup, so...
  @Test
  fun testSetDisplayName() {
    val onSuccess: () -> Unit = mockk(relaxed = true)

    error = false
    userAPI.setDisplayName(
        APITestUtils.USER.uid, "Test name", onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { onSuccess() }

    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = true
    userAPI.setDisplayName(
        APITestUtils.USER.uid, "Test name", { fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testAcceptInvitation() {
    val onSuccess: () -> Unit = mockk(relaxed = true)

    response =
        """
      [{
        "user_id": "${APITestUtils.USER.uid}",
        "role_id": ${APITestUtils.PERMISSION_ROLE.uid},
        "association_id": ${APITestUtils.ASSOCIATION.uid}
      }]
    """
            .trimIndent()
    error = false
    userAPI.acceptInvitation(
        APITestUtils.ASSOCIATION.uid, onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { onSuccess() }

    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = true
    userAPI.acceptInvitation(
        APITestUtils.ASSOCIATION.uid, { fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testGetInvitations() {
    val onSuccess: (List<Pair<PermissionRole, Association>>) -> Unit = mockk(relaxed = true)

    response =
        """
      [{
        "user_id": "${APITestUtils.USER.uid}",
        "role": ${APITestUtils.PERMISSION_JSON},
        "association": ${APITestUtils.ASSOCIATION_JSON}
      }]
    """
            .trimIndent()
    error = false
    userAPI.getInvitations(onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) {
      onSuccess(listOf(APITestUtils.PERMISSION_ROLE to APITestUtils.ASSOCIATION))
    }
  }

  @Test
  fun testGetCurrentUserRole() {
    val onSuccess: (PermissionRole) -> Unit = mockk(relaxed = true)

    response =
        """
      [{
        "user_id": "$uuid1",
        "role_id": "$uuid1",
        "association_id": "${APITestUtils.ASSOCIATION.uid}",
        "type": "presidency",
        "association_name": "Test",
        "association_description": "Test",
        "association_creation_date": "2022-01-01"
      }]
    """
            .trimIndent()
    error = false
    userAPI.getCurrentUserRole(onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) {
      onSuccess(PermissionRole(uuid1.toString(), APITestUtils.ASSOCIATION.uid, RoleType.PRESIDENCY))
    }

    error = true

    clearMocks(onSuccess)
    userAPI.getCurrentUserRole(onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) {
      onSuccess(PermissionRole(uuid1.toString(), APITestUtils.ASSOCIATION.uid, RoleType.PRESIDENCY))
    }

    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    // Change user to invalidate cache
    CurrentUser.userUid = "DEADBEEF-1000-0000-0000-000000000000"

    userAPI.getCurrentUserRole({ fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testSetGetProfilePicture() {
    error = true
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    userAPI.getProfilePicture(uuid1.toString(), { fail("Should not succeed") }, onFailure)
    verify(timeout = 1000) { onFailure(any()) }
    clearMocks(onFailure)

    // We can't test success :(
    userAPI.setProfilePicture(uuid1.toString(), mockk(), { fail("Should not succeed") }, onFailure)
    verify(timeout = 1000) { onFailure(any()) }
  }
}
