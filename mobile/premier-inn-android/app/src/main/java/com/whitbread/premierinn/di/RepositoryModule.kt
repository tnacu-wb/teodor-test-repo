package com.whitbread.premierinn.di

import android.content.SharedPreferences
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl
import com.whitbread.premierinn.businessbooker.data.company.CompanyRepositoryImpl
import com.whitbread.premierinn.businessbooker.data.customer.repository.BusinessCustomerRepositoryImpl
import com.whitbread.premierinn.businessbooker.data.remote.BusinessAccountApi
import com.whitbread.premierinn.businessbooker.data.remote.CompanyApi
import com.whitbread.premierinn.businessbooker.domain.company.CompanyRepository
import com.whitbread.premierinn.businessbooker.domain.customer.repository.BusinessCustomerRepository
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.alternativeroom.repository.AlternativeRoomRepositoryImpl
import com.whitbread.premierinn.data.apprating.AppRatingRepositoryImpl
import com.whitbread.premierinn.data.booking.dao.BookingDao
import com.whitbread.premierinn.data.booking.repository.BookingRepositoryImpl
import com.whitbread.premierinn.data.common.AppPackageDetails
import com.whitbread.premierinn.data.common.DatabaseTransactionRunner
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.countries.CountriesRepositoryImpl
import com.whitbread.premierinn.data.customer.repository.CustomerRepositoryImpl
import com.whitbread.premierinn.data.dashboard.dao.DashboardDao
import com.whitbread.premierinn.data.dashboard.repository.DashboardRepositoryImpl
import com.whitbread.premierinn.data.graphql.ForgotPasswordRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLAmendRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLAttachFileToReservationRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLAuthorizeCardRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLBookingDetailsRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLCombinedBusinessRestrictionsRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLCountriesRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLFindBookingRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLGuestDetailsRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLHDPRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLHoldBookingRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLHomePageAppsContentRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLHotelDetailsRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLHotelPreferencesRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLMarketingPreferencesRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLMyAccountSaveCardRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLMyBookingsRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLPackagesRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLPaymentMethodsRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLPreCheckInConfirmationRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLPreCheckInRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLPreCheckOutConfirmationRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLPreStayInfoRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLPromotionsInformationRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLReviewBookingRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLRoomClassConfigRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLSRPRepositoryImpl
import com.whitbread.premierinn.data.graphql.GraphQLSummaryRepositoryImpl
import com.whitbread.premierinn.data.location.repository.LocationRepositoryImpl
import com.whitbread.premierinn.data.recentsearch.dao.RecentSearchDao
import com.whitbread.premierinn.data.recentsearch.repository.RecentSearchRepositoryImpl
import com.whitbread.premierinn.data.remote.AccountApi
import com.whitbread.premierinn.data.remote.DashboardApi
import com.whitbread.premierinn.data.remote.PlacesApi
import com.whitbread.premierinn.data.remote.ReservationApi
import com.whitbread.premierinn.data.remote.SearchItemApi
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.reservation.AmendedReservationRepositoryImpl
import com.whitbread.premierinn.data.reservation.dao.AmendedReservationDao
import com.whitbread.premierinn.data.resource.ContentManagedResourceRepositoryImpl
import com.whitbread.premierinn.data.roombreakdown.RoomBreakdownDao
import com.whitbread.premierinn.data.roomcriteria.RoomCriteriaDao
import com.whitbread.premierinn.data.roomguest.RoomGuestDao
import com.whitbread.premierinn.data.roomupsell.RoomUpsellDao
import com.whitbread.premierinn.data.search.dao.SearchEntityDao
import com.whitbread.premierinn.data.search.repository.SearchItemRepositoryImpl
import com.whitbread.premierinn.data.upsellavailable.UpsellItemAvailableDao
import com.whitbread.premierinn.domain.alternativeroom.repository.AlternativeRoomRepository
import com.whitbread.premierinn.domain.apprating.repository.AppRatingRepository
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.common.hoteldetails.repository.GraphQLHotelDetailsRepository
import com.whitbread.premierinn.domain.countries.repository.CountriesRepository
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import com.whitbread.premierinn.domain.dashboard.repository.DashboardRepository
import com.whitbread.premierinn.domain.graphql.amend.repository.GraphQLAmendRepository
import com.whitbread.premierinn.domain.graphql.bookingDetails.repository.GraphQLBookingDetailsRepository
import com.whitbread.premierinn.domain.graphql.businessRestrictions.repository.GraphQLCombinedBusinessRestrictionsRepository
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLAttachFileToReservationRepository
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLAuthorizeCardRepository
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLHotelPreferencesRepository
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPackagesRepository
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPaymentMethodsRepository
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPreCheckInConfirmationRepository
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPreCheckInRepository
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPreCheckOutConfirmationRepository
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPreStayInfoRepository
import com.whitbread.premierinn.domain.graphql.common.GraphQLHomePageAppsContentRepository
import com.whitbread.premierinn.domain.graphql.common.repository.GraphQLHoldBookingRepository
import com.whitbread.premierinn.domain.graphql.countries.repository.GraphQLCountriesRepository
import com.whitbread.premierinn.domain.graphql.findBooking.repository.GraphQLFindBookingRepository
import com.whitbread.premierinn.domain.graphql.forgotpassword.repository.ForgotPasswordRepository
import com.whitbread.premierinn.domain.graphql.guestDetails.repository.GraphQLGuestDetailsRepository
import com.whitbread.premierinn.domain.graphql.hdp.repository.GraphQLHDPRepository
import com.whitbread.premierinn.domain.graphql.marketingPreferences.repository.GraphQLMarketingPreferencesRepository
import com.whitbread.premierinn.domain.graphql.myAccount.repository.GraphQLMyAccountSaveCardRepository
import com.whitbread.premierinn.domain.graphql.myBookings.repository.GraphQLMyBookingsRepository
import com.whitbread.premierinn.domain.graphql.promotions.repository.GraphQLPromotionsInformationRepository
import com.whitbread.premierinn.domain.graphql.reviewBooking.repository.GraphQLReviewBookingRepository
import com.whitbread.premierinn.domain.graphql.roomClassConfig.repository.GraphQLRoomClassConfigRepository
import com.whitbread.premierinn.domain.graphql.srp.repository.GraphQLSRPRepository
import com.whitbread.premierinn.domain.graphql.summary.repository.GraphQLSummaryRepository
import com.whitbread.premierinn.domain.location.repository.LocationRepository
import com.whitbread.premierinn.domain.recentsearch.repository.RecentSearchRepository
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.search.repository.SearchItemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.json.JSONObject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCustomerRepository(
        accountApi: AccountApi,
        preferences: SimplePersistenceManager,
        @Named("AkamaiSensorData") sensorData: Provider<String>
    ): CustomerRepository {
        return CustomerRepositoryImpl(accountApi, preferences, sensorData)
    }

    @Provides
    @Singleton
    fun provideBusinessPersistenceManager(
        gson: Gson,
        preferences: SharedPreferences
    ): BusinessPersistenceManager {
        return BusinessPersistenceManagerImpl(gson, preferences)
    }

    @Provides
    @Singleton
    fun provideBusinessCustomerRepository(
        businessAccountApi: BusinessAccountApi,
        businessPersistenceManager: BusinessPersistenceManager,
        simplePersistenceManager: SimplePersistenceManager,
        deviceLocaleProvider: DeviceLocaleProvider,
        @Named("AkamaiSensorData") sensorData: Provider<String>
    ): BusinessCustomerRepository {
        return BusinessCustomerRepositoryImpl(
            businessAccountApi,
            businessPersistenceManager,
            simplePersistenceManager,
            deviceLocaleProvider,
            sensorData
        )
    }

    @Provides
    @Singleton
    fun provideContentManagedResourceRepository(
        remoteConfig: FirebaseRemoteConfig,
        persistenceManager: SimplePersistenceManager
    ): ContentManagedResourceRepository {
        return ContentManagedResourceRepositoryImpl(remoteConfig, persistenceManager)
    }

    @Provides
    @Singleton
    fun provideBookingRepository(
        businessAccountApi: BusinessAccountApi,
        dao: BookingDao,
        simplePersistenceManager: SimplePersistenceManager,
        errorLogger: ErrorLogger
    ): BookingRepository {
        return BookingRepositoryImpl(
            businessAccountApi,
            dao,
            simplePersistenceManager,
            errorLogger
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLHDPRepository(
        wbGraphQLServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider
    ): GraphQLHDPRepository {
        return GraphQLHDPRepositoryImpl(
            wbGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject()
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLHotelDetailsRepository(
        weGraphServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider,
        businessPersistenceManager: BusinessPersistenceManager,
    ): GraphQLHotelDetailsRepository {
        return GraphQLHotelDetailsRepositoryImpl(
            weGraphServicesApi,
            fileDataProvider,
            businessPersistenceManager,
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLAmendRepository(
        wbGraphQLServicesApi: WBGraphQLServicesApi,
        nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
        fileDataProvider: FileDataProvider,
        dispatchers: AppDispatchers
    ): GraphQLAmendRepository {
        return GraphQLAmendRepositoryImpl(
            wbGraphQLServicesApi,
            nonRxGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            dispatchers
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLSummaryRepository(
        nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
        fileDataProvider: FileDataProvider,
        authenticationRepository: AuthenticationRepository,
        isCustomerLoggedIn: IsCustomerLoggedIn,
        dispatchers: AppDispatchers
    ): GraphQLSummaryRepository {
        return GraphQLSummaryRepositoryImpl(
            nonRxGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            authenticationRepository,
            isCustomerLoggedIn,
            dispatchers
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLGuestDetailsRepository(
        wbGraphQLServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider
    ): GraphQLGuestDetailsRepository {
        return GraphQLGuestDetailsRepositoryImpl(
            wbGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject()
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLCountriesRepository(
        wbGraphQLServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider
    ): GraphQLCountriesRepository {
        return GraphQLCountriesRepositoryImpl(
            wbGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            JSONObject()
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLSRPRepository(
        wbGraphQLServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider,
        promotionRepository: GraphQLPromotionsInformationRepository
    ): GraphQLSRPRepository {
        return GraphQLSRPRepositoryImpl(
            wbGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            JSONObject(),
            promotionRepository
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLReviewBookingRepository(
        wbGraphQLServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider,
        @Named("AkamaiSensorData") sensorData: Provider<String>
    ): GraphQLReviewBookingRepository {
        return GraphQLReviewBookingRepositoryImpl(
            wbGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            sensorData
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLBookingDetailsRepository(
        wbGraphQLServicesApi: WBGraphQLServicesApi,
        dao: BookingDao,
        fileDataProvider: FileDataProvider,
        countriesRepository: CountriesRepository
    ): GraphQLBookingDetailsRepository {
        return GraphQLBookingDetailsRepositoryImpl(
            fileDataProvider,
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            wbGraphQLServicesApi,
            dao,
            countriesRepository,
            JSONObject()
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLFindBookingRepository(
        wbGraphQLServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider
    ): GraphQLFindBookingRepository {
        return GraphQLFindBookingRepositoryImpl(
            wbGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            JSONObject()
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLMyBookingsRepository(
        wbGraphQLServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider
    ): GraphQLMyBookingsRepository {
        return GraphQLMyBookingsRepositoryImpl(
            wbGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            JSONObject()
        )
    }

    @Provides
    @Singleton
    fun provideCountriesRepository(
        fileDataProvider: FileDataProvider,
        gson: Gson,
        deviceLocaleProvider: DeviceLocaleProvider,
        simplePersistenceManager: SimplePersistenceManager
    ): CountriesRepository {
        return CountriesRepositoryImpl(
            deviceLocaleProvider,
            simplePersistenceManager,
            fileDataProvider,
            gson
        )
    }

    @Provides
    @Singleton
    fun provideRatingRepository(
        simplePersistenceManager: SimplePersistenceManager
    ): AppRatingRepository {
        return AppRatingRepositoryImpl(simplePersistenceManager)
    }

    @Provides
    @Singleton
    fun provideAmendedReservationRepository(
        simplePersistenceManager: SimplePersistenceManager,
        businessPersistenceManager: BusinessPersistenceManager,
        amendDao: AmendedReservationDao,
        roomDao: RoomCriteriaDao,
        upsellItemAvailableDao: UpsellItemAvailableDao,
        guestDao: RoomGuestDao,
        roomUpsellDao: RoomUpsellDao,
        bookingDao: BookingDao,
        roomBreakdownDao: RoomBreakdownDao,
        api: ReservationApi,
        runner: DatabaseTransactionRunner,
        errorLogger: ErrorLogger,
        deviceLocaleProvider: DeviceLocaleProvider,
        fileDataProvider: FileDataProvider,
        wbGraphQLServicesApi: WBGraphQLServicesApi,
        graphQLAmendRepository: GraphQLAmendRepository,
        bookingRepository: BookingRepository,
        countriesRepository: CountriesRepository
    ): AmendedReservationRepository {
        return AmendedReservationRepositoryImpl(
            simplePersistenceManager,
            businessPersistenceManager,
            amendDao,
            roomDao,
            upsellItemAvailableDao,
            guestDao,
            roomUpsellDao,
            bookingDao,
            roomBreakdownDao,
            runner,
            api,
            deviceLocaleProvider,
            errorLogger,
            graphQLAmendRepository,
            fileDataProvider,
            JSONObject(),
            JSONObject(),
            JSONObject(),
            wbGraphQLServicesApi,
            bookingRepository,
            countriesRepository
        )
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        placesApi: PlacesApi,
        @Named("PlacesApiKey") apiKey: String,
        details: AppPackageDetails
    ): LocationRepository {
        return LocationRepositoryImpl(
            placesApi,
            apiKey,
            details
        )
    }

    @Provides
    @Singleton
    fun provideDashboardRepository(
        dashboardApi: DashboardApi,
        dashboardDao: DashboardDao,
        logService: LogService,
        deviceLocaleProvider: DeviceLocaleProvider
    ): DashboardRepository {
        return DashboardRepositoryImpl(
            dashboardApi,
            dashboardDao,
            logService,
            deviceLocaleProvider
        )
    }

    @Provides
    @Singleton
    fun providesRecentSearchRepository(
        recentSearchDao: RecentSearchDao
    ): RecentSearchRepository {
        return RecentSearchRepositoryImpl(recentSearchDao)
    }

    @Provides
    @Singleton
    fun providesCompanyRepository(
        companyApi: CompanyApi,
        businessPersistenceManager: BusinessPersistenceManager,
        errorLogger: ErrorLogger,
        deviceLocaleProvider: DeviceLocaleProvider,
        @Named("AkamaiSensorData") sensorData: Provider<String>
    ): CompanyRepository {
        return CompanyRepositoryImpl(
            companyApi,
            businessPersistenceManager,
            errorLogger,
            deviceLocaleProvider,
            sensorData
        )
    }

    @Provides
    @Singleton
    fun provideAlternativeRoomRepository(
        contentManagerResourceRepository: ContentManagedResourceRepository,
        gson: Gson
    ): AlternativeRoomRepository {
        return AlternativeRoomRepositoryImpl(contentManagerResourceRepository, gson)
    }

    @Provides
    @Singleton
    fun providePackagesRepository(
        nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
        fileDataProvider: FileDataProvider,
        dispatchers: AppDispatchers
    ): GraphQLPackagesRepository {
        return GraphQLPackagesRepositoryImpl(
            nonRxGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            JSONObject(),
            dispatchers
        )
    }

    @Provides
    @Singleton
    fun providePreStayInfoRepository(
        nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
        fileDataProvider: FileDataProvider,
        dispatchers: AppDispatchers
    ): GraphQLPreStayInfoRepository {
        return GraphQLPreStayInfoRepositoryImpl(
            nonRxGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            JSONObject(),
            dispatchers
        )
    }

    @Provides
    @Singleton
    fun providePreCheckInConfirmationRepository(
        nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
        fileDataProvider: FileDataProvider,
        dispatchers: AppDispatchers
    ): GraphQLPreCheckInConfirmationRepository {
        return GraphQLPreCheckInConfirmationRepositoryImpl(
            nonRxGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            dispatchers
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLPreCheckOutConfirmationRepository(
        nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
        fileDataProvider: FileDataProvider,
        dispatchers: AppDispatchers
    ): GraphQLPreCheckOutConfirmationRepository {
        return GraphQLPreCheckOutConfirmationRepositoryImpl(
            nonRxGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            dispatchers
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLHotelPreferencesRepository(
        nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
        fileDataProvider: FileDataProvider,
        dispatchers: AppDispatchers
    ): GraphQLHotelPreferencesRepository {
        return GraphQLHotelPreferencesRepositoryImpl(
            nonRxGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            dispatchers
        )
    }

    @Provides
    @Singleton
    fun provideSimplePackagesRepository(
        nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
        authenticationRepository: AuthenticationRepository,
        isCustomerLoggedIn: IsCustomerLoggedIn,
        fileDataProvider: FileDataProvider,
        dispatchers: AppDispatchers
    ): GraphQLPaymentMethodsRepository {
        return GraphQLPaymentMethodsRepositoryImpl(
            nonRxGraphQLServicesApi,
            authenticationRepository,
            isCustomerLoggedIn,
            fileDataProvider,
            JSONObject(),
            JSONObject(),
            dispatchers
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLAuthorizeCardRepository(
        nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
        fileDataProvider: FileDataProvider,
        dispatchers: AppDispatchers
    ): GraphQLAuthorizeCardRepository {
        return GraphQLAuthorizeCardRepositoryImpl(
            nonRxGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            dispatchers
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLAttachFileToReservationRepository(
        nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
        fileDataProvider: FileDataProvider,
        dispatchers: AppDispatchers
    ): GraphQLAttachFileToReservationRepository {
        return GraphQLAttachFileToReservationRepositoryImpl(
            nonRxGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            dispatchers
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLPreCheckInRepository(
        nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
        fileDataProvider: FileDataProvider,
        dispatchers: AppDispatchers
    ): GraphQLPreCheckInRepository {
        return GraphQLPreCheckInRepositoryImpl(
            nonRxGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            dispatchers
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLMyAccountSaveCardRepository(
        wbGraphServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider
    ): GraphQLMyAccountSaveCardRepository {
        return GraphQLMyAccountSaveCardRepositoryImpl(
            wbGraphServicesApi,
            fileDataProvider,
            JSONObject(),
            JSONObject()
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLHomePageAppsContentRepository(
        wbGraphServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider
    ): GraphQLHomePageAppsContentRepository {
        return GraphQLHomePageAppsContentRepositoryImpl(
            wbGraphServicesApi,
            fileDataProvider,
            JSONObject()
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLRoomClassConfigRepository(
        wbGraphServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider
    ): GraphQLRoomClassConfigRepository {
        return GraphQLRoomClassConfigRepositoryImpl(
            wbGraphServicesApi,
            fileDataProvider,
            JSONObject(),
            JSONObject()
        )
    }

    @Provides
    @Singleton
    fun providePromotionsInformationRepository(
        wbGraphServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider
    ): GraphQLPromotionsInformationRepository {
        return GraphQLPromotionsInformationRepositoryImpl(
            wbGraphServicesApi,
            fileDataProvider
        )
    }

    @Provides
    @Singleton
    fun provideSearchItemRepository(
        searchItemApi: SearchItemApi,
        resourceRepository: ContentManagedResourceRepository,
        gson: Gson,
        dao: SearchEntityDao
    ): SearchItemRepository {
        return SearchItemRepositoryImpl(searchItemApi, resourceRepository, gson, dao)
    }

    @Provides
    @Singleton
    fun provideGraphQLCombinedBusinessRestrictionsRepository(
        wbGraphQLServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider
    ): GraphQLCombinedBusinessRestrictionsRepository {
        return GraphQLCombinedBusinessRestrictionsRepositoryImpl(
            fileDataProvider,
            JSONObject(),
            wbGraphQLServicesApi
        )
    }

    @Provides
    @Singleton
    fun provideForgotPasswordRepository(
        wbGraphQLServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider
    ): ForgotPasswordRepository {
        return ForgotPasswordRepositoryImpl(wbGraphQLServicesApi, fileDataProvider)
    }

    @Provides
    @Singleton
    fun provideGraphQLHoldBookingRepository(
        weGraphServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider,
        @Named("AkamaiSensorData") sensorData: Provider<String>
    ): GraphQLHoldBookingRepository {
        return GraphQLHoldBookingRepositoryImpl(
            weGraphServicesApi,
            fileDataProvider,
            JSONObject(),
            JSONObject(),
            JSONObject(),
            JSONObject(),
            sensorData
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLMarketingPreferencesRepository(
        wbGraphQLServicesApi: WBGraphQLServicesApi,
        authenticationRepository: AuthenticationRepository,
        deviceLocaleProvider: DeviceLocaleProvider,
        fileDataProvider: FileDataProvider
    ): GraphQLMarketingPreferencesRepository {
        return GraphQLMarketingPreferencesRepositoryImpl(
            wbGraphQLServicesApi,
            authenticationRepository,
            deviceLocaleProvider,
            fileDataProvider,
            JSONObject(),
            JSONObject()
        )
    }

    @Provides
    @Singleton
    fun provideGraphQLAnonymousNewsletterPreferencesRepository(
        wbGraphQLServicesApi: WBGraphQLServicesApi,
        fileDataProvider: FileDataProvider
    ): com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.repository.GraphQLAnonymousNewsletterPreferencesRepository {
        return com.whitbread.premierinn.data.graphql.GraphQLAnonymousNewsletterPreferencesRepositoryImpl(
            wbGraphQLServicesApi,
            fileDataProvider,
            JSONObject(),
            JSONObject()
        )
    }
}