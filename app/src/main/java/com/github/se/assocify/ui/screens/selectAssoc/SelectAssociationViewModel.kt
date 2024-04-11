package com.github.se.assocify.ui.screens.selectAssoc

import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.FirebaseApi
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class SelectAssociationViewModel(db: FirebaseFirestore, userId: String) : ViewModel() {
  private val _uiState: MutableStateFlow<SelectAssociationState>
  private val db = AssociationAPI(Firebase.firestore)
  val uiState: StateFlow<SelectAssociationState>

  init {
    _uiState = MutableStateFlow(SelectAssociationState())
    uiState = _uiState
    update()
  }

  private fun update(){
    db.getAssociations { assocList -> _uiState.value = SelectAssociationState(assocList, _uiState.value.searchQuery) }
  }

  fun updateSearchQuery(query: String){
    _uiState.value = SelectAssociationState(_uiState.value.associations, query)
  }

}

data class SelectAssociationState(
    val associations: List<Association> = emptyList(),
    val searchQuery: String = ""
)
