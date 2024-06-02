package com.github.se.assocify.composables

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.ui.composables.MainNavigationBar
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainNavigationBarTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private var tabPressed: Destination? = null

  @Before
  fun testSetup() {
    composeTestRule.setContent {
      MainNavigationBar(
          onTabSelect = { tabPressed = it },
          tabList = MAIN_TABS_LIST,
          selectedTab = Destination.Treasury)
    }
  }

  @Test
  fun tabsDisplayed() {
    with(composeTestRule) {
      onNodeWithTag("mainNavBar").assertIsDisplayed()
      onNodeWithTag("mainNavBar").onChild().onChildren().assertCountEquals(3)
    }
  }

  @Test
  fun labelsDisplayed() {
    with(composeTestRule) {
      onNodeWithTag("mainNavBarItem/event").assertTextContains("Event")
      onNodeWithTag("mainNavBarItem/profile").assertTextContains("Profile")
      onNodeWithTag("mainNavBarItem/treasury").assertTextContains("Treasury")
    }
  }

  @Test
  fun iconsDisplayed() {
    with(composeTestRule) {
      onNodeWithTag("eventIcon", useUnmergedTree = true).assertIsDisplayed()
      onNodeWithTag("profileIcon", useUnmergedTree = true).assertIsDisplayed()
      onNodeWithTag("treasuryIcon", useUnmergedTree = true).assertIsDisplayed()
    }
  }

  @Test
  fun tabSelect() {
    with(composeTestRule) {
      onNodeWithTag("mainNavBarItem/event").performClick()
      assert(tabPressed == Destination.Event)
      onNodeWithTag("mainNavBarItem/profile").performClick()
      assert(tabPressed == Destination.Profile)
      onNodeWithTag("mainNavBarItem/treasury").performClick()
      assert(tabPressed == Destination.Treasury)
    }
  }
}

@RunWith(AndroidJUnit4::class)
class MainNavigationBarTest2 : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private var tabPressed: Destination? = null

  @Before
  fun testSetup() {
    composeTestRule.setContent {
      MainNavigationBar(
          onTabSelect = { tabPressed = it },
          tabList = listOf(Destination.Login),
          selectedTab = Destination.Login)
    }
  }

  @Test
  fun nullIconLabelTab() {
    with(composeTestRule) { onAllNodesWithTag("mainNavBarItem/login").assertCountEquals(0) }
  }
}
