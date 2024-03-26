package com.github.se.assocify

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FirebaseTest {
    private val db = FirebaseFirestore.getInstance()
    private val myCollection = db.collection("test_collection")

    @Before
    fun setUp() {
        // Clear the test data before each test
        myCollection.document("test_document").delete()
    }

    @After
    fun tearDown() {
        // Clear the test data after each test
        myCollection.document("test_document").delete()
    }

    @Test
    fun testWriteAndReadData() {
        // Write data to Firebase Firestore
        val testData = hashMapOf("name" to "John Doe", "age" to 30)
        myCollection.document("test_document").set(testData)


        try {
            // Read data from Firebase Firestore and verify it
            val documentSnapshot = Tasks.await(myCollection.document("test_document").get())
            val name = documentSnapshot.getString("name")
            val age = documentSnapshot.getLong("age")
            Assert.assertEquals("John Doe", name)
            Assert.assertEquals(30, age?.toInt())
        } catch (e: Exception) {
            Assert.fail("The test should fail if there's an error") // The test should fail if there's an error


        }
    }
}
