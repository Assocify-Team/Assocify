package com.github.se.assocify.ui.screens.treasury.accounting

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.BudgetItem
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
    private var accountingSubCategoryAPI: AccountingSubCategoryAPI,
    private var balanceAPI: BalanceAPI,
    private var budgetAPI: BudgetAPI
) : ViewModel() {

  private val _uiState: MutableStateFlow<AccountingState> = MutableStateFlow(AccountingState())
  val uiState: StateFlow<AccountingState>

  /** Initialize the view model */
  init {
    getCategories()
    getSubCategories()
      getAccountingList()
    filterSubCategories()
    uiState = _uiState
  }

  /** Function to get the categories from the database */
  private fun getCategories() {
    // Sets the category list in the state from the database
    accountingCategoryAPI.getCategories(
        CurrentUser.associationUid!!,
        { categoryList -> _uiState.value = _uiState.value.copy(categoryList = categoryList) },
        {})
  }

  /** Function to get the subcategories from the database */
  private fun getSubCategories() {
    // Sets the subcategory list in the state from the database if a category is selected
    accountingSubCategoryAPI.getSubCategories(
        CurrentUser.associationUid!!,
        { subCategoryList ->
          _uiState.value = _uiState.value.copy(allSubCategoryList = subCategoryList)
        },
        { Log.d("BudgetViewModel", "Error getting subcategories") })
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

    private fun getAccountingList(){
        // get the budgetItem List
        budgetAPI.getBudget(
            CurrentUser.associationUid!!,
            { budgetList -> _uiState.value = _uiState.value.copy(budgetItemsList = budgetList) },
            { Log.d("BudgetViewModel", "Error getting budget items") })

        // get the balanceItem List
        balanceAPI.getBalance(
            CurrentUser.associationUid!!,
            { balanceList -> _uiState.value = _uiState.value.copy(balanceItemList = balanceList) },
            { Log.d("BudgetViewModel", "Error getting balance items") })
    }

    /** Updates the amount of a subcategory */
    fun setSubcategoryAmount(subCategory: AccountingSubCategory){
        val allSubcategoryList = emptyList<AccountingSubCategory>()
        allSubcategoryList.forEach {
            subCategory ->
            _uiState.value.balanceItemList.filter {
                balanceItem -> subCategory.uid ==  balanceItem.uid
            }
        }

        //updateDatabaseValues()
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
 * @param budgetItemsList: The list of budget items
 * @param balanceItemList: The list of balance items
 * @param allSubCategoryList: The list of all accounting subcategories
 * @param globalSelected: Whether the global category is selected
 * @param yearFilter: The year filter
 * @param tvaFilterActive: Whether the tva filter is active
 * @param searchQuery: The search query
 */
data class AccountingState(
    val categoryList: List<AccountingCategory> = emptyList(),
    val selectedCatUid: String = "",
    val subCategoryList: List<AccountingSubCategory> = emptyList(),
    val allSubCategoryList: List<AccountingSubCategory> = emptyList(),
    val budgetItemsList: List<BudgetItem> = emptyList(),
    val balanceItemList: List<BalanceItem> = emptyList(),
    val amountBudgetHT: Map<String, Int> = emptyMap(),
    val amountBudgetTTC: Map<String, Int> = emptyMap(),
    val amountBalanceHT: Map<String, Int> = emptyMap(),
    val amountBalanceTTC: Map<String, Int> = emptyMap(),
    val globalSelected: Boolean = true,
    val yearFilter: Int = 2024,
    val filterActive: Boolean = false,
    val searchQuery: String = ""
)
