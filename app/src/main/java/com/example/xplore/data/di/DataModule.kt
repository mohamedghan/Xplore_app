package com.example.xplore.data.di

import com.example.xplore.data.ApiAuthRepository
import com.example.xplore.data.AuthApiService
import com.example.xplore.data.AuthInterceptor
import com.example.xplore.data.AuthRepository
import com.example.xplore.data.DefaultMyFeedRepository
import com.example.xplore.data.FakeAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.example.xplore.data.MyFeedRepository
import com.example.xplore.data.JourneyResponse
import com.example.xplore.data.JourneysApiService
import com.example.xplore.data.JourneysResponse
import com.example.xplore.data.Point
import com.example.xplore.data.TokenManager
import com.example.xplore.data.User
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Singleton
    @Binds
    fun bindsMyModelRepository(
        myModelRepository: DefaultMyFeedRepository
       // myModelRepository: FakeMyFeedRepository
    ): MyFeedRepository

    @Singleton
    @Binds
    fun bindsAuthRepository(
        myauthRepository: ApiAuthRepository // TODO: use real repo
        //myauthRepository: FakeAuthRepository
    ): AuthRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DataDModule {



    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }


    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            //.baseUrl("http://10.0.2.2:3000/")
            .baseUrl("https://betaserver-337136438252.europe-west8.run.app")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }


    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService = retrofit.create(AuthApiService::class.java)


    @Provides
    @Singleton
    fun provideJourneyApiService(retrofit: Retrofit): JourneysApiService = retrofit.create(JourneysApiService::class.java)

}