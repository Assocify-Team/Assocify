package com.github.se.assocify.model.database

import com.google.firebase.firestore.FirebaseFirestore

class AssociationAPI(db: FirebaseFirestore) : FirebaseApi(db) {
    override val collectionName: String = "associations"

    fun getAssociation(id:String) = get(id)
    fun getAssociations() = getAll()

    fun addAssociation(data: Map<String, Any>) = add(data)
    fun updateAssociation(id: String, data: Map<String, Any>) = update(id, data)
    fun deleteAssociation(id: String) = delete(id)


}