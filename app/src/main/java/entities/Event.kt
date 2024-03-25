package entities

import java.time.LocalDate

data class Event(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val organizers: List<User>,
    val staffers:  List<User>
)
