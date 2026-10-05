package com.whitbread.premierinn.gdpr;

import static com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.GDPR_PRIVACY_MESSAGE;
import android.content.Context;
import androidx.annotation.NonNull;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.StringResourceProvider;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.contentSquare.CSQOptInHelper;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

@ActivityRetainedScoped
public class GdprPresenter extends Presenter<GdprPresenter.View> {

    private final GetStringResource getStringResource;
    private final SimplePersistenceManager persistenceManager;
    private final CompositeDisposable compositeDisposable;
    private final TrackingAnalytics trackingAnalytics;
    private final StringResourceProvider stringResourceProvider;
    private final CSQOptInHelper cSQOptInHelper;


    @Inject
    public GdprPresenter(
            @NonNull GetStringResource getStringResource,
            @NonNull SimplePersistenceManager persistenceManager,
            @NonNull CompositeDisposable compositeDisposable,
            @NonNull TrackingAnalytics trackingAnalytics,
            @NonNull StringResourceProvider stringResourceProvider,
            @NonNull CSQOptInHelper cSQOptInHelper
    ) {
        this.trackingAnalytics = trackingAnalytics;
        this.getStringResource = getStringResource;
        this.persistenceManager = persistenceManager;
        this.compositeDisposable = compositeDisposable;
        this.stringResourceProvider = stringResourceProvider;
        this.cSQOptInHelper = cSQOptInHelper;
    }

    @Override
    protected void onAttachView(View view) {
        compositeDisposable.add(view.changesAccepted()
            .subscribe(__ -> {
                persistenceManager.setHasAcceptedPrivacyPolicy();
                cSQOptInHelper.optInAndMask();
                view.returnResult();
            }));

        view.showInterstitialMessage(getStringResource.invoke(GDPR_PRIVACY_MESSAGE));

        compositeDisposable.add(view.privacyPolicyClicked()
                .subscribe(configurationInfo -> view.goToWebLink(stringResourceProvider.getString(R.string.privacy_policy_web_url))));

        compositeDisposable.add(view.howToUseDataClicked()
            .subscribe(__ -> view.openHowWeUseYourData()));

        trackingAnalytics.track(AnalyticsConstants.ScreenState.INTERSTITIAL, AnalyticsConstants.Type.HOME);
    }

    public interface View extends PresenterView {
        Observable<Unit> changesAccepted();
        Observable<Unit> howToUseDataClicked();
        Observable<Unit> privacyPolicyClicked();
        Context getContext();
        void returnResult();
        void goToWebLink(@NonNull String webUrl);
        void openHowWeUseYourData();
        void showInterstitialMessage(String messageContent);
    }
}
