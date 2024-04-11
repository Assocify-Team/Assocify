package com.github.se.assocify.model.entities

import java.time.LocalDate

sealed class MaybeRemotePhoto {
  data class LocalFile(val filePath: String) : MaybeRemotePhoto()

  data class Remote(val path: String) : MaybeRemotePhoto()
}

data class Receipt(
    val uid: String,
    val title: String,
    val description: String,
    val date: LocalDate,
    val payer: String, // User uid
    val cents: Int,
    val incoming: Boolean,
    val phase: Phase,
    val photo: MaybeRemotePhoto?,
)

enum class Phase {
  Unapproved,
  Approved,
  PaidBack,
  Archived,
}
