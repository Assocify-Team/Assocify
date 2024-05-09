package com.github.se.assocify.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.selectAssociation.DisplayOrganization
import com.github.se.assocify.ui.screens.selectAssociation.SelectAssociationScreen
import com.github.se.assocify.ui.screens.selectAssociation.SelectAssociationViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import java.time.LocalDate
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
  val node: KNode = onNode { hasTestTag("DisplayOrganizationScreen-testAssociation") }
}

/** This class represents the SelectAssociationScreen */
@RunWith(AndroidJUnit4::class)
class SelectAssociationTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockAssocAPI: AssociationAPI

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockUserAPI: UserAPI

  val testAssociation =
      Association("testAssociation", "an association to test the viewModel", "a", LocalDate.EPOCH)

  @Before
  fun setup() {
    CurrentUser.userUid = "adfslkj"
    CurrentUser.associationUid = "testAssocId"
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
      SelectAssociationScreen(mockNavActions, mockAssocAPI, mockUserAPI)
    }
    ComposeScreen.onComposeScreen<SelectAssociationScreenTest>(composeTestRule) {
      createOrgaButton {
        assertIsDisplayed()
        hasClickAction()
        performClick()
      }
    }
    // assert: the nav action has been called
    verify { mockNavActions.navigateTo(Destination.CreateAsso) }
  }

  /** This test checks if the search organization field is displayed */
  @Test
  fun testSearchOrganization() {
    CurrentUser.userUid = "testId"
    CurrentUser.associationUid = "testAssocId"
    composeTestRule.setContent {
      SelectAssociationScreen(mockNavActions, mockAssocAPI, mockUserAPI)
    }
    ComposeScreen.onComposeScreen<SelectAssociationScreenTest>(composeTestRule) {
      searchOrganization { assertIsDisplayed() }
    }
  }

  /** This test checks if the registered organization list is correctly displayed */
  @Test
  fun testRegisteredOrganizationList() {
    composeTestRule.setContent {
      SelectAssociationScreen(mockNavActions, mockAssocAPI, mockUserAPI)
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
      composeTestRule.onNodeWithText(organization.name).assertIsDisplayed()
    }
  }

  /** This test checks if the message is displayed when you're not registered to any organization */
  @Test
  fun testNoRegisteredOrganization() {
    every { mockAssocAPI.getAssociations(any(), any()) } answers
        {
          val onSuccessCallback = arg<(List<Association>) -> Unit>(0)
          val associations = emptyList<Association>()
          onSuccessCallback(associations)
        }
    composeTestRule.setContent {
      SelectAssociationScreen(mockNavActions, mockAssocAPI, mockUserAPI)
    }
    // Find the text node with the expected message and assert it is displayed
    composeTestRule.onNodeWithText("There are no organizations to display.").assertIsDisplayed()
  }

  /**
   * This test checks if the navigation to the home screen is triggered when selecting an
   * organization
   */
  @Test
  fun testNavigateToHomeWithSelectButton() {
    val model = SelectAssociationViewModel(mockAssocAPI, mockUserAPI, mockNavActions)
    composeTestRule.setContent { DisplayOrganization(organization = testAssociation, model) }
    ComposeScreen.onComposeScreen<DisplayOrganizationScreenTest>(composeTestRule) {
      node {
        assertIsDisplayed()
        performClick()
      }
    }
    verify(timeout = 250) { mockNavActions.onLogin(true) }
  }

  /**
   * This test checks if the navigation to the home screen is triggered when selecting an
   * organization
   */
  @Test
  fun testNavigateToHomeByClickingOnAssoc() {
    val model = SelectAssociationViewModel(mockAssocAPI, mockUserAPI, mockNavActions)
    composeTestRule.setContent { DisplayOrganization(organization = testAssociation, model) }
    composeTestRule.onNodeWithTag("DisplayOrganizationScreen-${testAssociation.uid}").performClick()
  }

  /* This test check if, when searching with the search bar the icons change */
  @Test
  fun testSearchBarWorksWithNoResult() {
    composeTestRule.setContent {
      SelectAssociationScreen(mockNavActions, mockAssocAPI, mockUserAPI)
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
