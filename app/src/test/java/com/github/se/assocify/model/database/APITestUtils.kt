package com.github.se.assocify.model.database

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain

object APITestUtils {
  /**
   * Sets up various convenient mocks for API testing.
   *
   * Currently sets up:
   * - Logging
   * - Dispatchers
   */
  @OptIn(ExperimentalCoroutinesApi::class)
  fun setup() {
    mockkStatic(Log::class)
    every { Log.e(any(), any()) } answers
        {
          println("\u001b[31m[ERROR] ${invocation.args[0]}: ${invocation.args[1]}\u001b[0m")
          1
        }

    every { Log.i(any(), any()) } answers
        {
          println("\u001b[32m[INFO] ${invocation.args[0]}: ${invocation.args[1]}\u001b[0m")
          1
        }

    every { Log.d(any(), any()) } answers
        {
          println("\u001b[34m[DEBUG] ${invocation.args[0]}: ${invocation.args[1]}\u001b[0m")
          1
        }

    every { Log.w(any(), any<String>()) } answers
        {
          println("\u001b[33m[WARN] ${invocation.args[0]}: ${invocation.args[1]}\u001b[0m")
          1
        }

    Dispatchers.setMain(UnconfinedTestDispatcher())
  }
}
