package com.github.se.assocify.ui.screens.selectAssoc

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A viewModel for the select association screen
 *
 * @param associationAPI the database used to fetch the association data
 * @param userAPI the database used to fetch the user data
 */
class SelectAssociationViewModel(
    private var associationAPI: AssociationAPI,
    private var userAPI: UserAPI,
    private var currentUser: CurrentUser
) : ViewModel() {
  private val _uiState: MutableStateFlow<SelectAssociationState> =
      MutableStateFlow(SelectAssociationState())
  val uiState: StateFlow<SelectAssociationState>

  init {
    uiState = _uiState
    updateDatabaseValues()
  }

  private fun updateDatabaseValues() {
    associationAPI.getAssociations(
        { assocList ->
          _uiState.value =
              SelectAssociationState(
                  assocList,
                  _uiState.value.searchQuery,
                  _uiState.value.user,
                  _uiState.value.searchState)
        },
        {})
    if (currentUser.userUid != "") {
      userAPI.getUser(
          currentUser.userUid,
          { user ->
            _uiState.value =
                SelectAssociationState(
                    _uiState.value.associations,
                    _uiState.value.searchQuery,
                    user,
                    _uiState.value.searchState)
          },
          {})
    }
  }

  /**
   * A function to update the search query
   *
   * @param query the new value we are trying to search on the bar
   * @param searchState if we are filtering the value on the bar or not
   */
  fun updateSearchQuery(query: String, searchState: Boolean) {
    _uiState.value =
        SelectAssociationState(_uiState.value.associations, query, _uiState.value.user, searchState)
  }
}

/**
 * A state to represents all the data that remain in the SelectAssociationViewModel
 *
 * @param associations the associations to display
 * @param searchQuery the current search query in the search bar
 * @param user the user that is connected in the current screen
 * @param searchState if the searchbar is activated or not
 */
data class SelectAssociationState(
    val associations: List<Association> = emptyList(),
    val searchQuery: String = "",
    val user: User = User(),
    val searchState: Boolean = false
)
