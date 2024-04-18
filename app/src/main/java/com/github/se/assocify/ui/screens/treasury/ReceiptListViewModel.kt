package com.github.se.assocify.ui.screens.treasury

import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Viewmodel for the "Receipts" page in Treasury menu
 *
 * @param currentUser: Current user of the app
 * @param receiptsDatabase: Database API for receipts. A default one is passed
 */
class ReceiptListViewModel(
    private val navActions: NavigationActions,
    private val receiptsDatabase: ReceiptAPI =
        ReceiptAPI(
            userId = CurrentUser.userUid!!,
            basePath = "associations/" + CurrentUser.associationUid!!,
            storage = Firebase.storage,
            db = Firebase.firestore)
) {
  // ViewModel states
  private val _uiState: MutableStateFlow<ReceiptUIState> = MutableStateFlow(ReceiptUIState())
  val uiState: StateFlow<ReceiptUIState>

  // User entity and it's API
  private val userAPI = UserAPI(receiptsDatabase.db)
  private var user: User? = null

  init {
    // Define user entity
    userAPI.getUser(
        CurrentUser.userUid!!,
        onSuccess = { user = it },
        onFailure = { // TODO on sprint 4 with error API
        })
    updateUserReceipts()
    updateAllReceipts()
    uiState = _uiState
  }

  /** Update the user's receipts */
  fun updateUserReceipts() {
    receiptsDatabase.getUserReceipts(
        onSuccess = { receipts ->
          _uiState.value =
              ReceiptUIState(
                  userReceipts = receipts,
                  allReceipts = _uiState.value.allReceipts,
                  searchQuery = _uiState.value.searchQuery)
        },
        onError = {
          // TODO on sprint 4 with error API
        })
  }

  /** Update all receipts, if you have permissions */
  fun updateAllReceipts() {
    // TODO : Add a permission check.
    // TODO Note : not done because permissions & database will change
    // TODO Note : on sprint 4.
    receiptsDatabase.getAllReceipts(
        onReceiptsFetched = { receipts ->
          _uiState.value =
              ReceiptUIState(
                  userReceipts = _uiState.value.userReceipts,
                  allReceipts = receipts,
                  searchQuery = _uiState.value.searchQuery)
        },
        onError = { error, exception ->
          // TODO on sprint 4 with error API
        })
  }

  /** Filter the list of receipts when the user types in the search bar. */
  fun onSearch(): List<Receipt> {
    val filter = _uiState.value.searchQuery.trim()
    return _uiState.value.allReceipts.filter { receipt ->
      receipt.title.contains(filter, ignoreCase = true) ||
          receipt.description.contains(filter, ignoreCase = true)
    }
  }

  /** Set the search query when changed in the search bar */
  fun setSearchQuery(query: String) {
    _uiState.value =
        ReceiptUIState(
            userReceipts = _uiState.value.userReceipts,
            allReceipts = _uiState.value.allReceipts,
            searchQuery = query)
  }

  fun onReceiptClick(receipt: Receipt) {
    navActions.navigateTo(Destination.EditReceipt(receipt.uid))
  }
}

/**
 * UI state for the Receipts page
 *
 * @param userReceipts: List of receipts of the user
 * @param allReceipts: List of all receipts from admin's view
 * @param searchQuery: Search query in the search bar
 */
data class ReceiptUIState(
    val userReceipts: List<Receipt> = listOf(),
    val allReceipts: List<Receipt> = listOf(),
    val searchQuery: String = "",
)
