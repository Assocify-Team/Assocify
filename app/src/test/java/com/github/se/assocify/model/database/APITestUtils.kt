package com.github.se.assocify.model.database

import android.net.Uri
import android.util.Log
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.AssociationMember
import com.github.se.assocify.model.entities.PermissionRole
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.model.entities.User
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.UploadData
import io.github.jan.supabase.storage.resumable.MemoryResumableCache
import io.github.jan.supabase.storage.resumable.createDefaultResumableCache
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import java.io.File
import java.nio.file.Path
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain

object APITestUtils {
  val USER = User("DEADBEEF-0000-0000-0000-000000000000", "API Test User", "test@example.com")
  val USER_JSON = """{"uid":"${USER.uid}", "name": "${USER.name}", "email": "${USER.email}"}"""
  val ASSOCIATION =
      Association(
          "13379999-0000-0000-0000-000000000000",
          "API Test Association",
          "A test association",
          LocalDate.of(2020, 2, 20))
  val ASSOCIATION_JSON =
      """
      {"uid": "${ASSOCIATION.uid}", "name": "${ASSOCIATION.name}", "description": "${ASSOCIATION.description}", "creation_date": "2020-02-20"}
      """
          .trimIndent()
  val PERMISSION_ROLE =
      PermissionRole("12345678-0000-0000-0000-000000000000", ASSOCIATION.uid, RoleType.PRESIDENCY)
  val PERMISSION_JSON =
      """
      {"uid": "${PERMISSION_ROLE.uid}", "association_id": "${PERMISSION_ROLE.associationId}", "type": "${PERMISSION_ROLE.type.name.lowercase()}"}
  """
          .trimIndent()

  val ASSOCIATION_MEMBER = AssociationMember(USER, ASSOCIATION, PERMISSION_ROLE)

  /**
   * Sets up various convenient mocks for API testing.
   *
   * Currently sets up:
   * - Logging
   * - Dispatchers
   * - Current user
   */
  @OptIn(ExperimentalCoroutinesApi::class, SupabaseInternal::class)
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
    CurrentUser.userUid = USER.uid
    CurrentUser.associationUid = ASSOCIATION.uid

    // Workaround for supabase internals that create a class unsupported on Linux.
    mockkStatic(::createDefaultResumableCache)
    every { createDefaultResumableCache() } returns MemoryResumableCache()

    // Mock android.net.Uri `fromFile`:
    mockkStatic(Uri::class)
    every { Uri.fromFile(any()) } answers { mockk() }
  }

  fun setupImageCacher(error: () -> Boolean): Path {
    val fileMock = mockk<File>()
    val cachePath = mockk<Path>()

    every { fileMock.exists() } returns false
    every { fileMock.mkdirs() } returns true
    every { fileMock.delete() } returns true
    every { fileMock.lastModified() } returns 0L
    every { fileMock.toPath() } returns cachePath
    every { fileMock.renameTo(any()) } returns true

    every { cachePath.resolve(any<String>()) } returns cachePath
    every { cachePath.resolve(any<Path>()) } returns cachePath
    every { cachePath.toFile() } returns fileMock

    val mockBucketApi = mockk<BucketApi>(relaxUnitFun = true)
    // Note: this doesn't work. `upload` with a URI is inlined,
    // but in the inline `applicationContext` is called, which throws an error.
    // So we can't mock uploading.
    coEvery { mockBucketApi.upload(any(), any<UploadData>(), any()) } answers
        {
          if (error()) {
            throw Exception("error = true, HTTP should throw error")
          } else {
            firstArg()
          }
        }
    coEvery { mockBucketApi.downloadAuthenticated(any(), any(), any()) } answers
        {
          if (error()) {
            throw Exception("error = true, HTTP should throw error")
          }
        }

    val mockStorageImpl = mockk<Storage>(relaxUnitFun = true)
    every { mockStorageImpl[any()] } returns mockBucketApi

    mockkObject(Storage)
    every { Storage.create(any(), any()) } returns mockStorageImpl
    return cachePath
  }
}
