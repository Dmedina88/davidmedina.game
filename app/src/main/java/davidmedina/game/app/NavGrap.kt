package davidmedina.game.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import davidmedina.game.app.ui.screens.login.LogInScreen
import davidmedina.game.app.ui.screens.FeedBackScreen
import davidmedina.game.app.ui.screens.LinkScreen
import davidmedina.game.app.ui.screens.MainMenuScreen
import davidmedina.game.app.ui.screens.cardgame.GameScreen
import davidmedina.game.app.ui.screens.login.LoginViewModel
import davidmedina.game.app.ui.screens.register.RegisterScreen
import davidmedina.game.app.ui.screens.register.RegisterViewModel
import davidmedina.game.app.ui.screens.worldgen.BoxArtScreen
import davidmedina.game.app.ui.screens.worldgen.CanvisArtScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavGraph(navController: NavHostController, innerPadding : PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Routes.REGISTER.name,
        modifier = androidx.compose.ui.Modifier.padding(innerPadding)
    ) {
        composable(Routes.REGISTER.name) {
            RegisterScreen({
                navController.navigate(Routes.HOME.name) {
                    popUpTo(Routes.REGISTER.name) {
                        inclusive = true
                    }
                }
            })
        }
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
        //consider movi here for   wase of wierint
        composable(Routes.HOME.name) {
            MainMenuScreen(
                { navController.navigate(Routes.GAME.name) },
                { navController.navigate(Routes.LINKS.name) },
                { navController.navigate(Routes.FEEDBACK.name) },
                { navController.navigate(Routes.PROTO_GEN.name) },
                { navController.navigate(Routes.ART_GEN.name) },

            )
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
        composable(Routes.PROTO_GEN.name) {
            CanvisArtScreen()
        }
        composable(Routes.ART_GEN.name) {
            BoxArtScreen()
        }
    }
}