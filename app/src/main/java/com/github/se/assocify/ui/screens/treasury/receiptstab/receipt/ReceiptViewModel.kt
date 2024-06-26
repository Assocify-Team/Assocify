package com.github.se.assocify.ui.screens.treasury.receiptstab.receipt

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.model.entities.MaybeRemotePhoto
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.util.DateUtil
import com.github.se.assocify.ui.util.PriceUtil
import com.github.se.assocify.ui.util.SnackbarSystem
import java.time.LocalDate
import java.util.UUID
import kotlin.math.absoluteValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReceiptViewModel {

  private val NEW_RECEIPT_TITLE = "New Receipt"
  private val EDIT_RECEIPT_TITLE = "Edit Receipt"

  private val receiptApi: ReceiptAPI
  private val navActions: NavigationActions
  private val receiptUid: String
  private var receiptCreatorUid: String

  private val snackbarSystem: SnackbarSystem

  private val _uiState: MutableStateFlow<ReceiptState>
  val uiState: StateFlow<ReceiptState>

  constructor(navActions: NavigationActions, receiptApi: ReceiptAPI) {
    this.navActions = navActions
    this.receiptApi = receiptApi
    this.receiptUid = UUID.randomUUID().toString()
    this.receiptCreatorUid = CurrentUser.userUid!!
    _uiState = MutableStateFlow(ReceiptState(isNewReceipt = true, pageTitle = NEW_RECEIPT_TITLE))
    uiState = _uiState
    snackbarSystem = SnackbarSystem(_uiState.value.snackbarHostState)
  }

  constructor(receiptUid: String, navActions: NavigationActions, receiptApi: ReceiptAPI) {
    this.navActions = navActions
    this.receiptApi = receiptApi
    this.receiptUid = receiptUid
    // Temporary. Gets overridden once the receipt is fetched
    this.receiptCreatorUid = CurrentUser.userUid!!
    _uiState = MutableStateFlow(ReceiptState(isNewReceipt = false, pageTitle = EDIT_RECEIPT_TITLE))
    uiState = _uiState
    snackbarSystem = SnackbarSystem(_uiState.value.snackbarHostState)
    loadReceipt()
  }

  /**
   * Load receipt from database If successful, update the UI state with the receipt data If failed,
   * update the UI state with an error message
   */
  fun loadReceipt() {
    _uiState.value = _uiState.value.copy(loading = true, error = null)
    this.receiptApi.getReceipt(
        receiptUid,
        onSuccess = { receipt ->
          _uiState.value =
              _uiState.value.copy(
                  status = receipt.status,
                  title = receipt.title,
                  description = receipt.description,
                  amount = PriceUtil.fromCents(receipt.cents.absoluteValue),
                  date = DateUtil.formatDate(receipt.date),
                  incoming = receipt.cents >= 0)

          _uiState.value = _uiState.value.copy(loading = false, error = null)
          this.receiptCreatorUid = receipt.userId
          loadImage()
        },
        onFailure = {
          _uiState.value = _uiState.value.copy(loading = false, error = "Error loading receipt")
        })
  }

  /**
   * Load receipt image from database If successful, update the UI state with the image URI If
   * failed, update the UI state with an error message
   */
  fun loadImage() {
    _uiState.value = _uiState.value.copy(imageLoading = true, imageError = null)
    receiptApi.getReceiptImage(
        receiptUid,
        {
          _uiState.value = _uiState.value.copy(receiptImageURI = it)
          _uiState.value = _uiState.value.copy(imageLoading = false, imageError = null)
        },
        {
          _uiState.value =
              _uiState.value.copy(imageLoading = false, imageError = "Error loading image")
        })
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
    _uiState.value = _uiState.value.copy(date = DateUtil.formatDate(date))
    if (date == null) {
      _uiState.value = _uiState.value.copy(dateError = "Date cannot be empty")
    } else {
      _uiState.value = _uiState.value.copy(dateError = null)
    }
  }

  fun setIncoming(incoming: Boolean) {
    _uiState.value = _uiState.value.copy(incoming = incoming)
  }

  fun showBottomSheet() {
    _uiState.value = _uiState.value.copy(showBottomSheet = true)
  }

  fun hideBottomSheet() {
    _uiState.value = _uiState.value.copy(showBottomSheet = false)
  }

  fun setImage(uri: Uri?) {
    if (uri == null) return
    _uiState.value = _uiState.value.copy(receiptImageURI = uri)
  }

  fun signalCameraPermissionDenied() {
    snackbarSystem.showSnackbar("Camera permission denied")
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

    if (_uiState.value.receiptImageURI == null) {
      snackbarSystem.showSnackbar("Receipt image is required")
      return
    }

    val date = DateUtil.toDate(_uiState.value.date) ?: return

    val receipt =
        Receipt(
            uid = receiptUid,
            title = _uiState.value.title,
            description = _uiState.value.description,
            cents =
                PriceUtil.toCents(_uiState.value.amount) * (if (_uiState.value.incoming) 1 else -1),
            date = date,
            status = _uiState.value.status,
            photo = MaybeRemotePhoto.LocalFile(_uiState.value.receiptImageURI!!),
            userId = receiptCreatorUid)
    receiptApi.uploadReceipt(
        receipt,
        onPhotoUploadSuccess = {},
        onReceiptUploadSuccess = { navActions.back() },
        onFailure = { receiptFail, _ ->
          if (receiptFail) {
            snackbarSystem.showSnackbar("Failed to save receipt", "Retry", { saveReceipt() })
          } else {
            snackbarSystem.showSnackbar("Failed to save image", "Retry", { saveReceipt() })
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
            snackbarSystem.showSnackbar("Failed to delete receipt", "Retry", { deleteReceipt() })
          })
    }
  }

  fun back() {
    navActions.back()
  }
}

data class ReceiptState(
    val loading: Boolean = false,
    val error: String? = null,
    val imageLoading: Boolean = false,
    val imageError: String? = null,
    val isNewReceipt: Boolean,
    val status: Status = Status.Pending,
    val pageTitle: String,
    val title: String = "",
    val description: String = "",
    val amount: String = "",
    val date: String = "",
    val incoming: Boolean = false,
    val titleError: String? = null,
    val amountError: String? = null,
    val dateError: String? = null,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val showBottomSheet: Boolean = false,
    val receiptImageURI: Uri? = null
)
