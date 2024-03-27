package com.github.se.assocify.navigation

import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class NavigationActionsTest {

    private val navController = mockk<NavHostController>()
    private var destination: String? = null

    private val user = mockk<FirebaseUser>()

    @Before
    fun setupLogin() {
        every {
            navController.navigate(route = any(), builder = any())
        } answers {
            destination = firstArg()
        }
    }
    @Test
    fun test() {
        val navActions = NavigationActions(navController)
        navActions.navigateToMainTab(Destination.Home)
        assert(destination == Destination.Home.route)
        try {
            navActions.navigateToMainTab(Destination.Login)
        } catch (e: IllegalArgumentException) {
            assert(e.message == "Destination Login is not a main tab")
        }

        navActions.onAuthError()
        navActions.onLogin(user)
    }
}