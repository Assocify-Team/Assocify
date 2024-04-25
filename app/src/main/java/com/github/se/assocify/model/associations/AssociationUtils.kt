/*
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
    db.getAllUsers(
        assocId,
        { usList ->
          usList.filter { us -> us.uid == user.uid }
              .forEach { us ->
                val newUser = User(uid = us.uid, name = us.name, role = role)
                db.updateUser(assocId, newUser, onFailure)
              }
          }, onFailure)
        }
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

 */
