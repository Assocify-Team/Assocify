package com.github.se.assocify.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAssociation(){
    //TODO: fetch lists from DB
    //lists of organizations
    val allOrganization = listOf("Organization 1", "Organization 2", "Organization 3") //all organization in db
    val registeredOrganization = listOf("CLIC", "GAME*") // organizations that the user is registered to
    val filteredOrganization = emptyList<String>() // organizations that matches the search query

    //search bar var: TODO: implement search
    val isSearching = false

    Scaffold(
        modifier = Modifier.testTag("SelectAssociationScreen"),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Add 16dp padding to all sides
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp) // Space items vertically by 8.dp
            ) {
                /*TODO: replace user by user's actual name*/
                Text(text = "Hello User", style = MaterialTheme.typography.headlineSmall)
                SearchBar(
                    query = "",
                    onQueryChange = {},
                    onSearch = {},
                    onActiveChange = {},
                    active = isSearching,
                    placeholder = { Text(text = "Search an organization") },
                    trailingIcon = {
                        if (isSearching) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                modifier =
                                Modifier.clickable(
                                    onClick = { /*TODO: clear the search*/ }))
                        }
                    },
                    leadingIcon = {
                        if (isSearching) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                modifier =
                                Modifier.clickable(
                                    onClick = {/*TODO: go back to selectOrganization screen*/ })
                            )
                        }  else {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier =
                                Modifier.clickable(onClick = {/*TODO: search*/}))
                        }
                    }
                ) {
                    // TODO: Display search results (filtered organizations)
                }
            }
        },
        bottomBar = {
            Button(
                onClick = { /*TODO: navigate to go to newAssociation screen*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Add 16dp padding to all sides
                content = { Text(text = "Create new organization") },
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ){
        //Display registered organizations
        innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize().fillMaxWidth().padding(16.dp)
        ){
            if(registeredOrganization.isEmpty()){
                item {
                    Text(text = "You are not registered to any organization.")
                }
            } else {
                itemsIndexed(registeredOrganization) { index, organization ->
                    DisplayOrganization(organization)
                    // Add a Divider for each organization except the last one
                    if (index < registeredOrganization.size - 1) {
                        Divider(
                            Modifier.fillMaxWidth().padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

//TODO: change the parameter to organization object (should have name, logo, etc)
@Composable
fun DisplayOrganization(organization: String){
    //display organization
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp), // Space items horizontally by 8.dp
        verticalAlignment = Alignment.CenterVertically // Align items vertically in the center
    ) {
        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Organization Icon")
        Text(text = organization, style = MaterialTheme.typography.bodyLarge)
    }
}