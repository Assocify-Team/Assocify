package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.TVA
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The view model for the budget screen
 *
 * @param budgetApi the budget api
 */
class BudgetDetailedViewModel(private var budgetApi: BudgetAPI, private var subCategoryUid: String) : ViewModel() {
  private val _uiState: MutableStateFlow<BudgetItemState> = MutableStateFlow(BudgetItemState())
  val uiState: StateFlow<BudgetItemState>

  init {
    uiState = _uiState
    updateDatabaseValues()
  }

  private fun updateDatabaseValues() {
   budgetApi.getBudget(
      CurrentUser.associationUid!!,
      { budgetList ->
        // Filter the budgetList to only include items with the matching subCategoryUid
        val filteredList = budgetList.filter { budgetItem ->
          budgetItem.category.uid == subCategoryUid
        }
        // Update the UI state with the filtered list
        _uiState.value = _uiState.value.copy(budgetList = budgetList)


      },
      {}
    )
      println(_uiState.value.budgetList + "TAMERE")
      println("BOUGE")
  }

  // TODO: handle filter

  // TODO: handle accoutingSubCategory

}

data class BudgetItemState(val budgetList: List<BudgetItem> = emptyList())
