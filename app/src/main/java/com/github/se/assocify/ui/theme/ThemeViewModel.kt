package com.github.se.assocify.ui.theme

import android.util.Log
import com.github.se.assocify.model.entities.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel {
    private val _theme: MutableStateFlow<Theme> = MutableStateFlow(Theme.SYSTEM)
    val theme: StateFlow<Theme> = _theme
    fun setTheme(theme: Theme) {
        if ( _theme.value != theme) {
            Log.d("ThemeViewModel", "Setting dark theme to $theme")
            _theme.value = theme
        }
    }
}
