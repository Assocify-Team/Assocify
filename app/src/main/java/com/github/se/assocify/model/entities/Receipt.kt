package com.github.se.assocify.model.entities

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.CircleNotifications
import androidx.compose.ui.graphics.vector.ImageVector
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
  Pending,
  Approved,
  Reimbursed,
  Archived;

  fun getIcon(): ImageVector {
    return when (this) {
      Status.Pending -> Icons.Filled.CircleNotifications
      Status.Approved -> Icons.Filled.AccessTimeFilled
      Status.Reimbursed -> Icons.Filled.CheckCircle
      Status.Archived -> Icons.Filled.Circle
    }
  }
}
