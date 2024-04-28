package com.github.se.assocify.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/** Main tab top bar */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    title: String,
    optInSearchBar: Boolean,
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    onSearch: (String) -> Unit = {},
    page: Int = 0
) {
  // Search bar state
  var searchBarVisible by remember { mutableStateOf(false) }
  // Page state
  var currentPage by remember { mutableStateOf(page) }

  if (currentPage != page) {
    currentPage = page
    searchBarVisible = false
  }

  if (!searchBarVisible) {
    // Regular top bar
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
          IconButton(
              modifier = Modifier.testTag("accountIconButton"),
              onClick = { /*TODO On assoc account click */}) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Association Account")
              }
        },
        actions = {
          if (optInSearchBar) {
            IconButton(
                modifier = Modifier.testTag("searchIconButton"),
                onClick = {
                  onQueryChange("")
                  onSearch("")
                  searchBarVisible = true
                }) {
                  Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                }
          }
        },
    )
  } else {
    // Search bar
    SearchBar(
        modifier = Modifier.testTag("searchBar").padding(horizontal = 5.dp),
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = false,
        onActiveChange = {},
        placeholder = { Text("Search receipts") },
        leadingIcon = {
          IconButton(
              onClick = {
                onQueryChange("")
                onSearch("")
                searchBarVisible = false
              }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
              }
        },
        trailingIcon = {
          IconButton(
              onClick = {
                onQueryChange("")
                onSearch("")
              }) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Reset Search")
              }
        }) {}
  }
}
