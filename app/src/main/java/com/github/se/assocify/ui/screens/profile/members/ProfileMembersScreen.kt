package com.github.se.assocify.ui.screens.profile.members

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
fun ProfileMembersScreen(navActions: NavigationActions) {
    Scaffold(
        modifier = Modifier.testTag("Members Screen"),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Members Management") },
                navigationIcon = {
                    IconButton(
                        onClick = { navActions.back() }, modifier = Modifier.testTag("backButton")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Arrow Back")
                    }
                })
        }) {
        Text(modifier = Modifier.padding(it), text = "Members Screen : not yet implemented")
    }
}
