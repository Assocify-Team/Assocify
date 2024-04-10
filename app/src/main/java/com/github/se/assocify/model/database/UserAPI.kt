package com.github.se.assocify.model.database

import android.util.Log
import com.github.se.assocify.model.entities.User
import com.google.firebase.firestore.FirebaseFirestore

/**
 * API for interacting with the users in the database
 *
 * @property db the Firestore database
 */
class UserAPI(db: FirebaseFirestore) : FirebaseApi(db) {
  override val collectionName: String
    get() = "users"

  /**
   * Gets a user from the database
   *
   * @param id the id of the user to get
   * @param callback the callback to call with the user
   * @return the user with the given id
   */
  fun getUser(id: String, callback: (User) -> Unit) {
    db.collection(collectionName).document(id).get().continueWith { task ->
      if (task.isSuccessful) {
        val document = task.result
        if (document != null && document.exists()) {
          val user = document.toObject(User::class.java)
          callback(user!!)
        } else {
          throw Exception("No User found with ID: $id")
        }
      } else {
        throw task.exception ?: Exception("Unknown error occurred")
      }
    }
  }
  /**
   * Gets all users from the database
   *
   * @param callback the callback to call with the list of users
   * @return a list of all users
   */
  fun getAllUsers(callback: (List<User>) -> Unit) {

    db.collection(collectionName).get().continueWith { task ->
      if (task.isSuccessful) {
        val users =
            task.result!!.documents.map { document -> document.toObject(User::class.java)!! }
        Log.d("UserAPI", "Users: $users")
        callback(users)
      } else {
        throw task.exception ?: Exception("Unknown error occurred")
      }
    }
  }

  /**
   * Adds/edit a user to the database
   *
   * @param user the user to add/edit
   */
  fun addUser(user: User) {
    db.collection(collectionName).document(user.uid).set(user)
  }
  /**
   * Deletes a user from the database
   *
   * @param id the id of the user to delete
   */
  fun deleteUser(id: String) = delete(id)
}
