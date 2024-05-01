package com.github.se.assocify.navigation

import androidx.navigation.NavGraphBuilder
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.localsave.LoginSave
import com.github.se.assocify.ui.screens.chat.chatGraph
import com.github.se.assocify.ui.screens.createAssociation.createAssociationGraph
import com.github.se.assocify.ui.screens.event.eventGraph
import com.github.se.assocify.ui.screens.home.homeGraph
import com.github.se.assocify.ui.screens.login.loginGraph
import com.github.se.assocify.ui.screens.profile.profileGraph
import com.github.se.assocify.ui.screens.selectAssociation.selectAssociationGraph
import com.github.se.assocify.ui.screens.treasury.treasuryGraph

fun NavGraphBuilder.mainNavGraph(
    navActions: NavigationActions,
    userAPI: UserAPI,
    associationAPI: AssociationAPI,
    eventAPI: EventAPI,
    loginSaver: LoginSave
) {
  homeGraph(navActions)
  treasuryGraph(navActions)
  eventGraph(navActions, eventAPI)
  chatGraph(navActions)
  profileGraph(navActions, userAPI, associationAPI)
  loginGraph(navActions, userAPI, associationAPI, loginSaver)
  selectAssociationGraph(navActions, userAPI, associationAPI, loginSaver)
  createAssociationGraph(navActions, userAPI, associationAPI, loginSaver)
}
