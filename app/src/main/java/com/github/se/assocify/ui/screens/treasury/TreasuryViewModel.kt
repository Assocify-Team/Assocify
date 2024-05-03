package com.github.se.assocify.ui.screens.treasury

import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.receiptstab.ReceiptListViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TreasuryViewModel(
    private val navActions: NavigationActions,
    private val receiptListViewModel: ReceiptListViewModel
) {
  // ViewModel states
  private val _uiState: MutableStateFlow<TreasuryUIState> = MutableStateFlow(TreasuryUIState())
  val uiState: StateFlow<TreasuryUIState>

  init {
    _uiState.value = TreasuryUIState()
    uiState = _uiState
  }

  fun setSearchQuery(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
  }

  fun onSearch(currentPage: Int) {
    when (currentPage) {
      TreasuryPageIndex.Receipts.ordinal -> {
        receiptListViewModel.onSearch(_uiState.value.searchQuery)
      }
      TreasuryPageIndex.Budget.ordinal -> {}
      TreasuryPageIndex.Balance.ordinal -> {}
    }
  }
}

data class TreasuryUIState(
    val searchQuery: String = "",
)
