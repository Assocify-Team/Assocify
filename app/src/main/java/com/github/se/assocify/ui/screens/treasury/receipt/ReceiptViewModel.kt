package com.github.se.assocify.ui.screens.treasury.receipt

import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.User
import com.github.se.assocify.ui.util.DateUtil
import com.github.se.assocify.ui.util.PriceUtil
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReceiptViewModel {

  private val NEW_RECEIPT_TITLE = "New Receipt"
  private val EDIT_RECEIPT_TITLE = "Edit Receipt"

  private val userAPI = UserAPI(Firebase.firestore)

  private val _uiState: MutableStateFlow<ReceiptState>
  val uiState: StateFlow<ReceiptState>

  constructor() {
    _uiState = MutableStateFlow(ReceiptState(pageTitle = NEW_RECEIPT_TITLE))
    uiState = _uiState
  }

  constructor(todoUID: String) {
    _uiState = MutableStateFlow(ReceiptState(pageTitle = EDIT_RECEIPT_TITLE))
    uiState = _uiState

    /*TODO: Implement fetching of receipt from database*/
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

  fun searchPayer(payerSearch: String) {
    _uiState.value = _uiState.value.copy(payerSearch = payerSearch)
    _uiState.value =
        _uiState.value.copy(
            payerList = userAPI.getAllUsers().filter { it.name.contains(payerSearch) })
    if (_uiState.value.payerList.isEmpty()) {
      _uiState.value = _uiState.value.copy(payerError = "No users found")
    }
  }

  fun setPayer(payer: User) {
    _uiState.value = _uiState.value.copy(payer = payer)
    _uiState.value = _uiState.value.copy(payerSearch = "")
  }

  fun unsetPayer() {
    _uiState.value = _uiState.value.copy(payer = null)
  }

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
    val receipt =
        _uiState.value.payer?.let {
          Receipt(
              uid = "",
              title = _uiState.value.title,
              description = _uiState.value.description,
              amount = PriceUtil.toDouble(_uiState.value.amount),
              payer = it.uid,
              date = DateUtil.toDate(_uiState.value.date),
              incoming = _uiState.value.incoming)
        }
    /*TODO: Implement saving of receipt to database*/
  }

  fun deleteReceipt() {}
}

data class ReceiptState(
    val pageTitle: String,
    val title: String = "",
    val description: String = "",
    val amount: String = "",
    val payerSearch: String = "",
    val payerList: List<User> = emptyList(),
    val payer: User? = null,
    val date: String = "",
    val incoming: Boolean = false,
    val titleError: String? = null,
    val amountError: String? = null,
    val payerError: String? = null,
    val dateError: String? = null,
)
