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


    @Before
    fun testSetup() {
        composeTestRule.setContent { SelectAssociation() }
    }
    /**
     * This test checks if the "Create new organization" button is displayed
     */
    @Test
    fun testCreateNewOrganizationButton() {
        ComposeScreen.onComposeScreen<SelectAssociationScreen>(composeTestRule){
            createOrgaButton{
                assertIsDisplayed()
                hasClickAction()
            }
        }
    }

    @Test
    fun testSearchOrganization() {
        ComposeScreen.onComposeScreen<SelectAssociationScreen>(composeTestRule){
            searchOrganization{
                assertIsDisplayed()

            }
        }
    }

    @Test
    fun testRegisteredOrganizationList() {
        ComposeScreen.onComposeScreen<SelectAssociationScreen>(composeTestRule){
            registeredList{
                assertIsDisplayed()
                hasScrollAction()
            }
        }
        val organizations = listOf("CLIC", "GAME*")
        organizations.forEach { organization ->
            composeTestRule
                .onNodeWithText(organization)
                .assertIsDisplayed()
        }
    }

    @Test
    fun testDisplayOrganization(){
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