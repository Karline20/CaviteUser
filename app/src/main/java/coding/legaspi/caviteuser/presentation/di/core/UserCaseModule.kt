package coding.legaspi.caviteuser.presentation.di.core

import coding.legaspi.caviteuser.domain.getusecase.GetEventsUseCase
import coding.legaspi.caviteuser.domain.repository.AllEventsRepository
import dagger.Module
import dagger.Provides

@Module
class UserCaseModule {


    @Provides
    fun providegetRvEventsUseCase(allEventsRepository: AllEventsRepository): GetEventsUseCase {
        return GetEventsUseCase(allEventsRepository)
    }

}