package com.github.se.assocify.ui.screens.profile.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.MainNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileThemeScreen(navActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("themeScreen"),
      topBar = {
          CenterAlignedTopAppBar(
              title = { Text("Theme Settings") },
              navigationIcon = {
                  IconButton(onClick = { navActions.back() }) {
                      Icon(
                          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                          contentDescription = "Arrow Back")
                  }
              })
      }) {
        Text(modifier = Modifier.padding(it), text = "Theme Screen : not yet implemented")
      }
}
