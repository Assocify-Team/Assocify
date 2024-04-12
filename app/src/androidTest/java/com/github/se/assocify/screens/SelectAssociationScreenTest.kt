package com.github.se.assocify.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.selectAssoc.SelectAssociation
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This class represents the SelectAssociationScreen
 *
 * @param semanticsProvider the semantics provider
 */
class SelectAssociationScreenTest(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SelectAssociationScreenTest>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("SelectAssociationScreen") }) {
  val searchOrganization: KNode = child { hasTestTag("SearchOrganization") }
  val registeredList: KNode = child { hasTestTag("RegisteredList") }
  val createOrgaButton: KNode = child { hasTestTag("CreateNewOrganizationButton") }
  val searchOrgaButton: KNode = onNode { hasTestTag("SOB") }
  val arrowBackButton: KNode = onNode { hasTestTag("ArrowBackButton") }
  val helloText: KNode = onNode { hasTestTag("HelloText") }
}

/**
 * This class represents the DisplayOrganizationScreen used in SelectAssociation
 *
 * @param semanticsProvider the semantics provider
 */
class DisplayOrganizationScreenTest(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<DisplayOrganizationScreenTest>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("DisplayOrganizationScreen") }) {
  val organizationName: KNode = child { hasTestTag("OrganizationName") }
  val organizationIcon: KNode = child { hasTestTag("OrganizationIcon") }
}

/**
 * This class represents the SelectAssociationScreen
 *
 * @param semanticsProvider the semantics provider
 */
@RunWith(AndroidJUnit4::class)
class SelectAssociationTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockAssocAPI: AssociationAPI

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockUserAPI: UserAPI

  @RelaxedMockK lateinit var mockCurrentUser: CurrentUser

  @get:Rule val mockkRule = MockKRule(this)

  val testAssociation =
      Association(
          "testAssociation",
          "an association to test the viewModel",
          "a",
          "b",
          "c",
          emptyList(),
          emptyList())

  @Before
  fun setup() {
    val exception = Exception("the test does not work")
    every { mockAssocAPI.getAssociations(any(), any()) } answers
        {
          val onSuccessCallback = arg<(List<Association>) -> Unit>(0)
          val associations = listOf(testAssociation)
          onSuccessCallback(associations)
        }
  }

  /** This test checks if the "Create new organization" button is displayed */
  @Test
  fun testCreateNewOrganizationButton() {
    composeTestRule.setContent {
      SelectAssociation(mockNavActions, mockAssocAPI, mockUserAPI, mockCurrentUser)
    }
    ComposeScreen.onComposeScreen<SelectAssociationScreenTest>(composeTestRule) {
      createOrgaButton {
        assertIsDisplayed()
        hasClickAction()
      }
    }
  }

  /** This test checks if the search organization field is displayed */
  @Test
  fun testSearchOrganization() {
    composeTestRule.setContent {
      SelectAssociation(mockNavActions, mockAssocAPI, mockUserAPI, mockCurrentUser)
    }
    ComposeScreen.onComposeScreen<SelectAssociationScreenTest>(composeTestRule) {
      searchOrganization { assertIsDisplayed() }
    }
  }

  /** This test checks if the registered organization list is correctly displayed */
  @Test
  fun testRegisteredOrganizationList() {
    composeTestRule.setContent {
      SelectAssociation(mockNavActions, mockAssocAPI, mockUserAPI, mockCurrentUser)
    }
    ComposeScreen.onComposeScreen<SelectAssociationScreenTest>(composeTestRule) {
      registeredList {
        assertIsDisplayed()
        hasScrollAction()
      }
    }
    // Check if the organizations are displayed
    val organizations = listOf(testAssociation)
    organizations.forEach { organization ->
      composeTestRule.onNodeWithText(organization.getName()).assertIsDisplayed()
    }
  }

  /** This test checks if the message is displayed when you're not registered to any organization */
  @Test
  fun testNoRegisteredOrganization() {
    val exception = Exception("the test does not work")
    every { mockAssocAPI.getAssociations(any(), any()) } answers
        {
          val onSuccessCallback = arg<(List<Association>) -> Unit>(0)
          val associations = emptyList<Association>()
          onSuccessCallback(associations)
        }
    composeTestRule.setContent {
      SelectAssociation(mockNavActions, mockAssocAPI, mockUserAPI, mockCurrentUser)
    }
    // Find the text node with the expected message and assert it is displayed
    composeTestRule.onNodeWithText("There is no organization to display.").assertIsDisplayed()
  }

  /** This test checks if the organization name and icon are displayed */
  @Test
  fun testDisplayOrganization() {
    composeTestRule.setContent {
      SelectAssociation(mockNavActions, mockAssocAPI, mockUserAPI, mockCurrentUser)
    }
    ComposeScreen.onComposeScreen<DisplayOrganizationScreenTest>(composeTestRule) {
      organizationName { assertIsDisplayed() }
      organizationIcon { assertIsDisplayed() }
    }
  }

  /* This test check if, when searching with the search bar the icons change */
  @Test
  fun testSearchBarWorksWithNoResult() {
    composeTestRule.setContent {
      SelectAssociation(mockNavActions, mockAssocAPI, mockUserAPI, mockCurrentUser)
    }
    ComposeScreen.onComposeScreen<SelectAssociationScreenTest>(composeTestRule) {
      // Checking initial state
      searchOrgaButton { assertIsDisplayed() }
      arrowBackButton { assertIsNotDisplayed() }
      registeredList { assertIsDisplayed() }
      searchOrganization { assertIsDisplayed() }
      // Check what happens if clicking the organizing button
      searchOrgaButton { performClick() }
      searchOrgaButton { assertIsNotDisplayed() }
      arrowBackButton { assertIsDisplayed() }
      // Check what happens if clicking the back button
      arrowBackButton { performClick() }
      searchOrgaButton { assertIsDisplayed() }
      arrowBackButton { assertIsNotDisplayed() }
    }
  }
}

/*
    @Test
    fun testWithDifferentUserId(){
      every {mockUserAPI.getUser("testId", any(), any())} answers
          {
            val onSuccessCallback = arg<(User) -> Unit>(0)
            val user = User("Ciro", "cane", Role("president"))
            onSuccessCallback(user)
          }
      val currentUser = CurrentUser("testId", "testAssocId")
      composeTestRule.setContent { SelectAssociation(mockNavActions, mockAssocAPI, mockUserAPI, currentUser) }
      composeTestRule.onNodeWithText("Hello Tonno !!").assertIsNotDisplayed()
      composeTestRule.onNodeWithText("Hello Ciro !!").assertIsDisplayed()



}
*/
