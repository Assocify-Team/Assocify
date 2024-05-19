package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.navigation.NavigationActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * The view model for the budget detailed screen
 *
 * @param budgetApi the budget api
 * @param accountingSubCategoryAPI the accounting subcategory api
 * @param accountingCategoryAPI the accounting category api
 * @param subCategoryUid the subcategory uid
 */
class BudgetDetailedViewModel(
    private var navigationActions: NavigationActions,
    private var budgetApi: BudgetAPI,
    private var accountingSubCategoryAPI: AccountingSubCategoryAPI,
    private var accountingCategoryAPI: AccountingCategoryAPI,
    private var subCategoryUid: String
) : ViewModel() {
  private val _uiState: MutableStateFlow<BudgetItemState> = MutableStateFlow(BudgetItemState())
  val uiState: StateFlow<BudgetItemState>

  init {
    loadBudgetDetails()
    uiState = _uiState
  }

  private var loadCounter = 0
  private val maxNameLength = 50
  private val maxDescriptionLength = 200

  fun loadBudgetDetails() {
    loadCounter += 2
    _uiState.value = _uiState.value.copy(loading = true, error = null)
    setSubCategoryInBudget(subCategoryUid)
    updateDatabaseBudgetValues()
  }

  private fun endLoad(error: String? = null) {
    loadCounter--
    if (error != null) {
      _uiState.value = _uiState.value.copy(loading = false, error = error)
    } else if (loadCounter == 0 && _uiState.value.error == null) {
      _uiState.value = _uiState.value.copy(loading = false, error = null)
    }
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
          endLoad()
        },
        { endLoad("Error loading category") })
  }
  /** Update the database values */
  private fun updateDatabaseBudgetValues() {
    var innerLoadCounter = 2
    // Get the budget items from the database
    budgetApi.getBudget(
        CurrentUser.associationUid!!,
        { budgetList ->
          // Filter the budgetList to only include items with the matching subCategoryUid
          val filteredList =
              budgetList.filter { budgetItem -> budgetItem.subcategoryUID == subCategoryUid }

          // Update the UI state with the filtered list
          _uiState.value = _uiState.value.copy(budgetList = filteredList)
          if (--innerLoadCounter == 0) endLoad()
        },
        { endLoad("Error loading budget items") })

    // Get the categories from the database
    accountingCategoryAPI.getCategories(
        CurrentUser.associationUid!!,
        { categoryList ->
          _uiState.value = _uiState.value.copy(categoryList = categoryList)
          if (--innerLoadCounter == 0) endLoad()
        },
        { endLoad("Error loading tags") })
  }

  /**
   * Enter in the edit state so the popup appears
   *
   * @param budgetItem the item we want to edit
   */
  fun startEditing(budgetItem: BudgetItem) {
    _uiState.value =
        _uiState.value.copy(
            editing = true,
            editedBudgetItem = budgetItem,
            titleError = false,
            amountError = false,
            descriptionError = false)
  }

  /**
   * Exit the edit state while saving the modifications performed
   *
   * @param budgetItem the new edited budgetItem
   */
  fun saveEditing(budgetItem: BudgetItem) {
    if (_uiState.value.titleError ||
        _uiState.value.amountError ||
        _uiState.value.descriptionError) {
      return
    }
    budgetApi.updateBudgetItem(
        CurrentUser.associationUid!!,
        budgetItem,
        {
          _uiState.value =
              _uiState.value.copy(
                  editing = false,
                  budgetList =
                      _uiState.value.budgetList.filter { it.uid != budgetItem.uid } + budgetItem)
        },
        {})
  }

  /** Exit the edit state without keeping the modifications done */
  fun cancelEditing() {
    _uiState.value = _uiState.value.copy(editing = false, editedBudgetItem = null)
  }

  /** Delete the item that is being edited from the list */
  fun deleteEditing() {
    budgetApi.deleteBudgetItem(
        _uiState.value.editedBudgetItem!!.uid,
        {
          _uiState.value =
              _uiState.value.copy(
                  editing = false,
                  budgetList =
                      _uiState.value.budgetList.filter {
                        it.uid != _uiState.value.editedBudgetItem!!.uid
                      },
                  editedBudgetItem = null)
        },
        {})
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
    accountingSubCategoryAPI.updateSubCategory(
        subCategoryBudget,
        {
          _uiState.value = _uiState.value.copy(subCategory = subCategoryBudget)
          _uiState.value = _uiState.value.copy(subCatEditing = false)
        },
        {
          _uiState.value = _uiState.value.copy(subCatEditing = false)
          CoroutineScope(Dispatchers.Main).launch {
            _uiState.value.snackbarState.showSnackbar(
                message = "Failed to update category",
            )
          }
        })
  }

  /** Cancel the Subcategory editing */
  fun cancelSubCategoryEditingInBudget() {
    _uiState.value = _uiState.value.copy(subCatEditing = false)
  }

  /** Delete the subcategory and all items related to it */
  fun deleteSubCategoryInBudget() {
    if (_uiState.value.subCategory == null) return
    accountingSubCategoryAPI.deleteSubCategory(
        _uiState.value.subCategory!!,
        { navigationActions.back() },
        {
          _uiState.value = _uiState.value.copy(subCatEditing = false)
          CoroutineScope(Dispatchers.Main).launch {
            _uiState.value.snackbarState.showSnackbar(
                message = "Failed to delete category",
            )
          }
        })
  }

  fun startCreating() {
    _uiState.value =
        _uiState.value.copy(
            creating = true, titleError = false, amountError = false, descriptionError = false)
  }

  fun cancelCreating() {
    _uiState.value =
        _uiState.value.copy(
            creating = false,
        )
  }

  fun saveCreating(budgetItem: BudgetItem) {
    if (_uiState.value.titleError ||
        _uiState.value.amountError ||
        _uiState.value.descriptionError) {
      return
    }
    budgetApi.addBudgetItem(
        CurrentUser.associationUid!!,
        budgetItem,
        {
          _uiState.value = _uiState.value.copy(budgetList = _uiState.value.budgetList + budgetItem)
        },
        {})
    _uiState.value = _uiState.value.copy(creating = false)
  }

  /**
   * Checks if the format of title is good and changes the error depending on it
   *
   * @param title the string that is checked
   */
  fun setTitle(title: String) {
    if (title.length > maxNameLength) {
      _uiState.value =
          _uiState.value.copy(
              titleError = true,
              titleErrorString = "Title is too long, max length is $maxNameLength")
    } else if (title.isEmpty()) {
      _uiState.value = _uiState.value.copy(titleError = true, titleErrorString = "Title is empty")
    } else {
      _uiState.value = _uiState.value.copy(titleError = false)
    }
  }

  /**
   * Checks if the format of amount is good and changes the error depending on it
   *
   * @param amount the string that is checked
   */
  fun setAmount(amount: String) {
    _uiState.value =
        _uiState.value.copy(
            amountError =
                amount.isEmpty() || amount.toDoubleOrNull() == null || amount.toDouble() < 0.0)
  }

  fun setDescription(description: String) {
    _uiState.value =
        _uiState.value.copy(descriptionError = description.length > maxDescriptionLength)
  }
}

/**
 * The state for the budget item
 *
 * @param loading weather the page is loading
 * @param error the current error message
 * @param budgetList the current list of budget items
 * @param subCategory the current subcategory
 * @param categoryList the current list of categories
 * @param yearFilter the current year filter
 * @param editing the current editing state
 * @param subCatEditing the current category editing state
 * @param editedBudgetItem the current edited budget item
 * @param snackbarState the snackbar state
 */
data class BudgetItemState(
    val loading: Boolean = false,
    val error: String? = null,
    val budgetList: List<BudgetItem> = emptyList(),
    val subCategory: AccountingSubCategory? = null,
    val categoryList: List<AccountingCategory> = emptyList(),
    val editing: Boolean = false,
    val creating: Boolean = false,
    val subCatEditing: Boolean = false,
    val editedBudgetItem: BudgetItem? = null,
    val snackbarState: SnackbarHostState = SnackbarHostState(),
    val filterActive: Boolean = false,
    val titleError: Boolean = false,
    val titleErrorString: String = "",
    val amountError: Boolean = false,
    val descriptionError: Boolean = false
)
