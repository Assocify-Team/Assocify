package com.github.se.assocify.ui.screens.profile.treasuryTags

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.profileTreasuryTagsGraph(navigationActions: NavigationActions, associationAPI: AssociationAPI, userAPI: UserAPI, accountingCategoryAPI: AccountingCategoryAPI) {
  composable(route = Destination.ProfileTreasuryTags.route) {
    ProfileTreasuryTagsScreen(navigationActions, ProfileTreasuryTagsViewModel(associationAPI, userAPI, accountingCategoryAPI, navigationActions))
  }
}
