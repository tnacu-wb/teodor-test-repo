package com.whitbread.premierinn.di

import com.whitbread.premierinn.common.AppConfiguration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    @MicroServiceRetrofit
    fun provideMicroServiceRetrofit(
        builder: Retrofit.Builder,
        appConfig: AppConfiguration
    ): Retrofit =
        builder.baseUrl(appConfig.microServicesUrl)
            .build()


    @Provides
    @Singleton
    @GraphQLRetrofit
    fun provideGraphQLRetrofit(
        builder: Retrofit.Builder,
        appConfig: AppConfiguration
    ): Retrofit =
        builder.baseUrl(appConfig.graphQLUrl)
            .build()

    @Provides
    @Singleton
    @NonRxGraphQLRetrofit
    fun provideNonRxGraphQLRetrofit(
        @Named("NonRxRetrofitClientBuilder") builder: Retrofit.Builder,
        appConfig: AppConfiguration
    ): Retrofit =
        builder.baseUrl(appConfig.graphQLUrl)
            .build()

    @Provides
    @Singleton
    @SnowdropRetrofit
    fun provideSnowdropRetrofit(
        builder: Retrofit.Builder,
        appConfig: AppConfiguration
    ): Retrofit =
        builder.baseUrl(appConfig.snowdropUrl)
            .build()

    @Provides
    @Singleton
    @GoogleMapsRetrofit
    fun provideGoogleMapsRetrofit(
        builder: Retrofit.Builder
    ): Retrofit =
        builder.baseUrl("https://places.googleapis.com/v1/")
            .build()

}