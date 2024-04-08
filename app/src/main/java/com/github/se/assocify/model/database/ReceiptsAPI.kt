package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.MaybeRemotePhoto
import com.github.se.assocify.model.entities.Receipt
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDate

class ReceiptsAPI(userId: String, basePath: String, storage: FirebaseStorage, db: FirebaseFirestore) : FirebaseApi(userId, db) {
    override val collectionName: String = "$basePath/receipts"
    private val userCollection = "$collectionName/$userId/list"
    private val storageReference = storage.getReference(userCollection)
    private val dbReference = db.collection(userCollection)

    /**
     * Uploads a receipt to the database, as well as the image (if needed). Can create or update a receipt.
     *
     * @param receipt the receipt to upload
     * @param onPhotoUploadSuccess called when the photo has been uploaded successfully. The parameter indicates whether an upload was actually performed (`true` if it is).
     * @param onReceiptUploadSuccess called when the receipt data has been uploaded successfully.
     * @param onFailure called when any upload has failed. The boolean parameter indicates whether it failed on the receipt or the image (`true` when the receipt failed). The second parameter is the exception that occured.
     */
    fun uploadReceipt(receipt: Receipt, onPhotoUploadSuccess: (Boolean) -> Unit, onReceiptUploadSuccess: () -> Unit, onFailure: (Boolean, Exception) -> Unit) {
        when (receipt.photo) {
            is MaybeRemotePhoto.LocalFile -> {
                storageReference
                    .child(receipt.uid)
                    .putFile(receipt.photo.filePath)
                    .addOnSuccessListener { onPhotoUploadSuccess(true) }
                    .addOnFailureListener { onFailure(false, it) }
            }

            else -> {
                onPhotoUploadSuccess(false)
            }
        }

        dbReference
            .document(receipt.uid)
            .set(receipt)
            .addOnSuccessListener { onReceiptUploadSuccess() }
            .addOnFailureListener { onFailure(true, it) }
    }

    private fun parseReceiptList(snapshot: QuerySnapshot): List<Receipt> =
        snapshot.documents.map {
            it.toObject(FirestoreReceipt::class.java)!!.toReceipt(it.id)
        }

    /**
     * Gets all receipts created by the current user.
     */
    fun getUserReceipts(onSuccess: (List<Receipt>) -> Unit, onError: (Exception) -> Unit) =
        dbReference.get()
            .addOnSuccessListener { onSuccess(parseReceiptList(it)) }
            .addOnFailureListener { onError(it) }

    /**
     * Gets *all* receipts created by *all* users, if the current user has permissions to do so.
     *
     * @param onReceiptsFetched called whenever a new list is fetched. Will be called several times with new lists.
     * @param onError called whenever an error has occurred. The first parameter contains the ID of the user whose fetch failed (or `null` if all fetches failed).
     */
    fun getAllReceipts(onReceiptsFetched: (List<Receipt>) -> Unit, onError: (String?, Exception) -> Unit) =
        db.collection(collectionName).get().addOnSuccessListener { query ->
            query.documents.forEach { snapshot ->
                snapshot.reference.collection("list")
                    .get()
                    .addOnSuccessListener { onReceiptsFetched(parseReceiptList(it)) }
                    .addOnFailureListener { onError(snapshot.id, it) }
            }
        }.addOnFailureListener { onError(null, it) }

    private data class FirestoreReceipt(
        val date: String,
        val title: String,
        val notes: String,
        val photo: String,
    ) {
        constructor(from: Receipt) : this(from.date.toString(), from.title, from.notes, from.uid)

        fun toReceipt(uid: String) =
            Receipt(
                uid,
                LocalDate.parse(this.date),
                this.title,
                this.notes,
                MaybeRemotePhoto.Remote(photo),
            )
    }
}