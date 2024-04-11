package com.github.se.assocify.ui.screens.selectAssoc

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SelectAssociationViewModel(private var associationAPI: AssociationAPI, private var userAPI: UserAPI) : ViewModel() {
  private val _uiState: MutableStateFlow<SelectAssociationState>
  val uiState: StateFlow<SelectAssociationState>

  init {
    _uiState = MutableStateFlow(SelectAssociationState())
    uiState = _uiState
    update()
  }

  private fun update() {
    associationAPI.getAssociations { assocList ->
      _uiState.value = SelectAssociationState(assocList, _uiState.value.searchQuery, _uiState.value.user, _uiState.value.searchState)
    }
    userAPI.getUser(getUserId()) {user ->
      _uiState.value = SelectAssociationState(_uiState.value.associations, _uiState.value.searchQuery, user, _uiState.value.searchState)
    }
  }

  fun updateSearchQuery(query: String, searchState: Boolean) {
    _uiState.value = SelectAssociationState(_uiState.value.associations, query)
  }

  fun getUserId(): String{
    return Firebase.auth.currentUser!!.uid
  }
}

data class SelectAssociationState(
  val associations: List<Association> = emptyList(),
  val searchQuery: String = "",
  val user: User = User(),
  val searchState: Boolean = false
)
