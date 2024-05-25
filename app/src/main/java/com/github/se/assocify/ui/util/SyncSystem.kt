package com.github.se.assocify.ui.util

/**
 * A system to manage sync operations. This class is used to manage a set of sync operations. It is
 * used to ensure that all operations are completed before calling the success callback. If an error
 * occurs during any of the operations, the error callback is called and the success callback is not
 * called.
 *
 * In particular useful for loading or refreshing several things at once (asynchronous)
 *
 * @param success The callback to call when all operations are completed successfully.
 * @param error The callback to call when an error occurs during any of the operations.
 */
class SyncSystem(private val success: () -> Unit, private val error: (String) -> Unit) {
  private var counter = 0
  private var errorOccurred = false

  /**
   * Start a new set of sync operation.
   *
   * @param count The number of operations that will be performed.
   * @return True if the op set was started, false if there is already an op set in progress.
   */
  fun start(count: Int): Boolean {
    return if (counter == 0) {
      counter = count
      errorOccurred = false
      true
    } else {
      false
    }
  }

  /**
   * End a sync operation.
   *
   * @param error An error message if an error occured.
   */
  fun end(error: String? = null) {
    if (errorOccurred) return

    if (error != null) {
      errorOccurred = true
      counter = 0
      error(error)
    } else {
      counter -= 1
      if (counter == 0) {
        success()
      }
    }
  }
}
