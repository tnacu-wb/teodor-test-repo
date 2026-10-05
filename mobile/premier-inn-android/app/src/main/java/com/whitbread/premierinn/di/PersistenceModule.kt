package com.whitbread.premierinn.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.securepreferences.SecurePreferences
import com.whitbread.premierinn.BuildConfig
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.CONFIRMATION_POLLING_DELAY
import com.whitbread.premierinn.common.CONFIRMATION_POLLING_INTERVAL
import com.whitbread.premierinn.common.POLLING_DELAY_3CP
import com.whitbread.premierinn.common.POLLING_INTERVAL_3CP
import com.whitbread.premierinn.common.POLLING_RETRIES_3CP
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.data.booking.dao.BookingDao
import com.whitbread.premierinn.data.common.DatabaseTransactionRunner
import com.whitbread.premierinn.data.common.MIGRATION_10_11
import com.whitbread.premierinn.data.common.MIGRATION_11_12
import com.whitbread.premierinn.data.common.MIGRATION_12_13
import com.whitbread.premierinn.data.common.MIGRATION_13_14
import com.whitbread.premierinn.data.common.MIGRATION_14_15
import com.whitbread.premierinn.data.common.MIGRATION_15_16
import com.whitbread.premierinn.data.common.MIGRATION_16_17
import com.whitbread.premierinn.data.common.MIGRATION_17_18
import com.whitbread.premierinn.data.common.MIGRATION_18_19
import com.whitbread.premierinn.data.common.MIGRATION_19_20
import com.whitbread.premierinn.data.common.MIGRATION_20_21
import com.whitbread.premierinn.data.common.MIGRATION_1_2
import com.whitbread.premierinn.data.common.MIGRATION_2_3
import com.whitbread.premierinn.data.common.MIGRATION_3_4
import com.whitbread.premierinn.data.common.MIGRATION_4_5
import com.whitbread.premierinn.data.common.MIGRATION_5_6
import com.whitbread.premierinn.data.common.MIGRATION_6_7
import com.whitbread.premierinn.data.common.MIGRATION_7_8
import com.whitbread.premierinn.data.common.MIGRATION_8_9
import com.whitbread.premierinn.data.common.MIGRATION_9_10
import com.whitbread.premierinn.data.common.PremierInnDatabase
import com.whitbread.premierinn.data.common.addBookingLastModifiedDateTriggers
import com.whitbread.premierinn.data.common.persistence.RoomTransactionRunner
import com.whitbread.premierinn.data.dashboard.dao.DashboardDao
import com.whitbread.premierinn.data.recentsearch.dao.RecentSearchDao
import com.whitbread.premierinn.data.reservation.dao.AmendedReservationDao
import com.whitbread.premierinn.data.roombreakdown.RoomBreakdownDao
import com.whitbread.premierinn.data.roomcriteria.RoomCriteriaDao
import com.whitbread.premierinn.data.roomguest.RoomGuestDao
import com.whitbread.premierinn.data.roomupsell.RoomUpsellDao
import com.whitbread.premierinn.data.search.dao.SearchEntityDao
import com.whitbread.premierinn.data.upsellavailable.UpsellItemAvailableDao
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.IOException
import java.security.GeneralSecurityException
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PersistenceModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        // TODO replace this lib when androidx.security:security-crypto:$security_version becomes stable
        //  https://developer.android.com/jetpack/androidx/releases/security
        return SecurePreferences(context, "they forced me to do that, help!", "secure_prefs")
    }

    @Provides
    @Singleton
    @Named("SharedPrefCrypto")
    fun provideSecureSharedPreferencesCrypto(
        context: Context,
        logger: LogService
    ): SharedPreferences {
        val mainKey: MasterKey?
        return try {
            mainKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                NEW_ENCRYPTED_SHARED_PREF_FILE,
                mainKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: GeneralSecurityException) {
            logger.logException(e, e.message)
            throw Exception()
        } catch (e: IOException) {
            logger.logException(e, e.message)
            throw Exception()
        }
    }

    @Provides
    @Singleton
    fun provideDataBase(context: Context): PremierInnDatabase {
        val builder: RoomDatabase.Builder<*> = databaseBuilder(
            context,
            PremierInnDatabase::class.java,
            DB_FILENAME
        )
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_5_6,
                MIGRATION_6_7,
                MIGRATION_7_8,
                MIGRATION_8_9,
                MIGRATION_9_10,
                MIGRATION_10_11,
                MIGRATION_11_12,
                MIGRATION_12_13,
                MIGRATION_13_14,
                MIGRATION_14_15,
                MIGRATION_15_16,
                MIGRATION_16_17,
                MIGRATION_17_18,
                MIGRATION_18_19,
                MIGRATION_19_20,
                MIGRATION_20_21
            )

        if (BuildConfig.STAGING) {
            builder.fallbackToDestructiveMigration()
        }

        builder.addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                addBookingLastModifiedDateTriggers(db)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
            }
        })

        return builder.build() as PremierInnDatabase
    }

    @Provides
    @Singleton
    fun provideSearchEntityDao(database: PremierInnDatabase): SearchEntityDao {
        return database.searchEntityDao()
    }

    @Provides
    @Singleton
    fun provideBookingDao(database: PremierInnDatabase): BookingDao {
        return database.bookingEntityDao()
    }

    @Provides
    @Singleton
    fun provideAmendReservationDao(database: PremierInnDatabase): AmendedReservationDao {
        return database.amendReservationDao()
    }

    @Provides
    @Singleton
    fun provideRoomCriteriaDao(database: PremierInnDatabase): RoomCriteriaDao {
        return database.roomCriteriaDao()
    }

    @Provides
    @Singleton
    fun provideRoomGuestDao(database: PremierInnDatabase): RoomGuestDao {
        return database.roomGuestDao()
    }

    @Provides
    @Singleton
    fun provideRoomUpsellDao(database: PremierInnDatabase): RoomUpsellDao {
        return database.roomUpsellDao()
    }

    @Provides
    @Singleton
    fun provideRoomBreakdownDao(database: PremierInnDatabase): RoomBreakdownDao {
        return database.roomBreakdownDao()
    }

    @Provides
    @Singleton
    fun upsellItemAvailableDao(database: PremierInnDatabase): UpsellItemAvailableDao {
        return database.upsellItemAvailableDao()
    }

    @Provides
    @Singleton
    fun provideDashboardDao(database: PremierInnDatabase): DashboardDao {
        return database.dashboardDao()
    }

    @Provides
    @Singleton
    fun provideRecentSearchDao(database: PremierInnDatabase): RecentSearchDao {
        return database.recentSearchDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(context: Context): FirebaseRemoteConfig {
        FirebaseApp.initializeApp(context)
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setConfigSettingsAsync(FirebaseRemoteConfigSettings.Builder().build())

        remoteConfig.setDefaultsAsync(object : HashMap<String?, Any?>() {
            init {
                put(
                    ContentManagedResourceRepository.Key.MINIMUM_SUPPORTED_VERSION.value,
                    BuildConfig.VERSION_CODE
                )
                put(
                    ContentManagedResourceRepository.Key.FORCE_UPDATE_TITLE.value,
                    context.getString(
                        R.string.force_update_title_fallback
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.FORCE_UPDATE_DESCRIPTION.value,
                    context.getString(
                        R.string.force_update_desc_fallback
                    )
                )

                put(ContentManagedResourceRepository.Key.BART_DOWN.value, false)
                put(
                    ContentManagedResourceRepository.Key.BART_DOWNTIME_INFO.value,
                    context.getString(
                        R.string.bart_down_maintenance_info
                    )
                )

                put(
                    ContentManagedResourceRepository.Key.TOP_DESTINATIONS.value,
                    context.getString(R.string.top_destinations)
                )
                put(
                    ContentManagedResourceRepository.Key.CUSTOMER_SERVICE_NUMBER.value,
                    context.getString(
                        R.string.call_view_default_customer_service_number
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.GROUP_BOOKINGS_NUMBER.value,
                    context.getString(
                        R.string.call_view_group_bookings_number
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.CHARGEABLE_PHONE_DESC.value,
                    context.getString(
                        R.string.call_view_cost
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.CALL_CUSTOMER_SERVICE_DESC.value,
                    context.getString(
                        R.string.call_view_call_us_prompt
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.NON_CHARGEABLE_PHONE_DESC.value,
                    context.getString(
                        R.string.call_view_no_cost
                    )
                )

                put(
                    ContentManagedResourceRepository.Key.BOOKING_PRIVACY_FOOTER.value,
                    context.getString(
                        R.string.booking_privacy_footer
                    )
                )

                put(
                    ContentManagedResourceRepository.Key.GDPR_PRIVACY_MESSAGE.value,
                    context.getString(
                        R.string.gdpr_privacy_message
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.GDPR_PRIVACY_FOOTER.value,
                    context.getString(
                        R.string.gdpr_privacy_footer
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.GDPR_MY_DETAILS_USAGE.value,
                    context.getString(
                        R.string.gdpr_my_details_usage
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.GDPR_DATA_USAGE.value,
                    context.getString(R.string.gdpr_data_usage)
                )
                put(
                    ContentManagedResourceRepository.Key.GDPR_PAYMENT_DETAILS_DATA_USAGE.value,
                    context.getString(
                        R.string.gdpr_payment_details_usage
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.GDPR_GUEST_DETAILS_PRIVACY.value,
                    context.getString(
                        R.string.gdpr_guest_details_usage
                    )
                )

                // TODO Rate names and descriptions to be removed as part of this https://whitbreadis.atlassian.net/browse/MON-2136
                put(
                    ContentManagedResourceRepository.Key.RATE_FLEX_NAME.value,
                    context.getString(R.string.flex_value)
                )
                put(
                    ContentManagedResourceRepository.Key.RATE_FLEX_DESC.value,
                    context.getString(R.string.flex_desc)
                )
                put(
                    ContentManagedResourceRepository.Key.RATE_SEMI_FLEX_NAME.value,
                    context.getString(
                        R.string.semi_flex_value
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.RATE_SEMI_FLEX_DESC.value,
                    context.getString(
                        R.string.semi_flex_desc
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.RATE_SAVER_NAME.value,
                    context.getString(R.string.saver_value)
                )
                put(
                    ContentManagedResourceRepository.Key.RATE_SAVER_DESC.value,
                    context.getString(R.string.saver_desc)
                )

                // Returns every rate possible
                put(
                    ContentManagedResourceRepository.Key.RATE_CONTENT.value,
                    context.getString(R.string.rate_content)
                )

                put(ContentManagedResourceRepository.Key.FEATURE_COVID_SRP_AND_HDP.value, false)
                put(ContentManagedResourceRepository.Key.FEATURE_COVID_HOME.value, false)
                put(
                    ContentManagedResourceRepository.Key.COVID_BANNER_MESSAGE.value,
                    context.getString(
                        R.string.coronavirus_banner_message
                    )
                )

                put(
                    ContentManagedResourceRepository.Key.PASSWORD_VALIDATOR.value,
                    context.getString(
                        R.string.password_validator
                    )
                )

                put(
                    ContentManagedResourceRepository.Key.FEATURE_COVID_NOTIFICATION_MESSAGE.value,
                    context.getString(
                        R.string.notification_message
                    )
                )

                put(
                    ContentManagedResourceRepository.Key.TWIN_ROOM_INFO.value,
                    context.getString(R.string.twin_room_info)
                )

                // Business booker key
                put(ContentManagedResourceRepository.Key.FEATURE_BUSINESS_BOOKER_QA.value, false)

                put(
                    ContentManagedResourceRepository.Key.CHECK_IN_CHECK_OUT_TIME.value,
                    context.getString(
                        R.string.checkin_checkout_info_android
                    )
                )
                put(ContentManagedResourceRepository.Key.FEATURE_CIOL.value, false)
                put(ContentManagedResourceRepository.Key.FEATURE_CIOL_UPSELLS.value, false)

                //Donations
                put(ContentManagedResourceRepository.Key.FEATURE_DONATION.value, false)
                put(
                    ContentManagedResourceRepository.Key.DONATION_DETAILS.value,
                    context.getString(R.string.donation_details)
                )
                put(
                    ContentManagedResourceRepository.Key.DONATION_PLEDGE.value,
                    context.getString(R.string.donation_pledge)
                )
                put(
                    ContentManagedResourceRepository.Key.HOTELS_WITHOUT_DONATIONS.value,
                    context.getString(
                        R.string.hotels_without_donations
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.DONATIONS.value,
                    context.getString(R.string.donations)
                )
                put(
                    ContentManagedResourceRepository.Key.DONATION_TITLE_OPERA.value,
                    context.getString(
                        R.string.donations
                    )
                )

                //3CP
                put(
                    ContentManagedResourceRepository.Key.IPAGE_COMPLETE_TRIGGER.value,
                    context.getString(
                        R.string.ipage_complete_trigger
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.IPAGE_LOADED_TRIGGER.value,
                    context.getString(
                        R.string.ipage_loaded_trigger
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.IPAGE_URL_PAYMENT_TEXT.value,
                    context.getString(
                        R.string.ipage_url_payment
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.IPAGE_3DS_COMPLETE_STRING.value,
                    context.getString(
                        R.string.ipage_3ds_complete
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.IPAGE_3DS_NOTIFICATION_TEXT.value,
                    context.getString(
                        R.string.ipage_3ds_notification
                    )
                )

                put(
                    ContentManagedResourceRepository.Key.ALL_CHECK_IN_CHECK_OUT_TIMES.value,
                    context.getString(
                        R.string.all_check_in_times_android
                    )
                )

                //PAYMENT ERRORS
                put(
                    ContentManagedResourceRepository.Key.BOOKING_PAYMENT_DECLINED.value,
                    context.getString(
                        R.string.review_booking_payment_declined
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.BOOKING_PAYMENT_NOT_FOUND.value,
                    context.getString(
                        R.string.review_booking_payment_no_payment
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.GENERAL_PAYMENT_ERROR.value,
                    context.getString(
                        R.string.review_booking_general_payment_error
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.PAYMENT_AMOUNT_CONFLICT.value,
                    context.getString(
                        R.string.review_booking_payment_amount_conflict
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.TRANSACTIONAL_REFUND_ERROR.value,
                    context.getString(
                        R.string.review_booking_transactional_refund_error
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.PAYMENT_FRAUD_CHECK.value,
                    context.getString(
                        R.string.payment_fraud_check_error
                    )
                )

                //PAYMENT POLLING
                put(ContentManagedResourceRepository.Key.POLLING_DELAY_3CP.value, POLLING_DELAY_3CP)
                put(
                    ContentManagedResourceRepository.Key.POLLING_INTERVAL_3CP.value,
                    POLLING_INTERVAL_3CP
                )
                put(
                    ContentManagedResourceRepository.Key.POLLING_RETRIES_3CP.value,
                    POLLING_RETRIES_3CP
                )

                // OPERA FALLBACK
                // After discussion with the team, we agreed that the reason that we need it to default to false is so that
                // when firebase is down and service is up, we should call the service
                put(
                    ContentManagedResourceRepository.Key.OPERA_FALLBACK_POPUP_DETAILS.value,
                    context.getString(
                        R.string.opera_fallback_popup_details
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.AMEND_OPERA_BOOKING_INFO.value,
                    context.getString(
                        R.string.my_bookings_amend_info_message_default
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.FEATURE_SHOULD_SHOW_AMEND_BANNER.value,
                    true
                )

                //Business
                put(
                    ContentManagedResourceRepository.Key.AMEND_OPERA_BOOKING_INFO_BUSINESS.value,
                    context.getString(R.string.my_bookings_amend_info_message_default_business)
                )
                put(
                    ContentManagedResourceRepository.Key.FEATURE_SHOULD_SHOW_AMEND_BANNER_BUSINESS.value,
                    true
                )

                put(
                    ContentManagedResourceRepository.Key.CONFIRMATION_POLLING_DELAY.value,
                    CONFIRMATION_POLLING_DELAY
                )
                put(
                    ContentManagedResourceRepository.Key.CONFIRMATION_POLLING_INTERVAL.value,
                    CONFIRMATION_POLLING_INTERVAL
                )
                put(
                    ContentManagedResourceRepository.Key.CONFIRMATION_POLLING_MESSAGES_CONFIG.value,
                    context.getString(
                        R.string.opera_confirmation_polling_message
                    )
                )

                put(
                    ContentManagedResourceRepository.Key.FEATURE_USE_GRAPHQL_AVAILABILITIES.value,
                    false
                )

                // PAYPAL
                put(
                    ContentManagedResourceRepository.Key.USE_PAYPAL_INITIATE_PAYMENT_MUTATION.value,
                    false
                )
                put(ContentManagedResourceRepository.Key.FEATURE_PAYPAL.value, false)

                // Google Pay
                put(ContentManagedResourceRepository.Key.FEATURE_GOOGLE_PAY.value, false)

                // QR Kiosk Hotels
                put(
                    ContentManagedResourceRepository.Key.QR_KIOSK_HOTELS.value,
                    context.getString(R.string.qr_kiosk_hotels)
                )

                // DeepLink
                put(ContentManagedResourceRepository.Key.FEATURE_DEEPLINK_HDP.value, false)
                put(ContentManagedResourceRepository.Key.FEATURE_DEEPLINK_SRP.value, false)
                put(ContentManagedResourceRepository.Key.FEATURE_DEEPLINK_CIOL.value, false)

                //ECI_LCO
                put(ContentManagedResourceRepository.Key.FEATURE_ALLOW_ECI_LCO.value, false)
                put(
                    ContentManagedResourceRepository.Key.ECI_LCO_BANNER_MESSAGE.value,
                    context.getString(
                        R.string.booking_details_eci_lco_banner_message
                    )
                )

                //SAVED_CARD
                put(ContentManagedResourceRepository.Key.FEATURE_ADD_NEW_CARD.value, false)

                // Employee offer
                put(ContentManagedResourceRepository.Key.FEATURE_ALLOW_EMPLOYEE_OFFER.value, false)

                // Content Square Masking
                put(ContentManagedResourceRepository.Key.FEATURE_CONTENT_SQUARE_UNMASK.value, false)

                // App Promotional Incentive
                put(
                    ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED.value,
                    false
                )
                put(
                    ContentManagedResourceRepository.Key.APP_PROMO_CONTENT.value, context.getString(
                        R.string.app_promo_content
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.APP_PROMO_DISCOUNT_TAG.value,
                    context.getString(
                        R.string.discount_tag
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.APP_PROMO_CODE.value,
                    StringUtils.EMPTY_STRING
                )

                // Free Breakfast Promotion
                put(
                    ContentManagedResourceRepository.Key.FREE_BREAKFAST_PROMO_CONTENT.value,
                    context.getString(
                        R.string.free_breakfast_promo_content
                    )
                )
                put(
                    ContentManagedResourceRepository.Key.FREE_BREAKFAST_PROMO_TAG.value,
                    context.getString(
                        R.string.free_breakfast_tag
                    )
                )
            }
        })

        return remoteConfig
    }

    @Provides
    @Singleton
    fun provideDbTransactionRunner(db: PremierInnDatabase): DatabaseTransactionRunner {
        return RoomTransactionRunner(db)
    }

    companion object {
        private const val DB_FILENAME = "premierinn.db"
        private const val NEW_ENCRYPTED_SHARED_PREF_FILE = "secure_prefs_crypto"
    }
}
