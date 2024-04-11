package com.github.se.assocify.ui.screens.selectAssoc

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.entities.Association
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SelectAssociationViewModel(private var db: AssociationAPI) : ViewModel() {
  private val _uiState: MutableStateFlow<SelectAssociationState>
  val uiState: StateFlow<SelectAssociationState>

  init {
    _uiState = MutableStateFlow(SelectAssociationState())
    uiState = _uiState
    update()
  }

  private fun update() {
    db.getAssociations { assocList ->
      _uiState.value = SelectAssociationState(assocList, _uiState.value.searchQuery)
    }
  }

  fun updateSearchQuery(query: String, searchState: Boolean) {
    _uiState.value = SelectAssociationState(_uiState.value.associations, query)
  }
}

data class SelectAssociationState(
    val associations: List<Association> = emptyList(),
    val searchQuery: String = "",
    val searchState: Boolean = false
)
