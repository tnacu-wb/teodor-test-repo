package com.whitbread.premierinn.loading;

import static com.whitbread.premierinn.data.common.Constants.DEFAULT_MAX_ARRIVAL_DATE;
import static com.whitbread.premierinn.data.common.Constants.DEFAULT_MAX_NIGHTS_INN_BUSINESS;
import static com.whitbread.premierinn.data.common.Constants.DEFAULT_MAX_NIGHTS_LEISURE;
import static com.whitbread.premierinn.data.common.Constants.DEFAULT_MAX_ROOMS_AMEND;
import static com.whitbread.premierinn.data.common.Constants.DEFAULT_MAX_ROOMS_INN_BUSINESS;
import static com.whitbread.premierinn.data.common.Constants.DEFAULT_MAX_ROOMS_LEISURE;
import static com.whitbread.premierinn.data.common.Constants.EMPTY_STRING;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.whitbread.premierinn.api.response.InstanceFactory;
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager;
import com.whitbread.premierinn.common.analytics.FirebaseLogger;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.deeplink.DeeplinkContent;
import com.whitbread.premierinn.common.deeplink.DeeplinkMapper;
import com.whitbread.premierinn.common.deeplink.DeeplinkMapperKt;
import com.whitbread.premierinn.common.deeplink.DeeplinkStatus;
import com.whitbread.premierinn.common.pushNotification.PushNotificationFactory;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager;
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelInfoSlugGraphQLContract;
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn;
import com.whitbread.premierinn.domain.graphql.hdp.usecase.GraphQLHDPUseCase;
import com.whitbread.premierinn.domain.resource.usecase.BartDownMaintenanceRequired;
import com.whitbread.premierinn.domain.resource.usecase.ForceUpdateRequired;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;
import com.whitbread.premierinn.utils.RxJavaTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

/**
 * Todo: Review Test methods complete them and and apply best practices
 */
@RunWith(MockitoJUnitRunner.class)
public class LoadingPresenterTest {

    @Mock
    ForceUpdateRequired forceUpdateRequired;
    @Mock
    BartDownMaintenanceRequired bartDownMaintenanceRequired;
    @Mock
    GraphQLHDPUseCase graphQLHDPUseCase;
    @Mock
    DeviceLocaleProvider deviceLocaleProvider;
    @Mock
    SimplePersistenceManager persistenceManager;
    @Mock
    BusinessPersistenceManager businessPersistenceManager;
    @Mock
    LoadingPresenter.View view;
    @Mock
    TrackingAnalytics analytics;
    @Mock
    LogService logService;
    @Mock
    GetStringResource getStringResource;
    @Mock
    IsFeatureOn isFeatureOn;
    @Mock
    DeeplinkMapper deeplinkMapper;
    @Mock
    PushNotificationFactory pushNotificationFactory;
    @Mock
    FirebaseLogger firebaseLogger;

    TestScheduler testScheduler = new TestScheduler();
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private HotelInfoSlugGraphQLContract.HotelInfoData hotelInfoSlugDomainResponse;

    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();

    private LoadingPresenter loadingPresenter;

    private LoadingPresenter loadingPresenterInnBusiness;

    @Before
    public void onSetup() {
        hotelInfoSlugDomainResponse = InstanceFactory.create(
                HotelInfoSlugGraphQLContract.HotelInfoData.class, "apiTest/graphql/hotel_info_slug_gql.json"
        );
        when(businessPersistenceManager.getBusinessCustomerEmail()).thenReturn(EMPTY_STRING);



        when(view.gdprPrivacyPolicyAccepted()).thenReturn(Observable.never());
        when(view.notificationMessageAccepted()).thenReturn(Observable.never());
        when(view.getAppsFlyerDeeplink()).thenReturn(Observable.never());
        when(view.getPushNotificationData()).thenReturn(Single.just(new HashMap<>()));
        when(forceUpdateRequired.execute()).thenReturn(Single.just(Boolean.FALSE));
        when(bartDownMaintenanceRequired.execute()).thenReturn(Single.just(Boolean.FALSE));
        when(view.getDeeplink()).thenReturn(Single.just(EMPTY_STRING));
    }

