package com.github.se.assocify.ui.screens.profile

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.localsave.LocalSave
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.profile.events.profileEventsGraph
import com.github.se.assocify.ui.screens.profile.members.profileMembersGraph
import com.github.se.assocify.ui.screens.profile.preferences.profilePreferencesGraph
import com.github.se.assocify.ui.screens.profile.treasuryTags.profileTreasuryTagsGraph
import com.github.se.assocify.ui.theme.ThemeViewModel

fun NavGraphBuilder.profileGraph(
    navigationActions: NavigationActions,
    userAPI: UserAPI,
    associationAPI: AssociationAPI,
    accountingCategoryAPI: AccountingCategoryAPI,
    eventAPI: EventAPI,
    appThemeVM: ThemeViewModel,
    localSave: LocalSave
) {
  composable(
      route = Destination.Profile.route,
  ) {
    val profileViewModel = remember { ProfileViewModel(associationAPI, userAPI, navigationActions) }
    ProfileScreen(navigationActions, profileViewModel)
  }

  profilePreferencesGraph(navigationActions, appThemeVM, localSave)
  profileMembersGraph(navigationActions, associationAPI, userAPI)
  profileTreasuryTagsGraph(navigationActions, accountingCategoryAPI)
  profileEventsGraph(navigationActions, eventAPI)
}
