package com.github.se.assocify.model.interfaces

import com.github.se.assocify.model.entities.Budget
import com.github.se.assocify.model.entities.BudgetItem

/** Interface for the Budget API */
interface BudgetAPIInterface {
  // get
  fun getBudgets(associationUID: String): List<Budget>

  fun getBudgetItem(associationUID: String, budgetUID: String): BudgetItem
  // add
  fun addBudgetItem(associationUID: String, budgetItem: BudgetItem)

  fun addBudget(associationUID: String, budget: Budget)

  // update
  fun updateBudgetItem(associationUID: String, budgetItem: BudgetItem)

  fun updateBudget(associationUID: String, budget: Budget)

  // delete
  fun deleteBudgetItem(budgetItemUID: String)

  fun deleteBudget(budgetUID: String)
}
