package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.entities.BudgetItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The view model for the budget screen
 *
 * @param budgetApi the budget api
 */
class BudgetDetailedViewModel(
   // private var budgetApi: BudgetAPI
) : ViewModel() {

    //TODO: handle filter

    //TODO: handle accoutingSubCategory

    //TODO: getBudgetItems

    //TODO: updateBudgetItems

    //TODO: deleteBudgetItems
}


data class BudgetItemState(
    val budgetList: List<BudgetItem> = emptyList(),
    val associationUId: String =  "assoc",
    val searchQuery: String = "",
    val searchState: Boolean = false
)