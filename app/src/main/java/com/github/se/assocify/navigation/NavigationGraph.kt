package com.github.se.assocify.navigation

import androidx.navigation.NavGraphBuilder
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.ui.screens.chat.chatGraph
import com.github.se.assocify.ui.screens.event.eventGraph
import com.github.se.assocify.ui.screens.home.homeGraph
import com.github.se.assocify.ui.screens.profile.profileGraph
import com.github.se.assocify.ui.screens.treasury.treasuryGraph

fun NavGraphBuilder.mainNavGraph(navActions: NavigationActions, currentUser: CurrentUser) {
  homeGraph(navActions)
  treasuryGraph(navActions, currentUser)
  eventGraph(navActions)
  chatGraph(navActions)
  profileGraph(navActions)
}
