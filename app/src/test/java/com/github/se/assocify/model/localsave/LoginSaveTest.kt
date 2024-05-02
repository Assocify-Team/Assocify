package com.github.se.assocify.model.localsave

import android.content.Context
import android.content.SharedPreferences
import com.github.se.assocify.MainActivity
import com.github.se.assocify.model.CurrentUser
import io.mockk.every
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MockKExtension.ConfirmVerification
class LoginSaveTest {
  @get:Rule val mockkRule = MockKRule(this)

  var user: String? = null
  var assoc: String? = null

  val editor: SharedPreferences.Editor =
      mockk<SharedPreferences.Editor> {
        every { putString("user_uid", any()) } answers
            {
              user = secondArg()
              this@mockk
            }

        every { putString("association_uid", any()) } answers
            {
              assoc = secondArg()
              this@mockk
            }

        every { remove("user_uid") } answers
            {
              user = null
              this@mockk
            }

        every { remove("association_uid") } answers
            {
              assoc = null
              this@mockk
            }

        every { apply() } answers {}
      }
  val prefs: SharedPreferences =
      mockk<SharedPreferences> {
        every { edit() } answers { editor }

        every { getString("user_uid", null) } answers { user }

        every { getString("association_uid", null) } answers { assoc }
      }

  val activity: MainActivity =
      mockk<MainActivity> {
        every {
          getSharedPreferences("com.github.se.assocify.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        } answers { prefs }
      }

  val loginSaver = LoginSave(activity)

  @Before
  fun setup() {
    CurrentUser.userUid = "testUser"
    CurrentUser.associationUid = "testAssociation"
  }

  @Test
  fun testSaveUserInfo() {
    loginSaver.saveUserInfo()
    assert(user == "testUser")
    assert(assoc == "testAssociation")
  }

  @Test
  fun testSaveAssociation() {
    loginSaver.saveAssociation()
    assert(user == null)
    assert(assoc == "testAssociation")
  }

  @Test
  fun testLoadUserInfo() {
    user = "newUser"
    assoc = "newAssoc"
    loginSaver.loadUserInfo()
    assert(CurrentUser.userUid == user)
    assert(CurrentUser.associationUid == assoc)
  }

  @Test
  fun testClearSavedUserInfo() {
    user = "newUser"
    assoc = "newAssoc"
    loginSaver.clearSavedUserInfo()
    assert(user == null)
    assert(assoc == null)
  }

  @Test
  fun testClearSavedAssociation() {
    user = "newUser"
    assoc = "newAssoc"
    loginSaver.clearSavedAssociation()
    assert(user == "newUser")
    assert(assoc == null)
  }
}
