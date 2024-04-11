package com.github.se.assocify.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.selectAssoc.SelectAssociation
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import io.mockk.impl.annotations.RelaxedMockK
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
  val registeredAssociation = listOf("CLIC", "GAME*")

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockAssocAPI: AssociationAPI

  /** This test checks if the "Create new organization" button is displayed */
  @Test
  fun testCreateNewOrganizationButton() {
    composeTestRule.setContent {
      SelectAssociation(registeredAssociation, mockNavActions, mockAssocAPI)
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
      SelectAssociation(registeredAssociation, mockNavActions, mockAssocAPI)
    }
    ComposeScreen.onComposeScreen<SelectAssociationScreenTest>(composeTestRule) {
      searchOrganization { assertIsDisplayed() }
    }
  }

  /** This test checks if the registered organization list is correctly displayed */
  @Test
  fun testRegisteredOrganizationList() {
    composeTestRule.setContent {
      SelectAssociation(registeredAssociation, mockNavActions, mockAssocAPI)
    }
    ComposeScreen.onComposeScreen<SelectAssociationScreenTest>(composeTestRule) {
      registeredList {
        assertIsDisplayed()
        hasScrollAction()
      }
    }
    // Check if the organizations are displayed
    val organizations = listOf("CLIC", "GAME*")
    organizations.forEach { organization ->
      composeTestRule.onNodeWithText(organization).assertIsDisplayed()
    }
  }

  /** This test checks if the message is displayed when you're not registered to any organization */
  @Test
  fun testNoRegisteredOrganization() {
    composeTestRule.setContent { SelectAssociation(emptyList(), mockNavActions, mockAssocAPI) }
    // Find the text node with the expected message and assert it is displayed
    composeTestRule.onNodeWithText("There is no organization to display.").assertIsDisplayed()
  }

  /** This test checks if the organization name and icon are displayed */
  @Test
  fun testDisplayOrganization() {
    composeTestRule.setContent {
      SelectAssociation(registeredAssociation, mockNavActions, mockAssocAPI)
    }
    ComposeScreen.onComposeScreen<DisplayOrganizationScreenTest>(composeTestRule) {
      organizationName { assertIsDisplayed() }
      organizationIcon { assertIsDisplayed() }
    }
  }
}
