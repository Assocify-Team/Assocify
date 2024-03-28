package com.github.se.assocify.ui.screens.Treasury

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.se.assocify.R

@Composable
fun EditReceiptScreen() {
    var receiptTitle by remember { mutableStateOf("Courses ICeLan") }
    var receiptDesc by remember { mutableStateOf("Courses pour la bouffe IceLan ") }

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
                minLines = 2
            )

            // Image
            //ImageWithUploadButton()
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
fun ImageWithUploadButton() {
    var imageResourceId by remember { mutableStateOf(R.drawable.ic_launcher_background) }

    Box(
        modifier = Modifier
            .border(BorderStroke(2.dp, color = MaterialTheme.colorScheme.primary), shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {
        Image(
            painter = painterResource(id = imageResourceId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }

    Button(onClick = { /*TODO: Add upload logic here and update imageResourceId*/ }) {
        Text("Upload new file")
    }
}

@Preview
@Composable
private fun PreviewEditReceipt() {
    EditReceiptScreen()
}

