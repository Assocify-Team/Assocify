package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.entities.Task
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
import java.time.OffsetDateTime
import java.util.UUID
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MockKExtension.ConfirmVerification
class TaskAPITest {
  @get:Rule val mockkRule = MockKRule(this)

  private var error = false
  private var response = ""
  private val uuid1: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
  private val uuid2: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")
  private val uuid3: UUID = UUID.fromString("22222222-2222-2222-2222-222222222222")
  private val responseTime = OffsetDateTime.now()

  private lateinit var taskAPI: TaskAPI

  @Before
  fun setup() {
    APITestUtils.setup()
    error = true
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
    error = false
  }

  @Test
  fun testGetTask() {
    val onSuccess: (Task) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = false
    response =
        """  
        [{
          "uid": "$uuid1"
          "title": "testName",
          "description": "description",
          "is_completed": false,
          "start_time": "$responseTime" ,
          "duration": 60,
          "people_needed": 0,
          "category": "Committee",
          "location": "Here",
          "event_id": "eventUid"
        }]
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

  @Test
  fun testGetAllTasks() {
    val onSuccess: (List<Task>) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    // Test failure
    error = true
    taskAPI.getTasks({ fail("should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
    clearMocks(onSuccess, onFailure)

    error = false
    response =
        """
        [
          {
            "uid": "$uuid1",
            "title": "testName",
            "description": "description",
            "is_completed": false,
            "start_time": "$responseTime" ,
            "duration": 60,
            "people_needed": 0,
            "category": "Committee",
            "location": "Here",
            "event_id": "eventUid"
          },
          {
            "uid": "$uuid2",
            "title": "testName2",
            "description": "description2",
            "is_completed": false,
            "start_time": "$responseTime",
            "duration": 60,
            "people_needed": 2,
            "category": "Committee2",
            "location": "Here2",
            "event_id": "eventUid2"
          }
        ]
    """
            .trimIndent()

    taskAPI.getTasks(onSuccess, onFailure)

    verify(timeout = 1000) { onSuccess(any()) }
    verify(exactly = 0) { onFailure(any()) }
    clearMocks(onSuccess, onFailure)

    error = true
    // Test cache
    taskAPI.getTasks(onSuccess, onFailure)

    verify(timeout = 1000) { onSuccess(any()) }
    verify(exactly = 0) { onFailure(any()) }
  }

  @Test
  fun testAddTask() {
    val onSuccess: () -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    val task =
        Task(
            uuid1.toString(),
            "testName",
            "description",
            false,
            OffsetDateTime.now(),
            60,
            0,
            "Committee",
            "Here",
            "eventUid")

    error = false
    response = ""

    taskAPI.addTask(task, onSuccess, onFailure)

    verify(timeout = 1000) { onSuccess() }
    verify(exactly = 0) { onFailure(any()) }

    // Test failure
    error = true
    taskAPI.addTask(task, { fail("should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testEditTask() {
    response =
        """
          [{
          "uid": "$uuid1",
          "title": "testName",
          "description": "description",
          "is_completed": false,
          "start_time": "$responseTime",
          "duration": "60",
          "people_needed": 0,
          "category": "Committee",
          "location": "Here",
          "event_id": "eventUid"
        }]
      """
            .trimIndent()
    val successMockCache: (List<Task>) -> Unit = mockk(relaxed = true)
    taskAPI.updateTaskCache(successMockCache, { fail("should not fail") })
    verify(timeout = 1000) { successMockCache(any()) }

    val onSuccess: () -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)
    error = false
    taskAPI.editTask(
        Task(
            uuid1.toString(),
            "newName",
            "newDescription",
            true,
            OffsetDateTime.now(),
            60,
            2,
            "newCategory",
            "newLocation",
            "newEventUid"),
        onSuccess) {
          fail("should not fail")
        }
    verify(timeout = 1000) { onSuccess() }

    error = true
    taskAPI.editTask(
        Task(
            uuid1.toString(),
            "newName",
            "newDescription",
            true,
            OffsetDateTime.now(),
            2,
            60,
            "newCategory",
            "newLocation",
            "newEventUid"),
        { fail("should not fail") },
        onFailure)
    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testDeleteTask() {
    val onSuccess: () -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    taskAPI.deleteTask(uuid1.toString(), onSuccess) { fail("should not fail") }
    verify(timeout = 1000) { onSuccess() }

    error = true
    taskAPI.deleteTask(uuid1.toString(), { fail("should not fail") }, onFailure)
    verify(timeout = 1000) { onFailure(any()) }
  }

  @Test
  fun testTaskOfEvent() {
    val onSuccess: (List<Task>) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    // Test failure
    error = true
    taskAPI.getTasksOfEvent("eventUid", { fail("should not succeed") }, onFailure)

    verify(timeout = 1000) { onFailure(any()) }
    clearMocks(onSuccess, onFailure)

    // Test success
    error = false
    response =
        """
        [
          {
            "uid": "$uuid1",
            "title": "testName",
            "description": "description",
            "is_completed": false,
            "start_time": "$responseTime" ,
            "duration": 60,
            "people_needed": 0,
            "category": "Committee",
            "location": "Here",
            "event_id": "eventUid"
          },
          {
            "uid": "$uuid2",
            "title": "testName2",
            "description": "description2",
            "is_completed": false,
            "start_time": "$responseTime" ,
            "duration": 60,
            "people_needed": 2,
            "category": "Committee2",
            "location": "Here2",
            "event_id": "eventUid2"
          }
        ]
    """
            .trimIndent()

    taskAPI.getTasksOfEvent(uuid3.toString(), onSuccess, onFailure)

    verify(timeout = 1000) { onSuccess(any()) }
    verify(exactly = 0) { onFailure(any()) }
  }
}
