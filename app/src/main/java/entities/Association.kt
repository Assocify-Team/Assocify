package entities

import java.time.LocalDate

data class Association(
    val name: String,
    val description: String,
    val creationDate: LocalDate,
    val status: String,
    val members: Map<User, Role>,
    val events:  List<User>,
)
