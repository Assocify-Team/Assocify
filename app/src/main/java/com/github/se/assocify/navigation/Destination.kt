package com.github.se.assocify.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.github.se.assocify.R

sealed class Destination(
    val route: String,
    @StringRes val labelId: Int? = null,
    @DrawableRes val iconId: Int? = null
) {
  data object Home : Destination("home", R.string.home_tab_label, R.drawable.home_tab_icon)

  data object Treasury :
      Destination("treasury", R.string.treasury_tab_label, R.drawable.treasury_tab_icon)

  data object Event : Destination("event", R.string.event_tab_label, R.drawable.event_tab_icon)

  data object Chat : Destination("chat", R.string.chat_tab_label, R.drawable.chat_tab_icon)

  data object Profile :
      Destination("profile", R.string.profile_tab_label, R.drawable.profile_tab_icon)

  data object Login : Destination("login/authentication")

  data object SelectAsso : Destination("login/selectAsso")

  data object CreateAsso : Destination("login/createAsso")

  // data class Receipt(val id: String) : Destination("receipt/$id")
}

val MAIN_TABS_LIST =
    listOf(
        Destination.Home,
        Destination.Treasury,
        Destination.Event,
        Destination.Chat,
        Destination.Profile)
