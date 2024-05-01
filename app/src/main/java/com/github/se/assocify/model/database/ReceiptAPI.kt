package com.github.se.assocify.model.database

import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.MaybeRemotePhoto
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.Status
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import java.time.LocalDate
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class ReceiptAPI(private val db: SupabaseClient) : SupabaseApi() {
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
    scope.launch {
      try {
        receipt.photo?.let {
          when (it) {
            is MaybeRemotePhoto.LocalFile -> {
              bucket.upload(receipt.uid, it.uri, upsert = true)
              onPhotoUploadSuccess(true)
            }
            is MaybeRemotePhoto.Remote -> {
              onPhotoUploadSuccess(false)
            }
          }
        } ?: onPhotoUploadSuccess(false)
      } catch (e: Exception) {
        onFailure(false, e)
      }
    }

    scope.launch {
      try {
        val sreceipt = SupabaseReceipt.fromReceipt(receipt)
        db.from("receipt").upsert(sreceipt)
        db.from("receipt_status").upsert(LinkedReceiptStatus(sreceipt.uid, receipt.status))
        onReceiptUploadSuccess()
      } catch (e: Exception) {
        onFailure(true, e)
      }
    }
  }

  /**
   * Gets all receipts created by the current user.
   *
   * @param onSuccess called when the fetch succeeds with the list of receipts
   * @param onError called when the fetch fails with the exception that occurred
   */
  fun getUserReceipts(onSuccess: (List<Receipt>) -> Unit, onError: (Exception) -> Unit) {
    scope.launch {
      try {
        onSuccess(
            db.from("receipt")
                .select(columns) { filter { SupabaseReceipt::userId eq CurrentUser.userUid } }
                .decodeList<SupabaseReceipt>()
                .map { it.toReceipt() })
      } catch (e: Exception) {
        onError(e)
      }
    }
  }

  /**
   * Gets a receipt by its ID.
   *
   * @param id the ID of the receipt to get
   * @param onSuccess called when the receipt is fetched successfully
   * @param onError called when the fetch fails with the exception that occurred
   */
  fun getReceipt(id: String, onSuccess: (Receipt) -> Unit, onError: (Exception) -> Unit) {
    scope.launch {
      try {
        val receipt =
            db.from("receipt")
                .select { filter { SupabaseReceipt::uid eq id } }
                .decodeAs<SupabaseReceipt>()
                .toReceipt()
        onSuccess(receipt)
      } catch (e: Exception) {
        onError(e)
      }
    }
  }

  /**
   * Gets *all* receipts created by *all* users, if the current user has permissions to do so.
   *
   * @param onSuccess called whenever the fetch is successful with the list of receipts
   * @param onError called whenever an error has occurred with the exception that occurred
   */
  fun getAllReceipts(onSuccess: (List<Receipt>) -> Unit, onError: (Exception) -> Unit) {
    scope.launch {
      try {
        val select = db.from("receipt").select(columns)
        onSuccess(select.decodeList<SupabaseReceipt>().map { it.toReceipt() })
      } catch (e: Exception) {
        onError(e)
      }
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
    scope.launch {
      try {
        db.from("receipt").delete { filter { SupabaseReceipt::uid eq id } }
        onSuccess()
      } catch (e: Exception) {
        onFailure(e)
      }
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
