package com.github.se.assocify.ui

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
<<<<<<< HEAD
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
=======
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
>>>>>>> 4e6cde9d7cdce77d0e2f93e603fe6982136fdf42
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
<<<<<<< HEAD
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.assocify.R
import com.github.se.assocify.ui.theme.AssocifyTheme
=======
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.assocify.R
>>>>>>> 4e6cde9d7cdce77d0e2f93e603fe6982136fdf42
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
<<<<<<< HEAD
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                onAuthComplete(authResult)
            }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }
=======
  val scope = rememberCoroutineScope()
  return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      result ->
    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
    try {
      val account = task.getResult(ApiException::class.java)!!
      val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
      scope.launch {
        val authResult = Firebase.auth.signInWithCredential(credential).await()
        onAuthComplete(authResult)
      }
    } catch (e: ApiException) {
      onAuthError(e)
    }
  }
>>>>>>> 4e6cde9d7cdce77d0e2f93e603fe6982136fdf42
}

@Composable
fun LoginPage(onLogin: (FirebaseUser?) -> Unit, onAuthError: () -> Unit) {
<<<<<<< HEAD
    val launcher =
        rememberFirebaseAuthLauncher(
            onAuthComplete = { result -> onLogin(result.user) },
            onAuthError = { onAuthError() })
    val token = stringResource(R.string.web_client_id)
    val context = LocalContext.current

    Column(
        modifier =
        Modifier
            .background(color = Color(0xFFFFFFFF))
            .testTag("LoginScreen"),
        verticalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .width(189.dp)
                .height(189.dp),
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "App logo",
            contentScale = ContentScale.FillBounds)
        Text(
            modifier = Modifier.testTag("LoginTitle"),
            text = "Welcome",
            style =
=======
  val launcher =
      rememberFirebaseAuthLauncher(
          onAuthComplete = { result -> onLogin(result.user) }, onAuthError = { onAuthError() })
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
>>>>>>> 4e6cde9d7cdce77d0e2f93e603fe6982136fdf42
            TextStyle(
                fontSize = 48.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
<<<<<<< HEAD
            )
        )
        Spacer(modifier = Modifier.height(136.dp))
        TextButton(
            onClick = {
                val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(token)
                        .requestEmail()
                        .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                launcher.launch(googleSignInClient.signInIntent)
            },
            modifier =
            Modifier
                .border(
                    width = 1.dp,
                    color = Color(0xFFDADCE0),
                    shape = RoundedCornerShape(size = 20.dp)
                )
                .testTag("LoginButton")
        ){
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
=======
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
          launcher.launch(googleSignInClient.signInIntent)
        },
        modifier =
            Modifier.border(
                    width = 1.dp,
                    color = Color(0xFFDADCE0),
                    shape = RoundedCornerShape(size = 20.dp))
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
>>>>>>> 4e6cde9d7cdce77d0e2f93e603fe6982136fdf42
                    TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF3C4043),
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.25.sp,
                    ))
<<<<<<< HEAD
            }
        }
    }
}
=======
          }
        }
  }
}
>>>>>>> 4e6cde9d7cdce77d0e2f93e603fe6982136fdf42
