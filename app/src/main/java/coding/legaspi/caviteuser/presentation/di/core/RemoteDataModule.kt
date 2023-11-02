package coding.legaspi.caviteuser.presentation.di.core


import coding.legaspi.caviteuser.data.api.TMDBService
import coding.legaspi.caviteuser.data.repository.events.datasource.AllEventsRemoteDataSource
import coding.legaspi.caviteuser.data.repository.events.datasourceImpl.AllEventsRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RemoteDataModule {


    @Singleton
    @Provides
    fun provideAllEventRemoteDataSource(tmdbService: TMDBService): AllEventsRemoteDataSource {
        return AllEventsRemoteDataSourceImpl(tmdbService)
    }

}