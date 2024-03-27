package com.github.se.assocify.ui.composables

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.github.se.assocify.navigation.Destination

@Composable
fun MainNavigationBar(
    onTabSelect: (Destination) -> Unit,
    tabList: List<Destination>,
    selectedTab: Destination,
) {
    NavigationBar(
        modifier = Modifier.testTag("mainNavBar")
    ) {
        tabList.forEach { tab ->
            NavigationBarItem(
                modifier = Modifier.testTag("mainNavBarItem/${tab.route}"),
                selected = tab == selectedTab,
                onClick = { onTabSelect(tab) },
                label = { Text(if(tab.labelId != null) {
                    stringResource(id =  tab.labelId) } else { "" } ) },
                icon = { if (tab.iconId != null) {
                    Icon(painterResource(tab.iconId), contentDescription = null,
                        modifier = Modifier.testTag("${tab.route}Icon")) } },
            )
        }
    }
}

