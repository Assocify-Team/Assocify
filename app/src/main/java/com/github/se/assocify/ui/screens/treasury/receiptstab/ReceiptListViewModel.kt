package com.github.se.assocify.ui.screens.treasury.receiptstab

import android.util.Log
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.SupabaseClient
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
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
    private val receiptsDatabase: ReceiptAPI = ReceiptAPI(SupabaseClient.supabaseClient)
) {
  // ViewModel states
  private val _uiState: MutableStateFlow<ReceiptUIState> = MutableStateFlow(ReceiptUIState())
  val uiState: StateFlow<ReceiptUIState>

  // User entity and it's API
  private val userAPI = UserAPI(SupabaseClient.supabaseClient)
  private var user: User? = null

  init {
    // Define user entity
    userAPI.getUser(
        CurrentUser.userUid!!,
        onSuccess = { user = it },
        onFailure = { // TODO Error message
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
              _uiState.value.copy(
                  userReceipts =
                      receipts.filter {
                        it.title.contains(_uiState.value.searchQuery, ignoreCase = true)
                      })
        },
        onError = {
          Log.e("ReceiptListViewModel", "Error fetching user receipts", it)
          // TODO Error message
        })
  }

  /** Update all receipts */
  fun updateAllReceipts() {
    // TODO : Add a permission check.
    receiptsDatabase.getAllReceipts(
        onSuccess = { receipts ->
          _uiState.value =
              _uiState.value.copy(
                  allReceipts =
                      receipts.filter {
                        it.title.contains(_uiState.value.searchQuery, ignoreCase = true)
                      })
        },
        onError = {
          Log.e("ReceiptListViewModel", "Error fetching all receipts", it)
          // TODO on sprint 4 with error API
        })
  }

  /**
   * Filter the list of receipts when the user uses the search bar.
   *
   * @param searchQuery: Search query in the search bar
   */
  fun onSearch(searchQuery: String) {
    _uiState.value = _uiState.value.copy(searchQuery = searchQuery)
    updateUserReceipts()
    updateAllReceipts()
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
    val searchQuery: String = ""
)
