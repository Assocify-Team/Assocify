package com.github.se.assocify.ui.util

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** A system to show snackbars in the app. */
class SnackbarSystem(private val snackbarHostState: SnackbarHostState) {
  /**
   * Shows a snackbar with the given message.
   *
   * Note : Persistent snackbars with no action will have a cross to dismiss them even if dismiss is
   * false
   *
   * @param message the message to show
   * @param actionLabel the label for the action button
   * @param actionHandler the handler for the action button
   * @param dismiss whether the snackbar should have a cross to dismiss it
   * @param persistent whether the snackbar should remain until explicitely dismissed (otherwise,
   *   times out on its own)
   */
  fun showSnackbar(
      message: String,
      actionLabel: String? = null,
      actionHandler: (() -> Unit) = {},
      dismiss: Boolean = false,
      persistent: Boolean = false,
  ) {

    CoroutineScope(Dispatchers.Main).launch {
      val result =
          snackbarHostState.showSnackbar(
              message, actionLabel, dismiss || (persistent && actionLabel == null))
      if (result == SnackbarResult.ActionPerformed) {
        actionHandler()
      }
    }
  }
}
