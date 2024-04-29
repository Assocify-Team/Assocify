package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Budget
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.interfaces.BudgetAPIInterfaces

class BudgetAPI : BudgetAPIInterfaces {
  /**
   * Get the budgets of an association
   *
   * @param associationUID the unique identifier of the association
   * @return the list of budgets of the association
   */
  override fun getBudgets(associationUID: String): List<Budget> {
    TODO("Not yet implemented")
  }
  /**
   * Get a budget item
   *
   * @param associationUID the unique identifier of the association
   * @param budgetUID the unique identifier of the budget
   * @return the budget item
   */
  override fun getBudgetItem(associationUID: String, budgetUID: String): BudgetItem {
    TODO("Not yet implemented")
  }
  /**
   * Add a budget item
   *
   * @param associationUID the unique identifier of the association
   * @param budgetItem the budget item to add
   */
  override fun addBudgetItem(associationUID: String, budgetItem: BudgetItem) {
    TODO("Not yet implemented")
  }
  /**
   * Add a budget
   *
   * @param associationUID the unique identifier of the association
   * @param budget the budget to add
   */
  override fun addBudget(associationUID: String, budget: Budget) {
    TODO("Not yet implemented")
  }
  /**
   * Update a budget item
   *
   * @param associationUID the unique identifier of the association
   * @param budgetItem the budget item to update
   */
  override fun updateBudgetItem(associationUID: String, budgetItem: BudgetItem) {
    TODO("Not yet implemented")
  }
  /**
   * Update a budget
   *
   * @param associationUID the unique identifier of the association
   * @param budget the budget to update
   */
  override fun updateBudget(associationUID: String, budget: Budget) {
    TODO("Not yet implemented")
  }
  /**
   * Delete a budget item
   *
   * @param budgetItemUID the unique identifier of the budget item
   */
  override fun deleteBudgetItem(budgetItemUID: String) {
    TODO("Not yet implemented")
  }
  /**
   * Delete a budget
   *
   * @param budgetUID the unique identifier of the budget
   */
  override fun deleteBudget(budgetUID: String) {
    TODO("Not yet implemented")
  }
}
