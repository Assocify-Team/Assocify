package com.github.se.assocify.model.database

import com.google.firebase.firestore.FirebaseFirestore

abstract class FirebaseApi(val userId: String, val db: FirebaseFirestore) {
  /** The name of the collection in the database */
  abstract val collectionName: String

  /**
   * Gets a new id for a document in the collection
   *
   * @return the new id
   */
  fun getNewId() = db.collection(collectionName).document().id
}
