package com.github.se.assocify.model.entities

import android.net.Uri
import java.time.LocalDate

sealed class MaybeRemotePhoto {
  data class LocalFile(val uri: Uri) : MaybeRemotePhoto()

  data class Remote(val path: String) : MaybeRemotePhoto()
}

data class Receipt(
    val uid: String,
    val title: String,
    val description: String,
    val date: LocalDate,
    val cents: Int,
    val incoming: Boolean,
    val status: Status,
    val photo: MaybeRemotePhoto?,
)

enum class Status {
  Unapproved,
  Approved,
  PaidBack,
  Archived,
}
