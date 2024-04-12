import androidx.navigation.NavGraph.Companion.findStartDestination
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import androidx.navigation.NavHostController
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions

class NavigationActionsTest {

  private lateinit var navController: NavHostController
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navController = mockk(relaxed = true)
    navigationActions = NavigationActions(navController)
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

    verify { navController.navigate(Destination.Home.route) }
  }

  @Test
  fun `onLogin navigates to SelectAsso when user does not exist`() {
    navigationActions.onLogin(false)

    verify { navController.navigate(Destination.SelectAsso.route) }
  }

  @Test
  fun `back navigates back`() {
    navigationActions.back()

    verify { navController.popBackStack() }
  }
}
