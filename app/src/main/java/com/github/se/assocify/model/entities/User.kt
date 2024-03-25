package com.github.se.assocify.model.entities


data class User(
    val uid: String,
    val name: String,
    val role : Role
){
    constructor(): this("", "", Role(""))
}

