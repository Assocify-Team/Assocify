package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
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
   * @return the user with the given id
   */
    fun getUser(id: String): Task<User?> {
    return db.collection(collectionName).document(id).get().continueWith { task ->
          if (task.isSuccessful) {
            val document = task.result
            if (document != null && document.exists()) {
              document.toObject(User::class.java)
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
   * @return a list of all users
   */
  fun getAllUsers(): List<User> {
    return Tasks.await(
        db.collection(collectionName).get().continueWith { task ->
          if (task.isSuccessful) {
            val users = mutableListOf<User>()
            for (document in task.result!!.documents) {
              val user = document.toObject(User::class.java)
              users.add(user!!)
            }
            users
          } else {
            throw task.exception ?: Exception("Unknown error occurred")
          }
        })
  }

  /**
   * Adds/edit a user to the database
   *
   * @param user the user to add/edit
   */
  fun addUser(user: User) {
    Tasks.await(db.collection(collectionName).document(user.uid).set(user))
  }
  /**
   * Deletes a user from the database
   *
   * @param id the id of the user to delete
   */
  fun deleteUser(id: String) = Tasks.await(delete(id))

}
