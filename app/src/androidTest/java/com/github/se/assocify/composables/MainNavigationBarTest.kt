package com.github.se.assocify.composables

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.se.assocify.MAIN_TABS_LIST
import com.github.se.assocify.Screen
import com.github.se.assocify.ui.composables.MainNavigationBar
import com.kaspersky.components.composesupport.config.withComposeSupport
import org.junit.Test
import org.junit.runner.RunWith
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class MainNavigationBarTest: TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()){
    @get:Rule
    val composeTestRule = createComposeRule()
    @Before
    fun testSetup() {
        composeTestRule.setContent { MainNavigationBar(
            tabList = MAIN_TABS_LIST,
            selectedTab = Screen.Home
        ) }
    }
    @Test
    fun iconsDisplayed() {
        composeTestRule.onNodeWithTag("mainNavBar").assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("mainNavBarItem")
            .assertCountEquals(5)
    }
}

/*class MainNavigationBarSemantics(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<MainNavigationBarSemantics>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("mainNavBar") }
    ) {

    }*/
