package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Association
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
}
