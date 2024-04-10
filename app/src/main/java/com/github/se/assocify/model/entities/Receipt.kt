package com.github.se.assocify.model.entities

import java.time.LocalDate

sealed class MaybeRemotePhoto {
  data class LocalFile(val filePath: String) : MaybeRemotePhoto()

  data class Remote(val path: String) : MaybeRemotePhoto()
}

data class Receipt(
    val uid: String,
    val date: LocalDate,
    val cents: Int,
    val phase: Phase,
    val title: String,
    val notes: String,
    val photo: MaybeRemotePhoto,
)

enum class Phase {
  Unapproved,
  Approved,
  PaidBack,
  Archived,
}
