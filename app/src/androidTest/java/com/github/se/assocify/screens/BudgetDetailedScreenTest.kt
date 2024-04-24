package com.github.se.assocify.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.TVA
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetDetailedScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BudgetDetailedScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
    @get:Rule val composeTestRule = createComposeRule()
    @get:Rule val mockkRule = MockKRule(this)

    @RelaxedMockK
    lateinit var mockNavActions: NavigationActions
    val subCategory =
        AccountingSubCategory("subCategoryUid", "Logistics Pole", AccountingCategory("Pole"), 1205)
    val budgetItems = listOf(
        BudgetItem(
            "1", "pair of scissors", 5, TVA.TVA_8, "scissors for paper cutting", subCategory
        ),
        BudgetItem("2", "sweaters", 1000, TVA.TVA_8, "order for 1000 sweaters", subCategory),
        BudgetItem("3", "chairs", 200, TVA.TVA_8, "order for 200 chairs", subCategory)
    )
    @Before
    fun setup() {
        CurrentUser.userUid = "userId"
        CurrentUser.associationUid = "associationId"
        composeTestRule.setContent { BudgetDetailedScreen("subcategoryuid", mockNavActions) }
    }

    /**
     * Tests if the nodes are displayed
     */
    @Test
    fun testDisplay() {
        // Test the accounting screen
        with(composeTestRule) {
            onNodeWithTag("BudgetDetailedScreen").assertIsDisplayed()
            onNodeWithTag("filterRowDetailed").assertIsDisplayed()
            onNodeWithTag("totalItems").assertIsDisplayed()
            onNodeWithTag("yearListTag").assertIsDisplayed()
            onNodeWithTag("tvaListTag").assertIsDisplayed()
            budgetItems.forEach { onNodeWithTag("displayItem${it.uid}").assertIsDisplayed() }
        }
    }

    /**
     * Tests if the total amount correspond to the sum of the items
     */
    @Test
    fun testTotalAmount(){
        // Test the accounting screen
        with(composeTestRule) {
            onNodeWithTag("totalItems").assertIsDisplayed()
            var total = 0
            budgetItems.forEach { total += it.amount }
            onNodeWithText(total.toString())
        }
    }


}