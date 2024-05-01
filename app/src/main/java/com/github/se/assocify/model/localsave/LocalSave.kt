package com.github.se.assocify.model.localsave

import android.content.Context
import android.content.SharedPreferences
import com.github.se.assocify.MainActivity

class LoginSave(private val activity: MainActivity) {

  private val ASSOCIFY_PREF = "com.github.se.assocify.PREFERENCE_FILE_KEY"
  private val USER_PREF = "user_uid"
  private val ASSOC_PREF = "association_uid"

  fun saveLoginInfo(userUid: String, associationUid: String) {
    val sharedPref: SharedPreferences =
        activity.getSharedPreferences(ASSOCIFY_PREF, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString(USER_PREF, userUid)
    editor.putString(ASSOC_PREF, associationUid)
    editor.apply()
  }

  fun saveCurrentAssociation(associationUid: String) {
    val sharedPref: SharedPreferences =
        activity.getSharedPreferences(ASSOCIFY_PREF, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString(ASSOC_PREF, associationUid)
    editor.apply()
  }

  fun getSavedUserUid(): String? {
    val sharedPref: SharedPreferences =
        activity.getSharedPreferences(ASSOCIFY_PREF, Context.MODE_PRIVATE)
    return sharedPref.getString(USER_PREF, null)
  }

  fun getSavedAssociationUid(): String? {
    val sharedPref: SharedPreferences =
        activity.getSharedPreferences(ASSOCIFY_PREF, Context.MODE_PRIVATE)
    return sharedPref.getString(ASSOC_PREF, null)
  }

  fun clearSavedLoginInfo() {
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
