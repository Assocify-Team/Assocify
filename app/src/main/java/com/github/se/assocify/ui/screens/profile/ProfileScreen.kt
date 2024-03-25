package com.github.se.assocify.ui.screens.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.MainNavigationBar

@Composable
fun ProfileScreen(navActions: NavigationActions) {
    Scaffold (
        bottomBar = {
            MainNavigationBar(
                onTabSelect = { navActions.navigateToMainTab(it) },
                tabList = MAIN_TABS_LIST,
                selectedTab = Destination.Profile
            )
        }
    ) {
        Text(
            modifier = Modifier.padding(it),
            text = "Profile Screen")
    }
}