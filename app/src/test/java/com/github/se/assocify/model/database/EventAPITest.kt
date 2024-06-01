package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.entities.Event
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.OffsetDateTime
import java.util.UUID

@MockKExtension.ConfirmVerification
class EventAPITest {

  @get:Rule val mockkRule = MockKRule(this)

  private var error = false
  private var response = ""
  private var responseTime = "2007-12-03T10:15:30+01:00"
  private var uuid1 = UUID.fromString("00000000-0000-0000-0000-000000000000")!!

  private lateinit var eventAPI: EventAPI

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    APITestUtils.setup()
    error = true
    eventAPI =
        EventAPI(
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
  fun testGetEvent() {
    val onSuccess: (Event) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)

    error = true
    eventAPI.updateEventCache({ fail("should not succeed") }, onFailure)
    eventAPI.getEvent("$uuid1", { fail("should not succeed") }, onFailure)
    verify(timeout = 100, exactly = 2) { onFailure(any()) }
    clearMocks(onSuccess, onFailure)

    error = false

    response =
        """
            [{
                "uid": "$uuid1",
                "name": "Test Event",
                "description": "Test Description",
                "start_date": "$responseTime",
                "end_date": "$responseTime",
                "guests_or_artists": "Test Guest",
                "location": "Test Location",
                "association_uid": "00000000-0000-0000-0000-000000000000"
            }]
        """
            .trimIndent()

    eventAPI.getEvent("$uuid1", onSuccess, onFailure)
    verify(timeout = 200) { onSuccess(any()) }
    verify(exactly = 0) { onFailure(any()) }
    clearMocks(onSuccess, onFailure)

    error = true
    // Test cache
    eventAPI.getEvent("$uuid1", onSuccess, onFailure)
    verify(timeout = 100) { onSuccess(any()) }
    verify(exactly = 0) { onFailure(any()) }
  }

  @Test
  fun testGetAllEvent() {
    val onSuccess: (List<Event>) -> Unit = mockk(relaxed = true)
    val onFailure: (Exception) -> Unit = mockk(relaxed = true)
    error = false

    response =
        """
            [
            {
                "uid": "$uuid1",
                "name": "Test Event",
                "description": "Test Description",
                "start_date": "$responseTime",
                "end_date": "$responseTime",
                "guests_or_artists": "Test Guest",
                "location": "Test Location",
                "association_uid": "00000000-0000-0000-0000-000000000000"
            }, {
                "uid": "$uuid1",
                "name": "Test Event",
                "description": "Test Description",
                "start_date": "$responseTime",
                "end_date": "$responseTime",
                "guests_or_artists": "Test Guest",
                "location": "Test Location",
                "association_uid": "00000000-0000-0000-0000-000000000000"
            }
                
            ]
        """
            .trimIndent()

    eventAPI.getEvents(onSuccess, onFailure)
    verify(timeout = 100) { onSuccess(any()) }
    verify(exactly = 0) { onFailure(any()) }
    clearMocks(onSuccess, onFailure)

    error = true
    // Test cache

    eventAPI.getEvents(onSuccess, onFailure)
    verify(timeout = 100) { onSuccess(any()) }
    verify(exactly = 0) { onFailure(any()) }
  }

  @Test
  fun testAddEvent() {
    error = false

    val onSuccess: (String) -> Unit = mockk(relaxed = true)
    val currentTime = OffsetDateTime.parse("2007-12-03T10:15:30+01:00")

    // need to define the response as the object serialized to make the test pass
    response =
        """
            {
                "uid": "$uuid1",
                "name": "Test Event",
                "description": "Test Description",
                "start_date": "$currentTime",
                "end_date": "$currentTime",
                "guests_or_artists": "Test Guest",
                "location": "Test Location",
                "association_uid": "00000000-0000-0000-0000-000000000000"
            }
        """
            .trimIndent()

    eventAPI.addEvent(
        Event(
            uid = uuid1.toString(),
            name = "Test Event",
            description = "Test Description"),
        onSuccess) {
          fail("should not fail")
        }
    verify(timeout = 1000) { onSuccess(any()) }
  }

  @Test
  fun testDeleteEvent() {
    val onSuccess: () -> Unit = mockk(relaxed = true)
    error = false
    eventAPI.deleteEvent("1", onSuccess) { fail("Should not fail") }
    verify(timeout = 1000) { onSuccess() }
  }

  @Test
  fun testUpdateEvent() {

    val onSuccess: () -> Unit = mockk(relaxed = true)
    error = false
    val currentTime = OffsetDateTime.parse("2007-12-03T10:15:30+01:00")

    response =
        """
          [{
            "uid": "$uuid1",
            "name": "Test Event",
            "description": "Test Description A",
            "start_date": "2008-12-03T10:15:30+01:00",
            "end_date": "2008-12-03T10:15:30+01:00",
            "guests_or_artists": "Test Guest",
            "location": "Test Location",
            "association_uid": "00000000-0000-0000-0000-000000000000"
        }]
      """
            .trimIndent()
    val successMockCache = mockk<(List<Event>) -> Unit>(relaxed = true)
    eventAPI.updateEventCache(successMockCache, { fail("Should not fail") })
    verify(timeout = 1000) { successMockCache(any()) }

    eventAPI.updateEvent(
        Event(
            uid = "$uuid1",
            name = "Test Event",
            description = "Test Description"),
        onSuccess) {
          fail("Should not fail")
        }

    verify(timeout = 1000) { onSuccess() }
  }
}
