package davidmedina.game.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import davidmedina.game.app.ui.screens.*
import davidmedina.game.app.ui.theme.DavidmedinagameTheme
import davidmedina.game.app.viewmodel.LoginViewModel
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            val currentBackStackEntry = navController.currentBackStackEntryAsState()
            DavidmedinagameTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text(text = "TDMG") },
                            navigationIcon = if (currentBackStackEntry.value?.destination?.route != Routes.HOME.name) {
                                {
                                    IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(Icons.Filled.ArrowBack, "backIcon")
                                    }
                                }
                            } else {
                                null
                            }
                        )
                    },

                    ) { innerPadding ->


                    NavHost(
                        navController = navController,
                        startDestination = Routes.HOME.name,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Routes.LOGIN.name) {
                            val loginViewModel = koinViewModel<LoginViewModel>()
                            LogInScreen({
                                navController.navigate(Routes.HOME.name) {
                                    popUpTo("login") {
                                        inclusive = true
                                        loginViewModel.navigated()
                                    }
                                }
                            }, loginViewModel)
                        }
                        composable(Routes.HOME.name) {
                            MainMenu({ navController.navigate(Routes.GAME.name) },
                                { navController.navigate(Routes.LINKS.name) },
                                { navController.navigate(Routes.FEEDBACK.name) })
                        }
                        composable(Routes.FEEDBACK.name) {
                            FeedBackScreen()
                        }
                        composable(Routes.LINKS.name) {
                            LinkScreen()
                        }
                        composable(Routes.GAME.name) {
                            GameScreen()
                        }
                    }
                }
            }

        }

    }

}

