package com.github.se.assocify

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

fun Context.createImageFile(): File {
  // Create an image file name
  val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
  val imageFileName = "JPEG_" + timeStamp + "_"
  val image =
      File.createTempFile(
          imageFileName, /* prefix */ ".jpg", /* suffix */ externalCacheDir /* directory */)
  return image
}
