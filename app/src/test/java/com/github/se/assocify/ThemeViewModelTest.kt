package com.github.se.assocify

import com.github.se.assocify.model.entities.Theme
import com.github.se.assocify.ui.theme.ThemeViewModel
import org.junit.Test

class ThemeViewModelTest {

    @Test
    fun testSetTheme() {
        val themeVM = ThemeViewModel()
        themeVM.setTheme(Theme.DARK)
        assert(themeVM.theme.value == Theme.DARK)
        themeVM.setTheme(Theme.LIGHT)
        assert(themeVM.theme.value == Theme.LIGHT)
        themeVM.setTheme(Theme.SYSTEM)
        assert(themeVM.theme.value == Theme.SYSTEM)
    }
}