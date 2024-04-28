package com.github.se.assocify.model.database

import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import org.junit.Rule

@MockKExtension.ConfirmVerification
class TaskAPITest {
  @get:Rule val mockkRule = MockKRule(this)
}