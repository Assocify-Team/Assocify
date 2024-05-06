package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.BudgetItem
import io.github.jan.supabase.SupabaseClient

class BudgetAPI(val db: SupabaseClient) : SupabaseApi() {
  /**
   * Get the budget of an association
   *
   * @param associationUID the unique identifier of the association
   * @param onSuccess the callback to be called when the budget items are retrieved
   * @param onFailure the callback to be called when the budget items could not be retrieved
   */
  fun getBudget(
      associationUID: String,
      onSuccess: (List<BudgetItem>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // TODO("Not yet implemented")
  }

  /**
   * Add the budget items of an association
   *
   * @param associationUID the unique identifier of the association
   */
  fun addBudgetItem(
      associationUID: String,
      budgetItem: BudgetItem,
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
   * @param onSuccess the callback to be called when the budget item is updated
   * @param onFailure the callback to be called when the budget item could not be updated
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
   * Delete a budget item
   *
   * @param budgetItemUID the unique identifier of the budget item
   * @param onSuccess the callback to be called when the budget item is deleted
   * @param onFailure the callback to be called when the budget item could not be deleted
   */
  fun deleteBudgetItem(
      budgetItemUID: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // TODO("Not yet implemented")
  }
}
