package com.whitbread.premierinn.di

import com.whitbread.premierinn.businessbooker.data.remote.BusinessAccountApi
import com.whitbread.premierinn.businessbooker.data.remote.BusinessBookerLoginApi
import com.whitbread.premierinn.businessbooker.data.remote.CompanyApi
import com.whitbread.premierinn.data.remote.AccountApi
import com.whitbread.premierinn.data.remote.DashboardApi
import com.whitbread.premierinn.data.remote.PlacesApi
import com.whitbread.premierinn.data.remote.ReservationApi
import com.whitbread.premierinn.data.remote.SearchItemApi
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {

    @Provides
    @Singleton
    fun provideWBGraphQLServicesApi(@GraphQLRetrofit retrofit: Retrofit): WBGraphQLServicesApi {
        return retrofit.create(WBGraphQLServicesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNonRxGraphQLServicesApi(@NonRxGraphQLRetrofit retrofit: Retrofit): NonRxGraphQLServicesApi {
        return retrofit.create(NonRxGraphQLServicesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSearchItemApi(@SnowdropRetrofit retrofit: Retrofit): SearchItemApi {
        return retrofit.create(SearchItemApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReservationApi(@MicroServiceRetrofit retrofit: Retrofit): ReservationApi{
        return retrofit.create(ReservationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGoogleMapsApi(@GoogleMapsRetrofit retrofit: Retrofit): PlacesApi{
        return retrofit.create(PlacesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAccountApi(@MicroServiceRetrofit retrofit: Retrofit): AccountApi{
        return retrofit.create(AccountApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDashboardApi(@MicroServiceRetrofit retrofit: Retrofit): DashboardApi{
        return retrofit.create(DashboardApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginApi(@MicroServiceRetrofit retrofit: Retrofit): BusinessBookerLoginApi{
        return retrofit.create(BusinessBookerLoginApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBusinessAccountApi(@MicroServiceRetrofit retrofit: Retrofit): BusinessAccountApi{
        return retrofit.create(BusinessAccountApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCompanyApi(@MicroServiceRetrofit retrofit: Retrofit): CompanyApi{
        return retrofit.create(CompanyApi::class.java)
    }
}