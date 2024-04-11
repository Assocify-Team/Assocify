package com.github.se.assocify.ui.screens.treasury.receipt

import com.github.se.assocify.model.database.ReceiptsAPI
import com.github.se.assocify.model.entities.Phase
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.util.DateUtil
import com.github.se.assocify.ui.util.PriceUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReceiptViewModel {

  private val NEW_RECEIPT_TITLE = "New Receipt"
  private val EDIT_RECEIPT_TITLE = "Edit Receipt"

  private val receiptApi: ReceiptsAPI
  private val navActions: NavigationActions

  private val _uiState: MutableStateFlow<ReceiptState>
  val uiState: StateFlow<ReceiptState>

  constructor(
      navActions: NavigationActions,
      receiptApi: ReceiptsAPI =
          ReceiptsAPI(
              userId = Firebase.auth.currentUser!!.uid,
              basePath = "basePath",
              storage = Firebase.storage,
              db = Firebase.firestore)
  ) {
    this.navActions = navActions
    this.receiptApi = receiptApi
    _uiState = MutableStateFlow(ReceiptState(pageTitle = NEW_RECEIPT_TITLE))
    uiState = _uiState
  }

  constructor(
      receiptUid: String,
      navActions: NavigationActions,
      receiptApi: ReceiptsAPI =
          ReceiptsAPI(
              userId = Firebase.auth.currentUser!!.uid,
              basePath = "basePath",
              storage = Firebase.storage,
              db = Firebase.firestore)
  ) {
    this.navActions = navActions
    this.receiptApi = receiptApi
    _uiState = MutableStateFlow(ReceiptState(pageTitle = EDIT_RECEIPT_TITLE))
    uiState = _uiState

    this.receiptApi.getUserReceipts(
        onSuccess = { receipts ->
          receipts.forEach { receipt ->
            if (receipt.uid == receiptUid) {
              _uiState.value =
                  _uiState.value.copy(
                      title = receipt.title,
                      description = receipt.description,
                      amount = PriceUtil.fromCents(receipt.cents),
                      date = DateUtil.toString(receipt.date),
                      incoming = receipt.incoming)
            }
          }
        },
        onError = {})
  }

  fun setTitle(title: String) {
    _uiState.value = _uiState.value.copy(title = title)
    if (title.isEmpty()) {
      _uiState.value = _uiState.value.copy(titleError = "Title cannot be empty")
    } else {
      _uiState.value = _uiState.value.copy(titleError = null)
    }
  }

  fun setDescription(description: String) {
    _uiState.value = _uiState.value.copy(description = description)
  }

  fun setAmount(amount: String) {
    if (PriceUtil.hasInvalidCharacters(amount)) {
      return
    }
    if (amount.isEmpty()) {
      _uiState.value = _uiState.value.copy(amount = amount)
      _uiState.value = _uiState.value.copy(amountError = "Price cannot be empty")
    } else if (PriceUtil.isZero(amount)) {
      _uiState.value = _uiState.value.copy(amount = amount)
      _uiState.value = _uiState.value.copy(amountError = "Price cannot be zero")
    } else if (!PriceUtil.isTooPrecise(amount) && PriceUtil.isTooLarge(amount)) {
      _uiState.value = _uiState.value.copy(amount = amount)
      _uiState.value = _uiState.value.copy(amountError = "Price is too large")
    } else if (!PriceUtil.isTooPrecise(amount) && !PriceUtil.isTooLarge(amount)) {
      _uiState.value = _uiState.value.copy(amount = amount)
      _uiState.value = _uiState.value.copy(amountError = null)
    }
  }

  /*fun searchPayer(payerSearch: String) {
    _uiState.value = _uiState.value.copy(payerSearch = payerSearch)
    userAPI.getAllUsers { userList ->
      _uiState.value =
          _uiState.value.copy(
              payerList = userList.filter { it.name.lowercase().contains(payerSearch.lowercase()) })

      if (_uiState.value.payerList.isEmpty()) {
        _uiState.value = _uiState.value.copy(payerError = "No users found")
      } else {
        _uiState.value = _uiState.value.copy(payerError = null)
      }
    }
  }

  fun setPayer(payer: User) {
    _uiState.value = _uiState.value.copy(payer = payer)
    _uiState.value = _uiState.value.copy(payerSearch = "")
  }

  fun unsetPayer() {
    _uiState.value = _uiState.value.copy(payer = null)
  }*/

  fun setDate(date: LocalDate?) {
    _uiState.value = _uiState.value.copy(date = DateUtil.toString(date))
    if (date == null) {
      _uiState.value = _uiState.value.copy(dateError = "Date cannot be empty")
    } else {
      _uiState.value = _uiState.value.copy(dateError = null)
    }
  }

  fun setIncoming(incoming: Boolean) {
    _uiState.value = _uiState.value.copy(incoming = incoming)
  }

  fun setImage() {
    /*TODO: Implement image selection / capture and saving*/
  }

  fun saveReceipt() {
    if (_uiState.value.title.isEmpty()) {
      _uiState.value = _uiState.value.copy(titleError = "Title cannot be empty")
      return
    }
    if (_uiState.value.amount.isEmpty()) {
      _uiState.value = _uiState.value.copy(amountError = "Price cannot be empty")
      return
    }
    if (_uiState.value.date.isEmpty()) {
      _uiState.value = _uiState.value.copy(dateError = "Date cannot be empty")
      return
    }
    val date = DateUtil.toDate(_uiState.value.date)
    if (date == null) {
      _uiState.value = _uiState.value.copy(dateError = "Invalid date")
      return
    }

    val receipt =
        Receipt(
            uid = "",
            title = _uiState.value.title,
            description = _uiState.value.description,
            cents = PriceUtil.toCents(_uiState.value.amount),
            date = date,
            incoming = _uiState.value.incoming,
            phase = Phase.Unapproved,
            photo = null)

    receiptApi.uploadReceipt(
        receipt,
        onPhotoUploadSuccess = {},
        onReceiptUploadSuccess = { navActions.back() },
        onFailure = { b, e -> })
  }

  fun deleteReceipt() {}
}

data class ReceiptState(
    val pageTitle: String,
    val title: String = "",
    val description: String = "",
    val amount: String = "",
    /*    val payerSearch: String = "",
    val payerList: List<User> = emptyList(),
    val payer: User? = null,*/
    val date: String = "",
    val incoming: Boolean = false,
    val titleError: String? = null,
    val amountError: String? = null,
    // val payerError: String? = null,
    val dateError: String? = null,
)
