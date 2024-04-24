package com.github.se.assocify.ui.composables

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.github.se.assocify.BuildConfig
import java.io.File
import java.util.Objects

/**
 * A modal bottom sheet with options to take a photo or select an image from the gallery.
 *
 * @param visible whether the sheet should be visible
 * @param hideSheet a callback to hide the sheet
 * @param setImageUri a callback to set the image URI
 * @param signalCameraPermissionDenied a callback to signal that the camera permission was denied
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoSelectionSheet(
    visible: Boolean,
    hideSheet: () -> Unit,
    setImageUri: (Uri?) -> Unit,
    signalCameraPermissionDenied: () -> Unit
) {
  val context = LocalContext.current
  val tempUri = remember { mutableStateOf<Uri?>(null) }
  fun getTempUri(): Uri? {
    val file =
        File.createTempFile(
            "image_" + System.currentTimeMillis().toString(), ".jpg", context.externalCacheDir)

    return FileProvider.getUriForFile(
        Objects.requireNonNull(context), BuildConfig.APPLICATION_ID + ".provider", file)
  }

  val cameraLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        setImageUri(tempUri.value)
      }

  val imagePicker =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { imageUri -> setImageUri(imageUri) })

  val permissionLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
          tempUri.value = getTempUri()
          cameraLauncher.launch(tempUri.value)
        } else {
          signalCameraPermissionDenied()
        }
      }

  if (visible) {
    ModalBottomSheet(onDismissRequest = { hideSheet() }) {
      Column(
          modifier = Modifier.fillMaxWidth().padding(bottom = 60.dp),
          horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier =
                    Modifier.padding(start = 16.dp, end = 16.dp).testTag("photoSelectionSheet"),
                text = "Choose option",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center)
            ListItem(
                modifier =
                    Modifier.testTag("takePhotoOption").clickable {
                      hideSheet()
                      val permissionCheckResult =
                          ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                      if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        tempUri.value = getTempUri()
                        cameraLauncher.launch(tempUri.value)
                      } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                      }
                    },
                headlineContent = {
                  Text(text = "Take photo", style = MaterialTheme.typography.titleMedium)
                },
                leadingContent = { Icon(Icons.Default.Camera, "Camera icon") })
            ListItem(
                modifier =
                    Modifier.testTag("selectImageOption").clickable {
                      hideSheet()
                      imagePicker.launch(
                          PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                headlineContent = {
                  Text(text = "Select image", style = MaterialTheme.typography.titleMedium)
                },
                leadingContent = { Icon(Icons.Default.Image, "Image icon") })
          }
    }
  }
}
