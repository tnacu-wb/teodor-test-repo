package com.whitbread.premierinn.login;

import static com.whitbread.premierinn.common.utils.StringUtils.EMPTY_STRING;
import static com.whitbread.premierinn.domain.authentication.AuthenticationError.Type.INVALID_CREDENTIALS;
import static com.whitbread.premierinn.domain.authentication.AuthenticationError.Type.NETWORK;
import static com.whitbread.premierinn.domain.authentication.AuthenticationError.Type.TOO_MANY_ATTEMPTS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager;
import com.whitbread.premierinn.common.StringResourceProvider;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.FirebaseLogger;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager;
import com.whitbread.premierinn.domain.authentication.AuthenticationError;
import com.whitbread.premierinn.domain.authentication.usecase.AuthenticateCustomer;
import com.whitbread.premierinn.domain.booking.usecase.SyncCustomerFutureBookings;
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn;
import com.whitbread.premierinn.utils.RxJavaTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {
    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();
    @Mock LoginPresenter.View view;
    @Mock AuthenticateCustomer authenticateCustomer;
    @Mock TrackingAnalytics trackingAnalytics;
    @Mock FirebaseLogger firebaseLogger;
    @Mock LogService crashlyticsLogger;
    @Mock SyncCustomerFutureBookings syncCustomerFutureBookings;
    @Mock IsFeatureOn isFeatureOn;
    @Mock SimplePersistenceManager simplePersistenceManager;
    @Mock BusinessPersistenceManager businessPersistenceManager;
    @Mock StringResourceProvider stringResourceProvider;
    @Mock
    DeviceLocaleProvider deviceLocaleProvider;
    Screen screen = new Screen(ScreenType.ACCOUNT_LOGIN.name(), AnalyticsConstants.ScreenState.ACCOUNT_LOG_IN);

    final CompositeDisposable viewCompositeDisposable = new CompositeDisposable();

    LoginDataInput loginDataDefaultInput;

    LoginPresenter presenter;

    static final String EMAIL = "EMAIL";
    static final String PASSWORD = "PASSWORD";
    private static final String  BUSINESS_BOOKER_WEB_URL = "https://www.premierinn.com/gb/en/business/business-booker.html?INTCMP=And_BB";

    @Before
    public void setup() {
        presenter = new LoginPresenter(
                authenticateCustomer,
                trackingAnalytics,
                firebaseLogger,
                crashlyticsLogger,
                isFeatureOn,
                simplePersistenceManager,
                businessPersistenceManager,
                viewCompositeDisposable,
                syncCustomerFutureBookings,
                stringResourceProvider,
                deviceLocaleProvider
        );

        when(view.onContinueGuestClick()).thenReturn(Observable.never());
        when(view.onForgotPasswordClick()).thenReturn(Observable.never());
        when(view.onLogInButtonClick()).thenReturn(Observable.never());
        when(view.onLoginDataInputChanged()).thenReturn(Observable.never());
        when(view.onResetPasswordConfirmation()).thenReturn(Observable.never());
        when(view.onTabSelected()).thenReturn(Observable.never());
        when(view.onMoreAboutBusinessBookingClick()).thenReturn(Observable.never());
        when(stringResourceProvider.getString(R.string.business_booker_web_url)).thenReturn(BUSINESS_BOOKER_WEB_URL);
        when(deviceLocaleProvider.getDeviceLocale()).thenReturn(Locale.UK);
        loginDataDefaultInput = LoginDataInput.create(false, EMAIL, PASSWORD, false);
        when(businessPersistenceManager.getBusinessCustomerEmail()).thenReturn(EMPTY_STRING);
    }

    @Test
    public void lifeCycle() {
        presenter.detachView();
        assertFalse(presenter.isViewAttached());
        assertEquals(0, viewCompositeDisposable.size());
        presenter.attachView(view);
        assertTrue(presenter.isViewAttached());
        assertEquals(7, viewCompositeDisposable.size());
        presenter.detachView();
        assertFalse(presenter.isViewAttached());
        assertEquals(0, viewCompositeDisposable.size());
    }

    @Test
    public void loginSuccessful_WhenBookingsSucceed() {
        when(view.onLogInButtonClick()).thenReturn(Observable.just(loginDataDefaultInput));
        when(authenticateCustomer.invoke(any(AuthenticateCustomer.Params.class))).thenReturn(Completable.complete());
        when(syncCustomerFutureBookings.execute(any())).thenReturn(Completable.complete());

        presenter.attachView(view);

        verify(view, times(2)).showLoginLoading(eq(true));
        verify(view).showLoginFailedError(eq(false), eq(0));
        verify(view, times(2)).showLoginLoading(eq(false));
        verify(firebaseLogger, times(1)).logEvent(eq(FirebaseAnalytics.Event.LOGIN));
        verify(view, times(1)).finishSuccessLoginActivity();
    }

    @Test
    public void loginSuccessful_WhenBookingsFail() {
        when(view.onLogInButtonClick()).thenReturn(Observable.just(loginDataDefaultInput));
        when(authenticateCustomer.invoke(any(AuthenticateCustomer.Params.class))).thenReturn(Completable.complete());
        when(syncCustomerFutureBookings.execute(any())).thenReturn(Completable.error(Throwable::new));

        presenter.attachView(view);

        verify(view, times(2)).showLoginLoading(eq(true));
        verify(view).showLoginFailedError(eq(false), eq(0));
        verify(view, times(2)).showLoginLoading(eq(false));
        verify(firebaseLogger, times(1)).logEvent(eq(FirebaseAnalytics.Event.LOGIN));
        verify(view, times(1)).finishSuccessLoginActivity();
    }

    @Test
    public void loginFailure_InvalidCredentials() {
        when(view.onLogInButtonClick()).thenReturn(Observable.just(loginDataDefaultInput));
        when(authenticateCustomer.invoke(any(AuthenticateCustomer.Params.class))).thenReturn(
                Completable.error(new AuthenticationError(INVALID_CREDENTIALS, null, null)));

        presenter.attachView(view);

        verify(view, times(1)).showLoginLoading(eq(true));
        verify(view).showLoginFailedError(true, R.string.log_in_log_in_api_call_failed);
        verify(view, times(1)).showLoginLoading(eq(false));
        verify(firebaseLogger, times(0)).logEvent(any());
    }

    @Test
    public void loginFailure_UnexpectedError() {
        when(view.onLogInButtonClick()).thenReturn(Observable.just(loginDataDefaultInput));
        when(authenticateCustomer.invoke(any(AuthenticateCustomer.Params.class))).thenReturn(
                Completable.error(new Exception()));

        presenter.attachView(view);

        verify(view, times(1)).showLoginLoading(eq(true));
        verify(view).showLoginFailedError(true, R.string.generic_error_description);
        verify(view, times(1)).showLoginLoading(eq(false));
        verify(firebaseLogger, times(0)).logEvent(any());
    }

    @Test
    public void loginFailure_Too_Many_Attempts() {
        when(view.onLogInButtonClick()).thenReturn(Observable.just(loginDataDefaultInput));
        when(authenticateCustomer.invoke(any(AuthenticateCustomer.Params.class))).thenReturn(
                Completable.error(new AuthenticationError(TOO_MANY_ATTEMPTS, null, "too many")));

        presenter.attachView(view);

        verify(view, times(1)).showLoginLoading(eq(true));
        verify(view).showLoginFailedError(eq(true), eq(R.string.log_in_log_in_api_too_many_attempts));
        verify(view, times(1)).showLoginLoading(eq(false));
        verify(firebaseLogger, times(0)).logEvent(any());
    }

    @Test
    public void loginFailure_TimeoutError() {
        when(view.onLogInButtonClick()).thenReturn(Observable.just(loginDataDefaultInput));
        when(authenticateCustomer.invoke(any(AuthenticateCustomer.Params.class))).thenReturn(Completable.error(
                new AuthenticationError(NETWORK, null, null)));

        presenter.attachView(view);

        verify(view, times(1)).showLoginLoading(eq(true));
        verify(view).showLoginFailedError(true, com.auth0.android.auth0.R.string.com_auth0_webauth_network_error);
        verify(view, times(1)).showLoginLoading(eq(false));
        verify(firebaseLogger, times(0)).logEvent(any());
    }

    @Test
    public void onEmailTextChanged_Invalid_Then_Valid() {
        when(view.onLoginDataInputChanged()).thenReturn(Observable.fromArray(
                LoginDataInput.create(false, "invalid_email", "password", false),
                LoginDataInput.create(false, "valid@email.com", "valid_password", false)));

        presenter.attachView(view);

        verify(view, times(1)).showEmailValidationError(eq(true));
        verify(view, times(1)).enableLoginButton(eq(false));

        verify(view, times(1)).showEmailValidationError(eq(false));
        verify(view, times(1)).enableLoginButton(eq(true));
    }

    @Test
    public void onPasswordChanged_Empty_Then_Valid() {
        when(view.onLoginDataInputChanged()).thenReturn(Observable.fromArray(
                LoginDataInput.create(false, "valid@email.com", "", false),
                LoginDataInput.create(false, "valid@email.com", "8characters", false)
        ));

        presenter.attachView(view);

        verify(view, times(2)).showEmailValidationError(eq(false));
        verify(view, times(1)).enableLoginButton(eq(false));
        verify(view, times(1)).enableLoginButton(eq(true));
    }

    @Test
    public void onContinueGuestClick() {
        when(view.onContinueGuestClick()).thenReturn(Observable.just(Unit.INSTANCE));

        presenter.attachView(view);

        verify(view).finishLoginActivity();
    }

    @Test
    public void onForgotPasswordClick() {
        when(view.onForgotPasswordClick()).thenReturn(Observable.just(Unit.INSTANCE));

        presenter.attachView(view);

        verify(view).startResetPasswordActivity();
    }

    @Test
    public void incorrectEmailAddress() {
        when(view.onLoginDataInputChanged()).thenReturn(Observable.just(LoginDataInput.create(false, "abc.com", "", false)));

        presenter.attachView(view);

        verify(view).enableLoginButton(eq(false));
    }

    @Test
    public void correctEmailAddress() {
        when(view.onLoginDataInputChanged()).thenReturn(Observable.just(LoginDataInput.create(false, "a@b.com", "password", false)));

        presenter.attachView(view);

        verify(view).enableLoginButton(eq(true));
    }

    @Test
    public void showResetPasswordConfirmation() {
        when(view.onResetPasswordConfirmation()).thenReturn(Observable.just("email@email.com"));

        presenter.attachView(view);

        verify(view).showResetPasswordConfirmation(anyString());
    }

    @Test
    public void onMoreAboutBusinessBookerClick() {
        when(view.onMoreAboutBusinessBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));

        presenter.attachView(view);

        verify(view).goToWebLink(BUSINESS_BOOKER_WEB_URL);
    }
}
