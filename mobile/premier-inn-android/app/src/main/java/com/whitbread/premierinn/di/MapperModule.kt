package com.whitbread.premierinn.di

import com.whitbread.premierinn.bookingdetails.BookingDetailsUiModelMapper
import com.whitbread.premierinn.bookingdetails.BookingUiModelMapper
import com.whitbread.premierinn.bookingdetails.OperaBookingDetailsUiModelMapper
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.contentSquare.CSQScreenNameMapper
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.ciol.usecase.IsCheckInOnlineEnabledUseCase
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.landing.MessageProvider
import com.whitbread.premierinn.landing.SearchPayloadMapper
import com.whitbread.premierinn.landing.UiMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object MapperModule {

    @ActivityRetainedScoped
    @Provides
    fun provideCSQScreenNameMapper(): CSQScreenNameMapper = CSQScreenNameMapper()

    @ActivityRetainedScoped
    @Provides
    fun provideDetailsMapper(
        provider: StringResourceProvider,
        getStringResource: GetStringResource
    ): BookingDetailsUiModelMapper {
        return BookingDetailsUiModelMapper(provider, getStringResource)
    }

    @ActivityRetainedScoped
    @Provides
    fun provideOperaDetailsMapper(
        provider: StringResourceProvider,
        getStringResource: GetStringResource
    ): OperaBookingDetailsUiModelMapper {
        return OperaBookingDetailsUiModelMapper(provider, getStringResource)
    }

    @ActivityRetainedScoped
    @Provides
    fun provideUIMapper(messageProvider: MessageProvider, logger: ErrorLogger, deviceLocaleProvider: DeviceLocaleProvider) =
        UiMapper(messageProvider, logger, deviceLocaleProvider)

    @ActivityRetainedScoped
    @Provides
    fun provideSearchPayloadMapper() = SearchPayloadMapper

    @ActivityRetainedScoped
    @Provides
    fun providesBookingUiModelMapper(
        provider: StringResourceProvider,
        deviceLocaleProvider: DeviceLocaleProvider,
        isCheckInOnlineEnabledUseCase: IsCheckInOnlineEnabledUseCase
    ): BookingUiModelMapper {
        return BookingUiModelMapper(
            provider,
            deviceLocaleProvider,
            isCheckInOnlineEnabledUseCase
        )
    }
}