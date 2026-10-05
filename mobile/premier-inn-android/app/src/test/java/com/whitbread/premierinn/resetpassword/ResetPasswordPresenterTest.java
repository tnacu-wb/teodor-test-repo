package com.whitbread.premierinn.resetpassword;

import static com.whitbread.premierinn.data.common.Constants.LANGUAGE_ENGLISH;
import static com.whitbread.premierinn.domain.common.Constants.LANGUAGE_ENGLISH_DOMAIN;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.core.util.Pair;

import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.graphql.forgotpassword.entity.ForgotPasswordDomain;
import com.whitbread.premierinn.domain.graphql.forgotpassword.usecase.ForgotPasswordUseCase;
import com.whitbread.premierinn.utils.RxJavaTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

@RunWith(MockitoJUnitRunner.class)
public class ResetPasswordPresenterTest {
    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();

    private final CompositeDisposable viewCompositeDisposable = new CompositeDisposable();

    @Mock ResetPasswordPresenter.View view;
    @Mock ForgotPasswordUseCase forgotPasswordUseCase;
    @Mock ResetPasswordPresenter.UsernameFormInput emailFormInput;
    @Mock TrackingAnalytics analytics;
    @Mock DeviceLocaleProvider deviceLocaleProvider;

    private ResetPasswordPresenter presenter;

    @Before
    public void setup() {
        presenter = new ResetPasswordPresenter(viewCompositeDisposable, forgotPasswordUseCase, analytics, deviceLocaleProvider);
        when(view.onEmailChanged()).thenReturn(Observable.never());
        when(view.onResetPasswordClick()).thenReturn(Observable.never());
        when(deviceLocaleProvider.getDeviceLanguage()).thenReturn(LANGUAGE_ENGLISH);
    }

    @Test
    public void testOnSuccessfulResetPassword() {
        String email = "valid@email.com";
        when(view.onEmailChanged()).thenReturn(Observable.just(ResetPasswordPresenter.UsernameFormInput.create(false, email)));
        when(view.onResetPasswordClick()).thenReturn(Observable.just(Pair.create(email, false)));
        when(forgotPasswordUseCase.execute(email, false, LANGUAGE_ENGLISH_DOMAIN)).thenReturn(Single.just(new ForgotPasswordDomain(true)));

        presenter.attachView(view);
        verify(view).resetSuccessful(email);
    }

    @Test
    public void testNotRegisteredUserResetPassword() {
        String email = "valid@email.com";
        when(view.onResetPasswordClick()).thenReturn(Observable.just(Pair.create(email, false)));
        when(forgotPasswordUseCase.execute(email, false, LANGUAGE_ENGLISH_DOMAIN)).thenReturn(Single.just(new ForgotPasswordDomain(false)));
        presenter.attachView(view);
        verify(view).showWrongUsernameMessage();
    }

    @Test
    public void testRequestFailed() {
        String email = "valid@email.com";
        when(view.onResetPasswordClick()).thenReturn(Observable.just(Pair.create(email, false)));
        when(forgotPasswordUseCase.execute(email, false, LANGUAGE_ENGLISH_DOMAIN)).thenReturn(Single.error(new Exception()));
        presenter.attachView(view);
        verify(view).showRequestFailedMessage();
    }

    @Test
    public void testHideErrorWhenEmailIsValidOnTextChanged() {
        when(view.onEmailChanged()).thenReturn(Observable.just(emailFormInput));
        when(emailFormInput.email()).thenReturn("valid@email.com");
        when(emailFormInput.hasError()).thenReturn(true);
        presenter.attachView(view);
        verify(view).showEmailErrorValidation(false);
    }

    @Test
    public void testShowEmailNotValidOnClick() {
        when(view.onResetPasswordClick()).thenReturn(Observable.just(Pair.create("email.com", false)));
        presenter.attachView(view);
        verify(view).showEmailErrorValidation(true);
    }

    @Test
    public void testLifeCycle() {
        assertEquals(0, viewCompositeDisposable.size());
        assertFalse(presenter.isViewAttached());
        presenter.attachView(view);
        verify(analytics).track(AnalyticsConstants.ScreenState.RESET_PASSWORD, AnalyticsConstants.Type.MY_PREMIER_INN);
        assertEquals(2, viewCompositeDisposable.size());
        assertTrue(presenter.isViewAttached());
        presenter.detachView();
        assertFalse(presenter.isViewAttached());
        assertEquals(0, viewCompositeDisposable.size());
    }
}
