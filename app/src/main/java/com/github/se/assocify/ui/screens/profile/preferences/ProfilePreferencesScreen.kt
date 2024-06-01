package com.github.se.assocify.ui.screens.profile.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.assocify.model.entities.Theme
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.DropdownOption
import com.github.se.assocify.ui.composables.DropdownWithSetOptions
import com.github.se.assocify.ui.theme.ThemeViewModel

/**
 * The screen for the user to change the theme, text size, language and currency Accessed from the
 * profile screen
 *
 * will change the param to only have a viewmodel once implemented :
 *
 * @param navActions: The navigation actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePreferencesScreen(navActions: NavigationActions, appThemeViewModel: ThemeViewModel) {
  // temporary values for the theme, text size, language and currency, waiting for the viewmodel
  val themeOptions = listOf(Theme.LIGHT, Theme.DARK, Theme.SYSTEM)
    val currentTheme by appThemeViewModel.theme.collectAsState()
  var themeSelectedIndex by remember { mutableIntStateOf(themeOptions.indexOf(currentTheme)) }

  var sliderPosition by remember { mutableFloatStateOf(15f) }

  var openLanguageDropdown by remember { mutableStateOf(false) }
  val languageOptions = listOf(DropdownOption("English", "English"))
  var selectedLanguage by remember { mutableStateOf("English") }

  Scaffold(
      modifier = Modifier.testTag("preferencesScreen"),
      topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Preferences settings") },
            navigationIcon = {
              IconButton(
                  onClick = { navActions.back() }, modifier = Modifier.testTag("backButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Arrow Back")
                  }
            })
      },
      contentWindowInsets = WindowInsets(20.dp, 10.dp, 20.dp, 20.dp)) {
        Column(modifier = Modifier.padding(it), verticalArrangement = Arrangement.spacedBy(16.dp)) {
          // The theme options : color scheme of the app
          Text(
              text = "Theme",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.testTag("themeTitle"))

          SingleChoiceSegmentedButtonRow(
              modifier = Modifier.testTag("themeSegmentedButtonRow").align(CenterHorizontally)) {
                themeOptions.forEachIndexed { index, label ->
                  SegmentedButton(
                      shape =
                          SegmentedButtonDefaults.itemShape(
                              index = index, count = themeOptions.size),
                      onClick = { themeSelectedIndex = index
                        appThemeViewModel.setTheme(label) },
                      selected = index == themeSelectedIndex
                  ) {
                    Text(label.name)
                  }
                }
              }

          // The text size options : change the font size, from 12 to 20
          Text(
              text = "Size of text",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.testTag("textSize"))

          Row(
              modifier = Modifier.align(CenterHorizontally),
              horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    steps = 7,
                    valueRange = 12f..20f,
                    modifier = Modifier.testTag("textSizeSlider").weight(1f))

                Text(
                    text = "${sliderPosition.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterVertically).weight(0.1f))
              }

          // The language options : default is English, the only one supported currently
          Text(text = "Language", style = MaterialTheme.typography.titleMedium)

          DropdownWithSetOptions(
              options = languageOptions,
              selectedOption = DropdownOption(selectedLanguage, selectedLanguage),
              opened = openLanguageDropdown,
              onOpenedChange = { openLanguageDropdown = it },
              onSelectOption = { selectedLanguage = it.name },
              modifier = Modifier.testTag("languageDropdown").align(CenterHorizontally))

          Text(text = "Functionalities not yet implemented")
        }
      }
}
