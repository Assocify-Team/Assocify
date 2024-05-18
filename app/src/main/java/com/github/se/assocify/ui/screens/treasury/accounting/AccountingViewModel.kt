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
    loadAccounting()
    uiState = _uiState
  }

  private var loadCounter = 0

  private fun startLoad(count: Int) {
    loadCounter = count
    _uiState.value = _uiState.value.copy(loading = true, error = null)
  }

  private fun endLoad(error: String? = null) {
    loadCounter--
    if (error != null) {
      _uiState.value = _uiState.value.copy(loading = false, error = error)
    } else if (loadCounter == 0) {
      filterSubCategories()
      _uiState.value = _uiState.value.copy(loading = false, error = null)
    }
  }

  /** Function to load categories and subcategories */
  fun loadAccounting() {
    startLoad(2)
    getCategories()
    getSubCategories()
  }

  /** Function to get the categories from the database */
  private fun getCategories() {
    // Sets the category list in the state from the database
    accountingCategoryAPI.getCategories(
        CurrentUser.associationUid!!,
        { categoryList ->
          _uiState.value = _uiState.value.copy(categoryList = categoryList)
          endLoad()
        },
        { endLoad("Error loading tags") })
  }

  /** Function to get the subcategories from the database */
  private fun getSubCategories() {
    // Sets the subcategory list in the state from the database if a category is selected
    accountingSubCategoryAPI.getSubCategories(
        CurrentUser.associationUid!!,
        { subCategoryList ->
          _uiState.value = _uiState.value.copy(allSubCategoryList = subCategoryList)
          endLoad()
        },
        { endLoad("Error loading categories") })
  }

  /** Function to filter the subCategoryList */
  private fun filterSubCategories() {
    val allSubCategoryList = _uiState.value.allSubCategoryList
    // If the global category is selected, display all subcategories
    if (_uiState.value.globalSelected) {
      _uiState.value =
          _uiState.value.copy(
              subCategoryList =
                  allSubCategoryList.filter {
                    it.year == _uiState.value.yearFilter &&
                        it.name.contains(_uiState.value.searchQuery, ignoreCase = true)
                  })
    } else {
      _uiState.value =
          _uiState.value.copy(
              subCategoryList =
                  allSubCategoryList.filter {
                    it.categoryUID == _uiState.value.selectedCatUid &&
                        it.year == _uiState.value.yearFilter &&
                        it.name.contains(_uiState.value.searchQuery, ignoreCase = true)
                  })
    }
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
    } else {
      _uiState.value = _uiState.value.copy(globalSelected = false)
      _uiState.value =
          _uiState.value.copy(
              selectedCatUid = _uiState.value.categoryList.find { it.name == categoryName }!!.uid)
    }
    filterSubCategories()
  }

  /** Function to update the year filter */
  fun onYearFilter(yearFilter: Int) {
    _uiState.value = _uiState.value.copy(yearFilter = yearFilter)
    filterSubCategories()
  }

  /**
   * Filter the list of receipts when the user uses the search bar.
   *
   * @param searchQuery: Search query in the search bar
   */
  fun onSearch(searchQuery: String) {
    _uiState.value = _uiState.value.copy(searchQuery = searchQuery)
    filterSubCategories()
  }

  fun modifyTVAFilter(tvaActive: Boolean) {
    _uiState.value = _uiState.value.copy(filterActive = tvaActive)
  }
}

/**
 * The state of the budget screen
 *
 * @param categoryList: The list of accounting categories
 * @param selectedCatUid: The selected category unique identifier
 * @param subCategoryList: The list of accounting subcategories
 * @param allSubCategoryList: The list of all accounting subcategories
 * @param globalSelected: Whether the global category is selected
 * @param yearFilter: The year filter
 * @param filterActive: Whether the filter is active
 * @param searchQuery: The search query
 */
data class AccountingState(
    val loading: Boolean = false,
    val error: String? = null,
    val categoryList: List<AccountingCategory> = emptyList(),
    val selectedCatUid: String = "",
    val subCategoryList: List<AccountingSubCategory> = emptyList(),
    val allSubCategoryList: List<AccountingSubCategory> = emptyList(),
    val globalSelected: Boolean = true,
    val yearFilter: Int = 2024,
    val filterActive: Boolean = false,
    val searchQuery: String = ""
)
