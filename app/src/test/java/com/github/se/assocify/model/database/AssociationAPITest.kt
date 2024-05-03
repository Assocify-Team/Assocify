package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.PermissionRole
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.model.entities.User
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.http.Headers
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MockKExtension.ConfirmVerification
class AssociationAPITest {
  @get:Rule val mockkRule = MockKRule(this)

  private var error = false
  private var response = ""
  private var responseHeaders = Headers.Empty
  private val uuid1 = UUID.fromString("00000000-0000-0000-0000-000000000000")!!
  private val uuid2 = UUID.fromString("ABCDEF00-0000-0000-0000-000000000000")!!
  private val uuid3 = UUID.fromString("12345600-0000-0000-0000-000000000000")!!

  private lateinit var assoAPI: AssociationAPI

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    APITestUtils.setup()
    assoAPI =
        AssociationAPI(
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
  fun testGetAssociation() {
    val onSuccess: (Association) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response =
        """
      {
        "uid": "$uuid1",
        "name": "Test",
        "description": "Test",
        "creation_date": "2022-01-01"
      }
    """
            .trimIndent()

    assoAPI.getAssociation(uuid1.toString(), onSuccess, onFailure)

    verify(timeout = 1000) { onSuccess(any()) }
    verify(exactly = 0) { onFailure(any()) }

    // Test failure
    error = true
    assoAPI.getAssociation(uuid1.toString(), { fail("should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testGetAllAssociations() {
    val onSuccess: (List<Association>) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response =
        """
      [{
        "uid": "$uuid1",
        "name": "Test",
        "description": "Test",
        "creation_date": "2022-01-01"
      }, {
        "uid": "$uuid2",
        "name": "Test2",
        "description": "Test2",
        "creation_date": "2022-01-02"
      }]
    """
            .trimIndent()

    assoAPI.getAssociations(onSuccess, onFailure)

    verify(timeout = 100) { onSuccess(any()) }
    verify(exactly = 0) { onFailure(any()) }

    // Test failure
    error = true
    assoAPI.getAssociations({ fail("should not succeed") }, onFailure)

    verify(timeout = 100) { onFailure(any()) }
  }

  @Test
  fun testAddAssociation() {
    val onSuccess: () -> Unit = mockk(relaxed = true)

    error = false
    response =
        """
      {
        "uid": "$uuid1",
        "name": "Test",
        "description": "Test",
        "creation_date": "2022-01-01"
      }
    """
            .trimIndent()

    assoAPI.addAssociation(
        Association(uuid1.toString(), "Test", "Test", LocalDate.now()),
        onSuccess,
        { fail("Should not fail") })

    verify(timeout = 1000) { onSuccess() }

    // Test failure
    val onFailure = mockk<(Exception) -> Unit>(relaxed = true)

    error = true
    assoAPI.addAssociation(
        Association(uuid1.toString(), "Test", "Test", LocalDate.now()),
        { fail("Should not succeed") },
        onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testEditAssociation() {
    val onSuccess: () -> Unit = mockk(relaxed = true)

    assoAPI.editAssociation(
        uuid1.toString(), "TestN", "NewTestD", onSuccess, { fail("Should not fail") })

    verify(timeout = 1000) { onSuccess() }

    // Test failure
    val onFailure = mockk<(Exception) -> Unit>(relaxed = true)
    error = true

    assoAPI.editAssociation(
        uuid1.toString(), "TestN", "NewTestD", { fail("Should not succeed") }, onFailure)
    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testDeleteAssociation() {
    val onSuccess: () -> Unit = mockk(relaxed = true)

    assoAPI.deleteAssociation(uuid1.toString(), onSuccess, { fail("Should not fail") })

    verify(timeout = 1000) { onSuccess() }

    // Test failure
    val onFailure = mockk<(Exception) -> Unit>(relaxed = true)
    error = true

    assoAPI.deleteAssociation(uuid1.toString(), { fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testGetApplicants() {
    val onSuccess: (List<User>) -> Unit = mockk(relaxed = true)

    response =
        """
      [
        {
          "association_id": "$uuid3",
          "users": {
            "uid": "$uuid1",
            "name": "UUID1"
          }
        },
        {
          "association_id": "$uuid3",
          "users": {
            "uid": "$uuid2",
            "name": "UUID2",
            "email": "uuid2@example.com"
          }
        }
      ]
      """
            .trimIndent()

    assoAPI.getApplicants(uuid3.toString(), onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { onSuccess(any()) }

    // Test failure
    val onFailure = mockk<(Exception) -> Unit>(relaxed = true)
    error = true

    assoAPI.getApplicants(uuid3.toString(), { fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testAcceptUser() {
    val onSuccess: () -> Unit = mockk(relaxed = true)
    response = """{"count": 1}"""
    responseHeaders = Headers.build { append("Content-Range", "/1") }

    val permissionRole = PermissionRole(uuid2.toString(), uuid3.toString(), RoleType.PRESIDENCY)
    assoAPI.acceptUser(
        uuid1.toString(), permissionRole, onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) { onSuccess() }

    // Test failure
    val onFailure = mockk<(Exception) -> Unit>(relaxed = true)
    error = true

    assoAPI.acceptUser(uuid1.toString(), permissionRole, { fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testGetRoles() {
    val onSuccess: (List<PermissionRole>) -> Unit = mockk(relaxed = true)

    response =
        """
            [
            ${APITestUtils.PERMISSION_JSON},
            ${APITestUtils.PERMISSION_JSON}
            ]
        """
            .trimIndent()

    assoAPI.getRoles(uuid1.toString(), onSuccess, { fail("Should not fail, failed with $it") })

    verify(timeout = 1000) {
      onSuccess(listOf(APITestUtils.PERMISSION_ROLE, APITestUtils.PERMISSION_ROLE))
    }

    val onFailure = mockk<(Exception) -> Unit>(relaxed = true)
    error = true

    assoAPI.getRoles(uuid1.toString(), { fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testAddRole() {
    // Coverage test
    val onSuccess: () -> Unit = mockk(relaxed = true)

    assoAPI.addRole(APITestUtils.PERMISSION_ROLE, onSuccess, { fail("Should not fail") })

    verify(timeout = 1000) { onSuccess() }

    val onFailure = mockk<(Exception) -> Unit>(relaxed = true)

    error = true
    assoAPI.addRole(APITestUtils.PERMISSION_ROLE, { fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testInviteUser() {
    // Coverage test
    val onSuccess: () -> Unit = mockk(relaxed = true)

    assoAPI.inviteUser(
        uuid1.toString(), APITestUtils.PERMISSION_ROLE, onSuccess, { fail("Should not fail") })

    verify(timeout = 1000) { onSuccess() }

    assoAPI.inviteUser(APITestUtils.ASSOCIATION_MEMBER, onSuccess, { fail("Should not fail") })

    verify(timeout = 1000) { onSuccess() }

    val onFailure = mockk<(Exception) -> Unit>(relaxed = true)

    error = true
    assoAPI.inviteUser(
        uuid1.toString(), APITestUtils.PERMISSION_ROLE, { fail("Should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testInitAssociation() {
    // Coverage test
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
    assoAPI.initAssociation(
        listOf(APITestUtils.PERMISSION_ROLE),
        listOf(APITestUtils.ASSOCIATION_MEMBER),
        onSuccess,
        { fail("Should not fail") })

    verify(timeout = 1000) { onSuccess() }

    val onFailure = mockk<(Exception) -> Unit>(relaxed = true)

    error = true
    assoAPI.initAssociation(
        listOf(APITestUtils.PERMISSION_ROLE),
        listOf(APITestUtils.ASSOCIATION_MEMBER),
        { fail("Should not succeed") },
        onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }
}
