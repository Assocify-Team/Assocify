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
import com.github.se.assocify.Screen

@Composable
fun MainNavigationBar(
    tabList: List<Screen>,
    selectedTab: Screen,
) {
    NavigationBar(
        modifier = Modifier.testTag("mainNavBar")
    ) {
        tabList.forEach { tab ->
            NavigationBarItem(
                modifier = Modifier.testTag("mainNavBarItem"),
                selected = tab == selectedTab,
                onClick = { /*TODO*/ },
                label = { Text(stringResource(id = tab.labelId)) },
                icon = { if (tab.iconId != null) { Icon(painterResource(tab.iconId), contentDescription = null) } },
            )
        }
    }
}

