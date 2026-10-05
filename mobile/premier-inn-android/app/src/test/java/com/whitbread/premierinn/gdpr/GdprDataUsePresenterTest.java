package com.whitbread.premierinn.gdpr;

import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.disposables.CompositeDisposable;

import static com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.GDPR_DATA_USAGE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class GdprDataUsePresenterTest {

    @Mock GdprDataUsePresenter.View view;
    @Mock GetStringResource getStringResource;
    @Mock TrackingAnalytics trackingAnalytics;

    private GdprDataUsePresenter presenter;
    private final CompositeDisposable subscriptions = new CompositeDisposable();

    @Before
    public void setUp() {
        presenter = new GdprDataUsePresenter(getStringResource, subscriptions, trackingAnalytics);

        when(getStringResource.invoke(any())).thenReturn("");
    }

    @Test
    public void testLifecycle() {
        assertFalse(presenter.isViewAttached());
        assertEquals(0, subscriptions.size());
        presenter.attachView(view);
        assertTrue(presenter.isViewAttached());
    }

    @Test
    public void shouldShowAcceptButtonOnlyIfTermsHaveNotAccepted() {

        presenter.attachView(view);

    }

    @Test
    public void shouldHideAcceptButtonTermsHaveBeenAccepted() {

        presenter.attachView(view);

    }

    @Test
    public void shouldShowContent() {
        when(getStringResource.invoke(GDPR_DATA_USAGE)).thenReturn("data usage");

        presenter.attachView(view);

        verify(view).showContent("data usage");
    }

    @Test
    public void shouldSavePrivacyPolicyAndFinishActivityWhenAcceptButtonIsClicked() {

        presenter.attachView(view);

    }

    @Test
    public void shouldTrackScreenStateOnAttachView() {
        presenter.attachView(view);
        verify(trackingAnalytics).track(AnalyticsConstants.ScreenState.HOW_DATA_USED, AnalyticsConstants.Type.HOME);
    }
}