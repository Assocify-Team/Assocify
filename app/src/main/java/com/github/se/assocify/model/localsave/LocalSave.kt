package com.github.se.assocify.model.localsave

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.github.se.assocify.MainActivity
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.Theme
import com.github.se.assocify.ui.theme.ThemeViewModel

class LocalSave(private val activity: MainActivity, private val themeVM: ThemeViewModel) {

  private val ASSOCIFY_PREF = "com.github.se.assocify.PREFERENCE_FILE_KEY"
  private val USER_PREF = "user_uid"
  private val ASSOC_PREF = "association_uid"
  private val THEME_PREF = "theme"

  init {
    loadUserInfo()
  }

  fun saveUserInfo() {
    val sharedPref: SharedPreferences =
        activity.getSharedPreferences(ASSOCIFY_PREF, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString(USER_PREF, CurrentUser.userUid)
    editor.putString(ASSOC_PREF, CurrentUser.associationUid)
    editor.putString(THEME_PREF, themeVM.theme.value.name)
    editor.apply()
  }

  fun saveAssociation() {
    val sharedPref: SharedPreferences =
        activity.getSharedPreferences(ASSOCIFY_PREF, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString(ASSOC_PREF, CurrentUser.associationUid)
    editor.apply()
  }

  fun saveTheme() {
    val sharedPref: SharedPreferences =
        activity.getSharedPreferences(ASSOCIFY_PREF, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString(THEME_PREF, themeVM.theme.value.name)
    editor.apply()
  }

  fun loadUserInfo() {
    loadTheme()
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

  fun loadTheme() {
    val sharedPref: SharedPreferences =
        activity.getSharedPreferences(ASSOCIFY_PREF, Context.MODE_PRIVATE)
    themeVM.setTheme(Theme.fromString(sharedPref.getString(THEME_PREF, null)))
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

  fun clearSavedTheme() {
    val sharedPref: SharedPreferences =
        activity.getSharedPreferences(ASSOCIFY_PREF, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.remove(THEME_PREF)
    editor.apply()
  }
}
