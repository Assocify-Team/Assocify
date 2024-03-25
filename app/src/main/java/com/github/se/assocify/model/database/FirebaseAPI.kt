package com.github.se.assocify.model.database

import com.google.firebase.firestore.FirebaseFirestore

abstract class FirebaseApi(val db: FirebaseFirestore) {

  // Define common properties
  abstract val collectionName: String

  fun getNewId() = db.collection(collectionName).document().id

  fun delete(id: String) = db.collection(collectionName).document(id).delete()
}


