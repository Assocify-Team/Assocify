package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.entities.BudgetItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The view model for the budget detailed screen
 *
 * @param budgetApi the budget api
 * @param subCategoryUid the subcategory uid
 */
class BudgetDetailedViewModel(
    private var budgetApi: BudgetAPI,
    private var subCategoryUid: String
) : ViewModel() {
  private val _uiState: MutableStateFlow<BudgetItemState> = MutableStateFlow(BudgetItemState())
  val uiState: StateFlow<BudgetItemState>

  init {
    updateDatabaseValues()
    uiState = _uiState
  }

  private fun updateDatabaseValues() {
    budgetApi.getBudget(
        CurrentUser.associationUid!!,
        { budgetList ->
          // Filter the budgetList to only include items with the matching subCategoryUid
          val filteredList =
              budgetList.filter { budgetItem -> budgetItem.year == _uiState.value.yearFilter }

          // TODO: handle TVA filter: HT = amount in budgetITem, TTC = amount in budgetItem + amount
          // * tva
          // TODO: sprint 7

          // Update the UI state with the filtered list
          _uiState.value = _uiState.value.copy(budgetList = filteredList)
        },
        {})
    // Log.d("BudgetList in viewmodel: ", _uiState.value.budgetList.toString())
  }

  fun onYearFilter(yearFilter: Int) {
    _uiState.value = _uiState.value.copy(yearFilter = yearFilter)
    updateDatabaseValues()
  }

  fun startEditing(budgetItem: BudgetItem) {
    _uiState.value = _uiState.value.copy(editing = true)
  }

  fun saveEditing(budgetItem: BudgetItem) {
    /* TODO: add to the database */
    _uiState.value = _uiState.value.copy(editing = false, budgetList = _uiState.value.budgetList.filter { it.uid != budgetItem.uid} + budgetItem)
  }

  fun cancelEditing() {
    _uiState.value = _uiState.value.copy(editing = false)
  }
}

data class BudgetItemState(
    val budgetList: List<BudgetItem> = emptyList(),
    val yearFilter: Int = 2023,
    val editing: Boolean = false,
    val editedBudgetItem: BudgetItem? = null
)
