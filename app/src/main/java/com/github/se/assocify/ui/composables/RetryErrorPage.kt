package com.github.se.assocify.ui.composables

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ErrorMessagePage(
    errorMessage: String?,
    onBack: (() -> Unit),
    title: String? = null,
    onRetry: (() -> Unit)? = null,
) {
  Scaffold(
      topBar = {
        CenterAlignedTopAppBar(
            title = { title?.let { Text(text = it) } },
            navigationIcon = {
              IconButton(onClick = { onBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
              }
            })
      },
  ) {
    ErrorMessage(errorMessage) {
        onRetry?.invoke()
    }
  }
}
