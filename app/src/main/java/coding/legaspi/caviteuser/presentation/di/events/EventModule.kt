package coding.legaspi.caviteuser.presentation.di.events

import coding.legaspi.caviteuser.domain.getusecase.GetEventsUseCase
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class EventModule {
    @EventScope
    @Provides
    fun provideEventViewModelFactory(
        getEventsUseCase: GetEventsUseCase
    ): EventViewModelFactory {
        return EventViewModelFactory(
            getEventsUseCase
        )
    }

}