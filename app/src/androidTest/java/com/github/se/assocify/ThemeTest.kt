package com.github.se.assocify

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onSibling
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.chat.ChatScreen
import com.github.se.assocify.ui.theme.AssocifyTheme
import com.github.se.assocify.ui.theme.md_theme_dark_primary
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Composable
fun TestScreen() {
    Text("Test")
    Text((MaterialTheme.colorScheme.primary).toString())
}

@RunWith(AndroidJUnit4::class)
class ThemeTest: TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun testSetup() {
        composeTestRule.setContent { AssocifyTheme (darkTheme = true, dynamicColor = false) {
            TestScreen()
        } }
    }

    @Test
    fun theme() {
        with (composeTestRule) {
            onNodeWithText("Test").assertIsDisplayed()
                .onSibling().assertTextContains(md_theme_dark_primary.toString())
        }
    }
}