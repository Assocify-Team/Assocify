package com.github.se.assocify.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun BackButton(contentDescription: String, onClick: () -> Unit) {
  IconButton(onClick = onClick) {
    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = contentDescription)
  }
}
