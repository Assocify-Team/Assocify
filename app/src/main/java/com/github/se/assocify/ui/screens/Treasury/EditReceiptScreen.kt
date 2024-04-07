package com.github.se.assocify.ui.screens.Treasury

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun EditReceiptScreen() {
    var receiptTitle by remember { mutableStateOf("Courses ICeLan") }
    var receiptDesc by remember { mutableStateOf("Courses pour la bouffe IceLan ") }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri.value = result.data!!.data!!
        }
    }

    fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        launcher.launch(intent)
    }

    Scaffold(
        topBar = {
            EditReceiptTopBar("Courses ICeLan") {}
        }
    ) { innerPadding ->
        Column(
            modifier =
            Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState(), true),
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            OutlinedTextField(
                modifier = Modifier
                    .testTag("inputTodoTitle")
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                value = receiptTitle,
                onValueChange = { receiptTitle = it },
                label = { Text("Title") },
                placeholder = { Text("Name the task") },
                isError = receiptTitle.isEmpty(),
                supportingText = {
                    if (receiptTitle.isEmpty()) {
                        Text(text = "Receipt title cannot be empty", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Description
            OutlinedTextField(
                modifier = Modifier
                    .testTag("inputTodoTitle")
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                value = receiptDesc,
                onValueChange = { receiptDesc = it },
                label = { Text("Description") },
                placeholder = { Text("Name the task") },
                isError = receiptDesc.isEmpty(),
                supportingText = {
                    if (receiptDesc.isEmpty()) {
                        Text(text = "Receipt desc cannot be empty", color = MaterialTheme.colorScheme.error)
                    }
                },
                minLines = 3
            )

            // Image box
            Text(
                text = "Receipt",
                style = MaterialTheme.typography.headlineSmall
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .height(300.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                selectedImageUri.value?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } ?: run {
                    Text("No image selected")
                }
            }

            // Upload button
            Button(
                onClick = { openImagePicker() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Text("Upload file")
            }

            // Upload button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = { /* Cancel button action */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = { /* Save button action */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReceiptTopBar(
    receiptTitle: String,
    onBackClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = receiptTitle,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun OpenImagePicker(onImageSelected: (Uri) -> Unit) {
    val intent = Intent(Intent.ACTION_PICK).apply {
        type = "image/*"
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            onImageSelected(result.data!!.data!!)
        }
    }
    launcher.launch(intent)
}



@Preview
@Composable
private fun PreviewEditReceipt() {
    EditReceiptScreen()
}

