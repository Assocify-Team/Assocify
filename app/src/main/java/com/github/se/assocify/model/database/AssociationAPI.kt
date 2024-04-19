package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.google.firebase.firestore.FirebaseFirestore

/**
 * API for interacting with the associations in the database
 *
 * @property db the Firestore database
 */
class AssociationAPI(db: FirebaseFirestore) : FirebaseApi(db) {
  override val collectionName: String = "associations"

  /**
   * Gets an association from the database
   *
   * @param id the id of the association to get
   * @param onSuccess called on success with the association
   * @param onFailure called on failure
   * @return the association with the given id
   */
  fun getAssociation(id: String, onSuccess: (Association) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionName)
        .document(id)
        .get()
        .addOnSuccessListener {
          val doc = it.toObject(Association::class.java)
          onSuccess(doc!!)
        }
        .addOnFailureListener { onFailure(it) }
  }

  /**
   * Gets all associations from the database
   *
   * @param onSuccess called on success with the list of associations
   * @param onFailure called on failure
   * @return a list of all associations
   */
  fun getAssociations(onSuccess: (List<Association>) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionName)
        .get()
        .addOnSuccessListener {
          val associations =
              it.documents.map { document -> document.toObject(Association::class.java)!! }
          onSuccess(associations)
        }
        .addOnFailureListener { onFailure(it) }
  }

  /**
   * Adds/edit an association to the database
   *
   * @param association the association to add/edit
   * @param onSuccess called on success on success (by default does nothing)
   * @param onFailure called on failure
   */
  fun addAssociation(
      association: Association,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionName)
        .document(association.uid)
        .set(association)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener(onFailure)
  }

  /**
   * Deletes an association from the database
   *
   * @param id the id of the association to delete
   * @param onSuccess called on success (by default does nothing)
   * @param onFailure called on failure
   */
  fun deleteAssociation(id: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit) {
    db.collection(collectionName)
        .document(id)
        .delete()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener(onFailure)
  }

  /**
   * Gets all associations from the database that match the filter
   *
   * @param onSuccess called on success with the list of associations
   * @param onFailure called on failure
   * @param filter the filter to apply to the associations
   * @return a list of all associations that match the filter
   */
  fun getFilteredAssociations(
      onSuccess: (List<Association>) -> Unit,
      onFailure: (Exception) -> Unit,
      filter: (Association) -> Boolean
  ) {
    db.collection(collectionName)
        .get()
        .addOnSuccessListener {
          val associations =
              it.documents
                  .map { document -> document.toObject(Association::class.java)!! }
                  .filter { associations -> filter(associations) }
          onSuccess(associations)
        }
        .addOnFailureListener { onFailure(it) }
  }

  /**
   * Gets all users from the association that follow the filter
   *
   * @param assocId the id of the association
   * @param onSuccess called on success with the list of users
   * @param onFailure called on failure
   * @param filter the filter to apply to the users
   */
  private fun getFilteredUsers(
      assocId: String,
      onSuccess: (List<User>) -> Unit,
      onFailure: (Exception) -> Unit,
      filter: (User) -> Boolean
  ) {
    db.collection(collectionName)
        .get()
        .addOnSuccessListener {
          val users =
              it.documents
                  .map { document -> document.toObject(Association::class.java)!! }
                  .map { association -> association.getMembers() }
                  .flatten()
                  .filter { user -> filter(user) }
          onSuccess(users)
        }
        .addOnFailureListener { onFailure(it) }
  }

  /**
   * Gets all pending users from the given association
   *
   * @param assocId the id of the association
   * @param onSuccess called on success with the list of users
   * @param onFailure called on failure
   * @return a list of all pending users
   */
  fun getPendingUsers(
      assocId: String,
      onSuccess: (List<User>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    getFilteredUsers(assocId, onSuccess, onFailure) { user -> user.getRole() == Role("pending") }
  }

  /**
   * Gets all accepted users from the given association
   *
   * @param assocId the id of the association
   * @param onSuccess called on success with the list of users
   * @param onFailure called on failure
   * @return a list of all accepted users
   */
  fun getAcceptedUsers(
      assocId: String,
      onSuccess: (List<User>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    getFilteredUsers(assocId, onSuccess, onFailure) { user -> user.getRole() != Role("pending") }
  }

  /**
   * Gets all users from the given association
   *
   * @param assocId the id of the association
   * @param onSuccess called on success with the list of users
   * @param onFailure called on failure
   * @return a list of all users
   */
  fun getAllUsers(
      assocId: String,
      onSuccess: (List<User>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    getFilteredUsers(assocId, onSuccess, onFailure) { true }
  }
}
