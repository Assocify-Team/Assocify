package com.github.se.assocify.model.database

import android.util.Log
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
  fun getAssociation(id: String): Association {
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
            val associations = mutableListOf<Association>()
            for (document in task.result!!.documents) {
              val association = document.toObject(Association::class.java)
              associations.add(association!!)
            }
            associations
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
    Tasks.await(
        db.collection(collectionName)
            .document(association.uid)
            .set(association)
            .addOnSuccessListener {
              Log.d("FirebaseConnection", "DocumentSnapshot successfully written! Asso added")
            }
            .addOnFailureListener { e ->
              Log.w("FirebaseConnection", "Error writing document Asso", e)
            })
  }

    /**
     * Adds/edit a list of associations to the database
     *
     * @param associations the list of associations to add/edit
     */
  fun addAllAssociations(associations: List<Association>) {
    for (association in associations) {
      addAssociation(association)
    }
  }

    /**
     * Deletes an association from the database
     */
  fun deleteAllAssociations() {
    Tasks.await(
        db.collection(collectionName).get().addOnSuccessListener { querySnapshot ->
          for (document in querySnapshot) {
            document.reference.delete()
          }
        })
  }
    /**
     * Deletes an association from the database
     *
     * @param id the id of the association to delete
     */
  fun deleteAssociation(id: String) = delete(id)
}
