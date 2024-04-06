package com.github.se.assocify.ui.screens.treasury.receipt

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.se.assocify.R
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.DatePickerWithDialog
import com.github.se.assocify.ui.composables.UserSearchTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(navActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("receiptScreen"),
      topBar = {
        TopAppBar(
            title = { Text("New Receipt") },
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("backButton"), onClick = { navActions.back() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                  }
            })
      },
      contentWindowInsets = WindowInsets(60.dp, 20.dp, 60.dp, 0.dp)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it).verticalScroll(ScrollState(0)),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
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
              OutlinedTextField(
                  modifier = Modifier.testTag("amountField"),
                  value = "",
                  onValueChange = { /*TODO*/},
                  label = { Text("Amount") },
                  keyboardOptions =
                      KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal))
              UserSearchTextField(
                  modifier = Modifier.testTag("payerField"),
                  value = "",
                  onUserSelect = { /*TODO*/},
                  label = { Text("Payer") })
              DatePickerWithDialog(
                  modifier = Modifier.testTag("dateField"),
                  value = "",
                  onDateSelect = { /*TODO*/},
                  label = { Text("Date") })
              Card(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                Box(modifier = Modifier.fillMaxSize()) {
                  Image(
                      modifier = Modifier.align(Alignment.Center),
                      painter = painterResource(id = R.drawable.fake_receipt),
                      contentDescription = "Receipt")
                  FilledIconButton(
                      modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp),
                      onClick = { /*TODO*/}) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                      }
                }
              }
              Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = true,
                    onClick = { /*TODO*/},
                    label = { Text("Expense") },
                    leadingIcon = {
                      if (true) {
                        Icon(Icons.Filled.Check, contentDescription = "Selected")
                      }
                    })
                FilterChip(
                    selected = false,
                    onClick = { /*TODO*/},
                    label = { Text("Earning") },
                    leadingIcon = {
                      if (false) {
                        Icon(Icons.Filled.Check, contentDescription = "Selected")
                      }
                    })
              }
              Spacer(modifier = Modifier.weight(1.0f))
              Button(
                  modifier = Modifier.fillMaxWidth().testTag("saveButton"),
                  onClick = { /*TODO*/},
                  content = { Text("Save") })
              OutlinedButton(
                  modifier = Modifier.fillMaxWidth().testTag("deleteButton"),
                  onClick = { /*TODO*/},
                  content = { Text("Delete") },
                  colors =
                      ButtonDefaults.outlinedButtonColors(
                          contentColor = MaterialTheme.colorScheme.error),
                  border = BorderStroke(1.dp, MaterialTheme.colorScheme.error))
              Spacer(modifier = Modifier.weight(1.0f))
            }
      }
}
