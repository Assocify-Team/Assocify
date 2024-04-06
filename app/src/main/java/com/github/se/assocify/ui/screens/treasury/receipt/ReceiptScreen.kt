package com.github.se.assocify.ui.screens.treasury.receipt

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.DatePickerWithDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(navActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("receiptScreen"),
      topBar = {
        MediumTopAppBar(
            title = { Text("New Receipt") },
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("backButton"), onClick = { navActions.back() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                  }
            })
      },
  ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = it,
        horizontalAlignment = Alignment.CenterHorizontally) {
          item {
            OutlinedTextField(
                modifier = Modifier.testTag("titleField"),
                value = "",
                onValueChange = { /*TODO*/},
                label = { Text("Title") })
            OutlinedTextField(
                modifier = Modifier.testTag("descriptionField"),
                value = "",
                onValueChange = { /*TODO*/},
                label = { Text("Description") },
                minLines = 3)
            DatePickerWithDialog(
                modifier = Modifier.testTag("dateField"),
                label = { Text("Date") },
                dateValue = "",
                onDateSelected = { /*TODO*/})
          }
        }
  }
}
