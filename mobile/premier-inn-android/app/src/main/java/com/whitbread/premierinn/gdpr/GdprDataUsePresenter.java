package com.whitbread.premierinn.gdpr;

import static com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.GDPR_DATA_USAGE;

import androidx.annotation.NonNull;

import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.disposables.CompositeDisposable;

@ActivityRetainedScoped
public class GdprDataUsePresenter extends Presenter<GdprDataUsePresenter.View> {

    private final GetStringResource getStringResource;
    private final CompositeDisposable subscriptions;
    private final TrackingAnalytics trackingAnalytics;

    @Inject
    public GdprDataUsePresenter(@NonNull GetStringResource getStringResource,
                                @NonNull CompositeDisposable subscriptions, @NonNull TrackingAnalytics trackingAnalytics) {
        this.trackingAnalytics = trackingAnalytics;
        this.getStringResource = getStringResource;
        this.subscriptions = subscriptions;
    }

    @Override
    protected void onAttachView(View view) {
        view.showContent(getStringResource.invoke(GDPR_DATA_USAGE));
        trackingAnalytics.track(AnalyticsConstants.ScreenState.HOW_DATA_USED, AnalyticsConstants.Type.HOME);
    }

    @Override
    protected void onDetachView() {
        subscriptions.clear();
        super.onDetachView();
    }

    public interface View extends PresenterView {
        void showContent(String hmlContent);
    }
}
