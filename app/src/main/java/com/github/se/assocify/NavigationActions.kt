package com.github.se.assocify

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class NavigationActions {

}

sealed class Screen(val route: String, @StringRes val labelId: Int, @DrawableRes val iconId: Int? = null) {
    data object Home : Screen("home", R.string.home_tab_label, R.drawable.home_tab_icon)
    data object Treasury : Screen("treasury", R.string.treasury_tab_label, R.drawable.treasury_tab_icon)
    data object Event : Screen("event", R.string.event_tab_label, R.drawable.event_tab_icon)
    data object Chat : Screen("chat", R.string.chat_tab_label, R.drawable.chat_tab_icon)
    data object Profile : Screen("profile", R.string.profile_tab_label, R.drawable.profile_tab_icon)

    data object Login : Screen("login", R.string.login_page_label, null)
    data object ChooseAssoc : Screen("chooseAssoc", R.string.choose_assoc_page_label, null)
    data object CreateAssoc : Screen("createAssoc", R.string.create_assoc_page_label, null)
}

val MAIN_TABS_LIST = listOf(
    Screen.Home,
    Screen.Treasury,
    Screen.Event,
    Screen.Chat,
    Screen.Profile
)

