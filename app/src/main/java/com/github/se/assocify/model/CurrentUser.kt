package com.github.se.assocify.model

import com.github.se.assocify.model.entities.User

object CurrentUser {
  var userUid: String? = null
  var associationUid: String? = null
  var user: User? = null
}
