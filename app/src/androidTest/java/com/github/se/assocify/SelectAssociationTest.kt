package com.github.se.assocify

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.screens.DisplayOrganizationScreen
import com.github.se.assocify.screens.SelectAssociationScreen
import com.github.se.assocify.ui.screens.SelectAssociation
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SelectAssociationTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
    @get:Rule val composeTestRule = createComposeRule()
    val registeredAssociation = listOf("CLIC", "GAME*")

    /**
     * This test checks if the "Create new organization" button is displayed
     */
    @Test
    fun testCreateNewOrganizationButton() {
        composeTestRule.setContent { SelectAssociation(registeredAssociation) }
        ComposeScreen.onComposeScreen<SelectAssociationScreen>(composeTestRule){
            createOrgaButton{
                assertIsDisplayed()
                hasClickAction()
            }
        }
    }

    /**
     * This test checks if the search organization field is displayed
     */
    @Test
    fun testSearchOrganization() {
        composeTestRule.setContent { SelectAssociation(registeredAssociation) }
        ComposeScreen.onComposeScreen<SelectAssociationScreen>(composeTestRule){
            searchOrganization{
                assertIsDisplayed()

            }
        }
    }

    /**
     * This test checks if the registered organization list is correctly displayed
     */
    @Test
    fun testRegisteredOrganizationList() {
        composeTestRule.setContent { SelectAssociation(registeredAssociation) }
        ComposeScreen.onComposeScreen<SelectAssociationScreen>(composeTestRule){
            registeredList{
                assertIsDisplayed()
                hasScrollAction()
            }
        }
        // Check if the organizations are displayed
        val organizations = listOf("CLIC", "GAME*")
        organizations.forEach { organization ->
            composeTestRule
                .onNodeWithText(organization)
                .assertIsDisplayed()
        }
    }

    /**
     * This test checks if the message is displayed when you're not registered to any organization
     */
    @Test
    fun testNoRegisteredOrganization() {
        composeTestRule.setContent { SelectAssociation(emptyList()) }
        // Find the text node with the expected message and assert it is displayed
        composeTestRule
            .onNodeWithText("You are not registered to any organization.")
            .assertIsDisplayed()

    }

    /**
     * This test checks if the organization name and icon are displayed
     */
    @Test
    fun testDisplayOrganization(){
        composeTestRule.setContent { SelectAssociation(registeredAssociation) }
        ComposeScreen.onComposeScreen<DisplayOrganizationScreen>(composeTestRule){
            organizationName{
                assertIsDisplayed()
            }
            organizationIcon{
                assertIsDisplayed()
            }
        }
    }

}