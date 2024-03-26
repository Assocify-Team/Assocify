package com.github.se.assocify.model.database

import com.google.firebase.firestore.FirebaseFirestore

class UserAPI(db: FirebaseFirestore) : FirebaseApi(db) {
  override val collectionName: String
    get() = "users"
}
