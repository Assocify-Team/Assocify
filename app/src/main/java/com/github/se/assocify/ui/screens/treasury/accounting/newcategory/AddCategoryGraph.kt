package com.github.se.assocify.ui.screens.treasury.accounting.newcategory

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.addAccountingCategory(navigationActions: NavigationActions) {
  composable(route = Destination.NewBalanceCategory.route) {
    //AddAccountingSubCategory(navigationActions)
    AddCategoryPopUp()
  }
}
