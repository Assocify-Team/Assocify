package com.github.se.assocify.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAssociation(){
    //lists of organizations
    val allOrganization = listOf("Organization 1", "Organization 2", "Organization 3") //all organization in db
    val registeredOrganization = listOf("Organization 1", "Organization 2") // organizations that the user is registered to
    val filteredOrganization = emptyList<String>() // organizations that matches the search query

    //search bar var
    val isSearching = false
    val searchText = ""

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp) // Add 16dp padding to all sides
            ) {
                /*SearchBar(
                    /*query = "",
                    onQueryChange = (String) -> Unit,
                    onSearch =,
                    active = isSearching,
                    placeholder = { Text(text = "Search an organization") },*/
                ) {

                }*/
            }
        }
    ){
        innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ){
            // Display registered organizations
        }
    }
}