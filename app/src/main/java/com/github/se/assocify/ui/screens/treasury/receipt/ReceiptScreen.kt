package com.github.se.assocify.ui.screens.treasury.receipt

import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.assocify.ui.composables.DatePickerWithDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen() {
  Scaffold(
      modifier = Modifier.testTag("receiptScreen"),
      topBar = {
        MediumTopAppBar(
            title = { Text("New Receipt") },
            navigationIcon = {
              IconButton(onClick = { /*TODO*/}) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
              }
            })
      },
  ) {
    LazyColumn(modifier = Modifier.padding(it)) {
      item {
        OutlinedTextField(value = "", onValueChange = { /*TODO*/}, label = { Text("Title") })
        OutlinedTextField(
            value = "", onValueChange = { /*TODO*/}, label = { Text("Description") }, minLines = 3)
        DatePickerWithDialog(dateText = "", onDateSelected = { /*TODO*/})
      }
    }
  }
}
