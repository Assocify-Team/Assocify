package com.github.se.assocify.model.database

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk

object APITestUtils {
  inline fun <reified T> mockSuccessfulTask(result: T? = null): Task<T> {
    val task = mockk<Task<T>>()
    every { task.addOnSuccessListener(any()) }
        .answers {
          @Suppress("UNCHECKED_CAST")
          (it.invocation.args[0] as OnSuccessListener<T>).onSuccess(result)
          task
        }

    every { task.addOnFailureListener(any()) }.returns(task)

    return task
  }

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

  inline fun <reified T> mockFailingTask(
      exception: Exception = Exception("Test exception")
  ): Task<T> {
    val task = mockk<Task<T>>()
    every { task.addOnFailureListener(any()) }
        .answers {
          (it.invocation.args[0] as OnFailureListener).onFailure(exception)
          task
        }

    every { task.addOnSuccessListener(any()) }.returns(task)

    return task
  }

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
