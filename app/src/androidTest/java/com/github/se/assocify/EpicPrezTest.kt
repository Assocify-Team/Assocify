package com.github.se.assocify

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.localsave.LoginSave
import com.github.se.assocify.navigation.NavigationActions
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EpicPrezTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var navActions: NavigationActions

    private val associationAPI = mockk<AssociationAPI>() {}

    private val userAPI = mockk<UserAPI>() {}

    private val eventAPI = mockk<EventAPI>() {}

    private val taskAPI = mockk<TaskAPI>(relaxUnitFun = true)

    private val budgetAPI = mockk<BudgetAPI>(relaxUnitFun = true)

    private val loginSave = mockk<LoginSave>(relaxUnitFun = true)

    @Before
    fun testSetup() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            navActions = NavigationActions(navController, loginSave)

            TestAssocifyApp(
                navController, navActions, userAPI, associationAPI, eventAPI, budgetAPI, taskAPI)
        }
    }

    @Test
    fun EpicPrezTest() {
        with(composeTestRule) {
            // After login as uid "1", the user is in selectAsso :
            // check it's well displayed (no back arrow...)


            // As the president, they want to create their association1
            // Goes to createAsso

            // Fill all the fields with the association1 data (themselves as president + 1 treasury)

            // Arrives at home screen with association1 as the current association

            // Goes to the profile screen to check that and go to add their other association
            // Click on join an other, arrives at selectAsso, goes to createAsso

            // Fill all the fields with the association2 data (only themselves as president)

            // Arrives back at profile with association2 as the current association

            // CHeck that association2 is the current asso and that they can switch back to asso1

        }

    }
}