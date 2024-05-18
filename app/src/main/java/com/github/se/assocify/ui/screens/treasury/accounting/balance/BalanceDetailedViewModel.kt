package com.github.se.assocify.ui.screens.treasury.accounting.balance

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.navigation.NavigationActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * View model for the balance detailed screen
 *
 * @param balanceApi the balance api
 * @param accountingSubCategoryAPI the accounting subcategory api
 * @param accountingCategoryAPI the accounting category api
 * @param subCategoryUid the subcategory uid
 */
class BalanceDetailedViewModel(
    private var navigationActions: NavigationActions,
    private var balanceApi: BalanceAPI,
    private var receiptAPI: ReceiptAPI,
    private var subCategoryAPI: AccountingSubCategoryAPI,
    private var accountingCategoryAPI: AccountingCategoryAPI,
    private var subCategoryUid: String
) : ViewModel() {
  private val _uiState: MutableStateFlow<BalanceItemState> = MutableStateFlow(BalanceItemState())
  val uiState: StateFlow<BalanceItemState>

  init {
    loadBalanceDetails()
    uiState = _uiState
  }

  private var loadCounter = 0

  fun loadBalanceDetails() {
    loadCounter += 2
    _uiState.value = _uiState.value.copy(loading = true, error = null)
    setSubCategoryInBalance(subCategoryUid)
    updateDatabaseValuesInBalance()
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
  private fun setSubCategoryInBalance(subCategoryUid: String) {
    subCategoryAPI.getSubCategories(
        CurrentUser.associationUid!!,
        { subCategoryList ->
          val subCategory = subCategoryList.find { it.uid == subCategoryUid }
          if (subCategory != null) {
            _uiState.value = _uiState.value.copy(subCategory = subCategory)
          }
          endLoad()
        },
        { endLoad("Error loading category") })
  }

  /** Update the database values */
  private fun updateDatabaseValuesInBalance() {
    var innerLoadCounter = 2

    receiptAPI.getUserReceipts(
        { receiptList -> _uiState.value = _uiState.value.copy(receiptList = receiptList) }, {})

    subCategoryAPI.getSubCategories(
        CurrentUser.associationUid!!,
        { subCategoryList ->
          _uiState.value = _uiState.value.copy(subCategoryList = subCategoryList)
        },
        {})

    balanceApi.getBalance(
        CurrentUser.associationUid!!,
        { balanceList ->
          // Filter the balanceList to only include items with the matching subCategoryUid, year and
          // status
          val filteredBalanceList =
              balanceList.filter { balanceItem ->
                balanceItem.date.year == _uiState.value.year &&
                    balanceItem.subcategoryUID == subCategoryUid
              }

          // if status is not null, filter the list by status
          val statusFilteredBalanceList =
              if (_uiState.value.status != null) {
                filteredBalanceList.filter { balanceItem ->
                  balanceItem.status == _uiState.value.status
                }
              } else {
                filteredBalanceList
              }

          // Update the UI state with the filtered list
          _uiState.value = _uiState.value.copy(balanceList = statusFilteredBalanceList)
          if (--innerLoadCounter == 0) endLoad()
        },
        { endLoad("Error loading balance items") })

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
   * Handle the year filter
   *
   * @param year the year to filter by
   */
  fun onYearFilter(year: Int) {
    _uiState.value = _uiState.value.copy(year = year)
    updateDatabaseValuesInBalance()
  }

  /**
   * Handle the status filter
   *
   * @param status the status to filter by
   */
  fun onStatusFilter(status: Status?) {
    _uiState.value = _uiState.value.copy(status = status)
    updateDatabaseValuesInBalance()
  }

  /** Start editing the Subcategory */
  fun startSubCategoryEditingInBalance() {
    _uiState.value = _uiState.value.copy(subCatEditing = true)
  }

  /**
   * Save the Subcategory editing
   *
   * @param name the new name of the subCategory
   * @param categoryUid the new category uid associated with the subCategory
   * @param year the new year of the subCategory
   */
  fun saveSubCategoryEditingInBalance(name: String, categoryUid: String, year: Int) {
    val subCategory = AccountingSubCategory(subCategoryUid, categoryUid, name, 0, year)
    subCategoryAPI.updateSubCategory(
        subCategory,
        {
          _uiState.value = _uiState.value.copy(subCategory = subCategory)
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
  fun cancelSubCategoryEditingInBalance() {
    _uiState.value = _uiState.value.copy(subCatEditing = false)
  }

  /** Delete the subcategory and all items related to it */
  fun deleteSubCategoryInBalance() {
    if (_uiState.value.subCategory == null) return
    subCategoryAPI.deleteSubCategory(
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

  /**
   * Set the TVA filter as active or inactive
   *
   * @param tvaActive the new status of the TVA filter
   */
  fun modifyTVAFilter(tvaActive: Boolean) {
    _uiState.value = _uiState.value.copy(filterActive = tvaActive)
  }

  /**
   * Enter in the edit state so the popup appears
   *
   * @param balanceItem the item we want to edit
   */
  fun startEditing(balanceItem: BalanceItem) {
    _uiState.value = _uiState.value.copy(editing = true, editedBalanceItem = balanceItem)
  }

  /**
   * Exit the edit state while saving the modifications performed
   *
   * @param balanceItem the new edited budgetItem
   */
  fun saveEditing(balanceItem: BalanceItem) {
    balanceApi.updateBalance(
        CurrentUser.associationUid!!,
        balanceItem,
        balanceItem.receiptUID,
        balanceItem.subcategoryUID,
        {
          _uiState.value =
              _uiState.value.copy(
                  editing = false,
                  balanceList =
                      _uiState.value.balanceList.filter { it.uid != balanceItem.uid } + balanceItem,
                  editedBalanceItem = null)
        },
        { _uiState.value = _uiState.value.copy(editedBalanceItem = null, editing = false) })
  }

  /** Exit the edit state without keeping the modifications done */
  fun cancelPopUp() {
    _uiState.value =
        _uiState.value.copy(editing = false, editedBalanceItem = null, creating = false)
  }

  fun deleteBalanceItem(balanceItemUid: String) {
    balanceApi.deleteBalance(
        balanceItemUid,
        {
          _uiState.value =
              _uiState.value.copy(
                  balanceList = _uiState.value.balanceList.filter { it.uid != balanceItemUid },
                  editedBalanceItem = null,
                  editing = false)
        },
        { _uiState.value = _uiState.value.copy(editedBalanceItem = null, editing = false) })
  }

  fun startCreation() {
    _uiState.value = _uiState.value.copy(creating = true)
  }

  fun saveCreation(balanceItem: BalanceItem) {
    if (_uiState.value.errorName ||
        _uiState.value.errorCategory ||
        _uiState.value.errorReceipt ||
        _uiState.value.errorAmount ||
        _uiState.value.errorAssignee) {
      return
    }
    balanceApi.addBalance(
        CurrentUser.associationUid!!,
        balanceItem.subcategoryUID,
        balanceItem.receiptUID,
        balanceItem,
        {
          _uiState.value =
              _uiState.value.copy(
                  creating = false, balanceList = _uiState.value.balanceList + balanceItem)
        },
        { _uiState.value = _uiState.value.copy(creating = false) })
  }

  fun checkName(name: String) {
    _uiState.value = _uiState.value.copy(errorName = name.isEmpty() || name.length > 50)
  }

  fun checkCategory(categoryUid: String) {
    _uiState.value = _uiState.value.copy(errorCategory = categoryUid.isEmpty())
  }

  fun checkReceipt(receiptUid: String) {
    _uiState.value = _uiState.value.copy(errorReceipt = receiptUid.isEmpty())
  }

  fun checkAmount(amount: String) {
    _uiState.value =
        _uiState.value.copy(
            errorAmount =
                amount.isEmpty() || amount.toDoubleOrNull() == null || amount.toDouble() < 0)
  }

  fun checkAssignee(assignee: String) {
    _uiState.value = _uiState.value.copy(errorAssignee = assignee.isEmpty())
  }

  fun checkAll(
      name: String,
      categoryUid: String,
      receiptUid: String,
      amount: String,
      assignee: String
  ) {
    checkName(name)
    checkCategory(categoryUid)
    checkReceipt(receiptUid)
    checkAmount(amount)
    checkAssignee(assignee)
  }
}

/**
 * The state for the balance item
 *
 * @param loading whether the page is loading
 * @param error the error message, if any
 * @param balanceList the current list of balance items
 * @param subCategory the current subcategory of the item
 * @param status the current status
 * @param subCatEditing whether the subcategory is being edited
 * @param year the current year
 * @param snackbarState the snackbar state
 * @param filterActive if the tva filter is active or not
 */
data class BalanceItemState(
    val loading: Boolean = false,
    val error: String? = null,
    val balanceList: List<BalanceItem> = emptyList(),
    val subCategory: AccountingSubCategory? = null,
    val categoryList: List<AccountingCategory> = emptyList(),
    val loadingCategory: Boolean = false,
    val status: Status? = null,
    val receiptList: List<Receipt> = emptyList(),
    val subCategoryList: List<AccountingSubCategory> = emptyList(),
    val editing: Boolean = false,
    val creating: Boolean = false,
    val editedBalanceItem: BalanceItem? = null,
    val subCatEditing: Boolean = false,
    val year: Int = 2023,
    val snackbarState: SnackbarHostState = SnackbarHostState(),
    val filterActive: Boolean = false,
    val errorName: Boolean = false,
    val errorCategory: Boolean = false,
    val errorReceipt: Boolean = false,
    val errorAmount: Boolean = false,
    val errorAssignee: Boolean = false
)
