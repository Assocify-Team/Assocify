package com.github.se.assocify.ui.screens.treasury.accounting.balance

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.Status
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
        updateDatabaseValues()
        uiState = _uiState
    }

    private fun updateDatabaseValues(){
        balancetApi.getBalance(
            CurrentUser.associationUid!!,
            { balanceList ->
                // Filter the balanceList to only include items with the matching subCategoryUid
                val filteredList =
                    balanceList.filter {
                        balanceItem ->
                            balanceItem.year == _uiState.value.year
                            && balanceItem.subCategory.uid == subCategoryUid
                            && balanceItem.status == _uiState.value.status
                    }

                // Update the UI state with the filtered list
                _uiState.value = _uiState.value.copy(balanceList = filteredList)
            },
            {})
    }

    fun onYearFilter(year: Int){
        _uiState.value = _uiState.value.copy(year = year)
        updateDatabaseValues()
    }

    fun onStatusFilter(status: Status){
        _uiState.value = _uiState.value.copy(status = status)
        updateDatabaseValues()
    }

}

data class BalanceItemState(
    val balanceList: List<BalanceItem> = emptyList(),
    val status: Status = Status.Pending,
    val year: Int = LocalDate.now().year
)