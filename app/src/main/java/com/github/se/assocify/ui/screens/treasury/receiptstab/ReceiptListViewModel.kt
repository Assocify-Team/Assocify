package com.github.se.assocify.ui.screens.treasury.receiptstab

import android.util.Log
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.util.SnackbarSystem
import com.github.se.assocify.ui.util.SyncSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Viewmodel for the "Receipts" page in Treasury menu
 *
 * @param receiptsDatabase: Database API for receipts. A default one is passed
 */
class ReceiptListViewModel(
    private val navActions: NavigationActions,
    private val receiptsDatabase: ReceiptAPI,
    private val snackbarSystem: SnackbarSystem
) {
  // ViewModel states
  private val _uiState: MutableStateFlow<ReceiptUIState> = MutableStateFlow(ReceiptUIState())
  val uiState: StateFlow<ReceiptUIState>

  private val loadSystem =
      SyncSystem(
          { _uiState.value = _uiState.value.copy(loading = false, refresh = false, error = null) },
          { _uiState.value = _uiState.value.copy(loading = false, refresh = false, error = it) })

  private val refreshSystem =
      SyncSystem(
          { updateReceipts() },
          { error ->
            _uiState.value = _uiState.value.copy(refresh = false)
            Log.e("ReceiptListScreen", error)
          })

  init {
    uiState = _uiState
    updateReceipts()
  }

  fun updateReceipts() {
    if (!loadSystem.start(2)) return

    _uiState.value = _uiState.value.copy(loading = true)

    updateUserReceipts()
    updateAllReceipts()
  }

  fun refreshReceipts() {
    if (!refreshSystem.start(2)) return

    _uiState.value = _uiState.value.copy(refresh = true)

    // two callbacks !
    receiptsDatabase.updateCaches(
        onSuccess = { _, _ -> refreshSystem.end() },
        onFailure = { _, _ -> refreshSystem.end("Error refreshing receipts") })
  }

  /** Update the user's receipts */
  private fun updateUserReceipts() {
    receiptsDatabase.getUserReceipts(
        onSuccess = { receipts ->
          _uiState.value =
              _uiState.value.copy(
                  userReceipts =
                      receipts.filter {
                        it.title.contains(_uiState.value.searchQuery, ignoreCase = true)
                      })
          loadSystem.end()
        },
        onFailure = {
          Log.e("ReceiptListViewModel", "Error fetching user receipts", it)
          loadSystem.end("Error loading user receipts")
        })
  }

  /** Update all receipts */
  private fun updateAllReceipts() {
    // TODO : Add a permission check.
    receiptsDatabase.getAllReceipts(
        onSuccess = { receipts ->
          _uiState.value =
              _uiState.value.copy(
                  allReceipts =
                      receipts.filter {
                        it.title.contains(_uiState.value.searchQuery, ignoreCase = true)
                      })
          loadSystem.end()
        },
        onFailure = {
          Log.e("ReceiptListViewModel", "Error fetching all receipts", it)
          loadSystem.end("Error loading receipts")
        })
  }

  /**
   * Filter the list of receipts when the user uses the search bar.
   *
   * @param searchQuery: Search query in the search bar
   */
  fun onSearch(searchQuery: String) {
    _uiState.value = _uiState.value.copy(searchQuery = searchQuery)
    updateReceipts()
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
    val loading: Boolean = false,
    val error: String? = null,
    val refresh: Boolean = false,
    val userReceipts: List<Receipt> = listOf(),
    val allReceipts: List<Receipt> = listOf(),
    val searchQuery: String = ""
)
