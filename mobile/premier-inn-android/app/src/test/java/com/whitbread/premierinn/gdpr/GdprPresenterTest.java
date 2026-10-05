package com.whitbread.premierinn.gdpr;

import static com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.GDPR_PRIVACY_MESSAGE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.StringResourceProvider;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.contentSquare.CSQOptInHelper;
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

@RunWith(MockitoJUnitRunner.class)
public class GdprPresenterTest {

    private static final String EXPECTED_PRIVACY_POLICY = "privacy message";

    private GdprPresenter presenter;
    private static final String  PRIVACY_POLICY_WEB_URL =
            "https://www.premierinn.com/gb/en/terms/privacy-policy.html?INTCMP=And_privacyPolicy";

    @Mock GdprPresenter.View view;
    @Mock GetStringResource getStringResource;
    @Mock SimplePersistenceManager persistenceManager;
    @Mock TrackingAnalytics trackingAnalytics;
    @Mock StringResourceProvider stringResourceProvider;
    @Mock
    CSQOptInHelper cSQOptInHelper;

    @Before
    public void onSetup() {
        presenter = new GdprPresenter(getStringResource, persistenceManager, new CompositeDisposable(),
                trackingAnalytics, stringResourceProvider, cSQOptInHelper);

        when(view.privacyPolicyClicked()).thenReturn(Observable.never());
        when(view.howToUseDataClicked()).thenReturn(Observable.never());
        when(view.changesAccepted()).thenReturn(Observable.never());
        when(getStringResource.invoke(GDPR_PRIVACY_MESSAGE)).thenReturn(EXPECTED_PRIVACY_POLICY);
        when(stringResourceProvider.getString(R.string.privacy_policy_web_url)).thenReturn(PRIVACY_POLICY_WEB_URL);
    }

    @Test
    public void testPrivacyPolicyAcceptanceStored() {
        when(view.changesAccepted()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);
        verify(persistenceManager).setHasAcceptedPrivacyPolicy();
    }

    @Test
    public void testPrivacyPolicyAcceptanceReturns() {
        when(view.changesAccepted()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);
        verify(view).returnResult();
    }

    @Test
    public void testDataUsagePolicyShownOnClick() {
        when(view.howToUseDataClicked()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);
        verify(view).openHowWeUseYourData();
    }

    @Test
    public void testPrivacyPolicyShownWhenClicked() {
        when(view.privacyPolicyClicked()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);
        verify(view).goToWebLink(PRIVACY_POLICY_WEB_URL);
    }

    @Test
    public void testPrivacyMessageFromConfigIsShown() {
        presenter.attachView(view);
        verify(view).showInterstitialMessage(EXPECTED_PRIVACY_POLICY);
    }

    @Test
    public void shouldTrackScreenStateOnAttachView() {
        presenter.attachView(view);
        verify(trackingAnalytics).track(AnalyticsConstants.ScreenState.INTERSTITIAL, AnalyticsConstants.Type.HOME);
    }
}