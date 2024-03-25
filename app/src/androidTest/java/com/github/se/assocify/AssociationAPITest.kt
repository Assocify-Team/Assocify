package com.github.se.assocify

import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.Event
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class AssociationAPITest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var assoAPI: AssociationAPI

    @Before
    fun setup() {
        firestore = FirebaseFirestore.getInstance()
        assoAPI = AssociationAPI(firestore)
    }

    @After
    fun teardown() {
        // Delete all associations added during the test to clean up the database
        runBlocking {
            assoAPI.deleteAllAssociations()
        }
    }


    @Test
    fun testGetAllAssociations() {
        assoAPI.deleteAllAssociations()
        // Given
        val association1 = Association(assoAPI.getNewId(), "caaaa", "description", "", "status", emptyList(), emptyList())
        val association2 = Association(assoAPI.getNewId(), "caaaa", "description", "", "status", emptyList(), emptyList())
        val association3 = Association(assoAPI.getNewId(), "caaaa", "description", "", "status", emptyList(), emptyList())

        // When
        assoAPI.addAssociation(association1)
        assoAPI.addAssociation(association2)
        assoAPI.addAssociation(association3)

        // Then
        val associations = Tasks.await(assoAPI.getAssociations())

        assertEquals(3, associations.size)
    }
    @Test
    fun testUpdateAssociation() {
        // Given
        val association = Association(assoAPI.getNewId(), "caaaa", "description", "", "status", emptyList(), emptyList())
        assoAPI.addAssociation(association)

        // When
        val updatedAssociation = Association(association.uid, "new name", "new description", "", "new status", emptyList(), emptyList())
        assoAPI.addAssociation(updatedAssociation)

        // Then
        val updatedAssociationTask = assoAPI.getAssociation(association.uid)
        val fetchedUpdatedAssociation = updatedAssociationTask

        assertEquals(updatedAssociation.uid, fetchedUpdatedAssociation.uid)
        assertEquals(updatedAssociation.name, fetchedUpdatedAssociation.name)
        assertEquals(updatedAssociation.description, fetchedUpdatedAssociation.description)
        assertEquals(updatedAssociation.creationDate, fetchedUpdatedAssociation.creationDate)
        assertEquals(updatedAssociation.status, fetchedUpdatedAssociation.status)
        assertEquals(updatedAssociation.members, fetchedUpdatedAssociation.members)
        assertEquals(updatedAssociation.events, fetchedUpdatedAssociation.events)
    }



    @Test
    fun testAddAssociation_associationDoesNotExist() {
        // Given
        val association = Association(assoAPI.getNewId(), "caaaa", "description", "", "status", emptyList(), emptyList())

        // When
        assoAPI.addAssociation(association)

        // Then
        val addedAssociationTask = assoAPI.getAssociation(association.uid)
        val addedAssociation = addedAssociationTask

        assertNotNull(addedAssociation)
        assertEquals(association.uid, addedAssociation.uid)
        assertEquals(association.name, addedAssociation.name)
        assertEquals(association.description, addedAssociation.description)
        assertEquals(association.creationDate, addedAssociation.creationDate)
        assertEquals(association.status, addedAssociation.status)
        assertEquals(association.members, addedAssociation.members)
        assertEquals(association.events, addedAssociation.events)
    }

    @Test
    fun testDeleteAssociation() {
        assoAPI.deleteAllAssociations()
        // Add some test associations to the database
        val usss = User("test-user-uid", "Test User", Role("Admin"))
        val association1 = Association(
            uid = assoAPI.getNewId(),
            name = "Test Association 1",
            description = "This is a test association",
            creationDate = "",
            status = "active",
            members = listOf(
                 usss
            ),
            events = listOf(
                Event(
                    startDate = "",
                    endDate ="",
                    organizers = listOf(User("test-user-uid", "Test User", usss.role)),
                    staffers = listOf(User("test-user-uid", "Test User", usss.role))
                )
            )
        )
        val association2 = Association(
            uid = assoAPI.getNewId(),
            name = "Test Association 1",
            description = "This is a test association",
            creationDate = "",
            status = "active",
            members = listOf(
                usss
            ),
            events = listOf(
                Event(
                    startDate = "",
                    endDate = "",
                    organizers = listOf(User("test-user-uid", "Test User", usss.role)),
                    staffers = listOf(User("test-user-uid", "Test User", usss.role))
                )
            )
        )
        assoAPI.addAssociation(association1)
        assoAPI.addAssociation(association2)

        // Delete one of the test associations
        assoAPI.deleteAssociation(association1.uid)

        // Get the list of associations from the database
        val associations = Tasks.await(assoAPI.getAssociations())

        // Verify that the deleted association is no longer present
        assertEquals(1, associations.size)
        assertTrue(associations.contains(association2))
    }

}