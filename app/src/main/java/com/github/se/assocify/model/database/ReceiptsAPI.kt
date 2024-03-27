package com.github.se.assocify.model.database

import android.net.Uri
import com.github.se.assocify.model.entities.MaybeRemotePhoto
import com.github.se.assocify.model.entities.Receipt
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDate

class ReceiptsAPI(userId: String, basePath: String, storage: FirebaseStorage, db: FirebaseFirestore) : FirebaseApi(userId, db) {
    override val collectionName: String = "$basePath/receipts"
    private val userCollection = "$collectionName/$userId/list"
    private val storageReference = storage.getReference(userCollection)
    private val dbReference = db.collection(userCollection)

    // TODO: Progress callbacks; when the photo is uploaded, and when the receipt itself is uploaded.
    // More generally, don't return a Unit.
    fun uploadReceipt(receipt: Receipt): Task<Unit> {
        val photoUpload = when (receipt.photo) {
            is MaybeRemotePhoto.LocalFile -> {
                storageReference.child(receipt.uid).putFile(receipt.photo.filePath).continueWith {}
            }

            is MaybeRemotePhoto.Remote -> {
                Tasks.forResult(Unit)
            }
        }

        val receiptUpload = dbReference.document(receipt.uid).set(receipt)

        return receiptUpload.continueWithTask {
            photoUpload
        }
    }

    fun getUserReceipts(): Task<List<Receipt>> =
        dbReference.get().continueWith { query ->
            query.result.documents.map {
                it.toObject(FirestoreReceipt::class.java)!!.toReceipt(it.id)
            }
        }

    fun getAllUserIds(): Task<List<String>> =
        db.collection(collectionName).get().continueWith { query ->
            query.result.documents.map {
                it.id
            }
        }

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