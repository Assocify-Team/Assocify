package com.github.se.assocify.ui.screens.selectAssoc

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.FirebaseApi
import com.github.se.assocify.model.entities.Association
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SelectAssociationViewModel() : ViewModel() {
  private val _uiState: MutableStateFlow<SelectAssociationState>
  val uiState: StateFlow<SelectAssociationState>

  init {
    _uiState = MutableStateFlow(SelectAssociationState())
    uiState = _uiState
  }

  fun update(){
  }
}

data class SelectAssociationState(
    val associations: List<Association> = emptyList(),
    val searchQuery: String = ""
)
