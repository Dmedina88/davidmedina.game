package davidmedina.game.app

import android.app.Application
import davidmedina.game.app.di.appModule
import davidmedina.game.app.di.viewModuleModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber


class DMGameApplication : Application() {
    override fun onCreate() {


        startKoin {
            androidLogger()
            androidContext(this@DMGameApplication)
            modules(appModule, viewModuleModule)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        super.onCreate()
    }
}