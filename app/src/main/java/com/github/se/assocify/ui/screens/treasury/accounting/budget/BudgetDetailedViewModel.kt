package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BudgetItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The view model for the budget screen
 *
 * @param budgetApi the budget api
 */
class BudgetDetailedViewModel(private var budgetApi: BudgetAPI) : ViewModel() {
  private val _uiState: MutableStateFlow<BudgetItemState> = MutableStateFlow(BudgetItemState())
  val uiState: StateFlow<BudgetItemState>

  init {
    uiState = _uiState
    updateDatabaseValues()
  }

  private fun updateDatabaseValues() {
    budgetApi.getBudget(
        CurrentUser.associationUid!!,
        { budgetList -> _uiState.value = _uiState.value.copy(budgetList = budgetList) },
        {})
  }

  /**
   * Gets the list of budgetItems of a given subCategory
   *
   * @param accountingSubCategory the subCategory to filter on
   * @return the list of budgetItems of the given subCategory
   */
  fun getBudgetItems(accountingSubCategory: AccountingSubCategory): List<BudgetItem> {
    return uiState.value.budgetList.filter { item -> item.category == accountingSubCategory }
  }

  // TODO: handle filter

  // TODO: handle accoutingSubCategory

}

data class BudgetItemState(val budgetList: List<BudgetItem> = emptyList())
