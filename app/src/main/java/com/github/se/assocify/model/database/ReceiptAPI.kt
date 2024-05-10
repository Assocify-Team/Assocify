package com.github.se.assocify.model.database

import android.net.Uri
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.MaybeRemotePhoto
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.Status
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.downloadAuthenticatedTo
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import java.nio.file.Path
import java.time.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class ReceiptAPI(private val db: SupabaseClient, private val cachePath: Path) : SupabaseApi() {
  private val bucket = db.storage["receipt"]

  private val columns =
      Columns.raw(
          """
              *,
              receipt_status (
                status
              )
          """
              .trimIndent()
              .filter { it != '\n' })

  /**
   * Uploads a receipt to the database, as well as the image (if needed). Can create or update a
   * receipt.
   *
   * @param receipt the receipt to upload
   * @param onPhotoUploadSuccess called when the photo has been uploaded successfully. The parameter
   *   indicates whether an upload was actually performed (`true` if it is).
   * @param onReceiptUploadSuccess called when the receipt data has been uploaded successfully.
   * @param onFailure called when any upload has failed. The boolean parameter indicates whether it
   *   failed on the receipt or the image (`true` when the receipt failed). The second parameter is
   *   the exception that occurred.
   */
  fun uploadReceipt(
      receipt: Receipt,
      onPhotoUploadSuccess: (Boolean) -> Unit,
      onReceiptUploadSuccess: () -> Unit,
      onFailure: (Boolean, Exception) -> Unit
  ) {
    tryAsync({ onFailure(false, it) }) {
      receipt.photo?.let {
        when (it) {
          is MaybeRemotePhoto.LocalFile -> {
            bucket.upload(receipt.uid, it.uri, upsert = true)
            val imageCachePath = cachePath.resolve(receipt.uid + ".jpg")
            imageCachePath.toFile().delete()
            onPhotoUploadSuccess(true)
          }
          is MaybeRemotePhoto.Remote -> {
            onPhotoUploadSuccess(false)
          }
        }
      } ?: onPhotoUploadSuccess(false)
    }

    tryAsync({ onFailure(true, it) }) {
      val sreceipt = SupabaseReceipt.fromReceipt(receipt)
      db.from("receipt").upsert(sreceipt)
      db.from("receipt_status").upsert(LinkedReceiptStatus(sreceipt.uid, receipt.status))
      onReceiptUploadSuccess()
    }
  }

  /**
   * Fetches the image of a receipt. If the image is not cached, it will be downloaded from the
   * network, and stored in disk cache. TODO: limit the size of the cache NOTE: If the *same* image
   * is requested twice at the same time, the second request will return immediately, *incorrectly*!
   *
   * @param receipt the receipt to fetch the image of
   * @param onSuccess called when the image is fetched successfully with the URI of the image
   * @param onFailure called when the fetch fails with the exception that occurred
   */
  fun getReceiptImage(receipt: Receipt, onSuccess: (Uri) -> Unit, onFailure: (Exception) -> Unit) {
    val imageCachePath = cachePath.resolve(receipt.uid + ".jpg")
    if (imageCachePath.toFile().exists()) {
      onSuccess(Uri.fromFile(imageCachePath.toFile()))
      return
    }

    tryAsync(onFailure) {
      bucket.downloadAuthenticatedTo(receipt.uid, imageCachePath)
      onSuccess(Uri.fromFile(imageCachePath.toFile()))
    }
  }

  /**
   * Gets all receipts created by the current user.
   *
   * @param onSuccess called when the fetch succeeds with the list of receipts
   * @param onFailure called when the fetch fails with the exception that occurred
   */
  fun getUserReceipts(onSuccess: (List<Receipt>) -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      onSuccess(
          db.from("receipt")
              .select(columns) { filter { SupabaseReceipt::userId eq CurrentUser.userUid } }
              .decodeList<SupabaseReceipt>()
              .map { it.toReceipt() })
    }
  }

  /**
   * Gets a receipt by its ID.
   *
   * @param id the ID of the receipt to get
   * @param onSuccess called when the receipt is fetched successfully
   * @param onFailure called when the fetch fails with the exception that occurred
   */
  fun getReceipt(id: String, onSuccess: (Receipt) -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      val receipt =
          db.from("receipt")
              .select(columns) { filter { SupabaseReceipt::uid eq id } }
              .decodeSingle<SupabaseReceipt>()
              .toReceipt()
      onSuccess(receipt)
    }
  }

  /**
   * Gets *all* receipts created by *all* users, if the current user has permissions to do so.
   *
   * @param onSuccess called whenever the fetch is successful with the list of receipts
   * @param onFailure called whenever an error has occurred with the exception that occurred
   */
  fun getAllReceipts(onSuccess: (List<Receipt>) -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      val select = db.from("receipt").select(columns)
      onSuccess(select.decodeList<SupabaseReceipt>().map { it.toReceipt() })
    }
  }

  /**
   * Deletes a receipt by its ID.
   *
   * @param id the ID of the receipt to delete
   * @param onSuccess called when the deletion is successful
   * @param onFailure called when the deletion fails with the exception that occurred
   */
  fun deleteReceipt(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      db.from("receipt").delete { filter { SupabaseReceipt::uid eq id } }
      onSuccess()
    }
  }

  @Serializable
  private data class SupabaseReceipt(
      val uid: String,
      val date: String,
      val cents: Int,
      val title: String,
      val description: String,
      @SerialName("user_id") val userId: String,
      @SerialName("association_id") val associationId: String,
      @SerialName("receipt_status") val receiptStatus: ReceiptStatus? = null,
  ) {
    companion object {
      fun fromReceipt(receipt: Receipt) =
          SupabaseReceipt(
              uid = receipt.uid,
              date = receipt.date.toString(),
              cents = receipt.cents,
              title = receipt.title,
              description = receipt.description,
              userId = CurrentUser.userUid!!,
              associationId = CurrentUser.associationUid!!)
    }

    fun toReceipt(): Receipt =
        Receipt(
            uid = this.uid,
            date = LocalDate.parse(this.date),
            cents = this.cents,
            title = this.title,
            description = this.description,
            status = this.receiptStatus!!.status,
            photo = MaybeRemotePhoto.Remote(this.uid))
  }

  @Serializable private data class ReceiptStatus(val status: Status)

  @Serializable
  private data class LinkedReceiptStatus(
      @SerialName("receipt_id") val receiptId: String,
      val status: Status
  )
}
