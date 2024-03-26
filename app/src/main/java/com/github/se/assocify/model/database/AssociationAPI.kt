package com.github.se.assocify.model.database

import android.util.Log
import com.github.se.assocify.model.entities.Association
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore

class AssociationAPI(db: FirebaseFirestore) : FirebaseApi(db) {
  override val collectionName: String = "associations"

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

  fun getAssociations(): Task<List<Association>> {
    return db.collection(collectionName).get().continueWith { task ->
      if (!task.isSuccessful) {
        throw task.exception!!
      }
      task.result!!.documents.map { it.toObject(Association::class.java)!! }
    }
  }

  fun addAssociation(association: Association) {
    Tasks.await(
        db.collection(collectionName)
            .document(association.uid)
            .set(association)
            .addOnSuccessListener {
              Log.d(
                  "FirebaseConnection", "DocumentSnapshot successfully written! Association added")
            }
            .addOnFailureListener { e ->
              Log.w("FirebaseConnection", "Error writing document Association", e)
            })
  }

  fun deleteAllAssociations() {
    Tasks.await(
        db.collection(collectionName).get().addOnSuccessListener { querySnapshot ->
          for (document in querySnapshot) {
            document.reference.delete()
          }
        })
  }

  fun deleteAssociation(id: String) = delete(id)
}