    @Test
    public void shouldShowGdprActivity() {
        when(persistenceManager.hasAcceptedPrivacyPolicy()).thenReturn(false);

        loadingPresenter = new LoadingPresenter(forceUpdateRequired, bartDownMaintenanceRequired, graphQLHDPUseCase,
                deviceLocaleProvider, deeplinkMapper, pushNotificationFactory, persistenceManager, businessPersistenceManager,
                compositeDisposable, analytics, firebaseLogger, logService, isFeatureOn, getStringResource, testScheduler);

        loadingPresenter.attachView(view);

        verify(view, times(2)).loading();
        verify(view).startGDPRActivity();
    }

    @Test
    public void shouldStartNextActivityWhenPrivacyPolicyAccepted() {
        when(persistenceManager.hasAcceptedPrivacyPolicy()).thenReturn(false);
        when(view.gdprPrivacyPolicyAccepted()).thenReturn(Observable.just(new Object()));

        loadingPresenter = new LoadingPresenter(forceUpdateRequired, bartDownMaintenanceRequired, graphQLHDPUseCase,
                deviceLocaleProvider, deeplinkMapper, pushNotificationFactory, persistenceManager, businessPersistenceManager,
                compositeDisposable, analytics, firebaseLogger, logService, isFeatureOn, getStringResource, testScheduler);
        loadingPresenter.attachView(view);

        verify(view, times(2)).loading();
        verify(view).startHomeActivity();
    }

    @Test
    public void shouldLogErrorWhenForceUpdateFails() {
        Exception exception = new Exception();
        when(persistenceManager.hasAcceptedPrivacyPolicy()).thenReturn(true);
        when(forceUpdateRequired.execute()).thenReturn(Single.error(exception));

        loadingPresenter = new LoadingPresenter(forceUpdateRequired, bartDownMaintenanceRequired, graphQLHDPUseCase,
                deviceLocaleProvider, deeplinkMapper, pushNotificationFactory, persistenceManager, businessPersistenceManager,
                compositeDisposable, analytics, firebaseLogger, logService, isFeatureOn, getStringResource, testScheduler);

        loadingPresenter.attachView(view);

        verify(view, times(2)).loading();
        verify(view).initAppsFlyerAndCheckForDeferredDeeplink();
        verify(logService).logException(eq(exception), any());
    }

    @Test
    public void shouldShowForceUpdateScreen() {
        when(forceUpdateRequired.execute()).thenReturn(Single.just(Boolean.TRUE));

        loadingPresenter = new LoadingPresenter(forceUpdateRequired, bartDownMaintenanceRequired, graphQLHDPUseCase,
                deviceLocaleProvider, deeplinkMapper, pushNotificationFactory, persistenceManager, businessPersistenceManager,
                compositeDisposable, analytics, firebaseLogger, logService, isFeatureOn, getStringResource, testScheduler);

        loadingPresenter.attachView(view);

        verify(view, times(2)).loading();
        verify(view).showForceUpdate();
    }

    @Test
    public void shouldShowBartDownScreen() {
        when(bartDownMaintenanceRequired.execute()).thenReturn(Single.just(Boolean.TRUE));

        loadingPresenter = new LoadingPresenter(forceUpdateRequired, bartDownMaintenanceRequired, graphQLHDPUseCase,
                deviceLocaleProvider, deeplinkMapper, pushNotificationFactory, persistenceManager, businessPersistenceManager,
                compositeDisposable, analytics, firebaseLogger, logService, isFeatureOn, getStringResource, testScheduler);

        loadingPresenter.attachView(view);

        verify(view).loading();
        verify(view).showBartDowntimeScreen();
    }

    @Test
    public void shouldLogErrorWhenBartDownFails() {
        Exception exception = new Exception();
        when(persistenceManager.hasAcceptedPrivacyPolicy()).thenReturn(true);
        when(bartDownMaintenanceRequired.execute()).thenReturn(Single.error(exception));

        loadingPresenter = new LoadingPresenter(forceUpdateRequired, bartDownMaintenanceRequired, graphQLHDPUseCase,
                deviceLocaleProvider, deeplinkMapper, pushNotificationFactory, persistenceManager, businessPersistenceManager,
                compositeDisposable, analytics, firebaseLogger, logService, isFeatureOn, getStringResource, testScheduler);

        loadingPresenter.attachView(view);

        verify(logService).logException(eq(exception), any());
    }

