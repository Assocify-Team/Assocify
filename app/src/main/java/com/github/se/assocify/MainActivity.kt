package com.github.se.assocify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.se.assocify.ui.theme.AssocifyTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.se.assocify.navigation.MAIN_TABS_LIST
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.navigation.mainNavGraph
import com.github.se.assocify.ui.composables.MainNavigationBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AssocifyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navActions = NavigationActions(navController)
                    NavHost(navController = navController, startDestination = Destination.Home.route) {
                        mainNavGraph(navActions = navActions)
                    }
                }
            }
        }
    }
}
