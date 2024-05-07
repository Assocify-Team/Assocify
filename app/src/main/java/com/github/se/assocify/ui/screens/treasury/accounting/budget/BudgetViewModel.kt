package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoriesAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.BudgetItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The view model for the budget detailed screen
 *
 * @param budgetApi the budget api
 * @param subCategoryUid the subcategory uid
 */
class BudgetViewModel(
    private var accountingCategoryAPI: AccountingCategoriesAPI,
    private var accountingSubCategoryAPI: AccountingSubCategoryAPI
) : ViewModel() {
    private val _uiState: MutableStateFlow<BudgetState> = MutableStateFlow(BudgetState())
    val uiState: StateFlow<BudgetState>
    init {
        updateDatabaseValues()
        uiState = _uiState
    }

    private fun updateDatabaseValues(){
        accountingCategoryAPI.getCategories(
            CurrentUser.associationUid!!,
            { categoryList ->
                _uiState.value = _uiState.value.copy(categoryList = categoryList)
            },
            {}
        )
    }
}

data class BudgetState(
    val categoryList: List<AccountingCategory> = emptyList()
)
