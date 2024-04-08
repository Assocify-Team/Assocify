package com.github.se.assocify.model.entities

import android.net.Uri
import java.time.LocalDate

sealed class MaybeRemotePhoto {
  data class LocalFile(val filePath: Uri) : MaybeRemotePhoto()

  data class Remote(val path: String) : MaybeRemotePhoto()
}

data class Receipt(
    val uid: String,
    val date: LocalDate,
    val title: String,
    val notes: String,
    val photo: MaybeRemotePhoto,
)
