package com.whitbread.premierinn.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.patloew.rxlocation.RxLocation
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.PreferencesContentProvider
import com.whitbread.premierinn.common.akamai.PIAkamaiHelper.sensorData
import com.whitbread.premierinn.common.analytics.FirebaseLogger
import com.whitbread.premierinn.common.contentSquare.CSQMaskingRegistryHelper
import com.whitbread.premierinn.common.forms.FormInputErrorMessageProvider
import com.whitbread.premierinn.common.utils.LocationProvider
import com.whitbread.premierinn.createaccount.CreateAccountMessageProvider
import com.whitbread.premierinn.editpaymentmethods.EditPaymentMethodsMessageProvider
import com.whitbread.premierinn.landing.MessageProvider
import com.whitbread.premierinn.reviewbooking.ReviewBookingMessageProvider
import com.whitbread.premierinn.search.SearchMessageProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HelperModule {

    @Provides
    @Singleton
    @Named("PlacesApiKey")
    fun providePlacesApiKey(context: Context): String {
        return context.getString(R.string.places_api_key)
    }

    @Provides
    @Singleton
    fun provideMessageProvider(context: Context) = MessageProvider(context.resources)

    @Provides
    @Singleton
    fun provideRxLocation(context: Context): RxLocation {
        return RxLocation(context)
    }

    @Provides
    @Singleton
    fun provideLocationProvider(location: RxLocation): LocationProvider {
        return LocationProvider(location)
    }

    @Provides
    @Singleton
    fun provideCSQMaskingRegistryHelper(): CSQMaskingRegistryHelper = CSQMaskingRegistryHelper()

    @Provides
    @Singleton
    fun provideFirebaseLogger(context: Context): FirebaseLogger {
        return FirebaseLogger(FirebaseAnalytics.getInstance(context))
    }

    @Provides
    @Singleton
    fun provideCreateAccountMessageProvider(context: Context): CreateAccountMessageProvider {
        return CreateAccountMessageProvider(context)
    }

    @Provides
    @Singleton
    fun provideFormInputErrorMessageProvider(context: Context): FormInputErrorMessageProvider {
        return FormInputErrorMessageProvider(context)
    }

    @Provides
    @Singleton
    fun provideEditPaymentMethodsMessageProvider(context: Context): EditPaymentMethodsMessageProvider {
        return EditPaymentMethodsMessageProvider(context)
    }

    @Provides
    @Singleton
    fun providePreferencesContentProvider(context: Context): PreferencesContentProvider {
        return PreferencesContentProvider(context)
    }

    @Provides
    @Singleton
    fun provideReviewBookingMessageProvider(context: Context): ReviewBookingMessageProvider {
        return ReviewBookingMessageProvider(context)
    }

    @Provides
    @Singleton
    fun provideSearchMessageProvider(context: Context): SearchMessageProvider {
        return SearchMessageProvider(context)
    }

    @Provides
    @Named("AkamaiSensorData")
    fun provideAkamaiSensorData(): String {
        return sensorData()
    }
}