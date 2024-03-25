package com.github.se.assocify

import com.github.se.assocify.model.database.UserAPI
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Before
import org.junit.Test

class UserAPITest {

    private val db = FirebaseFirestore.getInstance()
    private val userAPI = UserAPI(db)
    @Before
    fun setUp() {
    }
    @After
    fun tearDown() {
    }

    @Test
    fun test() {
    }
}