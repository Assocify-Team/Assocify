package com.github.se.assocify.ui.screens.treasury.receipt

import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.ui.util.DateUtil
import com.github.se.assocify.ui.util.PriceUtil
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReceiptViewModel {

  private val _uiState: MutableStateFlow<ReceiptState>
  val uiState: StateFlow<ReceiptState>

  constructor() {
    _uiState = MutableStateFlow(ReceiptState())
    uiState = _uiState
  }

  constructor(todoUID: String) {
    _uiState = MutableStateFlow(ReceiptState())
    uiState = _uiState

    /*TODO: Implement fetching of receipt from database*/
  }

  fun setTitle(title: String) {
    _uiState.value = _uiState.value.copy(title = title)
  }

  fun setDescription(description: String) {
    _uiState.value = _uiState.value.copy(description = description)
  }

  fun setAmount(amount: String) {
    _uiState.value = _uiState.value.copy(amount = amount)
  }

  fun setPayer(payer: String?) {
    /*TODO: Implement checking and fetching of user from database*/
    // _uiState.value = _uiState.value.updateReceipt(payer = payer)
  }

  fun setDate(date: LocalDate?) {
    _uiState.value = _uiState.value.copy(date = DateUtil.toString(date))
  }

  fun setIncoming(incoming: Boolean) {
    _uiState.value = _uiState.value.copy(incoming = incoming)
  }

  fun setImage() {
    /*TODO: Implement image selection / capture and saving*/
  }

  fun saveReceipt() {
    val receipt =
        Receipt(
            uid = "",
            title = _uiState.value.title,
            description = _uiState.value.description,
            amount = PriceUtil.toDouble(_uiState.value.amount),
            payer = _uiState.value.payer,
            date = DateUtil.toDate(_uiState.value.date),
            incoming = _uiState.value.incoming)
    /*TODO: Implement saving of receipt to database*/
  }

  fun deleteReceipt() {}
}

data class ReceiptState(
    val title: String = "",
    val description: String = "",
    val amount: String = "",
    val payer: String = "",
    val date: String = DateUtil.NULL_DATE_STRING,
    val incoming: Boolean = false
)
