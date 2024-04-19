package com.github.se.assocify.ui.screens.treasury.accounting

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Budget() {
    //filter box
    //list of budget lines
    //last line must be total
    //add budget line button
    //total
    //clickable budget

}

@Composable
fun DisplayBudgetLine(){
    ListItem(
        headlineContent = { /*title*/},
        trailingContent = { /*total amount*/},
        modifier = Modifier.clickable{ /*TODO*/}
    )

}