package com.whitbread.premierinn.di

import android.content.Context
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.whitbread.premierinn.api.Auth0Configuration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Auth0Module {

    @Provides
    @Singleton
    fun provideAuth0Account(
        auth0Configuration: Auth0Configuration
    ): Auth0{
        return Auth0(auth0Configuration.clientId, auth0Configuration.domain).apply {
            isOIDCConformant = true
        }
    }

    @Provides
    @Singleton
    fun provideAuthenticationAPIClient(
        auth0: Auth0
    ): AuthenticationAPIClient{
        return AuthenticationAPIClient(auth0)
    }

    @Provides
    @Singleton
    fun provideSecureCredentialsManager(
        context: Context,
        client: AuthenticationAPIClient
    ): SecureCredentialsManager {
        return SecureCredentialsManager(context, client, SharedPreferencesStorage(context))
    }
}
