package com.github.se.assocify.ui.screens.treasury.accounting

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The view model for the budget screen
 *
 * @param accountingCategoryAPI the accounting category api
 * @param accountingSubCategoryAPI the accounting subcategory api
 */
class AccountingViewModel(
    private var accountingCategoryAPI: AccountingCategoryAPI,
    private var accountingSubCategoryAPI: AccountingSubCategoryAPI
) : ViewModel() {

  private val _uiState: MutableStateFlow<AccountingState> = MutableStateFlow(AccountingState())
  val uiState: StateFlow<AccountingState>

  /** Initialize the view model */
  init {
    updateDatabaseValues()
    uiState = _uiState
  }

  /** Function to update the database values */
  private fun updateDatabaseValues() {
    // Sets the category list in the state from the database
    accountingCategoryAPI.getCategories(
        CurrentUser.associationUid!!,
        { categoryList -> _uiState.value = _uiState.value.copy(categoryList = categoryList) },
        {})

    // Sets the subcategory list in the state from the database if a category is selected
    accountingSubCategoryAPI.getSubCategories(
        CurrentUser.associationUid!!,
        { subCategoryList ->
          // If the global category is selected, display all subcategories
          if (_uiState.value.globalSelected) {
            _uiState.value =
                _uiState.value.copy(
                    subCategoryList =
                        subCategoryList.filter { it.year == _uiState.value.yearFilter })
          } else {
            _uiState.value =
                _uiState.value.copy(
                    subCategoryList =
                        subCategoryList.filter {
                          it.categoryUID == _uiState.value.selectedCatUid &&
                              it.year == _uiState.value.yearFilter
                        })
          }
        },
        {})
  }

  /**
   * Function to update the subcategories list when a category is selected
   *
   * @param categoryName: The name of the selected category
   */
  fun onSelectedCategory(categoryName: String) {
    // if the category is global, display all subcategories
    if (categoryName == "Global") {
      _uiState.value = _uiState.value.copy(globalSelected = true)
      updateDatabaseValues()
    } else {
      _uiState.value = _uiState.value.copy(globalSelected = false)
      _uiState.value =
          _uiState.value.copy(
              selectedCatUid = _uiState.value.categoryList.find { it.name == categoryName }!!.uid)
      updateDatabaseValues()
    }
  }

  /** Function to update the year filter */
  fun onYearFilter(yearFilter: Int) {
    _uiState.value = _uiState.value.copy(yearFilter = yearFilter)
    updateDatabaseValues()
  }
}

/**
 * The state of the budget screen
 *
 * @param categoryList: The list of accounting categories
 * @param selectedCatUid: The selected category unique identifier
 * @param subCategoryList: The list of accounting subcategories
 * @param globalSelected: Whether the global category is selected
 */
data class AccountingState(
    val categoryList: List<AccountingCategory> = emptyList(),
    val selectedCatUid: String = "",
    val subCategoryList: List<AccountingSubCategory> = emptyList(),
    val globalSelected: Boolean = true,
    val yearFilter: Int = 2024
)
