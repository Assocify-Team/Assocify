package com.github.se.assocify.ui.screens.profile.securityPrivacy

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
fun ProfileSecurityPrivacyScreen(navActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("security/privacy Screen"),
      topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Security/Privacy Settings") },
            navigationIcon = {
              IconButton(
                  onClick = { navActions.back() }, modifier = Modifier.testTag("backButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Arrow Back")
                  }
            })
      }) {
        Text(
            modifier = Modifier.padding(it), text = "Security/Privacy Screen : not yet implemented")
      }
}
