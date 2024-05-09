package com.github.se.assocify.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.ui.screens.event.scheduletab.EventScheduleScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class ScheduleScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun testSetup() {
    composeTestRule.setContent { EventScheduleScreen() }
  }

  @Test
  fun testSideBar() {
    with(composeTestRule) {
      for (i in 0..23) {
        onNodeWithText(LocalTime.of(i, 0).format(DateTimeFormatter.ofPattern("HH:mm")))
            .assertExists()
      }
    }
  }
}