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
import com.github.se.assocify.ui.util.SnackbarSystem
import com.github.se.assocify.ui.util.SyncSystem
import java.time.Year
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The view model for the budget screen
 *
 * @param accountingCategoryAPI the accounting category api
 * @param accountingSubCategoryAPI the accounting subcategory api
 * @param balanceAPI the balance api
 * @param budgetAPI the budget api
 */
class AccountingViewModel(
    private var accountingCategoryAPI: AccountingCategoryAPI,
    private var accountingSubCategoryAPI: AccountingSubCategoryAPI,
    private var balanceAPI: BalanceAPI,
    private var budgetAPI: BudgetAPI,
    private val snackbarSystem: SnackbarSystem
) : ViewModel() {

  private val _uiState: MutableStateFlow<AccountingState> = MutableStateFlow(AccountingState())
  val uiState: StateFlow<AccountingState>

  private val loadSystem =
      SyncSystem(
          {
            filterSubCategories()
            setSubcategoriesAmount()
            _uiState.value = _uiState.value.copy(refresh = false, loading = false, error = null)
          },
          { _uiState.value = _uiState.value.copy(refresh = false, loading = false, error = it) })

  private val refreshSystem =
      SyncSystem(
          { loadAccounting() },
          {
            _uiState.value = _uiState.value.copy(refresh = false)
            snackbarSystem.showSnackbar(it)
          })

  /** Initialize the view model */
  init {
    uiState = _uiState
    loadAccounting()
  }

  /** Function to load categories and subcategories */
  fun loadAccounting() {
    if (!loadSystem.start(4)) return
    _uiState.value = _uiState.value.copy(loading = true, error = null)
    getCategories()
    getSubCategories()
    getAccountingList()
  }

  fun refreshAccounting() {
    if (!refreshSystem.start(4)) return
    _uiState.value = _uiState.value.copy(refresh = true)
    accountingSubCategoryAPI.updateSubCategoryCache(
        CurrentUser.associationUid!!,
        { refreshSystem.end() },
        { refreshSystem.end("Error refreshing accounting") })
    accountingCategoryAPI.updateCategoryCache(
        CurrentUser.associationUid!!,
        { refreshSystem.end() },
        { refreshSystem.end("Error refreshing tags") })
    budgetAPI.updateBudgetCache(
        CurrentUser.associationUid!!,
        { refreshSystem.end() },
        { refreshSystem.end("Error refreshing budget") })
    balanceAPI.updateBalanceCache(
        CurrentUser.associationUid!!,
        { refreshSystem.end() },
        { refreshSystem.end("Error refreshing balance") })
  }

  /** Function to get the categories from the database */
  private fun getCategories() {
    // Sets the category list in the state from the database
    accountingCategoryAPI.getCategories(
        CurrentUser.associationUid!!,
        { categoryList ->
          _uiState.value = _uiState.value.copy(categoryList = categoryList)
          loadSystem.end()
        },
        { loadSystem.end("Error loading tags") })
  }

  /** Function to get the subcategories from the database */
  private fun getSubCategories() {
    // Sets the subcategory list in the state from the database if a category is selected
    accountingSubCategoryAPI.getSubCategories(
        CurrentUser.associationUid!!,
        { subCategoryList ->
          _uiState.value = _uiState.value.copy(allSubCategoryList = subCategoryList)
          loadSystem.end()
        },
        { loadSystem.end("Error loading categories") })
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
                    it.categoryUID == _uiState.value.selectedCategory!!.uid &&
                        it.year == _uiState.value.yearFilter &&
                        it.name.contains(_uiState.value.searchQuery, ignoreCase = true)
                  })
    }
  }

  /** Function to get the budget and balance items from the database */
  private fun getAccountingList() {
    // get the budgetItem List
    budgetAPI.getBudget(
        CurrentUser.associationUid!!,
        { budgetList ->
          _uiState.value = _uiState.value.copy(budgetItemsList = budgetList)
          loadSystem.end()
        },
        { loadSystem.end("Error loading budget") })

    // get the balanceItem List
    balanceAPI.getBalance(
        CurrentUser.associationUid!!,
        { balanceList ->
          _uiState.value = _uiState.value.copy(balanceItemList = balanceList)
          loadSystem.end()
        },
        { loadSystem.end("Error loading balance") })
  }

  /** Set the amount of a subcategory */
  private fun setSubcategoriesAmount() {
    val updatedAmountBalanceHT: MutableMap<String, Int> = mutableMapOf()
    val updatedAmountBalanceTTC: MutableMap<String, Int> = mutableMapOf()
    val updatedAmountBudgetHT: MutableMap<String, Int> = mutableMapOf()
    val updatedAmountBudgetTTC: MutableMap<String, Int> = mutableMapOf()

    val balanceItemsBySubCategory = _uiState.value.balanceItemList.groupBy { it.subcategoryUID }
    val budgetItemsBySubCategory = _uiState.value.budgetItemsList.groupBy { it.subcategoryUID }

    _uiState.value.allSubCategoryList.forEach { subCategory ->
      val balanceItems = balanceItemsBySubCategory[subCategory.uid].orEmpty()
      val budgetItems = budgetItemsBySubCategory[subCategory.uid].orEmpty()

      balanceItems.forEach { balanceItem ->
        // Add to map the balance amount of the subcategory
        updatedAmountBalanceHT[subCategory.uid] =
            updatedAmountBalanceHT.getOrPut(subCategory.uid) { 0 } + balanceItem.amount

        // Add to map the balance amount of the subcategory with TVA
        val amountWithTVA =
            balanceItem.amount + (balanceItem.amount * balanceItem.tva.rate / 100f).toInt()
        updatedAmountBalanceTTC[subCategory.uid] =
            updatedAmountBalanceTTC.getOrPut(subCategory.uid) { 0 } + amountWithTVA
      }

      budgetItems.forEach { budgetItem ->
        // Add to map the budget amount of the subcategory
        updatedAmountBudgetHT[subCategory.uid] =
            updatedAmountBudgetHT.getOrPut(subCategory.uid) { 0 } + budgetItem.amount

        // Add to map the budget amount of the subcategory with TVA
        val amountWithTVA =
            budgetItem.amount + (budgetItem.amount * budgetItem.tva.rate / 100f).toInt()
        updatedAmountBudgetTTC[subCategory.uid] =
            updatedAmountBudgetTTC.getOrPut(subCategory.uid) { 0 } + amountWithTVA
      }

      // Update the state with the new maps
      _uiState.value =
          _uiState.value.copy(
              amountBalanceHT = updatedAmountBalanceHT,
              amountBalanceTTC = updatedAmountBalanceTTC,
              amountBudgetHT = updatedAmountBudgetHT,
              amountBudgetTTC = updatedAmountBudgetTTC)
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
              selectedCategory = _uiState.value.categoryList.find { it.name == categoryName })
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

  fun resetNewSubcategoryDialog() {
    _uiState.value =
        _uiState.value.copy(
            newSubcategoryTitle = "",
            newSubcategoryCategory = null,
            newSubcategoryYear = _uiState.value.yearFilter.toString(),
            newSubcategoryTitleError = null,
            newSubcategoryCategoryError = null)
  }

  fun showNewSubcategoryDialog() {
    resetNewSubcategoryDialog()
    _uiState.value = _uiState.value.copy(showNewSubcategoryDialog = true)
  }

  fun hideNewSubcategoryDialog() {
    _uiState.value = _uiState.value.copy(showNewSubcategoryDialog = false)
    resetNewSubcategoryDialog()
  }

  fun setNewSubcategoryTitle(title: String) {
    _uiState.value = _uiState.value.copy(newSubcategoryTitle = title)
    when {
      title.isEmpty() -> {
        _uiState.value = _uiState.value.copy(newSubcategoryTitleError = "Name cannot be empty")
      }
      _uiState.value.subCategoryList.any { it.name == title } -> {
        _uiState.value = _uiState.value.copy(newSubcategoryTitleError = "Name already used")
      }
      title.length > 50 -> {
        _uiState.value =
            _uiState.value.copy(
                newSubcategoryTitleError = "Name cannot be longer than 50 characters")
      }
      else -> {
        _uiState.value = _uiState.value.copy(newSubcategoryTitleError = null)
      }
    }
  }

  fun setNewSubcategoryCategory(category: AccountingCategory?) {
    _uiState.value = _uiState.value.copy(newSubcategoryCategory = category)
    if (category == null) {
      _uiState.value = _uiState.value.copy(newSubcategoryCategoryError = "Category cannot be empty")
    } else {
      _uiState.value = _uiState.value.copy(newSubcategoryCategoryError = null)
    }
  }

  fun setNewSubcategoryYear(year: String) {
    _uiState.value = _uiState.value.copy(newSubcategoryYear = year)
  }

  fun createNewSubcategory() {

    setNewSubcategoryCategory(_uiState.value.newSubcategoryCategory)
    setNewSubcategoryTitle(_uiState.value.newSubcategoryTitle)

    if (_uiState.value.newSubcategoryTitleError != null ||
        _uiState.value.newSubcategoryCategoryError != null) {
      return
    }

    val newSubcategory =
        AccountingSubCategory(
            uid = UUID.randomUUID().toString(),
            name = _uiState.value.newSubcategoryTitle,
            categoryUID = _uiState.value.newSubcategoryCategory!!.uid,
            year = _uiState.value.newSubcategoryYear.toInt(),
            amount = 0)
    accountingSubCategoryAPI.addSubCategory(
        CurrentUser.associationUid!!,
        newSubcategory,
        {
          hideNewSubcategoryDialog()
          loadAccounting()
        },
        { Log.e("ACCOUNTING", "New category creation failed") })
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
    val loading: Boolean = false,
    val error: String? = null,
    val refresh: Boolean = false,
    val categoryList: List<AccountingCategory> = emptyList(),
    val selectedCategory: AccountingCategory? = null,
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
    val searchQuery: String = "",
    val showNewSubcategoryDialog: Boolean = false,
    val newSubcategoryTitle: String = "",
    val newSubcategoryCategory: AccountingCategory? = null,
    val newSubcategoryYear: String = Year.now().value.toString(),
    val newSubcategoryTitleError: String? = null,
    val newSubcategoryCategoryError: String? = null,
)
