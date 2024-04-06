package com.github.se.assocify.ui.screens.treasury.receipt

import com.github.se.assocify.model.entities.Receipt
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
    _uiState.value = _uiState.value.updateReceipt(title = title)
  }

  fun setDescription(description: String) {
    _uiState.value = _uiState.value.updateReceipt(description = description)
  }

  fun setAmount(amount: Double): Boolean {
    if (amount < 0) return false
    /*TODO: Implement currency formatting and add appropriate checks*/
    _uiState.value = _uiState.value.updateReceipt(amount = amount)
    return true
  }

  fun setPayer(payer: String) {
    /*TODO: Implement checking and fetching of user from database*/
    _uiState.value = _uiState.value.updateReceipt(payer = payer)
  }

  fun setDate(date: String) {
    _uiState.value = _uiState.value.updateReceipt(date = date)
  }

  fun setIncoming(incoming: Boolean) {
    _uiState.value = _uiState.value.updateReceipt(incoming = incoming)
  }

  fun saveReceipt() {}
}

class ReceiptState(val receipt: Receipt) {
  constructor() : this(Receipt())

  fun updateReceipt(
      title: String = receipt.title,
      description: String = receipt.description,
      amount: Double = receipt.amount,
      payer: String = receipt.payer.uid,
      date: String = receipt.date,
      incoming: Boolean = receipt.incoming
  ) =
      ReceiptState(
          receipt =
              receipt.copy(
                  title = title,
                  description = description,
                  amount = amount,
                  payer = receipt.payer.copy(uid = payer),
                  date = date,
                  incoming = incoming))
}
