package com.github.se.assocify.model.localsave

import android.content.Context
import android.content.SharedPreferences
import com.github.se.assocify.MainActivity
import com.github.se.assocify.model.CurrentUser

class LoginSave(private val activity: MainActivity) {

  private val ASSOCIFY_PREF = "com.github.se.assocify.PREFERENCE_FILE_KEY"
  private val USER_PREF = "user_uid"
  private val ASSOC_PREF = "association_uid"

  fun saveUserInfo() {
    val sharedPref: SharedPreferences =
        activity.getSharedPreferences(ASSOCIFY_PREF, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString(USER_PREF, CurrentUser.userUid)
    editor.putString(ASSOC_PREF, CurrentUser.associationUid)
    editor.apply()
  }

  fun saveAssociation() {
    val sharedPref: SharedPreferences =
        activity.getSharedPreferences(ASSOCIFY_PREF, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString(ASSOC_PREF, CurrentUser.associationUid)
    editor.apply()
  }

  fun loadUserInfo() {
    loadUserUid()
    loadAssociation()
  }

  fun loadUserUid() {
    val sharedPref: SharedPreferences =
        activity.getSharedPreferences(ASSOCIFY_PREF, Context.MODE_PRIVATE)
    CurrentUser.userUid = sharedPref.getString(USER_PREF, null)
  }

  fun loadAssociation() {
    val sharedPref: SharedPreferences =
        activity.getSharedPreferences(ASSOCIFY_PREF, Context.MODE_PRIVATE)
    CurrentUser.associationUid = sharedPref.getString(ASSOC_PREF, null)
  }

  fun clearSavedUserInfo() {
    val sharedPref: SharedPreferences =
        activity.getSharedPreferences(ASSOCIFY_PREF, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.remove(USER_PREF)
    editor.remove(ASSOC_PREF)
    editor.apply()
  }

  fun clearSavedAssociation() {
    val sharedPref: SharedPreferences =
        activity.getSharedPreferences(ASSOCIFY_PREF, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.remove(ASSOC_PREF)
    editor.apply()
  }
}
