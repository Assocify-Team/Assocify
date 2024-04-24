package com.github.se.assocify.ui.screens.event

class EventScreenViewModel {

}


// Index of each tag for navigation
enum class EventPageIndex(val index: Int) {
  TASKS(0),
  MAP(1),
  SCHEDULE(2);

  companion object {
    val NUMBER_OF_PAGES: Int = entries.size
  }
}