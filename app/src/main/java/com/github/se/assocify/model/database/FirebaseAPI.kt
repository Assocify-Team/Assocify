package com.github.se.assocify.model.database

import com.google.firebase.firestore.FirebaseFirestore
import com.github.se.assocify.model.entities.Association

abstract class FirebaseApi(val db: FirebaseFirestore) {

    // Define common properties
    abstract val collectionName: String


    fun getNewId() = db.collection(collectionName).document().id

    fun update(id: String, data: Map<String, Any>) =
        db.collection(collectionName).document(id).update(data)

    fun delete(id: String) = db.collection(collectionName).document(id).delete()

    abstract fun deserialize(data: String): Association


}