package com.github.se.assocify.ui.screens.selectAssociation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import kotlin.math.min

/**
 * Screen to select an association
 *
 * @param navActions the navigation actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAssociationScreen(navActions: NavigationActions, viewModel: SelectAssociationViewModel) {
  val state = viewModel.uiState.collectAsState()
  var query by remember { mutableStateOf("") }

  Scaffold(
      modifier = Modifier.testTag("SelectAssociationScreen"),
      topBar = {
        CenterAlignedTopAppBar(
            title = {
              Text(
                  modifier = Modifier.testTag("HelloText"),
                  text = "Hello " + state.value.user.name + " !!",
                  style = MaterialTheme.typography.headlineSmall)
            },
            navigationIcon = {
              if (navActions.backFromSelectAsso()) {
                IconButton(
                    onClick = { navActions.back() }, modifier = Modifier.testTag("GoBackButton")) {
                      Icon(
                          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                          contentDescription = "Back arrow",
                      )
                    }
              }
            })
      },
      bottomBar = {
        Button(
            onClick = { navActions.navigateTo(Destination.CreateAsso) },
            modifier =
                Modifier.fillMaxWidth().padding(16.dp).testTag("CreateNewOrganizationButton"),
            content = { Text(text = "Create new association") },
            colors =
                ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary))
      },
      contentWindowInsets = WindowInsets(20.dp, 10.dp, 20.dp, 20.dp)) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
          SearchBar(
              modifier = Modifier.testTag("SearchOrganization"),
              query = query,
              onQueryChange = { query = it },
              onSearch = { viewModel.updateSearchQuery(query, true) },
              onActiveChange = {},
              active = state.value.searchState,
              placeholder = { Text(text = "Search an association") },
              trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier =
                        Modifier.clickable(
                            onClick = {
                              viewModel.updateSearchQuery("", false)
                              query = ""
                            }))
              },
              leadingIcon = {
                if (state.value.searchState) {
                  Icon(
                      imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                      contentDescription = null,
                      modifier =
                          Modifier.clickable(
                                  onClick = {
                                    viewModel.updateSearchQuery("", false)
                                    query = ""
                                  })
                              .testTag("ArrowBackButton"))
                } else {
                  Icon(
                      imageVector = Icons.Default.Search,
                      contentDescription = null,
                      modifier =
                          Modifier.clickable(onClick = { viewModel.updateSearchQuery(query, true) })
                              .testTag("SOB"))
                }
              }) {
                if (state.value.searchState) {
                  val filteredAssos =
                      state.value.associations.filter { ass ->
                        val min = min(ass.name.length, state.value.searchQuery.length)
                        ass.name.take(min).lowercase() ==
                            state.value.searchQuery.take(min).lowercase()
                      }
                  filteredAssos.map { ass -> DisplayOrganization(ass, viewModel) }
                } else {
                  state.value.associations
                }
              }

          LazyColumn(
              //            contentPadding = innerPadding,
              verticalArrangement = Arrangement.spacedBy(4.dp),
              modifier =
                  Modifier.fillMaxSize().fillMaxWidth().padding(16.dp).testTag("RegisteredList")) {
                // Display only registered organization
                val registeredAssociation = state.value.associations
                if (registeredAssociation.isEmpty()) {
                  item { Text(text = "There are no associations to display.") }
                } else {
                  itemsIndexed(registeredAssociation) { index, organization ->
                    DisplayOrganization(organization, viewModel)
                    // Add a Divider for each organization except the last one
                    if (index < registeredAssociation.size - 1) {
                      HorizontalDivider(Modifier.fillMaxWidth().padding(8.dp))
                    }
                  }
                }
              }
        }
      }
}

/**
 * Display an organization on the select association screen
 *
 * @param organization the name of the organization
 */
@Composable
fun DisplayOrganization(organization: Association, viewModel: SelectAssociationViewModel) {
  ListItem(
      headlineContent = {
        Text(
            text = organization.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("OrganizationName"))
      },
      leadingContent = {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Organization Icon",
            modifier = Modifier.testTag("OrganizationIcon"))
      },
      trailingContent = {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Select Icon",
            modifier = Modifier.testTag("SelectIcon"))
      },
      modifier =
          Modifier.clickable { viewModel.selectAssoc(organization.uid) }
              .testTag("DisplayOrganizationScreen-${organization.uid}"))
}
