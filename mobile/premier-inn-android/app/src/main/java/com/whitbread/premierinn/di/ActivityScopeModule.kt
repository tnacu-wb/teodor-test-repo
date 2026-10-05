package com.whitbread.premierinn.di

import com.whitbread.premierinn.data.checkinonline.PriceBreakdownRepositoryImpl
import com.whitbread.premierinn.domain.ciol.repository.PriceBreakdownRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityScopeModule {

    @Provides
    @ActivityRetainedScoped
    fun providePriceBreakdownRepository(): PriceBreakdownRepository = PriceBreakdownRepositoryImpl()
}