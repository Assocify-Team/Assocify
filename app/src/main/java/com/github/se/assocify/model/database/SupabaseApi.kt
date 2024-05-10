package com.github.se.assocify.model.database

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class SupabaseApi {
  protected val scope = CoroutineScope(Dispatchers.Main)

  protected fun tryAsync(
      onFailure: (Exception) -> Unit,
      tag: String? = null,
      block: suspend () -> Unit
  ) {
    scope.launch {
      try {
        block()
      } catch (e: Exception) {
        if (tag != null) {
          Log.e("API", "Error in $tag")
        }
        Log.e("API", e.toString())
        onFailure(e)
      }
    }
  }
}
