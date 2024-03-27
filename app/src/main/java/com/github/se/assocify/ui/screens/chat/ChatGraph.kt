package com.github.se.assocify.ui.screens.chat

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

fun NavGraphBuilder.chatGraph(navigationActions: NavigationActions) {
  composable(
      route = Destination.Chat.route,
  ) {
    ChatScreen(navigationActions)
  }
}
