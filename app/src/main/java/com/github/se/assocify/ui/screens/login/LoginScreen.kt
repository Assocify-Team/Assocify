package com.github.se.assocify.ui.screens.login

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.assocify.R
import com.github.se.assocify.model.SupabaseClient
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.navigation.NavigationActions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.launch

@Composable
fun rememberSupabaseAuthLauncher(
    onAuthComplete: (UserInfo, GoogleSignInAccount) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
  val scope = rememberCoroutineScope()
  return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      result ->
    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
    try {
      val account = task.getResult(ApiException::class.java)!!
      scope.launch {
        SupabaseClient.supabaseClient.auth.signInWith(IDToken) {
          idToken = account.idToken!!
          provider = Google
        }
        onAuthComplete(SupabaseClient.supabaseClient.auth.currentUserOrNull()!!, account)
      }
    } catch (e: ApiException) {
      onAuthError(e)
    }
  }
}

@Composable
fun LoginScreen(navActions: NavigationActions, userAPI: UserAPI) {
  val viewModel = LoginViewModel(navActions, userAPI)

  val launcher =
      rememberSupabaseAuthLauncher(
          onAuthComplete = viewModel::updateUser, onAuthError = { navActions.onAuthError() })
  val token = stringResource(R.string.web_client_id)
  val context = LocalContext.current

  Column(
      modifier = Modifier.background(color = Color(0xFFFFFFFF)).testTag("LoginScreen"),
      verticalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Image(
        modifier = Modifier.width(189.dp).height(189.dp),
        painter = painterResource(id = R.drawable.app_logo),
        contentDescription = "App logo",
        contentScale = ContentScale.FillBounds)
    Text(
        modifier = Modifier.testTag("LoginTitle"),
        text = "Welcome",
        style =
            TextStyle(
                fontSize = 48.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
            ))
    Spacer(modifier = Modifier.height(136.dp))
    TextButton(
        onClick = {
          val gso =
              GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                  .requestIdToken(token)
                  .requestEmail()
                  .build()
          val googleSignInClient = GoogleSignIn.getClient(context, gso)
          googleSignInClient.signOut()
          launcher.launch(googleSignInClient.signInIntent)
        },
        modifier =
            Modifier.border(
                    width = 1.dp,
                    color = Color(0xFFDADCE0),
                    shape = RoundedCornerShape(size = 30.dp))
                .testTag("LoginButton")) {
          Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(32.dp),
          ) {
            Image(
                painter = painterResource(id = R.drawable.google_logo),
                contentDescription = "image description",
                contentScale = ContentScale.None)
            Text(
                text = "Sign in with Google",
                style =
                    TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF3C4043),
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.25.sp,
                    ))
          }
        }
  }
}
