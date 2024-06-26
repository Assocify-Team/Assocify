package com.github.se.assocify.model.database

import android.net.Uri
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.MaybeRemotePhoto
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.Status
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import java.nio.file.Path
import java.time.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class ReceiptAPI(private val db: SupabaseClient, cachePath: Path) : SupabaseApi() {
  // The image fetcher. Caches for 1 minute (60000 milliseconds)
  private val imageCacher = ImageCacher(60_000, cachePath, db.storage["receipt"])

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

  /// The list of receipts owned by the user
  private var userReceipts: List<Receipt>? = null
  // The list of *all* receipts (if the user doesn't have permission to see all receipts, this
  // will be the same as userReceipts)
  private var receipts: List<Receipt>? = null
  /// The user's UID
  private var userUid: String? = CurrentUser.userUid
  private var associationUid: String? = CurrentUser.associationUid

  init {
    updateCaches({ _, _ -> }, { _, _ -> })
  }

  private fun updateReceiptInList(receipt: Receipt, receipts: List<Receipt>): List<Receipt> {
    val index = receipts.indexOfFirst { r -> r.uid == receipt.uid }
    return if (index != -1) {
      receipts.toMutableList().apply { set(index, receipt) }
    } else {
      receipts + receipt
    }
  }

  private fun deleteReceiptInList(id: String, receipts: List<Receipt>): List<Receipt> {
    return receipts.filter { r -> r.uid != id }
  }

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
    receipt.photo?.let { photo ->
      when (photo) {
        is MaybeRemotePhoto.LocalFile -> {
          imageCacher.uploadImage(
              "${receipt.uid}.jpg",
              photo.uri,
              { onPhotoUploadSuccess(true) },
              { onFailure(false, it) })
        }
        is MaybeRemotePhoto.Remote -> {
          onPhotoUploadSuccess(false)
        }
      }
    } ?: onPhotoUploadSuccess(false)

    tryAsync({ onFailure(true, it) }) {
      val sreceipt = SupabaseReceipt.fromReceipt(receipt)
      db.from("receipt").upsert(sreceipt)
      db.from("receipt_status").upsert(LinkedReceiptStatus(sreceipt.uid, receipt.status))

      // Update the cache
      if (CurrentUser.userUid != userUid || CurrentUser.associationUid != associationUid) {
        // Invalidate cache if the user changed
        associationUid = null
        userReceipts = null
        receipts = null
      }

      if (userUid == sreceipt.userId) {
        userReceipts = userReceipts?.let { updateReceiptInList(receipt, it) }
      }
      receipts = receipts?.let { updateReceiptInList(receipt, it) }

      onReceiptUploadSuccess()
    }
  }

  /**
   * Fetches the image of a receipt. If the image is not cached, it will be downloaded from the
   * network, and stored in disk cache.
   *
   * @param receiptUid the uid of the receipt to fetch the image of
   * @param onSuccess called when the image is fetched successfully with the URI of the image
   * @param onFailure called when the fetch fails with the exception that occurred
   */
  fun getReceiptImage(
      receiptUid: String,
      onSuccess: (Uri) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    imageCacher.fetchImage(
        "$receiptUid.jpg", { onSuccess(Uri.fromFile(it.toFile())) }, { onFailure(it) })
  }

  private fun updateUserCache(onSuccess: (List<Receipt>) -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      userReceipts =
          db.from("receipt")
              .select(columns) {
                filter {
                  SupabaseReceipt::userId eq CurrentUser.userUid
                  SupabaseReceipt::associationId eq CurrentUser.associationUid
                }
              }
              .decodeList<SupabaseReceipt>()
              .map { it.toReceipt() }
      userUid = CurrentUser.userUid
      associationUid = CurrentUser.associationUid

      onSuccess(userReceipts!!)
    }
  }

  private fun updateCache(onSuccess: (List<Receipt>) -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      receipts =
          db.from("receipt")
              .select(columns) {
                filter { SupabaseReceipt::associationId eq CurrentUser.associationUid }
              }
              .decodeList<SupabaseReceipt>()
              .map { it.toReceipt() }
      userUid = CurrentUser.userUid
      associationUid = CurrentUser.associationUid
      onSuccess(receipts!!)
    }
  }

  /**
   * Updates the caches of receipts. This is useful when the user wants to refresh the list of
   * receipts.
   *
   * @param onSuccess called when the update is successful with the list of receipts. The boolean
   *   parameter indicates whether the user's receipts were updated (`true`), or the global receipts
   *   (`false`). Thus, this lambda will be called up to 2 times.
   * @param onFailure called when the update fails with the exception that occurred. The boolean
   *   parameter indicates whether the user's receipts were updated (`true`), or the global receipts
   *   (`false`). Thus, this lambda will be called up to 2 times.
   */
  fun updateCaches(
      onSuccess: (Boolean, List<Receipt>) -> Unit,
      onFailure: (Boolean, Exception) -> Unit
  ) {
    updateUserCache({ onSuccess(true, it) }, { onFailure(true, it) })

    updateCache({ onSuccess(false, it) }, { onFailure(false, it) })
  }

  /**
   * Gets all receipts created by the current user.
   *
   * @param onSuccess called when the fetch succeeds with the list of receipts
   * @param onFailure called when the fetch fails with the exception that occurred
   */
  fun getUserReceipts(onSuccess: (List<Receipt>) -> Unit, onFailure: (Exception) -> Unit) {
    if (userReceipts != null &&
        userUid == CurrentUser.userUid &&
        associationUid == CurrentUser.associationUid) {
      onSuccess(userReceipts!!)
      return
    }

    updateUserCache(onSuccess, onFailure)
  }

  /**
   * Gets a receipt by its ID.
   *
   * @param id the ID of the receipt to get
   * @param onSuccess called when the receipt is fetched successfully
   * @param onFailure called when the fetch fails with the exception that occurred
   */
  fun getReceipt(id: String, onSuccess: (Receipt) -> Unit, onFailure: (Exception) -> Unit) {
    val getReceiptFromCache = { receipts: List<Receipt>? ->
      receipts!!.find { it.uid == id }?.let { onSuccess(it) }
          ?: onFailure(Exception("Receipt not found"))
    }

    if (receipts != null &&
        userUid == CurrentUser.userUid &&
        associationUid == CurrentUser.associationUid) {
      getReceiptFromCache(receipts)
      return
    }

    updateCache(getReceiptFromCache, onFailure)
  }

  /**
   * Gets *all* receipts created by *all* users, if the current user has permissions to do so.
   *
   * @param onSuccess called whenever the fetch is successful with the list of receipts
   * @param onFailure called whenever an error has occurred with the exception that occurred
   */
  fun getAllReceipts(onSuccess: (List<Receipt>) -> Unit, onFailure: (Exception) -> Unit) {
    if (receipts != null &&
        userUid == CurrentUser.userUid &&
        associationUid == CurrentUser.associationUid) {
      onSuccess(receipts!!)
      return
    }

    updateCache(onSuccess, onFailure)
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
      // Update the cache
      userReceipts = userReceipts?.let { deleteReceiptInList(id, it) }
      receipts = receipts?.let { deleteReceiptInList(id, it) }
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
              userId = receipt.userId,
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
            photo = MaybeRemotePhoto.Remote(this.uid),
            userId = this.userId)
  }

  @Serializable private data class ReceiptStatus(val status: Status)

  @Serializable
  private data class LinkedReceiptStatus(
      @SerialName("receipt_id") val receiptId: String,
      val status: Status
  )
}
