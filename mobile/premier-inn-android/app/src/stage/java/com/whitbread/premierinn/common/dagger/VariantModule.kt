package com.whitbread.premierinn.common.dagger

import android.content.Context
import android.content.SharedPreferences
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.whitbread.premierinn.api.Auth0Configuration
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.dagger.retrofitConfiguration.StagingOkHttpClientBuilder
import com.whitbread.premierinn.common.retrofitConfiguration.ConsumerType
import com.whitbread.premierinn.data.authentication.AuthenticationRepositoryImpl
import com.whitbread.premierinn.data.common.AppPackageDetails
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VariantModule {
    @Singleton
    @Provides
    fun provideConfiguration(
        preferences: SharedPreferences,
        isFeatureOn: IsFeatureOn
    ): AppConfiguration {
        return StagingConfiguration(preferences, isFeatureOn)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        context: Context,
        configuration: AppConfiguration,
        resourceRepository: ContentManagedResourceRepository,
        appPackageDetails: AppPackageDetails
    ): OkHttpClient {
        return StagingOkHttpClientBuilder(
            context,
            configuration,
            resourceRepository,
            appPackageDetails,
            ConsumerType.RX
        ).build()
    }

    @Provides
    @Singleton
    @Named("NonRxOkHttpClient")
    fun provideNonRxOkHttpClient(
        context: Context,
        configuration: AppConfiguration,
        resourceRepository: ContentManagedResourceRepository,
        appPackageDetails: AppPackageDetails
    ): OkHttpClient {
        return StagingOkHttpClientBuilder(
            context,
            configuration,
            resourceRepository,
            appPackageDetails,
            ConsumerType.NON_RX
        ).build()
    }

    @Provides
    @Singleton
    fun provideAuthenticationRepository(
        appConfiguration: AppConfiguration,
        client: AuthenticationAPIClient,
        errorLogger: ErrorLogger,
        secureCredentialsManager: SecureCredentialsManager
    ): AuthenticationRepository {
        return AuthenticationRepositoryImpl(
            client,
            secureCredentialsManager,
            errorLogger,
            appConfiguration.isLive,
            appConfiguration.isPreLive,
            appConfiguration.graphQLUrl
        )
    }

    @Provides
    @Singleton
    fun provideAuth0Configuration(appConfiguration: AppConfiguration): Auth0Configuration =
        if (appConfiguration.isPreLive || appConfiguration.isLive) {
            Auth0Configuration.Production
        } else {
            Auth0Configuration.Stage
        }
}
