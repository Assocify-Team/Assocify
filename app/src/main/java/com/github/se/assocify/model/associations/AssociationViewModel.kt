package com.github.se.assocify.model.associations

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.FirebaseApi
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

class AssociationViewModel(private var user: User, private var assocId: String): ViewModel() {
    val _associationState: MutableStateFlow<Association?> = MutableStateFlow(null)

    fun getPendingUsers(): List<User> {
        if(_associationState.value == null){
            return emptyList()
        }
        return _associationState.value!!.members.filter { x -> x.role == Role.PENDING_MEMBER }
    }

    fun getRecordedUsers(): List<User> {
        if(_associationState.value == null){
            return emptyList()
        }
        return _associationState.value!!.members.filter { x -> x.role != Role.PENDING_MEMBER }
    }

    fun getAssociationName(): String {
        if (_associationState.value == null) return ""
        return _associationState.value!!.name
    }

    fun getAssociationDescription(): String{
        if (_associationState.value == null) return ""
        return _associationState.value!!.description
    }

    fun getCreationDate(): String {
        if (_associationState.value == null) return ""
        return _associationState.value!!.creationDate
    }
    fun getEvents(): List<Event> {
        if (_associationState.value == null) return emptyList()
        return _associationState.value!!.events
    }

    fun addMember() {
        if (user.role != Role.MEMBER && user.role != Role.PENDING_MEMBER) {
            /*TODO: use the database to add the value to the db*/
        }
    }


}
