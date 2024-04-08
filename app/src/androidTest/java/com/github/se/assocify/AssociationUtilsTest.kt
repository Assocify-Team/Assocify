package com.github.se.assocify

import com.github.se.assocify.model.associations.AssociationUtils
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.FirebaseApi
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockMakers
import org.mockito.Mockito
import org.mockito.Mockito.mock

class AssociationUtilsTest {
    private lateinit var db: FirebaseFirestore
  private lateinit var assoApi: AssociationAPI
    private val documentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
    private val documentReference = Mockito.mock(DocumentReference::class.java)
    private val collectionReference = Mockito.mock(CollectionReference::class.java)
    private val president = User("testId", "Carlo", Role("president"))
    private val newUser = User()
    val oldAsso =
        Association(
            "aId",
            "cassify",
            "a cool association",
            "31/09/2005",
            "active",
            listOf(president),
            emptyList())
    val oldAssoUpdated =
        Association(
            "aId",
            "cassify",
            "a cool association",
            "31/09/2005",
            "active",
            listOf(president, newUser),
            emptyList())

  @Before
  fun setup() {
    db = Mockito.mock(FirebaseFirestore::class.java)
      assoApi = AssociationAPI(db)
  }

  @Test
  fun checkThatEmptyAssocWorksWell() {
    val assoUtilsNewUser = AssociationUtils(newUser, associationDatabase = assoApi)
    assert(assoUtilsNewUser.getPendingUsers() == emptyList<User>())
    assert(assoUtilsNewUser.getRecordedUsers() == emptyList<User>())
    assert(assoUtilsNewUser.getEvents() == emptyList<User>())
    assert(assoUtilsNewUser.getCreationDate() == "")
    assert(assoUtilsNewUser.getAssociationName() == "")
  }

  @Test
  fun checkCreateWorks() {

  }
}
