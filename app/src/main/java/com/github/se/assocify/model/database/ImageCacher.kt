package com.github.se.assocify.model.database

import android.net.Uri
import android.util.Log
import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.downloadAuthenticatedTo
import io.github.jan.supabase.storage.upload
import java.nio.file.Path
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.toHexString

class ImageCacher(val timeout: Long, val cacheDir: Path, private val bucket: BucketApi) {
  private val scope = CoroutineScope(Dispatchers.Main)

  init {
    if (!cacheDir.toFile().exists()) {
      cacheDir.toFile().mkdirs()
    }
  }

  private fun tryAsync(onFailure: (Exception) -> Unit, block: suspend () -> Unit) {
    scope.launch {
      try {
        block()
      } catch (e: Exception) {
        Log.e("IMG", e.toString())
        onFailure(e)
      }
    }
  }

  fun uploadImage(
      pathInBucket: String,
      uri: Uri,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      bucket.upload(pathInBucket, uri, upsert = true)
      // Invalidate cache. It would be better to replace the image in the cache,
      // But fetching data from URIs is... difficult.
      val imageCachePath = cacheDir.resolve("$pathInBucket.jpg")
      if (!imageCachePath.toFile().delete()) {
        // If this fails, the cache is corrupted, but it only fails in bad situations
        // Where the cache breaking is okay
        Log.w("IMG", "Failed to delete image cache file")
      }
      onSuccess()
    }
  }

  fun invalidateImage(pathInBucket: String) {
    val imageCachePath = cacheDir.resolve("$pathInBucket.jpg")
    if (!imageCachePath.toFile().delete()) {
      Log.w("IMG", "Failed to delete image cache file")
    }
  }

  fun fetchImage(pathInBucket: String, onSuccess: (Path) -> Unit, onFailure: (Exception) -> Unit) {
    val imageFile = cacheDir.resolve("$pathInBucket.jpg")

    val now = System.currentTimeMillis()
    if (imageFile.toFile().exists() && now - imageFile.toFile().lastModified() < timeout) {
      onSuccess(imageFile)
      return
    }

    val nonce = Random.Default.nextInt().toHexString()
    val tmpImageCacheFile = cacheDir.resolve("$pathInBucket.$nonce.jpg.tmp").toFile()

    tryAsync({
      val deleted = tmpImageCacheFile.delete()
      if (!deleted) {
        Log.w("IMG", "Failed to delete temporary image cache file")
      }
      onFailure(it)
    }) {
      bucket.downloadAuthenticatedTo(pathInBucket, tmpImageCacheFile.toPath())
      if (tmpImageCacheFile.length() < 100L) {
        // Instead of throwing an exception, the bucket API writes a file with the error JSON.
        // So we do this...
        throw Exception("Image not found")
      }
      val renamed = tmpImageCacheFile.renameTo(imageFile.toFile())
      if (!renamed) {
        Log.w("IMG", "Failed to rename temporary image cache file ($pathInBucket)")
      }
      onSuccess(imageFile)
    }
  }
}
