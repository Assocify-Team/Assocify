package com.github.se.assocify.ui.screens.treasury.accounting.balance

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.ui.screens.treasury.accounting.budget.BudgetItemState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

class BalanceDetailedViewModel(
    private var balancetApi: BalanceAPI,
    private var subCategoryUid: String
) : ViewModel() {
    private val _uiState: MutableStateFlow<BalanceItemState> = MutableStateFlow(BalanceItemState())
    val uiState: StateFlow<BalanceItemState>

    init {
        uiState = _uiState
    }
}

data class BalanceItemState(
    val balanceList: List<BalanceItem> = emptyList(),
    val yearFilter: Int = LocalDate.now().year
)