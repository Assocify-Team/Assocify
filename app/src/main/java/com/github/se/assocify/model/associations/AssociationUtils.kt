import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.Role
import com.github.se.assocify.model.entities.User

class AssociationRequest(private val db: AssociationAPI) {
  /**
   * Accept a new user into an association
   *
   * @param assocId the id of the association
   * @param user the user to accept
   * @param role the new role of the user
   * @param onFailure called on failure
   */
  fun acceptNewUser(assocId: String, user: User, role: Role, onFailure: (Exception) -> Unit) {
    db.getAssociation(
        assocId,
        { ass ->
          val matchingUsers = ass.getMembers().filter { listUser -> user.uid == listUser.uid }
          if (matchingUsers.size != 1) {
            val newUser = User(user.uid, user.getName(), role)
            val newAss =
                Association(
                    uid = ass.uid,
                    creationDate = ass.getCreationDate(),
                    name = ass.getName(),
                    description = ass.getDescription(),
                    events = ass.getEvents(),
                    members =
                        ass.getMembers().filter { listUser -> listUser.uid == user.uid } + newUser,
                    status = ass.getStatus())
            db.addAssociation(newAss, onFailure = onFailure)
          }
        },
        onFailure)
  }

  /**
   * Ask to a member to be accepted in an association
   *
   * @param assocId the id of the association
   * @param user the user that we want to add
   * @param onFailure called on failure
   */
  fun askAssociationAccess(assocId: String, user: User, onFailure: (Exception) -> Unit) {
    db.getAssociation(
        assocId,
        { ass ->
          val waitingUser = User(uid = user.uid, name = user.getName(), role = Role("pending"))
          val newAss =
              Association(
                  uid = ass.uid,
                  creationDate = ass.getCreationDate(),
                  name = ass.getName(),
                  description = ass.getDescription(),
                  events = ass.getEvents(),
                  members = ass.getMembers() + waitingUser,
                  status = ass.getStatus())
          db.addAssociation(newAss, onFailure = onFailure)
        },
        onFailure)
  }
}
