
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.github.se.assocify.model.localsave.LoginSave
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class NavigationActionsTest {

  private lateinit var navController: NavHostController
  private lateinit var loginSave: LoginSave
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navController = mockk(relaxed = true)
    loginSave = mockk(relaxed = true)
    navigationActions = NavigationActions(navController, loginSave)
  }

  @Test(expected = IllegalArgumentException::class)
  fun `navigateToMainTab throws exception when destination is not main tab`() {
    navigationActions.navigateToMainTab(Destination.Login)
  }

  @Test
  fun `navigateTo performs navigation`() {
    navigationActions.navigateTo(Destination.Login)

    verify { navController.navigate(Destination.Login.route) }
  }

  @Test
  fun `onLogin navigates to Home when user exists`() {
    navigationActions.onLogin(true)

    verify { navController.navigate(Destination.Home.route, any<(NavOptionsBuilder) -> Unit>()) }
  }

  @Test
  fun `onLogin navigates to SelectAsso when user does not exist`() {
    navigationActions.onLogin(false)

    verify { navController.navigate(Destination.SelectAsso.route, any<(NavOptionsBuilder) -> Unit>()) }
  }

  @Test
  fun `back navigates back`() {
    navigationActions.back()

    verify { navController.popBackStack() }
  }
}
