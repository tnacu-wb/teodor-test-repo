package com.whitbread.premierinn.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.whitbread.premierinn.BuildConfig
import com.whitbread.premierinn.amend.AmendStringProvider
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.analytics.FirebaseLogger
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.pushNotification.PushNotificationFactory
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.service.OfflineDetectorService
import com.whitbread.premierinn.common.utils.ResourceUtils
import com.whitbread.premierinn.common.utils.qrBitmap.QRCodeCreateBitmap
import com.whitbread.premierinn.common.utils.qrBitmap.QrCodeCreateBitmapImpl
import com.whitbread.premierinn.data.common.AppPackageDetails
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.AdobeABPersistenceManager
import com.whitbread.premierinn.data.common.persistence.AdobeABPersistenceManagerImpl
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl
import com.whitbread.premierinn.data.qrcode.QRCodeGeneratorImpl
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.qrcode.QRCodeGenerator
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.ForceUpdateRequired
import com.whitbread.premierinn.domain.resource.usecase.SyncRemoteResources
import com.whitbread.premierinn.domain.search.repository.SearchItemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideLogService(): LogService {
        return LogService()
    }

    @Provides
    @Singleton
    fun provideOfflineDetectorService(context: Context): OfflineDetectorService {
        return OfflineDetectorService(context)
    }

    @Provides
    @Singleton
    fun provideAppPackageDetails(
        context: Context,
        logService: LogService
    ): AppPackageDetails {
        return AppPackageDetails(
            context.getPackageName(),
            ResourceUtils.getSignature(context, logService),
            BuildConfig.VERSION_CODE
        )
    }

    @Provides
    @Singleton
    fun provideErrorLogger(): ErrorLogger {
        return LogService()
    }

    @Provides
    @Singleton
    fun provideQRCodeGenerator(): QRCodeGenerator {
        return QRCodeGeneratorImpl()
    }

    @Provides
    @Singleton
    fun provideQRCodeCreateBitmap(): QRCodeCreateBitmap {
        return QrCodeCreateBitmapImpl()
    }

    @Provides
    @Singleton
    fun provideIsFeatureOn(repository: ContentManagedResourceRepository): IsFeatureOn {
        return IsFeatureOn(repository)
    }

    @Provides
    @Singleton
    fun provideDeviceProvider(context: Context): DeviceLocaleProvider {
        return DeviceLocaleProvider(context)
    }

    @Provides
    @Singleton
    fun provideAmendStringProvider(context: Context, deviceLocaleProvider: DeviceLocaleProvider): AmendStringProvider {
        return AmendStringProvider(context, deviceLocaleProvider)
    }

    @Provides
    @Singleton
    fun provideAdobeABPersistenceManager(sharedPreferences: SharedPreferences): AdobeABPersistenceManager {
        return AdobeABPersistenceManagerImpl(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideSimplePersistenceManager(gson: Gson, preference: SharedPreferences, @Named("SharedPrefCrypto") sharedPrefCrypto: SharedPreferences): SimplePersistenceManager {
        return SimplePersistenceManagerImpl(gson, preference, sharedPrefCrypto)
    }

    @Provides
    @Singleton
    fun provideForceUpdateUseCase(
        appPackageDetails: AppPackageDetails,
        syncRemoteResources: SyncRemoteResources,
        repository: ContentManagedResourceRepository
    ): ForceUpdateRequired {
        return ForceUpdateRequired(appPackageDetails.versionCode, syncRemoteResources, repository)
    }

    @Provides
    @Singleton
    fun providePushNotificationFactory(
        firebaseLogger: FirebaseLogger,
        trackingAnalytics: TrackingAnalytics,
        forceUpdateRequired: ForceUpdateRequired,
        deviceLocaleProvider: DeviceLocaleProvider,
        appConfiguration: AppConfiguration,
        searchItemRepository: SearchItemRepository
    ): PushNotificationFactory {
        return PushNotificationFactory(
            firebaseLogger,
            trackingAnalytics,
            forceUpdateRequired,
            deviceLocaleProvider,
            appConfiguration,
            searchItemRepository
        )
    }
}