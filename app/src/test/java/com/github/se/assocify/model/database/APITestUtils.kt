package com.github.se.assocify.model.database

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk

object APITestUtils {
  /**
   * Mocks a successful task that succeeds with the given result immediately.
   *
   * @param result the result to return, null by default
   */
  inline fun <reified T> mockSuccessfulTask(result: T? = null): Task<T> {
    return mockSuccessfulTaskAdvanced<T, Task<T>>(result)
  }

  /**
   * Mocks a successful task that succeeds with the given result immediately, but can return a
   * subclass of Task.
   */
  inline fun <reified S, reified T : Task<S>> mockSuccessfulTaskAdvanced(result: S? = null): T {
    val task = mockk<T>()
    every { task.addOnSuccessListener(any()) }
        .answers {
          @Suppress("UNCHECKED_CAST")
          (it.invocation.args[0] as OnSuccessListener<S>).onSuccess(result)
          task
        }

    every { task.addOnFailureListener(any()) }.returns(task)

    return task
  }

  /**
   * Mocks a failing task that throws the given exception immediately.
   *
   * @param exception the exception to throw, a test exception by default
   */
  inline fun <reified T> mockFailingTask(
      exception: Exception = Exception("Test exception")
  ): Task<T> {
    return mockFailingTaskAdvanced<T, Task<T>>(exception)
  }

  /**
   * Mocks a failing task that throws the given exception immediately, but can return a subclass of
   * Task.
   */
  inline fun <reified S, reified T : Task<S>> mockFailingTaskAdvanced(
      exception: Exception = Exception("Test exception")
  ): T {
    val task = mockk<T>()
    every { task.addOnFailureListener(any()) }
        .answers {
          (it.invocation.args[0] as OnFailureListener).onFailure(exception)
          task
        }

    every { task.addOnSuccessListener(any()) }.returns(task)

    return task
  }
}
