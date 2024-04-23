package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.entities.Event
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime


@MockKExtension.ConfirmVerification
class EventAPITest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private var error = false
    private var response = ""


    private lateinit var eventAPI: EventAPI

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {

        Dispatchers.setMain(UnconfinedTestDispatcher())
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
    }

    @Test
    fun testGetEvent() {
        val onSuccess: (Event) -> Unit = mockk(relaxed = true)
        val onFailure: (Exception) -> Unit = mockk(relaxed = true)
        error = false

        response = """
            {
                "uid": "1",
                "name": "Test Event",
                "description": "Test Description",
                "startDate": "2021-10-10T10:10:10",
                "endDate": "2021-10-10T10:10:10",
                "guestsOrArtists": "Test Guest",
                "location": "Test Location"
            }
        """.trimIndent()



        eventAPI.getEvent("1", onSuccess, onFailure)
        verify(timeout = 100) { onSuccess(any()) }
        verify(exactly = 0) { onFailure(any()) }

        error = true
        eventAPI.getEvent("1", onSuccess, onFailure)
        verify(timeout = 100) { onFailure(any()) }

    }

    @Test
    fun testGetAllEvent() {
        val onSuccess: (List<Event>) -> Unit = mockk(relaxed = true)
        val onFailure: (Exception) -> Unit = mockk(relaxed = true)
        error = false

        response = """
            [
                {
                    "uid": "1",
                    "name": "Test Event",
                    "description": "Test Description",
                    "startDate": "2021-10-10T10:10:10",
                    "endDate": "2021-10-10T10:10:10",
                    "guestsOrArtists": "Test Guest",
                    "location": "Test Location"
                }, {
                    "uid": "2",
                    "name": "Test Event 2",
                    "description": "Test Description 2",
                    "startDate": "2021-10-10T10:10:10",
                    "endDate": "2021-10-10T10:10:10",
                    "guestsOrArtists": "Test Guest 2",
                    "location": "Test Location 2"
                }
                
            ]
        """.trimIndent()

        eventAPI.getEvents(onSuccess, onFailure)
        verify(timeout = 100) { onSuccess(any()) }
        verify(exactly = 0) { onFailure(any()) }

    }

    @Test
    fun testAddEvent() {
        error = false

        val onSuccess: (String) -> Unit = mockk(relaxed = true)
        val currentTime = LocalDateTime.now()


        val uid = "1"
        // need to define the response as the object serialized to make the test pass
        response = """
            {
                "uid": "$uid",
                "name": "Test Event",
                "description": "Test Description",
                "startDate": "$currentTime",
                "endDate": "$currentTime",
                "guestsOrArtists": "Test Guest",
                "location": "Test Location"
            }
        """.trimIndent()


        eventAPI.addEvent(
            Event(
                uid = uid,
                name = "Test Event",
                description = "Test Description",
                startDate = currentTime,
                endDate = currentTime,
                guestsOrArtists = "Test Guest",
                location = "Test Location"
            ),
            onSuccess
        ) { fail("should not fail") }
        verify(timeout = 1000) { onSuccess(any())  }

    }

    @Test
    fun testDeleteEvent() {
        val onSuccess: () -> Unit = mockk(relaxed = true)
        error = false
        eventAPI.deleteEvent("1", onSuccess) { fail("Should not fail") }
        verify(timeout = 1000) { onSuccess() }

    }

    @Test
    fun testUpdateEvent(){

        val onSuccess: () -> Unit = mockk(relaxed = true)
        error = false
        val currentTime = LocalDateTime.now()

        eventAPI.updateEvent(
            Event(
                uid = "1",
                name = "Test Event",
                description = "Test Description",
                startDate = currentTime,
                endDate = currentTime,
                guestsOrArtists = "Test Guest",
                location = "Test Location"
            ),
            onSuccess
        ) { fail("Should not fail") }

        verify(timeout = 1000) { onSuccess() }
    }
}
