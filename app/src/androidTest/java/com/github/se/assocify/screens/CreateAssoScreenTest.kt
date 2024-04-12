package com.github.se.assocify.screens

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.createAsso.CreateAssoScreen
import com.github.se.assocify.ui.screens.createAsso.CreateAssoViewmodel
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class CreateAssoScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val bigList =
      listOf(
          User("1", "jean", Role("com")),
          User("2", "roger", Role("tres")),
          User("1", "jean", Role("com")),
          User("1", "jean", Role("com")),
          User("1", "jean", Role("com")),
          User("1", "jean", Role("com")),
          User("1", "jean", Role("com")),
          User("1", "jean", Role("com")),
          User("2", "roger", Role("tres")),
          User("2", "roger", Role("tres")),
          User("2", "roger", Role("tres")),
          User("2", "roger", Role("tres")),
          User("2", "roger", Role("tres")),
          User("2", "roger", Role("tres")))
  private val smallList = listOf(User("1", "jean", Role("com")), User("2", "roger", Role("tres")))

  private val bigView = CreateAssoViewmodel(bigList)
  private val smallView = CreateAssoViewmodel(smallList)

  @Test
  fun displaySmall() {
    composeTestRule.setContent { CreateAssoScreen(mockNavActions, smallView) }
    with(composeTestRule) {
      onNodeWithTag("createAssoScreen").assertIsDisplayed()
      onNodeWithTag("TopAppBar").assertIsDisplayed()
      onNodeWithTag("logo").assertIsDisplayed()
      onNodeWithTag("name").assertIsDisplayed()
      onNodeWithTag("MemberList").assertIsDisplayed()
      onAllNodesWithTag("MemberListItem").assertCountEquals(2)
      onNodeWithTag("addMember").assertIsDisplayed()
      onNodeWithTag("create").assertIsDisplayed()
    }
  }

  @Test
  fun testCreateButton() {
    composeTestRule.setContent { CreateAssoScreen(mockNavActions, smallView) }
    with(composeTestRule) {
      onNodeWithTag("create").performClick()
      verify { mockNavActions.navigateTo(Destination.Home) }
    }
  }

  @Test
  fun testGoBackButton() {
    composeTestRule.setContent { CreateAssoScreen(mockNavActions, smallView) }
    with(composeTestRule) {
      onNodeWithTag("Back").performClick()
      verify { mockNavActions.back() }
    }
  }
}
