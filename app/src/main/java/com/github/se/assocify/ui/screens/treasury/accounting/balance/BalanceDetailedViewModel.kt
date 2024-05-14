package com.github.se.assocify.ui.screens.treasury.accounting.balance

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.Status
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
    private var accountingSubCategoryAPI: AccountingSubCategoryAPI,
    private var subCategoryUid: String
) : ViewModel() {
  private val _uiState: MutableStateFlow<BalanceItemState> = MutableStateFlow(BalanceItemState())
  val uiState: StateFlow<BalanceItemState>

  init {
    updateDatabaseValues()
    setSubCategory(subCategoryUid)
    uiState = _uiState
  }

  /**
   * Set the subcategory
   *
   * @param subCategoryUid the subcategory uid
   */
  private fun setSubCategory(subCategoryUid: String) {
    accountingSubCategoryAPI.getSubCategories(
        CurrentUser.associationUid!!,
        { subCategoryList ->
          val subCategory = subCategoryList.find { it.uid == subCategoryUid }
          if (subCategory != null) {
            _uiState.value = _uiState.value.copy(subCategory = subCategory)
          }
        },
        {})
  }

  /** Update the database values */
  private fun updateDatabaseValues() {
    balanceApi.getBalance(
        CurrentUser.associationUid!!,
        { balanceList ->
          // Filter the balanceList to only include items with the matching subCategoryUid, year and
          // status
          val filteredList =
              balanceList.filter { balanceItem ->
                balanceItem.date.year == _uiState.value.year &&
                    balanceItem.subcategoryUID == subCategoryUid
              }

          // if status is not null, filter the list by status
          val statusFilteredList =
              if (_uiState.value.status != null) {
                filteredList.filter { balanceItem -> balanceItem.status == _uiState.value.status }
              } else {
                filteredList
              }

          // Update the UI state with the filtered list
          _uiState.value = _uiState.value.copy(balanceList = statusFilteredList)
        },
        {})
  }

  /**
   * Handle the year filter
   *
   * @param year the year to filter by
   */
  fun onYearFilter(year: Int) {
    _uiState.value = _uiState.value.copy(year = year)
    Log.d("BalanceDetailedViewModel", "Year filter: $year")
    updateDatabaseValues()
  }

  /**
   * Handle the status filter
   *
   * @param status the status to filter by
   */
  fun onStatusFilter(status: Status?) {
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
    val subCategory: AccountingSubCategory = AccountingSubCategory("", "", "", 0, 2023),
    val status: Status? = null,
    val year: Int = 2023
)
