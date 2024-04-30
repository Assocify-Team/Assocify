package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Budget
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.interfaces.BudgetAPIInterface
import io.github.jan.supabase.SupabaseClient

class BudgetAPI(val db: SupabaseClient) : BudgetAPIInterface, SupabaseApi() {
  /**
   * Get the budgets of an association
   *
   * @param associationUID the unique identifier of the association
   * @return the list of budgets of the association
   */
  override fun getBudgets(
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
  override fun addBudgetItem(
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
  override fun addBudget(
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
  override fun updateBudgetItem(
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
  override fun updateBudget(
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
  override fun deleteBudgetItem(
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
  override fun deleteBudget(
      budgetUID: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // TODO("Not yet implemented")
  }
}
