package com.github.se.assocify

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.chat.ChatScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppTest: TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun testSetup() {
        composeTestRule.setContent { MainActivity() }
    }

    @Test
    fun display() {
        with (composeTestRule) {

        }
    }
}