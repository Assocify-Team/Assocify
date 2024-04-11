package com.github.se.assocify.ui.screens.treasury

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.ReceiptsAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReceiptViewmodel(
  private var user: User,
  private val receiptsDatabase: ReceiptsAPI
) {
  private val _uiState = MutableStateFlow(ReceiptUIState())
  val uiState: StateFlow<ReceiptUIState> = _uiState

  init {
    updateUserReceipts()
    updateAllReceipts()
  }

  fun updateUserReceipts() {
    receiptsDatabase.getUserReceipts(
      onSuccess = { receipts ->
        _uiState.value = ReceiptUIState(
          userReceipts = receipts,
          allReceipts = _uiState.value.allReceipts,
          searchQuery = _uiState.value.searchQuery
        )
      },
      onError = { exception ->
        // What should I do here
      }
    )
  }

  fun updateAllReceipts() {
    // TODO : check perm
    receiptsDatabase.getAllReceipts(
      onReceiptsFetched = { receipts ->
        _uiState.value = ReceiptUIState(
          userReceipts = _uiState.value.userReceipts,
          allReceipts = receipts,
          searchQuery = _uiState.value.searchQuery
        )
      },
      onError = { error, exception ->
        // what to do here
      }
    )
  }

  fun onSearch(): List<Receipt> {
    val filter = _uiState.value.searchQuery.trim()
    return _uiState.value.allReceipts.filter { receipt -> receipt.title.contains(filter, ignoreCase = true)
            || receipt.description.contains(filter, ignoreCase = true)
    }
  }

  fun setSearchQuery(query: String) {
    _uiState.value = ReceiptUIState(
      userReceipts = _uiState.value.userReceipts,
      allReceipts = _uiState.value.allReceipts,
      searchQuery = query
    )
  }
}

data class ReceiptUIState(
  val userReceipts: List<Receipt> = listOf(),
  val allReceipts: List<Receipt> = listOf(),
  val searchQuery: String = "",
)