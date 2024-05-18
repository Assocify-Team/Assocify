package com.github.se.assocify.ui.screens.treasury.receiptstab.receipt

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import coil.compose.AsyncImage
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.ui.composables.CenteredCircularIndicator
import com.github.se.assocify.ui.composables.DatePickerWithDialog
import com.github.se.assocify.ui.composables.ErrorMessage
import com.github.se.assocify.ui.composables.PhotoSelectionSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(viewModel: ReceiptViewModel) {

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
                  modifier = Modifier.testTag("backButton"), onClick = { viewModel.back() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                  }
            })
      },
      contentWindowInsets = WindowInsets(40.dp, 20.dp, 40.dp, 0.dp),
      snackbarHost = {
        SnackbarHost(
            hostState = receiptState.snackbarHostState,
            snackbar = { snackbarData -> Snackbar(snackbarData = snackbarData) })
      }) { paddingValues ->
        if (receiptState.loading) {
          CenteredCircularIndicator()
          return@Scaffold
        }

        if (receiptState.error != null) {
          ErrorMessage(errorMessage = receiptState.error) { viewModel.loadReceipt() }
          return@Scaffold
        }

        Column(
            modifier =
                Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Box(
                  modifier =
                      Modifier.testTag("statusDropdownChip")
                          .padding(bottom = 5.dp)
                          .fillMaxWidth()) {
                    var statusExpanded by remember { mutableStateOf(false) }
                    FilterChip(
                        modifier = Modifier.testTag("statusChip"),
                        selected = false,
                        onClick = { statusExpanded = !statusExpanded },
                        label = { Text(receiptState.status.name) },
                        leadingIcon = {
                          Icon(
                              modifier = Modifier.testTag("currentStatusIcon"),
                              imageVector = receiptState.status.getIcon(),
                              contentDescription = "status icon")
                        },
                        trailingIcon = {
                          Icon(
                              imageVector = Icons.Filled.ArrowDropDown,
                              contentDescription = "Expand")
                        })
                    DropdownMenu(
                        modifier = Modifier.testTag("statusDropdownMenu"),
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false },
                        properties = PopupProperties(focusable = true)) {
                          Status.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name) },
                                onClick = {
                                  viewModel.setStatus(status)
                                  statusExpanded = false
                                },
                                leadingIcon = {
                                  Icon(
                                      modifier = Modifier.testTag("statusIcon"),
                                      imageVector = status.getIcon(),
                                      contentDescription = "Status icon")
                                })
                          }
                        }
                  }
              OutlinedTextField(
                  modifier = Modifier.testTag("titleField").fillMaxWidth(),
                  value = receiptState.title,
                  singleLine = true,
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
                  singleLine = true,
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
                      when {
                        receiptState.imageLoading -> {
                          CenteredCircularIndicator()
                        }
                        receiptState.imageError != null -> {
                          ErrorMessage(errorMessage = receiptState.imageError) {
                            viewModel.loadImage()
                          }
                        }
                        receiptState.receiptImageURI == null -> {
                          Image(
                              modifier = Modifier.align(Alignment.Center).size(100.dp),
                              imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                              contentDescription = "receipt icon")
                        }
                        else -> {
                          AsyncImage(
                              model = receiptState.receiptImageURI,
                              modifier = Modifier.align(Alignment.Center),
                              contentDescription = "receipt image",
                          )
                        }
                      }
                      FilledIconButton(
                          modifier =
                              Modifier.testTag("editImageButton")
                                  .align(Alignment.BottomEnd)
                                  .padding(10.dp),
                          onClick = { viewModel.showBottomSheet() },
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

        PhotoSelectionSheet(
            visible = receiptState.showBottomSheet,
            hideSheet = { viewModel.hideBottomSheet() },
            setImageUri = { viewModel.setImage(it) },
            signalCameraPermissionDenied = { viewModel.signalCameraPermissionDenied() })
      }
}
