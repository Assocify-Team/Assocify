package com.github.se.assocify.ui.screens.treasury.accounting.budget

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoriesAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.model.entities.BudgetItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The view model for the budget screen
 *
 * @param accountingCategoryAPI the accounting category api
 * @param accountingSubCategoryAPI the accounting subcategory api
 */
class BudgetViewModel(
    private var accountingCategoryAPI: AccountingCategoriesAPI,
    private var accountingSubCategoryAPI: AccountingSubCategoryAPI
) : ViewModel() {

    private val _uiState: MutableStateFlow<BudgetState> = MutableStateFlow(BudgetState())
    val uiState: StateFlow<BudgetState>

    /**
     * Initialize the view model
     */
    init {
        updateDatabaseValues()
        uiState = _uiState
    }

    /**
     * Function to update the database values
     */
    private fun updateDatabaseValues(){
        // Sets the category list in the state from the database
        accountingCategoryAPI.getCategories(
            CurrentUser.associationUid!!,
            { categoryList ->
                _uiState.value = _uiState.value.copy(categoryList = categoryList)
            },
            {}
        )

        //Sets the subcategory list in the state from the database if a category is selected
        if(_uiState.value.selectedCatUid != "") {
            accountingSubCategoryAPI.getSubCategories(
                _uiState.value.selectedCatUid,
                { subCategoryList ->
                    _uiState.value = _uiState.value.copy(subCategoryList = subCategoryList)
                },
                {}
            )
        }


    }

    /**
     * Function to update the subcategories list when a category is selected
     */
    fun onSelectedCategory(categoryName: String) {
        if(categoryName == "Global"){

        }
        val category = _uiState.value.categoryList.find { it.name == categoryName } ?: return
        _uiState.value = _uiState.value.copy(selectedCatUid = category.uid)
        updateDatabaseValues()
    }


}

/**
 * The state of the budget screen
 * @param categoryList: The list of accounting categories
 * @param selectedCatUid: The selected category unique identifier
 * @param subCategoryList: The list of accounting subcategories
 */
data class BudgetState(
    val categoryList: List<AccountingCategory> = emptyList(),
    val selectedCatUid: String = "",
    val subCategoryList: List<AccountingSubCategory> = emptyList()
)
