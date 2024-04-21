package com.github.se.assocify.composables

import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.assocify.ui.composables.PhotoSelectionSheet
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReceiptScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private var visible = true
  private var testUri = Uri.parse("content://test")

  fun setImageUri(uri: Uri?) {
    testUri = uri
  }

  @Before
  fun testSetup() {
    composeTestRule.setContent {
      PhotoSelectionSheet(
          visible = visible,
          hideSheet = { visible = false },
          setImageUri = { setImageUri(it) },
          signalCameraPermissionDenied = {})
    }
  }

  @Test
  fun display() {
    with(composeTestRule) {
      onNodeWithText("Take photo").assertIsDisplayed()
      onNodeWithText("Select image").assertIsDisplayed()
      onNodeWithText("Choose option").assertIsDisplayed()
    }
  }
}
