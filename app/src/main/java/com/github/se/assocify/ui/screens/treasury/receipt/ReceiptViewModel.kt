package com.github.se.assocify.ui.screens.treasury.receipt

import androidx.compose.material3.SnackbarHostState
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.model.entities.Phase
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.util.DateUtil
import com.github.se.assocify.ui.util.PriceUtil
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReceiptViewModel {

  private val NEW_RECEIPT_TITLE = "New Receipt"
  private val EDIT_RECEIPT_TITLE = "Edit Receipt"

  private val receiptApi: ReceiptAPI
  private val navActions: NavigationActions
  private val receiptUid: String

  private val _uiState: MutableStateFlow<ReceiptState>
  val uiState: StateFlow<ReceiptState>

  constructor(
      navActions: NavigationActions,
      receiptApi: ReceiptAPI =
          ReceiptAPI(
              userId = CurrentUser.userUid!!,
              basePath = "associations/" + CurrentUser.associationUid!!,
              storage = Firebase.storage,
              db = Firebase.firestore)
  ) {
    this.navActions = navActions
    this.receiptApi = receiptApi
    this.receiptUid = receiptApi.getNewId()
    _uiState = MutableStateFlow(ReceiptState(isNewReceipt = true, pageTitle = NEW_RECEIPT_TITLE))
    uiState = _uiState
  }

  constructor(
      receiptUid: String,
      navActions: NavigationActions,
      receiptApi: ReceiptAPI =
          ReceiptAPI(
              userId = CurrentUser.userUid!!,
              basePath = "associations/" + CurrentUser.associationUid!!,
              storage = Firebase.storage,
              db = Firebase.firestore)
  ) {
    this.navActions = navActions
    this.receiptApi = receiptApi
    this.receiptUid = receiptUid
    _uiState = MutableStateFlow(ReceiptState(isNewReceipt = false, pageTitle = EDIT_RECEIPT_TITLE))
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
    when {
      PriceUtil.hasInvalidCharacters(amount) -> {
        return
      }
      amount.isEmpty() -> {
        _uiState.value = _uiState.value.copy(amount = amount)
        _uiState.value = _uiState.value.copy(amountError = "Price cannot be empty")
      }
      PriceUtil.isZero(amount) -> {
        _uiState.value = _uiState.value.copy(amount = amount)
        _uiState.value = _uiState.value.copy(amountError = "Price cannot be zero")
      }
      !PriceUtil.isTooPrecise(amount) && PriceUtil.isTooLarge(amount) -> {
        _uiState.value = _uiState.value.copy(amount = amount)
        _uiState.value = _uiState.value.copy(amountError = "Price is too large")
      }
      !PriceUtil.isTooPrecise(amount) && !PriceUtil.isTooLarge(amount) -> {
        _uiState.value = _uiState.value.copy(amount = amount)
        _uiState.value = _uiState.value.copy(amountError = null)
      }
    }
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
    setTitle(_uiState.value.title)
    setAmount(_uiState.value.amount)
    setDate(DateUtil.toDate(_uiState.value.date))

    if (_uiState.value.titleError != null ||
        _uiState.value.amountError != null ||
        _uiState.value.dateError != null) {
      return
    }
    val date = DateUtil.toDate(_uiState.value.date) ?: return

    val receipt =
        Receipt(
            uid = receiptUid,
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
        onFailure = { receiptFail, _ ->
          if (receiptFail) {
            CoroutineScope(Dispatchers.Main).launch {
              _uiState.value.snackbarHostState.showSnackbar(
                  message = "Failed to save receipt",
                  actionLabel = "Retry",
              )
            }
          } else {
            CoroutineScope(Dispatchers.Main).launch {
              _uiState.value.snackbarHostState.showSnackbar(
                  message = "Failed to save image",
                  actionLabel = "Retry",
              )
            }
          }
        })
  }

  fun deleteReceipt() {
    if (_uiState.value.isNewReceipt) {
      navActions.back()
    } else {
      receiptApi.deleteReceipt(
          id = receiptUid,
          onSuccess = { navActions.back() },
          onFailure = { _ ->
            CoroutineScope(Dispatchers.Main).launch {
              _uiState.value.snackbarHostState.showSnackbar(
                  message = "Failed to delete receipt",
                  actionLabel = "Retry",
              )
            }
          })
    }
  }
}

data class ReceiptState(
    val isNewReceipt: Boolean,
    val pageTitle: String,
    val title: String = "",
    val description: String = "",
    val amount: String = "",
    val date: String = "",
    val incoming: Boolean = false,
    val titleError: String? = null,
    val amountError: String? = null,
    val dateError: String? = null,
    val snackbarHostState: SnackbarHostState = SnackbarHostState()
)
