package com.github.se.assocify.ui.screens.treasury

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.FirebaseApi
import com.github.se.assocify.model.database.ReceiptsAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReceiptViewmodel(
  private val currentUser: CurrentUser, private val receiptsDatabase: ReceiptsAPI =
    ReceiptsAPI(
      userId = currentUser.userUid,
      basePath = "associations/" + currentUser.associationUid,
      storage = Firebase.storage,
      db = Firebase.firestore
    )
) {

  // ViewModel states
  private val _uiState: MutableStateFlow<ReceiptUIState> = MutableStateFlow(ReceiptUIState())
  val uiState: StateFlow<ReceiptUIState>


  // User entity and it's API
  private val userAPI = UserAPI(receiptsDatabase.db)
  private var user: User? = null

  init {
    // Define user entity
    userAPI.getUser(currentUser.userUid,
      onSuccess = { user = it },
      onFailure = { // TODO on sprint 4 with error API
      }
    )
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
      onError = {
        // TODO on sprint 4 with error API
      }
    )
  }

  fun updateAllReceipts() {
    // All receipts should be seen by admins only
    //if (user?.role?.equals("lol") == true)
    receiptsDatabase.getAllReceipts(
      onReceiptsFetched = { receipts ->
        _uiState.value = ReceiptUIState(
          userReceipts = _uiState.value.userReceipts,
          allReceipts = receipts,
          searchQuery = _uiState.value.searchQuery
        )
      },
      onError = { error, exception ->
        // TODO on sprint 4 with error API
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

  init {
    uiState = _uiState
  }
}

data class ReceiptUIState(
  val userReceipts: List<Receipt> = listOf(),
  val allReceipts: List<Receipt> = listOf(),
  val searchQuery: String = "",
)