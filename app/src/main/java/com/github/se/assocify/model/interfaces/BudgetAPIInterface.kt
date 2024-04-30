package com.github.se.assocify.model.interfaces

import com.github.se.assocify.model.entities.Budget
import com.github.se.assocify.model.entities.BudgetItem

/** Interface for the Budget API */
@SuppressWarnings("java:S106")
interface BudgetAPIInterface {
  // get budgets of an Association
  fun getBudgets(
      associationUID: String,
      onSuccess: (List<Budget>) -> Unit,
      onFailure: (Exception) -> Unit
  )
  // add budget item to a budget sheet
  fun addBudgetItem(
      budgetUID: String,
      budgetItem: BudgetItem,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit
  )
  // Add budget to an association
  fun addBudget(
      associationUID: String,
      budget: Budget,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit
  )

  // update a budget item from the
  fun updateBudgetItem(
      associationUID: String,
      budgetItem: BudgetItem,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit
  )

  fun updateBudget(
      associationUID: String,
      budget: Budget,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit
  )

  // delete
  fun deleteBudgetItem(
      budgetItemUID: String,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit
  )

  fun deleteBudget(budgetUID: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit)
}
