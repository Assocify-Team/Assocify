package com.github.se.assocify.model.entities

import java.time.LocalDate

data class Association(
    val uid: String,
    val name: String,
    val description: String,
    val creationDate: LocalDate,
)
