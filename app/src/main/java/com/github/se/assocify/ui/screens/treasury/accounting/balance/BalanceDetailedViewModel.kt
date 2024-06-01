package com.github.se.assocify.ui.screens.treasury.accounting.balance

import android.util.Log
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
import com.github.se.assocify.ui.util.PriceUtil
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * View model for the balance detailed screen
 *
 * @param balanceApi the balance api
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
  private val maxDescriptionLength = 100
  private val maxNameLength = 50

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
    var innerLoadCounter = 4

    receiptAPI.getAllReceipts(
        { receiptList ->
          _uiState.value = _uiState.value.copy(receiptList = receiptList)
          if (--innerLoadCounter == 0) endLoad()
        },
        { endLoad("Error loading receipts") })

    subCategoryAPI.getSubCategories(
        CurrentUser.associationUid!!,
        { subCategoryList ->
          _uiState.value = _uiState.value.copy(subCategoryList = subCategoryList)
          if (--innerLoadCounter == 0) endLoad()
        },
        { endLoad("Error loading balance category") })

    balanceApi.getBalance(
        CurrentUser.associationUid!!,
        { balanceList ->
          // Filter the balanceList to only include items with the matching subCategoryUid
          val filteredBalanceList =
              balanceList.filter { balanceItem -> balanceItem.subcategoryUID == subCategoryUid }

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
    _uiState.value =
        _uiState.value.copy(
            editing = true,
            editedBalanceItem = balanceItem,
            errorName = null,
            errorReceipt = null,
            errorAmount = null,
            errorAssignee = null,
            errorDescription = null,
            errorDate = null)
  }

  /**
   * Exit the edit state while saving the modifications performed
   *
   * @param balanceItem the new edited budgetItem
   */
  fun saveEditing(balanceItem: BalanceItem) {
    if (_uiState.value.errorName != null ||
        _uiState.value.errorReceipt != null ||
        _uiState.value.errorAmount != null ||
        _uiState.value.errorAssignee != null ||
        _uiState.value.errorDescription != null ||
        _uiState.value.errorDate != null) {
      Log.e("BalanceDetailedViewModel", "Error in editing")
      return
    }
    // update the status of the receipt
    val receipt = _uiState.value.receiptList.find { it.uid == balanceItem.receiptUID }
    if (receipt != null && receipt.status != balanceItem.status) {
      modifyReceiptStatus(receipt, balanceItem.status)
    }

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
                      if (_uiState.value.status == null ||
                          balanceItem.status == _uiState.value.status)
                          _uiState.value.balanceList.filter { it.uid != balanceItem.uid } +
                              balanceItem
                      else _uiState.value.balanceList.filter { it.uid != balanceItem.uid },
                  editedBalanceItem = null)
        },
        { _uiState.value = _uiState.value.copy(errorReceipt = "The receipt is already used!") })
  }

  /** Exit the edit state without keeping the modifications done */
  fun cancelPopUp() {
    _uiState.value =
        _uiState.value.copy(editing = false, editedBalanceItem = null, creating = false)
  }

  fun deleteBalanceItem(balanceItemUid: String) {
    // before deleting, put the status of receipt to pending
    val balanceItem = _uiState.value.balanceList.find { it.uid == balanceItemUid } ?: return
    val receipt = _uiState.value.receiptList.find { it.uid == balanceItem.receiptUID }
    if (receipt != null) {
      modifyReceiptStatus(receipt, Status.Pending)
    }

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
    _uiState.value =
        _uiState.value.copy(
            creating = true,
            errorName = null,
            errorReceipt = null,
            errorAmount = null,
            errorAssignee = null,
            errorDescription = null,
            errorDate = null)
  }

  fun saveCreation(balanceItem: BalanceItem) {
    if (_uiState.value.errorName != null ||
        _uiState.value.errorReceipt != null ||
        _uiState.value.errorAmount != null ||
        _uiState.value.errorAssignee != null ||
        _uiState.value.errorDescription != null ||
        _uiState.value.errorDate != null) {
      return
    }
    // update the status of the receipt
    val receipt = _uiState.value.receiptList.find { it.uid == balanceItem.receiptUID }
    if (receipt != null && receipt.status != balanceItem.status) {
      modifyReceiptStatus(receipt, balanceItem.status)
    }

    balanceApi.addBalance(
        CurrentUser.associationUid!!,
        balanceItem.subcategoryUID,
        balanceItem.receiptUID,
        balanceItem,
        {
          _uiState.value =
              _uiState.value.copy(
                  creating = false,
                  balanceList =
                      if (_uiState.value.status == null ||
                          balanceItem.status == _uiState.value.status)
                          _uiState.value.balanceList + balanceItem
                      else _uiState.value.balanceList,
                  editedBalanceItem = null)
        },
        { _uiState.value = _uiState.value.copy(errorReceipt = "The receipt is already used!") })
  }

  fun checkName(name: String) {
    if (name.length > maxNameLength) {
      _uiState.value = _uiState.value.copy(errorName = "Name is too long")
    } else if (name.isEmpty()) {
      _uiState.value = _uiState.value.copy(errorName = "Name cannot be empty")
    } else {
      _uiState.value = _uiState.value.copy(errorName = null)
    }
  }

  fun checkAmount(amount: String) {
    if (amount.isEmpty()) {
      _uiState.value = _uiState.value.copy(errorAmount = "You cannot have an empty amount!")
    } else if (amount.toDoubleOrNull() == null) {
      _uiState.value = _uiState.value.copy(errorAmount = "You have to input a correct amount!!")
    } else if (PriceUtil.isTooLarge(amount)) {
      _uiState.value = _uiState.value.copy(errorAmount = "Amount is too large!")
    } else {
      _uiState.value = _uiState.value.copy(errorAmount = null)
    }
  }

  fun checkAssignee(assignee: String) {
    if (assignee.isEmpty())
        _uiState.value = _uiState.value.copy(errorAssignee = "Cannot have an empty assignee!")
    else _uiState.value = _uiState.value.copy(errorAssignee = null)
  }

  fun checkDescription(description: String) {
    if (description.length > maxDescriptionLength) {
      _uiState.value = _uiState.value.copy(errorDescription = "Description is too long")
    } else {
      _uiState.value = _uiState.value.copy(errorDescription = null)
    }
  }

  fun checkDate(date: LocalDate) {
    if (date.year != _uiState.value.subCategory!!.year) {
      _uiState.value =
          _uiState.value.copy(
              errorDate =
                  "The year doesn't match ${_uiState.value.subCategory!!.name}'s year: ${_uiState.value.subCategory!!.year}")
    } else {
      _uiState.value = _uiState.value.copy(errorDate = null)
    }
  }

  fun checkAll(
      name: String,
      receiptUid: String?,
      amount: String,
      assignee: String,
      description: String,
      date: LocalDate
  ) {
    checkName(name)
    _uiState.value = _uiState.value.copy(errorReceipt = null)
    checkAmount(amount)
    checkAssignee(assignee)
    checkDescription(description)
    checkDate(date)
  }

  /**
   * Set the no receipt selected state
   *
   * @param selected whether no receipt is selected
   */
  fun noReceiptSelected(selected: Boolean) {
    _uiState.value = _uiState.value.copy(noReceiptSelected = selected)
  }

  /**
   * Modify the status receipt
   *
   * @param receipt the receipt to modify
   * @param status the new status of the receipt
   */
  fun modifyReceiptStatus(receipt: Receipt, status: Status) {
    receiptAPI.uploadReceipt(
        receipt.copy(status = status),
        {},
        {
          _uiState.value =
              _uiState.value.copy(
                  receiptList =
                      _uiState.value.receiptList.map {
                        if (it.uid == receipt.uid) {
                          receipt.copy(status = status)
                        } else it
                      })
        },
        { receiptFail, _ ->
          if (receiptFail) {
            CoroutineScope(Dispatchers.Main).launch {
              _uiState.value.snackbarState.showSnackbar(
                  message = "Failed to save status of the receipt",
                  actionLabel = "Retry",
              )
            }
          }
        })
  }
}

/**
 * The state for the balance item
 *
 * @param loading whether the page is loading
 * @param error the error message, if any
 * @param balanceList the current list of balance items
 * @param subCategory the current subcategory of the item
 * @param categoryList the list of categories
 * @param loadingCategory whether the categories are loading
 * @param status the current status filter
 * @param receiptList the list of receipts
 * @param noReceiptSelected whether no receipt is selected
 * @param subCategoryList the list of subcategories
 * @param editing whether the item is being edited
 * @param editedBalanceItem the item being edited
 * @param subCatEditing whether the subcategory is being edited
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
    val noReceiptSelected: Boolean = true,
    val subCategoryList: List<AccountingSubCategory> = emptyList(),
    val editing: Boolean = false,
    val creating: Boolean = false,
    val editedBalanceItem: BalanceItem? = null,
    val subCatEditing: Boolean = false,
    val snackbarState: SnackbarHostState = SnackbarHostState(),
    val filterActive: Boolean = false,
    val errorName: String? = "",
    val errorReceipt: String? = null,
    val errorAmount: String? = "",
    val errorAssignee: String? = "",
    val errorDescription: String? = "",
    val errorDate: String? = ""
)
