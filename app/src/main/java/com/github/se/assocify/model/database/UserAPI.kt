package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore

class UserAPI(db: FirebaseFirestore) : FirebaseApi(db) {
  override val collectionName: String
    get() = "users"

  /**
   * Gets a user from the database
   *
   * @param id the id of the user to get
   * @return the user with the given id
   */
  fun getUser(id: String): User {
    return Tasks.await(
        db.collection(collectionName).document(id).get().continueWith { task ->
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
        })
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
  fun deleteUser(id: String) {
    Tasks.await(
        db.collection(collectionName)
            .document(id)
            .delete()
            .addOnSuccessListener { println("DocumentSnapshot successfully deleted! User deleted") }
            .addOnFailureListener { e -> println("Error deleting document User: $e") })
  }

  /** Deletes all users from the database */
  fun deleteAllUsers() {
    Tasks.await(
        db.collection(collectionName).get().addOnSuccessListener { querySnapshot ->
          for (document in querySnapshot) {
            document.reference.delete()
          }
        })
  }

  /**
   * Adds multiple users to the database
   *
   * @param users the list of users to add
   */
  fun addAllUsers(users: List<User>) {
    for (user in users) {
      addUser(user)
    }
  }
}
