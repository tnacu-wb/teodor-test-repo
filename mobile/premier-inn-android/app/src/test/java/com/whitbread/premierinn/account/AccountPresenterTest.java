package com.whitbread.premierinn.account;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.pm.PackageManager;

import com.whitbread.premierinn.api.response.InstanceFactory;
import com.whitbread.premierinn.api.response.customer.ParcelableCustomer;
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.data.common.DomainMappers;
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager;
import com.whitbread.premierinn.data.remote.AccountApiContract;
import com.whitbread.premierinn.domain.authentication.NoLongerValidCredentials;
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn;
import com.whitbread.premierinn.domain.authentication.usecase.LogoutCustomer;
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer;
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;
import com.whitbread.premierinn.utils.RxJavaTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Pair;
import kotlin.Unit;

@RunWith(MockitoJUnitRunner.class)
public class AccountPresenterTest {

    private final CompositeDisposable viewCompositeDisposable = new CompositeDisposable();
    private AccountPresenter presenter;
    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();

    @Mock
    AccountPresenter.View view;
    @Mock
    GetCustomer getCustomer;
    @Mock
    LogoutCustomer logoutCustomer;
    @Mock
    IsCustomerLoggedIn isCustomerLoggedIn;
    AccountApiContract.CustomerResponse successCustomerResponse;
    @Mock
    AppFeedbackMessageProvider appFeedbackMessageProvider;
    @Mock
    LogService errorLogger;
    @Mock
    TrackingAnalytics analytics;
    @Mock
    GetStringResource isFeatureOn;
    @Mock
    BusinessPersistenceManager businessPersistenceManager;
    @Mock
    SimplePersistenceManager persistenceManager;

