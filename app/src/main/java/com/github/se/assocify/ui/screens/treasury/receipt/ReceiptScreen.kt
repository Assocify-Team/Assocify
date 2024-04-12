package com.github.se.assocify.ui.screens.treasury.receipt

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.se.assocify.R
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.DatePickerWithDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(
    navActions: NavigationActions,
    currentUser: CurrentUser,
    viewModel: ReceiptViewModel = ReceiptViewModel(navActions, currentUser)
) {

  val receiptState by viewModel.uiState.collectAsState()

  Scaffold(
      modifier = Modifier.testTag("receiptScreen"),
      topBar = {
        TopAppBar(
            title = {
              Text(modifier = Modifier.testTag("receiptScreenTitle"), text = receiptState.pageTitle)
            },
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("backButton"), onClick = { navActions.back() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                  }
            })
      },
      contentWindowInsets = WindowInsets(50.dp, 20.dp, 50.dp, 0.dp),
      snackbarHost = {
        SnackbarHost(
            hostState = receiptState.snackbarHostState,
            snackbar = { snackbarData -> Snackbar(snackbarData = snackbarData) })
      }) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              OutlinedTextField(
                  modifier = Modifier.testTag("titleField").fillMaxWidth(),
                  value = receiptState.title,
                  onValueChange = { viewModel.setTitle(it) },
                  label = { Text("Title") },
                  isError = receiptState.titleError != null,
                  supportingText = { receiptState.titleError?.let { Text(it) } })
              OutlinedTextField(
                  modifier = Modifier.testTag("descriptionField").fillMaxWidth(),
                  value = receiptState.description,
                  onValueChange = { viewModel.setDescription(it) },
                  label = { Text("Description") },
                  minLines = 3,
                  supportingText = {})
              OutlinedTextField(
                  modifier = Modifier.testTag("amountField").fillMaxWidth(),
                  value = receiptState.amount,
                  onValueChange = { viewModel.setAmount(it) },
                  label = { Text("Amount") },
                  keyboardOptions =
                      KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                  isError = receiptState.amountError != null,
                  supportingText = { receiptState.amountError?.let { Text(it) } })
              DatePickerWithDialog(
                  modifier = Modifier.testTag("dateField").fillMaxWidth(),
                  value = receiptState.date,
                  onDateSelect = { viewModel.setDate(it) },
                  label = { Text("Date") },
                  isError = receiptState.dateError != null,
                  supportingText = { receiptState.dateError?.let { Text(it) } })
              Card(
                  modifier =
                      Modifier.testTag("imageCard")
                          .fillMaxWidth()
                          .aspectRatio(1f)
                          .padding(top = 15.dp, bottom = 5.dp)) {
                    Box(modifier = Modifier.fillMaxSize()) {
                      Image(
                          modifier = Modifier.align(Alignment.Center),
                          painter =
                              painterResource(
                                  id = R.drawable.fake_receipt), /*TODO: Implement image loading*/
                          contentDescription = "Receipt")
                      FilledIconButton(
                          modifier =
                              Modifier.testTag("editImageButton")
                                  .align(Alignment.BottomEnd)
                                  .padding(10.dp),
                          onClick = { viewModel.setImage() },
                      ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                      }
                    }
                  }
              Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    modifier = Modifier.testTag("expenseChip"),
                    selected = !receiptState.incoming,
                    onClick = { viewModel.setIncoming(false) },
                    label = { Text("Expense") },
                    leadingIcon = {
                      if (!receiptState.incoming) {
                        Icon(Icons.Filled.Check, contentDescription = "Selected")
                      }
                    })
                FilterChip(
                    modifier = Modifier.testTag("earningChip"),
                    selected = receiptState.incoming,
                    onClick = { viewModel.setIncoming(true) },
                    label = { Text("Earning") },
                    leadingIcon = {
                      if (receiptState.incoming) {
                        Icon(Icons.Filled.Check, contentDescription = "Selected")
                      }
                    })
              }

              Column {
                Button(
                    modifier = Modifier.testTag("saveButton").fillMaxWidth(),
                    onClick = { viewModel.saveReceipt() },
                    content = { Text("Save") })
                OutlinedButton(
                    modifier = Modifier.testTag("deleteButton").fillMaxWidth(),
                    onClick = { viewModel.deleteReceipt() },
                    content = {
                      Text(
                          if (receiptState.isNewReceipt) {
                            "Cancel"
                          } else {
                            "Delete"
                          })
                    },
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error))
              }

              Spacer(modifier = Modifier.weight(1.0f))
            }
      }
}
