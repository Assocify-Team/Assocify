package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BudgetItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The view model for the budget detailed screen
 *
 * @param budgetApi the budget api
 * @param accountingSubCategoryAPI the accounting subcategory api
 * @param subCategoryUid the subcategory uid
 */
class BudgetDetailedViewModel(
    private var budgetApi: BudgetAPI,
    private var accountingSubCategoryAPI: AccountingSubCategoryAPI,
    private var subCategoryUid: String
) : ViewModel() {
  private val _uiState: MutableStateFlow<BudgetItemState> = MutableStateFlow(BudgetItemState())
  val uiState: StateFlow<BudgetItemState>

  init {
    updateDatabaseValues()
      setSubCategory(subCategoryUid)
    uiState = _uiState
  }
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
  private fun updateDatabaseValues() {
    budgetApi.getBudget(
        CurrentUser.associationUid!!,
        { budgetList ->
          // Filter the budgetList to only include items with the matching subCategoryUid
          val filteredList =
              budgetList.filter { budgetItem -> budgetItem.year == _uiState.value.yearFilter
                      && budgetItem.uid == subCategoryUid}

          // Update the UI state with the filtered list
          _uiState.value = _uiState.value.copy(budgetList = filteredList)
        },
        {})
  }

  fun onYearFilter(yearFilter: Int) {
    _uiState.value = _uiState.value.copy(yearFilter = yearFilter)
    updateDatabaseValues()
  }
}

data class BudgetItemState(
    val budgetList: List<BudgetItem> = emptyList(),
    val subCategory: AccountingSubCategory = AccountingSubCategory("", "", "", 0),
    val yearFilter: Int = 2023
)
