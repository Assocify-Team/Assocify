package com.github.se.assocify.ui.screens.treasury

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.entities.Receipt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReceiptViewmodel : ViewModel() {
    private val _uiState = MutableStateFlow(ReceiptViewmodel())
    val uiState: StateFlow<ReceiptViewmodel> = _uiState
}

data class ReceiptUIState(
    val receipts: List<Receipt> = listOf(),
    val searchQuery: String = "",
)