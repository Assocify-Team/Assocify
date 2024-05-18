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
    setSubcategoriesAmount()
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

  private fun getAccountingList() {
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

  /** Set the amount of a subcategory */
  private fun setSubcategoriesAmount() {
    val updatedAmountBalanceHT = _uiState.value.amountBalanceHT.toMutableMap()
    val updatedAmountBalanceTTC = _uiState.value.amountBalanceTTC.toMutableMap()
    val updatedAmountBudgetHT = _uiState.value.amountBudgetHT.toMutableMap()
    val updatedAmountBudgetTTC = _uiState.value.amountBudgetTTC.toMutableMap()

    // For each subcategory, we calculate their amount given the budget and balance items (with and
    // without VAT)
    _uiState.value.allSubCategoryList.forEach { subCategory ->

      // Calculate the amount for the balance screen
      _uiState.value.balanceItemList
          .filter { balanceItem -> subCategory.uid == balanceItem.subcategoryUID }
          .forEach { balanceItem ->
            // Add to map the balance amount of the subcategory
            updatedAmountBalanceHT[subCategory.uid] =
                updatedAmountBalanceHT.getOrPut(subCategory.uid) { 0 } + balanceItem.amount

            // Add to map the balance amount of the subcategory with TVA
            val amountWithTVA =
                balanceItem.amount + (balanceItem.amount * balanceItem.tva.rate / 100f).toInt()
            updatedAmountBalanceTTC[subCategory.uid] =
                updatedAmountBalanceTTC.getOrPut(subCategory.uid) { 0 } + amountWithTVA
          }

      // Calculate the amount for the budget screen
      _uiState.value.budgetItemsList
          .filter { budgetItem -> subCategory.uid == budgetItem.subcategoryUID }
          .forEach { budgetItem ->
            // Add to map the budget amount of the subcategory
            updatedAmountBudgetHT[subCategory.uid] =
                updatedAmountBudgetHT.getOrPut(subCategory.uid) { 0 } + budgetItem.amount
            Log.d("BudgetViewModel", "amountBudgetHT ${updatedAmountBudgetHT[subCategory.uid]}")
            // Add to map the budget amount of the subcategory with TVA
            val amountWithTVA =
                budgetItem.amount + (budgetItem.amount * budgetItem.tva.rate / 100f).toInt()
            updatedAmountBudgetTTC[subCategory.uid] =
                updatedAmountBudgetTTC.getOrPut(subCategory.uid) { 0 } + amountWithTVA
            Log.d("BudgetViewModel", "amountBudgetTTC ${updatedAmountBudgetTTC[subCategory.uid]}")
          }
    }

    // Update the state with the new maps
    _uiState.value =
        _uiState.value.copy(
            amountBalanceHT = updatedAmountBalanceHT,
            amountBalanceTTC = updatedAmountBalanceTTC,
            amountBudgetHT = updatedAmountBudgetHT,
            amountBudgetTTC = updatedAmountBudgetTTC)

    Log.d("BudgetViewModel", "amountBalanceHT ${_uiState.value.amountBalanceHT}")
    Log.d("BudgetViewModel", "amountBalanceTTC ${_uiState.value.amountBalanceTTC}")
    Log.d("BudgetViewModel", "amountBudgetHT ${_uiState.value.amountBudgetHT}")
    Log.d("BudgetViewModel", "amountBudgetTTC ${_uiState.value.amountBudgetTTC}")
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

  /**
   * Function to update the tva filter
   *
   * @param tvaActive: The state of the tva filter
   */
  fun activeTVA(tvaActive: Boolean) {
    _uiState.value = _uiState.value.copy(tvaFilterActive = tvaActive)
  }
}

/**
 * The state of the budget screen
 *
 * @param categoryList: The list of accounting categories
 * @param selectedCatUid: The selected category unique identifier
 * @param subCategoryList: The list of accounting subcategories filtered
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
    val amountBudgetHT: MutableMap<String, Int> = mutableMapOf(),
    val amountBudgetTTC: MutableMap<String, Int> = mutableMapOf(),
    val amountBalanceHT: MutableMap<String, Int> = mutableMapOf(),
    val amountBalanceTTC: MutableMap<String, Int> = mutableMapOf(),
    val globalSelected: Boolean = true,
    val yearFilter: Int = 2024,
    val tvaFilterActive: Boolean = false,
    val searchQuery: String = ""
)
