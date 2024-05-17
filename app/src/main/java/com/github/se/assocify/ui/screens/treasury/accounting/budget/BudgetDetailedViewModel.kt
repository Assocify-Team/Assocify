package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BudgetItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The view model for the budget detailed screen
 *
 * @param budgetApi the budget api
 * @param accountingSubCategoryAPI the accounting subcategory api
 * @param accountingCategoryAPI the accounting category api
 * @param subCategoryUid the subcategory uid
 */
class BudgetDetailedViewModel(
    private var budgetApi: BudgetAPI,
    private var accountingSubCategoryAPI: AccountingSubCategoryAPI,
    private var accountingCategoryAPI: AccountingCategoryAPI,
    private var subCategoryUid: String
) : ViewModel() {
  private val _uiState: MutableStateFlow<BudgetItemState> = MutableStateFlow(BudgetItemState())
  val uiState: StateFlow<BudgetItemState>

  init {
    updateDatabaseBudgetValues()
    setSubCategoryInBudget(subCategoryUid)
    uiState = _uiState
  }

  /**
   * Set the subcategory
   *
   * @param subCategoryUid the subcategory uid
   */
  private fun setSubCategoryInBudget(subCategoryUid: String) {
    accountingSubCategoryAPI.getSubCategories(
        CurrentUser.associationUid!!,
        { subCategoryList ->
          val subCategoryInBudget = subCategoryList.find { it.uid == subCategoryUid }
          if (subCategoryInBudget != null) {
            _uiState.value = _uiState.value.copy(subCategory = subCategoryInBudget)
          }
        },
        {})
  }
  /** Update the database values */
  private fun updateDatabaseBudgetValues() {
    // Get the budget items from the database
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

    // Get the categories from the database
    accountingCategoryAPI.getCategories(
        CurrentUser.associationUid!!,
        { categoryList -> _uiState.value = _uiState.value.copy(categoryList = categoryList) },
        {})
  }

  /**
   * Handle the year filter
   *
   * @param yearFilter the year to filter by
   */
  fun onYearFilter(yearFilter: Int) {
    _uiState.value = _uiState.value.copy(yearFilter = yearFilter)
    updateDatabaseBudgetValues()
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

  /** Start editing the Subcategory */
  fun startSubCategoryEditingInBudget() {
    _uiState.value = _uiState.value.copy(subCatEditing = true)
  }

  /**
   * Start save subCategory editing
   *
   * @param name the name of the subcategory
   * @param categoryUid the category uid
   * @param year the year of the subcategory
   */
  fun saveSubCategoryEditingInBudget(name: String, categoryUid: String, year: Int) {
    val subCategoryBudget = AccountingSubCategory(subCategoryUid, categoryUid, name, 0, year)
    accountingSubCategoryAPI.updateSubCategory(subCategoryBudget, {}, {})
    _uiState.value = _uiState.value.copy(subCatEditing = false, subCategory = subCategoryBudget)
  }

  /** Cancel the Subcategory editing */
  fun cancelSubCategoryEditingInBudget() {
    _uiState.value = _uiState.value.copy(subCatEditing = false)
  }

  /** Delete the subcategory and all items related to it */
  fun deleteSubCategoryInBudget() {
    _uiState.value = _uiState.value.copy(budgetList = emptyList())
    accountingSubCategoryAPI.deleteSubCategory(_uiState.value.subCategory, {}, {})
    _uiState.value = _uiState.value.copy(subCatEditing = false)
  }
}

/**
 * The state for the budget item
 *
 * @param budgetList the current list of budget items
 * @param subCategory the current subcategory
 * @param categoryList the current list of categories
 * @param yearFilter the current year filter
 * @param editing the current editing state
 * @param subCatEditing the current category editing state
 * @param editedBudgetItem the current edited budget item
 */
data class BudgetItemState(
    val budgetList: List<BudgetItem> = emptyList(),
    val subCategory: AccountingSubCategory = AccountingSubCategory("", "", "", 0, 2023),
    val categoryList: List<AccountingCategory> = emptyList(),
    val yearFilter: Int = 2023,
    val editing: Boolean = false,
    val subCatEditing: Boolean = false,
    val editedBudgetItem: BudgetItem? = null,
    val filterActive: Boolean = false
)
