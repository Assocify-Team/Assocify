package com.github.se.assocify.ui.screens.profile.security_privacy

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.MainNavigationBar

@Composable
fun ProfileSecurity_PrivacyScreen(navActions: NavigationActions) {
    Scaffold(
        modifier = Modifier.testTag("security/privacy Screen"),
        bottomBar = {
            MainNavigationBar(
                onTabSelect = { navActions.navigateToMainTab(it) },
                tabList = MAIN_TABS_LIST,
                selectedTab = Destination.Profile)
        }) {
        Text(modifier = Modifier.padding(it), text = "Security/Privacy Screen : not yet implemented")
    }
}