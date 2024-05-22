package com.github.se.assocify.model.entities

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.CircleNotifications
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val status: Status,
    val photo: MaybeRemotePhoto?,
    val userId: String,
)

@Serializable
enum class Status {
  @SerialName("unapproved") Pending,
  @SerialName("approved") Approved,
  @SerialName("paid_back") Reimbursed,
  @SerialName("archived") Archived;

  fun getIcon(): ImageVector {
    return when (this) {
      Pending -> Icons.Filled.CircleNotifications
      Approved -> Icons.Filled.AccessTimeFilled
      Reimbursed -> Icons.Filled.CheckCircle
      Archived -> Icons.Filled.Circle
    }
  }
}