    @Before
    public void onSetup() {
        presenter = new AccountPresenter(
                viewCompositeDisposable,
                getCustomer,
                logoutCustomer,
                isCustomerLoggedIn,
                appFeedbackMessageProvider,
                errorLogger,
                analytics,
                isFeatureOn,
                businessPersistenceManager,
                persistenceManager);
        String accountLinks = "[ { \"ctaTitle\": \"COVID 19 update\","
                + "\"url\": \"https://www.premierinn.com/gb/en/covid-19.html\","
                + "\"isActive\": true }]";

        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.MY_ACCOUNT_LINKS))
                .thenReturn(accountLinks);
        when(view.onSetImmediately()).thenReturn(Observable.just(new Object()));
        when(view.onLogInClick()).thenReturn(Observable.never());
        when(view.onPrivacyPolicyClick()).thenReturn(Observable.never());
        when(view.onHowWeUseYourDataClick()).thenReturn(Observable.never());
        when(view.onTermsConditionsClick()).thenReturn(Observable.never());
        when(view.onAboutClick()).thenReturn(Observable.never());
        when(view.onContactClick()).thenReturn(Observable.never());
        when(view.onFaqClick()).thenReturn(Observable.never());
        when(view.onSendFeedbackClick()).thenReturn(Observable.never());
        when(view.onLoginSuccessful()).thenReturn(Observable.never());
        when(view.onLogOutClick()).thenReturn(Observable.never());
        when(view.onBookingPreferencesClick()).thenReturn(Observable.never());
        when(view.onPaymentMethodsClick()).thenReturn(Observable.never());
        when(view.onRefresh()).thenReturn(Observable.never());
        when(getCustomer.invoke()).thenReturn(Single.never());
        when(logoutCustomer.invoke()).thenReturn(Completable.never());
        when(view.onCreateAccountClick()).thenReturn(Observable.never());
        when(view.onAccountCreated()).thenReturn(Observable.never());
        when(view.onPersonalDetailsClick()).thenReturn(Observable.never());
        when(view.onChangePasswordClick()).thenReturn(Observable.never());
        when(view.onChangePasswordSuccessfull()).thenReturn(Observable.never());
        when(view.onSaveCardSuccessful()).thenReturn(Observable.never());
        when(view.onNewsletterUpdatesClick()).thenReturn(Observable.never());
        when(view.isEmployeeOfferToggleEnabled()).thenReturn(Observable.just(false));
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(false));

        successCustomerResponse = InstanceFactory.create(AccountApiContract.CustomerResponse.class, "apiTest/customer-success.json");
    }

    @Test
    public void getCustomerFails_startLoginActivityExpected() {
        when(view.onLoginSuccessful()).thenReturn(Observable.just(new Object()));
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(true));
        final Throwable err = new NoLongerValidCredentials(new IllegalArgumentException("hello"));
        when(getCustomer.invoke()).thenReturn(Single.error(err));

        presenter.attachView(view);

        verify(view).showForceLoginMessage();
        verify(view).startLogInActivity();
        verify(errorLogger).logWarning(err, "AccountPresenter", "ForceLogoutError");
    }

    @Test
    public void testLogInClick() {
        when(view.onLogInClick()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);
        verify(view).startLogInActivity();
    }

    @Test
    public void testLifecycle() {
        assertFalse(presenter.isViewAttached());
        assertEquals(0, viewCompositeDisposable.size());
        presenter.attachView(view);
        verify(analytics).track(AnalyticsConstants.ScreenState.MY_ACCOUNT, AnalyticsConstants.Type.MY_PREMIER_INN);
        assertTrue(presenter.isViewAttached());
        assertEquals(22, viewCompositeDisposable.size());
        presenter.detachView();
        assertFalse(presenter.isViewAttached());
        assertEquals(0, viewCompositeDisposable.size());
    }

    @Test
    public void testBannerShownOnAccountCreated() {
        when(view.onAccountCreated()).thenReturn(Observable.just(new Object()));
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(successCustomerResponse)));
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(true));
        when(persistenceManager.getCustomer()).thenReturn(DomainMappers.toDomain(successCustomerResponse));
        presenter.attachView(view);
        verify(view).showWelcomeToAccountBanner("Whitbread");
    }

    @Test
    public void testOnTermsConditionsClick() {
        when(view.onTermsConditionsClick()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);
        verify(view).openTermsConditionsUrl();
    }

    @Test
    public void testOnCOVIDFaqs_whenCOVIDEnabled() {

        String accountLinks = "[ { \"ctaTitle\": \"COVID 19 update\","
                + "\"url\": \"https://www.premierinn.com/gb/en/covid-19.html\","
                + "\"isActive\": true }]";

        Pair<String, String> link = new Pair<>("COVID 19 update",
                "https://www.premierinn.com/gb/en/covid-19.html");

        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.MY_ACCOUNT_LINKS))
                .thenReturn(accountLinks);

        presenter.attachView(view);
        verify(view).addLink(link);
    }

    @Test
    public void testOnPersonalDetailsClick() {
        when(getCustomer.getCustomerFromSharedPref()).thenReturn(DomainMappers.toDomain(successCustomerResponse));
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(true));
        when(view.onPersonalDetailsClick()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);
        verify(view).startPersonalDetailsActivity(any(ParcelableCustomer.class));
    }

    @Test
    public void testOnPrivacyPolicyClick() {
        when(view.onPrivacyPolicyClick()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);
        verify(view).openPrivacyPolicyUrl();
    }

    @Test
    public void testOnHowWeUseYourDataClick() {
        when(view.onHowWeUseYourDataClick()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);
        verify(view).openHowWeUseYourDataDialog();
    }

    @Test
    public void testOnAboutClick() {
        when(view.onAboutClick()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);
        verify(view).startAboutActivity();
    }

    @Test
    public void testOnFaqClick() {
        when(view.onFaqClick()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);
        verify(view).openFaqUrl();
    }

    @Test
    public void testOnContactUsClick() {
        when(view.onContactClick()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);
        verify(view).openContactUrl();
    }

    @Test
    public void testOnSendFeedbackCLick() throws PackageManager.NameNotFoundException {
        when(view.onSendFeedbackClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(appFeedbackMessageProvider.getEmailAddress()).thenReturn("email");
        when(appFeedbackMessageProvider.getEmailFeedbackSubject()).thenReturn("subject");
        when(appFeedbackMessageProvider.getEmailFeedbackBody()).thenReturn("body");

        presenter.attachView(view);
        verify(view).startEmailActivity(appFeedbackMessageProvider.getEmailAddress(), appFeedbackMessageProvider.getEmailFeedbackSubject(),
                appFeedbackMessageProvider.getEmailFeedbackBody());
    }

    @Test
    public void testAttachedWithLoggedUser() {
        when(view.onSetImmediately()).thenReturn(Observable.just(new Object()));
        when(view.onLoginSuccessful()).thenReturn(Observable.empty());

        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(true));
        when(getCustomer.getCustomerFromSharedPref()).thenReturn(DomainMappers.toDomain(successCustomerResponse));
        presenter.attachView(view);
        verify(view).render(true, "Whitbread", "Apps", "whitbreadapps@gmail.com", false);
    }

    @Test
    public void testAttachedWithLoggedOutUser() {
        when(view.onLoginSuccessful()).thenReturn(Observable.empty());
        presenter.attachView(view);

        verify(view).render(false, null, null, null, false);
    }

    @Test
    public void testAfterLoginSuccess() {
        when(view.onSetImmediately()).thenReturn(Observable.empty());
        when(view.onLoginSuccessful()).thenReturn(Observable.just(new Object()));

        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(true));
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(successCustomerResponse)));
        when(persistenceManager.getCustomer()).thenReturn(DomainMappers.toDomain(successCustomerResponse));
        presenter.attachView(view);
        verify(view).render(true, "Whitbread", "Apps", "whitbreadapps@gmail.com", false);
    }

    @Test
    public void onLogoutClick() {
        when(view.onLogOutClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(logoutCustomer.invoke()).thenReturn(Completable.complete());

        presenter.attachView(view);

        verify(view).showLogoutLoading(true);
        verify(view).showLogoutLoading(false);
        verify(view, times(2)).render(false, null, null, null, false);
    }

    @Test
    public void onBookingPreferencesClick() {
        when(view.onBookingPreferencesClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(view.onLoginSuccessful()).thenReturn(Observable.just(new Object()));
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(true));
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(successCustomerResponse)));

        presenter.attachView(view);

        verify(view).startBookingPreferencesActivity();
    }

    @Test
    public void onLogoutClickNotSuccess() {
        when(view.onLogOutClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(logoutCustomer.invoke()).thenReturn(Completable.complete());

        presenter.attachView(view);

        verify(view).showLogoutLoading(true);
        verify(view).showLogoutLoading(false);
    }

    @Test
    public void testCustomerInfoRefreshed() {
        when(view.onSetImmediately()).thenReturn(Observable.never());
        when(view.onRefresh()).thenReturn(Observable.just(new Object()));
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(true));

        presenter.attachView(view);

        verify(getCustomer, times(2)).getCustomerFromSharedPref();
    }

    @Test
    public void onCreateAccountClickTest() {
        when(view.onCreateAccountClick()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);
        verify(view).startCreateAccountActivity();
    }

    @Test
    public void onChangePasswordClick_startActivityExpected() {
        when(view.onChangePasswordClick()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);
        verify(view).startChangePasswordActivity();
    }

    @Test
    public void onChangePasswordSuccess_showBannerExpected() {
        when(view.onChangePasswordSuccessfull()).thenReturn(Observable.just(new Object()));
        presenter.attachView(view);
        verify(view).showBannerSuccessfulPasswordChange();
    }

    @Test
    public void onSaveCardSuccess_showSavedCardBanner() {
        when(view.onSaveCardSuccessful()).thenReturn(Observable.just(new Object()));
        presenter.attachView(view);
        verify(view).showBannerForSuccessfulSaveCard();
    }

    // Employee rate tests
    @Test
    public void onEmployeeOfferToggleTrueCheck_thenFlagIsSavedInSP() {
        when(view.isEmployeeOfferToggleEnabled()).thenReturn(Observable.just(true));

        presenter.attachView(view);

        verify(persistenceManager).saveEmployeeOfferToggleState(true);
    }

    @Test
    public void onEmployeeOfferToggleFalseCheck_thenFlagIsSavedInSP() {
        when(view.isEmployeeOfferToggleEnabled()).thenReturn(Observable.just(false));

        presenter.attachView(view);

        verify(persistenceManager).saveEmployeeOfferToggleState(false);
    }

    @Test
    public void whenEmployeeOfferEnabled_forEmployeeOfferEnabledFlow() {
        when(persistenceManager.getFlagForEmployeeOfferSection()).thenReturn(true);
        when(persistenceManager.getEmployeeOfferToggleState()).thenReturn(true);

        presenter.attachView(view);

        verify(view).showEmployeeOffer();
        verify(view).setToggleStateForEmployeeOffer(true);
    }

    @Test
    public void whenEmployeeOfferDisabled_forEmployeeOfferDisabledToggleFlow() {
        when(persistenceManager.getFlagForEmployeeOfferSection()).thenReturn(true);
        when(persistenceManager.getEmployeeOfferToggleState()).thenReturn(false);

        presenter.attachView(view);

        verify(view).showEmployeeOffer();
        verify(view).setToggleStateForEmployeeOffer(false);
    }

    @Test
    public void whenEmployeeOfferSectionIsDisabled_thenHideEmployeeOfferSection() {
        when(persistenceManager.getFlagForEmployeeOfferSection()).thenReturn(false);

        presenter.attachView(view);

        verify(view).hideEmployeeOffer();
    }
}