package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
   * @param onSuccess called on success with the user
   * @return the user with the given id
   */
  fun getUser(id: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionName)
        .document(id)
        .get()
        .addOnSuccessListener {
          val user = it.toObject(User::class.java)
          onSuccess(user!!)
        }
        .addOnFailureListener(onFailure)
  }
  /**
   * Gets all users from the database
   *
   * @param onSuccess called on success with the list of users
   * @param onFailure called on failure
   * @return a list of all users
   */
  fun getAllUsers(onSuccess: (List<User>) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionName)
        .get()
        .addOnSuccessListener {
          val users = it.documents.map { document -> document.toObject(User::class.java)!! }
          onSuccess(users)
        }
        .addOnFailureListener(onFailure)
  }

  /**
   * Adds/edit a user to the database
   *
   * @param user the user to add/edit
   * @param onSuccess called on success on success (by default does nothing)
   * @param onFailure called on failure
   */
  fun addUser(user: User, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit) {
    db.collection(collectionName)
        .document(user.uid)
        .set(user)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener(onFailure)
  }
  /**
   * Deletes a user from the database
   *
   * @param id the id of the user to delete
   * @param onSuccess called on success (by default does nothing)
   * @param onFailure called on failure
   */
  fun deleteUser(id: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit) {
    db.collection(collectionName)
        .document(id)
        .delete()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener(onFailure)
  }
}

