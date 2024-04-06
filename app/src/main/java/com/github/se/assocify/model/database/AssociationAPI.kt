package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Association
import com.google.android.gms.tasks.Tasks
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
   * @return the association with the given id
   */
  fun getAssociation(id: String): Association? {
    return Tasks.await(
        db.collection(collectionName).document(id).get().continueWith { task ->
          if (task.isSuccessful) {
            val document = task.result
            if (document != null && document.exists()) {
              document.toObject(Association::class.java)
            } else {
              throw Exception("No Asso found with ID: $id")
            }
          } else {
            throw task.exception ?: Exception("Unknown error occurred")
          }
        })
  }

  /**
   * Gets all associations from the database
   *
   * @return a list of all associations
   */
  fun getAssociations(): List<Association> {
    return Tasks.await(
        db.collection(collectionName).get().continueWith { task ->
          if (task.isSuccessful) {
            task.result!!.map { document ->
              document.toObject(Association::class.java)
            }
          } else {
            throw task.exception ?: Exception("Unknown error occurred")
          }
        })
  }

  /**
   * Adds/edit an association to the database
   *
   * @param association the association to add/edit
   */
  fun addAssociation(association: Association) {
    Tasks.await(db.collection(collectionName).document(association.uid).set(association))
  }

  /**
   * Deletes an association from the database
   *
   * @param id the id of the association to delete
   */
  fun deleteAssociation(id: String) = Tasks.await(delete(id))
}
