package com.example.xplore.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.xplore.data.JourneyResponse
import com.example.xplore.data.LocationService
import com.example.xplore.data.TokenManager
import com.example.xplore.ui.addjourney.AddJourneyScreen
import com.example.xplore.ui.addjourney.AddJourneyScreenActions
import com.example.xplore.ui.detail.DetailScreen
import com.example.xplore.ui.detail.DetailScreenActions
import com.example.xplore.ui.home.HomeScreen
import com.example.xplore.ui.home.HomeScreenActions
import com.example.xplore.ui.myauth.SignInScreen
import com.example.xplore.ui.myauth.SignUpScreen
import com.example.xplore.ui.myauth.SigninScreenActions
import com.example.xplore.ui.myauth.SignupScreenActions
import com.example.xplore.ui.myfeed.FeedScreenActions
import com.example.xplore.ui.myfeed.MyFeedScreen
import com.example.xplore.ui.splash.SplashScreen
import com.example.xplore.ui.splash.SplashScreenActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

sealed class Screen(val name: String, val route: String) {
    object Splash : Screen("splash", "splash")
    object Home : Screen("home", "home")
    object Detail : Screen("detail", "detail")
    object Feed : Screen("feed", "feed")
    object SignIn : Screen("si", "si")
    object SignUp : Screen("su", "su")
    object AddJourney : Screen("aj", "aj")
}

@Composable
fun TravelNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    tokenManager: TokenManager,
    journeyLocator: JourneyGlobal,
    actions: NavActions = remember(navController) {
        NavActions(navController, tokenManager, journeyLocator)
    },
    locationService: LocationService
) {
    NavHost(
        navController = navController,
        startDestination = if (runBlocking { tokenManager.getAccessToken().first().isNullOrEmpty() }) Screen.Splash.name else Screen.Feed.name,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                SplashScreen(onAction = actions::navigateToSignIn)
            }

        }

        composable(Screen.Home.route) {
            HomeScreen(onAction = actions::navigateFromHome,navController = navController)
        }

        composable(Screen.Detail.route) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                DetailScreen(onAction = actions::navigateFromDetails, journeyLocator.journey.collectAsStateWithLifecycle().value)
            }
        }

        composable(Screen.Feed.route) {
            MyFeedScreen(onActions = actions::navigateFromFeed)
        }

        composable(Screen.SignIn.route) {
            Scaffold { inner ->
                SignInScreen(modifier=Modifier.padding(inner), onAction = actions::navigateFromSignIn)
            }
        }

        composable(Screen.SignUp.route) {
            Scaffold { inner ->
                SignUpScreen(modifier=Modifier.padding(inner), onAction = actions::navigateFromSignup)
            }
        }

        composable(Screen.AddJourney.route) {
            AddJourneyScreen(
                onAction = actions::navigateFromAddJourney,
                locationService = locationService
            )
        }
    }
}

class NavActions(private val navController: NavController, private val tokenManager: TokenManager,private val journeyLocator: JourneyGlobal) {
    fun navigateToHome(_A: SplashScreenActions ) {
        navController.navigate(Screen.Home.name) {
            popUpTo(Screen.Splash.route){
                inclusive = true
            }
        }
    }


    private fun navigateToFeed() {
        navController.navigate(Screen.Feed.name) {
            popUpTo(navController.graph.startDestinationId){
                inclusive = true
            }
        }
    }

    private fun navigateToDetail() {
        navController.navigate(Screen.Detail.name)
    }

    fun navigateToAddJourney() {
        navController.navigate(Screen.AddJourney.name)
    }

    fun navigateFromFeed(actions: FeedScreenActions) {
        when(actions) {
            FeedScreenActions.SignOut -> navigateToSignIn()
            is FeedScreenActions.ViewPost -> {
                journeyLocator.setVal(actions.p)
                navigateToDetail()
            }

            FeedScreenActions.AddJourney -> navigateToAddJourney()
        }
    }



    fun navigateToSignIn() {
        navController.navigate(Screen.SignIn.name) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }

        }
    }

    fun navigateToSignUp() {
        navController.navigate(Screen.SignUp.name) {
            popUpTo(Screen.Splash.route){
                inclusive = true
            }
        }
    }

    fun navigateFromHome(actions: HomeScreenActions) {
        when (actions) {
            HomeScreenActions.Details -> {
                navController.navigate(Screen.Detail.name)
            }
        }
    }

    fun navigateFromAddJourney(actions: AddJourneyScreenActions) {
        when (actions) {
            AddJourneyScreenActions.AddJourney -> navigateToFeed()
            AddJourneyScreenActions.Back -> navController.popBackStack()
        }
    }

    fun navigateFromDetails(actions: DetailScreenActions) {
        when(actions) {
            DetailScreenActions.Back -> navController.popBackStack()
        }
    }

    fun navigateFromSignIn(actions: SigninScreenActions) {
        when(actions) {
            SigninScreenActions.SignIn -> navigateToFeed()
            SigninScreenActions.ForgotPassword -> TODO()
            SigninScreenActions.SignUp -> navigateToSignUp()
        }
    }

    fun navigateFromSignup(actions: SignupScreenActions) {
        when(actions) {
            SignupScreenActions.SignIn -> navigateToSignIn()
            SignupScreenActions.SignUp -> navigateToFeed()
        }
    }
}

@Singleton
class JourneyGlobal @Inject constructor() {
    private val _journey = MutableStateFlow<JourneyResponse?>(null)
    var journey = _journey.asStateFlow()

    fun setVal(p: JourneyResponse) {
        _journey.value = p
    }
}

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Explore : BottomNavItem("explore", Icons.Default.Explore, "Explore")
    object Chats : BottomNavItem("chats", Icons.Default.Chat, "Chats")
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<BottomNavItem> = listOf(
        BottomNavItem.Home,
        BottomNavItem.Explore,
        BottomNavItem.Chats
    )
) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(text = item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when navigating back to a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        // Your screen content here
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Explore.route) { ExploreScreen() }
            composable(BottomNavItem.Chats.route) { ChatsScreen() }
        }
    }
}

// Example screen composables
@Composable
fun HomeScreen() {
    // Your home screen content
}

@Composable
fun ExploreScreen() {
    // Your explore screen content
}

@Composable
fun ChatsScreen() {
    // Your chats screen content
}
