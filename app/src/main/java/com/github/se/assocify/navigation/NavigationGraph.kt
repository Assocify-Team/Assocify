package com.github.se.assocify.navigation

import androidx.navigation.NavGraphBuilder
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.ui.screens.createAssoc.createAssoGraph
import com.github.se.assocify.ui.screens.selectAssoc.selectAssoGraph
import com.github.se.assocify.ui.screens.chat.chatGraph
import com.github.se.assocify.ui.screens.event.eventGraph
import com.github.se.assocify.ui.screens.home.homeGraph
import com.github.se.assocify.ui.screens.login.loginGraph
import com.github.se.assocify.ui.screens.profile.profileGraph
import com.github.se.assocify.ui.screens.treasury.treasuryGraph

fun NavGraphBuilder.mainNavGraph(
    navActions: NavigationActions,
    userAPI: UserAPI,
    associationAPI: AssociationAPI
) {
  homeGraph(navActions)
  treasuryGraph(navActions)
  eventGraph(navActions)
  chatGraph(navActions)
  profileGraph(navActions)
  loginGraph(navActions, userAPI, associationAPI)
  selectAssoGraph(navActions, userAPI, associationAPI)
  createAssoGraph(navActions, userAPI, associationAPI)
}
