package com.github.se.assocify.ui.screens.treasury.receipt

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.github.se.assocify.BuildConfig
import com.github.se.assocify.R
import com.github.se.assocify.createImageFile
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.DatePickerWithDialog
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(
    navActions: NavigationActions,
    receiptUid: String = "",
    viewModel: ReceiptViewModel =
        if (receiptUid.isEmpty()) {
          ReceiptViewModel(navActions)
        } else {
          ReceiptViewModel(receiptUid = receiptUid, navActions = navActions)
        }
) {

  val receiptState by viewModel.uiState.collectAsState()

  val context = LocalContext.current
  val file = context.createImageFile()
  val uri =
      FileProvider.getUriForFile(
          Objects.requireNonNull(context), BuildConfig.APPLICATION_ID + ".provider", file)

  var capturedImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }

  val cameraLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        capturedImageUri = uri
      }

  val permissionLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
          Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
          cameraLauncher.launch(uri)
        } else {
          Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
      }

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
      contentWindowInsets = WindowInsets(40.dp, 20.dp, 40.dp, 0.dp),
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
                          onClick = {
                            viewModel.setImage()
                            val permissionCheckResult =
                                ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.CAMERA)
                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                              cameraLauncher.launch(uri)
                            } else {
                              // Request a permission
                              permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                          },
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
