package com.github.se.assocify.navigation

import androidx.navigation.NavGraphBuilder
import com.github.se.assocify.ui.screens.chat.chatGraph
import com.github.se.assocify.ui.screens.event.eventGraph
import com.github.se.assocify.ui.screens.home.homeGraph
import com.github.se.assocify.ui.screens.profile.profileGraph
import com.github.se.assocify.ui.screens.treasury.treasuryGraph

fun NavGraphBuilder.mainNavGraph(navActions: NavigationActions) {
  homeGraph(navActions)
  treasuryGraph(navActions)
  eventGraph(navActions)
  chatGraph(navActions)
  profileGraph(navActions)
}
