package com.github.se.assocify.ui.screens.treasury

import android.net.Uri
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingViewModel
import com.github.se.assocify.ui.screens.treasury.receiptstab.ReceiptListViewModel
import com.github.se.assocify.ui.util.SnackbarSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TreasuryViewModel(
    navActions: NavigationActions,
    receiptsAPI: ReceiptAPI,
    accountingCategoryAPI: AccountingCategoryAPI,
    accountingSubCategoryAPI: AccountingSubCategoryAPI,
    balanceAPI: BalanceAPI,
    budgetAPI: BudgetAPI,
    userAPI: UserAPI,
    associationAPI: AssociationAPI
) {
    init {
        Log.d("image", "getting logo in View model at init")
        associationAPI.getLogo(CurrentUser.associationUid!!,
            {uri ->
                Log.d("image", "uri is $uri")
                CurrentUser.setAssociationLogo(uri) },
            {CurrentUser.setAssociationLogo(null)}
        )
    }
  // ViewModel states
  private val _uiState: MutableStateFlow<TreasuryUIState> = MutableStateFlow(TreasuryUIState())
  val uiState: StateFlow<TreasuryUIState> = _uiState

  private val snackbarSystem = SnackbarSystem(_uiState.value.snackbarHostState)

  val receiptListViewModel: ReceiptListViewModel =
      ReceiptListViewModel(navActions, receiptsAPI, snackbarSystem, userAPI)

  val accountingViewModel: AccountingViewModel =
      AccountingViewModel(
          accountingCategoryAPI, accountingSubCategoryAPI, balanceAPI, budgetAPI, snackbarSystem)

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
}

/**
 * Treasury screen UI state
 *
 * @param searchQuery the current search query
 */
data class TreasuryUIState(
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val searchQuery: String = "",
    val currentTab: TreasuryPageIndex = TreasuryPageIndex.Receipts,
)

/** Treasury tabs */
enum class TreasuryPageIndex {
  Receipts,
  Budget,
  Balance
}
