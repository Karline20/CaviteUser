package coding.legaspi.caviteuser.presentation.di.core

import android.content.Context
import coding.legaspi.caviteuser.presentation.di.events.EventSubComponent

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(subcomponents = [
    EventSubComponent::class
])
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideApplicationContext(): Context{
        return context.applicationContext
    }
}