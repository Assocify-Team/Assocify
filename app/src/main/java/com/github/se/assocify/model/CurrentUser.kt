package com.github.se.assocify.model

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object CurrentUser {
  var userUid: String? = null
  var associationUid: String? = null
  private var _associationLogo: StateFlow<Uri?> = MutableStateFlow(null)
  val associationLogo: StateFlow<Uri?> = _associationLogo

  fun setAssociationLogo(uri: Uri?) {
    (_associationLogo as MutableStateFlow).value = uri
  }
}
