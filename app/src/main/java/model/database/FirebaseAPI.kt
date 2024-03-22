package model.database

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore

abstract class FirebaseApi(val db: FirebaseFirestore) {

    // Define common properties
    abstract val collectionName: String

    // Define common methods
    fun get(id: String) = db.collection(collectionName).document(id)

    fun getAll() = db.collection(collectionName).get()

    fun add(data: Map<String, Any>) = db.collection(collectionName).add(data)

    fun update(id: String, data: Map<String, Any>) =
        db.collection(collectionName).document(id).update(data)

    fun delete(id: String) = db.collection(collectionName).document(id).delete()
}