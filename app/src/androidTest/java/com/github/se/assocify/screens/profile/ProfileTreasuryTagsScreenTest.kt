package com.github.se.assocify.screens.profile

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.profile.treasuryTags.ProfileTreasuryTagsScreen
import com.github.se.assocify.ui.screens.profile.treasuryTags.ProfileTreasuryTagsViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileTreasuryTagsScreenTest :
    TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private val navActions = mockk<NavigationActions>()
  private var goBack = false

  private val accCats = listOf(AccountingCategory("1", "cat1"), AccountingCategory("2", "cat2"))

  private val mockAccountingCategoryAPI =
      mockk<AccountingCategoryAPI>() {
        every { getCategories(any(), any(), any()) } answers
            {
              val onSuccess = secondArg<(List<AccountingCategory>) -> Unit>()
              onSuccess(accCats)
            }
      }

  @Before
  fun testSetup() {
    CurrentUser.userUid = "1"
    CurrentUser.associationUid = "asso"

    every { navActions.back() } answers { goBack = true }

    composeTestRule.setContent {
      ProfileTreasuryTagsScreen(
          navActions = navActions,
          ProfileTreasuryTagsViewModel(mockAccountingCategoryAPI, navActions))
    }
  }

  @Test
  fun display() {
    with(composeTestRule) {
      onNodeWithTag("TreasuryTags Screen").assertIsDisplayed()
      onNodeWithText("Add a new tag").assertIsDisplayed()
      onNodeWithTag("addTagButton").assertIsDisplayed().assertHasClickAction()
      accCats.forEach { onNodeWithText(it.name).assertIsDisplayed() }
      onAllNodesWithTag("editTagButton").assertCountEquals(accCats.size)
      onAllNodesWithTag("deleteTagButton").assertCountEquals(accCats.size)
    }
  }

  @Test
  fun goBack() {
    with(composeTestRule) {
      onNodeWithTag("backButton").performClick()
      assert(goBack)
    }
  }
}
