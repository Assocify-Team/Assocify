package model.database

import com.google.firebase.firestore.FirebaseFirestore

class UserAPI(db: FirebaseFirestore) : FirebaseApi(db) {
    override val collectionName: String
        get() = "users"
    fun getUser(id:String) = get(id)
    fun getAllUsers() = getAll()
    /*
        REST TO IMPLEMENT
     */

}