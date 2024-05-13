package com.github.se.assocify.model.database

import com.github.se.assocify.BuildConfig
import com.github.se.assocify.model.entities.Language
import com.github.se.assocify.model.entities.Theme
import com.github.se.assocify.model.entities.UserPreference
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class UserPreferenceAPITest {
    private var error = false

    private var response = ""
    private lateinit var api: UserPreferenceAPI

    private val userUID = "00000000-0000-0000-0000-000000000000"
    private val userPreference = UserPreference(
        userUID = userUID,
        theme = Theme.DARK,
        textSize = 20,
        language = Language.ENGLISH
    )

    @Before
    @OptIn(ExperimentalCoroutinesApi::class)
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        api = UserPreferenceAPI(
            createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY)
            {
                install(Postgrest)
                httpEngine = MockEngine {
                    if (!error) {
                        respond(response)
                    } else {
                        respondBadRequest()
                    }
                }
            }
        )



    }

    @Test
    fun testGetUserPreference() {

        response = """
            {
                "userUID": "$userUID",
                "theme": "${userPreference.theme}",
                "textSize": ${userPreference.textSize},
                "language": "${userPreference.language}"
            }
        """.trimIndent()
        api.getUserPreference(userUID, {
            assert(it.userUID == userUID)
            assert(it.theme == userPreference.theme)
            assert(it.textSize == userPreference.textSize)
            assert(it.language == userPreference.language)
        }, {
            assert(false)
        })
    }

    @Test
    fun testAddUserPreference() {
        response = ""
        api.addUserPreference(userUID, userPreference, {
            assert(true)
        }, {
            assert(false)
        })
    }
    @Test
    fun testAddUserPreferenceFailure() {
        error = true
        api.addUserPreference(userUID, userPreference, {
            assert(false)
        }, {
            assert(true)
        })
    }
    @Test
    fun testGetUserPreferenceFailure() {
        error = true
        api.getUserPreference(userUID, {
            assert(false)
        }, {
            assert(true)
        })
    }

    @Test
    fun testUpdateUserPreference() {
        response = ""
        api.updateUserPreference(userUID, userPreference, {
            assert(true)
        }, {
            assert(false)
        })
    }

    @Test
    fun testUpdateUserPreferenceFailure() {
        error = true
        api.updateUserPreference(userUID, userPreference, {
            assert(false)
        }, {
            assert(true)
        })
    }
}