package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Association
import com.google.android.gms.tasks.Task
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
   * @param callback the callback to call with the association, it isn't called if the association
   *   isn't found
   * @return the association with the given id
   */
  fun getAssociation(id: String, callback: (Association) -> Unit): Task<Unit> {

    return db.collection(collectionName).document(id).get().continueWith { task ->
      if (task.isSuccessful) {
        val document = task.result
        if (document != null && document.exists()) {
          val doc = document.toObject(Association::class.java)
          callback(doc!!)
        }
      } else {
        throw task.exception ?: Exception("Unknown error occurred")
      }
    }
  }

  /**
   * Gets all associations from the database
   *
   * @param callback the callback to call with the list of associations
   * @return a list of all associations
   */
  fun getAssociations(callback: (List<Association>) -> Unit): Task<Unit> {
    return db.collection(collectionName).get().continueWith { task ->
      if (task.isSuccessful) {
        val associations =
            task.result!!.documents.map { document -> document.toObject(Association::class.java)!! }
        callback(associations)
      } else {
        throw task.exception ?: Exception("Unknown error occurred")
      }
    }
  }

  /**
   * Adds/edit an association to the database
   *
   * @param association the association to add/edit
   */
  fun addAssociation(association: Association): Task<Void> {
    return db.collection(collectionName).document(association.uid).set(association)
  }

  /**
   * Deletes an association from the database
   *
   * @param id the id of the association to delete
   */
  fun deleteAssociation(id: String): Task<Void> {
    return db.collection(collectionName).document(id).delete()
  }
}
