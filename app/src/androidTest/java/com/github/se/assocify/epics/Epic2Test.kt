package com.github.se.assocify.epics

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.localsave.LoginSave
import com.github.se.assocify.navigation.Destination
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

/**
 * This test is an end-to-end test for the second epic :
 *
 * As a member, I want to register a receipt to be able to be reimbursed later,
 * and keep track of the other receipts I have
 */
@RunWith(AndroidJUnit4::class)
class Epic2Test : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var navActions: NavigationActions

    private val associationAPI = mockk<AssociationAPI> {}
    private val userAPI = mockk<UserAPI> {}
    private val eventAPI = mockk<EventAPI> {}

    private val taskAPI = mockk<TaskAPI>(relaxUnitFun = true)
    private val budgetAPI = mockk<BudgetAPI>(relaxUnitFun = true)
    private val loginSave = mockk<LoginSave>(relaxUnitFun = true)
    @Before
    fun testSetup() {
        composeTestRule.setContent {
            CurrentUser.userUid = "1"
            CurrentUser.associationUid = "a"
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            navActions = NavigationActions(navController, loginSave)
            TestAssocifyApp(
                navController, navActions, userAPI, associationAPI, eventAPI, budgetAPI, taskAPI, Destination.Home)
        }
    }
    @Test
    fun Epic2Test() {
        with(composeTestRule) {
            // start at home
            onNodeWithTag("homeScreen").assertIsDisplayed()

            // go to profile to check what association I'm in and my role
            onNodeWithTag("mainNavBarItem/profile").assertIsDisplayed().performClick()
            val toProfile = navController.currentBackStackEntry?.destination?.route
            assert(toProfile == Destination.Profile.route)

            onNodeWithText("asso1").assertIsDisplayed()
            onNodeWithText("MEMBER").assertIsDisplayed()

            // go to treasury and see current receipts I have previously done (1)
            onNodeWithTag("mainNavBarItem/treasury").assertIsDisplayed().performClick()
            val toTreasury = navController.currentBackStackEntry?.destination?.route
            assert(toTreasury == Destination.Treasury.route)

            onNodeWithTag("treasuryScreen").assertIsDisplayed()
            onNodeWithText("Receipt-1-name").assertIsDisplayed().performClick()

            // check that the receipt is correct and change its title
            onNodeWithTag("titleField").assertIsDisplayed()
                .performClick().performTextInput("Receipt-1-name-changed")
            onNodeWithTag("saveButton").performScrollTo().assertIsDisplayed().performClick()
            onNodeWithText("Receipt-1-name-changed").assertIsDisplayed()

            // (shouldn't have access to budget and balance but not implemented)

            // add a receipt
            onNodeWithTag("createReceipt").assertIsDisplayed().performClick()
            onNodeWithTag("titleField").performClick().performTextInput("Receipt-2-name")
            onNodeWithTag("amountField").performClick().performTextInput("10")

            // NEED TO SEE HOW TO TEST DATE PICKER
            composeTestRule.onRoot().printToLog("ARBITRARY_LOG_TAG")
            onNodeWithTag("dateField").performClick().performTextInput("2022-01-01")

            onNodeWithTag("saveButton").performScrollTo().assertIsDisplayed().performClick()

            // check that receipt is here
            onNodeWithText("Receipt-2-name").assertIsDisplayed()

            // change association
            onNodeWithTag("mainNavBarItem/profile").performClick()
            onNodeWithTag("associationDropdown").performClick()
            onNodeWithTag("DropdownItem-b").performClick()
            assert(CurrentUser.associationUid == "b")

            // go to check receipts are different
            onNodeWithTag("mainNavBarItem/treasury").assertIsDisplayed().performClick()
            onNodeWithText("Receipt-1-name-changed").assertDoesNotExist()
            onNodeWithText("Receipt-2-name").assertDoesNotExist()

            // go back to asso1 and check that receipts are here
            onNodeWithTag("mainNavBarItem/profile").performClick()
            onNodeWithTag("associationDropdown").performClick()
            onNodeWithTag("DropdownItem-a").performClick()
            assert(CurrentUser.associationUid == "a")

            onNodeWithTag("mainNavBarItem/treasury").performClick()
            onNodeWithText("Receipt-1-name-changed").assertIsDisplayed()
            onNodeWithText("Receipt-2-name").assertIsDisplayed()

        }
    }
}