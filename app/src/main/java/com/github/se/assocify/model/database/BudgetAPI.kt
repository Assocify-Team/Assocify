package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Budget
import com.github.se.assocify.model.entities.BudgetItem
import io.github.jan.supabase.SupabaseClient

class BudgetAPI(val db: SupabaseClient) : SupabaseApi() {
  /**
   * Get the budgets of an association
   *
   * @param associationUID the unique identifier of the association
   * @return the list of budgets of the association
   */
  fun getBudgets(
      associationUID: String,
      onSuccess: (List<Budget>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // TODO("Not yet implemented")
  }
  /**
   * Add a budget item
   *
   * @param budgetUID the unique identifier of the association
   * @param budgetItem the budget item to add
   */
  fun addBudgetItem(
      budgetUID: String,
      budgetItem: BudgetItem,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // TODO("Not yet implemented")
  }
  /**
   * Add a budget
   *
   * @param associationUID the unique identifier of the association
   * @param budget the budget to add
   */
  fun addBudget(
      associationUID: String,
      budget: Budget,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // TODO("Not yet implemented")
  }
  /**
   * Update a budget item
   *
   * @param associationUID the unique identifier of the association
   * @param budgetItem the budget item to update
   */
  fun updateBudgetItem(
      associationUID: String,
      budgetItem: BudgetItem,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // TODO("Not yet implemented")
  }
  /**
   * Update a budget
   *
   * @param associationUID the unique identifier of the association
   * @param budget the budget to update
   */
  fun updateBudget(
      associationUID: String,
      budget: Budget,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // TODO("Not yet implemented")
  }
  /**
   * Delete a budget item
   *
   * @param budgetItemUID the unique identifier of the budget item
   */
  fun deleteBudgetItem(
      budgetItemUID: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // TODO("Not yet implemented")
  }
  /**
   * Delete a budget
   *
   * @param budgetUID the unique identifier of the budget
   */
  fun deleteBudget(budgetUID: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    // TODO("Not yet implemented")
  }
}
