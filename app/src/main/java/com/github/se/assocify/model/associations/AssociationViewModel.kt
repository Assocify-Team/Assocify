package com.github.se.assocify.model.associations

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.User
import kotlinx.coroutines.flow.MutableStateFlow

class AssociationViewModel(private var user: User, rivate var assocId: String): ViewModel() {
    val _associationState: MutableStateFlow<Association?> = MutableStateFlow(null)



    fun getPendingUsers(): List<User> {
        //TODO: modify the value of the notification
        return emptyList<User>()
    }

    fun getRecordedUsers(): List<User> {
        //TODO: modify the value of the list
        return emptyList<User>()
    }

    fun getAssociationName(): String {
        if (_associationState.value == null) return ""
        return _associationState.value!!.name
    }




}