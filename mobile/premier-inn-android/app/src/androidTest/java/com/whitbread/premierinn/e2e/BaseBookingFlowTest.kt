package com.whitbread.premierinn.e2e

import android.content.Context
import android.content.SharedPreferences
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.intent.Intents
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.whitbread.premierinn.R
import com.whitbread.premierinn.addextras.AddExtrasRobot
import com.whitbread.premierinn.api.Auth0Configuration
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.common.AppConfiguration
//import com.whitbread.premierinn.common.dagger.ApplicationModule
//import com.whitbread.premierinn.common.dagger.ComponentsManager
//import com.whitbread.premierinn.common.dagger.DaggerApplicationComponent
import com.whitbread.premierinn.di.PersistenceModule
//import com.whitbread.premierinn.common.dagger.ServiceModule
import com.whitbread.premierinn.common.dagger.StagingConfiguration
import com.whitbread.premierinn.common.dagger.VariantModule
import com.whitbread.premierinn.common.dagger.retrofitConfiguration.StagingOkHttpClientBuilder
import com.whitbread.premierinn.common.retrofitConfiguration.ConsumerType
import com.whitbread.premierinn.data.common.AppPackageDetails
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.domain.authentication.UserType
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.guestdetails.GuestDetailsRobot
import com.whitbread.premierinn.hoteldetails.HotelDetailsRobot
import com.whitbread.premierinn.landing.LandingActivity
import com.whitbread.premierinn.landing.LandingRobot
import com.whitbread.premierinn.login.LoginRobot
import com.whitbread.premierinn.reviewbooking.ReviewBookingRobot
import com.whitbread.premierinn.searchresults.SearchResultsRobot
import com.whitbread.premierinn.searchsuggestions.SearchSuggestionsRobot
import com.whitbread.premierinn.utils.InMemorySharedPreferences
import com.whitbread.premierinn.utils.replaceJSONTemplate
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

abstract class BaseBookingFlowTest {
    @Rule
    @JvmField
    val activityRule = ActivityTestRule(LandingActivity::class.java)

    protected lateinit var context: Context

