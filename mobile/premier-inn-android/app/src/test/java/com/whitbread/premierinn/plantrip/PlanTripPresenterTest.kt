package com.whitbread.premierinn.plantrip

import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.utils.LocationProvider
import com.whitbread.premierinn.common.utils.LocationUtils
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain
import com.whitbread.premierinn.domain.common.hoteldetails.usecase.GraphQLHotelDetailsUseCase
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.util.Locale

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class PlanTripPresenterTest {

    @Mock
    private lateinit var viewMock: PlanTripPresenter.View

    @Mock
    private lateinit var locationProvider: LocationProvider

    @Mock
    private lateinit var analytics: TrackingAnalytics

    @Mock
    private lateinit var deviceLocaleProvider: DeviceLocaleProvider

    @Mock
    private lateinit var graphQLHotelDetailsUseCase: GraphQLHotelDetailsUseCase

    private lateinit var hotelInfoDomain: HotelInformationDomain
    private lateinit var presenter: PlanTripPresenter
    private val networkDisposable = CompositeDisposable()

    companion object {
        private const val HOTEL_CODE = "hotelCode"
    }

    @Before
    fun onSetup() {
        // Ensure RxAndroid main thread scheduler is always available per-test.
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }

        hotelInfoDomain = HotelInformationDomain.createEmptyDomain()

        val locale = Locale.UK
        doReturn(locale).`when`(deviceLocaleProvider).getDeviceLocale()
        doReturn("en").`when`(deviceLocaleProvider).getDeviceLanguage()
        doReturn("gb").`when`(deviceLocaleProvider)
            .getCountryIfRegion(locale)

        doReturn(Single.just(hotelInfoDomain)).`when`(graphQLHotelDetailsUseCase)
            .fetchHotelInfoFromGQL(anyString(), anyString(), anyString())

        presenter = PlanTripPresenter(
            networkDisposable,
            graphQLHotelDetailsUseCase,
            locationProvider,
            analytics,
            deviceLocaleProvider
        )
        presenter.initParams(HOTEL_CODE)
        doReturn(Observable.never<Unit>()).`when`(viewMock).onDirectionClicked()
        doReturn(Observable.never<Boolean>()).`when`(viewMock).onLocationPermissionRequested()
        doReturn(Observable.never<android.location.Location>()).`when`(locationProvider)
            .locationUpdateObservable()
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
    }

    @Test
    fun testNetWorkRequestOnAttach() {
        presenter.attachView(viewMock)

        verify(viewMock, times(1)).setHotelCoordinates(any(), any())
        verify(viewMock, times(0)).setTelephone(anyString())
        verify(viewMock, times(1)).setAddress(anyString(), anyString())
        verify(viewMock, times(1)).setDirectionsInfo(anyString())
        verify(viewMock, times(1)).setParkingInfo(anyString())
        verify(viewMock, times(1)).setHotelName(anyString())
    }

    @Test
    fun testLocationPermissionDenied() {
        doReturn(Observable.just(false)).`when`(viewMock).onLocationPermissionRequested()

        presenter.attachView(viewMock)

        verify(viewMock, times(0)).setDistanceFromUser(anyFloat())
    }

    @Test
    fun testLocationNotAvailable() {
        doReturn(Observable.just(true)).`when`(viewMock).onLocationPermissionRequested()

        presenter.attachView(viewMock)

        verify(viewMock, times(0)).setDistanceFromUser(anyFloat())
    }

    @Test
    fun testLocationAvailable() {
        doReturn(Observable.just(true)).`when`(viewMock).onLocationPermissionRequested()
        doReturn(Observable.just(LocationUtils.create(1.0, 1.0))).`when`(locationProvider)
            .locationUpdateObservable()

        presenter.attachView(viewMock)

        verify(viewMock, times(1)).setDistanceFromUser(anyFloat())
    }

    @Test
    fun testLifeCycle() {
        // Construct - initial state checks
        assertFalse(presenter.isViewAttached)
        assertEquals(0, networkDisposable.size())

        // Call - attach view
        presenter.attachView(viewMock)

        // Check - view attached state
        verify(analytics).track(
            AnalyticsConstants.ScreenState.PLAN_YOUR_TRIP,
            AnalyticsConstants.Type.MY_PREMIER_INN
        )
        assertTrue(presenter.isViewAttached)
        assertEquals(3, networkDisposable.size())

        // Call - detach view
        presenter.detachView()

        // Check - view detached state
        assertFalse(presenter.isViewAttached)
        assertEquals(3, networkDisposable.size())

        // Call - destroy
        presenter.destroy()

        // Check - destroyed state
        assertFalse(presenter.isViewAttached)
        assertEquals(0, networkDisposable.size())
    }
}