package com.github.se.assocify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.se.assocify.model.localsave.LoginSave
import com.github.se.assocify.ui.theme.AssocifyTheme
import com.github.se.assocify.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    val themeVM = ThemeViewModel()


    setContent {
      val theme by themeVM.theme.collectAsState()

      AssocifyTheme(themeVM = themeVM, theme = theme){
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          AssocifyApp(LoginSave(this), themeVM)
        }
      }
    }
  }
}