    @Before
    fun setUp() {
        Intents.init()
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun loggedIn_User_With_BACS_Flex_Rate_Pay_On_Arrival() {
        stubApiCalls("BACS")

        executeBookingFlowFor("Flex")

        with(ReviewBookingRobot(context)) {
            verifyPayOnArrivalMessage() // todo group and rename those to verify
            verifyStandardAmendAndCancelMessage()
            verifyCvvNotDisplayed()

            clickMakeBooking()

            success("£46.50")
        }
    }

    @Test
    fun loggedIn_User_With_BACS_Saver_Rate_Pay_On_Arrival() {
        stubApiCalls("BACS")
        executeBookingFlowFor("Non-Flex")

        with(ReviewBookingRobot(context)) {
            verifyPayOnArrivalMessage()
            verifyCancellationMessageWithin(24)
            verifyNoAmendmentsMessage()
            verifyCvvNotDisplayed()

            verifyCvvNotDisplayed()
            clickMakeBooking()

            success("£58.00")
        }
    }

    @Test
    fun loggedIn_User_With_VISA_Saver_Rate_Pay_Now() {
        stubApiCalls("VISA")

        executeBookingFlowFor("Non-Flex")

        with(ReviewBookingRobot(context)) {
            verifyCancellationMessageWithin(24)
            verifyNoAmendmentsAllowedMessage()
            verifyCvvIsDisplayed()

            enterCvv(123)
            clickMakeBooking()

            //TODO: 3DS Try and Convert to 3CP
            verify3dsIsDisplayed()
            //TODO finish interaction with WebView and verifySuccess payNow
        }
    }

    @Test
    fun loggedIn_User_Flex_Rate_Chooses_To_Pay_Now() {
        stubApiCalls("VISA")

        executeBookingFlowFor("Flex")

        with(ReviewBookingRobot(context)) {
            selectsPayNow()

            verifyStandardAmendAndCancelMessage()
            verifyCvvIsDisplayed()

            enterCvv(123)
            clickMakeBooking()

            //TODO: 3DS Try and Convert to 3CP
            verify3dsIsDisplayed()
            //TODO finish interaction with WebView and verifySuccess payNow
        }
    }

    //TODO: 3DS Try and Convert to 3CP
    @Test
    fun loggedIn_User_Saver_With_AMEX_Rate_Pay_Now_3ds_Not_Triggered() {
        stubApiCalls("AMEX")

        executeBookingFlowFor("Non-Flex")

        with(ReviewBookingRobot(context)) {
            verifyCancellationMessageWithin(24)
            verifyNoAmendmentsAllowedMessage()
            verifyCvvIsDisplayed()

            enterCvv(1234)
            clickMakeBooking()
            successFullyPaid()
        }
    }

    protected fun executeBookingFlowFor(rateName: String) {
        with(LandingRobot()) {
            openSearchSuggestions()
        }

        with(SearchSuggestionsRobot()) {
            select("Belfast")
        }

        with((LandingRobot())) {
            searchForHotels()
        }

        with(SearchResultsRobot()) {
            chooseHotelResult(0)
        }

        with(HotelDetailsRobot()) {
            chooseStandardRate(rateName)
        }

        with(AddExtrasRobot()) {
            continueWithDefaults()
        }

        with(LoginRobot()) {
            enterUsername("test@gmail.com")
            enterPassword("Password1")
            login()
        }

        with(GuestDetailsRobot()) {
            continueWithDetails()
        }
    }


    protected fun stubApiCalls(cardType: String = "") {
        // TODO Refine the dynamic logic including the rateType so that makes more sense.

        val hotelCode = "BELFAS"
        val availabilitySuffix = "availability_1ad_standard_rates_flex_non_flex_ok"

        val customerJsonResponse = when (cardType) {
            "AMEX" -> "customer_test_with_AMEX.json"
            "VISA" -> "customer_test_with_VISA.json"
            "BACS" -> "customer_test_with_BACS.json"
            else -> "customer_test_with_VISA.json"
        }

        val threeDSJsonResponse = when (cardType) {
            "AMEX" -> "three_d_s_not_required.json"
            else -> "three_d_s_required.json"
        }

        // AMEX used just as an example to enable prepayment booking completed flow.
        val bookingCompleteJsonResponse = when (cardType) {
            "AMEX" -> "booking-OK-PN.json"
            else -> "booking-OK-POA.json"
        }

        //GET BELFAS LATLON availabilities
        RESTMockServer.whenGET(RequestMatchers.pathContains("/snowdrop/search/hotels/availabilities"))
                .thenReturnFile(200, "apiTest/search/hotels/BELFAS_availabilities_OK.json")

        //GET BELFAS availability ok
        RESTMockServer.whenGET(RequestMatchers.pathContains("/booking/hotels/${hotelCode}/availability"))
                .thenReturnFile(200, "apiTest/booking/hotels/${hotelCode}/$availabilitySuffix.json")

        //GET BELFAS hotelInfo ok
        RESTMockServer.whenGET(RequestMatchers.pathEndsWith("/hotels/${hotelCode}"))
                .thenReturnFile(200, "apiTest/hotels/${hotelCode}.json")

        //PUT hold ok
        RESTMockServer.whenPUT(RequestMatchers.pathEndsWith("/booking/hotels/BI09HQ7gShe6k3NT/hold?hotelBrand=PI"))
                .thenReturnFile(200, "apiTest/booking/hotels/hold/hold_OK.json")

        //POST Login ok
        RESTMockServer.whenPOST(RequestMatchers.pathEndsWith("/oauth/token"))
                .thenReturnFile(200, "apiTest/auth/hotels/login/auth0_login_OK.json")

        RESTMockServer.whenPOST(RequestMatchers.pathEndsWith("/auth/hotels/login"))
                .thenReturnFile(200, "apiTest/auth/hotels/login/login_OK.json")

        //GET customer OK
        RESTMockServer.whenGET(RequestMatchers.pathEndsWith("/customers/hotels/test@gmail.com"))
                .thenReturnString(200,
                        replaceJSONTemplate(this::class.java, "/apiTest/customers/hotels/$customerJsonResponse", 3))

        //POST payment ok
        RESTMockServer.whenPOST(RequestMatchers.pathEndsWith("/payment/hotels"))
                .thenReturnFile(200, "apiTest/payment/hotels/$threeDSJsonResponse")

        //POST booking ok
        RESTMockServer.whenPOST(RequestMatchers.pathEndsWith("/booking/hotels/fXKhMScvzjiZJXFl"))
                .thenReturnFile(200, "apiTest/booking/hotels/booking-complete/$bookingCompleteJsonResponse")

        //GET RESERVATION OK
        RESTMockServer.whenGET(RequestMatchers.pathContains("/reservation/hotels/AVPR273061"))
                .thenReturnFile(200, "apiTest/reservation/hotels/AVPR273061_BELFAS.json")
    }

    //TODO: 3DS Try and Convert to 3CP
    // todo make this properly - this is a temporary workaround to change the dagger graph in runtime
    /*fun buildDaggerGraphWithPayOnArrival3ds(value: Boolean) {
        val applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(context))
                .persistenceModule(object : PersistenceModule() {
                    override fun provideSecureSharedPreferences(context: Context): SharedPreferences {
                        return InMemorySharedPreferences()
                    }

                    override fun provideFirebaseRemoteConfig(context: Context): FirebaseRemoteConfig {
                        val remoteConfig = FirebaseRemoteConfig.getInstance()
                        remoteConfig.setDefaultsAsync(object : HashMap<String, Any>() {
                            init {
                                put(ContentManagedResourceRepository.Key.TOP_DESTINATIONS.value,
                                        context.getString(R.string.top_destinations))
                                put(ContentManagedResourceRepository.Key.PASSWORD_VALIDATOR.value,
                                    context.getString(R.string.password_validator))
                            }
                        })
                        return remoteConfig
                    }
                })
                .variantModule(object : VariantModule() {
                    override fun provideAuthenticationRepository(appConfiguration: AppConfiguration,
                                                                 client: AuthenticationAPIClient,
                                                                 errorLogger: ErrorLogger,
                                                                 manager: SecureCredentialsManager): AuthenticationRepository {
                        return object : AuthenticationRepository {
                            override fun authenticate(user: String, password: String, userType: UserType): Completable {
                                return Completable.complete()
                            }

                            override fun renewRefreshToken(): Single<String> {
                                return Single.just("token")
                            }

                            override fun logout(): Completable {
                                return Completable.complete()
                            }

                            override fun isTokenValid(): Boolean {
                                return true
                            }

                            override fun getIdToken(): Single<String> {
                                return Single.just("Token")
                            }

                            override fun getRefreshToken(): Single<String> {
                                return Single.just("Refresh Token")
                            }
                        }
                    }

                    override fun provideConfiguration(preferences: SharedPreferences, isFeatureOn: IsFeatureOn): AppConfiguration {
                        return object : StagingConfiguration(preferences, isFeatureOn) {

                            override fun getSnowdropUrl(): String {
                                return RESTMockServer.getUrl()
                            }

                            override fun getMicroServicesUrl(): String {
                                return RESTMockServer.getUrl()
                            }

                            override fun getCheckInOnlineUrl(): String {
                                return Urls.UAT_CHECK_IN_ONLINE_URL
                            }
                            //TODO: 3DS Try and Convert to 3CP Potentially switch with getGraphQLUrl()
//                            override fun getWebView3dsUrl(): String {
//                                return Urls.CONTENT_BASE_URL
//                            }
                        }
                    }

                    override fun provideOkHttpClient(context: Context, configuration: AppConfiguration,
                                                     resourceRepository: ContentManagedResourceRepository,
                                                     appPackageDetails: AppPackageDetails): OkHttpClient {

                        val okHttpClient = StagingOkHttpClientBuilder(context, configuration, resourceRepository,
                                appPackageDetails, ConsumerType.RX).build()
                        val okHttpIdlingResource = OkHttp3IdlingResource.create("OkHttp", okHttpClient)
                        IdlingRegistry.getInstance().register(okHttpIdlingResource)
                        return okHttpClient
                    }
                })
                .serviceModule(object : ServiceModule() {
                    override fun provideAuth0Account(auth0Configuration: Auth0Configuration): Auth0 {
                        val auth0Account = Auth0(auth0Configuration.clientId, RESTMockServer.getUrl())
                        auth0Account.isOIDCConformant = true
                        return auth0Account
                    }
                })
                .build()

        ComponentsManager.getInstance().appComponent = applicationComponent
    }*/
}