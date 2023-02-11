package davidmedina.game.app.di

import davidmedina.game.app.data.network.DMGameApi
import davidmedina.game.app.data.network.DMGameApiImpl
import davidmedina.game.app.data.repository.*
import davidmedina.game.app.features.login.LoginViewModel
import davidmedina.game.app.features.cardgame.GameScreenViewModel
import davidmedina.game.app.features.mainmenu.MainMenuViewModel
import davidmedina.game.app.features.register.RegisterViewModel
import davidmedina.game.app.features.worldgen.BoxArtViewModal


import org.koin.androidx.viewmodel.dsl.viewModelOf

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val appModule = module {

    singleOf(::DMGameApiImpl) bind DMGameApi::class
    singleOf(::MetaGameRepositoryInMemory) bind MetaGameRepository::class
}
val viewModuleModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainMenuViewModel)
    viewModelOf(::RegisterViewModel)

    viewModelOf(::BoxArtViewModal)
    viewModelOf(::GameScreenViewModel)
}