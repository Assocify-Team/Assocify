package com.github.se.assocify.navigation

import androidx.navigation.NavGraphBuilder
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AccountingSubCategoryAPI
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.BalanceAPI
import com.github.se.assocify.model.database.BudgetAPI
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.ReceiptAPI
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.ui.screens.createAssociation.createAssociationGraph
import com.github.se.assocify.ui.screens.event.eventGraph
import com.github.se.assocify.ui.screens.login.loginGraph
import com.github.se.assocify.ui.screens.profile.profileGraph
import com.github.se.assocify.ui.screens.selectAssociation.selectAssociationGraph
import com.github.se.assocify.ui.screens.treasury.treasuryGraph

fun NavGraphBuilder.mainNavGraph(
    navActions: NavigationActions,
    userAPI: UserAPI,
    associationAPI: AssociationAPI,
    eventAPI: EventAPI,
    budgetAPI: BudgetAPI,
    balanceAPI: BalanceAPI,
    taskAPI: TaskAPI,
    receiptsAPI: ReceiptAPI,
    accountingCategoriesAPI: AccountingCategoryAPI,
    accountingSubCategoryAPI: AccountingSubCategoryAPI
) {
  treasuryGraph(
      navActions,
      budgetAPI,
      balanceAPI,
      receiptsAPI,
      accountingCategoriesAPI,
      accountingSubCategoryAPI)
  eventGraph(navActions, eventAPI, taskAPI)
  profileGraph(navActions, userAPI, associationAPI, accountingCategoriesAPI, eventAPI)
  loginGraph(navActions, userAPI)
  selectAssociationGraph(navActions, userAPI, associationAPI)
  createAssociationGraph(navActions, userAPI, associationAPI)
}
