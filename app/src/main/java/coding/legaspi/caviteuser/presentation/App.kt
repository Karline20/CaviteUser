package coding.legaspi.caviteuser.presentation

import android.app.Application
import coding.legaspi.caviteuser.BuildConfig
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.di.core.AppComponent
import coding.legaspi.caviteuser.presentation.di.core.AppModule
import coding.legaspi.caviteuser.presentation.di.core.DaggerAppComponent
import coding.legaspi.caviteuser.presentation.di.core.NetModule
import coding.legaspi.caviteuser.presentation.di.events.EventSubComponent


class App: Application(), Injector {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .netModule(NetModule(BuildConfig.BASE_URL))
            .build()
    }

    override fun createEventsSubComponent(): EventSubComponent {
        return appComponent.eventSubComponent().create()
    }
}