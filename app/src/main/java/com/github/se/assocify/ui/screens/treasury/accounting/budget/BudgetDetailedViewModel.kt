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
    budgetApi.getBudget(
        CurrentUser.associationUid!!,
        { budgetList ->
          // Filter the budgetList to only include items with the matching subCategoryUid
          val filteredList =
              budgetList.filter { budgetItem ->
                budgetItem.year == _uiState.value.yearFilter &&
                    budgetItem.subcategoryUID == subCategoryUid
              }

          // Update the UI state with the filtered list
          _uiState.value = _uiState.value.copy(budgetList = filteredList)
        },
        {})
  }

  /**
   * Handle the year filter
   *
   * @param yearFilter the year to filter by
   */
  fun onYearFilter(yearFilter: Int) {
    _uiState.value = _uiState.value.copy(yearFilter = yearFilter)
    updateDatabaseValues()
  }

  /**
   * Enter in the edit state so the popup appears
   *
   * @param budgetItem the item we want to edit
   */
  fun startEditing(budgetItem: BudgetItem) {
    _uiState.value = _uiState.value.copy(editing = true, editedBudgetItem = budgetItem)
  }

  /**
   * Exit the edit state while saving the modifications performed
   *
   * @param budgetItem the new edited budgetItem
   */
  fun saveEditing(budgetItem: BudgetItem) {
    budgetApi.updateBudgetItem(CurrentUser.associationUid!!, budgetItem, {}, {})
    _uiState.value =
        _uiState.value.copy(
            editing = false,
            budgetList = _uiState.value.budgetList.filter { it.uid != budgetItem.uid } + budgetItem,
            editedBudgetItem = null)
  }

  /** Exit the edit state without keeping the modifications done */
  fun cancelEditing() {
    _uiState.value = _uiState.value.copy(editing = false, editedBudgetItem = null)
  }

  fun modifyTVAFilter(tvaActive: Boolean) {
    _uiState.value = _uiState.value.copy(filterActive = tvaActive)
  }
}

/**
 * The state for the budget item
 *
 * @param budgetList the current list of budget items
 * @param subCategory the current subcategory
 * @param yearFilter the current year filter
 * @param editing the current editing state
 * @param editedBudgetItem the current edited budget item
 * @param filterActive if the tva filter is active or not
 */
data class BudgetItemState(
    val budgetList: List<BudgetItem> = emptyList(),
    val subCategory: AccountingSubCategory = AccountingSubCategory("", "", "", 0, 2023),
    val yearFilter: Int = 2023,
    val editing: Boolean = false,
    val editedBudgetItem: BudgetItem? = null,
    val filterActive: Boolean = false
)
