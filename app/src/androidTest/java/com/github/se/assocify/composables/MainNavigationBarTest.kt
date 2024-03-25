package com.github.se.assocify.composables

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.ui.composables.MainNavigationBar
import com.kaspersky.components.composesupport.config.withComposeSupport
import org.junit.Test
import org.junit.runner.RunWith
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
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
            selectedTab = Destination.Home
        ) }
    }
    @Test
    fun iconsDisplayed() {
        with (composeTestRule) {
            onNodeWithTag("mainNavBar").assertIsDisplayed()
            onAllNodesWithTag("mainNavBarItem")
                .assertCountEquals(5)
        }
    }
}
