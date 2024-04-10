package com.github.se.assocify.ui.screens.createAsso

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CreateAssoViewmodel() : ViewModel() {
  private val _uiState = MutableStateFlow(CreateAssoUIState())
  val uiState: StateFlow<CreateAssoUIState> = _uiState

  private val assoApi = AssociationAPI(db = Firebase.firestore)
  // val currUser = Firebase.auth.currentUser

  fun setName(name: String) {
    // need input sanitization TODO
    _uiState.value = _uiState.value.copy(name = name)
  }

    fun addMember(member: User) {
        // need input sanitization TODO
        _uiState.value = _uiState.value.copy(members = _uiState.value.members + member)
    }

    fun removeMember(member: User) {
        // need input sanitization TODO
        _uiState.value = _uiState.value.copy(members = _uiState.value.members - member)
    }

    // unsure if this is needed yet
    fun modifyMember(member: User) {
        // need input sanitization TODO
    }

  fun saveAsso() {
    // TODO check that all is valid : at least one member (current user), name not empty

    // create asso today
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val asso =
        Association(
            assoApi.getNewId(), _uiState.value.name, "", date, "", _uiState.value.members, listOf())
    assoApi.addAssociation(asso)
  }
}

data class CreateAssoUIState(
    val name: String = "",
    val members: List<User> = listOf(),
    // there should be a logo val but not implemented yet
)