    @Test
    public void shouldSaveDefaultBusinessRulesForLeisure() {
        loadingPresenter = new LoadingPresenter(forceUpdateRequired, bartDownMaintenanceRequired, graphQLHDPUseCase,
                deviceLocaleProvider, deeplinkMapper, pushNotificationFactory, persistenceManager, businessPersistenceManager,
                compositeDisposable, analytics, firebaseLogger, logService, isFeatureOn, getStringResource, testScheduler);

        loadingPresenter.attachView(view);
        verify(persistenceManager, times(1))
                .saveValuesForBusinessRulesLeisure(DEFAULT_MAX_NIGHTS_LEISURE, DEFAULT_MAX_ROOMS_LEISURE,
                        DEFAULT_MAX_ROOMS_AMEND, DEFAULT_MAX_ARRIVAL_DATE);
    }

    @Test
    public void testDestroy() {
        loadingPresenter = new LoadingPresenter(forceUpdateRequired, bartDownMaintenanceRequired, graphQLHDPUseCase,
                deviceLocaleProvider, deeplinkMapper, pushNotificationFactory, persistenceManager, businessPersistenceManager,
                compositeDisposable, analytics, firebaseLogger, logService, isFeatureOn, getStringResource, testScheduler);

        loadingPresenter.destroy();

        assertEquals(0, compositeDisposable.size());
    }

    @Test
    public void testAttachView() {
        assertEquals(0, compositeDisposable.size());

        loadingPresenter = new LoadingPresenter(forceUpdateRequired, bartDownMaintenanceRequired, graphQLHDPUseCase,
                deviceLocaleProvider, deeplinkMapper, pushNotificationFactory, persistenceManager, businessPersistenceManager,
                compositeDisposable, analytics, firebaseLogger, logService, isFeatureOn, getStringResource, testScheduler);

        loadingPresenter.attachView(view);

        assertEquals(5, compositeDisposable.size());

        assertTrue(loadingPresenter.isViewAttached());
    }

    @Test
    public void testDetachView() {
        loadingPresenter = new LoadingPresenter(forceUpdateRequired, bartDownMaintenanceRequired, graphQLHDPUseCase,
                deviceLocaleProvider, deeplinkMapper, pushNotificationFactory, persistenceManager, businessPersistenceManager,
                compositeDisposable, analytics, firebaseLogger, logService, isFeatureOn, getStringResource, testScheduler);

        loadingPresenter.attachView(view);
        loadingPresenter.detachView();
        loadingPresenter.destroy();

        assertFalse(loadingPresenter.isViewAttached());
    }

    //InnBusiness


    @Test
    public void shouldSaveDefaultBusinessRulesForInnBusiness() {
        when(businessPersistenceManager.getBusinessCustomerEmail()).thenReturn("someemail@email.com");
        loadingPresenterInnBusiness = new LoadingPresenter(forceUpdateRequired, bartDownMaintenanceRequired, graphQLHDPUseCase,
                deviceLocaleProvider, deeplinkMapper, pushNotificationFactory, persistenceManager,
                businessPersistenceManager, compositeDisposable, analytics, firebaseLogger,
                logService, isFeatureOn, getStringResource, testScheduler);

        loadingPresenterInnBusiness.attachView(view);
        verify(businessPersistenceManager).getBusinessCustomerEmail();
        verify(businessPersistenceManager, times(1))
                .saveValuesForBusinessRulesInnBusiness(DEFAULT_MAX_NIGHTS_INN_BUSINESS,
                        DEFAULT_MAX_ROOMS_INN_BUSINESS, DEFAULT_MAX_ARRIVAL_DATE);
    }

    @Test
    public void shouldNavigateHomeDeeplinkWhenLaunchDeeplinkIsAvailable() {
        String deeplink = "https://premierinnuat.onelink.me/hKCB?pageName=home&promoCode=PREBF";
        when(persistenceManager.hasAcceptedPrivacyPolicy()).thenReturn(true);
        when(view.getDeeplink()).thenReturn(Single.just(deeplink));
        when(deeplinkMapper.mapUrl(deeplink)).thenReturn(
                new DeeplinkContent(
                        DeeplinkStatus.KNOWN_DEEPLINK,
                        DeeplinkMapperKt.HOME_DESTINATION,
                        new android.os.Bundle(),
                        new HashMap<>()
                )
        );

        loadingPresenter = new LoadingPresenter(forceUpdateRequired, bartDownMaintenanceRequired, graphQLHDPUseCase,
                deviceLocaleProvider, deeplinkMapper, pushNotificationFactory, persistenceManager,
                businessPersistenceManager, compositeDisposable, analytics, firebaseLogger, logService,
                isFeatureOn, getStringResource, testScheduler);

        loadingPresenter.attachView(view);

        verify(deeplinkMapper).mapUrl(deeplink);
        verify(view).navigateDeeplink(any());
    }
}
