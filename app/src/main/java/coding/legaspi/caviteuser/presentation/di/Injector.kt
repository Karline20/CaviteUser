package coding.legaspi.caviteuser.presentation.di

import coding.legaspi.caviteuser.presentation.di.events.EventSubComponent

interface Injector {
    fun createEventsSubComponent(): EventSubComponent
}

