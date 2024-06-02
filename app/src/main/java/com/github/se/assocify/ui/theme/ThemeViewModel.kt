package com.github.se.assocify.ui.theme

import com.github.se.assocify.model.entities.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel {
  private val _theme: MutableStateFlow<Theme> = MutableStateFlow(Theme.SYSTEM)
  val theme: StateFlow<Theme> = _theme

  fun setTheme(theme: Theme) {
    if (_theme.value != theme) {
      _theme.value = theme
    }
  }
}
