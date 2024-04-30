package com.github.se.assocify.model

object CurrentUser {
  var userUid: String? = null
  var associationUid: String? = null

}

fun isCurrentUser(userUid: String): Boolean {
  return CurrentUser.userUid == userUid
}