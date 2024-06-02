package com.github.se.assocify.epics

import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.MaybeRemotePhoto
import com.github.se.assocify.model.entities.PermissionRole
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.model.localsave.LocalSave
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test is an end-to-end test for the second epic :
 *
 * As a member, I want to register a receipt to be able to be reimbursed later, and keep track of
 * the other receipts I have
 */
@RunWith(AndroidJUnit4::class)
class Epic2Test : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navController: TestNavHostController
  private lateinit var navActions: NavigationActions

  private val associations =
      listOf(
          Association("a", "asso1", "desc1", LocalDate.EPOCH),
          Association("b", "asso2", "desc2", LocalDate.EPOCH),
      )

  private val user = User("1", "user1")

  private var myReceipts =
      listOf(
          Receipt(
              "r1",
              "Receipt-1-name",
              "descR1",
              LocalDate.EPOCH,
              100,
              Status.Pending,
              MaybeRemotePhoto.Remote("r1"),
              "1"))

  private var aReceipts =
      myReceipts +
          Receipt(
              "r2",
              "Receipt-2-name",
              "descR2",
              LocalDate.EPOCH,
              10,
              Status.Pending,
              MaybeRemotePhoto.Remote("r2"),
              "2")

  private var bReceipts =
      listOf(
          Receipt(
              "r3",
              "Receipt-3-name",
              "descR3",
              LocalDate.EPOCH,
              200,
              Status.Pending,
              MaybeRemotePhoto.Remote("r3"),
              "3"))

  private var allReceipts = aReceipts + bReceipts

  private val associationAPI =
      mockk<AssociationAPI> {
        every { getAssociations(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<Association>) -> Unit>()
              onSuccessCallback.invoke(associations)
            }

        every { getAssociation(any(), any(), any()) } answers
            {
              val assoID = firstArg<String>()
              val onSuccessCallback = secondArg<(Association) -> Unit>()
              onSuccessCallback.invoke(associations.find { it.uid == assoID }!!)
            }
        every { getLogo(any(), any(), any()) } answers {}
      }

  private val userAPI =
      mockk<UserAPI> {
        every { getUser(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(User) -> Unit>()
              onSuccessCallback.invoke(user)
            }

        every { getCurrentUserAssociations(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<Association>) -> Unit>()
              onSuccessCallback.invoke(associations)
            }

        every { getCurrentUserRole(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(PermissionRole) -> Unit>()
              when (CurrentUser.associationUid) {
                "a" -> {
                  onSuccessCallback.invoke(PermissionRole("m", "a", RoleType.MEMBER))
                }
                "b" -> {
                  onSuccessCallback.invoke(PermissionRole("m", "b", RoleType.MEMBER))
                }
              }
            }

        every { getProfilePicture(any(), any(), any()) } answers {}
      }

  private val receiptAPI =
      mockk<ReceiptAPI> {
        every { getAllReceipts(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<Receipt>) -> Unit>()
              if (CurrentUser.associationUid == "a") {
                onSuccessCallback.invoke(aReceipts)
              } else {
                onSuccessCallback.invoke(bReceipts)
              }
            }

        every { getUserReceipts(any(), any()) } answers
            {
              val onSuccessCallback = firstArg<(List<Receipt>) -> Unit>()
              if (CurrentUser.associationUid == "a") {
                onSuccessCallback.invoke(myReceipts)
              } else {
                onSuccessCallback.invoke(emptyList())
              }
            }

        every { getReceipt(any(), any(), any()) } answers
            {
              val receipt = allReceipts.find { it.uid == firstArg() }!!
              val onSuccessCallback = secondArg<(Receipt) -> Unit>()
              onSuccessCallback.invoke(receipt)
            }

        every { getReceiptImage(any(), any(), any()) } answers
            {
              val onSuccessCallback = secondArg<(Uri) -> Unit>()
              onSuccessCallback.invoke(Uri.parse(firstArg<String>()))
            }

        every { uploadReceipt(any(), any(), any(), any()) } answers
            {
              val receipt = firstArg<Receipt>()
              myReceipts = myReceipts + receipt
              secondArg<(Boolean) -> Unit>().invoke(true)
              thirdArg<() -> Unit>().invoke()
            }
      }

  private val eventAPI = mockk<EventAPI>(relaxUnitFun = true)
  private val taskAPI = mockk<TaskAPI>(relaxUnitFun = true)
  private val budgetAPI = mockk<BudgetAPI>(relaxUnitFun = true)
  private val balanceAPI = mockk<BalanceAPI>(relaxUnitFun = true)
  private val loginSave = mockk<LocalSave>(relaxUnitFun = true)
  private val accountingCategoriesAPI = mockk<AccountingCategoryAPI>(relaxUnitFun = true)
  private val accountingSubCategoryAPI = mockk<AccountingSubCategoryAPI>(relaxUnitFun = true)

  @Before
  fun testSetup() {
    /*everyComposable { PhotoSelectionSheet(any(), any(), any(), any()) } answers {
       val setImageUri = secondArg<(Uri?) -> Unit>()
       setImageUri(testUri)
    }*/

    composeTestRule.setContent {
      CurrentUser.userUid = "1"
      CurrentUser.associationUid = "a"
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      navActions = NavigationActions(navController, loginSave)
      TestAssocifyApp(
          navController,
          navActions,
          userAPI,
          associationAPI,
          eventAPI,
          budgetAPI,
          balanceAPI,
          taskAPI,
          receiptAPI,
          accountingCategoriesAPI,
          accountingSubCategoryAPI,
          Destination.Treasury)
    }
  }

  @Test
  fun Epic2Test() {
    with(composeTestRule) {
      // go to profile to check what association I'm in and my role
      onNodeWithTag("mainNavBarItem/profile").assertIsDisplayed().performClick()
      val toProfile = navController.currentBackStackEntry?.destination?.route
      assert(toProfile == Destination.Profile.route)

      onNodeWithText("asso1").assertIsDisplayed()
      onNodeWithText("MEMBER").assertIsDisplayed()

      // go to treasury and see current receipts I have previously done (1)
      onNodeWithTag("mainNavBarItem/treasury").assertIsDisplayed().performClick()
      val toTreasury = navController.currentBackStackEntry?.destination?.route
      assert(toTreasury == Destination.Treasury.route)

      onNodeWithTag("treasuryScreen").assertIsDisplayed()
      onAllNodesWithText("Receipt-1-name").apply {
        fetchSemanticsNodes().forEachIndexed { i, _ -> get(i).assertIsDisplayed() }
      }
      onNodeWithTag("receiptItem-true-r1").performClick()

      // check that the receipt is correct and change its title
      onNodeWithTag("titleField").assertIsDisplayed().performClick().performTextInput("-changed")
      onNodeWithTag("saveButton").performScrollTo().assertIsDisplayed().performClick()
      onNodeWithText("Receipt-1-name-changed").assertIsDisplayed()

      // (since i'm a member, i shouldn't have access to budget and balance : not implemented yet)

      // add a receipt -- removed this part of the test because adding a picture couldn't be mocked
      /*
      onNodeWithTag("createReceipt").assertIsDisplayed().performClick()
      onNodeWithTag("titleField").performClick().performTextInput("Receipt-2-name")
      onNodeWithTag("amountField").performClick().performTextInput("10")

      onNodeWithTag("dateField").performClick()
      onNodeWithContentDescription("Switch to text input mode").performClick()
      onNodeWithContentDescription("Date", true).performClick().performTextInput("01012024")
      onNodeWithTag("datePickerDialogOk").performClick()
      onNodeWithTag("dateField").assertTextContains("01/01/2024")

        onNodeWithTag("editImageButton").performClick()

      onNodeWithTag("saveButton").performScrollTo().assertIsDisplayed().performClick()

      // check that receipt is here
      onNodeWithTag("ReceiptList").assertIsDisplayed()
      onNodeWithText("Receipt-2-name").assertIsDisplayed()
      */

      // change association
      onNodeWithTag("mainNavBarItem/profile").performClick()
      onNodeWithTag("associationDropdown").performClick()
      onNodeWithTag("DropdownItem-b").performClick()
      assert(CurrentUser.associationUid == "b")

      // go to check receipts are different
      onNodeWithTag("mainNavBarItem/treasury").assertIsDisplayed().performClick()
      onNodeWithText("Receipt-1-name-changed").assertDoesNotExist()

      // go back to asso1 and check that receipts are here
      onNodeWithTag("mainNavBarItem/profile").performClick()
      onNodeWithTag("associationDropdown").performClick()
      onNodeWithTag("DropdownItem-a").performClick()
      assert(CurrentUser.associationUid == "a")

      onNodeWithTag("mainNavBarItem/treasury").performClick()
      onNodeWithText("Receipt-1-name-changed").assertIsDisplayed()
    }
  }
}
