package com.github.se.assocify.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun ErrorMessage(errorMessage: String?, onRetry: (() -> Unit)? = null) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxSize()) {
        errorMessage?.let { Text(text = it, modifier = Modifier.testTag("errorMessage")) }
        onRetry?.let { Button(onClick = it) { Text(text = "Retry") } }
      }
}
