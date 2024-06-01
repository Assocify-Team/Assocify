package com.github.se.assocify.ui.screens.treasury

import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.PermissionRole
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingViewModel
import com.github.se.assocify.ui.screens.treasury.receiptstab.ReceiptListViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TreasuryViewModel(
    private val navActions: NavigationActions,
    private val receiptListViewModel: ReceiptListViewModel,
    private val accountingViewModel: AccountingViewModel
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

  /**
   * Switch to a different tab
   *
   * @param tab the tab we want to switch to
   */
  fun switchTab(tab: TreasuryPageIndex) {
    _uiState.value = _uiState.value.copy(currentTab = tab)
  }

  fun onSearch(currentPage: Int) {
    when (currentPage) {
      TreasuryPageIndex.Receipts.ordinal -> {
        receiptListViewModel.onSearch(_uiState.value.searchQuery)
      }
      TreasuryPageIndex.Budget.ordinal -> {
        accountingViewModel.onSearch(_uiState.value.searchQuery)
      }
      TreasuryPageIndex.Balance.ordinal -> {
        accountingViewModel.onSearch(_uiState.value.searchQuery)
      }
    }
  }

  fun getCurrentUserRole() {
    // TODO: change this to receiptListViewModel.uiState.value.currentUserRole when merge with main
    _uiState.value =
        _uiState.value.copy(
            currentUserRole =
                PermissionRole(
                    CurrentUser.userUid!!, CurrentUser.associationUid!!, RoleType.MEMBER))
  }
}

/**
 * Treasury screen UI state
 *
 * @param searchQuery the current search query
 */
data class TreasuryUIState(
    val searchQuery: String = "",
    val currentTab: TreasuryPageIndex = TreasuryPageIndex.Receipts,
    val currentUserRole: PermissionRole =
        PermissionRole(CurrentUser.userUid!!, CurrentUser.associationUid!!, RoleType.MEMBER)
)

/** Treasury tabs */
enum class TreasuryPageIndex {
  Receipts,
  Budget,
  Balance
}
