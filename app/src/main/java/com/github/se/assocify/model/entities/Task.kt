package com.github.se.assocify.model.entities

data class Task(val name: String, val description: String, val isCompleted: Boolean = false, val startTime: String = "Tomorrow 10 am", val peopleNeeded: Int = 0, val category: String = "") {
  constructor() : this("", "", false)

  override fun hashCode(): Int {
    return name.hashCode()
  }

  override fun toString(): String {
    return "Task(name='$name', description='$description', isCompleted=$isCompleted)"
  }
}
