package coding.legaspi.caviteuser.presentation.di.core

import android.content.Context
import android.util.Log
import coding.legaspi.caviteuser.data.api.TMDBService
import coding.legaspi.caviteuser.utils.SharedPreferences
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetModule(private val baseUrl: String) {

    @Singleton
    @Provides
    fun provideRetrofit(context: Context):Retrofit{
        //val (token, userid) = SharedPreferences().checkToken(context)
        //Log.d("NetModule", "$token")
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
//                    .addInterceptor { chain ->
//                        val request = chain.request().newBuilder()
//                            .addHeader("Authorization", "Bearer $token")
//                            .build()
//                        chain.proceed(request)
//                    }
                    .build()
            )
            .build()
    }

    @Singleton
    @Provides
    fun provideTMDBService(retrofit: Retrofit): TMDBService {
        return retrofit.create(TMDBService::class.java)
    }
}