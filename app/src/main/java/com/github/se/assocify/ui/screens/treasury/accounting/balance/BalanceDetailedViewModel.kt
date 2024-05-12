package com.github.se.assocify.ui.screens.treasury.accounting.balance

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.Status
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * View model for the balance detailed screen
 *
 * @param balanceApi the balance api
 * @param subCategoryUid the subcategory uid
 */
class BalanceDetailedViewModel(
    private var balanceApi: BalanceAPI,
    private var receiptApi: ReceiptAPI,
    private var subCategoryUid: String
) : ViewModel() {
  private val _uiState: MutableStateFlow<BalanceItemState> = MutableStateFlow(BalanceItemState())
  val uiState: StateFlow<BalanceItemState>

  init {
    updateDatabaseValues()
    uiState = _uiState
  }

  /** Update the database values */
  private fun updateDatabaseValues() {
    balanceApi.getBalance(
        CurrentUser.associationUid!!,
        { balanceList ->
          // Filter the balanceList to only include items with the matching subCategoryUid
          val filteredList =
              balanceList.filter { balanceItem ->
                balanceItem.date.year == _uiState.value.year &&
                    balanceItem.categoryUID == subCategoryUid &&
                    balanceItem.status == _uiState.value.status
              }

          // Update the UI state with the filtered list
          _uiState.value = _uiState.value.copy(balanceList = filteredList) }, {})
  }

  /**
   * Handle the year filter
   *
   * @param year the year to filter by
   */
  fun onYearFilter(year: Int) {
    _uiState.value = _uiState.value.copy(year = year)
    updateDatabaseValues()
  }

  /**
   * Handle the status filter
   *
   * @param status the status to filter by
   */
  fun onStatusFilter(status: Status) {
    _uiState.value = _uiState.value.copy(status = status)
    updateDatabaseValues()
  }

}

/**
 * The state for the balance item
 *
 * @param balanceList the current list of balance items
 * @param status the current status
 * @param year the current year
 */
data class BalanceItemState(
    val balanceList: List<BalanceItem> = emptyList(),
    val status: Status = Status.Pending,
    val year: Int = LocalDate.now().year,
    val receiptUid: String = "",
)
