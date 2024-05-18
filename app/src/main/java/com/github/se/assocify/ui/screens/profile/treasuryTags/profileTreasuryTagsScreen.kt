package com.github.se.assocify.ui.screens.profile.treasuryTags

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTreasuryTagsScreen(navActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("TreasuryTags Screen"),
      topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Treasury Tags Management") },
            navigationIcon = {
              BackButton(
                  contentDescription = "Arrow Back",
                  onClick = { navActions.back() },
                  modifier = Modifier.testTag("backButton"))
            })
      }) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Text(modifier = Modifier.padding(it), text = "Treasury Tags Screen : not yet implemented")
        }
      }
}
