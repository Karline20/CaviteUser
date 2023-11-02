package coding.legaspi.caviteuser.presentation.di.core

import coding.legaspi.caviteuser.data.repository.events.AllEventsRepositoryImpl
import coding.legaspi.caviteuser.data.repository.events.datasource.AllEventsRemoteDataSource
import coding.legaspi.caviteuser.domain.repository.AllEventsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideEventRepository(
        eventsRemoteDataSource: AllEventsRemoteDataSource
    ): AllEventsRepository {
        return AllEventsRepositoryImpl(eventsRemoteDataSource)
    }
}