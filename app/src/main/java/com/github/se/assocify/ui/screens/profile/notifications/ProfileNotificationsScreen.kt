package com.github.se.assocify.ui.screens.profile.notifications

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
import com.github.se.assocify.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileNotificationsScreen(navActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("Notifications Screen"),
      topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Notifications Settings") },
            navigationIcon = {
              IconButton(onClick = { navActions.back() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Arrow Back")
              }
            })
      }) {
        Text(modifier = Modifier.padding(it), text = "Notifications Screen : not yet implemented")
      }
}
