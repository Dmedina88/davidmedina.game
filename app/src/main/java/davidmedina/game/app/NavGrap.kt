package davidmedina.game.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import davidmedina.game.app.features.FeedBackScreen
import davidmedina.game.app.features.LinkScreen
import davidmedina.game.app.features.ai_musemum.*
import davidmedina.game.app.features.cardgame.GameScreen
import davidmedina.game.app.features.login.LogInScreen
import davidmedina.game.app.features.login.LoginViewModel
import davidmedina.game.app.features.mainmenu.MainMenuScreen
import davidmedina.game.app.features.map.MapScreen
import davidmedina.game.app.features.register.RegisterScreen
import davidmedina.game.app.features.rpg.battle.ui.RPGBattleScreen
import davidmedina.game.app.features.rpg.states.CharacterMenuScreen
import davidmedina.game.app.features.storygame.GodotTwoFlower
import davidmedina.game.app.features.storygame.blueoger.level1.BlueOgerOpening
import davidmedina.game.app.features.worldgen.BoxArtScreen
import davidmedina.game.app.features.worldgen.CanvisArtScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavGraph(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME.name,
        modifier = androidx.compose.ui.Modifier.padding(innerPadding)
    ) {

        composable(Routes.HOME.name) {
            MainMenuScreen(navController)

        }

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
        composable(Routes.STORY_MODE.name) {
            BlueOgerOpening()
        }
        composable(Routes.STORY_TEST.name) {
            GodotTwoFlower()
        }
        composable(Routes.RPG.name) {
            RPGBattleScreen()
        }
        composable(Routes.RPG_CHARACTER.name) {
            CharacterMenuScreen()
        }
        composable(Routes.AI_ART.name) {
            AIUIWorkCarousel()
        }
        composable(Routes.RPG_MAP.name) {
            MapScreen()
        }
    }
}