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
    }

    @Test
    fun testGetAllEvent() {
        val onSuccess: (List<Event>) -> Unit = mockk(relaxed = true)
        val onFailure: (Exception) -> Unit = mockk(relaxed = true)

        error = false
    }

    @Test
    fun testAddEvent() {
        val onSuccess: () -> Unit = mockk(relaxed = true)
        error = false
        eventAPI.addEvent(
            Event(1, "Test", "Test", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 1, 1), listOf("Test"), "Test")
            , onSuccess
        ) { fail("Should not fail") }

        verify(timeout = 1000) { onSuccess() }

    }

    @Test
    fun testDeleteEvent() {
        eventAPI =
            EventAPI(
                createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY) {
                    install(Postgrest)
                })
        val onSuccess: () -> Unit = mockk(relaxed = true)

        eventAPI.deleteEvent(1, onSuccess) { fail("Should not fail") }

        verify(timeout = 1000) { onSuccess() }


    }
}