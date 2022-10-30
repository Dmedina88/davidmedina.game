package davidmedina.game.app.di

import android.system.Os.bind
import davidmedina.game.app.data.network.DMGameApi
import davidmedina.game.app.data.network.DMGameApiImpl
import davidmedina.game.app.data.repository.LoginRepository
import davidmedina.game.app.data.repository.LoginRepositoryImpl
import davidmedina.game.app.ui.screens.GameScreen
import davidmedina.game.app.viewmodel.LoginViewModel
import davidmedina.game.app.viewmodel.HomeViewModel
import davidmedina.game.app.viewmodel.GameScreenViewModel


import org.koin.androidx.viewmodel.dsl.viewModelOf

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


//todo make seperite modules
val appModule = module {

    singleOf(::DMGameApiImpl) bind DMGameApi::class

    singleOf(::LoginRepositoryImpl) bind LoginRepository::class

}
val viewModuleModule = module {

    viewModelOf(::LoginViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::GameScreenViewModel)


}