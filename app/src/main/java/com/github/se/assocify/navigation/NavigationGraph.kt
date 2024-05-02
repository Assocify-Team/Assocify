package com.github.se.assocify.navigation

import androidx.navigation.NavGraphBuilder
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.EventAPI
import com.github.se.assocify.model.database.TaskAPI
import com.github.se.assocify.model.database.UserAPI
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
    taskAPI: TaskAPI
) {
  homeGraph(navActions)
  treasuryGraph(navActions)
  eventGraph(navActions, eventAPI, taskAPI)
  chatGraph(navActions)
  profileGraph(navActions, userAPI, associationAPI)
  loginGraph(navActions, userAPI, associationAPI)
  selectAssociationGraph(navActions, userAPI, associationAPI)
  createAssociationGraph(navActions, userAPI, associationAPI)
}
