package davidmedina.game.app.di


import davidmedina.game.app.data.network.DMGameApi
import davidmedina.game.app.data.network.DMGameApiImpl
import davidmedina.game.app.data.repository.MetaGameRepository
import davidmedina.game.app.data.repository.MetaGameRepositoryInMemory
import davidmedina.game.app.features.cardgame.GameScreenViewModel
import davidmedina.game.app.features.login.LoginViewModel
import davidmedina.game.app.features.mainmenu.MainMenuViewModel
import davidmedina.game.app.features.register.RegisterViewModel
import davidmedina.game.app.features.rpg.battle.BattleStateMachine
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import davidmedina.game.app.features.rpg.states.CharacterViewModel
import davidmedina.game.app.features.antigravity.GravityPlaygroundViewModel
import davidmedina.game.app.features.antigravity.ColorMosaicViewModel
import davidmedina.game.app.features.antigravity.HandGravityViewModel
import davidmedina.game.app.features.antigravity.HandQuestViewModel
import davidmedina.game.app.features.antigravity.ClassicDungeonViewModel

val appModule = module {

    singleOf(::DMGameApiImpl) bind DMGameApi::class
    singleOf(::MetaGameRepositoryInMemory) bind MetaGameRepository::class
}
val viewModuleModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::BattleStateMachine)

    viewModelOf(::MainMenuViewModel)
    viewModelOf(::RegisterViewModel)

    viewModelOf(::GameScreenViewModel)
    viewModelOf(::CharacterViewModel)
    viewModelOf(::GravityPlaygroundViewModel)
    viewModelOf(::ColorMosaicViewModel)
    viewModelOf(::HandGravityViewModel)
    viewModelOf(::HandQuestViewModel)
    viewModelOf(::ClassicDungeonViewModel)
}